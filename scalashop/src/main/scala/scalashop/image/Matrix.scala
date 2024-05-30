package scalashop.image
trait Matrix[A]:
  val height: Int
  val width: Int

  /** Access matrix elements
    */
  def apply(x: Int, y: Int): A
