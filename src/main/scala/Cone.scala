package com.samuelcantrell.raytracer.cone

import com.samuelcantrell.raytracer.ray
import com.samuelcantrell.raytracer.tuple
import com.samuelcantrell.raytracer.intersection
import com.samuelcantrell.raytracer.matrix
import com.samuelcantrell.raytracer.material
import com.samuelcantrell.raytracer.shape
import com.samuelcantrell.raytracer.equality
import java.util.UUID

case class Cone(
    id: String = UUID.randomUUID().toString,
    transform: matrix.Matrix = matrix.Matrix.identity(),
    objectMaterial: material.Material = material.material(),
    minimum: Double = Double.NegativeInfinity,
    maximum: Double = Double.PositiveInfinity,
    closed: Boolean = false
) extends shape.Shape {

  def localIntersect(localRay: ray.Ray): intersection.Intersections = {
    val intersections = scala.collection.mutable.ArrayBuffer[intersection.Intersection]()
    
    // Check for intersections with the cone surface
    // Cone equation: x² + z² = y²
    // Substituting ray: (ox + t*dx)² + (oz + t*dz)² = (oy + t*dy)²
    val a = localRay.direction.x * localRay.direction.x + 
            localRay.direction.z * localRay.direction.z - 
            localRay.direction.y * localRay.direction.y
    
    val b = 2 * localRay.origin.x * localRay.direction.x + 
            2 * localRay.origin.z * localRay.direction.z - 
            2 * localRay.origin.y * localRay.direction.y
            
    val c = localRay.origin.x * localRay.origin.x + 
            localRay.origin.z * localRay.origin.z - 
            localRay.origin.y * localRay.origin.y

    if (math.abs(a) < equality.EPSILON) {
      // Ray is parallel to one of the cone's halves
      if (math.abs(b) >= equality.EPSILON) {
        val t = -c / (2 * b)
        val y = localRay.origin.y + t * localRay.direction.y
        if (minimum < y && y < maximum) {
          intersections += intersection.intersection(t, this)
        }
      }
    } else {
      val discriminant = b * b - 4 * a * c

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
    
    // Check for intersections with the caps if the cone is closed
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
    if (checkCap(localRay, t_lower, minimum)) {
      intersections += intersection.intersection(t_lower, this)
    }

    // Check intersection with upper cap at y = maximum
    val t_upper = (maximum - localRay.origin.y) / localRay.direction.y
    if (checkCap(localRay, t_upper, maximum)) {
      intersections += intersection.intersection(t_upper, this)
    }
  }

  private def checkCap(r: ray.Ray, t: Double, y: Double): Boolean = {
    val x = r.origin.x + t * r.direction.x
    val z = r.origin.z + t * r.direction.z
    val radius = math.abs(y)
    (x * x + z * z) <= radius * radius
  }

  def localNormalAt(localPoint: tuple.Tuple): tuple.Tuple = {
    val dist = localPoint.x * localPoint.x + localPoint.z * localPoint.z

    if (dist < math.abs(localPoint.y) && localPoint.y >= maximum - equality.EPSILON) {
      tuple.makeVector(0, 1, 0)
    } else if (dist < math.abs(localPoint.y) && localPoint.y <= minimum + equality.EPSILON) {
      tuple.makeVector(0, -1, 0)
    } else {
      val y = if (localPoint.y > 0) -math.sqrt(dist) else math.sqrt(dist)
      tuple.makeVector(localPoint.x, y, localPoint.z)
    }
  }

  def withTransform(newTransform: matrix.Matrix): Cone = {
    this.copy(transform = newTransform)
  }

  def withMaterial(newMaterial: material.Material): Cone = {
    this.copy(objectMaterial = newMaterial)
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