import com.samuelcantrell.raytracer.triangle._
import com.samuelcantrell.raytracer.tuple
import com.samuelcantrell.raytracer.ray

class TriangleSuite extends munit.FunSuite {

  test("Constructing a triangle") {
    val p1 = tuple.makePoint(0, 1, 0)
    val p2 = tuple.makePoint(-1, 0, 0) 
    val p3 = tuple.makePoint(1, 0, 0)
    val t = triangle(p1, p2, p3)
    
    assertEquals(t.p1, p1)
    assertEquals(t.p2, p2)
    assertEquals(t.p3, p3)
    assertEquals(t.e1, tuple.makeVector(-1, -1, 0))
    assertEquals(t.e2, tuple.makeVector(1, -1, 0))
    assertEquals(t.normal, tuple.makeVector(0, 0, -1))
  }

  test("Finding the normal on a triangle") {
    val t = triangle(tuple.makePoint(0, 1, 0), tuple.makePoint(-1, 0, 0), tuple.makePoint(1, 0, 0))
    val n1 = t.localNormalAt(tuple.makePoint(0, 0.5, 0))
    val n2 = t.localNormalAt(tuple.makePoint(-0.5, 0.75, 0))
    val n3 = t.localNormalAt(tuple.makePoint(0.5, 0.25, 0))
    
    assertEquals(n1, t.normal)
    assertEquals(n2, t.normal)
    assertEquals(n3, t.normal)
  }

  test("Intersecting a ray parallel to the triangle") {
    val t = triangle(tuple.makePoint(0, 1, 0), tuple.makePoint(-1, 0, 0), tuple.makePoint(1, 0, 0))
    val r = ray.ray(tuple.makePoint(0, -1, -2), tuple.makeVector(0, 1, 0))
    val xs = t.localIntersect(r)
    
    assertEquals(xs.count, 0)
  }

  test("A ray misses the p1-p3 edge") {
    val t = triangle(tuple.makePoint(0, 1, 0), tuple.makePoint(-1, 0, 0), tuple.makePoint(1, 0, 0))
    val r = ray.ray(tuple.makePoint(1, 1, -2), tuple.makeVector(0, 0, 1))
    val xs = t.localIntersect(r)
    
    assertEquals(xs.count, 0)
  }

  test("A ray misses the p1-p2 edge") {
    val t = triangle(tuple.makePoint(0, 1, 0), tuple.makePoint(-1, 0, 0), tuple.makePoint(1, 0, 0))
    val r = ray.ray(tuple.makePoint(-1, 1, -2), tuple.makeVector(0, 0, 1))
    val xs = t.localIntersect(r)
    
    assertEquals(xs.count, 0)
  }

  test("A ray misses the p2-p3 edge") {
    val t = triangle(tuple.makePoint(0, 1, 0), tuple.makePoint(-1, 0, 0), tuple.makePoint(1, 0, 0))
    val r = ray.ray(tuple.makePoint(0, -1, -2), tuple.makeVector(0, 0, 1))
    val xs = t.localIntersect(r)
    
    assertEquals(xs.count, 0)
  }

  test("A ray strikes a triangle") {
    val t = triangle(tuple.makePoint(0, 1, 0), tuple.makePoint(-1, 0, 0), tuple.makePoint(1, 0, 0))
    val r = ray.ray(tuple.makePoint(0, 0.5, -2), tuple.makeVector(0, 0, 1))
    val xs = t.localIntersect(r)
    
    assertEquals(xs.count, 1)
    assertEquals(xs(0).t, 2.0)
  }
}