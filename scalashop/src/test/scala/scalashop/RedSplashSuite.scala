package scalashop

import scalashop.common.*
import scalashop.image.*

import java.util.concurrent.*
import scala.collection.*

import util.Random

class RedSplashSuite extends scalashop.ImageTestSuite:

  // safety/invariance tests
  testDimensions.foreach((h, w) =>
    test(s"redSplash: white image of size $h x $w should be unchanged") {
      assertApproxEqual(pureColor(h, w, 0xffffffff), RedSplash(pureColor(h, w, 0xffffffff)))
    }
    test(s"redSplash: black image of size $h x $w should be unchanged") {
      assertApproxEqual(pureColor(h, w, 0x00000000), RedSplash(pureColor(h, w, 0x00000000)))
    }
    test(s"redSplash: randomized grayscale image of size $h x $w should be unchanged") {
      val source: Image = randomMonochrome(h, w).build
      assertApproxEqual(source, RedSplash(source))
    }
    test(s"redSplash: pure red image of size $h x $w should be unchanged") {
      val source: Image = pureColor(h, w, argb(255, 255, 0, 0)).build
      assertApproxEqual(source, RedSplash(source))
    }
  )

  // correctness tests
  /** Some colors and their red splashed versions
    */
  val colorPairs = Seq(
    argb(255, 255, 0, 0) -> argb(255, 255, 0, 0), // red
    argb(255, 0, 255, 0) -> argb(255, 149, 149, 149), // green
    argb(255, 0, 0, 255) -> argb(255, 29, 29, 29), // blue
    argb(116, 206, 39, 66) -> argb(116, 206, 39, 66), // random colors
    argb(9, 157, 231, 189) -> argb(9, 204, 204, 204),
    argb(248, 57, 247, 62) -> argb(248, 169, 169, 169),
    argb(79, 217, 185, 95) -> argb(79, 184, 184, 184),
    argb(48, 140, 35, 31) -> argb(48, 140, 35, 31),
    argb(9, 66, 255, 123) -> argb(9, 183, 183, 183),
    argb(110, 231, 164, 250) -> argb(110, 193, 193, 193),
    argb(74, 32, 152, 51) -> argb(74, 104, 104, 104),
    argb(51, 182, 188, 64) -> argb(51, 172, 172, 172),
    argb(246, 172, 178, 177) -> argb(246, 176, 176, 176)
  )

  testDimensions.foreach((h, w) =>
    test(s"redSplash: green image of size $h x $w should transform correctly") {
      assertApproxEqual(
        pureColor(h, w, colorPairs(1)._2),
        RedSplash(pureColor(h, w, colorPairs(1)._1))
      )
    }
    test(s"redSplash: blue image of size $h x $w should transform correctly") {
      assertApproxEqual(
        pureColor(h, w, colorPairs(2)._2),
        RedSplash(pureColor(h, w, colorPairs(2)._1))
      )
    }
  )

  def pickRandom[A](seq: Seq[A]): A =
    seq(Random.nextInt(seq.length))

  (1 to 10).foreach(i =>
    // generate a test case
    val (sourceArray, targetArray) = Array.fill(9)(pickRandom(colorPairs)).unzip
    val (sourceImage, targetImage) = (Image(3, 3, sourceArray), Image(3, 3, targetArray))
    test("redSplash: 3x3 random image should transform correctly") {
      assertApproxEqual(targetImage, RedSplash(sourceImage))
    }
  )
