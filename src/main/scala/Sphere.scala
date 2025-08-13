package com.samuelcantrell.raytracer.sphere

import com.samuelcantrell.raytracer.ray
import com.samuelcantrell.raytracer.tuple
import com.samuelcantrell.raytracer.intersection
import com.samuelcantrell.raytracer.matrix
import java.util.UUID

case class Sphere(
    id: String = UUID.randomUUID().toString,
    transform: matrix.Matrix = matrix.Matrix.identity()
)

def sphere(): Sphere = {
  Sphere()
}

def setTransform(s: Sphere, t: matrix.Matrix): Sphere = {
  s.copy(transform = t)
}

def intersect(s: Sphere, r: ray.Ray): intersection.Intersections = {
  // Transform the ray by the inverse of the sphere's transformation
  val transformed_ray = ray.transform(r, s.transform.inverse)

  // Vector from sphere center (0,0,0) to ray origin
  val sphere_to_ray =
    tuple.subtract(transformed_ray.origin, tuple.makePoint(0, 0, 0))

  // Coefficients for quadratic equation: at² + bt + c = 0
  val a = tuple.dot(transformed_ray.direction, transformed_ray.direction)
  val b = 2 * tuple.dot(transformed_ray.direction, sphere_to_ray)
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
      intersection.intersection(t1, s),
      intersection.intersection(t2, s)
    )
  }
}

def normalAt(s: Sphere, worldPoint: tuple.Tuple): tuple.Tuple = {
  // Transform the world point to object space
  val objectPoint = s.transform.inverse * worldPoint

  // Calculate the object normal (vector from center to point)
  val objectNormal = tuple.subtract(objectPoint, tuple.makePoint(0, 0, 0))

  // Transform the normal back to world space
  val worldNormal = s.transform.inverse.transpose * objectNormal

  // Set w component to 0 to ensure it's a vector, then normalize
  val correctedNormal =
    tuple.makeVector(worldNormal.x, worldNormal.y, worldNormal.z)

  tuple.normalize(correctedNormal)
}
