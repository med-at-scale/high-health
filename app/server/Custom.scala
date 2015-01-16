package server

import scala.collection.JavaConversions._

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


case class Source(ref:String, pattern:Int=>String) {
  def chr(chromosome:Int):String = ref + pattern(chromosome)
}

object Custom {

  // PUT something here... and it'll be shipped in spark
  // → care!

  @transient lazy val adam = server.SparkProvider.adamContext

  @transient val Sources = new {
    val `med-at-scale` =
      Source("s3n://med-at-scale/1000genomes/", ((i:Int) => s"ALL.chr$i.integrated_phase1_v3.20101123.snps_indels_svs.genotypes.vcf.adam/"))
  }


  def countSamples(chromosome:Int, source:Source=Sources.`med-at-scale`):Int = {
    val chr = source.chr(chromosome)

    println(chr)
    val gts:RDD[Genotype] = adam.sc.adamLoad(chr)

    val sampleCount = gts.map(_.getSampleId.toString.hashCode).distinct.count

    sampleCount.toInt
  }
}