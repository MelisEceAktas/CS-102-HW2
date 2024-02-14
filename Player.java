public class Player {
    String playerName;
    Tile[] playerTiles;
    int numberOfTiles;

    public Player(String name) {
        setName(name);
        playerTiles = new Tile[15]; // there are at most 15 tiles a player owns at any time
        numberOfTiles = 0; // currently this player owns 0 tiles, will pick tiles at the beggining of the game
    }

    /*
     * checks this player's hand to determine if this player is winning
     * the player with a complete chain of 14 consecutive numbers wins the game
     * note that the player whose turn is now draws one extra tile to have 15 tiles in hand,
     * and the extra tile does not disturb the longest chain and therefore the winning condition
     * check the assigment text for more details on winning condition
     */
    public boolean checkWinning() {
        if(findLongestChain() >= 14 ){return true;}
        return false;
    }

    /*
     * used for finding the longest chain in this player hand
     * this method should iterate over playerTiles to find the longest chain
     * of consecutive numbers, used for checking the winning condition
     * and also for determining the winner if tile stack has no tiles
     */
    public int findLongestChain() {
        int longestChain = 0;
        int chain = 0;
        for(int i = 0; i < numberOfTiles - 1; i++){
            if(playerTiles[i].canFormChainWith((playerTiles[i + 1]))){
                chain++;
            }
            else{
                chain = 0;
            }
            if(chain > longestChain){
                longestChain = chain;
            }
        }
        return longestChain;
    }

    /*
     * removes and returns the tile in given index position
     */
    public Tile getAndRemoveTile(int index) {
        if(numberOfTiles < index){
            return null;
        }
        Tile key = playerTiles[index];
        for(int j = index; j < numberOfTiles - 1; j++){
            playerTiles[j] = playerTiles[j + 1];
        }
        return key;
    }

    /*
     * adds the given tile to this player's hand keeping the ascending order
     * this requires you to loop over the existing tiles to find the correct position,
     * then shift the remaining tiles to the right by one
     */
    public void addTile(Tile t) {
        for(int i = 0; i < playerTiles.length; i++){
            if(playerTiles[i].getValue()>= t.getValue()){
                for(int j = numberOfTiles - 1; j > i; j--){
                    playerTiles[j + 1] = playerTiles[j];
                }
                playerTiles[i] = t;
                break;
            }
        }
        if( playerTiles.length == 0){playerTiles[0] = t;}
        this.numberOfTiles ++;
    }

    /*
     * finds the index for a given tile in this player's hand
     */
    public int findPositionOfTile(Tile t) {
        int tilePosition = -1;
        for (int i = 0; i < numberOfTiles; i++) {
            if(playerTiles[i].matchingTiles(t)) {
                tilePosition = i;
            }
        }
        return tilePosition;
    }

    /*
     * displays the tiles of this player
     */
    public void displayTiles() {
        System.out.println(playerName + "'s Tiles:");
        for (int i = 0; i < numberOfTiles; i++) {
            System.out.print(playerTiles[i].toString() + " ");
        }
        System.out.println();
    }

    public Tile[] getTiles() {
        return playerTiles;
    }

    public void setName(String name) {
        playerName = name;
    }

    public String getName() {
        return playerName;
    }
    
}
