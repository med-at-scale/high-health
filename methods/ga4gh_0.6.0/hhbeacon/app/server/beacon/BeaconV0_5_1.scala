package server.beacon

import java.net.InetSocketAddress

import scala.collection.JavaConversions._
import scala.collection.JavaConverters._

import org.apache.avro.AvroRemoteException

import org.apache.spark.rdd.RDD

import org.apache.avro.ipc.NettyServer
import org.apache.avro.ipc.specific.SpecificResponder


//import org.ga4gh.models.{Variant, Call, CallSet}
//import org.ga4gh.methods._
import org.ga4gh.beacon._

import org.apache.hadoop.fs.{FileSystem, Path}

import org.bdgenomics.formats.avro.{FlatGenotype, Genotype, GenotypeAllele}
import org.bdgenomics.adam.models.VariantContext
import org.bdgenomics.adam.rdd.ADAMContext._
import org.bdgenomics.adam.rdd.ADAMContext

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

  @transient lazy val adam = server.SparkProvider.adamContext
/* deactivate to get master 0.6.0 compiling
  def index(request:BEACONRequest):BEACONResponse = {
    new BEACONResponse(true, 1)
  }
  */
}