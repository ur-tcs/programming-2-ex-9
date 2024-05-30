package scalashop

import scalashop.common.*
import scalashop.image.*

import java.util.concurrent.*
import scala.collection.*

import util.Random

class ParallelSuite extends scalashop.ImageTestSuite:

  test(
    "parallel: ParImage.build should not throw an exception on a single threaded build (5 pts)"
  ) {
    val source = Image(10, 10, Array.fill(100)(Random.nextInt()))
    source.par(1).build
  }

  test("parallel: ParImage.build should not throw an exception on too many threads (5 pts)") {
    val source = Image(10, 10, Array.fill(100)(Random.nextInt()))
    source.par(100).build
  }

  test("parallel: ParImage.build should not lose data (5 pts)") {
    val source = Image(10, 10, Array.fill(100)(Random.nextInt()))
    assertEquals(Identity(source).par.build: Image, source)
  }

  test("parallel: ParImage.build should not lose data on very high number of threads (5 pts)") {
    val source = Image(10, 10, Array.fill(100)(Random.nextInt()))
    assertEquals(Identity(source).par(100).build: Image, source)
  }

  val filters: Seq[(String, Image => Image)] = Seq(
    ("Identity", Identity(_)),
    ("BlackAndWhite", BlackAndWhite(_)),
    ("RedSplash", RedSplash(_)),
    ("SimpleBlur", SimpleBlur(_)),
    ("BoxBlur(1)", BoxBlur(_, 1)),
    ("BoxBlur(3)", BoxBlur(_, 3)),
    ("BoxBlur(5)", BoxBlur(_, 5)),
    ("GaussianBlur", GaussianBlur(_))
  )

  filters.foreach((name, filter) =>
    test(
      s"parallel: ParImage.build should compute filters correctly on random image - $name (10 pts)"
    ) {
      val source = Image(10, 10, Array.fill(100)(Random.nextInt()))
      assertEquals(filter(source).par.build: Image, filter(source).build: Image)
    }
  )
