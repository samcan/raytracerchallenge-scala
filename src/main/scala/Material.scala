package com.samuelcantrell.raytracer.material

import com.samuelcantrell.raytracer.color
import com.samuelcantrell.raytracer.light
import com.samuelcantrell.raytracer.tuple
import com.samuelcantrell.raytracer.pattern

case class Material(
    materialColor: color.Color = color.Color(1, 1, 1),
    ambient: Double = 0.1,
    diffuse: Double = 0.9,
    specular: Double = 0.9,
    shininess: Double = 200.0,
    materialPattern: Option[pattern.Pattern] = None,
    reflective: Double = 0.0,
    transparency: Double = 0.0,
    refractive_index: Double = 1.0
)

def material(): Material = {
  Material()
}

def lighting(
    m: Material,
    obj: com.samuelcantrell.raytracer.shape.Shape,
    lit: light.PointLight,
    point: tuple.Tuple,
    eyev: tuple.Tuple,
    normalv: tuple.Tuple,
    inShadow: Boolean = false
): color.Color = {
  // Determine the color to use - either from pattern or material color
  val surfaceColor = m.materialPattern match {
    case Some(pat) =>
      // Convert world point to object space, then to pattern space
      val objectPoint = obj.transform.inverse * point
      val patternPoint = pat.transform.inverse * objectPoint
      pat.patternAt(patternPoint)
    case None => m.materialColor
  }

  // Combine the surface color with the light's color/intensity
  val effectiveColor = color.multiply(surfaceColor, lit.intensity)

  // Find the direction to the light source
  val lightv = tuple.normalize(tuple.subtract(lit.position, point))

  // Compute the ambient contribution
  val ambient = color.multiply(effectiveColor, m.ambient)

  // If in shadow, only return the ambient component
  if (inShadow) {
    ambient
  } else {
    // light_dot_normal represents the cosine of the angle between the
    // light vector and the normal vector. A negative number means the
    // light is on the other side of the surface.
    val lightDotNormal = tuple.dot(lightv, normalv)

    val (diffuse, specular) = if (lightDotNormal < 0) {
      // Light is on the other side of the surface
      (color.Color(0, 0, 0), color.Color(0, 0, 0))
    } else {
      // Compute the diffuse contribution
      val diffuseContrib =
        color.multiply(
          color.multiply(effectiveColor, m.diffuse),
          lightDotNormal
        )

      // reflect_dot_eye represents the cosine of the angle between the
      // reflection vector and the eye vector. A negative number means the
      // light reflects away from the eye.
      val reflectv = tuple.reflect(tuple.negate(lightv), normalv)
      val reflectDotEye = tuple.dot(reflectv, eyev)

      val specularContrib = if (reflectDotEye <= 0) {
        // Light reflects away from the eye
        color.Color(0, 0, 0)
      } else {
        // Compute the specular contribution
        val factor = math.pow(reflectDotEye, m.shininess)
        color.multiply(color.multiply(lit.intensity, m.specular), factor)
      }

      (diffuseContrib, specularContrib)
    }

    // Add the three contributions together to get the final shading
    color.add(color.add(ambient, diffuse), specular)
  }
}

// Backward compatibility - version without object parameter
def lighting(
    m: Material,
    lit: light.PointLight,
    point: tuple.Tuple,
    eyev: tuple.Tuple,
    normalv: tuple.Tuple,
    inShadow: Boolean
): color.Color = {
  // For backward compatibility, create a dummy sphere at origin for pattern calculations
  val dummyShape = com.samuelcantrell.raytracer.sphere.sphere()
  lighting(m, dummyShape, lit, point, eyev, normalv, inShadow)
}
