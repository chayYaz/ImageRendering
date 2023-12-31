package primitives;

import java.util.LinkedList;
import java.util.Objects;
import java.util.Random;

import static java.lang.Math.max;

/***
 * Class Point is the basic class representing a point of euclidean geometry in cartesian
 * three dimensional coordinate system.
 */

public class Point {
    /***
     * point zero is frequently used
     */
    public static final Point ZERO = new Point(0, 0, 0);
    /***
     * coordinates of the point
     */
    final Double3 _xyz;

    /***
     * constructor which recieves 3 doubles
     * @param d1 first coordinate
     * @param d2 second coordinate
     * @param d3 third coordinate
     */
    public Point(double d1, double d2, double d3) {
        _xyz = new Double3(d1, d2, d3);
    }

    /***
     * setter which recieves Double3
     * @param xyz the Double3 for creating the Point
     */
    public Point(Double3 xyz) {
        _xyz = xyz;
    }

    /***
     * getter
     * @return the Double3 coordinates
     */
    public Double3 getXyz() {
        return _xyz;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Point point = (Point) o;
        return _xyz.equals(point._xyz);
    }

    @Override
    public int hashCode() {
        return Objects.hash(_xyz);
    }

    @Override
    public String toString() {
        return "Point{" +
                "_xyz=" + _xyz +
                '}';
    }

    /***
     * vector is in the direction from the other to me
     * @param other is other vector
     * @return my vector minus other vector
     */
    public Vector subtract(Point other) {
        if (other._xyz.equals(_xyz)) {
            throw new IllegalArgumentException("Cannot create Vector (0,0,0)");
        }
        return new Vector(
                _xyz.d1 - other._xyz.d1,
                _xyz.d2 - other._xyz.d2,
                _xyz.d3 - other._xyz.d3
        );
    }

    /***
     *
     * @param vector to add to vector
     * @return point result of vector addition
     */
    public Point add(Vector vector) {
        return new Point(
                _xyz.d1 + vector._xyz.d1,
                _xyz.d2 + vector._xyz.d2,
                _xyz.d3 + vector._xyz.d3
        );
    }


    /***
     *
     * @param p one of the points for comparison
     * @return returns the distance between two points
     */
    public double distanceSquared(Point p) {
        double a = _xyz.d1 - p._xyz.d1;
        double b = _xyz.d2 - p._xyz.d2;
        double c = _xyz.d3 - p._xyz.d3;

        return a * a + b * b + c * c;
    }

    /***
     * finds distance between a the current point and a different point
     * @param p the point to find the distance from
     * @return the distance
     */
    public double distance(Point p) {
        return Math.sqrt(distanceSquared(p));
    }

    /***
     * helper function implementing DRY
     * @param up direction to move
     * @param right direction to move
     * @param aperture distance to move
     * @return random point in rectangle around the origin
     */
    public Point movePointRandom(Vector up, Vector right, double aperture) {
        Random rand = new Random();
        double moveUp = rand.nextDouble() * aperture;
        double moveRight = rand.nextDouble() * aperture;

        int upMinus = rand.nextInt() % 2;
        int rightMinus = rand.nextInt() % 2;

        if (upMinus == 0)
            upMinus = -1;

        if (rightMinus == 0)
            rightMinus = -1;


        Point movedPoint = this.add(up.scale(moveUp * upMinus));
        movedPoint = movedPoint.add(right.scale(moveRight * rightMinus));

        return movedPoint;
    }

    /***
     * creates a list of moved points around a point
     * @param edgeOfPixel corner of the pixel
     * @param _Vup vector up
     * @param _Vright vector right
     * @param sizeHOfPixel height of pixel
     * @param sizeWOfPixel width of pixel
     * @param numOfMiniPixels how many minipixels to split into
     * @return list of moved points
     */
    public LinkedList<Point> createListOfMovedPoints(Point edgeOfPixel, Vector _Vup, Vector _Vright, double sizeHOfPixel, double sizeWOfPixel, int numOfMiniPixels) {
        //move out, this is current point//
        // out.print(thisPixelPoint);
        double heightOfMiniPixel = sizeHOfPixel / numOfMiniPixels;
        double widthOfMiniPixel = sizeWOfPixel / numOfMiniPixels;

        //creating a point in the top left corner that is slightly outside the square so that our loop will bring it
        Point outerTopLeftCorner = edgeOfPixel.add(_Vup.scale(heightOfMiniPixel * 0.5));
        outerTopLeftCorner = outerTopLeftCorner.add(_Vright.scale(widthOfMiniPixel * 0.5));

        LinkedList<Point> movedPointsList = new LinkedList<>();

        //for each mini pixel
        for (int row = 1; row <= numOfMiniPixels; row++) {
            //lowers the point for each row of minipixels
            Point startPoint = outerTopLeftCorner.add(_Vup.scale(-1 * row * heightOfMiniPixel));
            for (int column = 1; column <= numOfMiniPixels; column++) {
                //moved the point to the right for each minipixel

                Point currentPoint = startPoint.add(_Vright.normalize().scale(column * widthOfMiniPixel));
                //out.print(currentPoint);
                //?????????should we make movedPointrandom get width and length insead of aparture???????????
                //creating ray from camera through random point in minipixel
                //this function should also be in point class
                Point movedPoint = currentPoint.movePointRandom(_Vup, _Vright, max(widthOfMiniPixel, heightOfMiniPixel));
                movedPointsList.add(movedPoint);
            }
        }
        return movedPointsList;
    }
}