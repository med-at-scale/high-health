package server.custom

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

import server.{Source, Sources}

object Custom {

  // PUT something here... and it'll be shipped in spark
  // → care!

  @transient lazy val adam = server.SparkProvider.adamContext

  def countSamples(chromosome:String, source:Source=Sources.`med-at-scale`):Int = {
    val chr = source.chr(chromosome)
    val gts:RDD[Genotype] = adam.sc.adamLoad(chr)

    val sampleCount = gts.map(_.getSampleId.toString.hashCode).distinct.count

    sampleCount.toInt
  }

  def head(chromosome: String, size:Int, source:Source=Sources.`med-at-scale`):List[Genotype] = {
    val chr = source.chr(chromosome)

    val gts:RDD[Genotype] = adam.sc.adamLoad(chr)

    // even this blows an exception when using Tachyon
    // java.lang.OutOfMemoryError: Requested array size exceeds VM limit
    gts.repartition(gts.partitions.size*10)
    gts.persist(org.apache.spark.storage.StorageLevel.OFF_HEAP)

    gts.take(size).toList
  }

  def countsOnChromosome(chromosome: String, source:Source=Sources.`med-at-scale`):(Long, Long, Long) = {
    val chr = source.chr(chromosome)

    println(chr)
    val gts:RDD[Genotype] = adam.sc.adamLoad(chr)

    // Xavier
    // mmmh seems that after adamLoad, the persistence level is already set...and cached?
    //
    // Andy
    // this worked for me, however I got this exception
    //  → https://issues.apache.org/jira/browse/SPARK-1353
    // Which is related to the UNRESOLVED https://issues.apache.org/jira/browse/SPARK-1476
    // Apparently, and weirdly, the partition of 7.5Mb on s3 will create disk storage more than 2G Ôö
    //gts.persist(org.apache.spark.storage.StorageLevel.DISK_ONLY)
    //
    // Andy
    // Even tachyon is failing on this one, it fails with a too big array issue (size > Int.MaxValue)!!!
    //            ↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓
    //gts.persist(org.apache.spark.storage.StorageLevel.OFF_HEAP)

    val genotypeCount = gts.count
    println(s"$chr $genotypeCount genotypes")

    val sampleCount = gts.map(_.getSampleId.toString.hashCode).distinct.count
    println(s"$chr $sampleCount samples")

    val variantCount = gts.map(_.getVariant.toString.hashCode).distinct.count
    println(s"$chr $variantCount variants")

    (sampleCount, variantCount, genotypeCount)
  }

}