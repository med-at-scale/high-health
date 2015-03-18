package server

import scala.collection.JavaConversions._

// WILL HAVE TO SEE HOW MUCH A SOURCE IS PROVIDING...
// THIS WILL HAVE TO BE READ FROM A REAL STORAGE ENGINE
trait DatasetProvider {
	def datasets = List[String]("1000genomes")
}

trait VariantSetProvider {
	def variantSetForDataset(id: String):Option[org.ga4gh.models.VariantSet] = None
	// for now, say these dates are provided by a VariantSet
	def createdDate(): Long = 0L
    def updatedDate(): Long = 0L
}

trait CallSetProvider {
	def callSets(ids: List[String]): Option[org.ga4gh.models.CallSet] = None
}

object Sources {

  val `med-at-scale` =
    new Source("s3n://med-at-scale/1000genomes/", ((i:String) => s"ALL.chr$i.integrated_phase1_v3.20101123.snps_indels_svs.genotypes.vcf.adam")) 
      with DatasetProvider with VariantSetProvider with CallSetProvider {
        val fmt = new java.text.SimpleDateFormat("yyyyMMdd")
        override def datasets = List[String]("1000genomes")
	    override def variantSetForDataset(id: String) = id match {
	    	case "1000genomes" => {
	    		val info = Map[String, java.util.List[java.lang.String]]("build" -> List("human", "whatever"))
	    		val metadata = new org.ga4gh.models.VariantSetMetadata("reference", "GRCh37", "genome", "UNKNOWN_TYPE", "1", "genome", info)
	    		Some(new org.ga4gh.models.VariantSet("22", "1000genomes", "referenceSetId", List[org.ga4gh.models.VariantSetMetadata](metadata)))
	    	}
	    	case _ => None
	    }
	    override def createdDate() = fmt.parse("20101123").getTime
	    override def updatedDate() = fmt.parse("20101123").getTime

	    override def callSets(ids: List[String]) = {
	    	val info: java.util.Map[String,java.util.List[String]] = Map[String, java.util.List[java.lang.String]]("build" -> List("human", "whatever"))
            val callSet = new org.ga4gh.models.CallSet("id", 
                              "name", 
                              "sampleId", 
                              ids, 
                              this.createdDate(),//TODO use the date in the 1kg file name
                              this.updatedDate(),//TODO use the date in the 1kg file name
                              info)
            Some(callSet)
	    }
     }

}

class Source(ref:String, pattern:String=>String) {
  def chr(chromosome:String):String = ref + pattern(chromosome)
}
