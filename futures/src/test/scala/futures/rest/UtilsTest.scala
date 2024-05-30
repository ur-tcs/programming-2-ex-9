package futures.rest

import upickle.default.{read, write}

class UtilsTest extends munit.FunSuite:

  test("getNextPageUrl: can extract next page URL"):
    val linkHeader =
      f"""<$API_URL/repositories/lampepfl/dotty/contributors?per_page=2&page=2>; rel="next", <$API_URL/repositories/609151934/contributors?per_page=2&page=4>; rel="last")"""
    assertEquals(
      getNextPageUrl(Some(linkHeader)),
      Some(f"$API_URL/repositories/lampepfl/dotty/contributors?per_page=2&page=2")
    )

  test("getNextPageUrl: works when next is the last element"):
    val linkHeader =
      f"""<$API_URL/repositories/609151934/contributors?per_page=2&page=4>; rel="last"), <$API_URL/repositories/lampepfl/dotty/contributors?per_page=2&page=2>; rel="next""""
    assertEquals(
      getNextPageUrl(Some(linkHeader)),
      Some(f"$API_URL/repositories/lampepfl/dotty/contributors?per_page=2&page=2")
    )

  test("NoneAsNull: can read when fields are non-`null`"):
    val json = """{"name": "John", "location": "Lausanne"}"""
    assertEquals(read[User](json), User(Some("John"), Some("Lausanne")))

  test("NoneAsNull: can read when fields are `null`"):
    val json = """{"name": null, "location": null}"""
    assertEquals(read[User](json), User(None, None))

  test("NoneAsNull: can write when fields are `Some`"):
    val json = """{"name":"John","location":"Lausanne"}"""
    assertEquals(write(User(Some("John"), Some("Lausanne"))), json)

  test("NoneAsNull: can write when fields are `None`"):
    val json = """{"name":null,"location":null}"""
    assertEquals(write(User(None, None)), json)
