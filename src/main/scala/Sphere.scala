package com.samuelcantrell.raytracer.sphere

import com.samuelcantrell.raytracer.ray
import com.samuelcantrell.raytracer.tuple
import com.samuelcantrell.raytracer.intersection
import com.samuelcantrell.raytracer.matrix
import com.samuelcantrell.raytracer.material
import com.samuelcantrell.raytracer.shape
import java.util.UUID

case class Sphere(
    id: String = UUID.randomUUID().toString,
    transform: matrix.Matrix = matrix.Matrix.identity(),
    objectMaterial: material.Material = material.material()
) extends shape.Shape {

  def localIntersect(localRay: ray.Ray): intersection.Intersections = {
    // Vector from sphere center (0,0,0) to ray origin
    val sphere_to_ray =
      tuple.subtract(localRay.origin, tuple.makePoint(0, 0, 0))

    // Coefficients for quadratic equation: at² + bt + c = 0
    val a = tuple.dot(localRay.direction, localRay.direction)
    val b = 2 * tuple.dot(localRay.direction, sphere_to_ray)
    val c = tuple.dot(sphere_to_ray, sphere_to_ray) - 1 // sphere radius² = 1

    // Calculate discriminant
    val discriminant = b * b - 4 * a * c

    if (discriminant < 0) {
      // No intersection
      intersection.Intersections(Array.empty[intersection.Intersection])
    } else {
      // Two intersections (or one if discriminant == 0)
      val sqrt_discriminant = math.sqrt(discriminant)
      val t1 = (-b - sqrt_discriminant) / (2 * a)
      val t2 = (-b + sqrt_discriminant) / (2 * a)

      intersection.intersections(
        intersection.intersection(t1, this),
        intersection.intersection(t2, this)
      )
    }
  }

  def localNormalAt(localPoint: tuple.Tuple): tuple.Tuple = {
    // For a unit sphere at origin, the normal is just the point as a vector
    tuple.subtract(localPoint, tuple.makePoint(0, 0, 0))
  }

  def withTransform(newTransform: matrix.Matrix): Sphere = {
    this.copy(transform = newTransform)
  }

  def withMaterial(newMaterial: material.Material): Sphere = {
    this.copy(objectMaterial = newMaterial)
  }
}

def sphere(): Sphere = {
  Sphere()
}

// Keep the old functions for backward compatibility, but delegate to the shape functions
def setTransform(s: Sphere, t: matrix.Matrix): Sphere = {
  shape.setTransform(s, t)
}

def setMaterial(s: Sphere, m: material.Material): Sphere = {
  shape.setMaterial(s, m)
}

def intersect(s: Sphere, r: ray.Ray): intersection.Intersections = {
  shape.intersect(s, r)
}

def normalAt(s: Sphere, worldPoint: tuple.Tuple): tuple.Tuple = {
  shape.normalAt(s, worldPoint)
}
