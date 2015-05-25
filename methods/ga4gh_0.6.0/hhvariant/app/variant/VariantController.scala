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
import sanitizer.variant.VariantSanitizer

object VariantController extends Controller {
  import server.AvroHelper.fromJson

  def index = Action {
    Ok(views.html.variant.index())
  }


  def searchVariantSets() = Action(BodyParsers.parse.json) { json =>
    //FIXME → deserves async
    val jsonStringUnsafe = Json.stringify(json.body)
    val jsonString = VariantSanitizer.searchVariantSetsRequest(jsonStringUnsafe)
    println(jsonString)
    val searchRequest = fromJson[SearchVariantSetsRequest](jsonString)
    val resp = Variants.searchVariantSets(searchRequest)
    Ok(resp.toString).withHeaders(
      "content-type" -> "application/json"
      )
    //Ok("")
  }

//  def searchVariantSetsOpts() = isPositiveOnOption
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
    val jsonStringUnsafe = Json.stringify(json.body)
    val jsonString = VariantSanitizer.searchVariantsRequest(jsonStringUnsafe)
    println(jsonString)
    val searchRequest = fromJson[SearchVariantsRequest](jsonString)

    val resp = Variants.searchVariants(searchRequest)
    Ok(resp.toString).withHeaders(
      "content-type" -> "application/json"
      )

/*
    Ok("")withHeaders(
      "content-type" -> "application/json"
      )
*/
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
  def searchCallSets() = Action(BodyParsers.parse.json) { json =>
    val jsonStringUnsafe = Json.stringify(json.body)
    val jsonString = VariantSanitizer.searchCallSetsRequest(jsonStringUnsafe)
    val searchRequest = fromJson[SearchCallSetsRequest](jsonString)
    //Ok(searchRequest.getVariantSetIds.mkString("\n"))
    println(searchRequest)
    val resp = Variants.searchCallSets(searchRequest)
    Ok(resp.toString).withHeaders(
      "content-type" -> "application/json"
      )
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

object Assets extends controllers.AssetsBuilder
