package com.samuelcantrell.raytracer.matrix

import com.samuelcantrell.raytracer.equality
import com.samuelcantrell.raytracer.tuple

case class Matrix(data: Array[Array[Double]], size: Int):

  def apply(row: Int, col: Int): Double = {
    if (row < 0 || row >= size || col < 0 || col >= size) {
      throw new IndexOutOfBoundsException(
        s"Index ($row, $col) out of bounds for ${size}x${size} matrix"
      )
    }
    data(row)(col)
  }

  def row(index: Int): Array[Double] = {
    if (index < 0 || index >= size) {
      throw new IndexOutOfBoundsException(
        s"Row index $index out of bounds for ${size}x${size} matrix"
      )
    }
    data(index).clone()
  }

  def col(index: Int): Array[Double] = {
    if (index < 0 || index >= size) {
      throw new IndexOutOfBoundsException(
        s"Column index $index out of bounds for ${size}x${size} matrix"
      )
    }
    (0 until size).map(row => data(row)(index)).toArray
  }

  def *(other: Matrix): Matrix = {
    if (this.size != other.size) {
      throw new IllegalArgumentException(
        s"Cannot multiply ${this.size}x${this.size} matrix with ${other.size}x${other.size} matrix"
      )
    }
    multiply(this, other)
  }

  def *(t: tuple.Tuple): tuple.Tuple = {
    if (this.size != 4) {
      throw new IllegalArgumentException(
        s"Can only multiply 4x4 matrix with tuple, got ${this.size}x${this.size} matrix"
      )
    }
    multiplyTuple(this, t)
  }

  def transpose: Matrix = {
    val resultData = Array.ofDim[Double](size, size)
    for {
      row <- 0 until size
      col <- 0 until size
    } {
      resultData(row)(col) = this(col, row)
    }
    Matrix(resultData, size)
  }

  def determinant: Double = {
    if (size == 2) {
      this(0, 0) * this(1, 1) - this(0, 1) * this(1, 0)
    } else {
      (0 until size).map { col =>
        this(0, col) * cofactor(0, col)
      }.sum
    }
  }

  def submatrix(row: Int, col: Int): Matrix = {
    val newSize = size - 1
    val resultData = Array.ofDim[Double](newSize, newSize)

    var resultRow = 0
    for (sourceRow <- 0 until size if sourceRow != row) {
      var resultCol = 0
      for (sourceCol <- 0 until size if sourceCol != col) {
        resultData(resultRow)(resultCol) = this(sourceRow, sourceCol)
        resultCol += 1
      }
      resultRow += 1
    }

    Matrix(resultData, newSize)
  }

  def minor(row: Int, col: Int): Double = {
    submatrix(row, col).determinant
  }

  def cofactor(row: Int, col: Int): Double = {
    val minorValue = minor(row, col)
    if ((row + col) % 2 == 1) -minorValue else minorValue
  }

  def isInvertible: Boolean = {
    determinant != 0.0
  }

  def inverse: Matrix = {
    val det = determinant
    if (det == 0.0) {
      throw new IllegalArgumentException(
        "Matrix is not invertible (determinant is 0)"
      )
    }

    val resultData = Array.ofDim[Double](size, size)
    for {
      row <- 0 until size
      col <- 0 until size
    } {
      val c = cofactor(row, col)
      // Note: transpose by using (col, row) instead of (row, col)
      resultData(col)(row) = c / det
    }

    Matrix(resultData, size)
  }

object Matrix:
  def apply(size: Int, values: Double*): Matrix = {
    if (values.length != size * size) {
      throw new IllegalArgumentException(
        s"Expected ${size * size} values for ${size}x${size} matrix, got ${values.length}"
      )
    }

    val data = Array.ofDim[Double](size, size)
    for {
      row <- 0 until size
      col <- 0 until size
    } {
      data(row)(col) = values(row * size + col)
    }

    Matrix(data, size)
  }

  def identity(): Matrix = {
    val data = Array.ofDim[Double](4, 4)
    for {
      row <- 0 until 4
      col <- 0 until 4
    } {
      data(row)(col) = if (row == col) 1.0 else 0.0
    }
    Matrix(data, 4)
  }

def multiply(a: Matrix, b: Matrix): Matrix = {
  if (a.size != b.size) {
    throw new IllegalArgumentException(
      s"Cannot multiply ${a.size}x${a.size} matrix with ${b.size}x${b.size} matrix"
    )
  }

  val size = a.size
  val resultData = Array.ofDim[Double](size, size)

  for {
    row <- 0 until size
    col <- 0 until size
  } {
    resultData(row)(col) = (0 until size).map(k => a(row, k) * b(k, col)).sum
  }

  Matrix(resultData, size)
}

def multiplyTuple(m: Matrix, t: tuple.Tuple): tuple.Tuple = {
  if (m.size != 4) {
    throw new IllegalArgumentException(
      s"Can only multiply 4x4 matrix with tuple, got ${m.size}x${m.size} matrix"
    )
  }

  val x = m(0, 0) * t.x + m(0, 1) * t.y + m(0, 2) * t.z + m(0, 3) * t.w
  val y = m(1, 0) * t.x + m(1, 1) * t.y + m(1, 2) * t.z + m(1, 3) * t.w
  val z = m(2, 0) * t.x + m(2, 1) * t.y + m(2, 2) * t.z + m(2, 3) * t.w
  val w = m(3, 0) * t.x + m(3, 1) * t.y + m(3, 2) * t.z + m(3, 3) * t.w

  tuple.Tuple(x, y, z, w)
}

def transpose(m: Matrix): Matrix = {
  val resultData = Array.ofDim[Double](m.size, m.size)
  for {
    row <- 0 until m.size
    col <- 0 until m.size
  } {
    resultData(row)(col) = m(col, row)
  }
  Matrix(resultData, m.size)
}

def isEqual(
    a: Matrix,
    b: Matrix,
    precision: Double = equality.EPSILON
): Boolean = {
  if (a.size != b.size) {
    false
  } else {
    (0 until a.size).forall { row =>
      (0 until a.size).forall { col =>
        equality.almostEqual(a(row, col), b(row, col), precision)
      }
    }
  }
}
