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

def isShadowed(w: World, point: tuple.Tuple): Boolean = {
  w.lightSource match {
    case Some(light) =>
      // Vector from point to light source
      val v = tuple.subtract(light.position, point)
      val distance = tuple.magnitude(v)
      val direction = tuple.normalize(v)

      // Create a ray from the point toward the light
      val r = ray.ray(point, direction)
      val intersections = intersectWorld(w, r)

      // Check if there's a hit between the point and the light
      intersection.hit(intersections) match {
        case Some(h) => h.t < distance
        case None    => false
      }
    case None => false // No light source, no shadows
  }
}

def shadeHit(w: World, comps: intersection.Computations): color.Color = {
  w.lightSource match {
    case Some(light) =>
      val shadowed = isShadowed(w, comps.overPoint)
      material.lighting(
        comps.obj.objectMaterial,
        light,
        comps.overPoint,
        comps.eyev,
        comps.normalv,
        shadowed
      )
    case None =>
      color.Color(0, 0, 0) // No light source, return black
  }
}

def colorAt(w: World, r: ray.Ray): color.Color = {
  val intersections = intersectWorld(w, r)
  intersection.hit(intersections) match {
    case Some(hit) =>
      val comps = intersection.prepareComputations(hit, r)
      shadeHit(w, comps)
    case None =>
      color.Color(0, 0, 0) // Ray missed all objects, return black
  }
}
