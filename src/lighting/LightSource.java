package lighting;

import primitives.Color;
import primitives.Point;
import primitives.Vector;

/***
 * Interface Light source for managing all types of light
 */
public interface LightSource {
    /***
     * gives the color of the light source affecting a certain point
     * @param p point in the 3D space
     * @return the color
     */
    public Color getIntensity(Point p);

    /***
     * returns a vector from the light source to the point
     * @param p the point in the 3D space
     * @return the vector
     */
    public Vector getL(Point p);

    /***
     * distance between a point and the light source
     * @param p the point in the 3D space
     * @return the distance
     */
    public double getDistance(Point p);
}
