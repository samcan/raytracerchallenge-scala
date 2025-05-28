import com.samuelcantrell.raytracer.canvas._
import com.samuelcantrell.raytracer.ppmfile._
import com.samuelcantrell.raytracer.color

class PpmFileSuite extends munit.FunSuite {
  test("Constructing the PPM header") {
    val c = Canvas(5, 3)
    val ppm = canvasToPpm(c)

    assertEquals(ppm.apply(0), "P3")
    assertEquals(ppm.apply(1), "5 3")
    assertEquals(ppm.apply(2), "255")
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
}
