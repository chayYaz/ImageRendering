package renderer;

import geometries.Intersectable.GeoPoint;
import lighting.LightSource;
import primitives.*;
import scene.Scene;

import java.util.LinkedList;
import java.util.List;

import static primitives.Util.alignZero;


public class RayTracerBasic extends RayTracerBase {

    //added in part7 in order to support shadows
    private static final double EPSILON = 0.001;

    //max values to stop recursion of functions calling each other
    //values were chosen according to the given instructions
    private static final int MAX_CALC_COLOR_LEVEL = 3;//for less recursion and faster calculation time
    private static final double MIN_CALC_COLOR_K = 0.001;
    private static final double INITIAL_K = 1.0;

    public RayTracerBasic(Scene scene) {
        super(scene);
    }

    /***
     * @param ray ray from the camera through the viewplane
     * @return the color of the closest point among the list of intersections
     */
    @Override
    public Color traceRay(Ray ray) {
        //LinkedList<GeoPoint> listPointsIntersections = (LinkedList<GeoPoint>) _scene._geometries.findGeoIntersectionsHelper(ray);
        //if (listPointsIntersections == null || listPointsIntersections.isEmpty()) {
        //   return _scene._background;//.add(_scene._ambientLight.getIntensity());
        //}

        //GeoPoint closestPoint = ray.findClosestGeoPoint(listPointsIntersections);
        GeoPoint closestPoint = findClosestGeoIntersection(ray);
        //out.print(closestPoint);
        if (closestPoint == null)
            return _scene._background;
        Color thisPixelColor = calcColor(closestPoint, ray);
        return thisPixelColor;
    }

    /***
     * Method which calculates the effects of deffusive light
     * @return color of the light
     */
    private double calcDiffusive(double kD, double nl) {
        //according to the phong model
        double calcD = kD * Math.abs(nl);
        return calcD;
    }

    /***
     * Method which calculates the effects of specular light
     * @return color of the light
     */
    private double calcSpecular(double kS, Vector n, Vector l, double nl, Vector v, double shininess) {
        //according to the phong model
        Vector r = createR(l, n);
        double max = Math.max(0, v.scale(-1).dotProduct(r));
        double calcS = kS * Math.pow(max, shininess);
        return calcS;
    }

    /***
     * assistant function which creates vector r to help frequently used calculations
     * used also in calculating refracted light
     *
     * @param n vector
     * @param l vector
     *
     * @return vector r
     */

    private Vector createR(Vector l, Vector n) {
        Vector r = l.subtract(n.scale(2 * l.dotProduct(n)));
        return r.normalize();

    }

    /***
     * Mathematical calculations according to the slides from the fist semester
     * This function adds the specular and diffusive effects to the color of the object
     * @param intersection the point for which we want the color
     * @param ray ray from the camera
     * @return the color of the point
     */
    private Color calcLocalEffectsSimple(GeoPoint intersection, Ray ray, double k) {
        Color color = intersection._geoPointGeometry.getEmission();//.scale(1 - intersection._geoPointGeometry.getMaterial().kT.getD1());
        Vector v = ray.getDir();
        Vector n = intersection._geoPointGeometry.getNormal(intersection._geoPoint);
        double nv = alignZero(n.dotProduct(v));
        if (nv == 0) return color;
        Material material = intersection._geoPointGeometry.getMaterial();
        for (LightSource lightSource : _scene._lights) {
            Vector l = lightSource.getL(intersection._geoPoint);
            double nl = alignZero(n.dotProduct(l));
            if (nl * nv > 0) { // sign(nl) == sing(nv)
                double ktr = transparency(intersection, l, n, lightSource);
                if (ktr * k > MIN_CALC_COLOR_K) {
                    Color iL = lightSource.getIntensity(intersection._geoPoint).scale(ktr);
                    color = color.add(iL.scale(calcDiffusive(material.getkD().getD1(), nl)),
                            iL.scale(calcSpecular(material.getkS().getD1(), n, l, nl, v, material.nShininess)));
                }
            }
        }
        return color;

    }

    /***
     * helper function for calcGlobalEffects used for recursion
     * could have been written in reg calcglobal but instead of writing it twice for reflect and refract
     * @param ray from the point to the camera
     * @param level number of recursions left
     * @param kx constant coefficient of lowering (could be kt or kr)
     * @param kkx current kx, if it is smaller than min it wont add much to total color so no need to calc it
     * @return color
     */
    public Color calcGlobalEffect(Ray ray, int level, Double3 kx, double kkx) {
        GeoPoint gp = findClosestGeoIntersection(ray);
        if (gp == null) {
            return _scene._background;
        }

        //level-1 to ensure that the recursion stops
        //scales by kx to return only a percentage of the color according to the coefficient
        Color c = calcColor(gp, ray, level - 1, kkx).scale(kx);
        //out.print(c);
        return c;
    }

    /***
     * function calcGlobalEffects handles reflections and refractions of light on a specific point
     * @param geoPoint the point to calculate the color for
     * @param v the vector from the camera to the point
     * @param level how many recursion can still be performed
     * @param k constant coefficient of lowering
     * @return the color for that point
     */
    public Color calcGlobalEffects(GeoPoint geoPoint, Vector v, int level, double k) {
        //base color
        Color color = Color.BLACK;
        //normal
        Vector n = geoPoint._geoPointGeometry.getNormal(geoPoint._geoPoint);
        Material material = geoPoint._geoPointGeometry.getMaterial();

        //checking that we haven't had too many refractions
        //adding refracted color
        double kkt = k * material.kT.getD1();

        if (kkt > MIN_CALC_COLOR_K) {
            //kkt changes every iteration, kt stays the same

            color = color.add(calcGlobalEffect(constructRefractedRay(geoPoint._geoPoint, v, n), level, material.kT, kkt));
        }

        //checking that we haven't had too many reflections
        //adding reflected color
        double kkr = k * material.kR.getD1();
        if (kkr > MIN_CALC_COLOR_K) {
            color = color.add(calcGlobalEffect(constructReflectedRay(geoPoint._geoPoint, v, n), level, material.kR, kkr));
        }
        return color;
    }

    /***
     *
     * @param closestPoint the point that we want to find the color of
     * @param ray the ray from the camera through a pixel
     * @return the color for that point
     * recursive function
     */
    public Color calcColor(GeoPoint closestPoint, Ray ray, int level, double k) {
        Color color = calcLocalEffectsSimple(closestPoint, ray, k);
        //adding the local effects according to the phong model, including emissions
        if (level == 1)
            return color;
        else
            return color.add(calcGlobalEffects(closestPoint, ray.getDir(), level, k));
    }

    /***
     * this function calls calcColor with properties to limit recursion
     * @param gp the point for which we want the color
     * @param ray the ray to the point from the camera to the point
     * @return the color for the point
     */
    private Color calcColor(GeoPoint gp, Ray ray) {
        Color result = calcColor(gp, ray, MAX_CALC_COLOR_LEVEL, INITIAL_K)
                .add(_scene._ambientLight.getIntensity());
        return result;
    }

    /***
     * this function tells us whether a certain point is shaded or not
     * @param gp the point to check
     * @param l the vector from the light to the point
     * @param n normal to that point
     * @return true if the point is unshaded
     */
    private boolean unshaded(GeoPoint gp, Vector l, Vector n, LightSource lightSource) {
        Vector lightDirection = l.scale(-1); //from point to light source
        //took a point adding epsilon in the direction of the normal
        Ray lightRay;
        Vector v;
        if (n.dotProduct(lightDirection) < 0) {
            v = n.scale(-EPSILON);
        } else {
            v = n.scale(EPSILON);
        }
        lightRay = new Ray(gp._geoPoint, v);
        //checks for intersections between the point and the light
        List<GeoPoint> intersections = _scene._geometries.findGeoIntersections(lightRay);
        //if there is nothing between the point and the light then the point is unshaded
        if (intersections == null)
            return true;
        if (intersections.isEmpty())
            return true;

        //using distance to ensure that we don't put a shadow based on objects behind the light
        double distance = lightSource.getDistance(gp._geoPoint);
        for (GeoPoint intersection : intersections) {
            if (alignZero(intersection._geoPoint.distance(gp._geoPoint) - distance) <= 0 &&
                    intersection._geoPointGeometry.getMaterial().kT.getD1() == 0)
                return false;
        }
        return true;
    }


    /***
     *
     * @param p point at the head of the ray for the reflection
     * @param v the vector from the camera to the point
     * @param n the normal vector from the point
     * @return the reflected ray
     */
    public Ray constructReflectedRay(Point p, Vector v, Vector n) {
        Ray reflectedRay = new Ray(p, n,  createR(v,n));
        return reflectedRay;
    }

    /***
     *
     * @param p point at the head of the refracted ray
     * @param v the vector from the camera to the point
     * @param n the normal vector from the point
     * @return the refracted ray
     */
    public Ray constructRefractedRay(Point p, Vector v, Vector n) {
        //something transparent will show only the objects behind it.
        //we are moving the point using minus epsilon because we are moving inward in the opposite direction of the normal
        Ray refractedRay = new Ray(p, n, v);
        return refractedRay;
    }

    /***
     * helper function for frequently used code (DRY)
     * @param ray the ray for which we want to find the closest point
     * @return GeoPoint
     */
    private GeoPoint findClosestGeoIntersection(Ray ray) {
        LinkedList<GeoPoint> l = (LinkedList<GeoPoint>) _scene._geometries.findGeoIntersections(ray);
        if (l == null)
            return null;
        if (l.isEmpty())
            return null;
        GeoPoint p = ray.findClosestGeoPoint(l);
        return p;
    }

    /***
     * this function tells us whether a certain point is shaded or not
     * @param geoPoint the point to check
     * @param l the vector from the light to the point
     * @param n normal to that point
     * @return true if the point is unshaded
     */
    private double transparency(GeoPoint geoPoint, Vector l, Vector n, LightSource lightSource) {
        Vector lightDirection = l.scale(-1); //from point to light source
        //took a point adding epsilon in the direction of the normal
//        Vector v;
//        if (n.dotProduct(lightDirection) < 0) {
//            v = n.scale(0.1);
//        } else {
//            v = n.scale(-0.1);
//        }
        Ray lightRay = new Ray(geoPoint._geoPoint, n,l);
        //checks for intersections between the point and the light
        List<GeoPoint> intersections = _scene._geometries.findGeoIntersections(lightRay);
        //if there is nothing between the point and the light then the point is unshaded
        if (intersections == null)
            return 1.0;
        if (intersections.isEmpty())
            return 1.0;

        double ktr = 1.0;

        //using distance to ensure that we don't put a shadow based on objects behind the light
        double distance = lightSource.getDistance(geoPoint._geoPoint);
        for (GeoPoint intersection : intersections) {
            if (alignZero(intersection._geoPoint.distance(geoPoint._geoPoint) - distance) <= 0) {
                ktr = alignZero(ktr * intersection._geoPointGeometry.getMaterial().kT.getD1());
                if (ktr == 0)
                    return ktr;
            }
        }
        return ktr;
    }


}
