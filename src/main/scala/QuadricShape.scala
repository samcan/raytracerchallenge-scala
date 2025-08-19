package com.samuelcantrell.raytracer.quadric

import com.samuelcantrell.raytracer.ray
import com.samuelcantrell.raytracer.tuple
import com.samuelcantrell.raytracer.intersection
import com.samuelcantrell.raytracer.matrix
import com.samuelcantrell.raytracer.material
import com.samuelcantrell.raytracer.shape
import com.samuelcantrell.raytracer.equality
import java.util.UUID

// Base trait for shapes that can be represented by quadric surfaces (cylinders, cones, etc.)
abstract class QuadricShape(
    val id: String = UUID.randomUUID().toString,
    val transform: matrix.Matrix = matrix.Matrix.identity(),
    val objectMaterial: material.Material = material.material(),
    val minimum: Double = Double.NegativeInfinity,
    val maximum: Double = Double.PositiveInfinity,
    val closed: Boolean = false,
    val parent: Option[shape.Shape] = None
) extends shape.Shape {

  // Abstract methods that subclasses must implement
  protected def surfaceIntersectionCoefficients(localRay: ray.Ray): (Double, Double, Double)
  protected def handleParallelRay(localRay: ray.Ray, b: Double, c: Double): Option[Double]
  protected def capRadius(y: Double): Double
  protected def surfaceNormal(localPoint: tuple.Tuple): tuple.Tuple
  protected def isOnCap(localPoint: tuple.Tuple, y: Double): Boolean

  def localIntersect(localRay: ray.Ray): intersection.Intersections = {
    val intersections = scala.collection.mutable.ArrayBuffer[intersection.Intersection]()
    
    // Get surface intersection coefficients from subclass
    val (a, b, c) = surfaceIntersectionCoefficients(localRay)

    if (math.abs(a) < equality.EPSILON) {
      // Handle special case (parallel ray for cones, never happens for cylinders)
      handleParallelRay(localRay, b, c) match {
        case Some(t) =>
          val y = localRay.origin.y + t * localRay.direction.y
          if (minimum < y && y < maximum) {
            intersections += intersection.intersection(t, this)
          }
        case None => // No intersection
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
    
    // Check for intersections with the caps if the shape is closed
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
    val radius = capRadius(y)
    (x * x + z * z) <= radius * radius
  }

  def localNormalAt(localPoint: tuple.Tuple): tuple.Tuple = {
    // Check if we're on a cap first
    if (isOnCap(localPoint, maximum)) {
      tuple.makeVector(0, 1, 0)
    } else if (isOnCap(localPoint, minimum)) {
      tuple.makeVector(0, -1, 0)
    } else {
      // We're on the surface
      surfaceNormal(localPoint)
    }
  }
}