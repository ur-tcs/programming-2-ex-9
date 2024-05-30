package scalashop

import scalashop.common.*
import scalashop.image.*

import java.util.concurrent.*
import scala.collection.*

import util.Random

class SimpleBlurSuite extends scalashop.ImageTestSuite:

  test("simpleBlur: simple 3x3 image should blur to a constant value") {
    val source: Image = Image(
      3,
      3,
      Array(
        0, 1, 2, 3, 4, 5, 6, 7, 8
      )
    )
    val target: Image = Image(
      3,
      3,
      Array(
        4, 4, 4, 4, 4, 4, 4, 4, 4
      )
    )

    assertEquals(SimpleBlur(source): Image, target)
  }

  (1 to 10).foreach(_ =>
    test("simpleBlur: random 3x3 image should blur to a constant value") {
      val sourceArray = Array.fill(9)(Random.nextInt())
      val mean = argb(
        sourceArray.map(alpha(_)).sum / 9,
        sourceArray.map(red(_)).sum / 9,
        sourceArray.map(green(_)).sum / 9,
        sourceArray.map(blue(_)).sum / 9
      )
      val source: Image = Image(3, 3, sourceArray)
      val target: Image = Image(3, 3, Array.fill(9)(mean))

      assertEquals(SimpleBlur(source): Image, target)
    }
  )

  // safety/invariance tests
  testDimensions.foreach((h, w) =>
    test(s"simpleBlur: white image of size $h x $w should be unchanged") {
      assertApproxEqual(pureColor(h, w, 0xffffffff), SimpleBlur(pureColor(h, w, 0xffffffff)))
    }
    test(s"simpleBlur: black image of size $h x $w should be unchanged") {
      assertApproxEqual(pureColor(h, w, 0x00000000), SimpleBlur(pureColor(h, w, 0x00000000)))
    }
    test(s"simpleBlur: pure red image of size $h x $w should be unchanged") {
      val source: Image = pureColor(h, w, argb(255, 255, 0, 0))
      assertApproxEqual(source, SimpleBlur(source))
    }
    test(s"simpleBlur: pure green image of size $h x $w should be unchanged") {
      val source: Image = pureColor(h, w, argb(255, 0, 255, 0))
      assertApproxEqual(source, SimpleBlur(source))
    }
    test(s"simpleBlur: pure blue image of size $h x $w should be unchanged") {
      val source: Image = pureColor(h, w, argb(255, 0, 0, 255))
      assertApproxEqual(source, SimpleBlur(source))
    }
  )
