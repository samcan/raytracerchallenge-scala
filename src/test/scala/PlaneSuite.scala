import com.samuelcantrell.raytracer.plane._
import com.samuelcantrell.raytracer.ray
import com.samuelcantrell.raytracer.tuple

class PlaneSuite extends munit.FunSuite {

  test("The normal of a plane is constant everywhere") {
    val p = plane()
    val n1 = localNormalAt(p, tuple.makePoint(0, 0, 0))
    val n2 = localNormalAt(p, tuple.makePoint(10, 0, -10))
    val n3 = localNormalAt(p, tuple.makePoint(-5, 0, 150))
    val expected = tuple.makeVector(0, 1, 0)

    assertEquals(tuple.isEqual(n1, expected), true)
    assertEquals(tuple.isEqual(n2, expected), true)
    assertEquals(tuple.isEqual(n3, expected), true)
  }

  test("Intersect with a ray parallel to the plane") {
    val p = plane()
    val r = ray.ray(tuple.makePoint(0, 10, 0), tuple.makeVector(0, 0, 1))
    val xs = localIntersect(p, r)

    assertEquals(xs.count, 0)
  }

  test("Intersect with a coplanar ray") {
    val p = plane()
    val r = ray.ray(tuple.makePoint(0, 0, 0), tuple.makeVector(0, 0, 1))
    val xs = localIntersect(p, r)

    assertEquals(xs.count, 0)
  }

  test("A ray intersecting a plane from above") {
    val p = plane()
    val r = ray.ray(tuple.makePoint(0, 1, 0), tuple.makeVector(0, -1, 0))
    val xs = localIntersect(p, r)

    assertEquals(xs.count, 1)
    assertEquals(xs(0).t, 1.0)
    assertEquals(xs(0).obj, p)
  }

  test("A ray intersecting a plane from below") {
    val p = plane()
    val r = ray.ray(tuple.makePoint(0, -1, 0), tuple.makeVector(0, 1, 0))
    val xs = localIntersect(p, r)

    assertEquals(xs.count, 1)
    assertEquals(xs(0).t, 1.0)
    assertEquals(xs(0).obj, p)
  }
}
