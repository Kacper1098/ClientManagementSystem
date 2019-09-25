package stefanowicz.kacper.converter;

import stefanowicz.kacper.model.enums.ProductCategory;

import java.util.List;

public class ProductCategoriesJsonConverter extends JsonConverter<List<ProductCategory>> {
    public ProductCategoriesJsonConverter(String fileName) {
        super(fileName);
    }
}
