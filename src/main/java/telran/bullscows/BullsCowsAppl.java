package telran.bullscows;

import java.util.List;

import telran.view.InputOutput;
import telran.view.Item;
import telran.view.Menu;
import telran.view.SystemInputOutput;

public class BullsCowsAppl {
    public static void main(String[] args) {
        BullsCowsService service = new BullsCowsMapImpl();
        List<Item> items = BullsCowsApplItems.getItems(service);
        InputOutput io = new SystemInputOutput();
        Menu menu = new Menu("Bulls and Cows Game", items.toArray(new Item[0]));
        menu.perform(io);
    }
}