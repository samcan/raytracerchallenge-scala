import com.samuelcantrell.raytracer.world._
import com.samuelcantrell.raytracer.sphere
import com.samuelcantrell.raytracer.light
import com.samuelcantrell.raytracer.ray
import com.samuelcantrell.raytracer.tuple
import com.samuelcantrell.raytracer.color
import com.samuelcantrell.raytracer.material
import com.samuelcantrell.raytracer.transformation

class WorldSuite extends munit.FunSuite {

  test("Creating a world") {
    val w = world()

    assertEquals(w.objects.isEmpty, true)
    assertEquals(w.lightSource.isEmpty, true)
  }

  test("The default world") {
    val expectedLight = light.pointLight(
      tuple.makePoint(-10, 10, -10),
      color.Color(1, 1, 1)
    )

    val expectedS1Material = material.Material(
      materialColor = color.Color(0.8, 1.0, 0.6),
      ambient = 0.1,
      diffuse = 0.7,
      specular = 0.2,
      shininess = 200.0
    )

    val expectedS2Transform = transformation.scaling(0.5, 0.5, 0.5)

    val w = defaultWorld()

    assertEquals(w.lightSource.isDefined, true)
    assertEquals(w.lightSource.get.position, expectedLight.position)
    assertEquals(
      color.isEqual(w.lightSource.get.intensity, expectedLight.intensity),
      true
    )

    assertEquals(w.objects.length, 2)

    // Check first sphere's material
    val s1 = w.objects(0)
    assertEquals(
      color.isEqual(
        s1.objectMaterial.materialColor,
        expectedS1Material.materialColor
      ),
      true
    )
    assertEquals(s1.objectMaterial.diffuse, expectedS1Material.diffuse)
    assertEquals(s1.objectMaterial.specular, expectedS1Material.specular)

    // Check second sphere's transform
    val s2 = w.objects(1)
    assertEquals(
      com.samuelcantrell.raytracer.matrix
        .isEqual(s2.transform, expectedS2Transform),
      true
    )
  }

  test("Intersect a world with a ray") {
    val w = defaultWorld()
    val r = ray.ray(tuple.makePoint(0, 0, -5), tuple.makeVector(0, 0, 1))

    val xs = intersectWorld(w, r)

    assertEquals(xs.count, 4)
    assertEquals(xs(0).t, 4.0)
    assertEquals(xs(1).t, 4.5)
    assertEquals(xs(2).t, 5.5)
    assertEquals(xs(3).t, 6.0)
  }

  test("World contains spheres") {
    val w = defaultWorld()
    val s1 = w.objects(0)
    val s2 = w.objects(1)

    assertEquals(contains(w, s1), true)
    assertEquals(contains(w, s2), true)
  }

  test("World does not contain random sphere") {
    val w = defaultWorld()
    val randomSphere = sphere.sphere()

    assertEquals(contains(w, randomSphere), false)
  }
}
