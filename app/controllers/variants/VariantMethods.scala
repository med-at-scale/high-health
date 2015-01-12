package controllers.variants

import java.io.{ByteArrayInputStream, DataInputStream, InputStream}

import scala.collection.JavaConversions._
//import scala.reflect._

import play.api._
import play.api.mvc._

import org.apache.avro.Schema
import org.apache.avro.io.{DatumReader, Decoder, DecoderFactory}
import org.apache.avro.specific.{SpecificDatumReader}

import org.ga4gh.methods.SearchCallSetsRequest

object VariantMethods extends Controller {

  // API is http://avro.apache.org/docs/current/api/java/org/apache/avro/io/package-summary.html
  def fromJson[T/*:ClassTag*/](json:String, schema:Schema):T = {
    val input:InputStream = new ByteArrayInputStream(json.getBytes())
    val din:DataInputStream = new DataInputStream(input)

    //val schema:Schema = Schema.parse(schemaLines)

    val decoder:Decoder = DecoderFactory.get().jsonDecoder(schema, din)

    val reader:DatumReader[T] = new SpecificDatumReader(schema)
    val datum:T = reader.read(null.asInstanceOf[T], decoder)
    datum
  }

  def searchVariantSets() = TODO

  def searchVariants() = TODO

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
      Notice how fields are encoded with as pairs (because we're not converting to binary before objects?)
       → see http://www.michael-noll.com/blog/2013/03/17/reading-and-writing-avro-files-from-the-command-line/
       → see http://stackoverflow.com/questions/21977704/how-to-avro-binary-encode-the-json-string-using-apache-avro
   */
  def searchCallSets() = Action(BodyParsers.parse.text) { json =>
    val schema = SearchCallSetsRequest.SCHEMA$
    val searchRequest = fromJson[SearchCallSetsRequest](json.body, schema)
    Ok(searchRequest.getVariantSetIds.mkString("\n"))
  }

  def getCallSet(id:String) = TODO

}