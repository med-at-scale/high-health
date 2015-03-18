package sanitizer.variant

import play.api.libs.json._
import server.sanitizer

object VariantSanitizer extends Sanitizer {


	def searchVariantsRequest(jsonTxt: String): String = {
		val fields = List(
			"pageToken" -> "int", 
			"pageSize" -> "int",
			"referenceId" -> "string", 
			"referenceName" -> "string",
			"callSetIds" -> "???",
			"variantName" -> "string")

		sanitize(jsonTxt, fields)
	}

	def searchVariantSetsRequest(jsonTxt: String): String = {
		val json: JsValue = Json.parse(jsonTxt)
		val pageSize = json \ "pageSize" match {
			case _: JsUndefined => Some(Json.obj("pageSize" -> JsNull))
			case jsv: JsValue => None
		}
		val pageToken = json \ "pageToken" match {
			case _: JsUndefined => Some(Json.obj("pageToken" -> JsNull))
			case jsv: JsValue => None
		}
		val psJson = pageSize.map( jo => json.as[JsObject] ++ jo )
		                      .getOrElse(json.as[JsObject])

        val sanJson = pageToken.map( jo => psJson ++ jo )
		                      .getOrElse(psJson.as[JsObject])
		sanJson.as[JsValue].toString()

		sanitize(jsonTxt, fields)
	}

	def searchCallSetsRequest(jsonTxt: String): String = {
		val json: JsValue = Json.parse(jsonTxt)
		val fields = List(
			"pageToken" -> "int", 
			"pageSize" -> "int",
			"name" -> "string"
			)
		val sanJson = sanField(fields, json.as[JsObject])
		sanJson.as[JsValue].toString()
		sanitize(jsonTxt, fields)
	}

}