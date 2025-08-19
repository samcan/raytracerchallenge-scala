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

  // Create the middle sphere
  val middle = sphere.sphere()
  val middleTransformed = sphere.setTransform(
    middle,
    transformation.translation(-0.5, 1, 0.5)
  )
  val middleMaterial = material
    .material()
    .copy(
      materialColor = color.Color(0.1, 0.1, 0.1),
      ambient = 0.0,
      diffuse = 0.0,
      specular = 0.9,
      shininess = 300,
      reflective = 0.1,
      transparency = 0.9,
      refractive_index = 1.5
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
      materialColor = color.Color(0.1, 0.1, 0.1),
      diffuse = 0.05,
      specular = 0.98,
      reflective = 0.98,
      shininess = 200
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

  // Create a cube
  val cubeShape = cube.cube()
  val cubeTransform = transformation.translation(2, 3, 1.5) *
    transformation.scaling(0.3, 0.3, 0.3) *
    transformation.rotation_y(math.Pi / 4)
  val cubeTransformed = cube.setTransform(cubeShape, cubeTransform)
  val cubeMaterial = material
    .material()
    .copy(
      materialColor = color.Color(0.8, 0.2, 0.3),
      diffuse = 0.8,
      specular = 0.2,
      reflective = 0.0
    )
  val cubeWithMaterial = cube.setMaterial(cubeTransformed, cubeMaterial)

  // Create a cylinder
  val cylinderShape = cylinder.cylinder().copy(minimum = 0, maximum = 2, closed = true)
  val cylinderTransform = transformation.translation(-2, 0, 1) *
    transformation.scaling(0.5, 1, 0.5)
  val cylinderTransformed = cylinder.setTransform(cylinderShape, cylinderTransform)
  val cylinderMaterial = material
    .material()
    .copy(
      materialColor = color.Color(0.2, 0.6, 0.9),
      diffuse = 0.8,
      specular = 0.3,
      reflective = 0.1
    )
  val cylinderWithMaterial = cylinder.setMaterial(cylinderTransformed, cylinderMaterial)

  // Create a cone
  val coneShape = cone.cone().copy(minimum = -1, maximum = 0, closed = true)
  val coneTransform = transformation.translation(0.5, 1, -2) *
    transformation.scaling(0.8, 1, 0.8)
  val coneTransformed = cone.setTransform(coneShape, coneTransform)
  val coneMaterial = material
    .material()
    .copy(
      materialColor = color.Color(0.9, 0.4, 0.1),
      diffuse = 0.7,
      specular = 0.4,
      reflective = 0.05
    )
  val coneWithMaterial = cone.setMaterial(coneTransformed, coneMaterial)

  // Create the world with all objects
  val lightSource = light.pointLight(
    tuple.makePoint(-10, 10, -10),
    color.Color(1, 1, 1)
  )

  val sceneWorld = world.World(
    objects = Vector(
      floorWithMaterial,
      middleWithMaterial,
      rightWithMaterial,
      leftWithMaterial,
      cubeWithMaterial,
      cylinderWithMaterial,
      coneWithMaterial
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
