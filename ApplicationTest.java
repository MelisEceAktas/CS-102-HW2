import java.util.Scanner;
import java.util.Random;

public class ApplicationTest {

    public static void main(String[] args) {
        Random random = new Random();
        int testsToRun = 100_000;

        for (int tests = 0; tests < testsToRun; tests ++)
        {

            SimplifiedOkeyGame game = new SimplifiedOkeyGame();

            String playerName = "A";
            game.setPlayerName(0, playerName);
            game.setPlayerName(1, "B");
            game.setPlayerName(2, "C");
            game.setPlayerName(3, "D");

            game.createTiles();
            game.shuffleTiles();
            game.distributeTilesToPlayers();

            boolean devModeOn = true;
            
            boolean firstTurn = true;
            boolean gameContinues = true;
            int playerChoice = -1;

            while(gameContinues) {
                
                int currentPlayer = game.getCurrentPlayerIndex();
                System.out.println(game.getCurrentPlayerName() + "'s turn.");
                
                if(currentPlayer == 0) {
                    // this is the human player's turn
                    game.displayCurrentPlayersTiles();
                    game.displayDiscardInformation();

                    System.out.println("What will you do?");

                    if(!firstTurn) {
                        // after the first turn, player may pick from tile stack or last player's discard
                        System.out.println("1. Pick From Tiles");
                        System.out.println("2. Pick From Discard");
                        playerChoice = random.nextInt(2) + 1;
                    }
                    else{
                        // on first turn the starting player does not pick up new tile
                        System.out.println("1. Discard Tile");
                        playerChoice = 1;
                    }

                    System.out.print("Your choice: ");

                    // after the first turn we can pick up
                    if(!firstTurn) {
                        if(playerChoice == 1) {
                            System.out.println("You picked up: " + game.getTopTile());
                            firstTurn = false;
                        }
                        else if(playerChoice == 2) {
                            System.out.println("You picked up: " + game.getLastDiscardedTile());
                        }

                        // display the hand after picking up new tile
                        game.displayCurrentPlayersTiles();
                    }
                    else{
                        // after first turn it is no longer the first turn
                        firstTurn = false;
                    }

                    gameContinues = !game.didGameFinish() && game.hasMoreTileInStack();

                    if(gameContinues) {
                        // if game continues we need to discard a tile using the given index by the player
                        System.out.println("Which tile you will discard?");
                        System.out.print("Discard the tile in index: ");
                        playerChoice = random.nextInt(14) + 1;
                        System.out.println(playerChoice);

                        // TODO (KAAN): make sure the given index is correct, should be 0 <= index <= 14
                        if (playerChoice < 0 || playerChoice > 14){
                            boolean isInputCorrect = false;

                            assert isInputCorrect; // If random.nextInt(14) + 1; works should never come here anyway
                            while (!isInputCorrect){
                                System.out.print("Please enter an index between 0 and 14: ");
                            
                                if (!(playerChoice < 0) && !(playerChoice > 14)){
                                    isInputCorrect = true;
                                }
                            }
                        }

                        game.discardTile(playerChoice);
                        game.passTurnToNextPlayer();
                    }
                    else{
                        if(!game.didGameFinish()) {
                            // if we finish the hand we win
                            System.out.println("Congratulations, you win!");    
                        }
                        else{
                            // TODO (KAAN): the game ended with no more tiles in the stack
                            // determine the winner based on longest chain lengths of the players
                            // use getPlayerWithHighestLongestChain method of game for this task

                            System.out.print("No more tiles left in the stack, ");
                            printPlayers(game.getPlayerWithHighestLongestChain()); // Now printing player names instead of reference ~ omer
                            System.out.println(" wins !");

                        }
                    }
                }
                else{
                    // this is the computer player's turn
                    if(devModeOn) {
                        game.displayCurrentPlayersTiles();
                    }

                    // computer picks a tile from tile stack or other player's discard
                    game.pickTileForComputer();

                    gameContinues = !game.didGameFinish() && game.hasMoreTileInStack();

                    if(gameContinues) {
                        // if game did not end computer should discard
                        game.discardTileForComputer();
                        game.passTurnToNextPlayer();
                    }
                    else{
                        if(!game.didGameFinish()) {
                            // current computer character wins
                            System.out.println(game.getCurrentPlayerName() + " wins.");
                        }
                        else{
                            // TODO (KAAN): the game ended with no more tiles in the stack
                            // determine the winner based on longest chain lengths of the players
                            // use getPlayerWithHighestLongestChain method of game for this task

                            System.out.print("No more tiles left in the stack, ");
                            printPlayers(game.getPlayerWithHighestLongestChain()); // Now printing player names instead of reference ~ omer
                            System.out.println(" wins !");
                        }
                    }
                }
            }
        }
    }

    public static void printPlayers(Player[] players)
    {
        if (players.length == 1)
        {
            System.out.print(players[0].getName());
        }
        else if (players.length == 2)
        {
            System.out.print(players[0].getName() + " and " + players[1].getName());
        }
        else
        {
            for (int i = 0; i < players.length - 1; i ++)
            {
                System.out.print(players[i].getName() + ", ");
            }
            System.out.print("and " + players[players.length - 1].getName());
        }
    }
}