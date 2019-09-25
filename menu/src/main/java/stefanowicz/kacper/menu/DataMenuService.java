package stefanowicz.kacper.menu;

import stefanowicz.kacper.help.DataGeneratorService;
import stefanowicz.kacper.utils.UserDataService;

public class DataMenuService {
    public DataMenuService(String fileName){
        dataMenu(fileName);
    }
    public void dataMenu(String fileName){
        int option;
        do {
            try{
                option = printDataMenu();
                switch (option){
                    case 1 -> {
                        generateData(UserDataService.getInt("Enter number of clients to generate: "), fileName);
                        return;
                    }
                    case 2 ->{ return;}
                    case 0 -> {
                        UserDataService.close();
                        System.out.println("See you soon!");
                        System.exit(0);
                    }
                    default -> System.out.println("There is no such option!");
                }
            }catch (Exception e){
                System.out.println("---------------------------------");
                System.out.println("----------- EXCEPTION -----------");
                System.out.println(e.getMessage());
                System.out.println("---------------------------------");
            }
        }while(true);
    }

    private void generateData(int numberOfClients, String fileName){
        DataGeneratorService dataGeneratorService = new DataGeneratorService(fileName);
        dataGeneratorService.generateNewShoppingFile(numberOfClients);
    }

    private int printDataMenu(){
        System.out.println("1. Generate new data");
        System.out.println("2. Continue with previous data");
        System.out.println("0. Exit");
        return UserDataService.getInt("Choose an option: ");
    }
}
