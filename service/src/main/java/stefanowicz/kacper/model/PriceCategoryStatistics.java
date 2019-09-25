package stefanowicz.kacper.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import stefanowicz.kacper.model.enums.ProductCategory;

import java.math.BigDecimal;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PriceCategoryStatistics {
    private Map<ProductCategory, BigDecimal> averagePriceInCategory;
    private Map<ProductCategory, Product> mostExpensiveProduct;
    private Map<ProductCategory, Product> leastExpensiveProduct;
}
