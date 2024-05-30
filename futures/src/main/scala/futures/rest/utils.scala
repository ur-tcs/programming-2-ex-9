package futures.rest

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

import sttp.client3.{asStringAlways, basicRequest, HttpError, Response, SttpBackend}
import sttp.model.Uri

import upickle.default.{read, Reader, Writer}

type HttpBackend = SttpBackend[Future, Any]

def get[T](
    /** The URL to call. */
    uri: Uri
)(
    /** The HTTP backend used to send requests. */
    using backend: HttpBackend
)(
    /** Used to decode the response JSON to a `T`. */
    using Reader[T]
): Future[Response[T]] =
  basicRequest
    .get(uri)
    .response(asStringAlways)
    .send(backend)
    .map(res =>
      if res.isSuccess then res.copy(body = read[T](res.body))
      else throw HttpError(res.body, res.code)
    )

/** This is inspired by the GitHub API documentation:
  * https://docs.github.com/en/rest/guides/using-pagination-in-the-rest-api?apiVersion=2022-11-28#example-creating-a-pagination-method.
  *
  * @param response
  * @return
  */
def getNextPageUrl(linkHeader: Option[String]): Option[String] =
  val NEXT_PATTERN_RE = """<([\S]*)>; rel="[Nn]ext"""".r
  linkHeader match
    case Some(value) => NEXT_PATTERN_RE.findFirstMatchIn(value).map(_.group(1))
    case _           => None

object NoneAsNull:
  given [T](using writer: Writer[T]): Writer[Option[T]] = writer.comap(_.orNull.asInstanceOf[T])
  given [T](using reader: Reader[T]): Reader[Option[T]] = reader.mapNulls(Option.apply)
