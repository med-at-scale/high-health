package sanitizer.variant

import play.api.libs.json._

object VariantSanitizer {

	// reccusrive function to add-up missing fields
	def sanField(fields: List[(String, String)], json: JsObject): JsObject = fields match {
		case head :: tail => {
			val tmp = json \ head._1 match {
				case _: JsUndefined => Some(Json.obj(head._1 -> JsNull))
				case _: JsValue => None
			}
			val njson = tmp.map (jo => json ++ jo)
			 .getOrElse(json)
			sanField(tail, njson)
		}
		case Nil => json
	}

	def puant(json: JsObject) : JsObject = {
		val elt = json \ "pageSize" match {
			case v: JsValue => Json.obj("pageSize" -> Json.obj("int" -> v))
			case x => x.as[JsObject]
		}
		json ++ elt
	}

	def searchVariantsRequest(jsonTxt: String): String = {
		val json: JsValue = Json.parse(jsonTxt)
		val fields = List(
			"pageToken" -> "int", 
			"pageSize" -> "int",
			"referenceId" -> "string", 
			"referenceName" -> "string",
			"callSetIds" -> "???",
			"variantName" -> "string")
		val sanJson = sanField(fields, json.as[JsObject])
		puant(sanJson).as[JsValue].toString()
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
	}


}