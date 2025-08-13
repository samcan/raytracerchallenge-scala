import com.samuelcantrell.raytracer.sphere._
import com.samuelcantrell.raytracer.transformation
import com.samuelcantrell.raytracer.ray
import com.samuelcantrell.raytracer.tuple
import com.samuelcantrell.raytracer.material

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

  test("Computing the normal on a translated sphere") {
    val s = sphere()
    val s2 = setTransform(s, transformation.translation(0, 1, 0))
    // The point 1.70711 ≈ 1 + √2/2, and -0.70711 ≈ -√2/2
    val sqrt2over2 = math.sqrt(2) / 2
    val worldPoint = tuple.makePoint(0, 1 + sqrt2over2, -sqrt2over2)
    val n = normalAt(s2, worldPoint)
    val expected = tuple.makeVector(0, sqrt2over2, -sqrt2over2)

    assertEquals(tuple.isEqual(n, expected), true)
  }

  test("Computing the normal on a transformed sphere") {
    val s = sphere()
    val m =
      transformation.scaling(1, 0.5, 1) * transformation.rotation_z(math.Pi / 5)
    val s2 = setTransform(s, m)
    val sqrt2over2 = math.sqrt(2) / 2
    val n = normalAt(s2, tuple.makePoint(0, sqrt2over2, -sqrt2over2))
    val expected = tuple.makeVector(0, 0.97014, -0.24254)

    assertEquals(tuple.isEqual(n, expected, 0.00001), true)
  }

  test("A sphere has a default material") {
    val s = sphere()
    val m = s.objectMaterial
    val defaultMaterial = material.material()

    assertEquals(m == defaultMaterial, true)
  }

  test("A sphere may be assigned a material") {
    val s = sphere()
    var m = material.material()
    m = m.copy(ambient = 1.0)
    val s2 = setMaterial(s, m)

    assertEquals(s2.objectMaterial == m, true)
  }
}
