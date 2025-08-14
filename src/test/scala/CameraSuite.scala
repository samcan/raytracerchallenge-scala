import com.samuelcantrell.raytracer.camera._
import com.samuelcantrell.raytracer.matrix
import com.samuelcantrell.raytracer.tuple
import com.samuelcantrell.raytracer.transformation
import com.samuelcantrell.raytracer.world
import com.samuelcantrell.raytracer.color
import com.samuelcantrell.raytracer.equality

class CameraSuite extends munit.FunSuite {

  test("Constructing a camera") {
    val hsize = 160
    val vsize = 120
    val fieldOfView = math.Pi / 2
    val c = camera(hsize, vsize, fieldOfView)

    assertEquals(c.hsize, 160)
    assertEquals(c.vsize, 120)
    assertEquals(c.fieldOfView, math.Pi / 2)
    assertEquals(matrix.isEqual(c.transform, matrix.Matrix.identity()), true)
  }

  test("The pixel size for a horizontal canvas") {
    val c = camera(200, 125, math.Pi / 2)
    assertEquals(equality.almostEqual(c.pixelSize, 0.01), true)
  }

  test("The pixel size for a vertical canvas") {
    val c = camera(125, 200, math.Pi / 2)
    assertEquals(equality.almostEqual(c.pixelSize, 0.01), true)
  }

  test("Constructing a ray through the center of the canvas") {
    val c = camera(201, 101, math.Pi / 2)
    val r = rayForPixel(c, 100, 50)

    assertEquals(tuple.isEqual(r.origin, tuple.makePoint(0, 0, 0)), true)
    assertEquals(tuple.isEqual(r.direction, tuple.makeVector(0, 0, -1)), true)
  }

  test("Constructing a ray through a corner of the canvas") {
    val c = camera(201, 101, math.Pi / 2)
    val r = rayForPixel(c, 0, 0)

    assertEquals(tuple.isEqual(r.origin, tuple.makePoint(0, 0, 0)), true)
    assertEquals(
      tuple.isEqual(
        r.direction,
        tuple.makeVector(0.66519, 0.33259, -0.66851),
        0.00001
      ),
      true
    )
  }

  test("Constructing a ray when the camera is transformed") {
    val c = camera(201, 101, math.Pi / 2)
    val transformedCamera = c.copy(transform =
      transformation
        .rotation_y(math.Pi / 4) * transformation.translation(0, -2, 5)
    )
    val r = rayForPixel(transformedCamera, 100, 50)

    val sqrt2over2 = math.sqrt(2) / 2
    assertEquals(tuple.isEqual(r.origin, tuple.makePoint(0, 2, -5)), true)
    assertEquals(
      tuple.isEqual(
        r.direction,
        tuple.makeVector(sqrt2over2, 0, -sqrt2over2),
        0.00001
      ),
      true
    )
  }

  test("Rendering a world with a camera") {
    val w = world.defaultWorld()
    val c = camera(11, 11, math.Pi / 2)
    val from = tuple.makePoint(0, 0, -5)
    val to = tuple.makePoint(0, 0, 0)
    val up = tuple.makeVector(0, 1, 0)
    val transformedCamera =
      c.copy(transform = transformation.viewTransform(from, to, up))
    val image = render(transformedCamera, w)
    val pixelColor = image.pixelAt(5, 5)
    val expected = color.Color(0.38066, 0.47583, 0.2855)

    assertEquals(color.isEqual(pixelColor, expected, 0.00001), true)
  }
}
