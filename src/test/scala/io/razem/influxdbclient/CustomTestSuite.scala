package io.razem.influxdbclient

import java.util.function.Consumer

import com.dimafeng.testcontainers.{ForAllTestContainer, GenericContainer}
import com.github.dockerjava.api.model.ExposedPort
import org.scalatest.{BeforeAndAfterAll, FunSuite}
import org.testcontainers.containers.wait.strategy.Wait

import scala.concurrent.duration._
import scala.concurrent.{Await, Awaitable, ExecutionContext}

class CustomTestSuite extends FunSuite with BeforeAndAfterAll with ForAllTestContainer {
  private val influxDbInternalPort = 8086
  val databaseUsername = "influx_user"
  val databasePassword = "influx_password"

  override val container = GenericContainer(
    "influxdb:1.5",
    exposedPorts = Seq(influxDbInternalPort),
    waitStrategy = Wait.forHttp("/ping").forStatusCode(204),
    env = Map(
      "INFLUXDB_HTTP_AUTH_ENABLED" -> "true",
      "INFLUXDB_ADMIN_USER" -> "influx_user",
      "INFLUXDB_ADMIN_PASSWORD" -> "influx_password",
      "INFLUXDB_UDP_ENABLED" -> "true",
      "INFLUXDB_UDP_BIND_ADDRESS" -> s":$influxDbInternalPort",
      "INFLUXDB_UDP_DATABASE" -> "_test_database_udp",
      "INFLUXDB_UDP_BATCH_SIZE" -> "1",
      "INFLUXDB_UDP_BATCH_TIMEOUT" -> "1ms",
      "INFLUXDB_UDP_BATCH_PENDING" -> "1"
    )
  )

  container.container.withCreateContainerCmdModifier(
    toJavaConsumer(cmd => cmd.withExposedPorts(ExposedPort.udp(influxDbInternalPort)))
  )

  val waitDuration: FiniteDuration = 2.seconds
  implicit val ec: ExecutionContext = ExecutionContext.global

  var influxDb: InfluxDB = _
  var influxDbContainerIpAddress: String = _
  var influxDbContainerTcpPort: Int = _
  var influxDbContainerUdpPort: Int = _

  def await[T](f: Awaitable[T], duration: Duration = waitDuration): T = Await.result(f, duration)

  def waitForInternalDatabase(): Unit =
    while (Await.result(influxDb.showDatabases().map(_.isEmpty), 10.seconds)) {
      Thread.sleep(100)
    }

  override protected def beforeAll(): Unit = {
    import collection.JavaConverters._

    influxDbContainerIpAddress = container.containerIpAddress
    influxDbContainerTcpPort = container.mappedPort(influxDbInternalPort)
    influxDbContainerUdpPort = container.container.getContainerInfo.getNetworkSettings.getPorts.getBindings.asScala
      .get(ExposedPort.udp(influxDbInternalPort))
      .flatMap(_.headOption.map(_.getHostPortSpec.toInt))
      .getOrElse(0)

    influxDb =
      InfluxDB.connect(influxDbContainerIpAddress, influxDbContainerTcpPort, databaseUsername, databasePassword)

    super.beforeAll()
  }

  override def afterAll: Unit =
    influxDb.close()

  // workaround for scala 2.11
  // can be removed as soon support for 2.11 gets dropped
  def toJavaConsumer[T](consumer: (T) => Unit): Consumer[T] =
    new Consumer[T] {
      override def accept(t: T): Unit =
        consumer(t)
    }
}
