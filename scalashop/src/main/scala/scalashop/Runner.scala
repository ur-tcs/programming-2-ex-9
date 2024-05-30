package scalashop

import org.scalameter.*
import scalashop.image.Image
import scalashop.image.ParImage

class Runner(seq: Image, par: ParImage):
  val standardConfig = config(
    Key.exec.minWarmupRuns := 5,
    Key.exec.maxWarmupRuns := 10,
    Key.exec.benchRuns := 10,
    Key.verbose := false
  ) withWarmer (Warmer.Default())

  def main(args: Array[String]): Unit =
    val radius = 3
    val width = 1920
    val height = 1080
    val seqtime = standardConfig measure {
      // seq.build
    }
    println(s"sequential time: $seqtime")

    val numTasks = 32
    val partime = standardConfig measure {
      // par.build
    }
    println(s"fork/join time: $partime")
    println(s"speedup: ${seqtime.value / partime.value}")
