package com.samuelcantrell.raytracer.ray

import com.samuelcantrell.raytracer.tuple

case class Ray(origin: tuple.Tuple, direction: tuple.Tuple)

def ray(origin: tuple.Tuple, direction: tuple.Tuple): Ray = {
  Ray(origin, direction)
}

def position(r: Ray, t: Double): tuple.Tuple = {
  tuple.add(r.origin, tuple.multiply(r.direction, t))
}
