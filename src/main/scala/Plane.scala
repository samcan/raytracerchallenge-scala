package com.samuelcantrell.raytracer.plane

import com.samuelcantrell.raytracer.ray
import com.samuelcantrell.raytracer.tuple
import com.samuelcantrell.raytracer.intersection
import com.samuelcantrell.raytracer.matrix
import com.samuelcantrell.raytracer.material
import com.samuelcantrell.raytracer.shape
import com.samuelcantrell.raytracer.equality
import java.util.UUID

case class Plane(
    id: String = UUID.randomUUID().toString,
    transform: matrix.Matrix = matrix.Matrix.identity(),
    objectMaterial: material.Material = material.material()
) extends shape.Shape {

  def localIntersect(localRay: ray.Ray): intersection.Intersections = {
    // If the ray's direction y component is very close to 0,
    // the ray is parallel to the plane and will never intersect
    if (math.abs(localRay.direction.y) < equality.EPSILON) {
      intersection.Intersections(Array.empty[intersection.Intersection])
    } else {
      // The plane lies on y = 0, so we solve for t where ray.origin.y + t * ray.direction.y = 0
      val t = -localRay.origin.y / localRay.direction.y
      intersection.intersections(
        intersection.intersection(t, this)
      )
    }
  }

  def localNormalAt(localPoint: tuple.Tuple): tuple.Tuple = {
    // The normal of a plane at y=0 is always (0, 1, 0) regardless of the point
    tuple.makeVector(0, 1, 0)
  }

  def withTransform(newTransform: matrix.Matrix): Plane = {
    this.copy(transform = newTransform)
  }

  def withMaterial(newMaterial: material.Material): Plane = {
    this.copy(objectMaterial = newMaterial)
  }
}

def plane(): Plane = {
  Plane()
}

// Convenience functions for backward compatibility
def setTransform(p: Plane, t: matrix.Matrix): Plane = {
  shape.setTransform(p, t)
}

def setMaterial(p: Plane, m: material.Material): Plane = {
  shape.setMaterial(p, m)
}

def intersect(p: Plane, r: ray.Ray): intersection.Intersections = {
  shape.intersect(p, r)
}

def normalAt(p: Plane, worldPoint: tuple.Tuple): tuple.Tuple = {
  shape.normalAt(p, worldPoint)
}

// Direct access to local methods for testing
def localIntersect(p: Plane, localRay: ray.Ray): intersection.Intersections = {
  p.localIntersect(localRay)
}

def localNormalAt(p: Plane, localPoint: tuple.Tuple): tuple.Tuple = {
  p.localNormalAt(localPoint)
}
