package controllers.common

import play.api._
import play.api.mvc._

object CORS extends Controller {

  def option(path: String) =  Action { request =>
    Ok("").withHeaders(
        "Access-Control-Allow-Headers"   -> "content-type,accept" ,
        "Access-Control-Request-Methods" -> "GET,POST,PUT,DELETE,OPTIONS,PATCH,HEAD",
        "Access-Control-Allow-Origin"    ->  "*" ,
        "Content-Type"                   -> "application/json"
      )
  }
}