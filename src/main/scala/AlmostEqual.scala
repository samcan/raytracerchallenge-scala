package com.samuelcantrell.raytracer.equality

val EPSILON = 0.00000001

// see https://www.baeldung.com/scala/compare-floating-points
def almostEqual(a: Double, b: Double, precision: Double = EPSILON): Boolean = {
  (a - b).abs < precision
}
