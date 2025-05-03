package com.samuelcantrell.raytracer.canvas

import com.samuelcantrell.raytracer.color

class Canvas(
    var width: Int,
    var height: Int,
    c: color.Color = color.Color(0, 0, 0)
):
  private var _data =
    Array.fill[color.Color](height, width)(c)

  def writePixel(x: Int, y: Int, c: color.Color): Boolean = {
    if (x < 0 || x > cols() || y < 0 || y > rows()) {
      false
    } else {
      _data(y)(x) = c
      true
    }
  }

  def pixelAt(x: Int, y: Int): color.Color = {
    _data(y)(x)
  }

  def rows(): Int = _data.length
  def cols(): Int = _data(0).length
end Canvas
