package hello.itemservice.domain.item;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ItemType {

    BOOK("도서"), FOOD("식품"), ETC("기타");

    private final String description;

}
