import com.samuelcantrell.raytracer.pattern._
import com.samuelcantrell.raytracer.color
import com.samuelcantrell.raytracer.tuple
import com.samuelcantrell.raytracer.sphere
import com.samuelcantrell.raytracer.transformation

class PatternsSuite extends munit.FunSuite {

  // Background constants
  val black = color.Color(0, 0, 0)
  val white = color.Color(1, 1, 1)

  test("Creating a stripe pattern") {
    val pattern = stripePattern(white, black)

    assertEquals(color.isEqual(pattern.a, white), true)
    assertEquals(color.isEqual(pattern.b, black), true)
  }

  test("A stripe pattern is constant in y") {
    val pattern = stripePattern(white, black)

    assertEquals(
      color.isEqual(stripeAt(pattern, tuple.makePoint(0, 0, 0)), white),
      true
    )
    assertEquals(
      color.isEqual(stripeAt(pattern, tuple.makePoint(0, 1, 0)), white),
      true
    )
    assertEquals(
      color.isEqual(stripeAt(pattern, tuple.makePoint(0, 2, 0)), white),
      true
    )
  }

  test("A stripe pattern is constant in z") {
    val pattern = stripePattern(white, black)

    assertEquals(
      color.isEqual(stripeAt(pattern, tuple.makePoint(0, 0, 0)), white),
      true
    )
    assertEquals(
      color.isEqual(stripeAt(pattern, tuple.makePoint(0, 0, 1)), white),
      true
    )
    assertEquals(
      color.isEqual(stripeAt(pattern, tuple.makePoint(0, 0, 2)), white),
      true
    )
  }

  test("A stripe pattern alternates in x") {
    val pattern = stripePattern(white, black)

    assertEquals(
      color.isEqual(stripeAt(pattern, tuple.makePoint(0, 0, 0)), white),
      true
    )
    assertEquals(
      color.isEqual(stripeAt(pattern, tuple.makePoint(0.9, 0, 0)), white),
      true
    )
    assertEquals(
      color.isEqual(stripeAt(pattern, tuple.makePoint(1, 0, 0)), black),
      true
    )
    assertEquals(
      color.isEqual(stripeAt(pattern, tuple.makePoint(-0.1, 0, 0)), black),
      true
    )
    assertEquals(
      color.isEqual(stripeAt(pattern, tuple.makePoint(-1, 0, 0)), black),
      true
    )
    assertEquals(
      color.isEqual(stripeAt(pattern, tuple.makePoint(-1.1, 0, 0)), white),
      true
    )
  }

  test("Stripes with an object transformation") {
    val obj = sphere.sphere()
    val objTransformed =
      sphere.setTransform(obj, transformation.scaling(2, 2, 2))
    val pattern = stripePattern(white, black)
    val c = stripeAtObject(pattern, objTransformed, tuple.makePoint(1.5, 0, 0))

    assertEquals(color.isEqual(c, white), true)
  }

  test("Stripes with a pattern transformation") {
    val obj = sphere.sphere()
    val pattern = stripePattern(white, black)
    val patternTransformed =
      setPatternTransform(pattern, transformation.scaling(2, 2, 2))
    val c = stripeAtObject(patternTransformed, obj, tuple.makePoint(1.5, 0, 0))

    assertEquals(color.isEqual(c, white), true)
  }

  test("Stripes with both an object and a pattern transformation") {
    val obj = sphere.sphere()
    val objTransformed =
      sphere.setTransform(obj, transformation.scaling(2, 2, 2))
    val pattern = stripePattern(white, black)
    val patternTransformed =
      setPatternTransform(pattern, transformation.translation(0.5, 0, 0))
    val c = stripeAtObject(
      patternTransformed,
      objTransformed,
      tuple.makePoint(2.5, 0, 0)
    )

    assertEquals(color.isEqual(c, white), true)
  }
}
