import com.samuelcantrell.raytracer.material._
import com.samuelcantrell.raytracer.color
import com.samuelcantrell.raytracer.light
import com.samuelcantrell.raytracer.tuple
import com.samuelcantrell.raytracer.pattern

class MaterialSuite extends munit.FunSuite {

  test("The default material") {
    val m = material()
    val expectedColor = color.Color(1, 1, 1)

    assertEquals(color.isEqual(m.materialColor, expectedColor), true)
    assertEquals(m.ambient, 0.1)
    assertEquals(m.diffuse, 0.9)
    assertEquals(m.specular, 0.9)
    assertEquals(m.shininess, 200.0)
  }

  test("Lighting with the eye between the light and the surface") {
    val m = material()
    val position = tuple.makePoint(0, 0, 0)
    val eyev = tuple.makeVector(0, 0, -1)
    val normalv = tuple.makeVector(0, 0, -1)
    val lightSource =
      light.pointLight(tuple.makePoint(0, 0, -10), color.Color(1, 1, 1))
    val result = lighting(m, lightSource, position, eyev, normalv, false)
    val expected = color.Color(1.9, 1.9, 1.9)

    assertEquals(color.isEqual(result, expected), true)
  }

  test("Lighting with the eye between light and surface, eye offset 45°") {
    val m = material()
    val position = tuple.makePoint(0, 0, 0)
    val sqrt2over2 = math.sqrt(2) / 2
    val eyev = tuple.makeVector(0, sqrt2over2, -sqrt2over2)
    val normalv = tuple.makeVector(0, 0, -1)
    val lightSource =
      light.pointLight(tuple.makePoint(0, 0, -10), color.Color(1, 1, 1))
    val result = lighting(m, lightSource, position, eyev, normalv, false)
    val expected = color.Color(1.0, 1.0, 1.0)

    assertEquals(color.isEqual(result, expected), true)
  }

  test("Lighting with eye opposite surface, light offset 45°") {
    val m = material()
    val position = tuple.makePoint(0, 0, 0)
    val eyev = tuple.makeVector(0, 0, -1)
    val normalv = tuple.makeVector(0, 0, -1)
    val lightSource =
      light.pointLight(tuple.makePoint(0, 10, -10), color.Color(1, 1, 1))
    val result = lighting(m, lightSource, position, eyev, normalv, false)
    val expected = color.Color(0.7364, 0.7364, 0.7364)

    assertEquals(color.isEqual(result, expected, 0.0001), true)
  }

  test("Lighting with eye in the path of the reflection vector") {
    val m = material()
    val position = tuple.makePoint(0, 0, 0)
    val sqrt2over2 = math.sqrt(2) / 2
    val eyev = tuple.makeVector(0, -sqrt2over2, -sqrt2over2)
    val normalv = tuple.makeVector(0, 0, -1)
    val lightSource =
      light.pointLight(tuple.makePoint(0, 10, -10), color.Color(1, 1, 1))
    val result = lighting(m, lightSource, position, eyev, normalv, false)
    val expected = color.Color(1.6364, 1.6364, 1.6364)

    assertEquals(color.isEqual(result, expected, 0.0001), true)
  }

  test("Lighting with the light behind the surface") {
    val m = material()
    val position = tuple.makePoint(0, 0, 0)
    val eyev = tuple.makeVector(0, 0, -1)
    val normalv = tuple.makeVector(0, 0, -1)
    val lightSource =
      light.pointLight(tuple.makePoint(0, 0, 10), color.Color(1, 1, 1))
    val result = lighting(m, lightSource, position, eyev, normalv, false)
    val expected = color.Color(0.1, 0.1, 0.1)

    assertEquals(color.isEqual(result, expected), true)
  }

  test("Lighting with the surface in shadow") {
    val m = material()
    val position = tuple.makePoint(0, 0, 0)
    val eyev = tuple.makeVector(0, 0, -1)
    val normalv = tuple.makeVector(0, 0, -1)
    val lightSource =
      light.pointLight(tuple.makePoint(0, 0, -10), color.Color(1, 1, 1))
    val inShadow = true
    val result = lighting(m, lightSource, position, eyev, normalv, inShadow)
    val expected = color.Color(0.1, 0.1, 0.1)

    assertEquals(color.isEqual(result, expected), true)
  }

  test("Lighting with a pattern applied") {
    val stripePattern =
      pattern.stripePattern(color.Color(1, 1, 1), color.Color(0, 0, 0))
    val m = material().copy(
      materialPattern = Some(stripePattern),
      ambient = 1.0,
      diffuse = 0.0,
      specular = 0.0
    )
    val eyev = tuple.makeVector(0, 0, -1)
    val normalv = tuple.makeVector(0, 0, -1)
    val lightSource =
      light.pointLight(tuple.makePoint(0, 0, -10), color.Color(1, 1, 1))

    val c1 =
      lighting(m, lightSource, tuple.makePoint(0.9, 0, 0), eyev, normalv, false)
    val c2 =
      lighting(m, lightSource, tuple.makePoint(1.1, 0, 0), eyev, normalv, false)

    assertEquals(color.isEqual(c1, color.Color(1, 1, 1)), true)
    assertEquals(color.isEqual(c2, color.Color(0, 0, 0)), true)
  }
}
