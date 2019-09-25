package stefanowicz.kacper.utils;

import stefanowicz.kacper.exceptions.AppException;
import stefanowicz.kacper.model.enums.ProductCategory;

import java.util.Arrays;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;

public final class UserDataService {

    private static Scanner scn  = new Scanner(System.in);

    private UserDataService(){}

    public static int getInt(String message){
        System.out.println(message);

        String text = scn.nextLine();

        if(!text.matches("\\d+")){
            throw new AppException("This is not int value");
        }
        return Integer.parseInt(text);
    }

    public static ProductCategory getProductCategory(){
        int choice;
        AtomicInteger counter = new AtomicInteger(0);
        Arrays
                .stream(ProductCategory.values())
                .forEach(productCategory ->
                        System.out.println(counter.incrementAndGet() + ". " + productCategory));
        choice = getInt("Choose product category: ");

        if(choice < 1 || choice > ProductCategory.values().length){
            throw new AppException("No category with given number!");
        }

        return ProductCategory.values()[choice - 1];
    }

    public static void close(){
       if( scn !=null ){
           scn.close();
           scn = null;
       }
    }
}
