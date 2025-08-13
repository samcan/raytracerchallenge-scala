package com.samuelcantrell.raytracer.transformation

import com.samuelcantrell.raytracer.matrix

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
