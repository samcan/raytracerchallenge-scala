package com.samuelcantrell.raytracer.ppmfile

import com.samuelcantrell.raytracer.canvas
import com.samuelcantrell.raytracer.color

val MIN_RGB_COLOR = 0
val MAX_RGB_COLOR = 255

def clampColorComponent(v: Long): Long =
  math.max(math.min(v, MAX_RGB_COLOR), MIN_RGB_COLOR)

def colorToString(c: color.Color): String = {
  clampColorComponent(math.round(c.red * MAX_RGB_COLOR)) + " " +
    clampColorComponent(math.round(c.green * MAX_RGB_COLOR)) + " " +
    clampColorComponent(math.round(c.blue * MAX_RGB_COLOR))
}

def canvasToPpm(c: canvas.Canvas): IndexedSeq[String] = {
  IndexedSeq[String]()
    .appended("P3")
    .appended(c.width + " " + c.height)
    .appended(MAX_RGB_COLOR.toString())
}
