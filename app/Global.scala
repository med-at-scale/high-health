import play.api._
import play.api.mvc._

import play.api.http.HeaderNames
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
//object CorsFilter extends Filter {
//  import play.api.libs.concurrent.Execution.Implicits.defaultContext
//  def apply(next: (RequestHeader) => Result)(rh: RequestHeader) = {
//
//    def cors(result: PlainResult): Result = {
//      result.withHeaders( "Access-Control-Allow-Origin" -> "*")
//    }
//
//    next(rh) match {
//      case plain: PlainResult => cors(plain)
//      case async: AsyncResult => async.transform(cors)
//    }
//  }
//}
//

object Global extends WithFilters(CorsFilter) with GlobalSettings {

  //override def doFilter(action: EssentialAction) = CorsFilter(action)

  override def onStart(app: Application) {
    //TODO if DEV

    // Starting the Avro RPC servers
    Logger.warn("Start the RPC servers")

    //TODO if variants plugin/module/methods available then
    server.variant.VariantServer.start
    Logger.warn("Variants server started")
  }

  override def onStop(app: Application) {
    //TODO if DEV

    // Stopping the Avro RPC servers
    Logger.warn("Stop the RPC servers")

    //TODO if variants plugin/module/methods available then
    server.variant.VariantServer.stop
    Logger.warn("Variants server stopped")
  }

}

object CorsFilter extends Filter {

  def apply (nextFilter: (RequestHeader) => Future[Result])(requestHeader: RequestHeader): Future[Result] = {

    nextFilter(requestHeader).map { result =>
      result.withHeaders(HeaderNames.ACCESS_CONTROL_ALLOW_ORIGIN -> "*",
        HeaderNames.ALLOW -> "*",
        HeaderNames.ACCESS_CONTROL_ALLOW_METHODS -> "POST, GET, PUT, DELETE, OPTIONS",
        HeaderNames.ACCESS_CONTROL_ALLOW_HEADERS -> "Origin, X-Requested-With, Content-Type, Accept, Referer, User-Agent"
      )
    }
  }
}