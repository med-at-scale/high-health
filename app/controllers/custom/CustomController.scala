package controllers.custom

import play.api._
import play.api.libs.json._
import play.api.mvc._

import server.Custom

object CustomController extends Controller {

  def countSamples(chr:String) = Action {
    Ok(s"number of sample in 1000genomes for chromosome $chr is ${Custom.countSamples(chr)}")
  }
  
  def countsOnChromosome(chr:String) = Action {
  	val counts = Custom.countsOnChromosome(chr)
  	Ok(s"#Samples: ${counts._1}, #Variants: ${counts._2}, #Genotypes: ${counts._3}")
  }

}