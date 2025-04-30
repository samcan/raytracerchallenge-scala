class ColorSuite extends munit.FunSuite {
  test("Colors are (red, green, blue) tuples") {
    val obtained = Color(-0.5, 0.4, 1.7)
    assertEquals(obtained.red, -0.5)
    assertEquals(obtained.green, 0.4)
    assertEquals(obtained.blue, 1.7)
  }
}
