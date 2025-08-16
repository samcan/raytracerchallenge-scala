import com.samuelcantrell.raytracer.pattern._
import com.samuelcantrell.raytracer.color
import com.samuelcantrell.raytracer.tuple
import com.samuelcantrell.raytracer.sphere
import com.samuelcantrell.raytracer.transformation
import com.samuelcantrell.raytracer.matrix

class PatternSuite extends munit.FunSuite {

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

  test("The default pattern transformation") {
    val pattern = testPattern()
    val identity = matrix.Matrix.identity()

    assertEquals(matrix.isEqual(pattern.transform, identity), true)
  }

  test("Assigning a transformation") {
    val pattern = testPattern()
    val transformedPattern =
      setPatternTransform(pattern, transformation.translation(1, 2, 3))
    val expected = transformation.translation(1, 2, 3)

    assertEquals(matrix.isEqual(transformedPattern.transform, expected), true)
  }

  test("A pattern with an object transformation") {
    val shape = sphere.sphere()
    val shapeTransformed =
      sphere.setTransform(shape, transformation.scaling(2, 2, 2))
    val pattern = testPattern()
    val c = patternAtShape(pattern, shapeTransformed, tuple.makePoint(2, 3, 4))
    val expected = color.Color(1, 1.5, 2)

    assertEquals(color.isEqual(c, expected), true)
  }

  test("A pattern with a pattern transformation") {
    val shape = sphere.sphere()
    val pattern = testPattern()
    val patternTransformed =
      setPatternTransform(pattern, transformation.scaling(2, 2, 2))
    val c = patternAtShape(patternTransformed, shape, tuple.makePoint(2, 3, 4))
    val expected = color.Color(1, 1.5, 2)

    assertEquals(color.isEqual(c, expected), true)
  }

  test("A pattern with both an object and a pattern transformation") {
    val shape = sphere.sphere()
    val shapeTransformed =
      sphere.setTransform(shape, transformation.scaling(2, 2, 2))
    val pattern = testPattern()
    val patternTransformed =
      setPatternTransform(pattern, transformation.translation(0.5, 1, 1.5))
    val c = patternAtShape(
      patternTransformed,
      shapeTransformed,
      tuple.makePoint(2.5, 3, 3.5)
    )
    val expected = color.Color(0.75, 0.5, 0.25)

    assertEquals(color.isEqual(c, expected), true)
  }

  test("A gradient linearly interpolates between colors") {
    val pattern = gradientPattern(white, black)

    // At x=0, should be white
    assertEquals(
      color.isEqual(pattern.patternAt(tuple.makePoint(0, 0, 0)), white),
      true
    )

    // At x=0.25, should be color(0.75, 0.75, 0.75)
    assertEquals(
      color.isEqual(
        pattern.patternAt(tuple.makePoint(0.25, 0, 0)),
        color.Color(0.75, 0.75, 0.75)
      ),
      true
    )

    // At x=0.5, should be color(0.5, 0.5, 0.5)
    assertEquals(
      color.isEqual(
        pattern.patternAt(tuple.makePoint(0.5, 0, 0)),
        color.Color(0.5, 0.5, 0.5)
      ),
      true
    )

    // At x=0.75, should be color(0.25, 0.25, 0.25)
    assertEquals(
      color.isEqual(
        pattern.patternAt(tuple.makePoint(0.75, 0, 0)),
        color.Color(0.25, 0.25, 0.25)
      ),
      true
    )
  }
}
