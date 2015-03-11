package controllers.beacon

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

import org.ga4gh.beacon._

import server.GA4GHVersions
import server.beacon._
import sanitizer.beacon.BeaconSanitizer

object BeaconController extends Controller {
  import server.AvroHelper.fromJson


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
    //FIXME → deserves async
    val jsonStringUnsafe = Json.stringify(json.body)
    val jsonString =  jsonStringUnsafe//sanitize?
    println(jsonString)
    val request = fromJson[BEACONRequest](jsonString)
    //val resp = server.VariantMethods.searchVariants(searchRequest)
    val respString:String =  GA4GHVersions.named(version) match {
                              case GA4GHVersions.v0_5_1 => BeaconV0_5_1.index(request).toString
                            }

    Ok(respString).withHeaders(
      "content-type" -> "application/json"
    )
  }
}