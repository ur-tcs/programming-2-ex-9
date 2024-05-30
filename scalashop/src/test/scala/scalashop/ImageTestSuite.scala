package scalashop

import image.*
import common.*
import util.Random

trait ImageTestSuite extends munit.FunSuite:
  import munit.Printer
  override def munitPrint(clue: => Any): String =
    clue match
      case img: Image =>
        (0 until img.height)
          .map(y =>
            (0 until img.width)
              .map(x =>
                val px = img(x, y)
                s"At $x, $y : ${alpha(px)}, ${red(px)}, ${green(px)}, ${blue(px)}"
              )
              .reduce(_ ++ "\n" ++ _)
          )
          .reduce(_ ++ "\n" ++ _)
      case other => other.toString()

  /** Check that two images are close enough to each other.
    *
    * Provides +- 5 value margin in each color component in each pixel.
    */
  def assertApproxEqual(expected: Image, obtained: Image): Unit =
    if !approxEqual(expected, obtained) then
      assertEquals(obtained, expected) // guaranteed to fail and print output nicely

  def approxEqual(left: Image, right: Image): Boolean =
    inline val margin = 5
    left.height == right.height &&
    left.width == right.width &&
    (0 until left.height).forall(y =>
      (0 until left.width).forall(x =>
        val l = left(x, y)
        val r = right(x, y)
        val al = alpha(l) - alpha(r)
        val re = red(l) - red(r)
        val gr = green(l) - green(r)
        val bl = blue(l) - blue(r)
        Seq(al, re, gr, bl).map(x => math.abs(x)).forall(_ < margin)
      )
    )

  // image generation utilities:

  def pureColor(h: Int, w: Int, c: ARGB) = new Image(h, w):
    def apply(x: Int, y: Int) = c
  def randomMonochrome(h: Int, w: Int) = new Image(h, w):
    def apply(x: Int, y: Int) =
      val luma = Random.nextInt(256); argb(Random.nextInt(256), luma, luma, luma)

  val testDimensions = Seq(
    (3, 3),
    (3, 4),
    (1, 1),
    (1, 1000),
    (1000, 1)
  ) ++ (1 to 5).map(i => (util.Random.nextInt(20) + 1, util.Random.nextInt(20) + 1))
