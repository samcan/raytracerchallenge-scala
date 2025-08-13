package com.samuelcantrell.raytracer.intersection

import com.samuelcantrell.raytracer.sphere

case class Intersection(t: Double, obj: sphere.Sphere)

case class Intersections(values: Array[Intersection]):
  def count: Int = values.length
  def apply(index: Int): Intersection = values(index)

def intersection(t: Double, s: sphere.Sphere): Intersection = {
  Intersection(t, s)
}

def intersections(intersections: Intersection*): Intersections = {
  Intersections(intersections.toArray.sortBy(_.t))
}

def hit(xs: Intersections): Option[Intersection] = {
  xs.values.filter(_.t >= 0).headOption
}
