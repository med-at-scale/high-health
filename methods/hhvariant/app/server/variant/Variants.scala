package server.variant

import java.net.InetSocketAddress

import scala.collection.JavaConversions._
import scala.collection.JavaConverters._

import org.apache.avro.AvroRemoteException

import org.apache.spark.rdd.RDD

import org.apache.avro.ipc.NettyServer
import org.apache.avro.ipc.specific.SpecificResponder


import org.ga4gh.models.{Variant, Call, CallSet}
import org.ga4gh.methods._

import org.apache.hadoop.fs.{FileSystem, Path}

import org.bdgenomics.formats.avro.{FlatGenotype, Genotype, GenotypeAllele}
import org.bdgenomics.adam.converters.{ VCFLine, VCFLineConverter, VCFLineParser }
import org.bdgenomics.adam.models.VariantContext
import org.bdgenomics.adam.rdd.ADAMContext._
import org.bdgenomics.adam.rdd.variation.VariationContext._
import org.bdgenomics.adam.rdd.ADAMContext

import server.{Source, Sources}

object Variants extends VariantMethods {

  @transient private [this] lazy val _ipc = new NettyServer(new SpecificResponder(classOf[VariantMethods], this),
                                                            new InetSocketAddress(65001)
                                                          )

  @transient lazy val start = {
    _ipc
    ()
  }

  @transient lazy val stop = {
    _ipc.close
    ()
  }

  // PUT something here... and it'll be shipped in spark
  // → care!

  @transient lazy val adam = server.SparkProvider.adamContext

  /**
   * Gets a list of `VariantSet` matching the search criteria.
   * `POST /variantsets/search` must accept a JSON version of
   * `SearchVariantSetsRequest` as the post body and will return a JSON version
   * of `SearchVariantSetsResponse`.
   */
  def searchVariantSets(request:SearchVariantSetsRequest):SearchVariantSetsResponse = {
    //@throws AvroRemoteException, GAException
//    request.datasetIds
//    request.pageSize
//    request.pagetoken
    val source = Sources.`med-at-scale`

    val ids:List[String] = request.getDatasetIds.asScala.toList
    val variantSets = ids.map { id =>
      source.variantSetForDataset(id)
    }.collect {
      case Some(x) => x
    }

    val nextPageToken = ""
    new SearchVariantSetsResponse(variantSets.toList.asJava, nextPageToken)
  }


  def rangeOverlaps(r1:(Long,Long), r2:(Long,Long)):Boolean = !(r2._1 > r1._2 || r2._2 < r1._1)

  /**
   * Gets a list of `Variant` matching the search criteria.
   * `POST /variants/search` must accept a JSON version of `SearchVariantsRequest`
   * as the post body and will return a JSON version of `SearchVariantsResponse`.
   */
  def searchVariants(request:SearchVariantsRequest):SearchVariantsResponse = {
    //@throws AvroRemoteException, GAException

    // dataset
    val source = Sources.`med-at-scale`

    val ids:List[String] = request.getVariantSetIds.asScala.toList.map(_.toString)
    //TOCHECK → filter ids matching the source (med-at-scale 1000genomes)
    //          since a VarianSet correspond to a VCF file
    //          but for 1000genomes, a vcf is a chromosome
    val variantIdR = "med-at-scale/chr(.+)\\.vcf".r
    val chrs = ids.map {
      case variantIdR(chr) => chr
    }

    /*
      The reference on which this variant occurs.
      (e.g. `chr20` or `X`)
    */
    val referenceName = request.getReferenceName
    var validChrs = chrs.find(_ == referenceName)


    val start = request.getStart()
    val end = request.getEnd()

    //TODO
    val chr = validChrs.head

    val gts:RDD[Genotype] = adam.sc.adamLoad(source.chr(chr))

    val pageSize = request.getPageSize()
    val currentPage = 0 //TODO: should be derived using : request.getPageToken

    //https://github.com/bigdatagenomics/bdg-formats/blob/master/src/main/resources/avro/bdg.avdl
    val variants = gts.filter( g =>
      rangeOverlaps((start, end), (g.variant.start, g.variant.end))
    )
    .take(pageSize*(currentPage+1))
    .drop(pageSize*currentPage)
    .map{ g =>
      //`g` contains things like likelihood,dosage, alleles, phase etc.
      // which are used in the Call of the GA4GH api
      // It looks like the model is a bit reversed
      // in ADAM: Genotype → Variant
      // in GA4GH: Variant → Call


      val variant = g.variant

      // new Variant(id:String,
      //             variantSetId:String,
      //             names:List[String],
      //             created:Long,
      //             updated:Long,
      //             referenceName:String,
      //             start:Long,
      //             end:Long,
      //             referenceBases:String,
      //             alternateBases:List[String],
      //             info:Map[String, List[String]],
      //             calls:List[org.ga4gh.models.Call])

      // new Call(callSetId:String,
      //          callSetName:String,
      //          genotype:List[Integer],
      //          phaseset:String,
      //          genotypeLikelihood:List[Double],
      //          info:Map[String, List[String]])

      val alleles:List[Int] = g.alleles.asScala.toList.map {
        case GenotypeAllele.Ref       => 0
        case GenotypeAllele.Alt       => 1
        case GenotypeAllele.OtherAlt  => 2
        case GenotypeAllele.NoCall    => -1 //TODO → exception?
        case _                        => -1
      }
      val likelihoods:java.util.List[java.lang.Double] = g.genotypeLikelihoods.asScala.toList.map(x => new java.lang.Double(x.toDouble / 100)).asJava
      val v = new Variant(
        java.util.UUID.randomUUID.toString,//TODO
        source.chr(chr),//TODO
        List(""), //TODO
        0,//TODO use the date in the 1kg file name
        0,//TODO use the date in the 1kg file name
        chr,//TODO
        variant.start,
        variant.end,
        variant.referenceAllele,
        List(variant.alternateAllele), //TODO → a list? in ADAM, there a single string
        Map.empty[String, java.util.List[String]].asJava,//TODO
        List(new Call(
          chr,
          "1000genomes",
          /*TODO genotype → The genotype of this variant call. Each value represents either the value of the referenceBases
           field or is a 1-based index into alternateBases.
           If a variant had a referenceBases field of "T", an alternateBases value of ["A", "C"],
           and the genotype was [2, 1], that would mean the call represented the heterozygous value "CA" for this variant.
           If the genotype was instead [0, 1] the represented value would be "TA".
           Ordering of the genotype values is important if the phaseset field is present.
           */
           alleles,
           /*TODO is it pertinent ?
            * (If this field is present, this variant call's genotype ordering implies the phase of the bases and is consistent
            *  with any other variant calls on the same contig which have the same phaseset value.)*/
           null,
           /*
           The genotype likelihoods for this variant call. Each array entry represents how likely a specific genotype is
           for this call as log10(P(data | genotype)), analogous to the GL tag in the VCF spec.
           The value ordering is defined by the GL tag in the VCF spec.
            */
            likelihoods,
            Map.empty[String, java.util.List[String]].asJava
        ))
      )
      v
    }

    new SearchVariantsResponse(variants.toList.asJava, "nextPageToken")
  }

  /**
   * Gets a list of `CallSet` matching the search criteria.
   * `POST /callsets/search` must accept a JSON version of `SearchCallSetsRequest`
   * as the post body and will return a JSON version of `SearchCallSetsResponse`.
   */
  def searchCallSets(request:SearchCallSetsRequest):SearchCallSetsResponse = {
    //@throws AvroRemoteException, GAException
    ???
  }

  /**
   * Gets a `CallSet` by ID.
   * `GET /callsets/{id}` will return a JSON version of `CallSet`.
   */
  def getCallSet(id:String):CallSet = {
    //@throws AvroRemoteException, GAException
    ???
  }
}