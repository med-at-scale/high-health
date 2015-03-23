package server

import play.api.libs.json._

trait Sanitizer {

	// common...can do better ;)
	def unionField(j: JsValue, utype: String): JsValue = j match {
		case _: JsUndefined => JsNull
		case x: JsValue  => Json.obj(utype -> x)
	}


	// reccusrive function to add-up missing fields
	def sanField(fields: List[(String, () => JsObject, JsValue => JsObject)], json: JsObject): JsObject = fields match {
		case head :: tail => {
			val tmp = json \ head._1 match {
				case _: JsUndefined => head._2()
				case x: JsValue     => head._3(x)
			}
			val njson = json ++ tmp
			sanField(tail, njson)
		}
		case Nil => json
	}

	def sanitize(jsonTxt: String, fields: List[(String, () => JsObject, (JsValue) => JsObject)]): String = {
		val json: JsValue = Json.parse(jsonTxt)
		val sanJson = sanField(fields, json.as[JsObject])
		sanJson.as[JsValue].toString()
	}	
}