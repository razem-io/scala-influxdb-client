package io.razem.influxdbclient

import scala.concurrent.Future

protected[influxdbclient] trait UserManagement { self: InfluxDB =>
  def createUser(username: String, password: String, isClusterAdmin: Boolean = false): Future[QueryResult] = {
    var queryString = "CREATE USER %s WITH PASSWORD '%s'".format(username, password)
    if (isClusterAdmin)
      queryString = queryString + " WITH ALL PRIVILEGES"
    exec(queryString)
  }

  def dropUser(username: String): Future[QueryResult] = {
    val queryString = "DROP USER " + username
    exec(queryString)
  }

  def showUsers(): Future[QueryResult] =
    query("SHOW USERS")

  def setUserPassword(username: String, password: String): Future[QueryResult] =
    exec("SET PASSWORD FOR %s='%s'".format(username, password))

  def grantPrivileges(username: String, database: String, privilege: Privilege): Future[QueryResult] =
    exec("GRANT %s ON %s TO %s".format(privilege, database, username))

  def revokePrivileges(username: String, database: String, privilege: Privilege): Future[QueryResult] =
    exec("REVOKE %s ON %s FROM %s".format(privilege, database, username))

  def makeClusterAdmin(username: String): Future[QueryResult] =
    exec("GRANT ALL PRIVILEGES TO %s".format(username))

  def userIsClusterAdmin(username: String): Future[Boolean] =
    showUsers().map(
      result => result.series.head.records.exists(record => record("user") == username && record("admin") == true)
    )

  protected[influxdbclient] def escapePassword(password: String): String =
    password.replaceAll("(['\n])", "\\\\$1")
}

sealed trait Privilege
case object READ extends Privilege
case object WRITE extends Privilege
case object ALL extends Privilege
