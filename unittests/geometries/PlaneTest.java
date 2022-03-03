package geometries;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import primitives.Point;
import primitives.Vector;
import static org.junit.jupiter.api.Assertions.*;

class PlaneTest {

    /**
     * Test method for {@link geometries.Plane#getNormal()}.
     * This method tests the default get normal
     */
    @Test
    void testGetNormal() {
        Point p0=new Point(0,0,0);
        Point p1=new Point(0,1,0);
        Point p2=new Point(1,0,0);
        Plane plane=new Plane(p0,p1,p2,p0);

        Vector v = plane.getNormal();

        // ====== Equivalence Partition Tests ======
        //TC00: Checking that the vector was normalized
        assertEquals(v.length(),1,"vector not normalized");
        //TC01: Checking that the correct vector was given or its opposite
        assertTrue(new Vector (0,0,1).equals(v)||new Vector (0,0,-1).equals(v),
                "Incorrect normal vector");
        // ====== Boundary Value Tests ======
        Point p3 = new Point (0,0,0);
        Point p4 = new Point (0,0,1);
        Point p5 = new Point (0,0,2);
        //TC02: all three of the points are in a line
        assertThrows(IllegalArgumentException.class,
                ()-> new Plane(p3, p3, p4, p3),
                "all three of the points are in a line");
        //TC03: two or more of the points are the same
        assertThrows(IllegalArgumentException.class,
                ()-> new Plane(p3, p4, p5, p3),
                "all three of the points are in a line");
    }

    /**
     * Test method for {@link geometries.Plane#getNormal(Point)}.
     * This method checks the getNormal which receives a point
     */
    @Test
    void testGetNormalWithPoint() {
        Point p0=new Point(0,0,0);
        Point p1=new Point(0,1,0);
        Point p2=new Point(1,0,0);
        Plane plane=new Plane(p0,p1,p2,p0);

        Vector v = plane.getNormal(p0);

        // ====== Equivalence Partition Tests ======
        //TC00: Checking that the vector was normalized
        assertEquals(v.length(),1,"vector not normalized");
        //TC01: Checking that the correct vector was given or it's opposite
        assertTrue(new Vector (0,0,1).equals(v)||new Vector (0,0,-1).equals(v),
                "Incorrect normal vector");
    }

}