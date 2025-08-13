import com.samuelcantrell.raytracer.sphere._
import com.samuelcantrell.raytracer.transformation
import com.samuelcantrell.raytracer.matrix
import com.samuelcantrell.raytracer.ray
import com.samuelcantrell.raytracer.tuple

class SphereTransformSuite extends munit.FunSuite {

  test("A sphere's default transformation") {
    val s = sphere()
    val identity = matrix.Matrix.identity()

    assertEquals(matrix.isEqual(s.transform, identity), true)
  }

  test("Changing a sphere's transformation") {
    val s = sphere()
    val t = transformation.translation(2, 3, 4)
    val s2 = setTransform(s, t)

    assertEquals(matrix.isEqual(s2.transform, t), true)
  }

  test("Intersecting a scaled sphere with a ray") {
    val r = ray.ray(tuple.makePoint(0, 0, -5), tuple.makeVector(0, 0, 1))
    val s = sphere()
    val s2 = setTransform(s, transformation.scaling(2, 2, 2))
    val xs = intersect(s2, r)

    assertEquals(xs.count, 2)
    assertEquals(xs(0).t, 3.0)
    assertEquals(xs(1).t, 7.0)
  }

  test("Intersecting a translated sphere with a ray") {
    val r = ray.ray(tuple.makePoint(0, 0, -5), tuple.makeVector(0, 0, 1))
    val s = sphere()
    val s2 = setTransform(s, transformation.translation(5, 0, 0))
    val xs = intersect(s2, r)

    assertEquals(xs.count, 0)
  }
}
