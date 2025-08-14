package com.samuelcantrell.raytracer.camera

import com.samuelcantrell.raytracer.matrix
import com.samuelcantrell.raytracer.ray
import com.samuelcantrell.raytracer.tuple
import com.samuelcantrell.raytracer.canvas
import com.samuelcantrell.raytracer.world

case class Camera(
    hsize: Int,
    vsize: Int,
    fieldOfView: Double,
    transform: matrix.Matrix = matrix.Matrix.identity(),
    pixelSize: Double
)

object Camera {
  def apply(hsize: Int, vsize: Int, fieldOfView: Double): Camera = {
    val halfView = math.tan(fieldOfView / 2)
    val aspect = hsize.toDouble / vsize.toDouble

    val (halfWidth, halfHeight) = if (aspect >= 1) {
      (halfView, halfView / aspect)
    } else {
      (halfView * aspect, halfView)
    }

    val pixelSize = (halfWidth * 2) / hsize

    Camera(hsize, vsize, fieldOfView, matrix.Matrix.identity(), pixelSize)
  }
}

def camera(hsize: Int, vsize: Int, fieldOfView: Double): Camera = {
  Camera(hsize, vsize, fieldOfView)
}

def rayForPixel(c: Camera, px: Int, py: Int): ray.Ray = {
  // The offset from the edge of the canvas to the pixel's center
  val xOffset = (px + 0.5) * c.pixelSize
  val yOffset = (py + 0.5) * c.pixelSize

  // The untransformed coordinates of the pixel in world space
  // (remember that the camera looks toward -z, so +x is to the *left*)
  val halfWidth = c.hsize * c.pixelSize / 2
  val halfHeight = c.vsize * c.pixelSize / 2
  val worldX = halfWidth - xOffset
  val worldY = halfHeight - yOffset

  // Using the camera matrix, transform the canvas point and the origin,
  // and then compute the ray's direction vector
  // (remember that the canvas is at z=-1)
  val cameraInverse = c.transform.inverse
  val pixel = cameraInverse * tuple.makePoint(worldX, worldY, -1)
  val origin = cameraInverse * tuple.makePoint(0, 0, 0)
  val direction = tuple.normalize(tuple.subtract(pixel, origin))

  ray.ray(origin, direction)
}

def render(c: Camera, w: world.World): canvas.Canvas = {
  val image = canvas.Canvas(c.hsize, c.vsize)

  for {
    y <- 0 until c.vsize
    x <- 0 until c.hsize
  } {
    val r = rayForPixel(c, x, y)
    val color = world.colorAt(w, r)
    image.writePixel(x, y, color)
  }

  image
}
