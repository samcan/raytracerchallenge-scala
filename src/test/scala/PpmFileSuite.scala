import com.samuelcantrell.raytracer.canvas._
import com.samuelcantrell.raytracer.ppmfile._
import com.samuelcantrell.raytracer.color

class PpmFileSuite extends munit.FunSuite {
  test("Constructing the PPM header") {
    val c = Canvas(5, 3)
    val ppm = canvasToPpm(c)

    assertEquals(ppm.apply(0), "P3\n")
    assertEquals(ppm.apply(1), "5 3\n")
    assertEquals(ppm.apply(2), "255\n")
  }

  test("Constructing the PPM pixel data") {
    val c = Canvas(5, 3)
    c.writePixel(0, 0, color.Color(1.5, 0, 0))
    c.writePixel(2, 1, color.Color(0, 0.5, 0))
    c.writePixel(4, 2, color.Color(-0.5, 0, 1))
    val ppm = canvasToPpm(c)

    assertEquals(ppm.apply(3), "255 0 0 0 0 0 0 0 0 0 0 0 0 0 0\n")
    assertEquals(ppm.apply(4), "0 0 0 0 0 0 0 128 0 0 0 0 0 0 0\n")
    assertEquals(ppm.apply(5), "0 0 0 0 0 0 0 0 0 0 0 0 0 0 255\n")
  }

  test("Red color to string") {
    val c = color.Color(1.0, 0.0, 0.0)
    assertEquals(colorToString(c), "255 0 0")
  }

  test("Green color to string") {
    val c = color.Color(0.0, 1.0, 0.0)
    assertEquals(colorToString(c), "0 255 0")
  }

  test("Blue color to string") {
    val c = color.Color(0.0, 0.0, 1.0)
    assertEquals(colorToString(c), "0 0 255")
  }

  test("Clamp negative color value") {
    val c = color.Color(-1.0, 0.0, 1.0)
    assertEquals(colorToString(c), "0 0 255")
  }

  test("Clamp too large color value") {
    val c = color.Color(0.0, 0.5, 1.5)
    assertEquals(colorToString(c), "0 128 255")
  }

  test("PPM file with 2x1 canvas example") {
    val c = Canvas(2, 1)
    c.writePixel(0, 0, color.Color(1.0, 0.0, 0.0))
    c.writePixel(1, 0, color.Color(0.0, 1.0, 0.0))

    val ppm = canvasToPpm(c)

    assertEquals(ppm.apply(0), "P3\n")
    assertEquals(ppm.apply(1), "2 1\n")
    assertEquals(ppm.apply(2), "255\n")
    assertEquals(ppm.apply(3), "255 0 0 0 255 0\n")
  }

  test("PPM file contains complete header and pixel data") {
    val c = Canvas(2, 2)
    c.writePixel(0, 0, color.Color(1.0, 0.0, 0.0)) // red
    c.writePixel(1, 0, color.Color(0.0, 1.0, 0.0)) // green
    c.writePixel(0, 1, color.Color(0.0, 0.0, 1.0)) // blue
    c.writePixel(1, 1, color.Color(1.0, 1.0, 1.0)) // white

    val ppm = canvasToPpm(c)

    assertEquals(
      ppm.length,
      5
    ) // header (3 lines) + pixel data (2 lines), no additional newline
    assertEquals(ppm.apply(0), "P3\n")
    assertEquals(ppm.apply(1), "2 2\n")
    assertEquals(ppm.apply(2), "255\n")
    assertEquals(ppm.apply(3), "255 0 0 0 255 0\n") // first row
    assertEquals(ppm.apply(4), "0 0 255 255 255 255\n") // second row
  }

  test("PPM files are terminated by a newline character") {
    val c = Canvas(5, 3)
    val ppm = canvasToPpm(c)
    assertEquals(ppm.last.endsWith("\n"), true)
  }

  test("Splitting long lines in PPM files") {
    val c = Canvas(10, 2, color.Color(1.0, 0.8, 0.6))
    val ppm = canvasToPpm(c)

    assertEquals(
      ppm.apply(3),
      "255 204 153 255 204 153 255 204 153 255 204 153 255 204 153 255 204\n"
    )
    assertEquals(
      ppm.apply(4),
      "153 255 204 153 255 204 153 255 204 153 255 204 153\n"
    )
    assertEquals(
      ppm.apply(5),
      "255 204 153 255 204 153 255 204 153 255 204 153 255 204 153 255 204\n"
    )
    assertEquals(
      ppm.apply(6),
      "153 255 204 153 255 204 153 255 204 153 255 204 153\n"
    )
  }
}
