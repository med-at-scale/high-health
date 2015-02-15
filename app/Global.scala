import play.api._

object Global extends GlobalSettings {

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