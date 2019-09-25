package stefanowicz.kacper.main;

import stefanowicz.kacper.menu.DataMenuService;
import stefanowicz.kacper.menu.MenuService;

public class App {
    public static void main( String[] args ) {
        final String FILE_NAME = "files/shopping.json";
        final String GENERATED_DATA_FILENAME = "files/shopping.json";
        DataMenuService dataMenuService = new DataMenuService(GENERATED_DATA_FILENAME);
        MenuService menu = new MenuService(FILE_NAME);
        menu.mainMenu();
    }
}
