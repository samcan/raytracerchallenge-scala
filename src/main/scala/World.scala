package com.samuelcantrell.raytracer.world

import com.samuelcantrell.raytracer.shape
import com.samuelcantrell.raytracer.sphere
import com.samuelcantrell.raytracer.light
import com.samuelcantrell.raytracer.ray
import com.samuelcantrell.raytracer.intersection
import com.samuelcantrell.raytracer.color
import com.samuelcantrell.raytracer.tuple
import com.samuelcantrell.raytracer.transformation
import com.samuelcantrell.raytracer.material

case class World(
    objects: Vector[shape.Shape] = Vector.empty,
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
    val objIntersections = shape.intersect(obj, r)
    objIntersections.values
  }

  intersection.intersections(allIntersections*)
}

def contains(w: World, s: shape.Shape): Boolean = {
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

def reflectedColor(
    w: World,
    comps: intersection.Computations,
    remaining: Int = 5
): color.Color = {
  // If remaining is less than 1, return black to prevent infinite recursion
  if (remaining < 1) {
    return color.Color(0, 0, 0)
  }

  // If the material is not reflective, return black
  if (comps.obj.objectMaterial.reflective == 0.0) {
    return color.Color(0, 0, 0)
  }

  // Create a reflection ray
  val reflectRay = ray.ray(comps.overPoint, comps.reflectv)

  // Find the color of the reflection, decrementing remaining
  val reflectedColorValue = colorAt(w, reflectRay, remaining - 1)

  // Return the reflected color multiplied by the reflective value
  color.multiply(reflectedColorValue, comps.obj.objectMaterial.reflective)
}

def refracted_color(
    w: World,
    comps: intersection.Computations,
    remaining: Int = 5
): color.Color = {
  // If remaining is less than 1, return black to prevent infinite recursion
  if (remaining < 1) {
    return color.Color(0, 0, 0)
  }

  // If the material is not transparent, return black
  if (comps.obj.objectMaterial.transparency == 0.0) {
    return color.Color(0, 0, 0)
  }

  // Check for total internal reflection
  val nRatio = comps.n1 / comps.n2
  val cosI = tuple.dot(comps.eyev, comps.normalv)
  val sin2T = nRatio * nRatio * (1 - cosI * cosI)
  
  if (sin2T > 1) {
    // Total internal reflection
    return color.Color(0, 0, 0)
  }

  // Find cos(theta_t) via trigonometric identity
  val cosT = math.sqrt(1.0 - sin2T)

  // Compute the direction of the refracted ray
  val direction = tuple.subtract(
    tuple.multiply(comps.normalv, nRatio * cosI - cosT),
    tuple.multiply(comps.eyev, nRatio)
  )

  // Create the refracted ray
  val refractRay = ray.ray(comps.underPoint, direction)

  // Find the color of the refracted ray, decrementing remaining to avoid infinite recursion
  val refractedColorValue = colorAt(w, refractRay, remaining - 1)

  // Return the refracted color multiplied by the transparency value
  color.multiply(refractedColorValue, comps.obj.objectMaterial.transparency)
}

def shadeHit(
    w: World,
    comps: intersection.Computations,
    remaining: Int = 5
): color.Color = {
  w.lightSource match {
    case Some(light) =>
      val shadowed = isShadowed(w, comps.overPoint)
      val surface = material.lighting(
        comps.obj.objectMaterial,
        comps.obj,
        light,
        comps.overPoint,
        comps.eyev,
        comps.normalv,
        shadowed
      )
      val reflected = reflectedColor(w, comps, remaining)

      // Combine surface color and reflected color
      color.add(surface, reflected)
    case None =>
      color.Color(0, 0, 0) // No light source, return black
  }
}

// Backward compatibility version without remaining parameter
def shadeHit(w: World, comps: intersection.Computations): color.Color = {
  shadeHit(w, comps, 5)
}

def colorAt(w: World, r: ray.Ray, remaining: Int = 5): color.Color = {
  val intersections = intersectWorld(w, r)
  intersection.hit(intersections) match {
    case Some(hit) =>
      val comps = intersection.prepareComputations(hit, r)
      shadeHit(w, comps, remaining)
    case None =>
      color.Color(0, 0, 0) // Ray missed all objects, return black
  }
}

// Backward compatibility version without remaining parameter
def colorAt(w: World, r: ray.Ray): color.Color = {
  colorAt(w, r, 5)
}
