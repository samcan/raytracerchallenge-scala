import com.samuelcantrell.raytracer.canvas._
import com.samuelcantrell.raytracer.color

class CanvasSuite extends munit.FunSuite {
  val red = color.Color(1, 0, 0)
  val black = color.Color(0, 0, 0)

  test("Creating a canvas") {
    val c = Canvas(10, 20)
    assertEquals(c.width, 10)
    assertEquals(c.height, 20)

    for {
      i <- 0 until 20
      j <- 0 until 10
    } assertEquals(c.pixelAt(j, i), black)
  }

  test("Check that canvas has correct number of data rows") {
    val c = Canvas(10, 20)
    assertEquals(c.rows(), 20)
  }

  test("Check that canvas has correct number of data cols") {
    val c = Canvas(10, 20)
    assertEquals(c.cols(), 10)
  }

  test("Creating a canvas with a non-default color") {
    val c = Canvas(10, 20, color.Color(0.1, 0.2, 0.3))

    for {
      i <- 0 until 20
      j <- 0 until 10
    } assertEquals(c.pixelAt(j, i), color.Color(0.1, 0.2, 0.3))
  }

  test("Check a pixel with pixelAt()") {
    val c = Canvas(10, 20)
    assertEquals(c.pixelAt(2, 1), black)
  }

  test("Writing pixels to a canvas") {
    val c = Canvas(10, 20)
    assertEquals(c.writePixel(2, 3, red), true)
    assertEquals(c.pixelAt(2, 3), red)
  }

  test("Writing to a col outside the canvas min fails") {
    val c = Canvas(10, 20)
    assertEquals(c.writePixel(-1, 5, red), false)
  }

  test("Writing to a row outside the canvas min fails") {
    val c = Canvas(10, 20)
    assertEquals(c.writePixel(1, -1, red), false)
  }

  test("Writing to a col outside the canvas max fails") {
    val c = Canvas(10, 20)
    assertEquals(c.writePixel(11, 5, red), false)
  }

  test("Writing to a row outside the canvas max fails") {
    val c = Canvas(10, 20)
    assertEquals(c.writePixel(5, 21, red), false)
  }
}
