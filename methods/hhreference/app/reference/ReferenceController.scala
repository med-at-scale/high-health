package controllers.reference

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

import org.ga4gh.methods.{SearchCallSetsRequest, SearchReferencesRequest, SearchReferenceSetsRequest, ReferenceMethods}

import server.reference.References
import sanitizer.reference.ReferenceSanitizer

object ReferenceController extends Controller {
  import server.AvroHelper.fromJson

  def searchReferenceSets() = Action(BodyParsers.parse.json) { json =>
    //FIXME → deserves async
    val jsonStringUnsafe = Json.stringify(json.body)
    println(jsonStringUnsafe)
    val jsonString = ReferenceSanitizer.searchReferenceSetsRequest(jsonStringUnsafe)
    val searchRequest = fromJson[SearchReferenceSetsRequest](jsonString)
    //val resp = server.ReferenceMethods.searchReferences(searchRequest)
    val resp = References.searchReferenceSets(searchRequest)
    Ok(resp.toString).withHeaders(
      "content-type" -> "application/json"
      )
  }

  def getReferenceSet(id: String) = Action {
  	val resp = References.getReferenceSet(id)
  	Ok(resp.toString).withHeaders(
      "content-type" -> "application/json"
    )
  }

  def searchReferences() = Action(BodyParsers.parse.json) { json =>
    //FIXME → deserves async
    val jsonStringUnsafe = Json.stringify(json.body)
    println(jsonStringUnsafe)
    val jsonString = ReferenceSanitizer.searchReferencesRequest(jsonStringUnsafe)
    println(jsonString)
    val searchRequest = fromJson[SearchReferencesRequest](jsonString)
    //val resp = server.ReferenceMethods.searchReferences(searchRequest)
    val resp = References.searchReferences(searchRequest)
    Ok(resp.toString).withHeaders(
      "content-type" -> "application/json"
      )
  }

  def javascriptRoutes = Action { implicit request =>
    import controllers.reference.routes._
    Ok(
      Routes.javascriptRouter("jsRoutes")(
        javascript.ReferenceController.searchReferenceSets
      )
    ).as("text/javascript")
  }

}