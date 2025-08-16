package com.samuelcantrell.raytracer.pattern

import com.samuelcantrell.raytracer.color
import com.samuelcantrell.raytracer.tuple
import com.samuelcantrell.raytracer.matrix
import com.samuelcantrell.raytracer.shape

// Base trait for all patterns
trait Pattern {
  def transform: matrix.Matrix
  def patternAt(point: tuple.Tuple): color.Color
  def withTransform(newTransform: matrix.Matrix): Pattern
}

// Stripe pattern implementation
case class StripePattern(
    a: color.Color,
    b: color.Color,
    transform: matrix.Matrix = matrix.Matrix.identity()
) extends Pattern {
  def patternAt(point: tuple.Tuple): color.Color = {
    // Take the floor of x coordinate to determine which stripe we're in
    val stripeIndex = math.floor(point.x).toInt
    // Even indices get color 'a', odd indices get color 'b'
    if (stripeIndex % 2 == 0) a else b
  }

  def withTransform(newTransform: matrix.Matrix): StripePattern = {
    this.copy(transform = newTransform)
  }
}

// Factory functions
def stripePattern(a: color.Color, b: color.Color): StripePattern = {
  StripePattern(a, b)
}

// Function to get color at a specific point for a stripe pattern
def stripeAt(pattern: StripePattern, point: tuple.Tuple): color.Color = {
  pattern.patternAt(point)
}

// Function to set pattern transform
def setPatternTransform[T <: Pattern](
    pattern: T,
    transform: matrix.Matrix
): T = {
  pattern.withTransform(transform).asInstanceOf[T]
}

// Function to get color at a point on an object with transformations
def stripeAtObject(
    pattern: Pattern,
    obj: shape.Shape,
    worldPoint: tuple.Tuple
): color.Color = {
  // Convert world point to object space
  val objectPoint = obj.transform.inverse * worldPoint

  // Convert object point to pattern space
  val patternPoint = pattern.transform.inverse * objectPoint

  // Get the color at the pattern point
  pattern.patternAt(patternPoint)
}
