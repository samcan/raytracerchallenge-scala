import com.samuelcantrell.raytracer.ray._
import com.samuelcantrell.raytracer.tuple
import com.samuelcantrell.raytracer.transformation

class RayTransformSuite extends munit.FunSuite {

  test("Translating a ray") {
    val r = ray(tuple.makePoint(1, 2, 3), tuple.makeVector(0, 1, 0))
    val m = transformation.translation(3, 4, 5)
    val r2 = transform(r, m)

    val expectedOrigin = tuple.makePoint(4, 6, 8)
    val expectedDirection = tuple.makeVector(0, 1, 0)

    assertEquals(tuple.isEqual(r2.origin, expectedOrigin), true)
    assertEquals(tuple.isEqual(r2.direction, expectedDirection), true)
  }

  test("Scaling a ray") {
    val r = ray(tuple.makePoint(1, 2, 3), tuple.makeVector(0, 1, 0))
    val m = transformation.scaling(2, 3, 4)
    val r2 = transform(r, m)

    val expectedOrigin = tuple.makePoint(2, 6, 12)
    val expectedDirection = tuple.makeVector(0, 3, 0)

    assertEquals(tuple.isEqual(r2.origin, expectedOrigin), true)
    assertEquals(tuple.isEqual(r2.direction, expectedDirection), true)
  }
}
