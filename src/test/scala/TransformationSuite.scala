import com.samuelcantrell.raytracer.transformation._
import com.samuelcantrell.raytracer.tuple
import com.samuelcantrell.raytracer.matrix

class TransformationSuite extends munit.FunSuite {

  test("Multiplying by a translation matrix") {
    val transform = translation(5, -3, 2)
    val p = tuple.makePoint(-3, 4, 5)
    val expected = tuple.makePoint(2, 1, 7)

    val result = transform * p

    assertEquals(tuple.isEqual(result, expected), true)
  }

  test("Multiplying by the inverse of a translation matrix") {
    val transform = translation(5, -3, 2)
    val inv = transform.inverse
    val p = tuple.makePoint(-3, 4, 5)
    val expected = tuple.makePoint(-8, 7, 3)

    val result = inv * p

    assertEquals(tuple.isEqual(result, expected), true)
  }

  test("Translation does not affect vectors") {
    val transform = translation(5, -3, 2)
    val v = tuple.makeVector(-3, 4, 5)

    val result = transform * v

    assertEquals(tuple.isEqual(result, v), true)
  }

  test("A scaling matrix applied to a point") {
    val transform = scaling(2, 3, 4)
    val p = tuple.makePoint(-4, 6, 8)
    val expected = tuple.makePoint(-8, 18, 32)

    val result = transform * p

    assertEquals(tuple.isEqual(result, expected), true)
  }

  test("A scaling matrix applied to a vector") {
    val transform = scaling(2, 3, 4)
    val v = tuple.makeVector(-4, 6, 8)
    val expected = tuple.makeVector(-8, 18, 32)

    val result = transform * v

    assertEquals(tuple.isEqual(result, expected), true)
  }

  test("Multiplying by the inverse of a scaling matrix") {
    val transform = scaling(2, 3, 4)
    val inv = transform.inverse
    val v = tuple.makeVector(-4, 6, 8)
    val expected = tuple.makeVector(-2, 2, 2)

    val result = inv * v

    assertEquals(tuple.isEqual(result, expected), true)
  }

  test("Reflection is scaling by a negative value") {
    val transform = scaling(-1, 1, 1)
    val p = tuple.makePoint(2, 3, 4)
    val expected = tuple.makePoint(-2, 3, 4)

    val result = transform * p

    assertEquals(tuple.isEqual(result, expected), true)
  }

  test("Rotating a point around the x axis") {
    val p = tuple.makePoint(0, 1, 0)
    val half_quarter = rotation_x(math.Pi / 4)
    val full_quarter = rotation_x(math.Pi / 2)

    val expected_half = tuple.makePoint(0, math.sqrt(2) / 2, math.sqrt(2) / 2)
    val expected_full = tuple.makePoint(0, 0, 1)

    val result_half = half_quarter * p
    val result_full = full_quarter * p

    assertEquals(tuple.isEqual(result_half, expected_half), true)
    assertEquals(tuple.isEqual(result_full, expected_full), true)
  }

  test("The inverse of an x-rotation rotates in the opposite direction") {
    val p = tuple.makePoint(0, 1, 0)
    val half_quarter = rotation_x(math.Pi / 4)
    val inv = half_quarter.inverse
    val expected = tuple.makePoint(0, math.sqrt(2) / 2, -math.sqrt(2) / 2)

    val result = inv * p

    assertEquals(tuple.isEqual(result, expected), true)
  }

  test("Rotating a point around the y axis") {
    val p = tuple.makePoint(0, 0, 1)
    val half_quarter = rotation_y(math.Pi / 4)
    val full_quarter = rotation_y(math.Pi / 2)

    val expected_half = tuple.makePoint(math.sqrt(2) / 2, 0, math.sqrt(2) / 2)
    val expected_full = tuple.makePoint(1, 0, 0)

    val result_half = half_quarter * p
    val result_full = full_quarter * p

    assertEquals(tuple.isEqual(result_half, expected_half), true)
    assertEquals(tuple.isEqual(result_full, expected_full), true)
  }

  test("Rotating a point around the z axis") {
    val p = tuple.makePoint(0, 1, 0)
    val half_quarter = rotation_z(math.Pi / 4)
    val full_quarter = rotation_z(math.Pi / 2)

    val expected_half = tuple.makePoint(-math.sqrt(2) / 2, math.sqrt(2) / 2, 0)
    val expected_full = tuple.makePoint(-1, 0, 0)

    val result_half = half_quarter * p
    val result_full = full_quarter * p

    assertEquals(tuple.isEqual(result_half, expected_half), true)
    assertEquals(tuple.isEqual(result_full, expected_full), true)
  }

  test("A shearing transformation moves x in proportion to y") {
    val transform = shearing(1, 0, 0, 0, 0, 0)
    val p = tuple.makePoint(2, 3, 4)
    val expected = tuple.makePoint(5, 3, 4)

    val result = transform * p

    assertEquals(tuple.isEqual(result, expected), true)
  }

  test("A shearing transformation moves x in proportion to z") {
    val transform = shearing(0, 1, 0, 0, 0, 0)
    val p = tuple.makePoint(2, 3, 4)
    val expected = tuple.makePoint(6, 3, 4)

    val result = transform * p

    assertEquals(tuple.isEqual(result, expected), true)
  }

  test("A shearing transformation moves y in proportion to x") {
    val transform = shearing(0, 0, 1, 0, 0, 0)
    val p = tuple.makePoint(2, 3, 4)
    val expected = tuple.makePoint(2, 5, 4)

    val result = transform * p

    assertEquals(tuple.isEqual(result, expected), true)
  }

  test("A shearing transformation moves y in proportion to z") {
    val transform = shearing(0, 0, 0, 1, 0, 0)
    val p = tuple.makePoint(2, 3, 4)
    val expected = tuple.makePoint(2, 7, 4)

    val result = transform * p

    assertEquals(tuple.isEqual(result, expected), true)
  }

  test("A shearing transformation moves z in proportion to x") {
    val transform = shearing(0, 0, 0, 0, 1, 0)
    val p = tuple.makePoint(2, 3, 4)
    val expected = tuple.makePoint(2, 3, 6)

    val result = transform * p

    assertEquals(tuple.isEqual(result, expected), true)
  }

  test("A shearing transformation moves z in proportion to y") {
    val transform = shearing(0, 0, 0, 0, 0, 1)
    val p = tuple.makePoint(2, 3, 4)
    val expected = tuple.makePoint(2, 3, 7)

    val result = transform * p

    assertEquals(tuple.isEqual(result, expected), true)
  }

  test("Individual transformations are applied in sequence") {
    val p = tuple.makePoint(1, 0, 1)
    val A = rotation_x(math.Pi / 2)
    val B = scaling(5, 5, 5)
    val C = translation(10, 5, 7)

    // apply rotation first
    val p2 = A * p
    val expected_p2 = tuple.makePoint(1, -1, 0)
    assertEquals(tuple.isEqual(p2, expected_p2), true)

    // then apply scaling
    val p3 = B * p2
    val expected_p3 = tuple.makePoint(5, -5, 0)
    assertEquals(tuple.isEqual(p3, expected_p3), true)

    // then apply translation
    val p4 = C * p3
    val expected_p4 = tuple.makePoint(15, 0, 7)
    assertEquals(tuple.isEqual(p4, expected_p4), true)
  }

  test("Chained transformations must be applied in reverse order") {
    val p = tuple.makePoint(1, 0, 1)
    val A = rotation_x(math.Pi / 2)
    val B = scaling(5, 5, 5)
    val C = translation(10, 5, 7)

    val T = C * B * A
    val result = T * p
    val expected = tuple.makePoint(15, 0, 7)

    assertEquals(tuple.isEqual(result, expected), true)
  }

  test("Fluent interface for chaining transformations") {
    val p = tuple.makePoint(1, 0, 1)

    val transform = identity()
      .rotate_x(math.Pi / 2)
      .scale(5, 5, 5)
      .translate(10, 5, 7)

    val result = transform * p
    val expected = tuple.makePoint(15, 0, 7)

    assertEquals(tuple.isEqual(result, expected), true)
  }

  test("Fluent interface produces same result as manual chaining") {
    val p = tuple.makePoint(2, 1, 0)

    // Manual chaining
    val manual =
      translation(1, 0, 0) * scaling(2, 2, 2) * rotation_z(math.Pi / 4)

    // Fluent interface
    val fluent = identity()
      .rotate_z(math.Pi / 4)
      .scale(2, 2, 2)
      .translate(1, 0, 0)

    val manual_result = manual * p
    val fluent_result = fluent * p

    assertEquals(tuple.isEqual(manual_result, fluent_result), true)
  }

  test("The transformation matrix for the default orientation") {
    val from = tuple.makePoint(0, 0, 0)
    val to = tuple.makePoint(0, 0, -1)
    val up = tuple.makeVector(0, 1, 0)
    val t = viewTransform(from, to, up)
    val expected = matrix.Matrix.identity()

    assertEquals(matrix.isEqual(t, expected), true)
  }

  test("A view transformation matrix looking in positive z direction") {
    val from = tuple.makePoint(0, 0, 0)
    val to = tuple.makePoint(0, 0, 1)
    val up = tuple.makeVector(0, 1, 0)
    val t = viewTransform(from, to, up)
    val expected = scaling(-1, 1, -1)

    assertEquals(matrix.isEqual(t, expected), true)
  }

  test("The view transformation moves the world") {
    val from = tuple.makePoint(0, 0, 8)
    val to = tuple.makePoint(0, 0, 0)
    val up = tuple.makeVector(0, 1, 0)
    val t = viewTransform(from, to, up)
    val expected = translation(0, 0, -8)

    assertEquals(matrix.isEqual(t, expected), true)
  }

  test("An arbitrary view transformation") {
    val from = tuple.makePoint(1, 3, 2)
    val to = tuple.makePoint(4, -2, 8)
    val up = tuple.makeVector(1, 1, 0)
    val t = viewTransform(from, to, up)
    val expected = matrix.Matrix(4, -0.50709, 0.50709, 0.67612, -2.36643,
      0.76772, 0.60609, 0.12122, -2.82843, -0.35857, 0.59761, -0.71714, 0.00000,
      0.00000, 0.00000, 0.00000, 1.00000)

    val precision = 0.00001
    assertEquals(matrix.isEqual(t, expected, precision), true)
  }
}
