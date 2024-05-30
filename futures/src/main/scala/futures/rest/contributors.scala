package futures
package rest

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

import sttp.client3.UriContext // provides the `uri` interpolator
import sttp.model.Uri

import upickle.default.ReadWriter

import NoneAsNull.given // treat `None` as `null` when reading or writing JSON

val API_URL = "https://api.github.com"

case class Contributor(
    /** API URL of the corresponding user. */
    url: String
) derives ReadWriter
case class User(
    /** Full name of the user. */
    name: Option[String],
    /** Location of the user. */
    location: Option[String]
) derives ReadWriter:
  override def toString(): String =
    val n = name.getOrElse("Someone")
    val l = location.getOrElse("somewhere")
    f"$n from $l"

def showContributors(
    /** The organization that owns the repository. */
    org: String,
    /** The name of the repository. */
    repo: String,
    /** The number of contributors per page. */
    perPage: Int
)(using
    /** The HTTP backend used to send requests, used by `get`. */
    HttpBackend,
    /** Used to print the results. */
    Window
): Future[Unit] =
  def showNextPage(
      pageUri: Uri,
      prevPagePrinted: Future[Unit]
  ): Future[Unit] =
    ???
  val url = uri"$API_URL/repos/$org/$repo/contributors?per_page=$perPage"
  showNextPage(url, Future.successful(()))

def showContributorsDirect(
    org: String,
    repo: String,
    perPage: Int
)(using HttpBackend, Window): Future[Unit] =
  def showNextPageDirect(
      pageUri: Uri,
      prevPagePrinted: Future[Unit]
  ): Future[Unit] =
    Future:
      ???

  val url = uri"$API_URL/repos/$org/$repo/contributors?per_page=$perPage"
  showNextPageDirect(url, Future.successful(()))
