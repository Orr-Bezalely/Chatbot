import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * This class represents the cropped map, the map that the bot uses to make decisions on their movement.
 */
public class CroppedMap {


    /**
     * List of strings representing the cropped map
     */
    private List<String> croppedMap;

    /**
     * Coordinate representing the centre of the map
     */
    private Coordinate centreCoord;


    /**
     * @param croppedMap  List of strings representing the cropped map
     * @param centreCoord Coordinate representing the coordinate the map is centred on
     */
    public CroppedMap(List<String> croppedMap, Coordinate centreCoord) {
        this.croppedMap = croppedMap;
        this.centreCoord = centreCoord;
    }


    /**
     * Setter which updates the cropped map
     *
     * @param croppedMap List of strings representing the cropped map
     */
    public void setCroppedMap(List<String> croppedMap) {
        this.croppedMap = croppedMap;
    }


    /**
     * Setter which updates the centre coordinate
     *
     * @param centreCoord Coordinate representing the coordinate the map is centred on
     */
    public void setCentreCoord(Coordinate centreCoord) {
        this.centreCoord = centreCoord;
    }


    /**
     * Returns the size of the cropped map
     *
     * @return integer representing the size of the cropped map
     */
    private int getLength() {
        return croppedMap.size();
    }


    /**
     * Checks whether the coordinates are on the edge of the cropped map
     *
     * @param coords Coordinate representing the coordinates to check
     * @return boolean representing whether the coordinates are on the edge of the map or not
     */
    public boolean onEdge(Coordinate coords) {
        Coordinate fixedCoord = fixVector(new Coordinate(coords.x, coords.y));
        return !((1 <= fixedCoord.x && fixedCoord.x <= getLength() - 1) && (1 <= fixedCoord.y && fixedCoord.y <= getLength() - 1));
    }


    /**
     * Returns the radius of the map
     *
     * @return integer representing the radius of the map
     */
    private int getRadius() {
        return Math.floorDiv(getLength(), 2);
    }


    /**
     * Fixes a given coordinate from the absolute coordinate to its relative coordinate on the cropped map
     *
     * @param vector Coordinate representing the coordinates to fix
     * @return Coordinate representing the coordinates fixed after transforming them to the cropped map's coordinates
     */
    private Coordinate fixVector(Coordinate vector) {
        int radius = getRadius();
        return vector.addCoord(new Coordinate(radius - centreCoord.x, radius - centreCoord.y));
    }


    /**
     * Checks each of the neighbours of the coordinate and checks whether they are valid (i.e. not a wall)
     *
     * @param Coords Coordinate representing the coordinates to check
     * @return HashMap with key String direction and value of the coordinates corresponding to the position of the neighbour
     */
    public HashMap<String, Coordinate> getValidNeighbours(Coordinate Coords) {
        HashMap<String, Coordinate> validNeighbours = new HashMap<>();
        for (String direction : DoDUtility.TurnToVector.keySet()) { // Loops through all directions and checks whether each position is traversable
            Coordinate vector = new Coordinate(Coords.x, Coords.y);
            Coordinate newPos = fixVector(vector.addCoord(DoDUtility.TurnToVector.get(direction)));
            if (croppedMap.get(newPos.y).charAt(newPos.x) != '#')
                validNeighbours.put(direction, newPos); // Checks whether the position is traversable
        }
        return validNeighbours;
    }


    /**
     * Gets all the coordinates of players on this cropped map and returns them in an ArrayList of coordinates
     *
     * @return ArrayList of coordinates which representing the coordinates of all humans visible on cropped map
     */
    public ArrayList<Coordinate> getHumansPositions() {
        ArrayList<Coordinate> playerPosArray = new ArrayList<>();
        int radius = getRadius();
        // Loops through each tile of the cropped map and looks for players
        for (int y = -radius; y <= radius; y++) {
            for (int x = -radius; x <= radius; x++) {
                Coordinate vector = new Coordinate(this.centreCoord.x, this.centreCoord.y);
                Coordinate newPos = fixVector(vector.addCoord(new Coordinate(x, y)));
                if (croppedMap.get(newPos.y).charAt(newPos.x) == 'P')
                    playerPosArray.add(newPos); // Checks if the coordinate contains a player
            }
        }
        return playerPosArray;
    }
}
