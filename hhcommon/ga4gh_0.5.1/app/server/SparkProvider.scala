package server

import scala.collection.JavaConversions._

//import play.api.Play
//import play.api.Play.current

import com.typesafe.config.{ConfigFactory, ConfigValue, ConfigValueType}

import org.apache.spark.{SparkConf, SparkContext}

import org.bdgenomics.adam.rdd.ADAMContext

object SparkProvider {

  lazy val sparkContext:SparkContext = {
    //val c = Play.configuration.getConfig("spark").get.underlying
    //val configObject = ConfigFactory.load("common-application")//c.root()
    val configObject = ConfigFactory.load()
    println(" ** Spark Conf File content **")
    println(configObject.root().render())
    val map = configObject.getConfig("spark").entrySet.map(x => (x.getKey, x.getValue)).toMap//.unwrapped()
    def collapse(data:Map[String, Any], path:String, acc:Map[String, String]):Map[String, String] = {
      data.map {
        case (key, next:java.util.Map[String, Any]) =>
          collapse(next.toMap, path+"."+key, acc)

        case (key, o:ConfigValue) => o.valueType match {
          case ConfigValueType.STRING => acc + ((path+"."+key) → o.unwrapped.toString)
        }
      }.reduce(_ ++ _)
    }

    val conf = collapse(map.toMap, "spark", Map.empty)
    val sc = new SparkConf()
    sc.setAll(conf)

    println(" ** Spark Conf **")
    println(sc.toDebugString)
    new SparkContext(sc)
  }

  // add ADAM context
  lazy val adamContext:ADAMContext = new ADAMContext(sparkContext)
}
