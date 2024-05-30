package scalashop.image

import scalashop.common.*

import java.util.concurrent.ForkJoinPool
import scala.collection.parallel.CollectionConverters.ArrayIsParallelizable
import scala.collection.parallel.ForkJoinTaskSupport
/** Fully built image with a concrete underlying Array storing pixel data.
  */
final class ArrayImage(
    height: Int,
    width: Int,
    private val data: Array[Pixel]
) extends Image(height, width):

  /** Access underlying image data
    */
  def apply(x: Int, y: Int) = data(x + y * width)

  /** Update underlying image data, mutating state
    */
  def update(x: Int, y: Int, elem: Pixel) = data(x + y * width) = elem
  /** Construct an empty ArrayImage with all pixels set to 0.
    */
  def this(height: Int, width: Int) = this(height, width, Array.fill(height * width)(0))

  // building related functions have been trivialized
  override def build: ArrayImage = this
  override def seq: Image = this
