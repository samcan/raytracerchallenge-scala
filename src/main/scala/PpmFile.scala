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

def splitLineAt70Chars(line: String): IndexedSeq[String] = {
  if (line.length <= 70) {
    IndexedSeq(line + "\n")
  } else {
    val lastSpaceIndex = line.lastIndexOf(' ', 70)
    if (lastSpaceIndex == -1) {
      // No space found within 70 characters, just split at 70
      IndexedSeq(line.substring(0, 70) + "\n") ++ splitLineAt70Chars(
        line.substring(70)
      )
    } else {
      // Split at the last space within 70 characters
      IndexedSeq(
        line.substring(0, lastSpaceIndex) + "\n"
      ) ++ splitLineAt70Chars(
        line.substring(lastSpaceIndex + 1)
      )
    }
  }
}

def canvasToPpm(c: canvas.Canvas): IndexedSeq[String] = {
  val header = IndexedSeq[String]()
    .appended("P3\n")
    .appended(c.width + " " + c.height + "\n")
    .appended(MAX_RGB_COLOR.toString() + "\n")

  val pixelData = (0 until c.rows()).flatMap { row =>
    val rowString = (0 until c.cols())
      .map { col =>
        colorToString(c.pixelAt(col, row))
      }
      .mkString(" ")
    splitLineAt70Chars(rowString)
  }

  header ++ pixelData
}
