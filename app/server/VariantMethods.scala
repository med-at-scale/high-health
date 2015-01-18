package server

import scala.collection.JavaConversions._
import scala.collection.JavaConverters._

import org.apache.avro.AvroRemoteException

import org.apache.spark.rdd.RDD

import org.ga4gh.models.{Variant, Call, CallSet}
import org.ga4gh.methods.{VariantMethods => IVariantMethods, _}

import org.apache.hadoop.fs.{FileSystem, Path}

import org.bdgenomics.adam.converters.{ VCFLine, VCFLineConverter, VCFLineParser }
import org.bdgenomics.formats.avro.{Genotype, FlatGenotype}
import org.bdgenomics.adam.models.VariantContext
import org.bdgenomics.adam.rdd.ADAMContext._
import org.bdgenomics.adam.rdd.variation.VariationContext._
import org.bdgenomics.adam.rdd.ADAMContext


object VariantMethods extends IVariantMethods {

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
    ???
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
    //https://github.com/bigdatagenomics/bdg-formats/blob/master/src/main/resources/avro/bdg.avdl
    val variants = gts.filter( g =>
      rangeOverlaps((start, end), (g.variant.start, g.variant.end))
    ).map{ g =>
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

      val v = new Variant(
        java.util.UUID.randomUUID.toString,//TODO
        source.chr(chr),//TODO
        List(""), //TODO
        0,//???
        0,//???
        chr,//TODO
        variant.start,
        variant.end,
        variant.referenceAllele,
        List(variant.alternateAllele), //TODO → a list? in ADAM, there a single string
        Map.empty[String, java.util.List[String]].asJava,//TODO
        List.empty[Call]// will use the Genotype info !
      )
      v
    }

    new SearchVariantsResponse(variants.collect().toList, "nextPageToken")
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