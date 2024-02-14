import java.util.Random;

public class SimplifiedOkeyGame {

    Player[] players;
    Tile[] tiles;
    int tileCount;

    Tile lastDiscardedTile;

    int currentPlayerIndex = 0;

    public SimplifiedOkeyGame() {
        players = new Player[4];
    }

    public void createTiles() {
        tiles = new Tile[104];
        int currentTile = 0;

        // four copies of each value, no jokers
        for (int i = 1; i <= 26; i++) {
            for (int j = 0; j < 4; j++) {
                tiles[currentTile++] = new Tile(i);

            }
        }

        tileCount = 104;
    }

    /*
     * distributes the starting tiles to the players
     * player at index 0 gets 15 tiles and starts first
     * other players get 14 tiles, this method assumes the tiles are already shuffled
     */
    public void distributeTilesToPlayers() {
        shuffleTiles();
        for(int j = 103; j <= 47; j++){
            for(int i = 0; i < 57; i++){
                players[i % 4].addTile(tiles[j]);
                tileCount--;
            }
        }
    }

    /*
     * get the last discarded tile for the current player
     * (this simulates picking up the tile discarded by the previous player)
     * it should return the toString method of the tile so that we can print what we picked
     */
    public String getLastDiscardedTile() {
        players[currentPlayerIndex].addTile(lastDiscardedTile);
        return lastDiscardedTile.toString();
    }

    /*
     * get the top tile from tiles array for the current player
     * that tile is no longer in the tiles array (this simulates picking up the top tile)
     * and it will be given to the current player
     * returns the toString method of the tile so that we can print what we picked
     */
    public String getTopTile() {
        players[currentPlayerIndex].addTile(tiles[--tileCount]);
        return tiles[--tileCount].toString();
    }

    /*
     * should randomly shuffle the tiles array before game starts
     */
    public void shuffleTiles() {
        Tile[] newTiles = new  Tile[104] ;
        for(Tile tile: tiles){
            Random random  = new Random();
            int rand;
            do {
                rand = random.nextInt(104);
            } while(tiles[rand] != null);
            tiles[rand] = tile;
        }
        tiles = newTiles;
    }

    /*
     *  check if game still continues, should return true if current player
     * finished the game. use checkWinning method of the player class to determine
     */
    public boolean didGameFinish() {
        if(players[currentPlayerIndex].checkWinning()){return true;}
        return false;
    }

    /* finds the player who has the highest number for the longest chain
     * if multiple players have the same length may return multiple players
     */
    public Player[] getPlayerWithHighestLongestChain() {
        Player[] winners = new Player[4];
        int maxLength = 0;
        int maxPlayer = 0;
        for(int i = 0; i < 4; i++){
            if(players[i].findLongestChain() > maxLength){
                maxLength = players[i].findLongestChain();
                maxPlayer++;
            }
            else if(players[i].findLongestChain() == maxLength){maxPlayer++;}
            else{maxPlayer = 0;}
        }
        return winners;
    }
    
    /*
     * checks if there are more tiles on the stack to continue the game
     */
    public boolean hasMoreTileInStack() {
        return tileCount != -1;
    }

    /*
     * pick a tile for the current computer player using one of the following:
     * - picking from the tiles array using getTopTile()
     * - picking from the lastDiscardedTile using getLastDiscardedTile()
     * you should check if getting the discarded tile is useful for the computer
     * by checking if it increases the longest chain length, if not get the top tile
     */
    public void pickTileForComputer() {
        int longest = players[currentPlayerIndex].findLongestChain();;
        getLastDiscardedTile();
        if(longest < players[currentPlayerIndex].findLongestChain()){
            players[currentPlayerIndex].getAndRemoveTile(players[currentPlayerIndex].findPositionOfTile(lastDiscardedTile));
        }
        getTopTile();
    }

    /*
     * TODO: Current computer player will discard the least useful tile.
     * you may choose based on how useful each tile is
     */
    public void discardTileForComputer() {
        djdofodjjıojd
        discardTile()
    }

    /*
     * TODO: discards the current player's tile at given index
     * this should set lastDiscardedTile variable and remove that tile from
     * that player's tiles
     */
    public void discardTile(int tileIndex) {
        lastDiscardedTile = players[currentPlayerIndex].getAndRemoveTile(tileIndex);
        passTurnToNextPlayer();
    }

    public void displayDiscardInformation() {
        if(lastDiscardedTile != null) {
            System.out.println("Last Discarded: " + lastDiscardedTile.toString());
        }
    }

    public void displayCurrentPlayersTiles() {
        players[currentPlayerIndex].displayTiles();
    }

    public int getCurrentPlayerIndex() {
        return currentPlayerIndex;
    }

      public String getCurrentPlayerName() {
        return players[currentPlayerIndex].getName();
    }

    public void passTurnToNextPlayer() {
        currentPlayerIndex = (currentPlayerIndex + 1) % 4;
    }

    public void setPlayerName(int index, String name) {
        if(index >= 0 && index <= 3) {
            players[index] = new Player(name);
        }
    }

}
