package com.samuelcantrell.raytracer.cylinder

import com.samuelcantrell.raytracer.ray
import com.samuelcantrell.raytracer.tuple
import com.samuelcantrell.raytracer.intersection
import com.samuelcantrell.raytracer.matrix
import com.samuelcantrell.raytracer.material
import com.samuelcantrell.raytracer.shape
import com.samuelcantrell.raytracer.equality
import com.samuelcantrell.raytracer.quadric.QuadricShape
import java.util.UUID

case class Cylinder(
    override val id: String = UUID.randomUUID().toString,
    override val transform: matrix.Matrix = matrix.Matrix.identity(),
    override val objectMaterial: material.Material = material.material(),
    override val minimum: Double = Double.NegativeInfinity,
    override val maximum: Double = Double.PositiveInfinity,
    override val closed: Boolean = false
) extends QuadricShape(id, transform, objectMaterial, minimum, maximum, closed) {

  // Cylinder-specific implementations
  protected def surfaceIntersectionCoefficients(localRay: ray.Ray): (Double, Double, Double) = {
    val a = localRay.direction.x * localRay.direction.x + 
            localRay.direction.z * localRay.direction.z
    val b = 2 * localRay.origin.x * localRay.direction.x + 
            2 * localRay.origin.z * localRay.direction.z
    val c = localRay.origin.x * localRay.origin.x + 
            localRay.origin.z * localRay.origin.z - 1
    (a, b, c)
  }

  protected def handleParallelRay(localRay: ray.Ray, b: Double, c: Double): Option[Double] = {
    // Cylinders don't have parallel ray intersections (when a ≈ 0, no intersection)
    None
  }

  protected def capRadius(y: Double): Double = 1.0

  protected def surfaceNormal(localPoint: tuple.Tuple): tuple.Tuple = {
    tuple.makeVector(localPoint.x, 0, localPoint.z)
  }

  protected def isOnCap(localPoint: tuple.Tuple, y: Double): Boolean = {
    val dist = localPoint.x * localPoint.x + localPoint.z * localPoint.z
    dist < 1 && math.abs(localPoint.y - y) < equality.EPSILON
  }

  def withTransform(newTransform: matrix.Matrix): Cylinder = {
    this.copy(transform = newTransform)
  }

  def withMaterial(newMaterial: material.Material): Cylinder = {
    this.copy(objectMaterial = newMaterial)
  }
}

def cylinder(): Cylinder = {
  Cylinder()
}

// Convenience functions for backward compatibility
def setTransform(c: Cylinder, t: matrix.Matrix): Cylinder = {
  shape.setTransform(c, t)
}

def setMaterial(c: Cylinder, m: material.Material): Cylinder = {
  shape.setMaterial(c, m)
}

def intersect(c: Cylinder, r: ray.Ray): intersection.Intersections = {
  shape.intersect(c, r)
}

def normalAt(c: Cylinder, worldPoint: tuple.Tuple): tuple.Tuple = {
  shape.normalAt(c, worldPoint)
}

// Direct access to local methods for testing
def localIntersect(c: Cylinder, localRay: ray.Ray): intersection.Intersections = {
  c.localIntersect(localRay)
}

def localNormalAt(c: Cylinder, localPoint: tuple.Tuple): tuple.Tuple = {
  c.localNormalAt(localPoint)
}