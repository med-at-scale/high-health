package sanitizer.variant

import play.api.libs.json._
import server.Sanitizer

object VariantSanitizer extends Sanitizer {

	def searchVariantsRequest(jsonTxt: String): String = {
		val json: JsValue = Json.parse(jsonTxt)
		val fields = List(
			("pageSize",     () => Json.obj("pageSize" -> JsNull),   (j: JsValue) => Json.obj("pageSize"  -> unionField(j, "int"))),
			("pageToken",    () => Json.obj("pageToken" -> JsNull),  (j: JsValue) => Json.obj("pageToken" -> unionField(j, "string"))),
			("referenceId",  () => Json.obj("referenceId" -> JsNull),(j: JsValue) => Json.obj("referenceId" -> unionField(j, "string"))),
			("referenceName",  () => Json.obj("referenceName" -> JsNull),(j: JsValue) => Json.obj("referenceName" -> unionField(j, "string"))),
			("variantName",  () => Json.obj("variantName" -> JsNull),(j: JsValue) => Json.obj("variantName" -> unionField(j, "string"))),
			("callSetIds", () => Json.obj("callSetIds" -> JsNull), (j: JsValue) => j.as[JsObject])
		)

		val sanJson = sanField(fields, json.as[JsObject])
		sanJson.as[JsValue].toString()
	}

	def searchVariantSetsRequest(jsonTxt: String): String = {
		val json: JsValue = Json.parse(jsonTxt)
		val fields = List(
			("pageSize",     () => Json.obj("pageSize" -> JsNull),   (j: JsValue) => Json.obj("pageSize"  -> unionField(j, "int"))),
			("pageToken",    () => Json.obj("pageToken" -> JsNull),  (j: JsValue) => Json.obj("pageToken" -> unionField(j, "string")))
		)

		val sanJson = sanField(fields, json.as[JsObject])
		sanJson.as[JsValue].toString()		
	}

	def searchCallSetsRequest(jsonTxt: String): String = {
		val json: JsValue = Json.parse(jsonTxt)
		val fields = List(
			("name",     () => Json.obj("name" -> JsNull),   (j: JsValue) => Json.obj("name"  -> unionField(j, "string"))),
			("pageSize",     () => Json.obj("pageSize" -> JsNull),   (j: JsValue) => Json.obj("pageSize"  -> unionField(j, "int"))),
			("pageToken",    () => Json.obj("pageToken" -> JsNull),  (j: JsValue) => Json.obj("pageToken" -> unionField(j, "string")))
		)

		val sanJson = sanField(fields, json.as[JsObject])
		sanJson.as[JsValue].toString()		
	}

}