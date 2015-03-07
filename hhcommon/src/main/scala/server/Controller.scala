package server

import play.api._
import play.api.mvc._
//import play.api.libs.functional.syntax._
import play.api.mvc.BodyParsers._

trait CORS {
  
  def CORSAction(verbs: String)(f: => Request[Any] => Result) = {
	Action(request => f(request).withHeaders(
      "Access-Control-Allow-Headers"   -> "content-type,accept" ,
      "Access-Control-Request-Methods" -> verbs    ,
      "Access-Control-Allow-Origin"    ->  "*" ,
      "Content-Type"                   -> "application/json"                      
      )
    )
  }

  def CORSAction[T](verbs: String, b: BodyParser[T])(f: => Request[T] => Result) = {
	  Action(b)(request => f(request).withHeaders(
      "Access-Control-Allow-Headers"   -> "content-type,accept" ,
      "Access-Control-Request-Methods" -> verbs    ,
      "Access-Control-Allow-Origin"    ->  "*"     ,
      "Content-Type"                   -> "application/json"         
      )
    )
  }

}
