package com.samuelcantrell.raytracer.cone

import com.samuelcantrell.raytracer.ray
import com.samuelcantrell.raytracer.tuple
import com.samuelcantrell.raytracer.intersection
import com.samuelcantrell.raytracer.matrix
import com.samuelcantrell.raytracer.material
import com.samuelcantrell.raytracer.shape
import com.samuelcantrell.raytracer.equality
import com.samuelcantrell.raytracer.quadric.QuadricShape
import java.util.UUID

case class Cone(
    override val id: String = UUID.randomUUID().toString,
    override val transform: matrix.Matrix = matrix.Matrix.identity(),
    override val objectMaterial: material.Material = material.material(),
    override val minimum: Double = Double.NegativeInfinity,
    override val maximum: Double = Double.PositiveInfinity,
    override val closed: Boolean = false,
    override val parent: Option[shape.Shape] = None
) extends QuadricShape(id, transform, objectMaterial, minimum, maximum, closed, parent) {

  // Cone-specific implementations
  protected def surfaceIntersectionCoefficients(localRay: ray.Ray): (Double, Double, Double) = {
    // Cone equation: x² + z² = y²
    val a = localRay.direction.x * localRay.direction.x + 
            localRay.direction.z * localRay.direction.z - 
            localRay.direction.y * localRay.direction.y
    val b = 2 * localRay.origin.x * localRay.direction.x + 
            2 * localRay.origin.z * localRay.direction.z - 
            2 * localRay.origin.y * localRay.direction.y
    val c = localRay.origin.x * localRay.origin.x + 
            localRay.origin.z * localRay.origin.z - 
            localRay.origin.y * localRay.origin.y
    (a, b, c)
  }

  protected def handleParallelRay(localRay: ray.Ray, b: Double, c: Double): Option[Double] = {
    // Ray is parallel to one of the cone's halves
    if (math.abs(b) >= equality.EPSILON) {
      Some(-c / (2 * b))
    } else {
      None
    }
  }

  protected def capRadius(y: Double): Double = math.abs(y)

  protected def surfaceNormal(localPoint: tuple.Tuple): tuple.Tuple = {
    val dist = localPoint.x * localPoint.x + localPoint.z * localPoint.z
    val y = if (localPoint.y > 0) -math.sqrt(dist) else math.sqrt(dist)
    tuple.makeVector(localPoint.x, y, localPoint.z)
  }

  protected def isOnCap(localPoint: tuple.Tuple, y: Double): Boolean = {
    val dist = localPoint.x * localPoint.x + localPoint.z * localPoint.z
    dist < math.abs(y) && math.abs(localPoint.y - y) < equality.EPSILON
  }

  def withTransform(newTransform: matrix.Matrix): Cone = {
    this.copy(transform = newTransform)
  }

  def withMaterial(newMaterial: material.Material): Cone = {
    this.copy(objectMaterial = newMaterial)
  }

  def withParent(newParent: Option[shape.Shape]): Cone = {
    this.copy(parent = newParent)
  }
}

def cone(): Cone = {
  Cone()
}

// Convenience functions for backward compatibility
def setTransform(c: Cone, t: matrix.Matrix): Cone = {
  shape.setTransform(c, t)
}

def setMaterial(c: Cone, m: material.Material): Cone = {
  shape.setMaterial(c, m)
}

def intersect(c: Cone, r: ray.Ray): intersection.Intersections = {
  shape.intersect(c, r)
}

def normalAt(c: Cone, worldPoint: tuple.Tuple): tuple.Tuple = {
  shape.normalAt(c, worldPoint)
}

// Direct access to local methods for testing
def localIntersect(c: Cone, localRay: ray.Ray): intersection.Intersections = {
  c.localIntersect(localRay)
}

def localNormalAt(c: Cone, localPoint: tuple.Tuple): tuple.Tuple = {
  c.localNormalAt(localPoint)
}