package scalashop

import scalashop.common.*
import scalashop.image.*

import java.util.concurrent.*
import scala.collection.*

import util.Random

class ConvolutionSuite extends scalashop.ImageTestSuite:

  test("convolution: simple 3x3 image should blur to a constant value (10 pts)") {
    val source = Image(
      3,
      3,
      Array(
        0, 1, 2, 3, 4, 5, 6, 7, 8
      )
    )
    val target = Image(
      3,
      3,
      Array(
        4, 4, 4, 4, 4, 4, 4, 4, 4
      )
    )

    assertEquals(SimpleBlur(source): Image, target)
  }

  (1 to 10).foreach(_ =>
    test("convolution: random 3x3 image under the identity kernel should be unchanged (5 pts)") {
      val source = Image(3, 3, Array.fill(9)(Random.nextInt()))
      val kernel = Kernel(1, 1, Array(1f))
      assertEquals(Convolution(source, kernel): Image, source)
    }
    test("convolution: random 3x3 image under the null kernel should be zeroed (5 pts)") {
      val source = Image(3, 3, Array.fill(9)(Random.nextInt()))
      val target = Image(3, 3, Array.fill(9)(0))
      val kernel = Kernel(1, 1, Array(0f))
      assertEquals(Convolution(source, kernel): Image, target)
    }
  )

  test("convolution: simple 5x5 matrix should transform correctly under 3x3 kernel (100 pts)") {
    val source = Image(5, 5, Array.fill(25)(1))
    val kernel = Kernel(
      3,
      3,
      Array(
        2, 2, 2, 2, 2, 2, 2, 2, 2
      )
    )
    val target = Image(
      5,
      5,
      Array(
        8, 12, 12, 12, 8, 12, 18, 18, 18, 12, 12, 18, 18, 18, 12, 12, 18, 18, 18, 12, 8, 12, 12, 12,
        8
      )
    )
    assertEquals(Convolution(source, kernel): Image, target)
  }
