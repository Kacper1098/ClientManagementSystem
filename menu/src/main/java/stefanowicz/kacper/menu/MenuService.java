package stefanowicz.kacper.menu;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import stefanowicz.kacper.exceptions.AppException;
import stefanowicz.kacper.model.Client;
import stefanowicz.kacper.model.enums.ProductCategory;
import stefanowicz.kacper.service.ShoppingService;
import stefanowicz.kacper.utils.UserDataService;

import java.util.Map;

public class MenuService {

    private final ShoppingService shoppingService;

    public MenuService(String... fileNames) {
        this.shoppingService = new ShoppingService(fileNames);
    }
    public void mainMenu() {

        int option;
        do {
            try {
                option = printMenu();
                switch (option) {
                    case 1 -> option1();
                    case 2 -> option2();
                    case 3 -> option3();
                    case 4 -> option4();
                    case 5 -> option5();
                    case 6 -> option6();
                    case 0 -> {
                        UserDataService.close();
                        System.out.println("See you soon!");
                        System.exit(0);
                    }
                    default -> System.out.println("There is no such option!");
                }
            } catch (Exception e) {
                System.out.println("---------------------------------");
                System.out.println("----------- EXCEPTION -----------");
                System.out.println(e.getMessage());
                System.out.println("---------------------------------");
            }
        } while (true);
    }

    public int printMenu() {
        System.out.println("1. Client that spent most money on shopping.");
        System.out.println("2. Client that spent most money on shopping in given category.");
        System.out.println("3. Most popular categories, grouped by age.");
        System.out.println("4. Product category statistics.");
        System.out.println("5. Most frequent clients in each category.");
        System.out.println("6. Clients debts.");
        System.out.println("0. Exit");
        return UserDataService.getInt("Choose an option: ");
    }

    private void option1() {
        Client spentMost = shoppingService.spentMostMoney();
        System.out.println(toJson(spentMost));
    }

    private void option2() {
        ProductCategory category = UserDataService.getProductCategory();
        Client spentMostInCategory = shoppingService.spendMostMoneyInCategory(category);
        System.out.println(toJson(spentMostInCategory));
    }

    private void option3() {
        Map<Integer, ProductCategory> categoriesByAge = shoppingService.mostPopularCategoryByAge();
        System.out.println(toJson(categoriesByAge));
    }

    private void option4() {
        var priceCategoryStatistics = shoppingService.productInCategorySummaryStatistics();
        System.out.println(toJson(priceCategoryStatistics));
    }

    private void option5() {
        var buyersByCategory = shoppingService.mostOftenBuyersByCategory();
        System.out.println(toJson(buyersByCategory));
    }

    private void option6() {
        var debts = shoppingService.getClientsDebts();
        debts.forEach((k, v) -> System.out.println(toJson(k) + " -> " + v));
    }

    private static <T> String toJson(T t) {
        try {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            return gson.toJson(t);
        } catch (Exception e) {
            throw new AppException("to json conversion exception in menu service");
        }
    }

}
