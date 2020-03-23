package io.razem.influxdbclient

import scala.annotation.implicitNotFound

@implicitNotFound(
  "Cannot find implicit ToPoint instance for type ${A}. Ensure that instance is implemented and reachable."
)
trait ToPoint[A] {

  def convert(value: A): Point

}

object ToPoint {

  implicit object PointToPoint extends ToPoint[Point]{
    @inline
    override def convert(value: Point): Point = value
  }

}
