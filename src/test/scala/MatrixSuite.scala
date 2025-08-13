import com.samuelcantrell.raytracer.matrix._
import com.samuelcantrell.raytracer.equality
import com.samuelcantrell.raytracer.tuple

class MatrixSuite extends munit.FunSuite {

  test("Constructing and inspecting a 4x4 matrix") {
    val m = Matrix(4, 1, 2, 3, 4, 5.5, 6.5, 7.5, 8.5, 9, 10, 11, 12, 13.5, 14.5,
      15.5, 16.5)

    assertEquals(m(0, 0), 1.0)
    assertEquals(m(0, 3), 4.0)
    assertEquals(m(1, 0), 5.5)
    assertEquals(m(1, 2), 7.5)
    assertEquals(m(2, 2), 11.0)
    assertEquals(m(3, 0), 13.5)
    assertEquals(m(3, 2), 15.5)
  }

  test("A 2x2 matrix ought to be representable") {
    val m = Matrix(2, -3, 5, 1, -2)

    assertEquals(m(0, 0), -3.0)
    assertEquals(m(0, 1), 5.0)
    assertEquals(m(1, 0), 1.0)
    assertEquals(m(1, 1), -2.0)
  }

  test("A 3x3 matrix ought to be representable") {
    val m = Matrix(3, -3, 5, 0, 1, -2, -7, 0, 1, 1)

    assertEquals(m(0, 0), -3.0)
    assertEquals(m(1, 1), -2.0)
    assertEquals(m(2, 2), 1.0)
  }

  test("Matrix equality with identical matrices") {
    val a = Matrix(4, 1, 2, 3, 4, 5, 6, 7, 8, 9, 8, 7, 6, 5, 4, 3, 2)

    val b = Matrix(4, 1, 2, 3, 4, 5, 6, 7, 8, 9, 8, 7, 6, 5, 4, 3, 2)

    assertEquals(isEqual(a, b), true)
  }

  test("Matrix equality with different matrices") {
    val a = Matrix(4, 1, 2, 3, 4, 5, 6, 7, 8, 9, 8, 7, 6, 5, 4, 3, 2)

    val b = Matrix(4, 2, 3, 4, 5, 6, 7, 8, 9, 8, 7, 6, 5, 4, 3, 2, 1)

    assertEquals(isEqual(a, b), false)
  }

  test("Matrix elements are almost equal with floating point precision") {
    val m = Matrix(3, 1.000000001, 2.000000002, 3.000000003, 4.000000004,
      5.000000005, 6.000000006, 7.000000007, 8.000000008, 9.000000009)

    assertEquals(equality.almostEqual(m(0, 0), 1.0), true)
    assertEquals(equality.almostEqual(m(0, 1), 2.0), true)
    assertEquals(equality.almostEqual(m(0, 2), 3.0), true)
    assertEquals(equality.almostEqual(m(1, 0), 4.0), true)
    assertEquals(equality.almostEqual(m(1, 1), 5.0), true)
    assertEquals(equality.almostEqual(m(1, 2), 6.0), true)
    assertEquals(equality.almostEqual(m(2, 0), 7.0), true)
    assertEquals(equality.almostEqual(m(2, 1), 8.0), true)
    assertEquals(equality.almostEqual(m(2, 2), 9.0), true)
  }

  test("Matrix equality with different sizes") {
    val a = Matrix(2, 1, 2, 3, 4)
    val b = Matrix(3, 1, 2, 0, 3, 4, 0, 0, 0, 0)

    assertEquals(isEqual(a, b), false)
  }

  test("Accessing matrix rows") {
    val m = Matrix(3, 1, 2, 3, 4, 5, 6, 7, 8, 9)

    val row0 = m.row(0)
    val row1 = m.row(1)
    val row2 = m.row(2)

    assertEquals(row0.toSeq, Seq(1.0, 2.0, 3.0))
    assertEquals(row1.toSeq, Seq(4.0, 5.0, 6.0))
    assertEquals(row2.toSeq, Seq(7.0, 8.0, 9.0))
  }

  test("Accessing matrix columns") {
    val m = Matrix(3, 1, 2, 3, 4, 5, 6, 7, 8, 9)

    val col0 = m.col(0)
    val col1 = m.col(1)
    val col2 = m.col(2)

    assertEquals(col0.toSeq, Seq(1.0, 4.0, 7.0))
    assertEquals(col1.toSeq, Seq(2.0, 5.0, 8.0))
    assertEquals(col2.toSeq, Seq(3.0, 6.0, 9.0))
  }

  test("Matrix bounds checking for element access") {
    val m = Matrix(2, 1, 2, 3, 4)

    intercept[IndexOutOfBoundsException] {
      m(2, 0) // row out of bounds
    }

    intercept[IndexOutOfBoundsException] {
      m(0, 2) // column out of bounds
    }

    intercept[IndexOutOfBoundsException] {
      m(-1, 0) // negative row
    }

    intercept[IndexOutOfBoundsException] {
      m(0, -1) // negative column
    }
  }

  test("Matrix bounds checking for row access") {
    val m = Matrix(2, 1, 2, 3, 4)

    intercept[IndexOutOfBoundsException] {
      m.row(2) // row out of bounds
    }

    intercept[IndexOutOfBoundsException] {
      m.row(-1) // negative row
    }
  }

  test("Matrix bounds checking for column access") {
    val m = Matrix(2, 1, 2, 3, 4)

    intercept[IndexOutOfBoundsException] {
      m.col(2) // column out of bounds
    }

    intercept[IndexOutOfBoundsException] {
      m.col(-1) // negative column
    }
  }

  test("Matrix constructor validates argument count") {
    intercept[IllegalArgumentException] {
      Matrix(2, 1, 2, 3) // Too few values for 2x2 matrix
    }

    intercept[IllegalArgumentException] {
      Matrix(2, 1, 2, 3, 4, 5) // Too many values for 2x2 matrix
    }
  }

  test("Matrix constructor works with exact argument count") {
    val m2x2 = Matrix(2, 1, 2, 3, 4)
    assertEquals(m2x2(0, 0), 1.0)
    assertEquals(m2x2(1, 1), 4.0)

    val m3x3 = Matrix(3, 1, 2, 3, 4, 5, 6, 7, 8, 9)
    assertEquals(m3x3(0, 0), 1.0)
    assertEquals(m3x3(2, 2), 9.0)
  }

  test("Multiplying two matrices") {
    val a = Matrix(4, 1, 2, 3, 4, 5, 6, 7, 8, 9, 8, 7, 6, 5, 4, 3, 2)

    val b = Matrix(4, -2, 1, 2, 3, 3, 2, 1, -1, 4, 3, 6, 5, 1, 2, 7, 8)

    val expected = Matrix(4, 20, 22, 50, 48, 44, 54, 114, 108, 40, 58, 110, 102,
      16, 26, 46, 42)

    val result = a * b

    assertEquals(isEqual(result, expected), true)
  }

  test("Matrix multiplication using multiply function") {
    val a = Matrix(2, 1, 2, 3, 4)

    val b = Matrix(2, 2, 0, 1, 2)

    val expected = Matrix(2, 4, 4, 10, 8)

    val result = multiply(a, b)

    assertEquals(isEqual(result, expected), true)
  }

  test("Matrix multiplication size validation") {
    val a = Matrix(2, 1, 2, 3, 4)
    val b = Matrix(3, 1, 2, 3, 4, 5, 6, 7, 8, 9)

    intercept[IllegalArgumentException] {
      a * b
    }

    intercept[IllegalArgumentException] {
      multiply(a, b)
    }
  }

  test("A matrix multiplied by a tuple") {
    val a = Matrix(4, 1, 2, 3, 4, 2, 4, 4, 2, 8, 6, 4, 1, 0, 0, 0, 1)

    val b = tuple.Tuple(1, 2, 3, 1)
    val expected = tuple.Tuple(18, 24, 33, 1)

    val result = a * b

    assertEquals(tuple.isEqual(result, expected), true)
  }

  test("Matrix tuple multiplication using multiplyTuple function") {
    val m = Matrix(4, 1, 0, 0, 5, 0, 1, 0, 3, 0, 0, 1, 2, 0, 0, 0, 1)

    val t = tuple.Tuple(2, 1, 0, 1)
    val expected = tuple.Tuple(7, 4, 2, 1)

    val result = multiplyTuple(m, t)

    assertEquals(tuple.isEqual(result, expected), true)
  }

  test("Matrix tuple multiplication size validation") {
    val m = Matrix(2, 1, 2, 3, 4)
    val t = tuple.Tuple(1, 2, 3, 1)

    intercept[IllegalArgumentException] {
      m * t
    }

    intercept[IllegalArgumentException] {
      multiplyTuple(m, t)
    }
  }

  test("Creating an identity matrix") {
    val identity = Matrix.identity()

    assertEquals(identity(0, 0), 1.0)
    assertEquals(identity(0, 1), 0.0)
    assertEquals(identity(0, 2), 0.0)
    assertEquals(identity(0, 3), 0.0)
    assertEquals(identity(1, 0), 0.0)
    assertEquals(identity(1, 1), 1.0)
    assertEquals(identity(1, 2), 0.0)
    assertEquals(identity(1, 3), 0.0)
    assertEquals(identity(2, 0), 0.0)
    assertEquals(identity(2, 1), 0.0)
    assertEquals(identity(2, 2), 1.0)
    assertEquals(identity(2, 3), 0.0)
    assertEquals(identity(3, 0), 0.0)
    assertEquals(identity(3, 1), 0.0)
    assertEquals(identity(3, 2), 0.0)
    assertEquals(identity(3, 3), 1.0)
  }

  test("Multiplying a matrix by the identity matrix") {
    val a = Matrix(4, 0, 1, 2, 4, 1, 2, 4, 8, 2, 4, 8, 16, 4, 8, 16, 32)

    val identity = Matrix.identity()
    val result = a * identity

    assertEquals(isEqual(result, a), true)
  }

  test("Multiplying the identity matrix by a tuple") {
    val a = tuple.Tuple(1, 2, 3, 4)
    val identity = Matrix.identity()
    val result = identity * a

    assertEquals(tuple.isEqual(result, a), true)
  }

  test("Transposing a matrix") {
    val a = Matrix(4, 0, 9, 3, 0, 9, 8, 0, 8, 1, 8, 5, 3, 0, 0, 5, 8)

    val expected = Matrix(4, 0, 9, 1, 0, 9, 8, 8, 0, 3, 0, 5, 5, 0, 8, 3, 8)

    val result = a.transpose

    assertEquals(isEqual(result, expected), true)
  }

  test("Transposing a matrix using transpose function") {
    val a = Matrix(3, 1, 2, 3, 4, 5, 6, 7, 8, 9)

    val expected = Matrix(3, 1, 4, 7, 2, 5, 8, 3, 6, 9)

    val result = transpose(a)

    assertEquals(isEqual(result, expected), true)
  }

  test("Transposing the identity matrix") {
    val identity = Matrix.identity()
    val transposed = identity.transpose

    assertEquals(isEqual(transposed, identity), true)
  }

  test("Calculating the determinant of a 2x2 matrix") {
    val a = Matrix(2, 1, 5, -3, 2)

    assertEquals(a.determinant, 17.0)
  }

  test("A submatrix of a 3x3 matrix is a 2x2 matrix") {
    val a = Matrix(3, 1, 5, 0, -3, 2, 7, 0, 6, -3)

    val expected = Matrix(2, -3, 2, 0, 6)

    val result = a.submatrix(0, 2)

    assertEquals(isEqual(result, expected), true)
  }

  test("A submatrix of a 4x4 matrix is a 3x3 matrix") {
    val a = Matrix(4, -6, 1, 1, 6, -8, 5, 8, 6, -1, 0, 8, 2, -7, 1, -1, 1)

    val expected = Matrix(3, -6, 1, 6, -8, 8, 6, -7, -1, 1)

    val result = a.submatrix(2, 1)

    assertEquals(isEqual(result, expected), true)
  }

  test("Calculating a minor of a 3x3 matrix") {
    val a = Matrix(3, 3, 5, 0, 2, -1, -7, 6, -1, 5)

    val b = a.submatrix(1, 0)
    assertEquals(b.determinant, 25.0)
    assertEquals(a.minor(1, 0), 25.0)
  }

  test("Calculating a cofactor of a 3x3 matrix") {
    val a = Matrix(3, 3, 5, 0, 2, -1, -7, 6, -1, 5)

    assertEquals(a.minor(0, 0), -12.0)
    assertEquals(a.cofactor(0, 0), -12.0)
    assertEquals(a.minor(1, 0), 25.0)
    assertEquals(a.cofactor(1, 0), -25.0)
  }

  test("Calculating the determinant of a 3x3 matrix") {
    val a = Matrix(3, 1, 2, 6, -5, 8, -4, 2, 6, 4)

    assertEquals(a.cofactor(0, 0), 56.0)
    assertEquals(a.cofactor(0, 1), 12.0)
    assertEquals(a.cofactor(0, 2), -46.0)
    assertEquals(a.determinant, -196.0)
  }

  test("Calculating the determinant of a 4x4 matrix") {
    val a = Matrix(4, -2, -8, 3, 5, -3, 1, 7, 3, 1, 2, -9, 6, -6, 7, 7, -9)

    assertEquals(a.cofactor(0, 0), 690.0)
    assertEquals(a.cofactor(0, 1), 447.0)
    assertEquals(a.cofactor(0, 2), 210.0)
    assertEquals(a.cofactor(0, 3), 51.0)
    assertEquals(a.determinant, -4071.0)
  }

  test("Testing an invertible matrix for invertibility") {
    val a = Matrix(4, 6, 4, 4, 4, 5, 5, 7, 6, 4, -9, 3, -7, 9, 1, 7, -6)

    assertEquals(a.determinant, -2120.0)
    assertEquals(a.isInvertible, true)
  }

  test("Testing a noninvertible matrix for invertibility") {
    val a = Matrix(4, -4, 2, -2, -3, 9, 6, 2, 6, 0, -5, 1, -5, 0, 0, 0, 0)

    assertEquals(a.determinant, 0.0)
    assertEquals(a.isInvertible, false)
  }

  test("Calculating the inverse of a matrix") {
    val a = Matrix(4, -5, 2, 6, -8, 1, -5, 1, 8, 7, 7, -6, -7, 1, -3, 7, 4)

    val b = a.inverse

    assertEquals(a.determinant, 532.0)
    assertEquals(a.cofactor(2, 3), -160.0)
    assertEquals(equality.almostEqual(b(3, 2), -160.0 / 532.0), true)
    assertEquals(a.cofactor(3, 2), 105.0)
    assertEquals(equality.almostEqual(b(2, 3), 105.0 / 532.0), true)

    // Test a few key elements with sufficient precision for EPSILON
    assertEquals(equality.almostEqual(b(0, 0), 116.0 / 532.0), true)
    assertEquals(equality.almostEqual(b(0, 1), 240.0 / 532.0), true)
    assertEquals(equality.almostEqual(b(1, 0), -430.0 / 532.0), true)
    assertEquals(equality.almostEqual(b(1, 1), -775.0 / 532.0), true)
  }

  test("Calculating the inverse of another matrix") {
    val a = Matrix(4, 8, -5, 9, 2, 7, 5, 6, 1, -6, 0, 9, 6, -3, 0, -9, -4)

    val result = a.inverse

    // Test that A * A^-1 = I instead of comparing exact values
    val identity = Matrix.identity()
    val product = a * result

    assertEquals(isEqual(product, identity), true)
  }

  test("Calculating the inverse of a third matrix") {
    val a = Matrix(4, 9, 3, 0, 9, -5, -2, -6, -3, -4, 9, 6, 4, -7, 6, 6, 2)

    val result = a.inverse

    // Test that A * A^-1 = I instead of comparing exact values
    val identity = Matrix.identity()
    val product = a * result

    assertEquals(isEqual(product, identity), true)
  }

  test("Multiplying a matrix by its inverse") {
    val a = Matrix(4, 3, -9, 7, 3, 3, -8, 2, -9, -4, 4, 4, 1, -6, 5, -1, 1)

    val b = Matrix(4, 8, 2, 2, 2, 3, -1, 7, 0, 7, 0, 5, 4, 6, -2, 0, 5)

    val c = a * b
    val result = c * b.inverse

    assertEquals(isEqual(result, a), true)
  }
}
