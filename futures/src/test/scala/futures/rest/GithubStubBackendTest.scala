package futures.rest

import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.ExecutionContext.Implicits.global

import sttp.client3.{HttpError, UriContext}
import sttp.model.StatusCode

class GithubStubBackendTest extends munit.FunSuite:
  given HttpBackend = githubStubBackend

  test("contributors endpoint example"):
    // The `uri` interpolator is used to create a `Uri` from a string.
    val url = uri"$API_URL/repos/nasa/marsapi/contributors?per_page=3"

    // `get[T]` returns a `Future[T]`.
    get[List[Contributor]](url)
      // We `map` over the `Future` to access the response.
      .map(res =>
        // res.body` is a `List[RepoContributor]`:
        assertEquals(
          res.body,
          List(
            Contributor(f"$API_URL/users/john"),
            Contributor(f"$API_URL/users/mary"),
            Contributor(f"$API_URL/users/james")
          )
        )
        // Each `Response` contains a `Link` header with a link to the next
        // page. You can use the `getNextPageUrl` function to extract the link.
        // It returns an `Option[String]`:
        assertEquals(
          getNextPageUrl(res.header("Link")),
          Some(f"$API_URL/repos/0/contributors?per_page=3&page=2")
        )
      )

  test("user endpoint example"):
    val url = uri"$API_URL/users/jennifer"
    get[User](url)
      .map(res =>
        assertEquals(
          res.body,
          User(Some("Jennifer"), Some("Nairobi"))
        )
      )

  test("githubStubBackend: get an endpoint that does not exist"):
    get[User](uri"$API_URL/doesnotexist")
      .failed
      .map(error => assertNotFound(error, NOT_FOUND))

  test("githubStubBackend: get a user"):
    get[User](uri"$API_URL/users/john")
      .map(res => assertEquals(res.body, User(Some("John"), Some("Lausanne, Switzerland"))))

  test("githubStubBackend: get a user that does not exist"):
    get[User](uri"$API_URL/users/doesnotexist")
      .failed
      .map(error => assertNotFound(error, USER_NOT_FOUND))

  val marsContributorsUrl = f"$API_URL/repos/nasa/marsapi/contributors"
  val marsContributorsIdUrl = f"$API_URL/repos/0/contributors"
  val marsContributors = CONTRIBUTORS(0)

  test("githubStubBackend: get repo contributors"):
    get[List[Contributor]](uri"$marsContributorsUrl")
      .map(res => assertEquals(res.body, marsContributors))

  test("githubStubBackend: get repo contributors by id"):
    get[List[Contributor]](uri"$marsContributorsIdUrl")
      .map(res => assertEquals(res.body, marsContributors))

  test("githubStubBackend: get repo contributors with pagination"):
    get[List[Contributor]](uri"$marsContributorsUrl?per_page=2")
      .map(res =>
        assertEquals(res.body, marsContributors.slice(0, 2))
        assertEquals(getNextPageUrl(res.header("Link")), Some(f"$marsContributorsIdUrl?per_page=2&page=2"))
      )

  test("githubStubBackend: get repo contributors with pagination and page number"):
    get[List[Contributor]](uri"$marsContributorsUrl?per_page=2&page=2")
      .map(res =>
        assertEquals(res.body, marsContributors.slice(2, 4))
        assertEquals(getNextPageUrl(res.header("Link")), Some(f"$marsContributorsIdUrl?per_page=2&page=3"))
      )

  test("githubStubBackend: get repo contributors with pagination and last page number"):
    get[List[Contributor]](uri"$marsContributorsUrl?per_page=2&page=3")
      .map(res =>
        assertEquals(res.body, marsContributors.slice(4, 5))
        assertEquals(getNextPageUrl(res.header("Link")), None)
      )

  test("githubStubBackend: get repo contributors with pagination and page number that does not exist"):
    get[List[Contributor]](uri"$marsContributorsUrl?per_page=2&page=4")
      .map(res => assertEquals(res.body, List()))

  test("githubStubBackend: get repo that does not exist"):
    get[List[Contributor]](uri"$API_URL/repos/nasa/doesnotexist/contributors")
      .failed
      .map(error => assertNotFound(error, REPO_NOT_FOUND))

  def assertNotFound(e: Throwable, expectedBody: String) =
    e match
      case HttpError(body, code) if body.isInstanceOf[String] =>
        assertEquals(code, StatusCode(404))
        assertEquals(body.asInstanceOf[String], expectedBody)
      case _ => None
