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
    underPoint: tuple.Tuple,
    eyev: tuple.Tuple,
    normalv: tuple.Tuple,
    inside: Boolean,
    reflectv: tuple.Tuple,
    n1: Double,
    n2: Double
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
  prepareComputations(i, r, Intersections(Array(i)))
}

def prepareComputations(i: Intersection, r: ray.Ray, xs: Intersections): Computations = {
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

  // Compute the over point - slightly offset in the direction of the normal
  val overPoint = tuple.add(point, tuple.multiply(normalv, equality.EPSILON))

  // Compute the under point - slightly offset in the opposite direction of the normal
  val underPoint = tuple.subtract(point, tuple.multiply(normalv, equality.EPSILON))

  // Compute the reflection vector
  val reflectv = tuple.reflect(r.direction, normalv)

  // Calculate n1 and n2 for refraction
  val (n1, n2) = calculateRefractiveIndices(i, xs)

  Computations(
    t = i.t,
    obj = i.obj,
    point = point,
    overPoint = overPoint,
    underPoint = underPoint,
    eyev = eyev,
    normalv = normalv,
    inside = inside,
    reflectv = reflectv,
    n1 = n1,
    n2 = n2
  )
}

private def calculateRefractiveIndices(hit: Intersection, xs: Intersections): (Double, Double) = {
  var containers = List.empty[shape.Shape]
  var n1 = 1.0
  var n2 = 1.0
  var hitProcessed = false

  for (i <- xs.values if !hitProcessed) {
    // If this is the hit we're interested in, record n1
    if (i == hit) {
      n1 = if (containers.isEmpty) 1.0 else containers.last.objectMaterial.refractive_index
    }

    // Check if this intersection's object is already in containers
    val objectIndex = containers.indexOf(i.obj)
    if (objectIndex >= 0) {
      // Remove the object (we're exiting it)
      containers = containers.patch(objectIndex, Nil, 1)
    } else {
      // Add the object (we're entering it)
      containers = containers :+ i.obj
    }

    // If this is the hit we're interested in, record n2 and stop processing
    if (i == hit) {
      n2 = if (containers.isEmpty) 1.0 else containers.last.objectMaterial.refractive_index
      hitProcessed = true
    }
  }

  (n1, n2)
}
