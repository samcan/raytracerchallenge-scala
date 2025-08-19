package com.samuelcantrell.raytracer.shape

import com.samuelcantrell.raytracer.ray
import com.samuelcantrell.raytracer.tuple
import com.samuelcantrell.raytracer.intersection
import com.samuelcantrell.raytracer.matrix
import com.samuelcantrell.raytracer.material
import java.util.UUID
import scala.collection.mutable.ArrayBuffer

case class Group(
    id: String = UUID.randomUUID().toString,
    transform: matrix.Matrix = matrix.Matrix.identity(),
    objectMaterial: material.Material = material.material(),
    parent: Option[Group] = None,
    children: ArrayBuffer[Shape] = ArrayBuffer.empty[Shape]
) extends Shape {

  def localIntersect(localRay: ray.Ray): intersection.Intersections = {
    val allIntersections = scala.collection.mutable.ArrayBuffer[intersection.Intersection]()
    
    // Intersect the ray with each child shape using the general intersect function
    // which handles transformations properly
    for (child <- children) {
      val childIntersections = intersect(child, localRay)
      allIntersections ++= childIntersections.values
    }
    
    // Sort intersections by t value and return
    val sortedIntersections = allIntersections.sortBy(_.t)
    intersection.Intersections(sortedIntersections.toArray)
  }

  def localNormalAt(localPoint: tuple.Tuple): tuple.Tuple = {
    // Groups don't have a surface, so this shouldn't be called
    throw new RuntimeException("Groups don't have a surface to compute normals")
  }

  def withTransform(newTransform: matrix.Matrix): Group = {
    this.copy(transform = newTransform)
  }

  def withMaterial(newMaterial: material.Material): Group = {
    this.copy(objectMaterial = newMaterial)
  }

  def withParent(newParent: Option[Shape]): Group = {
    this.copy(parent = newParent.map(_.asInstanceOf[Group]))
  }

  def isEmpty: Boolean = children.isEmpty
  
  def includes(shape: Shape): Boolean = children.contains(shape)
}

def group(): Group = {
  Group()
}

def addChild(group: Group, shape: Shape): (Group, Shape) = {
  val updatedChildren = group.children.clone()
  val shapeWithParent = shape.withParent(Some(group))
  updatedChildren += shapeWithParent
  val updatedGroup = group.copy(children = updatedChildren)
  (updatedGroup, shapeWithParent)
}