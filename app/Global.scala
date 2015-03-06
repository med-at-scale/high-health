import play.api._
import play.api.mvc._


/*object CorsFilter extends Filter {
  import play.api.libs.concurrent.Execution.Implicits.defaultContext
  def apply(next: (RequestHeader) => Result)(rh: RequestHeader) = {

    def cors(result: PlainResult): Result = {
      result.withHeaders( "Access-Control-Allow-Origin" -> "*")
    }

    next(rh) match {
      case plain: PlainResult => cors(plain)
      case async: AsyncResult => async.transform(cors)
    }
  }
}*/


object Global extends GlobalSettings {

//  override def doFilter(action: EssentialAction) = CorsFilter(action)

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

