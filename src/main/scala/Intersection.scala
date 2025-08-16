package com.samuelcantrell.raytracer.intersection

import com.samuelcantrell.raytracer.shape
import com.samuelcantrell.raytracer.ray
import com.samuelcantrell.raytracer.tuple
import com.samuelcantrell.raytracer.equality

case class Intersection(t: Double, obj: shape.Shape)

case class Intersections(values: Array[Intersection]):
  def count: Int = values.length
  def apply(index: Int): Intersection = values(index)

case class Computations(
    t: Double,
    obj: shape.Shape,
    point: tuple.Tuple,
    overPoint: tuple.Tuple,
    eyev: tuple.Tuple,
    normalv: tuple.Tuple,
    reflectv: tuple.Tuple,
    inside: Boolean
)

def intersection(t: Double, s: shape.Shape): Intersection = {
  Intersection(t, s)
}

def intersections(intersections: Intersection*): Intersections = {
  Intersections(intersections.toArray.sortBy(_.t))
}

def hit(xs: Intersections): Option[Intersection] = {
  xs.values.filter(_.t >= 0).headOption
}

def prepareComputations(i: Intersection, r: ray.Ray): Computations = {
  // Compute the point where the intersection occurred
  val point = ray.position(r, i.t)

  // Compute the eye vector (pointing back toward the eye/camera)
  val eyev = tuple.negate(r.direction)

  // Compute the normal vector at the intersection point
  var normalv = shape.normalAt(i.obj, point)

  // Check if we're inside the object
  val inside = tuple.dot(normalv, eyev) < 0

  // If we're inside, invert the normal vector
  if (inside) {
    normalv = tuple.negate(normalv)
  }

  // Compute the reflection vector
  val reflectv = tuple.reflect(r.direction, normalv)

  // Compute the over point - slightly offset in the direction of the normal
  val overPoint = tuple.add(point, tuple.multiply(normalv, equality.EPSILON))

  Computations(
    t = i.t,
    obj = i.obj,
    point = point,
    overPoint = overPoint,
    eyev = eyev,
    normalv = normalv,
    reflectv = reflectv,
    inside = inside
  )
}
