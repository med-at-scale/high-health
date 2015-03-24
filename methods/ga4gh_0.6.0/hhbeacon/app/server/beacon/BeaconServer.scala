package server.beacon

import server.GA4GHVersions

object BeaconServer extends App {

  def start(version:String) = {
    println(s"Beacon $version Server starting...")
    GA4GHVersions.named(version) match {
      case GA4GHVersions.v0_5_1 => BeaconV0_5_1.start
    }
  }

  def stop(version:String) = {
    println(s"Beacon $version Server stopping...")
    GA4GHVersions.named(version) match {
      case GA4GHVersions.v0_5_1 => BeaconV0_5_1.stop
    }
  }
}