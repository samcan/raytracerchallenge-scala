import com.samuelcantrell.raytracer.intersection._
import com.samuelcantrell.raytracer.sphere
import com.samuelcantrell.raytracer.ray.ray
import com.samuelcantrell.raytracer.tuple.makePoint
import com.samuelcantrell.raytracer.tuple.makeVector

class IntersectionSuite extends munit.FunSuite {

  test("An intersection encapsulates t and object") {
    val s = sphere.sphere()
    val i = intersection(3.5, s)

    assertEquals(i.t, 3.5)
    assertEquals(i.obj, s)
  }

  test("Aggregating intersections") {
    val s = sphere.sphere()
    val i1 = intersection(1, s)
    val i2 = intersection(2, s)

    val xs = intersections(i1, i2)

    assertEquals(xs.count, 2)
    assertEquals(xs(0).t, 1.0)
    assertEquals(xs(1).t, 2.0)
  }

  test("Intersections are automatically sorted by t value") {
    val s = sphere.sphere()
    val i1 = intersection(5, s)
    val i2 = intersection(1, s)
    val i3 = intersection(3, s)

    val xs = intersections(i1, i2, i3)

    assertEquals(xs.count, 3)
    assertEquals(xs(0).t, 1.0)
    assertEquals(xs(1).t, 3.0)
    assertEquals(xs(2).t, 5.0)
  }

  test("The hit, when all intersections have positive t") {
    val s = sphere.sphere()
    val i1 = intersection(1, s)
    val i2 = intersection(2, s)
    val xs = intersections(i2, i1)

    val i = hit(xs)

    assertEquals(i.isDefined, true)
    assertEquals(i.get, i1)
  }

  test("The hit, when some intersections have negative t") {
    val s = sphere.sphere()
    val i1 = intersection(-1, s)
    val i2 = intersection(1, s)
    val xs = intersections(i2, i1)

    val i = hit(xs)

    assertEquals(i.isDefined, true)
    assertEquals(i.get, i2)
  }

  test("The hit, when all intersections have negative t") {
    val s = sphere.sphere()
    val i1 = intersection(-2, s)
    val i2 = intersection(-1, s)
    val xs = intersections(i2, i1)

    val i = hit(xs)

    assertEquals(i.isEmpty, true)
  }

  test("The hit is always the lowest nonnegative intersection") {
    val s = sphere.sphere()
    val i1 = intersection(5, s)
    val i2 = intersection(7, s)
    val i3 = intersection(-3, s)
    val i4 = intersection(2, s)
    val xs = intersections(i1, i2, i3, i4)

    val i = hit(xs)

    assertEquals(i.isDefined, true)
    assertEquals(i.get, i4)
  }

  test("Precomputing the state of an intersection") {
    val r = ray(makePoint(0, 0, -5), makeVector(0, 0, 1))
    val shape = sphere.Sphere()
    val i = intersection(4, shape)

    val comps = prepareComputations(i, r)

    assertEquals(comps.t, i.t)
    assertEquals(comps.obj, i.obj)
    assertEquals(comps.point, makePoint(0, 0, -1))
    assertEquals(comps.eyev, makeVector(0, 0, -1))
    assertEquals(comps.normalv, makeVector(0, 0, -1))
  }
}
