package com.samuelcantrell.raytracer.triangle

import com.samuelcantrell.raytracer.ray
import com.samuelcantrell.raytracer.tuple
import com.samuelcantrell.raytracer.intersection
import com.samuelcantrell.raytracer.matrix
import com.samuelcantrell.raytracer.material
import com.samuelcantrell.raytracer.shape
import com.samuelcantrell.raytracer.equality
import java.util.UUID

case class Triangle(
    p1: tuple.Tuple,
    p2: tuple.Tuple, 
    p3: tuple.Tuple,
    e1: tuple.Tuple,
    e2: tuple.Tuple,
    normal: tuple.Tuple,
    id: String = UUID.randomUUID().toString,
    transform: matrix.Matrix = matrix.Matrix.identity(),
    objectMaterial: material.Material = material.material(),
    parent: Option[shape.Shape] = None
) extends shape.Shape {

  def localIntersect(localRay: ray.Ray): intersection.Intersections = {
    // Möller-Trumbore ray-triangle intersection algorithm
    val dirCrossE2 = tuple.cross(localRay.direction, e2)
    val det = tuple.dot(e1, dirCrossE2)
    
    // If determinant is near zero, ray lies in plane of triangle
    if (math.abs(det) < equality.EPSILON) {
      return intersection.Intersections(Array.empty[intersection.Intersection])
    }
    
    val f = 1.0 / det
    val p1ToOrigin = tuple.subtract(localRay.origin, p1)
    val u = f * tuple.dot(p1ToOrigin, dirCrossE2)
    
    // Test first barycentric coordinate
    if (u < 0 || u > 1) {
      return intersection.Intersections(Array.empty[intersection.Intersection])
    }
    
    val originCrossE1 = tuple.cross(p1ToOrigin, e1)
    val v = f * tuple.dot(localRay.direction, originCrossE1)
    
    // Test second barycentric coordinate
    if (v < 0 || (u + v) > 1) {
      return intersection.Intersections(Array.empty[intersection.Intersection])
    }
    
    // Calculate t - the distance along the ray
    val t = f * tuple.dot(e2, originCrossE1)
    
    intersection.intersections(
      intersection.intersection(t, this)
    )
  }

  def localNormalAt(localPoint: tuple.Tuple): tuple.Tuple = {
    // For a triangle, the normal is constant across the entire surface
    normal
  }

  def withTransform(newTransform: matrix.Matrix): Triangle = {
    this.copy(transform = newTransform)
  }

  def withMaterial(newMaterial: material.Material): Triangle = {
    this.copy(objectMaterial = newMaterial)
  }

  def withParent(newParent: Option[shape.Shape]): Triangle = {
    this.copy(parent = newParent)
  }
}

def triangle(p1: tuple.Tuple, p2: tuple.Tuple, p3: tuple.Tuple): Triangle = {
  // Precompute edge vectors
  val e1 = tuple.subtract(p2, p1)
  val e2 = tuple.subtract(p3, p1)
  
  // Precompute normal using cross product
  val normal = tuple.normalize(tuple.cross(e2, e1))
  
  Triangle(p1, p2, p3, e1, e2, normal)
}