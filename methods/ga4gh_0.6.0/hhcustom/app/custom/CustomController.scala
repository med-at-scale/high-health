package controllers.custom

import play.api._
import play.api.libs.json._
import play.api.mvc._

import server.custom.Custom

object CustomController extends Controller {

  def countSamples(chr:String) = Action {
    Ok(s"number of sample in 1000genomes for chromosome $chr is ${Custom.countSamples(chr)}")
  }

  def head(chr:String, size:Int) = Action {
    Ok(s"First $size genotypes from $chr ${Custom.head(chr, size).mkString("\n")}")
  }

  def countsOnChromosome(chr:String) = Action {
  	val counts = Custom.countsOnChromosome(chr)
  	Ok(s"Chromosome $chr, #Samples: ${counts._1}, #Variants: ${counts._2}, #Genotypes: ${counts._3}\n")
  }

}