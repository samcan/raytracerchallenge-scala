package com.samuelcantrell.raytracer.pattern

import com.samuelcantrell.raytracer.color
import com.samuelcantrell.raytracer.tuple
import com.samuelcantrell.raytracer.matrix
import com.samuelcantrell.raytracer.shape

// Base Pattern class with common transformation functionality
abstract class Pattern(
    val transform: matrix.Matrix = matrix.Matrix.identity()
) {
  // Abstract method that concrete patterns must implement
  def patternAt(point: tuple.Tuple): color.Color

  // Method to create a new pattern with a different transform
  def withTransform(newTransform: matrix.Matrix): Pattern
}

// Test pattern implementation for testing base functionality
case class TestPattern(
    override val transform: matrix.Matrix = matrix.Matrix.identity()
) extends Pattern(transform) {
  def patternAt(point: tuple.Tuple): color.Color = {
    // Return a color based on the point coordinates
    color.Color(point.x, point.y, point.z)
  }

  def withTransform(newTransform: matrix.Matrix): TestPattern = {
    TestPattern(newTransform)
  }
}

// Stripe pattern implementation using the base
case class StripePattern(
    a: color.Color,
    b: color.Color,
    override val transform: matrix.Matrix = matrix.Matrix.identity()
) extends Pattern(transform) {

  def patternAt(point: tuple.Tuple): color.Color = {
    // Take the floor of x coordinate to determine which stripe we're in
    val stripeIndex = math.floor(point.x).toInt
    // Even indices get color 'a', odd indices get color 'b'
    if (stripeIndex % 2 == 0) a else b
  }

  def withTransform(newTransform: matrix.Matrix): StripePattern = {
    StripePattern(a, b, newTransform)
  }
}

// Gradient pattern implementation using linear interpolation
case class GradientPattern(
    a: color.Color,
    b: color.Color,
    override val transform: matrix.Matrix = matrix.Matrix.identity()
) extends Pattern(transform) {

  def patternAt(point: tuple.Tuple): color.Color = {
    // Get the fractional part of x coordinate for interpolation
    val distance = point.x - math.floor(point.x)

    // Linear interpolation between colors a and b
    color.Color(
      a.red + distance * (b.red - a.red),
      a.green + distance * (b.green - a.green),
      a.blue + distance * (b.blue - a.blue)
    )
  }

  def withTransform(newTransform: matrix.Matrix): GradientPattern = {
    GradientPattern(a, b, newTransform)
  }
}

// Factory functions
def testPattern(): TestPattern = {
  TestPattern()
}

def stripePattern(a: color.Color, b: color.Color): StripePattern = {
  StripePattern(a, b)
}

def gradientPattern(a: color.Color, b: color.Color): GradientPattern = {
  GradientPattern(a, b)
}

// Function to set pattern transform
def setPatternTransform[T <: Pattern](
    pattern: T,
    transform: matrix.Matrix
): T = {
  pattern.withTransform(transform).asInstanceOf[T]
}

// General function to get pattern color at a point on a shape
def patternAtShape(
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

// Backward compatibility functions
def stripeAt(pattern: StripePattern, point: tuple.Tuple): color.Color = {
  pattern.patternAt(point)
}

def stripeAtObject(
    pattern: Pattern,
    obj: shape.Shape,
    worldPoint: tuple.Tuple
): color.Color = {
  patternAtShape(pattern, obj, worldPoint)
}
