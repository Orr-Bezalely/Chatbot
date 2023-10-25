/**
 * An object of this class represents a 2-dimensional vector.
 */
public class Coordinate {


    /**
     * Integer representing the x coordinate of the vector
     */
    public int x;

    /**
     * Integer representing the y coordinate of the vector
     */
    public int y;


    /**
     * This method is the constructor and it initialises the x and y coordinates of the vector.
     *
     * @param x the x-coordinate of the vector
     * @param y the y-coordinate of the vector
     */
    public Coordinate(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Checks whether two vectors are equal
     *
     * @param vec1 an object of type Coordinate representing a vector
     * @param vec2 an object of type Coordinate representing a vector
     * @return boolean representing whether the two vectors are equal
     */
    public static boolean equalVectors(Coordinate vec1, Coordinate vec2) {
        return (vec1.x == vec2.x && vec1.y == vec2.y); // Checks whether the two vectors x's and y's are equal.
    }

    /**
     * This method receives another vector object and adds it to our current vector
     *
     * @param vec2 an object of type Coordinate representing a vector
     * @return this object (of type Coordinate) representing the vector after being transformed by vec2.
     */
    public Coordinate addCoord(Coordinate vec2) {
        this.x += vec2.x; // adding the vec2's x-coordinates to this vector
        this.y += vec2.y; // adding the vec2's y-coordinates to this vector
        return this;
    }
}
