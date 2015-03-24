package server

object GA4GHVersions extends Enumeration {
  type GA4GHVersions = Value

  val v0_5_1, v0_6_0 = Value

  def named(version:String) = withName(version.replaceAll("\\.", "_"))
}