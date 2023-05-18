package sample.cafekiosk.spring.domain.product;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class ProductTypeTest {

    @Test
    @DisplayName("상품 타입이 재고 관련 타입(BOTTLE, BAKERY)인지를 체크한다. - False case")
    void containsStockType1() {
        // given
        ProductType givenType = ProductType.HANDMADE;

        // when
        boolean result = ProductType.containsStockType(givenType);

        // then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("상품 타입이 재고 관련 타입(BOTTLE, BAKERY)인지를 체크한다. - Treu case")
    void containsStockType2() {
        // given
        ProductType givenType = ProductType.BAKERY;

        // when
        boolean result = ProductType.containsStockType(givenType);

        // then
        assertThat(result).isTrue();
    }

//    @Test
//    @DisplayName("상품 타입이 재고 관련 타입(BOTTLE, BAKERY)인지를 체크한다.")
//    void containsStockTypeEx() {
//        // given
//        ProductType[] productTypes = ProductType.values();
//
//        for (ProductType productType : productTypes) {
//            if (productType == ProductType.HANDMADE) {
//                // when
//                boolean result = ProductType.containsStockType(productType);
//
//                // then
//                assertThat(result).isFalse();
//            }
//
//            if (productType == ProductType.BOTTLE || productType == ProductType.BAKERY) {
//                // when
//                boolean result = ProductType.containsStockType(productType);
//
//                // then
//                assertThat(result).isTrue();
//            }
//        }
//    }

}