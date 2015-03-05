package server

// WILL HAVE TO SEE HOW MUCH A SOURCE IS PROVIDING...
// THIS WILL HAVE TO BE READ FROM A REAL STORAGE ENGINE
trait DatasetProvider {
	def datasets = List[String]("1000genomes")
}
trait VariantSetProvider {
	def variantSetForDataset(id: String):Option[org.ga4gh.models.VariantSet] = None
}

object Sources {

  val `med-at-scale` =
    new Source("s3n://med-at-scale/1000genomes/", ((i:String) => s"ALL.chr$i.integrated_phase1_v3.20101123.snps_indels_svs.genotypes.vcf.adam/")) with DatasetProvider with VariantSetProvider {
        override def datasets = List[String]("1000genomes")
	    override def variantSetForDataset(id: String) = id match {
	    	case "1000genomes" => Some(new org.ga4gh.models.VariantSet("0", "1000genomes", "referenceSetId", new java.util.ArrayList[org.ga4gh.models.VariantSetMetadata]()))
	    	case _ => None
	    }

     }

}

class Source(ref:String, pattern:String=>String) {
  def chr(chromosome:String):String = ref + pattern(chromosome)
}
