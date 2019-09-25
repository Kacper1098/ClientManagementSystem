package stefanowicz.kacper.converter;

import stefanowicz.kacper.model.ClientWithProducts;

import java.util.List;

public class ClientsWithProductsJsonConverter extends JsonConverter<List<ClientWithProducts>> {
    public ClientsWithProductsJsonConverter(String fileName) {
        super(fileName);
    }
}
