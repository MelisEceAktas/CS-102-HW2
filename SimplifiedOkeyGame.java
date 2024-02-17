import java.util.ArrayList;
import java.util.Random;

public class SimplifiedOkeyGame {
    
    Random random;
    Player[] players;
    Tile[] tiles;
    int tileCount;

    Tile lastDiscardedTile;

    int currentPlayerIndex = 0;

    public SimplifiedOkeyGame() {
        players = new Player[4];
        random = new Random();
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
     * (DORUK): distributes the starting tiles to the players
     * player at index 0 gets 15 tiles and starts first
     * other players get 14 tiles, this method assumes the tiles are already shuffled
     */
    public void distributeTilesToPlayers() {
        for (int i = 0; i < this.players[0].playerTiles.length; i++) {
            //this.players[0].playerTiles[i] = tiles[i];
            // ^ instead of this, use player.addTile(); ~brtcrt
            this.players[0].addTile(tiles[i]); 
            // also decrease tile count
            this.tileCount--;
        }
        for (int j = 1; j < 4; j++) {
            for (int i = 0; i < 14; i++) {
                //this.players[j].playerTiles[i] = tiles[i + 1 + j * 14];
                // ^ same thing here as well ~brtcrt
                this.players[j].addTile(tiles[i + 1 + j * 14]);
                this.tileCount--;
            }
        }    
    }

    /*
     * (DORUK): get the last discarded tile for the current player
     * (this simulates picking up the tile discarded by the previous player)
     * it should return the toString method of the tile so that we can print what we picked
     */
    public String getLastDiscardedTile() {
        return this.lastDiscardedTile.toString();
    }

    /*
     * (DORUK): get the top tile from tiles array for the current player
     * that tile is no longer in the tiles array (this simulates picking up the top tile)
     * and it will be given to the current player
     * returns the toString method of the tile so that we can print what we picked
     */
    public String getTopTile() {
        return this.tiles[this.tiles.length - 1].toString();
    }

    /*
     * (DORUK): should randomly shuffle the tiles array before game starts
     */
    public void shuffleTiles() {
        int index;
        for (int i = tiles.length - 1; i > 0; i--) {
            index = random.nextInt(i + 1);
            if (index != i) {
                Tile temp = tiles[index];
                tiles[index] = tiles[i];
                tiles[i] = temp;
            }
        }
    }

    /*
     * (DORUK): check if game still continues, should return true if current player
     * finished the game. use checkWinning method of the player class to determine
     */
    public boolean didGameFinish() {
        for (Player player : players) {
            if (player.checkWinning()) {
                return true;
            }
        }
        return !this.hasMoreTileInStack();
    }

    /* (brtcrt): finds the player who has the highest number for the longest chain
     * if multiple players have the same length may return multiple players
     * Trying to do this in a single loop and I'm gonna k*ll myself so I won't even bother.
     */
    public Player[] getPlayerWithHighestLongestChain() {
        int winnerCount = 1;
        int targetLength = this.players[0].findLongestChain();
        for (int i = 1; i < this.players.length; i++) {
            if (targetLength == this.players[i].findLongestChain()) {
                winnerCount++;
            } else if (targetLength < this.players[i].findLongestChain()) {
                winnerCount = 1;
                targetLength = this.players[i].findLongestChain();
            }
        }
        Player[] winners = new Player[winnerCount];
        int j = 0;
        for (int i = 0; i < this.players.length; i++) {
            if (this.players[i].findLongestChain() == targetLength) {
                winners[j] = this.players[i];
                j++;
            }
        }
        return winners;
    }
    
    /*
     * checks if there are more tiles on the stack to continue the game
     */
    public boolean hasMoreTileInStack() {
        return tileCount != 0;
    }

    /*
     * (brtcrt): pick a tile for the current computer player using one of the following:
     * - picking from the tiles array using getTopTile()
     * - picking from the lastDiscardedTile using getLastDiscardedTile()
     * you should check if getting the discarded tile is useful for the computer
     * by checking if it increases the longest chain length, if not get the top tile
     */
    public void pickTileForComputer() {
        Player currentPlayer = this.players[this.currentPlayerIndex];
        Tile[] playerTiles = currentPlayer.getTiles();
        // check the last discarded tile first
        Tile ldt = this.lastDiscardedTile;
        for (int i = 0; i < currentPlayer.numberOfTiles - 1; i++) { // also could have hard-coded it to be playerTiles.length - 1 since it will always be 14
            if (ldt.canFormChainWith(playerTiles[i])) {
                if (playerTiles[i + 1] == null) {
                    currentPlayer.addTile(ldt);
                    return;
                } else if (ldt.getValue() != playerTiles[i+1].getValue()) {
                    currentPlayer.addTile(ldt);
                    return;
                }
            }
        }
        // if ldt is not useful, pick from pile
        currentPlayer.addTile(this.tiles[this.tiles.length - 1]);
        tileCount--;
        return;

    }   

    /*
     * (brtcrt): Current computer player will discard the least useful tile.
     * you may choose based on how useful each tile is
     */
    public void discardTileForComputer() {
        // discard the tile if it doesn't make a chain
        // if no such tile exists, then discard a tile from the smallest chain
        Player currentPlayer = this.players[this.currentPlayerIndex];
        Tile[] playerTiles = currentPlayer.getTiles();
        ArrayList<ArrayList<Integer>> chains = this.findChains(playerTiles);
        for (int i = 0; i < playerTiles.length; i++) {
            // if tile is not part of a chain ~brtcrt
            if (!this.inChain(chains, i)) {
                currentPlayer.getAndRemoveTile(i);
                return;
            }
            // i was doing some goofy stuff here and not returning idk but it should be fixed now ~brtcrt
        }
        // if all tiles belong to a chain    
        // find smallest chain and remove first ~brtcrt
        ArrayList<Integer> small = chains.get(this.smallestChain(chains));
        currentPlayer.getAndRemoveTile(small.get(0));
        return;
    
    }

    /*
     * Finds all the chains in a given array of tiles
     */
    private ArrayList<ArrayList<Integer>> findChains(Tile[] tiles) {
        // [[chain1 indexes], [chain 2 indexes]]
        int chainIndex = 0;
        boolean endedChain = true;
        boolean newChain = true;
        ArrayList<ArrayList<Integer>> chains = new ArrayList<ArrayList<Integer>>();
        chains.add(new ArrayList<Integer>()); // i forgot to initialise the arraylist ~brtcrt
        for (int i = 0; i < tiles.length - 1; i++) {
            Tile cTile = tiles[i];
            Tile next = tiles[i + 1];
            if (cTile.canFormChainWith(next)) {
                endedChain = false;
                chains.get(chainIndex).add(i + 1);
                if (newChain) {
                    newChain = false;
                    chains.get(chainIndex).add(i + 1);
                    chains.get(chainIndex).set(0, i);
                }
            } else {
                if (!endedChain) {
                    chainIndex++;
                    chains.add(new ArrayList<Integer>()); // < same here ~brtcrt
                    endedChain = true;
                    newChain = true;
                }
            }
        }

        return chains;
    }

    /*
     * Checks if a given index is a member of a chain
     */
    private boolean inChain(ArrayList<ArrayList<Integer>> chains, int i) {
        for (ArrayList<Integer> chain : chains) {
            if (chain.contains(i)) {
                return true;
            }
        }
        return false;

    }

    private int smallestChain(ArrayList<ArrayList<Integer>> chains) {
        int smallest = 0;
        for (int i = 1; i < chains.size() - 1; i++) {
            if (chains.get(i).size() < chains.get(smallest).size()) {
                smallest = i;
            }
        }
        return smallest;
    }

    /*
     * (brtcrt): discards the current player's tile at given index
     * this should set lastDiscardedTile variable and remove that tile from
     * that player's tiles
     */
    public void discardTile(int tileIndex) {
        Tile lastTile = this.players[this.currentPlayerIndex].getAndRemoveTile(tileIndex);
        this.lastDiscardedTile = lastTile;


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
