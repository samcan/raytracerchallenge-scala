class ColorSuite extends munit.FunSuite {
  test("Colors are (red, green, blue) tuples") {
    val obtained = Color(-0.5, 0.4, 1.7)
    assertEquals(obtained.red, -0.5)
    assertEquals(obtained.green, 0.4)
    assertEquals(obtained.blue, 1.7)
  }

  test("Adding colors") {
    val obtained = add(Color(0.9, 0.6, 0.75), Color(0.7, 0.1, 0.25))
    val expected = Color(1.6, 0.7, 1.0)
    assertEquals(isEqual(obtained, expected), true)
  }

  test("Subtracting colors") {
    val obtained = subtract(Color(0.9, 0.6, 0.75), Color(0.7, 0.1, 0.25))
    val expected = Color(0.2, 0.5, 0.5)
    assertEquals(isEqual(obtained, expected), true)
  }

  test("Multiplying a color by a scalar") {
    val obtained = multiply(Color(0.2, 0.3, 0.4), 2)
    val expected = Color(0.4, 0.6, 0.8)
    assertEquals(isEqual(obtained, expected), true)
  }

  test("Multiplying colors") {
    val obtained = multiply(Color(1, 0.2, 0.4), Color(0.9, 1, 0.1))
    val expected = Color(0.9, 0.2, 0.04)
    assertEquals(isEqual(obtained, expected), true)
  }

  test("isEqual() returns true for matching colors") {
    val a = Color(3.3, 4.5, -1.2)
    val b = Color(3.3, 4.5, -1.2)

    assertEquals(isEqual(a, b), true)
  }

  test("isEqual() returns true for floating-point error colors") {
    val a = Color(3.3, 4.5, -1.2)
    val b = Color(3.3000000001, 4.500000000005, -1.200000003)

    assertEquals(isEqual(a, b), true)
  }

  test("isEqual() returns false for non-matching colors") {
    val a = Color(3.3, 4.5, -1.2)
    val b = Color(5.9, 4.5, -1.2)

    assertEquals(isEqual(a, b), false)
  }
}
