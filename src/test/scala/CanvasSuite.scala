import com.samuelcantrell.raytracer.canvas._
import com.samuelcantrell.raytracer.color

class CanvasSuite extends munit.FunSuite {
  test("Creating a canvas") {
    val c = Canvas(10, 20)
    assertEquals(c.width, 10)
    assertEquals(c.height, 20)

    // TODO: check that Canvas has only colors of (0, 0, 0)
  }
}
