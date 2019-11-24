package io.razem.influxdbclient

class UserManagementSuite extends CustomTestSuite {

  val username = "_test_username"
  val password = "test_password"

  test("A user can be created and dropped") {
    await(influxDb.createUser(username, password))

    var users = await(influxDb.showUsers())
    var usernames = users.series.head.points("user")
    assert(usernames.contains(username))

    await(influxDb.dropUser(username))

    users = await(influxDb.showUsers())
    usernames = users.series.head.points("user")
    assert(!usernames.contains(username))
  }

  test("Passwords are correctly escaped") {
    assert(influxDb.escapePassword("pass'wor\nd") == "pass\\'wor\\\nd")
  }

  test("A user's password can be changed") {
    await(influxDb.createUser(username, password))
    try {
      await(influxDb.setUserPassword(username, "new_password"))
    } finally {
      await(influxDb.dropUser(username))
    }
  }

  test("Privileges can be granted to and revoked from a user") {
    await(influxDb.createUser(username, password))
    val database = influxDb.selectDatabase("_test_database")
    await(database.create())
    try {
      await(influxDb.grantPrivileges(username, "_test_database", ALL))
      await(influxDb.revokePrivileges(username, "_test_database", WRITE))
    } finally {
      await(influxDb.dropUser(username))
      database.drop()
    }
  }

  test("A user can be created as cluster admin") {
    await(influxDb.createUser(username, password, true))
    try {
      await(influxDb.showUsers())
      testIsClusterAdmin()
    } finally {
      await(influxDb.dropUser(username))
    }
  }

  test("A user can be made cluster admin") {
    await(influxDb.createUser(username, password))
    try {
      await(influxDb.makeClusterAdmin(username))
      testIsClusterAdmin()
    } finally {
      await(influxDb.dropUser(username))
    }
  }

  def testIsClusterAdmin() =
    try {
      assert(await(influxDb.userIsClusterAdmin(username)))
    } catch {
      case e: Throwable =>
        await(influxDb.dropUser(username))
        throw e
    }
}
