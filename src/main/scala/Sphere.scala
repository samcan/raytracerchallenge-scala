package com.samuelcantrell.raytracer.sphere

import com.samuelcantrell.raytracer.ray
import com.samuelcantrell.raytracer.tuple
import com.samuelcantrell.raytracer.intersection
import java.util.UUID

case class Sphere(id: String = UUID.randomUUID().toString)

def sphere(): Sphere = {
  Sphere()
}

def intersect(s: Sphere, r: ray.Ray): intersection.Intersections = {
  // Vector from sphere center (0,0,0) to ray origin
  val sphere_to_ray = tuple.subtract(r.origin, tuple.makePoint(0, 0, 0))

  // Coefficients for quadratic equation: at² + bt + c = 0
  val a = tuple.dot(r.direction, r.direction)
  val b = 2 * tuple.dot(r.direction, sphere_to_ray)
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
