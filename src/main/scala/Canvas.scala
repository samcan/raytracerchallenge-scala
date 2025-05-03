package com.samuelcantrell.raytracer.canvas

import com.samuelcantrell.raytracer.color

class Canvas(var width: Int, var height: Int):
  private var _data = Array.ofDim[color.Color](height, width)
end Canvas
