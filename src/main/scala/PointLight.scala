package com.samuelcantrell.raytracer.light

import com.samuelcantrell.raytracer.tuple
import com.samuelcantrell.raytracer.color

case class PointLight(position: tuple.Tuple, intensity: color.Color)

def pointLight(position: tuple.Tuple, intensity: color.Color): PointLight = {
  PointLight(position, intensity)
}
