import com.samuelcantrell.raytracer.sphere._
import com.samuelcantrell.raytracer.transformation
import com.samuelcantrell.raytracer.ray
import com.samuelcantrell.raytracer.tuple
import com.samuelcantrell.raytracer.material
import com.samuelcantrell.raytracer.matrix

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

  test("The normal on a sphere at a point on the x axis") {
    val s = sphere()
    val n = normalAt(s, tuple.makePoint(1, 0, 0))
    val expected = tuple.makeVector(1, 0, 0)

    assertEquals(tuple.isEqual(n, expected), true)
  }

  test("The normal on a sphere at a point on the y axis") {
    val s = sphere()
    val n = normalAt(s, tuple.makePoint(0, 1, 0))
    val expected = tuple.makeVector(0, 1, 0)

    assertEquals(tuple.isEqual(n, expected), true)
  }

  test("The normal on a sphere at a point on the z axis") {
    val s = sphere()
    val n = normalAt(s, tuple.makePoint(0, 0, 1))
    val expected = tuple.makeVector(0, 0, 1)

    assertEquals(tuple.isEqual(n, expected), true)
  }

  test("The normal on a sphere at a nonaxial point") {
    val s = sphere()
    val sqrt3over3 = math.sqrt(3) / 3
    val n = normalAt(s, tuple.makePoint(sqrt3over3, sqrt3over3, sqrt3over3))
    val expected = tuple.makeVector(sqrt3over3, sqrt3over3, sqrt3over3)

    assertEquals(tuple.isEqual(n, expected), true)
  }

  test("The normal is a normalized vector") {
    val s = sphere()
    val sqrt3over3 = math.sqrt(3) / 3
    val n = normalAt(s, tuple.makePoint(sqrt3over3, sqrt3over3, sqrt3over3))
    val normalized = tuple.normalize(n)

    assertEquals(tuple.isEqual(n, normalized), true)
  }

  test("A helper for producing a sphere with a glassy material") {
    val s = glass_sphere()
    assertEquals(matrix.isEqual(s.transform, matrix.Matrix.identity()), true)
    assertEquals(s.objectMaterial.transparency, 1.0)
    assertEquals(s.objectMaterial.refractive_index, 1.5)
  }
}
