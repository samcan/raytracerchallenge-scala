package com.samuelcantrell.raytracer.material

import com.samuelcantrell.raytracer.color

case class Material(
    materialColor: color.Color = color.Color(1, 1, 1),
    ambient: Double = 0.1,
    diffuse: Double = 0.9,
    specular: Double = 0.9,
    shininess: Double = 200.0
)

def material(): Material = {
  Material()
}
