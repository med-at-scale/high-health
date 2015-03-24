package server.variant

object VariantServer extends App {


  println("Variant Server starting...")

  lazy val start = Variants.start

  lazy val stop = Variants.stop
}