package io.razem.influxdbclient

trait ToPoint[A] {

  def convert(value: A): Point

}

object ToPoint {

  implicit object PointToPoint extends ToPoint[Point]{
    @inline
    override def convert(value: Point): Point = value
  }

}
