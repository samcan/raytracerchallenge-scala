package com.samuelcantrell.raytracer.cylinder

import com.samuelcantrell.raytracer.ray
import com.samuelcantrell.raytracer.tuple
import com.samuelcantrell.raytracer.intersection
import com.samuelcantrell.raytracer.matrix
import com.samuelcantrell.raytracer.material
import com.samuelcantrell.raytracer.shape
import com.samuelcantrell.raytracer.equality
import java.util.UUID

case class Cylinder(
    id: String = UUID.randomUUID().toString,
    transform: matrix.Matrix = matrix.Matrix.identity(),
    objectMaterial: material.Material = material.material(),
    minimum: Double = Double.NegativeInfinity,
    maximum: Double = Double.PositiveInfinity,
    closed: Boolean = false
) extends shape.Shape {

  def localIntersect(localRay: ray.Ray): intersection.Intersections = {
    val intersections = scala.collection.mutable.ArrayBuffer[intersection.Intersection]()
    
    // Check for intersections with the cylinder walls
    val a = localRay.direction.x * localRay.direction.x + 
            localRay.direction.z * localRay.direction.z

    // Ray is not parallel to the y axis
    if (math.abs(a) >= equality.EPSILON) {
      val b = 2 * localRay.origin.x * localRay.direction.x + 
              2 * localRay.origin.z * localRay.direction.z
      val c = localRay.origin.x * localRay.origin.x + 
              localRay.origin.z * localRay.origin.z - 1

      val discriminant = b * b - 4 * a * c

      // Ray intersects the cylinder
      if (discriminant >= 0) {
        val t0 = (-b - math.sqrt(discriminant)) / (2 * a)
        val t1 = (-b + math.sqrt(discriminant)) / (2 * a)

        val (t0_sorted, t1_sorted) = if (t0 > t1) (t1, t0) else (t0, t1)

        val y0 = localRay.origin.y + t0_sorted * localRay.direction.y
        val y1 = localRay.origin.y + t1_sorted * localRay.direction.y

        if (minimum < y0 && y0 < maximum) {
          intersections += intersection.intersection(t0_sorted, this)
        }

        if (minimum < y1 && y1 < maximum) {
          intersections += intersection.intersection(t1_sorted, this)
        }
      }
    }
    
    // Check for intersections with the caps if the cylinder is closed
    if (closed) {
      intersectCaps(localRay, intersections)
    }

    intersection.Intersections(intersections.toArray)
  }

  private def intersectCaps(localRay: ray.Ray, intersections: scala.collection.mutable.ArrayBuffer[intersection.Intersection]): Unit = {
    // Check if ray is parallel to the xz plane
    if (math.abs(localRay.direction.y) < equality.EPSILON) {
      return
    }

    // Check intersection with lower cap at y = minimum
    val t_lower = (minimum - localRay.origin.y) / localRay.direction.y
    if (checkCap(localRay, t_lower)) {
      intersections += intersection.intersection(t_lower, this)
    }

    // Check intersection with upper cap at y = maximum
    val t_upper = (maximum - localRay.origin.y) / localRay.direction.y
    if (checkCap(localRay, t_upper)) {
      intersections += intersection.intersection(t_upper, this)
    }
  }

  private def checkCap(r: ray.Ray, t: Double): Boolean = {
    val x = r.origin.x + t * r.direction.x
    val z = r.origin.z + t * r.direction.z
    (x * x + z * z) <= 1
  }

  def localNormalAt(localPoint: tuple.Tuple): tuple.Tuple = {
    val dist = localPoint.x * localPoint.x + localPoint.z * localPoint.z

    if (dist < 1 && localPoint.y >= maximum - equality.EPSILON) {
      tuple.makeVector(0, 1, 0)
    } else if (dist < 1 && localPoint.y <= minimum + equality.EPSILON) {
      tuple.makeVector(0, -1, 0)
    } else {
      tuple.makeVector(localPoint.x, 0, localPoint.z)
    }
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