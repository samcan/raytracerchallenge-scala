package com.samuelcantrell.raytracer.transformation

import com.samuelcantrell.raytracer.matrix
import com.samuelcantrell.raytracer.tuple

def translation(x: Double, y: Double, z: Double): matrix.Matrix = {
  matrix.Matrix(4, 1, 0, 0, x, 0, 1, 0, y, 0, 0, 1, z, 0, 0, 0, 1)
}

def scaling(x: Double, y: Double, z: Double): matrix.Matrix = {
  matrix.Matrix(4, x, 0, 0, 0, 0, y, 0, 0, 0, 0, z, 0, 0, 0, 0, 1)
}

def rotation_x(radians: Double): matrix.Matrix = {
  val cos_r = math.cos(radians)
  val sin_r = math.sin(radians)
  matrix.Matrix(
    4,
    1,
    0,
    0,
    0,
    0,
    cos_r,
    -sin_r,
    0,
    0,
    sin_r,
    cos_r,
    0,
    0,
    0,
    0,
    1
  )
}

def rotation_y(radians: Double): matrix.Matrix = {
  val cos_r = math.cos(radians)
  val sin_r = math.sin(radians)
  matrix.Matrix(
    4,
    cos_r,
    0,
    sin_r,
    0,
    0,
    1,
    0,
    0,
    -sin_r,
    0,
    cos_r,
    0,
    0,
    0,
    0,
    1
  )
}

def rotation_z(radians: Double): matrix.Matrix = {
  val cos_r = math.cos(radians)
  val sin_r = math.sin(radians)
  matrix.Matrix(
    4,
    cos_r,
    -sin_r,
    0,
    0,
    sin_r,
    cos_r,
    0,
    0,
    0,
    0,
    1,
    0,
    0,
    0,
    0,
    1
  )
}

def shearing(
    xy: Double,
    xz: Double,
    yx: Double,
    yz: Double,
    zx: Double,
    zy: Double
): matrix.Matrix = {
  matrix.Matrix(4, 1, xy, xz, 0, yx, 1, yz, 0, zx, zy, 1, 0, 0, 0, 0, 1)
}

def identity(): matrix.Matrix = matrix.Matrix.identity()

def viewTransform(
    from: tuple.Tuple,
    to: tuple.Tuple,
    up: tuple.Tuple
): matrix.Matrix = {
  // Compute the forward vector (from the eye toward the target)
  val forward = tuple.normalize(tuple.subtract(to, from))

  // Compute the left vector (perpendicular to forward and up)
  val upNormalized = tuple.normalize(up)
  val left = tuple.cross(forward, upNormalized)

  // Compute the true up vector (perpendicular to left and forward)
  val trueUp = tuple.cross(left, forward)

  // Create the orientation matrix
  val orientation = matrix.Matrix(
    4,
    left.x,
    left.y,
    left.z,
    0,
    trueUp.x,
    trueUp.y,
    trueUp.z,
    0,
    -forward.x,
    -forward.y,
    -forward.z,
    0,
    0,
    0,
    0,
    1
  )

  // Translate by the negative of the from point
  val translation = matrix.Matrix(
    4,
    1,
    0,
    0,
    -from.x,
    0,
    1,
    0,
    -from.y,
    0,
    0,
    1,
    -from.z,
    0,
    0,
    0,
    1
  )

  // Combine orientation and translation
  orientation * translation
}

// Extension methods to add fluent interface to Matrix
extension (m: matrix.Matrix) {
  def translate(x: Double, y: Double, z: Double): matrix.Matrix = {
    translation(x, y, z) * m
  }

  def scale(x: Double, y: Double, z: Double): matrix.Matrix = {
    scaling(x, y, z) * m
  }

  def rotate_x(radians: Double): matrix.Matrix = {
    rotation_x(radians) * m
  }

  def rotate_y(radians: Double): matrix.Matrix = {
    rotation_y(radians) * m
  }

  def rotate_z(radians: Double): matrix.Matrix = {
    rotation_z(radians) * m
  }

  def shear(
      xy: Double,
      xz: Double,
      yx: Double,
      yz: Double,
      zx: Double,
      zy: Double
  ): matrix.Matrix = {
    shearing(xy, xz, yx, yz, zx, zy) * m
  }
}
