package com.samuelcantrell.raytracer.tuple

import com.samuelcantrell.raytracer.equality

case class Tuple(x: Double, y: Double, z: Double, w: Double)

def isPoint(t: Tuple): Boolean = equality.almostEqual(t.w, 1.0)
def isVector(t: Tuple): Boolean = equality.almostEqual(t.w, 0.0)

def makePoint(x: Double, y: Double, z: Double): Tuple = Tuple(x, y, z, 1.0)
def makeVector(x: Double, y: Double, z: Double): Tuple = Tuple(x, y, z, 0.0)

def isEqual(a: Tuple, b: Tuple): Boolean = {
  equality.almostEqual(a.x, b.x) && equality.almostEqual(a.y, b.y) &&
  equality.almostEqual(a.z, b.z) && equality.almostEqual(a.w, b.w)
}

def add(a: Tuple, b: Tuple): Tuple = {
  Tuple(a.x + b.x, a.y + b.y, a.z + b.z, a.w + b.w)
}

def subtract(a: Tuple, b: Tuple): Tuple = {
  Tuple(a.x - b.x, a.y - b.y, a.z - b.z, a.w - b.w)
}

def negate(a: Tuple): Tuple = {
  Tuple(-a.x, -a.y, -a.z, -a.w)
}

def multiply(a: Tuple, b: Double): Tuple = {
  Tuple(a.x * b, a.y * b, a.z * b, a.w * b)
}

def divide(a: Tuple, b: Double): Tuple = {
  Tuple(a.x / b, a.y / b, a.z / b, a.w / b)
}

def magnitude(a: Tuple): Double = {
  math.sqrt(
    math.pow(a.x, 2.0) + math.pow(a.y, 2.0) + math.pow(a.z, 2.0) +
      math.pow(a.w, 2.0)
  )
}

def normalize(a: Tuple): Tuple = {
  val mag = magnitude(a)
  Tuple(a.x / mag, a.y / mag, a.z / mag, a.w / mag)
}

def dot(a: Tuple, b: Tuple): Double = {
  a.x * b.x + a.y * b.y + a.z * b.z + a.w * b.w
}

def cross(a: Tuple, b: Tuple): Tuple = {
  makeVector(
    a.y * b.z - a.z * b.y,
    a.z * b.x - a.x * b.z,
    a.x * b.y - a.y * b.x
  )
}
