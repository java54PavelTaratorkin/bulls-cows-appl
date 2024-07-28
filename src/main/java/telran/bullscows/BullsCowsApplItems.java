package telran.bullscows;

import java.util.*;
import telran.view.*;

public class BullsCowsApplItems {
    private static final int CODE_LENGTH = 4;
	private static long currentGameId = -1;
    private static List<MoveResult> history = new ArrayList<>();
    private static boolean gameOver = false;
    private static BullsCowsService bullsCows;

    public static List<Item> getItems(BullsCowsService bullsCowsService) {
        BullsCowsApplItems.bullsCows = bullsCowsService;
        Item[] items = {
            Item.of("Start new game", BullsCowsApplItems::startNewGame),
            Item.ofExit()
        };
        return new ArrayList<>(List.of(items));
    }

    private static void startNewGame(InputOutput io) {
        currentGameId = bullsCows.createNewGame();
        io.writeLine("New game started. Game ID: " + currentGameId);
        history.clear();
        gameOver = false;
        showGameMenu(io);
    }

    private static void showGameMenu(InputOutput io) {
        List<Item> submenuItems = List.of(
            Item.of("Enter 4 non-repeated digits [‘0’ - ‘9’]", BullsCowsApplItems::playGame),
            Item.of("Exit", ioSubmenu -> gameOver = true, true)
        );

        Menu submenu = new Menu("Game Menu", submenuItems.toArray(Item[]::new));

        while (!gameOver) {
            submenu.perform(io);
        }
    }

    private static void playGame(InputOutput io) {
        if (!history.isEmpty()) {
            displayHistory(io);
        }
        String clientSequence = io.readStringPredicate(
                "Enter 4 non-repeated digits [‘0’ - ‘9’]: ",
                "Invalid input. Must be a 4-digit number without repetitions.",
                BullsCowsApplItems::isValidClientCode
        );
        Move move = new Move(currentGameId, clientSequence);
        try {
            history = bullsCows.getResults(currentGameId, move);
            gameOver = handleResponse(io);
        } catch (IllegalArgumentException e) {
            io.writeLine(e.getMessage());
        }
    }
    
    private static boolean isValidClientCode(String clientCode) {
        return clientCode.matches("\\d+") && clientCode.length() == CODE_LENGTH;
    }

    private static boolean handleResponse(InputOutput io) {
        boolean isGameOver = bullsCows.isGameOver(currentGameId);
        printLastMove(io, history.getLast());
        if (isGameOver) {                	
        	io.writeLine("Game is over");            
        }
        return isGameOver;
    }

    private static void displayHistory(InputOutput io) {
        io.writeLine("History of all results:");
        history.forEach(result -> printLastMove(io, result));
    }

	private static void printLastMove(InputOutput io, MoveResult result) {
		io.writeLine(
            "Sequence: " + result.clientSequence() + ", Bulls: " + result.bulls() + ", Cows: " + result.cows());
	}
}
