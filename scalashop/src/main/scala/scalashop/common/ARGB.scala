package scalashop.common
/** The value of every pixel is represented as a 32 bit integer. */
type ARGB = Int
/** Returns the alpha component. */
def alpha(c: ARGB): Int = (0xff000000 & c) >>> 24

/** Returns the red component. */
def red(c: ARGB): Int = (0x00ff0000 & c) >>> 16

/** Returns the green component. */
def green(c: ARGB): Int = (0x0000ff00 & c) >>> 8

/** Returns the blue component. */
def blue(c: ARGB): Int = (0x000000ff & c) >>> 0
/** Used to create an ARGB value from separate components. */
def argb(a: Int, r: Int, g: Int, b: Int): ARGB =
  (a << 24) | ((r << 24) >>> 8) | ((g << 24) >>> 16) | ((b << 24) >>> 24)

/** Used to create an ARGB value from separate components. */
def argb(separateChannels: (Int, Int, Int, Int)): ARGB =
  argb.tupled(separateChannels)

/** Separates the channels of an ARGB color, returning (a, r, g, b). */
def separateChannels(source: ARGB): (Int, Int, Int, Int) =
  (
    alpha(source),
    red(source),
    green(source),
    blue(source)
  )
