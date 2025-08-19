package com.samuelcantrell.raytracer.cube

import com.samuelcantrell.raytracer.ray
import com.samuelcantrell.raytracer.tuple
import com.samuelcantrell.raytracer.intersection
import com.samuelcantrell.raytracer.matrix
import com.samuelcantrell.raytracer.material
import com.samuelcantrell.raytracer.shape
import java.util.UUID

case class Cube(
    id: String = UUID.randomUUID().toString,
    transform: matrix.Matrix = matrix.Matrix.identity(),
    objectMaterial: material.Material = material.material(),
    parent: Option[shape.Shape] = None
) extends shape.Shape {

  def localIntersect(localRay: ray.Ray): intersection.Intersections = {
    // Helper function to check intersections with a pair of parallel planes
    def checkAxis(origin: Double, direction: Double): (Double, Double) = {
      val tmin_numerator = -1 - origin
      val tmax_numerator = 1 - origin

      if (math.abs(direction) >= 1e-10) {
        val tmin_temp = tmin_numerator / direction
        val tmax_temp = tmax_numerator / direction
        if (tmin_temp > tmax_temp) (tmax_temp, tmin_temp)
        else (tmin_temp, tmax_temp)
      } else {
        // If direction is essentially 0, check if origin is within the slab
        if (origin < -1 || origin > 1) {
          // Ray is parallel to the planes and outside the slab - no intersection possible
          (Double.PositiveInfinity, Double.NegativeInfinity)
        } else {
          // Ray is parallel to the planes and inside the slab
          (Double.NegativeInfinity, Double.PositiveInfinity)
        }
      }
    }

    // Check intersections with all three pairs of parallel planes
    val (xtmin, xtmax) = checkAxis(localRay.origin.x, localRay.direction.x)
    val (ytmin, ytmax) = checkAxis(localRay.origin.y, localRay.direction.y)
    val (ztmin, ztmax) = checkAxis(localRay.origin.z, localRay.direction.z)

    // Find the intersection interval
    val tmin = math.max(math.max(xtmin, ytmin), ztmin)
    val tmax = math.min(math.min(xtmax, ytmax), ztmax)

    // If tmin > tmax, there's no intersection
    if (tmin > tmax) {
      intersection.Intersections(Array.empty[intersection.Intersection])
    } else {
      // Return the two intersection points
      intersection.intersections(
        intersection.intersection(tmin, this),
        intersection.intersection(tmax, this)
      )
    }
  }

  def localNormalAt(localPoint: tuple.Tuple): tuple.Tuple = {
    // Find which coordinate has the maximum absolute value
    val maxc = math.max(
      math.max(math.abs(localPoint.x), math.abs(localPoint.y)),
      math.abs(localPoint.z)
    )

    if (maxc == math.abs(localPoint.x)) {
      tuple.makeVector(localPoint.x, 0, 0)
    } else if (maxc == math.abs(localPoint.y)) {
      tuple.makeVector(0, localPoint.y, 0)
    } else {
      tuple.makeVector(0, 0, localPoint.z)
    }
  }

  def withTransform(newTransform: matrix.Matrix): Cube = {
    this.copy(transform = newTransform)
  }

  def withMaterial(newMaterial: material.Material): Cube = {
    this.copy(objectMaterial = newMaterial)
  }

  def withParent(newParent: Option[shape.Shape]): Cube = {
    this.copy(parent = newParent)
  }
}

def cube(): Cube = {
  Cube()
}

// Convenience functions for backward compatibility
def setTransform(c: Cube, t: matrix.Matrix): Cube = {
  shape.setTransform(c, t)
}

def setMaterial(c: Cube, m: material.Material): Cube = {
  shape.setMaterial(c, m)
}

def intersect(c: Cube, r: ray.Ray): intersection.Intersections = {
  shape.intersect(c, r)
}

def normalAt(c: Cube, worldPoint: tuple.Tuple): tuple.Tuple = {
  shape.normalAt(c, worldPoint)
}

// Direct access to local methods for testing
def localIntersect(c: Cube, localRay: ray.Ray): intersection.Intersections = {
  c.localIntersect(localRay)
}

def localNormalAt(c: Cube, localPoint: tuple.Tuple): tuple.Tuple = {
  c.localNormalAt(localPoint)
}
