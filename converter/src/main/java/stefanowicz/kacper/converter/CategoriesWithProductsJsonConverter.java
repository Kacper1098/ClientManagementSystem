package stefanowicz.kacper.converter;

import stefanowicz.kacper.help.CategoriesWithProducts;

import java.util.List;

public class CategoriesWithProductsJsonConverter extends JsonConverter<List<CategoriesWithProducts>> {
    public CategoriesWithProductsJsonConverter(String fileName) {
        super(fileName);
    }
}
