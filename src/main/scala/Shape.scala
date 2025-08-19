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
  def parent: Option[Shape]

  // Abstract methods that concrete shapes must implement
  def localIntersect(localRay: ray.Ray): intersection.Intersections
  def localNormalAt(localPoint: tuple.Tuple): tuple.Tuple

  // Copy methods for immutability
  def withTransform(newTransform: matrix.Matrix): Shape
  def withMaterial(newMaterial: material.Material): Shape
  def withParent(newParent: Option[Shape]): Shape
}

// General intersect function that works for all shapes
def intersect(shape: Shape, r: ray.Ray): intersection.Intersections = {
  // Transform the ray by the inverse of the shape's transformation
  val transformedRay = ray.transform(r, shape.transform.inverse)
  // Delegate to the shape's local intersection method
  shape.localIntersect(transformedRay)
}

// General normal calculation that works for all shapes (including groups)
def normalAt(shape: Shape, worldPoint: tuple.Tuple): tuple.Tuple = {
  // Convert world point to object space using parent hierarchy
  val localPoint = worldToObject(shape, worldPoint)

  // Get the local normal from the shape
  val localNormal = shape.localNormalAt(localPoint)

  // Convert normal to world space using parent hierarchy
  normalToWorld(shape, localNormal)
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
    parent: Option[Shape] = None,
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

  def withParent(newParent: Option[Shape]): TestShape = {
    this.copy(parent = newParent)
  }
}

def testShape(): TestShape = {
  TestShape()
}

// Convert a point from world space to object space by walking up the parent hierarchy
def worldToObject(shape: Shape, worldPoint: tuple.Tuple): tuple.Tuple = {
  val point = shape.parent match {
    case Some(parent) => worldToObject(parent, worldPoint)
    case None => worldPoint
  }
  shape.transform.inverse * point
}

// Convert a normal from object space to world space by walking up the parent hierarchy
def normalToWorld(shape: Shape, objectNormal: tuple.Tuple): tuple.Tuple = {
  // Transform the normal to parent space
  val normal = shape.transform.inverse.transpose * objectNormal
  val correctedNormal = tuple.makeVector(normal.x, normal.y, normal.z)
  
  // Continue up the parent hierarchy
  val worldNormal = shape.parent match {
    case Some(parent) => normalToWorld(parent, correctedNormal)
    case None => correctedNormal
  }
  
  tuple.normalize(worldNormal)
}
