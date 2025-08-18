import com.samuelcantrell.raytracer.world._
import com.samuelcantrell.raytracer.sphere
import com.samuelcantrell.raytracer.plane
import com.samuelcantrell.raytracer.shape
import com.samuelcantrell.raytracer.light
import com.samuelcantrell.raytracer.ray
import com.samuelcantrell.raytracer.tuple
import com.samuelcantrell.raytracer.color
import com.samuelcantrell.raytracer.material
import com.samuelcantrell.raytracer.transformation
import com.samuelcantrell.raytracer.intersection
import com.samuelcantrell.raytracer.pattern

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

  test("Shading an intersection") {
    val w = defaultWorld()
    val r = ray.ray(tuple.makePoint(0, 0, -5), tuple.makeVector(0, 0, 1))
    val shape = w.objects(0) // the first object in w
    val i = intersection.intersection(4, shape)
    val comps = intersection.prepareComputations(i, r)
    val c = shadeHit(w, comps)
    val expected = color.Color(0.38066, 0.47583, 0.2855)

    assertEquals(color.isEqual(c, expected, 0.00001), true)
  }

  test("Shading an intersection from the inside") {
    val w = defaultWorld()
    val newLight =
      light.pointLight(tuple.makePoint(0, 0.25, 0), color.Color(1, 1, 1))
    val wWithNewLight = w.copy(lightSource = Some(newLight))
    val r = ray.ray(tuple.makePoint(0, 0, 0), tuple.makeVector(0, 0, 1))
    val shape = wWithNewLight.objects(1) // the second object in w
    val i = intersection.intersection(0.5, shape)
    val comps = intersection.prepareComputations(i, r)
    val c = shadeHit(wWithNewLight, comps)
    val expected = color.Color(0.90498, 0.90498, 0.90498)

    assertEquals(color.isEqual(c, expected, 0.00001), true)
  }

  test("The color when a ray misses") {
    val w = defaultWorld()
    val r = ray.ray(tuple.makePoint(0, 0, -5), tuple.makeVector(0, 1, 0))
    val c = colorAt(w, r)
    val expected = color.Color(0, 0, 0)

    assertEquals(color.isEqual(c, expected), true)
  }

  test("The color when a ray hits") {
    val w = defaultWorld()
    val r = ray.ray(tuple.makePoint(0, 0, -5), tuple.makeVector(0, 0, 1))
    val c = colorAt(w, r)
    val expected = color.Color(0.38066, 0.47583, 0.2855)

    assertEquals(color.isEqual(c, expected, 0.00001), true)
  }

  test("The color with an intersection behind the ray") {
    val w = defaultWorld()
    val outer = w.objects(0) // the first object in w
    val outerWithAmbient = shape.setMaterial(
      outer,
      outer.objectMaterial.copy(ambient = 1.0)
    )
    val inner = w.objects(1) // the second object in w
    val innerWithAmbient = shape.setMaterial(
      inner,
      inner.objectMaterial.copy(ambient = 1.0)
    )
    val wModified = w.copy(objects = Vector(outerWithAmbient, innerWithAmbient))
    val r = ray.ray(tuple.makePoint(0, 0, 0.75), tuple.makeVector(0, 0, -1))
    val c = colorAt(wModified, r)
    val expected = innerWithAmbient.objectMaterial.materialColor

    assertEquals(color.isEqual(c, expected), true)
  }

  test("There is no shadow when nothing is collinear with point and light") {
    val w = defaultWorld()
    val p = tuple.makePoint(0, 10, 0)

    assertEquals(isShadowed(w, p), false)
  }

  test("The shadow when an object is between the point and the light") {
    val w = defaultWorld()
    val p = tuple.makePoint(10, -10, 10)

    assertEquals(isShadowed(w, p), true)
  }

  test("There is no shadow when an object is behind the light") {
    val w = defaultWorld()
    val p = tuple.makePoint(-20, 20, -20)

    assertEquals(isShadowed(w, p), false)
  }

  test("There is no shadow when an object is behind the point") {
    val w = defaultWorld()
    val p = tuple.makePoint(-2, 2, -2)

    assertEquals(isShadowed(w, p), false)
  }

  test("shade_hit() is given an intersection in shadow") {
    val w = world()
    val lightSource =
      light.pointLight(tuple.makePoint(0, 0, -10), color.Color(1, 1, 1))
    val s1 = sphere.sphere()
    val s2 =
      sphere.setTransform(sphere.sphere(), transformation.translation(0, 0, 10))
    val wWithObjects =
      w.copy(objects = Vector(s1, s2), lightSource = Some(lightSource))

    val r = ray.ray(tuple.makePoint(0, 0, 5), tuple.makeVector(0, 0, 1))
    val i = intersection.intersection(4, s2)
    val comps = intersection.prepareComputations(i, r)
    val c = shadeHit(wWithObjects, comps)
    val expected = color.Color(0.1, 0.1, 0.1)

    assertEquals(color.isEqual(c, expected), true)
  }

  test("shade_hit() with a reflective material") {
    val w = defaultWorld()

    // Create a reflective plane
    val shape = plane.plane()
    val reflectiveMaterial = material.material().copy(reflective = 0.5)
    val shapeWithMaterial = plane.setMaterial(shape, reflectiveMaterial)
    val shapeWithTransform = plane.setTransform(
      shapeWithMaterial,
      transformation.translation(0, -1, 0)
    )

    // Add shape to world
    val wWithShape = w.copy(objects = w.objects :+ shapeWithTransform)

    // Create ray and intersection
    val sqrt2over2 = math.sqrt(2) / 2
    val r = ray.ray(
      tuple.makePoint(0, 0, -3),
      tuple.makeVector(0, -sqrt2over2, sqrt2over2)
    )
    val i = intersection.intersection(math.sqrt(2), shapeWithTransform)

    // Prepare computations and shade
    val comps = intersection.prepareComputations(i, r)
    val colorResult = shadeHit(wWithShape, comps)

    val expected = color.Color(0.87677, 0.92436, 0.82918)
    assertEquals(color.isEqual(colorResult, expected, 0.0001), true)
  }

  test("color_at() with mutually reflective surfaces") {
    val w = world()
    val lightSource =
      light.pointLight(tuple.makePoint(0, 0, 0), color.Color(1, 1, 1))
    val wWithLight = w.copy(lightSource = Some(lightSource))

    // Create lower plane
    val lower = plane.plane()
    val lowerMaterial = material.material().copy(reflective = 1.0)
    val lowerWithMaterial = plane.setMaterial(lower, lowerMaterial)
    val lowerWithTransform = plane.setTransform(
      lowerWithMaterial,
      transformation.translation(0, -1, 0)
    )

    // Create upper plane
    val upper = plane.plane()
    val upperMaterial = material.material().copy(reflective = 1.0)
    val upperWithMaterial = plane.setMaterial(upper, upperMaterial)
    val upperWithTransform =
      plane.setTransform(upperWithMaterial, transformation.translation(0, 1, 0))

    // Add both planes to world
    val wWithPlanes =
      wWithLight.copy(objects = Vector(lowerWithTransform, upperWithTransform))

    // Create ray
    val r = ray.ray(tuple.makePoint(0, 0, 0), tuple.makeVector(0, 1, 0))

    // This should terminate successfully (not hang in infinite recursion)
    val colorResult = colorAt(wWithPlanes, r)

    // Just verify it doesn't hang - the exact color isn't specified in the test
    assert(colorResult != null)
  }

  test("The reflected color at the maximum recursive depth") {
    val w = defaultWorld()

    // Create a reflective plane
    val shape = plane.plane()
    val reflectiveMaterial = material.material().copy(reflective = 0.5)
    val shapeWithMaterial = plane.setMaterial(shape, reflectiveMaterial)
    val shapeWithTransform = plane.setTransform(
      shapeWithMaterial,
      transformation.translation(0, -1, 0)
    )

    // Add shape to world
    val wWithShape = w.copy(objects = w.objects :+ shapeWithTransform)

    // Create ray and intersection
    val sqrt2over2 = math.sqrt(2) / 2
    val r = ray.ray(
      tuple.makePoint(0, 0, -3),
      tuple.makeVector(0, -sqrt2over2, sqrt2over2)
    )
    val i = intersection.intersection(math.sqrt(2), shapeWithTransform)

    // Prepare computations
    val comps = intersection.prepareComputations(i, r)

    // Call reflected_color with remaining = 0
    val colorResult = reflectedColor(wWithShape, comps, 0)

    val expected = color.Color(0, 0, 0)
    assertEquals(color.isEqual(colorResult, expected), true)
  }

  test("The refracted color with an opaque surface") {
    val w = defaultWorld()
    val shape = w.objects(0) // the first object in w
    val r = ray.ray(tuple.makePoint(0, 0, -5), tuple.makeVector(0, 0, 1))
    val xs = intersection.intersections(
      intersection.intersection(4, shape),
      intersection.intersection(6, shape)
    )

    val comps = intersection.prepareComputations(xs(0), r, xs)
    val c = refracted_color(w, comps, 5)

    assertEquals(color.isEqual(c, color.Color(0, 0, 0)), true)
  }

  test("The refracted color at the maximum recursive depth") {
    val w = defaultWorld()
    val shape = w.objects(0) // the first object in w

    // Modify the shape to have transparency and refractive index
    val transparentMaterial = shape.objectMaterial.copy(
      transparency = 1.0,
      refractive_index = 1.5
    )
    val transparentShape = shape.withMaterial(transparentMaterial)

    // Update the world with the modified shape
    val wWithTransparentShape =
      w.copy(objects = w.objects.updated(0, transparentShape))

    val r = ray.ray(tuple.makePoint(0, 0, -5), tuple.makeVector(0, 0, 1))
    val xs = intersection.intersections(
      intersection.intersection(4, transparentShape),
      intersection.intersection(6, transparentShape)
    )

    val comps = intersection.prepareComputations(xs(0), r, xs)
    val c = refracted_color(wWithTransparentShape, comps, 0)

    assertEquals(color.isEqual(c, color.Color(0, 0, 0)), true)
  }

  test("The refracted color under total internal reflection") {
    val w = defaultWorld()
    val shape = w.objects(0) // the first object in w

    // Modify the shape to have transparency and refractive index
    val transparentMaterial = shape.objectMaterial.copy(
      transparency = 1.0,
      refractive_index = 1.5
    )
    val transparentShape = shape.withMaterial(transparentMaterial)

    // Update the world with the modified shape
    val wWithTransparentShape =
      w.copy(objects = w.objects.updated(0, transparentShape))

    val sqrt2over2 = math.sqrt(2) / 2
    val r =
      ray.ray(tuple.makePoint(0, 0, sqrt2over2), tuple.makeVector(0, 1, 0))
    val xs = intersection.intersections(
      intersection.intersection(-sqrt2over2, transparentShape),
      intersection.intersection(sqrt2over2, transparentShape)
    )

    // NOTE: this time you're inside the sphere, so you need
    // to look at the second intersection, xs[1], not xs[0]
    val comps = intersection.prepareComputations(xs(1), r, xs)
    val c = refracted_color(wWithTransparentShape, comps, 5)

    assertEquals(color.isEqual(c, color.Color(0, 0, 0)), true)
  }

  test("The refracted color with a refracted ray") {
    val w = defaultWorld()
    val A = w.objects(0) // the first object in w
    val B = w.objects(1) // the second object in w

    // Modify A to have ambient = 1.0 and test pattern
    val AMaterial = A.objectMaterial.copy(
      ambient = 1.0,
      materialPattern = Some(pattern.testPattern())
    )
    val AModified = A.withMaterial(AMaterial)

    // Modify B to have transparency and refractive index
    val BMaterial = B.objectMaterial.copy(
      transparency = 1.0,
      refractive_index = 1.5
    )
    val BModified = B.withMaterial(BMaterial)

    // Update the world with modified objects
    val wModified = w.copy(objects = Vector(AModified, BModified))

    val r = ray.ray(tuple.makePoint(0, 0, 0.1), tuple.makeVector(0, 1, 0))
    val xs = intersection.intersections(
      intersection.intersection(-0.9899, AModified),
      intersection.intersection(-0.4899, BModified),
      intersection.intersection(0.4899, BModified),
      intersection.intersection(0.9899, AModified)
    )

    val comps = intersection.prepareComputations(xs(2), r, xs)
    val c = refracted_color(wModified, comps, 5)

    assertEquals(
      color.isEqual(c, color.Color(0, 0.99888, 0.04725), 0.0001),
      true
    )
  }

  test("shade_hit() with a transparent material") {
    val w = defaultWorld()

    // Create floor plane
    val floor = plane.plane()
    val floorMaterial = material
      .material()
      .copy(
        transparency = 0.5,
        refractive_index = 1.5
      )
    val floorWithMaterial = plane.setMaterial(floor, floorMaterial)
    val floorWithTransform = plane.setTransform(
      floorWithMaterial,
      transformation.translation(0, -1, 0)
    )

    // Create ball sphere
    val ball = sphere.sphere()
    val ballMaterial = material
      .material()
      .copy(
        materialColor = color.Color(1, 0, 0),
        ambient = 0.5
      )
    val ballWithMaterial = sphere.setMaterial(ball, ballMaterial)
    val ballWithTransform = sphere.setTransform(
      ballWithMaterial,
      transformation.translation(0, -3.5, -0.5)
    )

    // Add floor and ball to world
    val wWithObjects = w.copy(objects =
      w.objects ++ Vector(floorWithTransform, ballWithTransform)
    )

    val sqrt2over2 = math.sqrt(2) / 2
    val r = ray.ray(
      tuple.makePoint(0, 0, -3),
      tuple.makeVector(0, -sqrt2over2, sqrt2over2)
    )
    val xs = intersection.intersections(
      intersection.intersection(math.sqrt(2), floorWithTransform)
    )

    val comps = intersection.prepareComputations(xs(0), r, xs)
    val colorResult = shadeHit(wWithObjects, comps, 5)

    assertEquals(
      color
        .isEqual(colorResult, color.Color(0.93642, 0.68642, 0.68642), 0.0001),
      true
    )
  }

  test("shade_hit() with a reflective, transparent material") {
    val w = defaultWorld()

    // Create floor plane with both reflective and transparent properties
    val floor = plane.plane()
    val floorMaterial = material
      .material()
      .copy(
        reflective = 0.5,
        transparency = 0.5,
        refractive_index = 1.5
      )
    val floorWithMaterial = plane.setMaterial(floor, floorMaterial)
    val floorWithTransform = plane.setTransform(
      floorWithMaterial,
      transformation.translation(0, -1, 0)
    )

    // Create ball sphere
    val ball = sphere.sphere()
    val ballMaterial = material
      .material()
      .copy(
        materialColor = color.Color(1, 0, 0),
        ambient = 0.5
      )
    val ballWithMaterial = sphere.setMaterial(ball, ballMaterial)
    val ballWithTransform = sphere.setTransform(
      ballWithMaterial,
      transformation.translation(0, -3.5, -0.5)
    )

    // Add floor and ball to world
    val wWithObjects = w.copy(objects =
      w.objects ++ Vector(floorWithTransform, ballWithTransform)
    )

    val sqrt2over2 = math.sqrt(2) / 2
    val r = ray.ray(
      tuple.makePoint(0, 0, -3),
      tuple.makeVector(0, -sqrt2over2, sqrt2over2)
    )
    val xs = intersection.intersections(
      intersection.intersection(math.sqrt(2), floorWithTransform)
    )

    val comps = intersection.prepareComputations(xs(0), r, xs)
    val colorResult = shadeHit(wWithObjects, comps, 5)

    assertEquals(
      color
        .isEqual(colorResult, color.Color(0.93391, 0.69643, 0.69243), 0.0001),
      true
    )
  }

  test("Transparent sphere should transmit light to background plane") {
    // Create a world with only a light source (no default objects)
    val lightSource =
      light.pointLight(tuple.makePoint(-10, 10, -10), color.Color(1, 1, 1))
    val w = world().copy(lightSource = Some(lightSource))

    // Create a completely transparent sphere at origin
    val transparentSphere = sphere.sphere()
    val transparentMaterial = material
      .material()
      .copy(
        ambient = 0.0,
        diffuse = 0.0,
        specular = 0.0,
        transparency = 1.0,
        refractive_index = 1.0 // Same as air, so no refraction bending
      )
    val sphereWithMaterial =
      sphere.setMaterial(transparentSphere, transparentMaterial)

    // Create a white plane behind the sphere (rotated 90 degrees on X axis)
    val backgroundPlane = plane.plane()
    val planeMaterial = material
      .material()
      .copy(
        materialColor = color.Color(1, 1, 1),
        ambient =
          0.9, // High ambient so it's visible even without direct lighting
        diffuse = 0.1,
        specular = 0.0
      )
    val planeWithMaterial = plane.setMaterial(backgroundPlane, planeMaterial)
    val planeTransform =
      transformation.translation(0, 0, 6) * transformation.rotation_x(
        math.Pi / 2
      )
    val planeWithTransform =
      plane.setTransform(planeWithMaterial, planeTransform)

    // Add both objects to world
    val wWithObjects =
      w.copy(objects = Vector(sphereWithMaterial, planeWithTransform))

    // Shoot ray from (0, 0, -10) directly into sphere
    val r = ray.ray(tuple.makePoint(0, 0, -10), tuple.makeVector(0, 0, 1))
    val resultColor = colorAt(wWithObjects, r)

    // Should NOT be black - should show some light from the white plane behind
    val isBlack = color.isEqual(resultColor, color.Color(0, 0, 0), 0.0001)
    assertEquals(
      isBlack,
      false,
      s"Ray through transparent sphere returned black: $resultColor"
    )

    // Should be some shade of gray (the white plane with ambient lighting)
    val expectedMinIntensity = 0.1 // Should be at least this bright
    val actualIntensity =
      (resultColor.red + resultColor.green + resultColor.blue) / 3.0
    assert(
      actualIntensity > expectedMinIntensity,
      s"Expected light intensity > $expectedMinIntensity, got $actualIntensity. Color: $resultColor"
    )
  }

  test(
    "colorAt() correctly calculates n1 and n2 for nested transparent objects"
  ) {
    // This test verifies that colorAt() passes intersections to prepareComputations()
    // which is critical for correct n1/n2 calculation in transparency scenarios

    val lightSource =
      light.pointLight(tuple.makePoint(-10, 10, -10), color.Color(1, 1, 1))
    val w = world().copy(lightSource = Some(lightSource))

    // Create a simple test scenario: just use glass spheres with a background
    // The key is to verify that intersections are passed to prepareComputations

    // Create a glass sphere
    val glassSphere = sphere.glass_sphere()

    // Create a bright background plane positioned behind the sphere
    val background = plane.plane()
    val backgroundMaterial = material
      .material()
      .copy(
        materialColor = color.Color(1, 1, 1),
        ambient = 0.9, // High ambient so it's bright even in shadow
        diffuse = 0.1,
        specular = 0.0
      )
    val backgroundWithMaterial =
      plane.setMaterial(background, backgroundMaterial)
    // Position the plane vertically at z=3 (behind sphere)
    val backgroundTransformed = plane.setTransform(
      backgroundWithMaterial,
      transformation.translation(0, 0, 3) * transformation.rotation_x(
        math.Pi / 2
      )
    )

    val wWithObjects =
      w.copy(objects = Vector(glassSphere, backgroundTransformed))

    // Ray through the glass sphere to the background
    val r = ray.ray(tuple.makePoint(0, 0, -3), tuple.makeVector(0, 0, 1))
    val resultColor = colorAt(wWithObjects, r)

    // The critical test: if colorAt() correctly passes intersections to prepareComputations(),
    // we should get non-black color from the background through the glass
    val isBlack = color.isEqual(resultColor, color.Color(0, 0, 0), 0.01)
    assertEquals(
      isBlack,
      false,
      s"Glass sphere with background returned black, indicating n1/n2 calculation failure: $resultColor"
    )

    // Should see some light from the bright white background
    val brightness =
      (resultColor.red + resultColor.green + resultColor.blue) / 3.0
    assert(
      brightness > 0.05,
      s"Expected some brightness from background, got $brightness. Color: $resultColor"
    )
  }

  test("colorAt() with single transparent sphere shows refraction effects") {
    // Test that verifies colorAt() properly handles transparency by showing
    // that light is refracted through a glass sphere to reach a background

    val lightSource =
      light.pointLight(tuple.makePoint(0, 10, 0), color.Color(1, 1, 1))
    val w = world().copy(lightSource = Some(lightSource))

    // Create a glass sphere
    val glassSphere = sphere.glass_sphere()

    // Create a colored background plane to show refraction
    val background = plane.plane()
    val backgroundMaterial = material
      .material()
      .copy(
        materialColor = color.Color(0.8, 0.2, 0.2), // Red background
        ambient = 0.7,
        diffuse = 0.3
      )
    val backgroundWithMaterial =
      plane.setMaterial(background, backgroundMaterial)
    val backgroundTransformed = plane.setTransform(
      backgroundWithMaterial,
      transformation.translation(0, 0, 5)
    )

    val wWithObjects =
      w.copy(objects = Vector(glassSphere, backgroundTransformed))

    // Ray through the center of the glass sphere
    val r = ray.ray(tuple.makePoint(0, 0, -3), tuple.makeVector(0, 0, 1))
    val resultColor = colorAt(wWithObjects, r)

    // Should show some red from the background through the glass
    assert(
      resultColor.red > 0.1,
      s"Expected red component > 0.1 from background refraction, got ${resultColor.red}"
    )

    // Should not be completely black
    val totalIntensity = resultColor.red + resultColor.green + resultColor.blue
    assert(
      totalIntensity > 0.1,
      s"Expected total intensity > 0.1, got $totalIntensity. Color: $resultColor"
    )
  }
}
