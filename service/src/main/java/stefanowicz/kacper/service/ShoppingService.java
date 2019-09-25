package stefanowicz.kacper.service;

import org.eclipse.collections.impl.collector.BigDecimalSummaryStatistics;
import org.eclipse.collections.impl.collector.Collectors2;
import stefanowicz.kacper.converter.ClientsWithProductsJsonConverter;
import stefanowicz.kacper.exceptions.AppException;
import stefanowicz.kacper.model.Client;
import stefanowicz.kacper.model.ClientWithProducts;
import stefanowicz.kacper.model.PriceCategoryStatistics;
import stefanowicz.kacper.model.Product;
import stefanowicz.kacper.model.enums.ProductCategory;
import stefanowicz.kacper.validators.ClientValidator;
import stefanowicz.kacper.validators.ProductValidator;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ShoppingService {

    private final Map<Client, Map<Product, Long>> shoppingHistory;

    public ShoppingService(String... fileNames) {
        shoppingHistory = getData(fileNames);
    }

    private Map<Client, Map<Product, Long>> getData(String... filenames) {

        if (filenames == null) {
            throw new AppException("filenames array is null");
        }
        var clientCounter = new AtomicInteger(0);
        return Arrays
                .stream(filenames)
                .map(filename ->
                        new ClientsWithProductsJsonConverter(filename)
                                .fromJson()
                                .orElseThrow(() -> new AppException("from json coversion exception")))
                .flatMap(Collection::stream)
                .filter(clientWithProducts -> validateClientWithProducts(clientWithProducts, clientCounter))
                .collect(Collectors.toMap(
                        ClientWithProducts::getCustomer,
                        cwp -> cwp.getProducts().stream().collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
                ));
    }

    private boolean validateClient(Client client, AtomicInteger clientCounter){
        var clientValidator = new ClientValidator();

        var clientErrors =  clientValidator.validate(client);
        if ( clientValidator.hasErrors() ){
            System.out.println("--------------------------------------");
            System.out.println("-- Validation error for client no. " + clientCounter.get() + " --");
            System.out.println("--------------------------------------");
            clientErrors.forEach( ( k, v ) -> System.out.println(k + ": " + v) );
        }
        return !clientValidator.hasErrors();
    }

    private boolean validateProduct(Product product, AtomicInteger productCounter){
        var productValidator =  new ProductValidator();
        var productErrors = productValidator.validate(product);
        if( productValidator.hasErrors() ){
            System.out.println("--------------------------------------");
            System.out.println("-- Validation error for product no. " + productCounter.get() + " product no. " + productCounter.get() + " --");
            System.out.println("--------------------------------------");
            productErrors.forEach( ( k, v ) -> System.out.println(k + ": " + v) );
        }
        return !productValidator.hasErrors();
    }

    private boolean validateClientWithProducts(ClientWithProducts clientWithProducts, AtomicInteger clientCounter){

        var productCounter = new AtomicInteger(0);
        clientWithProducts.setProducts(clientWithProducts.getProducts().stream().filter(product -> {
            productCounter.incrementAndGet();
            return validateProduct(product, productCounter);
        }).collect(Collectors.toList()));

        clientCounter.incrementAndGet();
        return validateClient(clientWithProducts.getCustomer(), clientCounter);
    }


    /**
     * @return Client that spent most money.
     */
    public Client spentMostMoney() {
        return this.shoppingHistory
                .entrySet()
                .stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> e.getValue()
                                .entrySet()
                                .stream()
                                // masz dzialac tylko i wylacznie na BigDecimal!!!!
                                .map(e1 -> e1.getKey().getPrice().multiply(new BigDecimal(e1.getValue())))
                                .reduce(BigDecimal.ZERO, BigDecimal::add)
                ))
                .entrySet()
                .stream()
                .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
                .map(Map.Entry::getKey)
                .findFirst()
                .orElseThrow(() -> new AppException("Error while looking for client that spent most money"));
    }

    /**
     * @param productCategory Category in which to search for client.
     * @return Client that spent most money in given category.
     */
    public Client spendMostMoneyInCategory(ProductCategory productCategory) {
        return this.shoppingHistory
                .entrySet()
                .stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> e.getValue()
                                .entrySet()
                                .stream()
                                .filter(e1 -> e1.getKey().getCategory().equals(productCategory))
                                .map(e1 -> e1.getKey().getPrice().multiply(new BigDecimal(e1.getValue())))
                                .reduce(BigDecimal.ZERO, BigDecimal::add)
                ))
                .entrySet()
                .stream()
                .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
                .map(Map.Entry::getKey)
                .findFirst()
                .orElseThrow(() -> new AppException("Error while looking for client that spent most money"));
    }


    /**
     *
     * @return Most popular category grouped by age.
     */
    public Map<Integer, ProductCategory> mostPopularCategoryByAge() {

      return  shoppingHistory
                .entrySet().stream()
                .collect(Collectors.groupingBy(cwp -> cwp.getKey().getAge()))
                .entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> e.getValue()
                                .stream().flatMap(ee -> ee
                                        .getValue()
                                        .entrySet().stream()
                                        .flatMap(eee -> Collections.nCopies(eee.getValue().intValue(), eee.getKey()).stream()))
                        .collect(Collectors.groupingBy(Product::getCategory, Collectors.counting()))
                        .entrySet().stream()
                        .max(Comparator.comparing(Map.Entry::getValue))
                        .orElseThrow()
                        .getKey()
                ));
    }

    /**
     * @return Summary statistics that contains average price in each category, most expensive product in each category and least expensive product in each category
     */
public PriceCategoryStatistics productInCategorySummaryStatistics(){
        var average = this.shoppingHistory
                .entrySet()
                .stream()
                .flatMap(e->e.getValue().entrySet().stream())
                .collect(Collectors.groupingBy(e1 -> e1.getKey().getCategory(), Collectors.collectingAndThen(
                        Collectors2.summarizingBigDecimal(en -> en.getKey().getPrice()),
                        BigDecimalSummaryStatistics::getAverage
                )));
        var maximum =  this.shoppingHistory
                .entrySet()
                .stream()
                .flatMap(e->e.getValue().entrySet().stream())
                .collect(Collectors.groupingBy(e->e.getKey().getCategory(),Collectors.collectingAndThen(
                        Collectors.maxBy((e1, e2) -> e2.getKey().getPrice().compareTo(e1.getKey().getPrice())),
                        max->max.orElseThrow(()->new AppException("THere is no maximum value")).getKey()
                )));
        var minimum =  this.shoppingHistory
            .entrySet()
            .stream()
            .flatMap(e->e.getValue().entrySet().stream())
            .collect(Collectors.groupingBy(e1->e1.getKey().getCategory(),Collectors.collectingAndThen(
                    Collectors.minBy(Comparator.comparing(e -> e.getKey().getPrice())),
                    min->min.orElseThrow(()->new AppException("There is no minimum value")).getKey()
            )));
        return PriceCategoryStatistics.builder()
                .averagePriceInCategory(average)
                .mostExpensiveProduct(maximum)
                .leastExpensiveProduct(minimum)
                .build();
}

    /**
     *
     * @return Map with product category as key and client that bought most products in this category
     */
    public Map<ProductCategory, Client> mostOftenBuyersByCategory(){
        return this.shoppingHistory
        .entrySet()
        .stream()
        .flatMap(e->e.getValue().entrySet().stream())
        .map(e->e.getKey().getCategory())
        .distinct()
        .collect(Collectors.toMap(
                Function.identity(),
                product -> shoppingHistory
                    .entrySet().stream()
                    .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> e.getValue()
                            .entrySet().stream()
                            .filter(en->en.getKey().getCategory().equals(product))
                            .mapToLong(Map.Entry::getValue)
                            .sum()
                    ))
        .entrySet()
        .stream()
        .sorted((e1,e2)->Long.compare(e2.getValue(),e1.getValue()))
        .map(Map.Entry::getKey)
        .findFirst()
        .orElseThrow(() -> new AppException("Error while looking for most often buyer"))));
    }

/**
 *
 * @return Map with client as key and dept which client has to pay off.
 */
public Map<Client, BigDecimal> getClientsDebts(){
        return this.shoppingHistory
            .entrySet()
            .stream()
            .collect(Collectors.toMap(
                    Map.Entry::getKey,
                    e->{
                        BigDecimal dlug = e.getValue()
                            .entrySet().stream()
                            .map(e1->e1.getKey().getPrice().multiply(new BigDecimal(e1.getValue())))
                            .reduce(BigDecimal.ZERO, BigDecimal::add).subtract(e.getKey().getCash());
                        return dlug.compareTo(BigDecimal.ZERO) > 0 ? dlug : BigDecimal.ZERO;
                    }));
    }
}