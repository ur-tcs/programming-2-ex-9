package futures.rest
import scala.collection.mutable.ArrayBuffer
import scala.concurrent.Future
import scala.util.{Failure, Success, Try}
import scala.util.hashing.MurmurHash3.{mix}

import sttp.client3.{HttpError, Request, Response, SttpBackendOptions}
import sttp.client3.testing.SttpBackendStub
import sttp.model.{Header, StatusCode}

import upickle.default.write

val githubStubBackend =
  SttpBackendStub
    .asynchronousFuture
    .whenAnyRequest
    .thenRespondF(req => Future.fromTry(stubGithubRequest(req)))

def stubGithubRequest(req: Request[?, ?]): Try[Response[String]] =
  if !req.uri.toString.startsWith(API_URL) then
    return Failure(HttpError("Not found", StatusCode(404)))
  val page = req.uri.params.get("page").map(_.toInt).getOrElse(1)
  val perPage = req.uri.params.get("per_page").map(_.toInt).getOrElse(30)
  req.uri.path match
    case Seq("repos", organization, repoName, "contributors") =>
      stubContributors(organization, repoName, perPage, page)
    case Seq("repos", repoId, "contributors") =>
      stubContributors(repoId.toInt, perPage, page)
    case Seq("users", userId) =>
      USERS
        .get(userId)
        .map(user => Success(Response.ok(write(user))))
        .getOrElse(Failure(HttpError(USER_NOT_FOUND, StatusCode(404))))
    case _ =>
      Failure(HttpError(NOT_FOUND, StatusCode(404)))

def stubContributors(organization: String, repoName: String, perPage: Int, page: Int): Try[Response[String]] =
  val repoId = REPO_NAMES.indexWhere(_ == (organization, repoName))
  if repoId < 0 then Failure(HttpError(REPO_NOT_FOUND, StatusCode(404)))
  else stubContributors(repoId, perPage, page)

def stubContributors(repoId: Int, perPage: Int, page: Int): Try[Response[String]] =
  if repoId < 0 || repoId >= REPO_NAMES.length then
    Future.failed(HttpError(REPO_NOT_FOUND, StatusCode(404)))
  val numContributors = CONTRIBUTORS(repoId).length
  val start = math.max(math.min((page - 1) * perPage, numContributors), 0)
  val end = math.max(math.min(page * perPage, numContributors), 0)
  val linkHeader =
    val baseUrl = f"$API_URL/repos/$repoId/contributors?per_page=$perPage"
    val links = ArrayBuffer(
      f"""<$baseUrl&page=1>; rel="first"""",
      f"""<$baseUrl&page=${numContributors / perPage}>; rel="last""""
    )
    if page > 1 then links.append(f"""<$baseUrl&page=${page - 1}>; rel="prev"""")
    if end < numContributors then links.append(f"""<$baseUrl&page=${page + 1}>; rel="next"""")
    links.mkString(", ")
  Success(
    Response(
      write(CONTRIBUTORS(repoId).slice(start, end)),
      StatusCode.Ok,
      "",
      List(Header("Link", linkHeader))
    )
  )

val REPO_NOT_FOUND = """{
  "message": "Not Found",
  "documentation_url": "https://docs.github.com/rest/repos/repos#get-a-repository"
}"""

val USER_NOT_FOUND = """{
  "message": "Not Found",
  "documentation_url": "https://docs.github.com/rest/reference/users#get-a-user"
}"""

val NOT_FOUND = """{
  "message": "Not Found",
  "documentation_url": "https://docs.github.com/rest"
}"""

val REPO_NAMES = List(
  ("nasa", "marsapi"),
  ("sleepysloths", "lazyloader")
)

val CONTRIBUTORS = List(
  List("john", "mary", "james", "patricia", "user5"),
  List("jennifer", "user7")
).map(_.map(id => Contributor(f"$API_URL/users/${id}")))

val USERS = Map(
  // marsapi
  "john" -> User(Some("John"), Some("Lausanne, Switzerland")),
  "mary" -> User(Some("Mary"), None),
  "james" -> User(Some("James"), Some("Bangalore, India")),
  "patricia" -> User(Some("Patricia"), Some("Sydney")),
  "user5" -> User(None, Some("Tokyo")),
  // lazyloader
  "jennifer" -> User(Some("Jennifer"), Some("Nairobi")),
  "user7" -> User(None, None)
)
