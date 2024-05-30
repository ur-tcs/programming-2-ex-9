package scalashop

import scalashop.common.task
import scalashop.image.*

import java.awt.Dimension
import java.awt.Graphics
import java.awt.image.BufferedImage
import java.io.FileInputStream
import java.io.InputStream
import java.util.concurrent.ForkJoinTask
import javax.imageio.ImageIO
import javax.swing.JComponent

class PhotoCanvas extends JComponent:

  private var imagePath: Option[String] = None

  var image: Image = loadScalaImage()

  override def getPreferredSize: Dimension =
    Dimension(image.width, image.height)

  private def loadScalaImage(): Image =
    val stream = this.getClass.getResourceAsStream("/scalashop/citrus.jpg")
    try
      loadImage(stream)
    finally
      stream.close()

  private def loadFileImage(path: String): Image =
    val stream = FileInputStream(path)
    try
      loadImage(stream)
    finally
      stream.close()

  private def loadImage(inputStream: InputStream): Image =
    val bufferedImage = ImageIO.read(inputStream)
    Image.from(bufferedImage)

  def reload(): Unit =
    // do we have a known user-chosen path for the image? else load default
    image = imagePath match
      case Some(path) => loadFileImage(path)
      case None       => loadScalaImage()
    repaint()

  def loadFile(path: String): Unit =
    imagePath = Some(path)
    reload()

  def applyFilter(
      filterName: String,
      numTasks: Int,
      radius: Int,
      kernelX: Kernel,
      kernelY: Kernel
  ): Unit =
    val filteredImage = filterName match
      case "simple-blur" =>
        SimpleBlur(image)
      case "blur" =>
        Blur(image, kernelX)
      case "box-blur" =>
        BoxBlur(image, radius)
      case "gaussian-blur" =>
        GaussianBlur(image)
      case "black-and-white" =>
        BlackAndWhite(image)
      case "red-splash" =>
        RedSplash(image)
      case "edge-detection" =>
        SobelEdgeDetection(image, kernelX, kernelY)
      case _ =>
        Identity(image)

    // redraw canvas from the lazy image
    if numTasks == 1
    then
      // don't bother with the splitting call if you don't need to
      // also ensuring (more) correct measurements
      image = filteredImage.seq.build
    else image = filteredImage.par(numTasks).build

    // for a somewhat accurate time measurement, avoid an async repaint
    paintImmediately(0, 0, image.width, image.height)

  override def paintComponent(gcan: Graphics): Unit =
    super.paintComponent(gcan)
    val width = image.width
    val height = image.height
    val bufferedImage =
      BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB)

    for x <- 0 until width; y <- 0 until height do bufferedImage.setRGB(x, y, image(x, y))

    gcan.drawImage(bufferedImage, 0, 0, null)
