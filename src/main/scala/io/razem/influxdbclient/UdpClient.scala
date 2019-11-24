package io.razem.influxdbclient

import java.net.{DatagramPacket, DatagramSocket, InetSocketAddress}

class UdpClient protected[influxdbclient] (host: String, port: Int) {

  val socket = new DatagramSocket()
  val address = new InetSocketAddress(host, port)

  def write(point: Point): Unit =
    send(point.serialize().getBytes)

  def bulkWrite(points: Seq[Point]): Unit =
    send(points.map(_.serialize()).mkString("\n").getBytes)

  def close(): Unit = socket.close()

  private def send(payload: Array[Byte]): Unit = {
    val packet = new DatagramPacket(payload, payload.length, address)
    socket.send(packet)
  }
}
