case class Color(red: Double, green: Double, blue: Double)

def isEqual(a: Color, b: Color): Boolean =
  almostEqual(a.red, b.red) && almostEqual(a.green, b.green) &&
    almostEqual(a.blue, b.blue)

def add(a: Color, b: Color): Color =
  Color(a.red + b.red, a.green + b.green, a.blue + b.blue)

def subtract(a: Color, b: Color): Color =
  Color(a.red - b.red, a.green - b.green, a.blue - b.blue)

def multiply(a: Color, b: Double): Color =
  Color(a.red * b, a.green * b, a.blue * b)

def multiply(a: Color, b: Color): Color =
  Color(a.red * b.red, a.green * b.green, a.blue * b.blue)
