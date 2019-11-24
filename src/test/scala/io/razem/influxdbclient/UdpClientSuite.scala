package io.razem.influxdbclient

import org.scalatest.BeforeAndAfter

class UdpClientSuite extends CustomTestSuite with BeforeAndAfter {

  val databaseName = "_test_database_udp"
  var database: Database = _

  before {
    database = influxDb.selectDatabase(databaseName)
    await(database.create())
  }

  after {
    await(database.drop())
  }

  test("Points can be written") {
    val udpClient = InfluxDB.udpConnect(influxDbContainerIpAddress, influxDbContainerUdpPort)
    udpClient.write(Point("test_measurement").addField("value", 123).addTag("tag_key", "tag_value"))
    udpClient.close()
    Thread.sleep(1000) // to allow flushing to happen inside influx

    val database = influxDb.selectDatabase(databaseName)
    val result = await(database.query("SELECT * FROM test_measurement"))
    assert(result.series.head.records.length == 1)
    assert(result.series.head.records.head("value") == 123)
  }

  test("Points can be written in bulk") {
    val udpClient = InfluxDB.udpConnect(influxDbContainerIpAddress, influxDbContainerUdpPort)
    val timestamp = System.currentTimeMillis()
    udpClient.bulkWrite(
      List(
        Point("test_measurement", timestamp).addField("value", 1).addTag("tag_key", "tag_value"),
        Point("test_measurement", timestamp + 1).addField("value", 2).addTag("tag_key", "tag_value"),
        Point("test_measurement", timestamp + 2).addField("value", 3).addTag("tag_key", "tag_value")
      )
    )
    udpClient.close()
    Thread.sleep(1000) // to allow flushing to happen inside influx

    val database = influxDb.selectDatabase(databaseName)
    val result = await(database.query("SELECT * FROM test_measurement"))
    assert(result.series.head.records.length == 3)
  }

}
