package controllers.variant

import java.net.InetSocketAddress

import java.io.{ByteArrayInputStream, DataInputStream, InputStream}

import scala.collection.JavaConversions._
import scala.reflect.runtime.universe._

import play.api._
import play.api.libs.json._
import play.api.mvc._

import org.apache.avro.Schema
import org.apache.avro.io.{DatumReader, Decoder, DecoderFactory}
import org.apache.avro.specific.SpecificDatumReader
import org.apache.avro.ipc.NettyTransceiver
import org.apache.avro.ipc.specific.SpecificRequestor

import org.ga4gh.methods.{SearchCallSetsRequest, SearchVariantsRequest, SearchVariantSetsRequest, VariantMethods}

import server.variant.Variants

object VariantController extends Controller {

  // API is http://avro.apache.org/docs/current/api/java/org/apache/avro/io/package-summary.html
  def fromJson[T:TypeTag](json:String):T = {
    val wt = implicitly[TypeTag[T]]
    val clazz = wt.mirror.runtimeClass(wt.tpe)

    val fld = clazz.getDeclaredField("SCHEMA$")
    val schema:Schema = fld.get(null).asInstanceOf[Schema]

    val input:InputStream = new ByteArrayInputStream(json.getBytes())
    val din:DataInputStream = new DataInputStream(input)

    val decoder:Decoder = DecoderFactory.get().jsonDecoder(schema, din)

    val reader:DatumReader[T] = new SpecificDatumReader(schema)
    val datum:T = reader.read(null.asInstanceOf[T], decoder)
    datum
  }

  def isPositiveOnOption() = Action {
    Ok("").withHeaders(
      "Access-Control-Allow-Headers"   -> "content-type,accept" ,
      "Access-Control-Request-Methods" -> "OPTIONS,POST"    ,
      "Access-Control-Allow-Origin"    ->  "*"              
      )
  }

  def searchVariantSets() = Action(BodyParsers.parse.json) { json =>
    //FIXME → deserves async
    val jsonString = Json.stringify(json.body)
    println(jsonString)
    val searchRequest = fromJson[SearchVariantSetsRequest](jsonString)
    //val resp = server.VariantMethods.searchVariants(searchRequest)
    val resp = Variants.searchVariantSets(searchRequest)
    Ok(resp.toString).withHeaders(
      "content-type" -> "application/json"
      )
  }
 
  def searchVariantSetsOpts() = isPositiveOnOption
  /**
     Try with
     ```
      curl -i -X POST \
         -H "Content-Type:text/json; charset=utf-8" \
         -d \
      '{
        "variantSetIds": [ "med-at-scale/chr22.vcf" ],
        "variantName": {"string": "notUsed"},
        "referenceName": "22",
        "start": 38617521,
        "end": 38617523,
        "pageSize": {"int": 10},
        "pageToken": {"string": "notUsed"},
        "callSetIds": {"array": [ "notUsed" ] }
      }' \
       'http://localhost:9000/variants/search'
     ```
    */
  def searchVariants() = Action(BodyParsers.parse.json) { json =>
    //FIXME → deserves async
    val jsonString = Json.stringify(json.body)
    println(jsonString)
    val searchRequest = fromJson[SearchVariantsRequest](jsonString)
    //val resp = server.VariantMethods.searchVariants(searchRequest)
    val resp = Variants.searchVariants(searchRequest)
    Ok(resp.toString)
  }
/*
Access-Control-Allow-Headers:content-type
Access-Control-Allow-Headers:accept
Access-Control-Allow-Methods:OPTIONS
Access-Control-Allow-Methods:GET
Access-Control-Allow-Methods:POST
Access-Control-Allow-Origin:*
Access-Control-Max-Age:10
Content-Length:0
Content-Type:text/plain
Date:Wed, 04 Mar 2015 23:44:00 GMT
Server:HTTP::Server::PSGI
*/

  def searchVariantsOpts() = Action {
    println("Options on variants search")
    Ok("").withHeaders(
      "Access-Control-Allow-Headers"   -> "content-type,accept" ,
      "Access-Control-Request-Methods" -> "OPTIONS,POST"    ,
      "Access-Control-Allow-Origin"    ->  "*"              
      )
  }
  /*
      Test by POSTing the below json string on http://localhost:9000/callsets/search
      ```
      {
        "name": {"string": "bof"},
        "variantSetIds": [ "v1", "v3" ],
        "pageSize": {"int": 42},
        "pageToken": {"string": "da-token"}
      }
      ```
      Notice how optional fields are encoded with as pairs (because we're not converting to binary before objects?)
       → see http://www.michael-noll.com/blog/2013/03/17/reading-and-writing-avro-files-from-the-command-line/
       → see http://stackoverflow.com/questions/21977704/how-to-avro-binary-encode-the-json-string-using-apache-avro
   */
  def searchCallSets() = Action(BodyParsers.parse.text) { json =>
    val searchRequest = fromJson[SearchCallSetsRequest](json.body)
    //Ok(searchRequest.getVariantSetIds.mkString("\n"))
    NotImplemented("searchCallSets")
  }

  def getCallSet(id:String) = TODO


  def javascriptRoutes = Action { implicit request =>
    import controllers.variant.routes._
    Ok(
      Routes.javascriptRouter("jsRoutes")(
        javascript.VariantController.searchVariants
      )
    ).as("text/javascript")
  }

}