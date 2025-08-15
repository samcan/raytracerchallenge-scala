import com.samuelcantrell.raytracer.sphere
import com.samuelcantrell.raytracer.transformation
import com.samuelcantrell.raytracer.material
import com.samuelcantrell.raytracer.color
import com.samuelcantrell.raytracer.world
import com.samuelcantrell.raytracer.light
import com.samuelcantrell.raytracer.tuple
import com.samuelcantrell.raytracer.camera
import com.samuelcantrell.raytracer.ppmfile

@main def raytrace(): Unit = {
  println("Creating raytraced scene...")

  // Create the floor
  val floor = sphere.sphere()
  val floorTransformed = sphere.setTransform(
    floor,
    transformation.scaling(10, 0.01, 10)
  )
  val floorMaterial = material
    .material()
    .copy(
      materialColor = color.Color(1, 0.9, 0.9),
      specular = 0
    )
  val floorWithMaterial = sphere.setMaterial(floorTransformed, floorMaterial)

  // Create the left wall
  val leftWall = sphere.sphere()
  val leftWallTransform = transformation.translation(0, 0, 5) *
    transformation.rotation_y(-math.Pi / 4) *
    transformation.rotation_x(math.Pi / 2) *
    transformation.scaling(10, 0.01, 10)
  val leftWallTransformed = sphere.setTransform(leftWall, leftWallTransform)
  val leftWallWithMaterial =
    sphere.setMaterial(leftWallTransformed, floorMaterial)

  // Create the right wall
  val rightWall = sphere.sphere()
  val rightWallTransform = transformation.translation(0, 0, 5) *
    transformation.rotation_y(math.Pi / 4) *
    transformation.rotation_x(math.Pi / 2) *
    transformation.scaling(10, 0.01, 10)
  val rightWallTransformed = sphere.setTransform(rightWall, rightWallTransform)
  val rightWallWithMaterial =
    sphere.setMaterial(rightWallTransformed, floorMaterial)

  // Create the middle sphere
  val middle = sphere.sphere()
  val middleTransformed = sphere.setTransform(
    middle,
    transformation.translation(-0.5, 1, 0.5)
  )
  val middleMaterial = material
    .material()
    .copy(
      materialColor = color.Color(0.1, 1, 0.5),
      diffuse = 0.7,
      specular = 0.3
    )
  val middleWithMaterial = sphere.setMaterial(middleTransformed, middleMaterial)

  // Create the right sphere
  val right = sphere.sphere()
  val rightTransform = transformation.translation(1.5, 0.5, -0.5) *
    transformation.scaling(0.5, 0.5, 0.5)
  val rightTransformed = sphere.setTransform(right, rightTransform)
  val rightMaterial = material
    .material()
    .copy(
      materialColor = color.Color(0.5, 1, 0.1),
      diffuse = 0.7,
      specular = 0.3
    )
  val rightWithMaterial = sphere.setMaterial(rightTransformed, rightMaterial)

  // Create the left sphere
  val left = sphere.sphere()
  val leftTransform = transformation.translation(-1.5, 0.33, -0.75) *
    transformation.scaling(0.33, 0.33, 0.33)
  val leftTransformed = sphere.setTransform(left, leftTransform)
  val leftMaterial = material
    .material()
    .copy(
      materialColor = color.Color(1, 0.8, 0.1),
      diffuse = 0.7,
      specular = 0.3
    )
  val leftWithMaterial = sphere.setMaterial(leftTransformed, leftMaterial)

  // Create the world with all objects
  val lightSource = light.pointLight(
    tuple.makePoint(-10, 10, -10),
    color.Color(1, 1, 1)
  )

  val sceneWorld = world.World(
    objects = Vector(
      floorWithMaterial,
      leftWallWithMaterial,
      rightWallWithMaterial,
      middleWithMaterial,
      rightWithMaterial,
      leftWithMaterial
    ),
    lightSource = Some(lightSource)
  )

  // Create the camera
  val cam = camera.camera(600, 300, math.Pi / 3)
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
