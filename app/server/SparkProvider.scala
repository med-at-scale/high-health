package server

import scala.collection.JavaConversions._

import play.api.Play
import play.api.Play.current

import org.apache.spark.{SparkConf, SparkContext}

import org.bdgenomics.adam.rdd.ADAMContext

object SparkProvider {

  lazy val sparkContext:SparkContext = {
    val c = Play.configuration.getConfig("spark").get.underlying
    val configObject = c.root()
    val map = configObject.unwrapped()
    def collapse(data:Map[String, Any], path:String, acc:Map[String, String]):Map[String, String] = {
      data.map {
        case (key, next:java.util.Map[String, Any]) =>
          collapse(next.toMap, path+"."+key, acc)

        case (key, string:String) =>
          acc + ((path+"."+key) → string)
      }.reduce(_ ++ _)
    }
    val conf = collapse(map.toMap, "spark", Map.empty)
    val sc = new SparkConf()
    sc.setAll(conf)
    new SparkContext(sc)
  }

  // add ADAM context
  lazy val adamContext:ADAMContext = new ADAMContext(sparkContext)
}