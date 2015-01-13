package controllers.variants

import java.io.{ByteArrayInputStream, DataInputStream, InputStream}

import scala.collection.JavaConversions._
import scala.reflect.runtime.universe._

import play.api._
import play.api.mvc._

import org.apache.avro.Schema
import org.apache.avro.io.{DatumReader, Decoder, DecoderFactory}
import org.apache.avro.specific.SpecificDatumReader

import org.ga4gh.methods.{SearchCallSetsRequest, SearchVariantsRequest}

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

  def searchVariantSets() = TODO

  /**
     Try with
     ```
     curl -i -X POST \
       -d \
        '{
          "variantSetIds": [ "v1", "v6" ],
          "variantName": {"string": "daName"},
          "referenceName": "bof",
          "start": 10,
          "end": 251,
          "pageSize": {"int": 10},
          "pageToken": {"string": "daToken"},
          "callSetIds": null
        }' \
     'http://localhost:9000/variants/search'
     ```
    */
  def searchVariants() = Action(BodyParsers.parse.text) { json =>
    //FIXME → deserve async
    val searchRequest = fromJson[SearchVariantsRequest](json.body)
    val resp = server.VariantMethods.searchVariants(searchRequest)
    NotImplemented("serialiaze the response! " + resp.toString)
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

}