package futures.rest

trait Window:
  def println(s: String): Unit

object Window:
  def println(s: String)(using window: Window): Unit = window.println(s)

object ConsoleWindow extends Window:
  def println(s: String): Unit = Console.println(s)
