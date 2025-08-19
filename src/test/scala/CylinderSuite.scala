import com.samuelcantrell.raytracer.cylinder._
import com.samuelcantrell.raytracer.ray
import com.samuelcantrell.raytracer.tuple
import com.samuelcantrell.raytracer.equality

class CylinderSuite extends munit.FunSuite {

  test("The default minimum and maximum for a cylinder") {
    val cyl = cylinder()
    
    assertEquals(cyl.minimum, Double.NegativeInfinity)
    assertEquals(cyl.maximum, Double.PositiveInfinity)
  }

  test("The default closed value for a cylinder") {
    val cyl = cylinder()
    
    assertEquals(cyl.closed, false)
  }

  test("A ray misses a cylinder - test 1") {
    val cyl = cylinder()
    val direction = tuple.normalize(tuple.makeVector(0, 1, 0))
    val r = ray.ray(tuple.makePoint(1, 0, 0), direction)
    val xs = localIntersect(cyl, r)

    assertEquals(xs.count, 0)
  }

  test("A ray misses a cylinder - test 2") {
    val cyl = cylinder()
    val direction = tuple.normalize(tuple.makeVector(0, 1, 0))
    val r = ray.ray(tuple.makePoint(0, 0, 0), direction)
    val xs = localIntersect(cyl, r)

    assertEquals(xs.count, 0)
  }

  test("A ray misses a cylinder - test 3") {
    val cyl = cylinder()
    val direction = tuple.normalize(tuple.makeVector(1, 1, 1))
    val r = ray.ray(tuple.makePoint(0, 0, -5), direction)
    val xs = localIntersect(cyl, r)

    assertEquals(xs.count, 0)
  }

  test("A ray strikes a cylinder - test 1") {
    val cyl = cylinder()
    val direction = tuple.normalize(tuple.makeVector(0, 0, 1))
    val r = ray.ray(tuple.makePoint(1, 0, -5), direction)
    val xs = localIntersect(cyl, r)

    assertEquals(xs.count, 2)
    assertEquals(equality.almostEqual(xs(0).t, 5.0), true)
    assertEquals(equality.almostEqual(xs(1).t, 5.0), true)
  }

  test("A ray strikes a cylinder - test 2") {
    val cyl = cylinder()
    val direction = tuple.normalize(tuple.makeVector(0, 0, 1))
    val r = ray.ray(tuple.makePoint(0, 0, -5), direction)
    val xs = localIntersect(cyl, r)

    assertEquals(xs.count, 2)
    assertEquals(equality.almostEqual(xs(0).t, 4.0), true)
    assertEquals(equality.almostEqual(xs(1).t, 6.0), true)
  }

  test("A ray strikes a cylinder - test 3") {
    val cyl = cylinder()
    val direction = tuple.normalize(tuple.makeVector(0.1, 1, 1))
    val r = ray.ray(tuple.makePoint(0.5, 0, -5), direction)
    val xs = localIntersect(cyl, r)

    assertEquals(xs.count, 2)
    assertEquals(equality.almostEqual(xs(0).t, 6.80798, 0.0001), true)
    assertEquals(equality.almostEqual(xs(1).t, 7.08872, 0.0001), true)
  }

  test("Normal vector on a cylinder - point(1, 0, 0)") {
    val cyl = cylinder()
    val n = localNormalAt(cyl, tuple.makePoint(1, 0, 0))
    val expected = tuple.makeVector(1, 0, 0)

    assertEquals(tuple.isEqual(n, expected), true)
  }

  test("Normal vector on a cylinder - point(0, 5, -1)") {
    val cyl = cylinder()
    val n = localNormalAt(cyl, tuple.makePoint(0, 5, -1))
    val expected = tuple.makeVector(0, 0, -1)

    assertEquals(tuple.isEqual(n, expected), true)
  }

  test("Normal vector on a cylinder - point(0, -2, 1)") {
    val cyl = cylinder()
    val n = localNormalAt(cyl, tuple.makePoint(0, -2, 1))
    val expected = tuple.makeVector(0, 0, 1)

    assertEquals(tuple.isEqual(n, expected), true)
  }

  test("Normal vector on a cylinder - point(-1, 1, 0)") {
    val cyl = cylinder()
    val n = localNormalAt(cyl, tuple.makePoint(-1, 1, 0))
    val expected = tuple.makeVector(-1, 0, 0)

    assertEquals(tuple.isEqual(n, expected), true)
  }

  test("Intersecting a constrained cylinder - test 1") {
    val cyl = cylinder().copy(minimum = 1, maximum = 2)
    val direction = tuple.normalize(tuple.makeVector(0.1, 1, 0))
    val r = ray.ray(tuple.makePoint(0, 1.5, 0), direction)
    val xs = localIntersect(cyl, r)

    assertEquals(xs.count, 0)
  }

  test("Intersecting a constrained cylinder - test 2") {
    val cyl = cylinder().copy(minimum = 1, maximum = 2)
    val direction = tuple.normalize(tuple.makeVector(0, 0, 1))
    val r = ray.ray(tuple.makePoint(0, 3, -5), direction)
    val xs = localIntersect(cyl, r)

    assertEquals(xs.count, 0)
  }

  test("Intersecting a constrained cylinder - test 3") {
    val cyl = cylinder().copy(minimum = 1, maximum = 2)
    val direction = tuple.normalize(tuple.makeVector(0, 0, 1))
    val r = ray.ray(tuple.makePoint(0, 0, -5), direction)
    val xs = localIntersect(cyl, r)

    assertEquals(xs.count, 0)
  }

  test("Intersecting a constrained cylinder - test 4") {
    val cyl = cylinder().copy(minimum = 1, maximum = 2)
    val direction = tuple.normalize(tuple.makeVector(0, 0, 1))
    val r = ray.ray(tuple.makePoint(0, 2, -5), direction)
    val xs = localIntersect(cyl, r)

    assertEquals(xs.count, 0)
  }

  test("Intersecting a constrained cylinder - test 5") {
    val cyl = cylinder().copy(minimum = 1, maximum = 2)
    val direction = tuple.normalize(tuple.makeVector(0, 0, 1))
    val r = ray.ray(tuple.makePoint(0, 1, -5), direction)
    val xs = localIntersect(cyl, r)

    assertEquals(xs.count, 0)
  }

  test("Intersecting a constrained cylinder - test 6") {
    val cyl = cylinder().copy(minimum = 1, maximum = 2)
    val direction = tuple.normalize(tuple.makeVector(0, 0, 1))
    val r = ray.ray(tuple.makePoint(0, 1.5, -2), direction)
    val xs = localIntersect(cyl, r)

    assertEquals(xs.count, 2)
  }

  test("Intersecting the caps of a closed cylinder - test 1") {
    val cyl = cylinder().copy(minimum = 1, maximum = 2, closed = true)
    val direction = tuple.normalize(tuple.makeVector(0, -1, 0))
    val r = ray.ray(tuple.makePoint(0, 3, 0), direction)
    val xs = localIntersect(cyl, r)

    assertEquals(xs.count, 2)
  }

  test("Intersecting the caps of a closed cylinder - test 2") {
    val cyl = cylinder().copy(minimum = 1, maximum = 2, closed = true)
    val direction = tuple.normalize(tuple.makeVector(0, -1, 2))
    val r = ray.ray(tuple.makePoint(0, 3, -2), direction)
    val xs = localIntersect(cyl, r)

    assertEquals(xs.count, 2)
  }

  test("Intersecting the caps of a closed cylinder - test 3") {
    val cyl = cylinder().copy(minimum = 1, maximum = 2, closed = true)
    val direction = tuple.normalize(tuple.makeVector(0, -1, 1))
    val r = ray.ray(tuple.makePoint(0, 4, -2), direction)
    val xs = localIntersect(cyl, r)

    assertEquals(xs.count, 2)
  }

  test("Intersecting the caps of a closed cylinder - test 4") {
    val cyl = cylinder().copy(minimum = 1, maximum = 2, closed = true)
    val direction = tuple.normalize(tuple.makeVector(0, 1, 2))
    val r = ray.ray(tuple.makePoint(0, 0, -2), direction)
    val xs = localIntersect(cyl, r)

    assertEquals(xs.count, 2)
  }

  test("Intersecting the caps of a closed cylinder - test 5") {
    val cyl = cylinder().copy(minimum = 1, maximum = 2, closed = true)
    val direction = tuple.normalize(tuple.makeVector(0, 1, 1))
    val r = ray.ray(tuple.makePoint(0, -1, -2), direction)
    val xs = localIntersect(cyl, r)

    assertEquals(xs.count, 2)
  }

  test("The normal vector on a cylinder's end caps - point(0, 1, 0)") {
    val cyl = cylinder().copy(minimum = 1, maximum = 2, closed = true)
    val n = localNormalAt(cyl, tuple.makePoint(0, 1, 0))
    val expected = tuple.makeVector(0, -1, 0)

    assertEquals(tuple.isEqual(n, expected), true)
  }

  test("The normal vector on a cylinder's end caps - point(0.5, 1, 0)") {
    val cyl = cylinder().copy(minimum = 1, maximum = 2, closed = true)
    val n = localNormalAt(cyl, tuple.makePoint(0.5, 1, 0))
    val expected = tuple.makeVector(0, -1, 0)

    assertEquals(tuple.isEqual(n, expected), true)
  }

  test("The normal vector on a cylinder's end caps - point(0, 1, 0.5)") {
    val cyl = cylinder().copy(minimum = 1, maximum = 2, closed = true)
    val n = localNormalAt(cyl, tuple.makePoint(0, 1, 0.5))
    val expected = tuple.makeVector(0, -1, 0)

    assertEquals(tuple.isEqual(n, expected), true)
  }

  test("The normal vector on a cylinder's end caps - point(0, 2, 0)") {
    val cyl = cylinder().copy(minimum = 1, maximum = 2, closed = true)
    val n = localNormalAt(cyl, tuple.makePoint(0, 2, 0))
    val expected = tuple.makeVector(0, 1, 0)

    assertEquals(tuple.isEqual(n, expected), true)
  }

  test("The normal vector on a cylinder's end caps - point(0.5, 2, 0)") {
    val cyl = cylinder().copy(minimum = 1, maximum = 2, closed = true)
    val n = localNormalAt(cyl, tuple.makePoint(0.5, 2, 0))
    val expected = tuple.makeVector(0, 1, 0)

    assertEquals(tuple.isEqual(n, expected), true)
  }

  test("The normal vector on a cylinder's end caps - point(0, 2, 0.5)") {
    val cyl = cylinder().copy(minimum = 1, maximum = 2, closed = true)
    val n = localNormalAt(cyl, tuple.makePoint(0, 2, 0.5))
    val expected = tuple.makeVector(0, 1, 0)

    assertEquals(tuple.isEqual(n, expected), true)
  }
}
