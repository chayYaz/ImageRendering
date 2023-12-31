package lighting;

import org.junit.jupiter.api.Test;

import geometries.*;
import primitives.*;
import renderer.*;
import scene.Scene;

import java.util.LinkedList;

import static java.awt.Color.*;

/**
 * Test rendering a basic image
 *
 * @author Dan
 */
public class LightsTests {

    private Scene scene2 = new Scene.SceneBuilder("Test scene") //
            .setAmbientLight(new AmbientLight(new Color(WHITE), new Double3(0.15))).build();
    private Camera camera1 = new Camera(new Point(0, 0, 1000), new Vector(0, 0, -1), new Vector(0, 1, 0)) //
            .setVPSize(150, 150) //
            .setVPDistance(1000);
    private Camera camera2 = new Camera(new Point(0, 0, 1000), new Vector(0, 0, -1), new Vector(0, 1, 0)) //
            .setVPSize(200, 200) //
            .setVPDistance(1000);

    private Point[] p = { // The Triangles' vertices:
            new Point(-110, -110, -150), // the shared left-bottom
            new Point(95, 100, -150), // the shared right-top
            new Point(110, -110, -150), // the right-bottom
            new Point(-75, 78, 100) }; // the left-top
    private Point trPL = new Point(30, 10, -100); // Triangles test Position of Light
    private Point spPL = new Point(-50, -50, 25); // Sphere test Position of Light
    private Color trCL = new Color(800, 500, 250); // Triangles test Color of Light
    private Color spCL = new Color(800, 500, 0); // Sphere test Color of Light
    private Vector trDL = new Vector(-2, -2, -2); // Triangles test Direction of Light
    private Material material = new Material().setKd(0.5).setKs(0.5).setShininess(300);
    private Geometry triangle1 = new Triangle(p[0], p[1], p[2]).setMaterial(material);
    private Geometry triangle2 = new Triangle(p[0], p[1], p[3]).setMaterial(material);
    private Geometry sphere = new Sphere(new Point(0, 0, -50), 50d) //
            .setEmission(new Color(BLUE).reduce(2)) //
            .setMaterial(new Material().setKd(0.5).setKs(0.5).setShininess(300));

    /**
     * Produce a picture of a sphere lighted by a directional light
     */
    @Test
    public void sphereDirectional() {
       Geometries geometries = new Geometries(sphere);
       LinkedList<LightSource> lights = new LinkedList<>();
       lights.add(new DirectionalLight(spCL, new Vector(1, 1, -0.5)));

        Scene scene1 = new Scene.SceneBuilder("Test scene").setGeometries(geometries)
                .setLights(lights).build();

        ImageWriter imageWriter = new ImageWriter("lightSphereDirectional", 500, 500);
        camera1.setImageWriter(imageWriter) //
                .setRayTracer(new RayTracerBasic(scene1)) //
                .renderImage() //
                .writeToImage(); //
    }

    /**
     * Produce a picture of a sphere lighted by a point light
     */
    @Test
    public void spherePoint() {

        Geometries geometries = new Geometries(sphere);
        LinkedList<LightSource> lights = new LinkedList<>();
        lights.add(new PointLight(spCL, spPL).setKl(0.001).setKq(0.0002));

        Scene scene1 = new Scene.SceneBuilder("Test scene").setGeometries(geometries)
                .setLights(lights).build();

        ImageWriter imageWriter = new ImageWriter("lightSpherePoint", 500, 500);
        camera1.setImageWriter(imageWriter) //
                .setRayTracer(new RayTracerBasic(scene1)) //
                .renderImage() //
                .writeToImage(); //
    }

    /**
     * Produce a picture of a sphere lighted by a spot light
     */
    @Test
    public void sphereSpot() {
        Geometries geometries = new Geometries(sphere);
        LinkedList<LightSource> lights = new LinkedList<>();
        lights.add(new Spotlight(spCL, spPL, new Vector(1, 1, -0.5)).setKl(0.001).setKq(0.0001));

        Scene scene1 = new Scene.SceneBuilder("Test scene").setGeometries(geometries)
                .setLights(lights).build();

        ImageWriter imageWriter = new ImageWriter("lightSphereSpot", 500, 500);
        camera1.setImageWriter(imageWriter) //
                .setRayTracer(new RayTracerBasic(scene1)) //
                .renderImage() //
                .writeToImage(); //
    }

    /**
     * Produce a picture of a two triangles lighted by a directional light
     */
    @Test
    public void trianglesDirectional() {
        Geometries geometries = new Geometries(triangle1, triangle2);
        LinkedList<LightSource> lights = new LinkedList<>();
        lights.add(new DirectionalLight(trCL, trDL));

        Scene scene1 = new Scene.SceneBuilder("Test scene").setGeometries(geometries)
                .setLights(lights).build();

        ImageWriter imageWriter = new ImageWriter("lightTrianglesDirectional", 500, 500);
        camera2.setImageWriter(imageWriter) //
                .setRayTracer(new RayTracerBasic(scene1)) //
                .renderImage() //
                .writeToImage(); //
    }

    /**
     * Produce a picture of a two triangles lighted by a point light
     */
    @Test
    public void trianglesPoint() {

        Geometries geometries = new Geometries(triangle1, triangle2);
        LinkedList<LightSource> lights = new LinkedList<>();
        lights.add(new PointLight(trCL, trPL).setKl(0.001).setKq(0.0002));

        Scene scene2 = new Scene.SceneBuilder("Test scene").setGeometries(geometries)
                .setLights(lights).build();

        ImageWriter imageWriter = new ImageWriter("lightTrianglesPoint", 500, 500);
        camera2.setImageWriter(imageWriter) //
                .setRayTracer(new RayTracerBasic(scene2)) //
                .renderImage() //
                .writeToImage(); //
    }

    /**
     * Produce a picture of a two triangles lighted by a spot light
     */
    @Test
    public void trianglesSpot() {

        Geometries geometries = new Geometries(triangle1, triangle2);
        LinkedList<LightSource> lights = new LinkedList<>();
        lights.add(new Spotlight(trCL, trPL, trDL).setKl(0.001).setKq(0.0001));

        Scene scene2 = new Scene.SceneBuilder("Test scene").setGeometries(geometries)
                .setLights(lights).build();

        ImageWriter imageWriter = new ImageWriter("lightTrianglesSpot", 500, 500);
        camera2.setImageWriter(imageWriter) //
                .setRayTracer(new RayTracerBasic(scene2)) //
                .renderImage() //
                .writeToImage(); //
    }

    /**
     * Produce a picture of a sphere lighted by a narrow spot light
     */
    @Test
    public void sphereSpotSharp() {
        Geometries geometries = new Geometries(sphere);
        LinkedList<LightSource> lights = new LinkedList<>();
        lights.add(new Spotlight(spCL, spPL, new Vector(1, 1, -0.5)).setNarrowBeam(10).setKl(0.001).setKq(0.00004));

        Scene scene2 = new Scene.SceneBuilder("Test scene").setGeometries(geometries)
                .setLights(lights).build();

        ImageWriter imageWriter = new ImageWriter("lightSphereSpotSharp", 500, 500);
        camera1.setImageWriter(imageWriter) //
                .setRayTracer(new RayTracerBasic(scene2)) //
                .renderImage() //
                .writeToImage(); //
    }


    /**
     * Produce a picture of a two triangles lighted by a narrow spot light
     */
    @Test
    public void trianglesSpotSharp() {
        Geometries geometries = new Geometries(triangle1, triangle2);
        LinkedList<LightSource> lights = new LinkedList<>();
        lights.add(new Spotlight(trCL, trPL, trDL).setNarrowBeam(10).setKl(0.001).setKq(0.00004));

        Scene scene2 = new Scene.SceneBuilder("Test scene").setGeometries(geometries)
                .setLights(lights).build();

        ImageWriter imageWriter = new ImageWriter("lightTrianglesSpotSharp", 500, 500);
        camera2.setImageWriter(imageWriter) //
                .setRayTracer(new RayTracerBasic(scene2)) //
                .renderImage() //
                .writeToImage(); //
    }


    /**
     * This method tests the two triangles with multiple lights
     */
    @Test
    public void multipleLightsTriangleTest() {
        Geometries geometries = new Geometries(triangle1, triangle2);
        LinkedList<LightSource> lights = new LinkedList<>();
        lights.add(new Spotlight(Color.SILVER, new Point(50,30,-100), new Vector(-2,-1,0))
                .setKl(0.0000001).setKq(0.0000001));
        lights.add(new PointLight(Color.SILVER, new Point (100,20,300)));
        lights.add(new DirectionalLight(Color.MAGENTA,new Vector (0,0,-1) ));

        Scene scene2 = new Scene.SceneBuilder("Test scene").setGeometries(geometries)
                .setLights(lights).build();

        ImageWriter imageWriter = new ImageWriter("two triangles with lights", 500, 500);
        camera2.setImageWriter(imageWriter) //
                .setRayTracer(new RayTracerBasic(scene2)) //
                .renderImage() //
                .writeToImage(); //
    }

    /**
     *This method tests the sphere with multiple lights
     */
    @Test
    public void multipleLightsSphereTest() {
        Geometries geometries = new Geometries(sphere);
        LinkedList<LightSource> lights = new LinkedList<>();
        lights.add(new Spotlight(Color.GREEN, new Point(100,100,100), new Vector(-1,-1,-5))
                .setKl(0.0000001).setKq(0.0000001));
        lights.add(new PointLight(Color.ORANGE, new Point (100,20,300)));
        lights.add(new DirectionalLight(Color.DARK_GRAY,new Vector (0,0,-1) ));

        Scene scene2 = new Scene.SceneBuilder("Test scene").setGeometries(geometries)
                .setLights(lights).build();


        ImageWriter imageWriter = new ImageWriter("sphere with lights", 500, 500);
        camera1.setImageWriter(imageWriter) //
                .setRayTracer(new RayTracerBasic(scene2)) //
                .renderImage() //
                .writeToImage(); //
    }
}
