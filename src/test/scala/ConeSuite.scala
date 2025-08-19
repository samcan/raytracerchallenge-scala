import com.samuelcantrell.raytracer.cone._
import com.samuelcantrell.raytracer.ray
import com.samuelcantrell.raytracer.tuple
import com.samuelcantrell.raytracer.equality

class ConeSuite extends munit.FunSuite {

  test("Intersecting a cone with a ray - test 1") {
    val shape = cone()
    val direction = tuple.normalize(tuple.makeVector(0, 0, 1))
    val r = ray.ray(tuple.makePoint(0, 0, -5), direction)
    val xs = localIntersect(shape, r)

    assertEquals(xs.count, 2)
    assertEquals(equality.almostEqual(xs(0).t, 5.0), true)
    assertEquals(equality.almostEqual(xs(1).t, 5.0), true)
  }

  test("Intersecting a cone with a ray - test 2") {
    val shape = cone()
    val direction = tuple.normalize(tuple.makeVector(1, 1, 1))
    val r = ray.ray(tuple.makePoint(0, 0, -5), direction)
    val xs = localIntersect(shape, r)

    assertEquals(xs.count, 2)
    assertEquals(equality.almostEqual(xs(0).t, 8.66025, 0.001), true)
    assertEquals(equality.almostEqual(xs(1).t, 8.66025, 0.001), true)
  }

  test("Intersecting a cone with a ray - test 3") {
    val shape = cone()
    val direction = tuple.normalize(tuple.makeVector(-0.5, -1, 1))
    val r = ray.ray(tuple.makePoint(1, 1, -5), direction)
    val xs = localIntersect(shape, r)

    assertEquals(xs.count, 2)
    assertEquals(equality.almostEqual(xs(0).t, 4.55006, 0.001), true)
    assertEquals(equality.almostEqual(xs(1).t, 49.44994, 0.001), true)
  }

  test("Intersecting a cone with a ray parallel to one of its halves") {
    val shape = cone()
    val direction = tuple.normalize(tuple.makeVector(0, 1, 1))
    val r = ray.ray(tuple.makePoint(0, 0, -1), direction)
    val xs = localIntersect(shape, r)

    assertEquals(xs.count, 1)
    assertEquals(equality.almostEqual(xs(0).t, 0.35355, 0.001), true)
  }

  test("Intersecting a cone's end caps - test 1") {
    val shape = cone().copy(minimum = -0.5, maximum = 0.5, closed = true)
    val direction = tuple.normalize(tuple.makeVector(0, 1, 0))
    val r = ray.ray(tuple.makePoint(0, 0, -5), direction)
    val xs = localIntersect(shape, r)

    assertEquals(xs.count, 0)
  }

  test("Intersecting a cone's end caps - test 2") {
    val shape = cone().copy(minimum = -0.5, maximum = 0.5, closed = true)
    val direction = tuple.normalize(tuple.makeVector(0, 1, 1))
    val r = ray.ray(tuple.makePoint(0, 0, -0.25), direction)
    val xs = localIntersect(shape, r)

    assertEquals(xs.count, 2)
  }

  test("Intersecting a cone's end caps - test 3") {
    val shape = cone().copy(minimum = -0.5, maximum = 0.5, closed = true)
    val direction = tuple.normalize(tuple.makeVector(0, 1, 0))
    val r = ray.ray(tuple.makePoint(0, 0, -0.25), direction)
    val xs = localIntersect(shape, r)

    assertEquals(xs.count, 4)
  }

  test("Computing the normal vector on a cone - point(0, 0, 0)") {
    val shape = cone()
    val n = localNormalAt(shape, tuple.makePoint(0, 0, 0))
    val expected = tuple.makeVector(0, 0, 0)

    assertEquals(tuple.isEqual(n, expected), true)
  }

  test("Computing the normal vector on a cone - point(1, 1, 1)") {
    val shape = cone()
    val n = localNormalAt(shape, tuple.makePoint(1, 1, 1))
    val expected = tuple.makeVector(1, -math.sqrt(2), 1)

    assertEquals(tuple.isEqual(n, expected), true)
  }

  test("Computing the normal vector on a cone - point(-1, -1, 0)") {
    val shape = cone()
    val n = localNormalAt(shape, tuple.makePoint(-1, -1, 0))
    val expected = tuple.makeVector(-1, 1, 0)

    assertEquals(tuple.isEqual(n, expected), true)
  }
}