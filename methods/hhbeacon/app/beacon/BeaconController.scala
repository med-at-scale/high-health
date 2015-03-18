package controllers.beacon

import java.net.InetSocketAddress

import java.io.{ByteArrayInputStream, DataInputStream, InputStream}

import scala.collection.JavaConversions._
import scala.reflect.runtime.universe._

import play.api._
import play.api.data._
import play.api.data.Forms._
import play.api.libs.json._
import play.api.mvc._

import org.apache.avro.Schema
import org.apache.avro.io.{DatumReader, Decoder, DecoderFactory}
import org.apache.avro.specific.SpecificDatumReader
import org.apache.avro.ipc.NettyTransceiver
import org.apache.avro.ipc.specific.SpecificRequestor

import org.ga4gh.beacon._

import server.GA4GHVersions
import server.beacon._
import sanitizer.beacon.BeaconSanitizer

object BeaconController extends Controller {
  import server.AvroHelper.fromJson

  def execute(version:String, request:BEACONRequest):BEACONResponse =
    GA4GHVersions.named(version) match {
      case GA4GHVersions.v0_5_1 => BeaconV0_5_1.index(request)
    }

  /**
     Try with
     ```
      curl -i -X POST \
         -H "Content-Type:text/json; charset=utf-8" \
         -d \
      '{
        "populationId": {"string": "notUsed"},
        "referenceVersion": {"string": "notUsed"},
        "chromosome": {"string": "notUsed"},
        "coordinate": {"long": -1},
        "pageToken": {"string": "notUsed"},
        "allele": {"string": "notUsed"}
      }' \
       'http://localhost:9000/v0.5.1/beacon'
     ```
  */
  def index(version:String) = Action(BodyParsers.parse.json) { json =>
    /* temp fix to get ga4gh master compiling 
    //FIXME → deserves async
    val jsonStringUnsafe = Json.stringify(json.body)
    val jsonString =  jsonStringUnsafe//sanitize?
    println(jsonString)
    val request = fromJson[BEACONRequest](jsonString)
    val response = execute(version, request)
    Ok(response.toString).withHeaders(
      "content-type" -> "application/json"
    )
*/ Ok("")
  }

  case class FlatBeacon(populationId:String, referenceVersion:String, chromosome:String, coordinate:Long, allele:String) {
    def validate = {
      //nothing atm
      true
    }

    def toAvro = new BEACONRequest(populationId, referenceVersion, chromosome, coordinate, allele)
  }

  val beaconForm = Form(
    mapping(
      "populationId"     → text,
      "referenceVersion" → text,
      "chromosome"       → text,
      "coordinate"       → longNumber,
      "allele"           → text
    )(FlatBeacon.apply)(FlatBeacon.unapply) verifying ("FlatBeaconing with bad informations", flatBeacon => flatBeacon.validate)
  )

  //http://localhost:9000/v0.5.1/beacon/ui
  def ui(version:String) = Action {
    Ok(views.html.beacon.query(version))
  }

  def fromUI(version:String) = Action { implicit request =>
    beaconForm.bindFromRequest.fold(
      formWithErrors => {
        Logger.error(formWithErrors.errorsAsJson.toString)
        BadRequest("There is something bad!")
      },
      flatBeacon => {
        val request = flatBeacon.toAvro
        val response = execute(version, request)
        Ok(views.html.beacon.result(version, flatBeacon, (response.exists, response.frequency)))
      }
    )
  }

}