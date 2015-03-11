package controllers

import play.api._
import play.api.mvc._

object Application extends Controller {

  def index = Action {
    Ok(views.html.index())
  }

  def CORSoption(path: String) =  Action { request =>
    Ok("").withHeaders(
        "Access-Control-Allow-Headers"   -> "content-type,accept" ,
        "Access-Control-Request-Methods" -> "GET,POST,PUT,DELETE,OPTIONS,PATCH,HEAD",
        "Access-Control-Allow-Origin"    ->  "*" ,
        "Content-Type"                   -> "application/json"
      )
  }
}