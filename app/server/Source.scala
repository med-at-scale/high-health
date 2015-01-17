package server

case class Source(ref:String, pattern:String=>String) {
  def chr(chromosome:String):String = ref + pattern(chromosome)
}

object Sources {
  val `med-at-scale` =
    Source("s3n://med-at-scale/1000genomes/", ((i:String) => s"ALL.chr$i.integrated_phase1_v3.20101123.snps_indels_svs.genotypes.vcf.adam/"))
}