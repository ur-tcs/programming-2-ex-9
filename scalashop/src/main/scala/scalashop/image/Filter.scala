package scalashop.image

import scalashop.common.*
/** Identity filter, does not change pixels of the source image.
  */
class Identity(src: Image) extends Image(src.height, src.width):
  def apply(x: Int, y: Int): Pixel =
    src(x, y)
/** Black and white filter, transforms the source image in a grayscale one.
  */
class BlackAndWhite(src: Image) extends Image(src.height, src.width):
  // we generate a weighted grayscale image
  // to do this, we compute the "Luma" of each pixel
  // these numbers come from a standard called Rec 601
  // and are computed based on how we perceive colour and brightness
  // see: https://en.wikipedia.org/wiki/Luma_(video)
  val lumaR = 0.299f
  val lumaG = 0.587f
  val lumaB = 0.114f
  def grayscale(input: Pixel) =
    ???

  def apply(x: Int, y: Int): Pixel = grayscale(src(x, y))
class RedSplash(src: Image) extends BlackAndWhite(src):
  def isRedEnough(px: Pixel) =
    val r = red(px).toFloat
    val g = green(px).toFloat
    val b = blue(px).toFloat
    (r/g > 1.7) && (r/b > 1.7)

  override def apply(x: Int, y: Int): Pixel = ???
/** Performs a simple box-blur of given radius by averaging over a pixel's
  * neighbours
  *
  * @param src
  *   source image
  */
class SimpleBlur(src: Image) extends Image(src.height, src.width):
  val radius: Int = 3

  def apply(x: Int, y: Int): Pixel =
    ???
/** Produce the convolution of an image with a kernel
  *
  * @param src
  *   source image
  * @param kernel
  *   kernel to convolve with
  */
class Convolution(src: Image, kernel: Kernel) extends Image(src.height, src.width):
  def apply(x: Int, y: Int): Pixel =
    ???

/** Blur filter, computes a convolution between the image and the given blurring
  * kernel.
  */
class Blur(src: Image, kernel: Kernel) extends Image(src.height, src.width):
  private val convolution = Convolution(
    src,
    kernel.map(_ / kernel.sum)
  ) // for blurring, kernels are normalized to have sum = 1
  def apply(x: Int, y: Int): Pixel = convolution(x, y)

/** Box blur filter, blur filter with matrix of size `(radius * 2 + 1) x (radius
  * * 2 + 1)` filled with ones.
  */
class BoxBlur(src: Image, radius: Int) extends Blur(src, Kernel.uniform(radius * 2 + 1))

/** Gaussian blur filter, blurs with a 3x3 Gaussian kernel.
  */
class GaussianBlur(src: Image) extends Blur(src, Kernel.gaussian3x3)
/** Sobel edge detection filter, used to detect the horizontal and vertical
  * edges of an image. Take a look at `Kernel.sobelX` and `Kernel.sobelY` for
  * default kernels for this filter.
  */
class SobelEdgeDetection(src: Image, kernelX: Kernel, kernelY: Kernel)
    extends Image(src.height, src.width):
  require((kernelX.width, kernelX.height) == (kernelY.width, kernelY.height))

  val bwSrc = BlackAndWhite(src)
  val xConvo = Convolution(bwSrc, kernelX)
  val yConvo = Convolution(bwSrc, kernelY)

  def apply(x: Int, y: Int): Pixel =
    // Keep only 1 channel as they're all the same (black and white)
    val xVal = red(xConvo(x, y))
    val yVal = red(yConvo(x, y))
    val grayScale = math.sqrt(xVal * xVal + yVal * yVal).toInt

    argb(255, grayScale, grayScale, grayScale)
