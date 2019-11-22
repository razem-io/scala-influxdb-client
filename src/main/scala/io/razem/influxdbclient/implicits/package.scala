package io.razem.influxdbclient

import scala.annotation.implicitNotFound

package object implicits {

  implicit class ToPointSyntax[A](val value: A) extends AnyVal {

    /**
     * Example:
     * {{{
     *   import io.razem.influxdbclient.ToPoint
     *
     *   case class UserLoggedIn(userId: Long)
     *   object UserLoggedIn {
     *     implicit val userLoggedInToPoint: ToPoint[UserLoggedIn] = u =>
     *       Point("user_logins").addTag("user_id", u.userId)
     *   }
     *   // Usage:
     *   def loginUser(user: User) = {
     *     influx.write(UserLoggedIn(user.id).toPoint)
     *     // ...
     *   }
     * }}}
     */
    @implicitNotFound(
      "Cannot find implicit ToPoint instance for type ${A}. Ensure that instance is implemented and reachable."
    )
    def toPoint(implicit toPoint: ToPoint[A]): Point = toPoint.convert(value)

  }

  /**
   * Implicitly converts any type `A` to `Point` if it
   * has `ToPoint[A]` type class implementation in scope.
   *
   * Example:
   * {{{
   *   import io.razem.influxdbclient.ToPoint
   *
   *   case class UserLoggedIn(userId: Long)
   *   object UserLoggedIn {
   *     implicit val userLoggedInToPoint: ToPoint[UserLoggedIn] = u =>
   *       Point("user_logins").addTag("user_id", u.userId)
   *   }
   *   // Usage:
   *   def loginUser(user: User) = {
   *     influx.write(UserLoggedIn(user.id))
   *     // ...
   *   }
   * }}}
   */
  @implicitNotFound(
    "Cannot find implicit ToPoint instance for type ${A}. Ensure that instance is implemented and reachable."
  )
  implicit def anyToPoint[A](value: A)(implicit toPoint: ToPoint[A]): Point =
    toPoint.convert(value)

}