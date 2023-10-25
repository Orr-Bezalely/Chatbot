import java.util.ArrayList;
import java.util.Arrays;

/**
 * This class contains the map information, including the map name, gold required to win, and the map structure.
 */
public class Map {

    /**
     * ArrayList of strings which represents the maps. Each string represents a row
     */
    private ArrayList<String> map = new ArrayList<>();

    /**
     * String representing the map name
     */
    private String mapName;

    /**
     * Integer representing the gold required to beat the game (From human's prospective)
     */
    private int goldRequired;

    /**
     * Integer representing the minimum number of spawnable spaces the map should contain
     */
    private int minimumRequiredNumOfSpaces;


    /**
     * The constructor receives the minimum number of spaces the map should accomodate for (for placing bots and players)
     *
     * @param minimumRequiredNumOfSpaces integer representing the number of spaces the map should accommodate for
     */
    public Map(int minimumRequiredNumOfSpaces) {
        this.minimumRequiredNumOfSpaces = minimumRequiredNumOfSpaces;
    }


    /**
     * Getter which returns the gold required to win
     *
     * @return integer representing the gold required to win
     */
    protected int getGoldRequired() {
        return goldRequired;
    }


    /**
     * Getter which returns the name of the map
     *
     * @return String representing the name of the map
     */
    protected String getMapName() {
        return mapName;
    }


    /**
     * Gets the number of rows in the map
     *
     * @return integer representing the number of rows in the map
     */
    public int getYDim() {
        return this.map.size();
    }


    /**
     * Gets the number of columns in row y
     *
     * @param y Integer representing the row number in the map
     * @return integer representing the number of columns in the given row
     */
    public int getXDim(int y) {
        return this.map.get(y).length();
    }


    /**
     * Receives a vector and checks whether it is within the map or not
     *
     * @param vector Coordinate type representing the vector to check if is in map or not
     * @return boolean representing whether the vector is within the map or not.
     */
    public boolean vectorInMap(Coordinate vector) {
        return ((0 <= vector.y && vector.y < this.map.size()) && (0 <= vector.x && vector.x < this.map.get(vector.y).length()));
    }


    /**
     * Receives a vector and returns the tile in that coordinate.
     *
     * @param vector Coordinate type representing the vector to check
     * @return character representing the tile type in that coordinate.
     */
    public char getTile(Coordinate vector) {
        return this.map.get(vector.y).charAt(vector.x);
    }


    /**
     * Gets a coordinate and a tile and checks whether the type of tile in the coordinate is the same as the type of tile sent
     *
     * @param position Coordinate representing the coordinates of the tile to check
     * @param tile     character representing the type of tile to check
     * @return boolean of whether the tile is the same type of tile as the one in the coordinates sent
     */
    public boolean checkTile(Coordinate position, char tile) {
        return vectorInMap(position) && getTile(position) == tile;
    }


    /**
     * Changes the tile in coordinates position into the tile given
     *
     * @param position Coordinate representing the coordinates of the tile to change
     * @param tile     character representing the type of tile to change to
     */
    protected void changeTile(Coordinate position, char tile) {
        String str = this.map.get(position.y);
        this.map.set(position.y, str.substring(0, position.x) + tile + str.substring(position.x + 1));
    }


    /**
     * Loads in default map.
     */
    protected void DefaultMap() {
        this.mapName = "Default map";
        this.goldRequired = 2;
        this.map = new ArrayList<>(Arrays.asList(
                "###################",
                "#.................#",
                "#......G........E.#",
                "#.................#",
                "#..E..............#",
                "#..........G......#",
                "#.................#",
                "#.................#",
                "###################"));
        this.minimumRequiredNumOfSpaces = 117;
    }
}
