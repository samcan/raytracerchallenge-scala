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

  test("A ring should extend in both x and z") {
    val pattern = ringPattern(white, black)

    // At origin (0,0,0), distance = 0, should be white (even ring index)
    assertEquals(
      color.isEqual(pattern.patternAt(tuple.makePoint(0, 0, 0)), white),
      true
    )

    // At (1,0,0), distance = 1, should be black (odd ring index)
    assertEquals(
      color.isEqual(pattern.patternAt(tuple.makePoint(1, 0, 0)), black),
      true
    )

    // At (0,0,1), distance = 1, should be black (odd ring index)
    assertEquals(
      color.isEqual(pattern.patternAt(tuple.makePoint(0, 0, 1)), black),
      true
    )

    // At (0.708, 0, 0.708), distance ≈ 1.001, should be black (odd ring index)
    // 0.708 is just slightly more than √2/2 ≈ 0.707
    assertEquals(
      color.isEqual(pattern.patternAt(tuple.makePoint(0.708, 0, 0.708)), black),
      true
    )
  }

  test("Checkers should repeat in x") {
    val pattern = checkersPattern(white, black)

    // At (0,0,0), sum = 0+0+0 = 0 (even), should be white
    assertEquals(
      color.isEqual(pattern.patternAt(tuple.makePoint(0, 0, 0)), white),
      true
    )

    // At (0.99,0,0), floor(0.99) = 0, sum = 0+0+0 = 0 (even), should be white
    assertEquals(
      color.isEqual(pattern.patternAt(tuple.makePoint(0.99, 0, 0)), white),
      true
    )

    // At (1.01,0,0), floor(1.01) = 1, sum = 1+0+0 = 1 (odd), should be black
    assertEquals(
      color.isEqual(pattern.patternAt(tuple.makePoint(1.01, 0, 0)), black),
      true
    )
  }

  test("Checkers should repeat in y") {
    val pattern = checkersPattern(white, black)

    // At (0,0,0), sum = 0+0+0 = 0 (even), should be white
    assertEquals(
      color.isEqual(pattern.patternAt(tuple.makePoint(0, 0, 0)), white),
      true
    )

    // At (0,0.99,0), floor(0.99) = 0, sum = 0+0+0 = 0 (even), should be white
    assertEquals(
      color.isEqual(pattern.patternAt(tuple.makePoint(0, 0.99, 0)), white),
      true
    )

    // At (0,1.01,0), floor(1.01) = 1, sum = 0+1+0 = 1 (odd), should be black
    assertEquals(
      color.isEqual(pattern.patternAt(tuple.makePoint(0, 1.01, 0)), black),
      true
    )
  }

  test("Checkers should repeat in z") {
    val pattern = checkersPattern(white, black)

    // At (0,0,0), sum = 0+0+0 = 0 (even), should be white
    assertEquals(
      color.isEqual(pattern.patternAt(tuple.makePoint(0, 0, 0)), white),
      true
    )

    // At (0,0,0.99), floor(0.99) = 0, sum = 0+0+0 = 0 (even), should be white
    assertEquals(
      color.isEqual(pattern.patternAt(tuple.makePoint(0, 0, 0.99)), white),
      true
    )

    // At (0,0,1.01), floor(1.01) = 1, sum = 0+0+1 = 1 (odd), should be black
    assertEquals(
      color.isEqual(pattern.patternAt(tuple.makePoint(0, 0, 1.01)), black),
      true
    )
  }
}
