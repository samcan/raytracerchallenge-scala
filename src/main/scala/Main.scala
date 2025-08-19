import com.samuelcantrell.raytracer.sphere
import com.samuelcantrell.raytracer.plane
import com.samuelcantrell.raytracer.cube
import com.samuelcantrell.raytracer.cylinder
import com.samuelcantrell.raytracer.cone
import com.samuelcantrell.raytracer.transformation
import com.samuelcantrell.raytracer.material
import com.samuelcantrell.raytracer.color
import com.samuelcantrell.raytracer.world
import com.samuelcantrell.raytracer.light
import com.samuelcantrell.raytracer.tuple
import com.samuelcantrell.raytracer.camera
import com.samuelcantrell.raytracer.ppmfile
import com.samuelcantrell.raytracer.pattern
import com.samuelcantrell.raytracer.shape._

// Create a hexagon component (one corner sphere and one edge cylinder)
def createHexagonComponent(): Group = {
  // Create a small sphere for the corner
  val corner = sphere.sphere()
  val cornerTransformed = sphere.setTransform(
    corner,
    transformation.translation(0, 0, -1) * transformation.scaling(
      0.25,
      0.25,
      0.25
    )
  )
  val cornerMaterial = material
    .material()
    .copy(
      materialColor = color.Color(0.8, 0.1, 0.1),
      diffuse = 0.8,
      specular = 0.3
    )
  val cornerWithMaterial = sphere.setMaterial(cornerTransformed, cornerMaterial)

  // Create a cylinder for the edge
  val edge = cylinder.cylinder().copy(minimum = 0, maximum = 1, closed = false)
  val edgeTransformed = cylinder.setTransform(
    edge,
    transformation.translation(0, 0, -1) *
      transformation.rotation_y(-math.Pi / 6) *
      transformation.rotation_z(-math.Pi / 2) *
      transformation.scaling(0.1, 1, 0.1)
  )
  val edgeMaterial = material
    .material()
    .copy(
      materialColor = color.Color(0.1, 0.8, 0.1),
      diffuse = 0.8,
      specular = 0.2
    )
  val edgeWithMaterial = cylinder.setMaterial(edgeTransformed, edgeMaterial)

  // Create group and add both components
  val componentGroup = group()
  val (groupWithCorner, _) = addChild(componentGroup, cornerWithMaterial)
  val (groupWithBoth, _) = addChild(groupWithCorner, edgeWithMaterial)

  groupWithBoth
}

// Create a complete hexagon by rotating the component 6 times
def createHexagon(): Group = {
  val hexagonGroup = group()
  val hexagonTransformed =
    setTransform(hexagonGroup, transformation.translation(0, 1, -3))

  // Create 6 copies of the component, each rotated by 60 degrees
  val angleStep = math.Pi / 3 // 60 degrees in radians

  val finalGroup = (0 until 6).foldLeft(hexagonTransformed) {
    (currentGroup, i) =>
      val component = createHexagonComponent()
      val rotatedComponent =
        setTransform(component, transformation.rotation_y(i * angleStep))
      val (updatedGroup, _) = addChild(currentGroup, rotatedComponent)
      updatedGroup
  }

  finalGroup
}

@main def raytrace(): Unit = {
  println("Creating raytraced scene...")

  // Create the floor
  val floor = plane.Plane()
  val floorMaterial = material
    .material()
    .copy(
      materialPattern =
        Some(pattern.stripePattern(color.Color(0, 0, 0), color.Color(1, 1, 1))),
      specular = 0,
      reflective = 0.2
    )
  val floorWithMaterial = plane.setMaterial(floor, floorMaterial)

  // Create the hexagon
  val hexagon = createHexagon()

  // Create the world with all objects
  val lightSource = light.pointLight(
    tuple.makePoint(-10, 10, -10),
    color.Color(1, 1, 1)
  )

  val sceneWorld = world.World(
    objects = Vector(
      floorWithMaterial,
      hexagon
    ),
    lightSource = Some(lightSource)
  )

  // Create the camera
  val cam = camera.camera(400, 300, math.Pi / 3)
  val cameraTransform = transformation.viewTransform(
    tuple.makePoint(0, 1.5, -5),
    tuple.makePoint(0, 1, 0),
    tuple.makeVector(0, 1, 0)
  )
  val cameraWithTransform = cam.copy(transform = cameraTransform)

  println("Rendering scene...")

  // Record start time
  val startTime = System.currentTimeMillis()

  // Render the scene
  val canvas = camera.render(cameraWithTransform, sceneWorld)

  // Record end time and calculate duration
  val endTime = System.currentTimeMillis()
  val renderingTime = endTime - startTime

  // Convert to PPM format
  val ppmData = ppmfile.canvasToPpm(canvas)

  // Write to file using os-lib
  val ppmContent = ppmData.mkString
  os.write.over(os.pwd / "render.ppm", ppmContent)

  println(
    s"Scene rendered successfully in ${renderingTime}ms! Output saved to render.ppm"
  )
}
