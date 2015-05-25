package server.reference

import java.net.InetSocketAddress

import scala.collection.JavaConversions._
import scala.collection.JavaConverters._

import org.apache.avro.AvroRemoteException

import org.apache.spark.rdd.RDD

import org.apache.avro.ipc.NettyServer
import org.apache.avro.ipc.specific.SpecificResponder


import org.ga4gh.{GAReference, GAReferenceSet}
import org.ga4gh._

import org.apache.hadoop.fs.{FileSystem, Path}

import org.bdgenomics.formats.avro.{FlatGenotype, Genotype, GenotypeAllele}
import org.bdgenomics.adam.models.VariantContext
import org.bdgenomics.adam.rdd.ADAMContext._
import org.bdgenomics.adam.rdd.ADAMContext

import server.{Source, Sources}

object References extends GAReferenceMethods {

  @transient private [this] lazy val _ipc = new NettyServer(new SpecificResponder(classOf[GAReferenceMethods], this),
                                                            new InetSocketAddress(65011)
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
   * Gets a list of `ReferenceSet` matching the search criteria.
   * `POST /referencesets/search` must accept a JSON version of
   * `SearchReferenceSetsRequest` as the post body and will return a JSON version
   * of `SearchReferenceSetsResponse`.
   */
  def searchReferenceSets(request: GASearchReferenceSetsRequest): GASearchReferenceSetsResponse = {
    //@throws AvroRemoteException, GAException
/*    this.md5checksums = md5checksums;
    this.accessions = accessions;
    this.assemblyId = assemblyId;
    this.pageSize = pageSize;
    this.pageToken = pageToken; */
    val source = Sources.`med-at-scale`
    val accessions = request.getAccessions.asScala.toList

   // val ids:List[String] = request.getDatasetIds.asScala.toList
  val refSets = accessions.map { acc =>
    new GAReferenceSet( acc, 
                      List("referenceIds"), 
                      "md5checksum",
                      9606,
                      "Human assembly description",
                      "GRCh38",
                      "sourceURI", 
                      List(""), // source Accessions 
                      false)
  }
    val nextPageToken = ""

    new GASearchReferenceSetsResponse(refSets, nextPageToken)
  }

  def getReference(id: String): org.ga4gh.GAReference = {
    val source = Sources.`med-at-scale`
    val reference = source.getReferenceById(id)
    reference.getOrElse(null)
  }


  def getReferenceBases(x$1: String,x$2: org.ga4gh.GAListReferenceBasesRequest): org.ga4gh.GAListReferenceBasesResponse = ???

  def getReferenceSet(acc: String): org.ga4gh.GAReferenceSet = {
    val source = Sources.`med-at-scale`
    val gts: RDD[Genotype] = adam.sc.adamLoad(source.chr("22"))
    val gt = gts.first
    val url = gt.getVariant.getContig.getReferenceURL
    val name = gt.getVariant.getContig.getAssembly
    val species = gt.getVariant.getContig.getSpecies
    new GAReferenceSet( acc, 
                      List("referenceIds"), 
                      "md5checksum",
                      9606,
                      s"$species assembly description",
                      "GRCh38",
                      url, 
                      List(name), // source Accessions 
                      false)
  }

  def searchReferences(request: org.ga4gh.GASearchReferencesRequest): org.ga4gh.GASearchReferencesResponse = {
    val source = Sources.`med-at-scale`
    val md5sums = request.getMd5checksums.asScala.toList

    val references:java.util.List[org.ga4gh.GAReference] = md5sums.map(md5 => source.getReferenceByMd5(md5)).collect{case Some(r) => r}
    new org.ga4gh.GASearchReferencesResponse(references, "1")
  }

  def getSequenceBases(x$1: String,x$2: org.ga4gh.methods.GetSequenceBasesRequest): org.ga4gh.methods.GetSequenceBasesResponse = ???
  def sendsMode(x$1: String): Boolean = ???
}