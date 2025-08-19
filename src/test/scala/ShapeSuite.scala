import com.samuelcantrell.raytracer.shape._
import com.samuelcantrell.raytracer.matrix
import com.samuelcantrell.raytracer.material
import com.samuelcantrell.raytracer.transformation
import com.samuelcantrell.raytracer.ray
import com.samuelcantrell.raytracer.tuple
import com.samuelcantrell.raytracer.sphere
import com.samuelcantrell.raytracer.equality

class ShapeSuite extends munit.FunSuite {

  test("The default transformation") {
    val s = testShape()
    val identity = matrix.Matrix.identity()

    assertEquals(matrix.isEqual(s.transform, identity), true)
  }

  test("Assigning a transformation") {
    val s = testShape()
    val t = transformation.translation(2, 3, 4)
    val s2 = setTransform(s, t)

    assertEquals(matrix.isEqual(s2.transform, t), true)
  }

  test("The default material") {
    val s = testShape()
    val m = s.objectMaterial
    val defaultMaterial = material.material()

    assertEquals(m == defaultMaterial, true)
  }

  test("Assigning a material") {
    val s = testShape()
    var m = material.material()
    m = m.copy(ambient = 1.0)
    val s2 = setMaterial(s, m)

    assertEquals(s2.objectMaterial == m, true)
  }

  test("Intersecting a scaled shape with a ray") {
    val r = ray.ray(tuple.makePoint(0, 0, -5), tuple.makeVector(0, 0, 1))
    val s = testShape()
    val s2 = setTransform(s, transformation.scaling(2, 2, 2))
    val xs = intersect(s2, r)

    // Check that the saved ray has been transformed correctly
    assertEquals(s2.savedRay.isDefined, true)
    val savedRay = s2.savedRay.get
    val expectedOrigin = tuple.makePoint(0, 0, -2.5)
    val expectedDirection = tuple.makeVector(0, 0, 0.5)

    assertEquals(tuple.isEqual(savedRay.origin, expectedOrigin), true)
    assertEquals(tuple.isEqual(savedRay.direction, expectedDirection), true)
  }

  test("Intersecting a translated shape with a ray") {
    val r = ray.ray(tuple.makePoint(0, 0, -5), tuple.makeVector(0, 0, 1))
    val s = testShape()
    val s2 = setTransform(s, transformation.translation(5, 0, 0))
    val xs = intersect(s2, r)

    // Check that the saved ray has been transformed correctly
    assertEquals(s2.savedRay.isDefined, true)
    val savedRay = s2.savedRay.get
    val expectedOrigin = tuple.makePoint(-5, 0, -5)
    val expectedDirection = tuple.makeVector(0, 0, 1)

    assertEquals(tuple.isEqual(savedRay.origin, expectedOrigin), true)
    assertEquals(tuple.isEqual(savedRay.direction, expectedDirection), true)
  }

  test("Computing the normal on a translated shape") {
    val s = testShape()
    val s2 = setTransform(s, transformation.translation(0, 1, 0))
    val sqrt2over2 = math.sqrt(2) / 2
    val n = normalAt(s2, tuple.makePoint(0, 1 + sqrt2over2, -sqrt2over2))
    val expected = tuple.makeVector(0, sqrt2over2, -sqrt2over2)

    assertEquals(tuple.isEqual(n, expected, 0.00001), true)
  }

  test("Computing the normal on a transformed shape") {
    val s = testShape()
    val m =
      transformation.scaling(1, 0.5, 1) * transformation.rotation_z(math.Pi / 5)
    val s2 = setTransform(s, m)
    val sqrt2over2 = math.sqrt(2) / 2
    val n = normalAt(s2, tuple.makePoint(0, sqrt2over2, -sqrt2over2))
    val expected = tuple.makeVector(0, 0.97014, -0.24254)

    assertEquals(tuple.isEqual(n, expected, 0.00001), true)
  }

  test("A shape has a parent attribute") {
    val s = testShape()
    assertEquals(s.parent, None)
  }

  test("Converting a point from world to object space") {
    val g1 = group()
    val g1Transformed = setTransform(g1, transformation.rotation_y(math.Pi / 2))
    val g2 = group()
    val g2Transformed = setTransform(g2, transformation.scaling(2, 2, 2))
    val (g1WithChild, g2WithParent) = addChild(g1Transformed, g2Transformed)
    val s = sphere.sphere()
    val sTransformed = setTransform(s, transformation.translation(5, 0, 0))
    val (g2WithChild, sWithParent) = addChild(g2WithParent.asInstanceOf[Group], sTransformed)
    
    val p = worldToObject(sWithParent, tuple.makePoint(-2, 0, -10))
    val expected = tuple.makePoint(0, 0, -1)
    
    assertEquals(tuple.isEqual(p, expected, equality.EPSILON), true)
  }

  test("Converting a normal from object to world space") {
    val g1 = group()
    val g1Transformed = setTransform(g1, transformation.rotation_y(math.Pi / 2))
    val g2 = group()
    val g2Transformed = setTransform(g2, transformation.scaling(1, 2, 3))
    val (g1WithChild, g2WithParent) = addChild(g1Transformed, g2Transformed)
    val s = sphere.sphere()
    val sTransformed = setTransform(s, transformation.translation(5, 0, 0))
    val (g2WithChild, sWithParent) = addChild(g2WithParent.asInstanceOf[Group], sTransformed)
    
    val sqrt3over3 = math.sqrt(3) / 3
    val n = normalToWorld(sWithParent, tuple.makeVector(sqrt3over3, sqrt3over3, sqrt3over3))
    val expected = tuple.makeVector(0.2857, 0.4286, -0.8571)
    
    assertEquals(tuple.isEqual(n, expected, 0.0001), true)
  }

  test("Finding the normal on a child object") {
    val g1 = group()
    val g1Transformed = setTransform(g1, transformation.rotation_y(math.Pi / 2))
    val g2 = group()
    val g2Transformed = setTransform(g2, transformation.scaling(1, 2, 3))
    val (g1WithChild, g2WithParent) = addChild(g1Transformed, g2Transformed)
    val s = sphere.sphere()
    val sTransformed = setTransform(s, transformation.translation(5, 0, 0))
    val (g2WithChild, sWithParent) = addChild(g2WithParent.asInstanceOf[Group], sTransformed)
    
    val n = normalAt(sWithParent, tuple.makePoint(1.7321, 1.1547, -5.5774))
    val expected = tuple.makeVector(0.2857, 0.4286, -0.8571)
    
    assertEquals(tuple.isEqual(n, expected, 0.0001), true)
  }
}
