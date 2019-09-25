package stefanowicz.kacper.help;

import com.github.javafaker.Faker;
import stefanowicz.kacper.converter.CategoriesWithProductsJsonConverter;
import stefanowicz.kacper.converter.ClientsWithProductsJsonConverter;
import stefanowicz.kacper.converter.ProductCategoriesJsonConverter;
import stefanowicz.kacper.exceptions.AppException;
import stefanowicz.kacper.model.Client;
import stefanowicz.kacper.model.ClientWithProducts;
import stefanowicz.kacper.model.Product;
import stefanowicz.kacper.model.enums.ProductCategory;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DataGeneratorService {
    private final Faker faker = new Faker();
    private final String PRODUCTS_FILE = "files/random/products.json";
    private final String CATEGORIES_FILE = "files/random/categories.json";
    private final String SHOPPING_FILE;

    public DataGeneratorService(String fileName){
        SHOPPING_FILE = fileName;
    }

    public void generateNewShoppingFile(int numberOfClients){
        List<ClientWithProducts> clientWithProducts = new ArrayList<>();
        var converter = new ClientsWithProductsJsonConverter(SHOPPING_FILE);
        for (int i = 0; i < numberOfClients; i++) {
            clientWithProducts.add(generateClientWithProducts());
        }
        converter.toJson(clientWithProducts);
        System.out.println("----- DATA GENERATED SUCCESSFULLY -----");
    }

    private ClientWithProducts generateClientWithProducts(){
        Client client = generateNewClient();
        List<Product> products = new ArrayList<>();
        int numberOfProducts = new Random().nextInt(11) + 5;
        for (int i = 0; i < numberOfProducts; i++) {
            products.add(generateNewProduct());
        }

        return ClientWithProducts
                .builder()
                .customer(client)
                .products(products)
                .build();
    }


    private Client generateNewClient(){
        Random rnd = new Random();
        BigDecimal cash = new BigDecimal(Math.random()).multiply(new BigDecimal(5000)).setScale(1, RoundingMode.HALF_DOWN);
        return Client
                .builder()
                .firstName(faker.name().firstName())
                .lastName(faker.name().lastName())
                .age(rnd.nextInt(58) + 18)
                .cash(cash)
                .build();
    }

    private ProductCategory getRandomCategory(){
        var converter = new ProductCategoriesJsonConverter(CATEGORIES_FILE);
        List<ProductCategory> categories = converter.fromJson().orElseThrow(() -> new AppException("Error while converting categories from json"));

        return categories.get(new Random().nextInt(categories.size()));
    }

    private String getProductNameFromCategory(ProductCategory category){
        var converter = new CategoriesWithProductsJsonConverter(PRODUCTS_FILE);
        List<CategoriesWithProducts> categoriesWithProducts = converter
                        .fromJson()
                        .orElseThrow(() -> new AppException("Error while converting categories with products from json"));
        CategoriesWithProducts cwp1 = categoriesWithProducts
                .stream()
                .filter(cwp -> cwp.getCategory().equals(category))
                .findFirst()
                .orElseThrow(() -> new AppException("Error while filtering categories with products"));

        return cwp1.getProducts().get(new Random().nextInt(cwp1.getProducts().size()));
    }

    private BigDecimal getPriceByCategory(ProductCategory category){
        BigDecimal price;
        switch (category){
            case AGD -> price =  new BigDecimal(Math.random()).multiply(new BigDecimal(2000)).setScale(1, RoundingMode.HALF_DOWN).add(new BigDecimal(1000));
            case FOOD -> price = new BigDecimal(Math.random()).multiply(new BigDecimal(5)).setScale(1, RoundingMode.HALF_DOWN).add(new BigDecimal(2));
            case PETS -> price = new BigDecimal(Math.random()).multiply(new BigDecimal(30)).setScale(1, RoundingMode.HALF_DOWN).add(new BigDecimal(15));
            case BOOKS -> price = new BigDecimal(Math.random()).multiply(new BigDecimal(40)).setScale(1, RoundingMode.HALF_DOWN).add(new BigDecimal(20));
            case CLOTHES -> price = new BigDecimal(Math.random()).multiply(new BigDecimal(200)).setScale(1, RoundingMode.HALF_DOWN).add(new BigDecimal(70));
            case FURNITURE -> price = new BigDecimal(Math.random()).multiply(new BigDecimal(1000)).setScale(1, RoundingMode.HALF_DOWN).add(new BigDecimal(200));
            default -> throw new AppException("Could not find given product category " + category);
        }
        return price;
    }

    private Product generateNewProduct(){
        ProductCategory category = getRandomCategory();
        return Product
                .builder()
                .category(category)
                .name(getProductNameFromCategory(category))
                .price(getPriceByCategory(category))
                .build();
    }



}
