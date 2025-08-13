import com.samuelcantrell.raytracer.sphere._
import com.samuelcantrell.raytracer.intersection._
import com.samuelcantrell.raytracer.ray
import com.samuelcantrell.raytracer.tuple

class SphereSuite extends munit.FunSuite {

  test("A ray intersects a sphere at two points") {
    val r = ray.ray(tuple.makePoint(0, 0, -5), tuple.makeVector(0, 0, 1))
    val s = sphere()

    val xs = intersect(s, r)

    assertEquals(xs.count, 2)
    assertEquals(xs(0).t, 4.0)
    assertEquals(xs(1).t, 6.0)
  }

  test("sphere() returns unique spheres") {
    val s1 = sphere()
    val s2 = sphere()

    assertEquals(s1 != s2, true)
    assertEquals(s1.id != s2.id, true)
  }

  test("A ray misses a sphere") {
    val r = ray.ray(tuple.makePoint(0, 2, -5), tuple.makeVector(0, 0, 1))
    val s = sphere()

    val xs = intersect(s, r)

    assertEquals(xs.count, 0)
  }

  test("A ray is tangent to a sphere") {
    val r = ray.ray(tuple.makePoint(0, 1, -5), tuple.makeVector(0, 0, 1))
    val s = sphere()

    val xs = intersect(s, r)

    assertEquals(xs.count, 2)
    assertEquals(xs(0).t, 5.0)
    assertEquals(xs(1).t, 5.0)
  }

  test("A ray originates inside a sphere") {
    val r = ray.ray(tuple.makePoint(0, 0, 0), tuple.makeVector(0, 0, 1))
    val s = sphere()

    val xs = intersect(s, r)

    assertEquals(xs.count, 2)
    assertEquals(xs(0).t, -1.0)
    assertEquals(xs(1).t, 1.0)
  }

  test("A sphere is behind a ray") {
    val r = ray.ray(tuple.makePoint(0, 0, 5), tuple.makeVector(0, 0, 1))
    val s = sphere()

    val xs = intersect(s, r)

    assertEquals(xs.count, 2)
    assertEquals(xs(0).t, -6.0)
    assertEquals(xs(1).t, -4.0)
  }

  test("Intersect sets the object on the intersection") {
    val r = ray.ray(tuple.makePoint(0, 0, -5), tuple.makeVector(0, 0, 1))
    val s = sphere()

    val xs = intersect(s, r)

    assertEquals(xs.count, 2)
    assertEquals(xs(0).obj, s)
    assertEquals(xs(1).obj, s)
  }
}
