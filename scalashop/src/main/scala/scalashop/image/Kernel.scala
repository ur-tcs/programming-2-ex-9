package scalashop.image

class Kernel(val height: Int, val width: Int, val data: Array[Float]) extends Matrix[Float]:
  require(width > 0)
  require(height > 0)
  require(width % 2 == 1)
  require(height % 2 == 1)

  val yRadius = (height - 1) / 2
  val xRadius = (width - 1) / 2

  def map(f: Float => Float) = Kernel(height, width, data.map(f))
  def sum = data.sum

  def apply(x: Int, y: Int): Float = data(x + y * width)
  def update(x: Int, y: Int, elem: Float): Unit = data(x + y * width) = elem

/** Provides common kernels such as uniform, or gaussian.
  */
object Kernel:
  /** [ 1, 1, 1, 1, 1, 1, 1, 1, 1]
    *
    * @param size
    *   the width and the height of the uniform kernel
    */
  def uniform(size: Int) = Kernel(size, size, Array.fill(size * size)(1f))

  /** [ 1, 2, 1, 2, 4, 2, 1, 2, 1]
    */
  val gaussian3x3 =
    val data = Array(
      1f, 2f, 1f, 2f, 4f, 2f, 1f, 2f, 1f
    ).map(_ / 16)
    Kernel(3, 3, data)

  /** [-1, 0, 1,
    * -2, 0, 2,
    * -1, 0, 1]
    */
  val sobelX =
    val data = Array(
      -1f, 0f, 1f, -2f, 0f, 2f, -1f, 0f, 1f
    )
    Kernel(3, 3, data)

  /** [-1, -2, -1, 0, 0, 0, 1, 2, 1]
    */
  val sobelY =
    val data = Array(
      -1f, -2f, -1f, 0f, 0f, 0f, 1f, 2f, 1f
    )
    Kernel(3, 3, data)
