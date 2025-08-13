package com.samuelcantrell.raytracer.world

import com.samuelcantrell.raytracer.sphere
import com.samuelcantrell.raytracer.light
import com.samuelcantrell.raytracer.ray
import com.samuelcantrell.raytracer.intersection
import com.samuelcantrell.raytracer.color
import com.samuelcantrell.raytracer.tuple
import com.samuelcantrell.raytracer.transformation
import com.samuelcantrell.raytracer.material

case class World(
    objects: Vector[sphere.Sphere] = Vector.empty,
    lightSource: Option[light.PointLight] = None
)

def world(): World = {
  World()
}

def defaultWorld(): World = {
  val lightSource = light.pointLight(
    tuple.makePoint(-10, 10, -10),
    color.Color(1, 1, 1)
  )

  val s1Material = material.Material(
    materialColor = color.Color(0.8, 1.0, 0.6),
    ambient = 0.1,
    diffuse = 0.7,
    specular = 0.2,
    shininess = 200.0
  )

  val s1 = sphere.setMaterial(sphere.sphere(), s1Material)

  val s2Transform = transformation.scaling(0.5, 0.5, 0.5)
  val s2 = sphere.setTransform(sphere.sphere(), s2Transform)

  World(
    objects = Vector(s1, s2),
    lightSource = Some(lightSource)
  )
}

def intersectWorld(w: World, r: ray.Ray): intersection.Intersections = {
  val allIntersections = w.objects.flatMap { obj =>
    val objIntersections = sphere.intersect(obj, r)
    objIntersections.values
  }

  intersection.intersections(allIntersections: _*)
}

def contains(w: World, s: sphere.Sphere): Boolean = {
  w.objects.contains(s)
}
