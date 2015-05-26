package server.beacon

import java.net.InetSocketAddress

import scala.collection.JavaConversions._
import scala.collection.JavaConverters._

import org.apache.avro.AvroRemoteException

import org.apache.spark.rdd.RDD

import org.apache.avro.ipc.NettyServer
import org.apache.avro.ipc.specific.SpecificResponder


//import org.ga4gh.models.{Variant, Call, CallSet}
import org.ga4gh._

import org.apache.hadoop.fs.{FileSystem, Path}

//import org.bdgenomics.formats.avro.{FlatGenotype, Genotype, GenotypeAllele}
//import org.bdgenomics.adam.models.VariantContext
//import org.bdgenomics.adam.rdd.ADAMContext._
//import org.bdgenomics.adam.rdd.ADAMContext

import server.{Source, Sources}

/**
 * A Beacon is a web service for genetic data sharing that can be queried for
 * information about specific alleles.
 */
object BeaconV0_5_1 extends BEACON {

  @transient private [this] lazy val _ipc = new NettyServer(new SpecificResponder(classOf[BEACON], this),
                                                            new InetSocketAddress(65005)
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

  @transient lazy val sparkContext = server.SparkProvider.sparkContext
  val source = Sources.`med-at-scale`

  val alleleFreqs = scala.collection.mutable.Map[String, RDD[(Int, Map[Char, Short])]]()

  def index(request: BEACONRequest): BEACONResponse = {
    val chr = request.getChromosome()
    val coord = request.getCoordinate()
    val allele = request.getAllele().charAt(0)

    val rdd = if (alleleFreqs.isDefinedAt(chr)) alleleFreqs(chr)
              else {
                val tmprdd: RDD[(Long, Map[String, Long])] = sparkContext.objectFile(source.sourceOfChr(chr))
                val alls = tmprdd.map{cnt => (cnt._1.toInt, cnt._2.map{all => (all._1.charAt(0) -> all._2.toShort)})}
                alls.cache()
                alleleFreqs += (chr -> alls)
                alls
              }
//    val rdd: RDD[(Long, Map[String, Long])] = sparkContext.objectFile(source.sourceOfChr(chr))
//    val alls = rdd.map{cnt => (cnt._1.toInt, cnt._2.map{all => (all._1.charAt(0) -> all._2.toShort)})}
//    alls.cache()
    val variant = rdd.filter(_._1 == coord).take(1)
    val respOpt = if (variant.size == 1) {
      val cntOpt: Option[Short] = variant(0)._2.get(allele)
      cntOpt.map{
        cnt => 
          new BEACONResponse(true, (10000.0*cnt/variant(0)._2.values.map(_.toInt).reduce(_+_)).toLong)
      } 
    }
    else None

    respOpt.getOrElse(new BEACONResponse(false, 0))
  }

}