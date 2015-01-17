package server

import scala.collection.JavaConversions._
import scala.collection.JavaConverters._

import org.apache.avro.AvroRemoteException

import org.apache.spark.rdd.RDD

import org.ga4gh.models.CallSet
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

  /**
   * Gets a list of `Variant` matching the search criteria.
   * `POST /variants/search` must accept a JSON version of `SearchVariantsRequest`
   * as the post body and will return a JSON version of `SearchVariantsResponse`.
   */
  def searchVariants(request:SearchVariantsRequest):SearchVariantsResponse = {
    //@throws AvroRemoteException, GAException
    val ids:List[String] = request.getVariantSetIds.asScala.toList.map(_.toString)


    //TODO
    val chromosome = "22"

    //https://github.com/bigdatagenomics/bdg-formats/blob/master/src/main/resources/avro/bdg.avdl

    val source = Sources.`med-at-scale`

    val chr = source.chr(chromosome)

    val gts:RDD[Genotype] = adam.sc.adamLoad(chr)

    gts.map{ g =>
      val variant = g.variant
      val start = variant.start
      val end = variant.end

      // no variant id...??

      //how to create Variant object from ga4gh → CallSet objects and so on

    }

    ???
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