package com.samuelcantrell.raytracer.intersection

import com.samuelcantrell.raytracer.sphere
import com.samuelcantrell.raytracer.ray
import com.samuelcantrell.raytracer.tuple

case class Intersection(t: Double, obj: sphere.Sphere)

case class Intersections(values: Array[Intersection]):
  def count: Int = values.length
  def apply(index: Int): Intersection = values(index)

case class Computations(
    t: Double,
    obj: sphere.Sphere,
    point: tuple.Tuple,
    eyev: tuple.Tuple,
    normalv: tuple.Tuple
)

def intersection(t: Double, s: sphere.Sphere): Intersection = {
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
  val normalv = sphere.normalAt(i.obj, point)

  Computations(
    t = i.t,
    obj = i.obj,
    point = point,
    eyev = eyev,
    normalv = normalv
  )
}
