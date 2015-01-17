import play.api._

object Global extends GlobalSettings {

  override def onStart(app: Application) {
    Logger.warn("Starting SparkContext")
  }

  override def onStop(app: Application) {
    Logger.warn("Stopping SparkContext")
    server.SparkProvider.sparkContext.stop()
    Thread.sleep(1000)
  }

}