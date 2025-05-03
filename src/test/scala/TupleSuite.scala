import com.samuelcantrell.raytracer.equality
import com.samuelcantrell.raytracer.tuple._

// For more information on writing tests, see
// https://scalameta.org/munit/docs/getting-started.html
class TupleSuite extends munit.FunSuite {
  test("A tuple with w=1.0 is a point") {
    val obtained = Tuple(4.3, -4.2, 3.1, 1.0)

    assertEqualsDouble(obtained.x, 4.3, equality.EPSILON)
    assertEqualsDouble(obtained.y, -4.2, equality.EPSILON)
    assertEqualsDouble(obtained.z, 3.1, equality.EPSILON)
    assertEquals(isPoint(obtained), true)
    assertEquals(isVector(obtained), false)
  }

  test("A tuple with w=0.0 is a vector") {
    val obtained = Tuple(4.3, -4.2, 3.1, 0.0)

    assertEqualsDouble(obtained.x, 4.3, equality.EPSILON)
    assertEqualsDouble(obtained.y, -4.2, equality.EPSILON)
    assertEqualsDouble(obtained.z, 3.1, equality.EPSILON)
    assertEquals(isPoint(obtained), false)
    assertEquals(isVector(obtained), true)
  }

  test("makePoint() creates tuples with w=1.0") {
    val obtained = makePoint(4, -4, 3)
    assertEquals(obtained, Tuple(4, -4, 3, 1))
  }

  test("makeVector() creates tuples with w=0.0") {
    val obtained = makeVector(4, -4, 3)
    assertEquals(obtained, Tuple(4, -4, 3, 0))
  }

  test("isEqual() returns true for equal tuples") {
    val a = Tuple(3.3, 4.5, -1.2, 0.0)
    val b = Tuple(3.3, 4.5, -1.2, 0.0)

    assertEquals(isEqual(a, b), true)
  }

  test("isEqual() returns true for floating-point error tuples") {
    val a = Tuple(3.3, 4.5, -1.2, 0.0)
    val b = Tuple(3.3000000001, 4.500000000005, -1.200000003, 0.0)

    assertEquals(isEqual(a, b), true)
  }

  test("isEqual() returns false for nonequal tuples") {
    val a = Tuple(3.3, 4.5, -1.2, 0.0)
    val b = Tuple(5.9, 4.5, -1.2, 0.0)

    assertEquals(isEqual(a, b), false)
  }

  test("Adding two tuples") {
    val obtained = add(Tuple(3, -2, 5, 1), Tuple(-2, 3, 1, 0))
    val expected = Tuple(1, 1, 6, 1)
    assertEquals(isEqual(obtained, expected), true)
  }

  test("Subtracting two points") {
    val obtained = subtract(makePoint(3, 2, 1), makePoint(5, 6, 7))
    val expected = makeVector(-2, -4, -6)
    assertEquals(isEqual(obtained, expected), true)
  }

  test("Subtracting a vector from a point") {
    val obtained = subtract(makePoint(3, 2, 1), makeVector(5, 6, 7))
    val expected = makePoint(-2, -4, -6)
    assertEquals(isEqual(obtained, expected), true)
  }

  test("Subtracting two vectors") {
    val obtained = subtract(makeVector(3, 2, 1), makeVector(5, 6, 7))
    val expected = makeVector(-2, -4, -6)
    assertEquals(isEqual(obtained, expected), true)
  }

  test("Negating a tuple") {
    val obtained = negate(Tuple(1, -2, 3, -4))
    val expected = Tuple(-1, 2, -3, 4)
    assertEquals(isEqual(obtained, expected), true)
  }

  test("Multiplying a tuple by a scalar") {
    val obtained = multiply(Tuple(1, -2, 3, -4), 3.5)
    val expected = Tuple(3.5, -7, 10.5, -14)
    assertEquals(isEqual(obtained, expected), true)
  }

  test("Multiplying a tuple by a fraction") {
    val obtained = multiply(Tuple(1, -2, 3, -4), 0.5)
    val expected = Tuple(0.5, -1, 1.5, -2)
    assertEquals(isEqual(obtained, expected), true)
  }

  test("Dividing a tuple by a scalar") {
    val obtained = divide(Tuple(1, -2, 3, -4), 2)
    val expected = Tuple(0.5, -1, 1.5, -2)
    assertEquals(isEqual(obtained, expected), true)
  }

  test("Computing the magnitude of vector(1, 0, 0)") {
    val obtained = magnitude(makeVector(1, 0, 0))
    assertEquals(obtained, 1.0)
  }

  test("Computing the magnitude of vector(0, 1, 0)") {
    val obtained = magnitude(makeVector(0, 1, 0))
    assertEquals(obtained, 1.0)
  }

  test("Computing the magnitude of vector(0, 0, 1)") {
    val obtained = magnitude(makeVector(0, 0, 1))
    assertEquals(obtained, 1.0)
  }

  test("Computing the magnitude of vector(1, 2, 3)") {
    val obtained = magnitude(makeVector(1, 2, 3))
    assertEquals(obtained, math.sqrt(14))
  }

  test("Computing the magnitude of vector(-1, -2, -3)") {
    val obtained = magnitude(makeVector(-1, -2, -3))
    assertEquals(obtained, math.sqrt(14))
  }

  test("Normalizing vector(4, 0, 0) gives (1, 0, 0)") {
    val obtained = normalize(makeVector(4, 0, 0))
    val expected = makeVector(1, 0, 0)
    assertEquals(isEqual(obtained, expected), true)
  }

  test("Normalizing vector(1, 2, 3)") {
    val obtained = normalize(makeVector(1, 2, 3))
    val expected =
      makeVector(1 / math.sqrt(14), 2 / math.sqrt(14), 3 / math.sqrt(14))
    assertEquals(isEqual(obtained, expected), true)
  }

  test("The magnitude of a normalized vector") {
    val obtained = magnitude(normalize(makeVector(1, 2, 3)))
    assertEquals(obtained, 1.0)
  }

  test("The dot product of two tuples") {
    val obtained = dot(makeVector(1, 2, 3), makeVector(2, 3, 4))
    assertEquals(obtained, 20.0)
  }

  test("The cross product of two vectors - cross(a, b)") {
    val obtained = cross(makeVector(1, 2, 3), makeVector(2, 3, 4))
    val expected = makeVector(-1, 2, -1)
    assertEquals(isEqual(obtained, expected), true)
  }

  test("The cross product of two vectors - cross(b, a)") {
    val obtained = cross(makeVector(2, 3, 4), makeVector(1, 2, 3))
    val expected = makeVector(1, -2, 1)
    assertEquals(isEqual(obtained, expected), true)
  }
}
