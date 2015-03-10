package server

import java.io.{ByteArrayInputStream, DataInputStream, InputStream}

import scala.collection.JavaConversions._
import scala.reflect.runtime.universe._

import org.apache.avro.Schema
import org.apache.avro.io.{DatumReader, Decoder, DecoderFactory}
import org.apache.avro.specific.SpecificDatumReader
import org.apache.avro.ipc.NettyTransceiver
import org.apache.avro.ipc.specific.SpecificRequestor

object AvroHelper {
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
}