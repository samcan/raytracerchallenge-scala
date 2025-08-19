import com.samuelcantrell.raytracer.cube._
import com.samuelcantrell.raytracer.ray
import com.samuelcantrell.raytracer.tuple

class CubeSuite extends munit.FunSuite {

  test("A ray intersects a cube - +x") {
    val c = cube()
    val r = ray.ray(tuple.makePoint(5, 0.5, 0), tuple.makeVector(-1, 0, 0))
    val xs = localIntersect(c, r)

    assertEquals(xs.count, 2)
    assertEquals(xs(0).t, 4.0)
    assertEquals(xs(1).t, 6.0)
  }

  test("A ray intersects a cube - -x") {
    val c = cube()
    val r = ray.ray(tuple.makePoint(-5, 0.5, 0), tuple.makeVector(1, 0, 0))
    val xs = localIntersect(c, r)

    assertEquals(xs.count, 2)
    assertEquals(xs(0).t, 4.0)
    assertEquals(xs(1).t, 6.0)
  }

  test("A ray intersects a cube - +y") {
    val c = cube()
    val r = ray.ray(tuple.makePoint(0.5, 5, 0), tuple.makeVector(0, -1, 0))
    val xs = localIntersect(c, r)

    assertEquals(xs.count, 2)
    assertEquals(xs(0).t, 4.0)
    assertEquals(xs(1).t, 6.0)
  }

  test("A ray intersects a cube - -y") {
    val c = cube()
    val r = ray.ray(tuple.makePoint(0.5, -5, 0), tuple.makeVector(0, 1, 0))
    val xs = localIntersect(c, r)

    assertEquals(xs.count, 2)
    assertEquals(xs(0).t, 4.0)
    assertEquals(xs(1).t, 6.0)
  }

  test("A ray intersects a cube - +z") {
    val c = cube()
    val r = ray.ray(tuple.makePoint(0.5, 0, 5), tuple.makeVector(0, 0, -1))
    val xs = localIntersect(c, r)

    assertEquals(xs.count, 2)
    assertEquals(xs(0).t, 4.0)
    assertEquals(xs(1).t, 6.0)
  }

  test("A ray intersects a cube - -z") {
    val c = cube()
    val r = ray.ray(tuple.makePoint(0.5, 0, -5), tuple.makeVector(0, 0, 1))
    val xs = localIntersect(c, r)

    assertEquals(xs.count, 2)
    assertEquals(xs(0).t, 4.0)
    assertEquals(xs(1).t, 6.0)
  }

  test("A ray intersects a cube - inside") {
    val c = cube()
    val r = ray.ray(tuple.makePoint(0, 0.5, 0), tuple.makeVector(0, 0, 1))
    val xs = localIntersect(c, r)

    assertEquals(xs.count, 2)
    assertEquals(xs(0).t, -1.0)
    assertEquals(xs(1).t, 1.0)
  }

  test("A ray misses a cube - test 1") {
    val c = cube()
    val r = ray.ray(tuple.makePoint(-2, 0, 0), tuple.makeVector(0.2673, 0.5345, 0.8018))
    val xs = localIntersect(c, r)

    assertEquals(xs.count, 0)
  }

  test("A ray misses a cube - test 2") {
    val c = cube()
    val r = ray.ray(tuple.makePoint(0, -2, 0), tuple.makeVector(0.8018, 0.2673, 0.5345))
    val xs = localIntersect(c, r)

    assertEquals(xs.count, 0)
  }

  test("A ray misses a cube - test 3") {
    val c = cube()
    val r = ray.ray(tuple.makePoint(0, 0, -2), tuple.makeVector(0.5345, 0.8018, 0.2673))
    val xs = localIntersect(c, r)

    assertEquals(xs.count, 0)
  }

  test("A ray misses a cube - test 4") {
    val c = cube()
    val r = ray.ray(tuple.makePoint(2, 0, 2), tuple.makeVector(0, 0, -1))
    val xs = localIntersect(c, r)

    assertEquals(xs.count, 0)
  }

  test("A ray misses a cube - test 5") {
    val c = cube()
    val r = ray.ray(tuple.makePoint(0, 2, 2), tuple.makeVector(0, -1, 0))
    val xs = localIntersect(c, r)

    assertEquals(xs.count, 0)
  }

  test("A ray misses a cube - test 6") {
    val c = cube()
    val r = ray.ray(tuple.makePoint(2, 2, 0), tuple.makeVector(-1, 0, 0))
    val xs = localIntersect(c, r)

    assertEquals(xs.count, 0)
  }

  test("The normal on the surface of a cube - point(1, 0.5, -0.8)") {
    val c = cube()
    val p = tuple.makePoint(1, 0.5, -0.8)
    val normal = localNormalAt(c, p)
    val expected = tuple.makeVector(1, 0, 0)

    assertEquals(tuple.isEqual(normal, expected), true)
  }

  test("The normal on the surface of a cube - point(-1, -0.2, 0.9)") {
    val c = cube()
    val p = tuple.makePoint(-1, -0.2, 0.9)
    val normal = localNormalAt(c, p)
    val expected = tuple.makeVector(-1, 0, 0)

    assertEquals(tuple.isEqual(normal, expected), true)
  }

  test("The normal on the surface of a cube - point(-0.4, 1, -0.1)") {
    val c = cube()
    val p = tuple.makePoint(-0.4, 1, -0.1)
    val normal = localNormalAt(c, p)
    val expected = tuple.makeVector(0, 1, 0)

    assertEquals(tuple.isEqual(normal, expected), true)
  }

  test("The normal on the surface of a cube - point(0.3, -1, -0.7)") {
    val c = cube()
    val p = tuple.makePoint(0.3, -1, -0.7)
    val normal = localNormalAt(c, p)
    val expected = tuple.makeVector(0, -1, 0)

    assertEquals(tuple.isEqual(normal, expected), true)
  }

  test("The normal on the surface of a cube - point(-0.6, 0.3, 1)") {
    val c = cube()
    val p = tuple.makePoint(-0.6, 0.3, 1)
    val normal = localNormalAt(c, p)
    val expected = tuple.makeVector(0, 0, 1)

    assertEquals(tuple.isEqual(normal, expected), true)
  }

  test("The normal on the surface of a cube - point(0.4, 0.4, -1)") {
    val c = cube()
    val p = tuple.makePoint(0.4, 0.4, -1)
    val normal = localNormalAt(c, p)
    val expected = tuple.makeVector(0, 0, -1)

    assertEquals(tuple.isEqual(normal, expected), true)
  }

  test("The normal on the surface of a cube - point(1, 1, 1)") {
    val c = cube()
    val p = tuple.makePoint(1, 1, 1)
    val normal = localNormalAt(c, p)
    val expected = tuple.makeVector(1, 0, 0)

    assertEquals(tuple.isEqual(normal, expected), true)
  }

  test("The normal on the surface of a cube - point(-1, -1, -1)") {
    val c = cube()
    val p = tuple.makePoint(-1, -1, -1)
    val normal = localNormalAt(c, p)
    val expected = tuple.makeVector(-1, 0, 0)

    assertEquals(tuple.isEqual(normal, expected), true)
  }
}