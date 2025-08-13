import com.samuelcantrell.raytracer.ray._
import com.samuelcantrell.raytracer.tuple

class RaysSuite extends munit.FunSuite {

  test("Creating and querying a ray") {
    val origin = tuple.makePoint(1, 2, 3)
    val direction = tuple.makeVector(4, 5, 6)

    val r = ray(origin, direction)

    assertEquals(tuple.isEqual(r.origin, origin), true)
    assertEquals(tuple.isEqual(r.direction, direction), true)
  }

  test("Computing a point from a distance") {
    val r = ray(tuple.makePoint(2, 3, 4), tuple.makeVector(1, 0, 0))

    val pos0 = position(r, 0)
    val expected0 = tuple.makePoint(2, 3, 4)
    assertEquals(tuple.isEqual(pos0, expected0), true)

    val pos1 = position(r, 1)
    val expected1 = tuple.makePoint(3, 3, 4)
    assertEquals(tuple.isEqual(pos1, expected1), true)

    val posNeg1 = position(r, -1)
    val expectedNeg1 = tuple.makePoint(1, 3, 4)
    assertEquals(tuple.isEqual(posNeg1, expectedNeg1), true)

    val pos2_5 = position(r, 2.5)
    val expected2_5 = tuple.makePoint(4.5, 3, 4)
    assertEquals(tuple.isEqual(pos2_5, expected2_5), true)
  }
}
