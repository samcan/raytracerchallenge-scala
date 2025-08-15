package com.samuelcantrell.raytracer.shape

import com.samuelcantrell.raytracer.ray
import com.samuelcantrell.raytracer.tuple
import com.samuelcantrell.raytracer.intersection
import com.samuelcantrell.raytracer.matrix
import com.samuelcantrell.raytracer.material
import java.util.UUID

// Abstract Shape trait that defines common behavior
trait Shape {
  def id: String
  def transform: matrix.Matrix
  def objectMaterial: material.Material

  // Abstract methods that concrete shapes must implement
  def localIntersect(localRay: ray.Ray): intersection.Intersections
  def localNormalAt(localPoint: tuple.Tuple): tuple.Tuple

  // Copy methods for immutability
  def withTransform(newTransform: matrix.Matrix): Shape
  def withMaterial(newMaterial: material.Material): Shape
}

// General intersect function that works for all shapes
def intersect(shape: Shape, r: ray.Ray): intersection.Intersections = {
  // Transform the ray by the inverse of the shape's transformation
  val transformedRay = ray.transform(r, shape.transform.inverse)
  // Delegate to the shape's local intersection method
  shape.localIntersect(transformedRay)
}

// General normal calculation that works for all shapes
def normalAt(shape: Shape, worldPoint: tuple.Tuple): tuple.Tuple = {
  // Transform the world point to object space
  val localPoint = shape.transform.inverse * worldPoint

  // Get the local normal from the shape
  val localNormal = shape.localNormalAt(localPoint)

  // Transform the normal back to world space
  val worldNormal = shape.transform.inverse.transpose * localNormal

  // Set w component to 0 to ensure it's a vector, then normalize
  val correctedNormal =
    tuple.makeVector(worldNormal.x, worldNormal.y, worldNormal.z)

  tuple.normalize(correctedNormal)
}

// Convenience functions for setting transform and material
def setTransform[T <: Shape](shape: T, t: matrix.Matrix): T = {
  shape.withTransform(t).asInstanceOf[T]
}

def setMaterial[T <: Shape](shape: T, m: material.Material): T = {
  shape.withMaterial(m).asInstanceOf[T]
}

// TestShape implementation for testing
case class TestShape(
    id: String = UUID.randomUUID().toString,
    transform: matrix.Matrix = matrix.Matrix.identity(),
    objectMaterial: material.Material = material.material(),
    var savedRay: Option[ray.Ray] = None
) extends Shape {

  def localIntersect(localRay: ray.Ray): intersection.Intersections = {
    // Save the ray for testing purposes
    val mutableThis = this.asInstanceOf[TestShape]
    mutableThis.savedRay = Some(localRay)
    // Return empty intersections for testing
    intersection.Intersections(Array.empty[intersection.Intersection])
  }

  def localNormalAt(localPoint: tuple.Tuple): tuple.Tuple = {
    // Return the local point as a vector for testing
    tuple.makeVector(localPoint.x, localPoint.y, localPoint.z)
  }

  def withTransform(newTransform: matrix.Matrix): TestShape = {
    this.copy(transform = newTransform)
  }

  def withMaterial(newMaterial: material.Material): TestShape = {
    this.copy(objectMaterial = newMaterial)
  }
}

def testShape(): TestShape = {
  TestShape()
}
