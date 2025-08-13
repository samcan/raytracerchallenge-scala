import com.samuelcantrell.raytracer.material._
import com.samuelcantrell.raytracer.color

class MaterialSuite extends munit.FunSuite {

  test("The default material") {
    val m = material()
    val expectedColor = color.Color(1, 1, 1)

    assertEquals(color.isEqual(m.materialColor, expectedColor), true)
    assertEquals(m.ambient, 0.1)
    assertEquals(m.diffuse, 0.9)
    assertEquals(m.specular, 0.9)
    assertEquals(m.shininess, 200.0)
  }
}
