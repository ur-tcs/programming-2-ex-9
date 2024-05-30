package scalashop

import scalashop.common.argb
import scalashop.image.*

import image.{Image, Kernel}
import util.Random

@main def test =
  val white = argb(255, 255, 255, 255)
  val black = argb(255, 0, 0, 0)
  val red = argb(255, 255, 0, 0)
  val green = argb(255, 0, 255, 0)
  val blue = argb(255, 0, 0, 255)

  val coloredImage = Image(
    3,
    3,
    Array(
      red,
      green,
      blue,
      blue,
      red,
      green,
      white,
      black,
      white
    )
  )

  println("Single black pixel:")
  println("Before filter:")
  println(coloredImage.show)
  println("\nAfter BW fiter:")
  println(BlackAndWhite(coloredImage).show)
  /** Diagonal black line B W W W W W B W W W W W B W W W W W B W W W W W B
    *
    * The image is created via a direct specification of the apply method to
    * avoid writing the whole array
    */
  val diagonalImage = new Image(5, 5):
    def apply(x: Int, y: Int): Pixel = if x == y then black else white

  println("Diagonal black line:")
  println("Before filter:")
  println(diagonalImage.show)
  println("\nAfter Gaussian Blur fiter:")
  println(BlackAndWhite(diagonalImage).show)
  // uncomment to try when Gaussian Blur has been implemented:
  // println(GaussianBlur(diagonalImage).show)

  val singleElementKernel = Kernel(1, 1, Array(5))
  val whiteImage = Image(3, 3, Array.fill(3 * 3)(white))

  /** Image with random pixels. The .build "freezes" the image by computing the
    * random numbers once. Try removing the .build and printing it a few times!
    *
    * This should give you an idea of how the lazy filters and images work.
    */
  val noisyImage = (new Image(3, 3):
    def apply(x: Int, y: Int) =
      argb(Random.nextInt(256), Random.nextInt(256), Random.nextInt(256), Random.nextInt(256))
  ).build

  println("Noisy Image:")
  println(noisyImage.show)
  println("Noisy Image w/ Red Splash:")
  println(RedSplash(noisyImage).show)
