import com.samuelcantrell.raytracer.shape._
import com.samuelcantrell.raytracer.matrix
import com.samuelcantrell.raytracer.ray
import com.samuelcantrell.raytracer.tuple
import com.samuelcantrell.raytracer.sphere
import com.samuelcantrell.raytracer.transformation

class GroupSuite extends munit.FunSuite {

  test("Creating a new group") {
    val g = group()
    val identity = matrix.Matrix.identity()

    assertEquals(matrix.isEqual(g.transform, identity), true)
    assertEquals(g.isEmpty, true)
  }

  test("Adding a child to a group") {
    val g = group()
    val s = testShape()
    val (updatedGroup, updatedShape) = addChild(g, s)

    assertEquals(updatedGroup.isEmpty, false)
    assertEquals(updatedGroup.includes(updatedShape), true)
    assertEquals(updatedShape.parent.isDefined, true)
    assertEquals(updatedShape.parent.get.id, updatedGroup.id)
  }

  test("Intersecting a ray with an empty group") {
    val g = group()
    val r = ray.ray(tuple.makePoint(0, 0, 0), tuple.makeVector(0, 0, 1))
    val xs = g.localIntersect(r)

    assertEquals(xs.count, 0)
  }

  test("Intersecting a ray with a nonempty group") {
    val g = group()
    val s1 = sphere.sphere()
    val s2 = sphere.sphere()
    val s2Transformed = setTransform(s2, transformation.translation(0, 0, -3))
    val s3 = sphere.sphere()
    val s3Transformed = setTransform(s3, transformation.translation(5, 0, 0))
    
    val (g1, _) = addChild(g, s1)
    val (g2, _) = addChild(g1, s2Transformed)
    val (g3, _) = addChild(g2, s3Transformed)
    
    val r = ray.ray(tuple.makePoint(0, 0, -5), tuple.makeVector(0, 0, 1))
    val xs = g3.localIntersect(r)

    assertEquals(xs.count, 4)
    assertEquals(xs(0).obj.id, s2Transformed.id)
    assertEquals(xs(1).obj.id, s2Transformed.id)
    assertEquals(xs(2).obj.id, s1.id)
    assertEquals(xs(3).obj.id, s1.id)
  }

  test("Intersecting a transformed group") {
    val g = group()
    val gTransformed = setTransform(g, transformation.scaling(2, 2, 2))
    val s = sphere.sphere()
    val sTransformed = setTransform(s, transformation.translation(5, 0, 0))
    
    val (gWithChild, _) = addChild(gTransformed, sTransformed)
    
    val r = ray.ray(tuple.makePoint(10, 0, -10), tuple.makeVector(0, 0, 1))
    val xs = intersect(gWithChild, r)

    assertEquals(xs.count, 2)
  }
}