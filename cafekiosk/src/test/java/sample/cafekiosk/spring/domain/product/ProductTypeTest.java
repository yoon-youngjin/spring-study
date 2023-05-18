package sample.cafekiosk.spring.domain.product;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

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

    @Test
    @DisplayName("상품 타입이 재고 관련 타입(BOTTLE, BAKERY)인지를 체크한다. - Treu case")
    void containsStockType3() {
        // given
        ProductType givenType1 = ProductType.HANDMADE;
        ProductType givenType2 = ProductType.BAKERY;
        ProductType givenType3 = ProductType.BAKERY;

        // when
        boolean result1 = ProductType.containsStockType(givenType1);
        boolean result2 = ProductType.containsStockType(givenType2);
        boolean result3 = ProductType.containsStockType(givenType3);

        // then
        assertThat(result1).isFalse();
        assertThat(result2).isTrue();
        assertThat(result3).isTrue();
    }

    @DisplayName("상품 타입이 재고 관련 타입(BOTTLE, BAKERY)인지를 체크한다. - Treu case")
    @CsvSource({"HANDMADE,false", "BOTTLE,true", "BAKERY, true"})
    @ParameterizedTest
    void containsStockType4(ProductType productType, boolean expected) {

        // when
        boolean result = ProductType.containsStockType(productType);

        // then
        assertThat(result).isEqualTo(expected);
    }

    private static Stream<Arguments> provideProductTypesForCheckingStockType() {
        return Stream.of(
                Arguments.of(ProductType.HANDMADE, false),
                Arguments.of(ProductType.BAKERY, true),
                Arguments.of(ProductType.BOTTLE, true)
        );
    }

    @DisplayName("상품 타입이 재고 관련 타입(BOTTLE, BAKERY)인지를 체크한다. - True case")
    @MethodSource("provideProductTypesForCheckingStockType")
    @ParameterizedTest
    void containsStockType5(ProductType productType, boolean expected) {

        // when
        boolean result = ProductType.containsStockType(productType);

        // then
        assertThat(result).isEqualTo(expected);
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