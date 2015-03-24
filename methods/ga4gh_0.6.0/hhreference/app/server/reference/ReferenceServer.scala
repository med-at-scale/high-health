package server.reference

object ReferenceServer extends App {


  println("Reference Server starting...")

  lazy val start = References.start

  lazy val stop = References.stop
}