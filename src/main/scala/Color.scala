package com.samuelcantrell.raytracer.color

import com.samuelcantrell.raytracer.equality

case class Color(red: Double, green: Double, blue: Double)

def isEqual(a: Color, b: Color, precision: Double = equality.EPSILON): Boolean =
  equality.almostEqual(a.red, b.red, precision) &&
    equality.almostEqual(a.green, b.green, precision) &&
    equality.almostEqual(a.blue, b.blue, precision)

def add(a: Color, b: Color): Color =
  Color(a.red + b.red, a.green + b.green, a.blue + b.blue)

def subtract(a: Color, b: Color): Color =
  Color(a.red - b.red, a.green - b.green, a.blue - b.blue)

def multiply(a: Color, b: Double): Color =
  Color(a.red * b, a.green * b, a.blue * b)

def multiply(a: Color, b: Color): Color =
  Color(a.red * b.red, a.green * b.green, a.blue * b.blue)
