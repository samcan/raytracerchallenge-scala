import com.samuelcantrell.raytracer.light._
import com.samuelcantrell.raytracer.color
import com.samuelcantrell.raytracer.tuple

class PointLightSuite extends munit.FunSuite {

  test("A point light has a position and intensity") {
    val intensity = color.Color(1, 1, 1)
    val position = tuple.makePoint(0, 0, 0)
    val light = pointLight(position, intensity)

    assertEquals(tuple.isEqual(light.position, position), true)
    assertEquals(color.isEqual(light.intensity, intensity), true)
  }
}
