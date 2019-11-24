package io.razem.influxdbclient

protected[influxdbclient] object Util {
  def escapeString(str: String): String = str.replaceAll("([ ,=])", "\\\\$1")
}
