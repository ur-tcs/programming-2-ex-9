package futures.rest

import scala.collection.concurrent.TrieMap
import scala.collection.mutable.Queue
import scala.concurrent.{Future, Promise}
import scala.util.{Failure, Success, Try}

import sttp.client3.{Request, Response, UriContext}
import sttp.client3.testing.SttpBackendStub
import sttp.model.StatusCodes

class ContributorsTest extends munit.FunSuite:
  import Event.{Output, Response, ResponseError}

  type Signature = (String, String, Int) => (HttpBackend, Window) ?=> Future[Unit]
  for (name, f) <- List(
      "showContributors" -> (showContributors: Signature),
      "showContributorsDirect" -> (showContributorsDirect: Signature)
    )
  do
    test(f"$name: works"):
      testSchedule(
        stubGithubRequest,
        f("sleepysloths", "lazyloader", 2),
        IArray(
          Response(f"$API_URL/repos/sleepysloths/lazyloader/contributors?per_page=2"),
          Response(f"$API_URL/users/jennifer"),
          Response(f"$API_URL/users/user7"),
          Output("Jennifer from Nairobi"),
          Output("Someone from somewhere")
        )
      )

    test(f"$name: user responses can be returned in any order"):
      testSchedule(
        stubGithubRequest,
        f("sleepysloths", "lazyloader", 2),
        IArray(
          Response(f"$API_URL/repos/sleepysloths/lazyloader/contributors?per_page=2"),
          Response(f"$API_URL/users/user7"),
          Response(f"$API_URL/users/jennifer"),
          Output("Jennifer from Nairobi"),
          Output("Someone from somewhere")
        )
      )

    test(f"$name: works with multiple pages"):
      testSchedule(
        stubGithubRequest,
        f("nasa", "marsapi", 2),
        IArray(
          Response(f"$API_URL/repos/nasa/marsapi/contributors?per_page=2"),
          Response(f"$API_URL/users/john"),
          Response(f"$API_URL/users/mary"),
          Output("John from Lausanne, Switzerland"),
          Output("Mary from somewhere"),
          Response(f"$API_URL/repos/0/contributors?per_page=2&page=2"),
          Response(f"$API_URL/users/james"),
          Response(f"$API_URL/users/patricia"),
          Output("James from Bangalore, India"),
          Output("Patricia from Sydney"),
          Response(f"$API_URL/repos/0/contributors?per_page=2&page=3"),
          Response(f"$API_URL/users/user5"),
          Output("Someone from Tokyo")
        )
      )

    test(f"$name: first page user can be returned after last user"):
      testSchedule(
        stubGithubRequest,
        f("nasa", "marsapi", 2),
        IArray(
          Response(f"$API_URL/repos/nasa/marsapi/contributors?per_page=2"),
          Response(f"$API_URL/users/john"),
          Response(f"$API_URL/repos/0/contributors?per_page=2&page=2"),
          Response(f"$API_URL/users/james"),
          Response(f"$API_URL/users/patricia"),
          Response(f"$API_URL/repos/0/contributors?per_page=2&page=3"),
          Response(f"$API_URL/users/user5"),
          Response(f"$API_URL/users/mary"),
          Output("John from Lausanne, Switzerland"),
          Output("Mary from somewhere"),
          Output("James from Bangalore, India"),
          Output("Patricia from Sydney"),
          Output("Someone from Tokyo")
        )
      )

    test(f"$name: second and third page users can be returned before first page users"):
      testSchedule(
        stubGithubRequest,
        f("nasa", "marsapi", 2),
        IArray(
          Response(f"$API_URL/repos/nasa/marsapi/contributors?per_page=2"),
          Response(f"$API_URL/repos/0/contributors?per_page=2&page=2"),
          Response(f"$API_URL/repos/0/contributors?per_page=2&page=3"),
          Response(f"$API_URL/users/user5"),
          Response(f"$API_URL/users/james"),
          Response(f"$API_URL/users/patricia"),
          Response(f"$API_URL/users/john"),
          Response(f"$API_URL/users/mary"),
          Output("John from Lausanne, Switzerland"),
          Output("Mary from somewhere"),
          Output("James from Bangalore, India"),
          Output("Patricia from Sydney"),
          Output("Someone from Tokyo")
        )
      )
