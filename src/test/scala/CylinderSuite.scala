import com.samuelcantrell.raytracer.cylinder._
import com.samuelcantrell.raytracer.ray
import com.samuelcantrell.raytracer.tuple
import com.samuelcantrell.raytracer.equality

class CylinderSuite extends munit.FunSuite {

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
}
