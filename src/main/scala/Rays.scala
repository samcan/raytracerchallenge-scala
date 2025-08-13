package com.samuelcantrell.raytracer.ray

import com.samuelcantrell.raytracer.tuple
import com.samuelcantrell.raytracer.matrix

case class Ray(origin: tuple.Tuple, direction: tuple.Tuple)

def ray(origin: tuple.Tuple, direction: tuple.Tuple): Ray = {
  Ray(origin, direction)
}

def position(r: Ray, t: Double): tuple.Tuple = {
  tuple.add(r.origin, tuple.multiply(r.direction, t))
}

def transform(r: Ray, m: matrix.Matrix): Ray = {
  Ray(m * r.origin, m * r.direction)
}
