package sanitizer.reference

import play.api.libs.json._

object ReferenceSanitizer {

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

  def searchReferenceSetsRequest(jsonTxt: String): String = {
    val json: JsValue = Json.parse(jsonTxt)
    val fields = List(
      ("md5checksums", () => Json.obj("md5checksums" -> Json.toJson(List[String]())), (j: JsValue) => j.as[JsObject]),
      ("assemblyId",   () => Json.obj("assemblyId" -> JsNull), (j: JsValue) => Json.obj("assemblyId" -> j)),
      ("pageSize",     () => Json.obj("pageSize" -> JsNull),   (j: JsValue) => Json.obj("pageSize"  -> unionField(j, "int"))),
      ("pageToken",    () => Json.obj("pageToken" -> JsNull),  (j: JsValue) => Json.obj("pageToken" -> unionField(j, "string")))
      )
    val sanJson = sanField(fields, json.as[JsObject])
    sanJson.as[JsValue].toString()
  }

  def searchReferencesRequest(jsonTxt: String): String = {
    val json: JsValue = Json.parse(jsonTxt)
    val fields = List(
      ("referenceSetId", () => Json.obj("referenceSetId" -> JsNull), (j: JsValue) => Json.obj("referenceSetId" -> j)),
      ("parentReferenceIds", () => Json.obj("parentReferenceIds" -> Json.toJson(List[String]())), (j: JsValue) => j.as[JsObject]),
      ("accessions", () => Json.obj("accessions" -> Json.toJson(List[String]())), (j: JsValue) => j.as[JsObject]),
      ("referenceNames", () => Json.obj("referenceNames" -> Json.toJson(List[String]())), (j: JsValue) => j.as[JsObject]),
      ("pageSize",     () => Json.obj("pageSize" -> JsNull),   (j: JsValue) => Json.obj("pageSize"  -> unionField(j, "int"))),
      ("pageToken",    () => Json.obj("pageToken" -> JsNull),  (j: JsValue) => Json.obj("pageToken" -> unionField(j, "string")))
      )
    val sanJson = sanField(fields, json.as[JsObject])
    sanJson.as[JsValue].toString()
  }

}