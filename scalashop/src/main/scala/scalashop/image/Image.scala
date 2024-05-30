package scalashop.image

import scalashop.common.*

import java.awt.image.BufferedImage
import scala.collection.Parallel
import scala.collection.parallel.ForkJoinTaskSupport
import scala.reflect.ClassTag
/** Readable alias for underlying pixel representation
  */
type Pixel = ARGB
/** Given two coordinates, provides a Pixel. Builds sequentially by default.
  *
  * @param height
  *   height of the image
  * @param width
  *   width of the image
  */
trait Image(val height: Int, val width: Int) extends Matrix[Pixel]:
  /** Builds this image into an `ArrayImage`, sequentially.
    */
  def build: ArrayImage =
    val dst = new ArrayImage(height, width)

    for
      y <- 0 until height
      x <- 0 until width
    do dst(x, y) = this(x, y)

    dst

  /** Converts this image to a sequential one
    */
  def seq: Image = this

  /** Converts this image to a parallel one running with all available tasks
    */
  def par: ParImage = ParImage(this)

  /** Converts this image to a parallel one running with `numTasks` tasks
    */
  def par(numTasks: Int): ParImage = ParImage(this, numTasks)

  // testing and printing utilities:

  /** Two images are equal if they have the same dimensions, and are the same
    * for every pixel
    */
  override def equals(other: Any): Boolean =
    other match
      case other: Image =>
        other.height == this.height && other.width == this.width && (0 until height).forall(i =>
          (0 until width).forall(j => other(i, j) == this(i, j))
        )
      case _ => false

  /** Prints the image as a grid of separated A, R, G, and B values. Use only on
    * small images! Unreadable otherwise
    */
  def show: String =
    val rows =
      for y <- 0 until height
      yield
        /** This row, as a 4-tuple of colors ( a, r, g, b ) ... as a list
          */
        val thisRowSplit =
          (0 until width).foldLeft(("", "", "", ""): Tuple4[String, String, String, String])((str, x) =>
            val px = this(x, y)
            (
              str._1 ++ " " ++ alpha(px).toString.padTo(3, ' '),
              str._2 ++ " " ++ red(px).toString.padTo(3, ' '),
              str._3 ++ " " ++ green(px).toString.padTo(3, ' '),
              str._4 ++ " " ++ blue(px).toString.padTo(3, ' ')
            )
          )
        s"${thisRowSplit._1}\n${thisRowSplit._2}\n${thisRowSplit._3}\n${thisRowSplit._4}"

    rows.reduce(_ + "\n\n" + _)

object Image:
  def apply(height: Int, width: Int, data: Array[Pixel]): Image =
    ArrayImage(height, width, data)

  def from(source: BufferedImage): Image =
    val img = ArrayImage(source.getHeight(), source.getWidth())

    for
      x <- 0 until img.width
      y <- 0 until img.height
    do img(x, y) = source.getRGB(x, y)

    img
