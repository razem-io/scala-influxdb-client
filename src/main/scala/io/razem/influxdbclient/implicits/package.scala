package io.razem.influxdbclient

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
  implicit def anyToPoint[A](value: A)(implicit toPoint: ToPoint[A]): Point =
    toPoint.convert(value)

}