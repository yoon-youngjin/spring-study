# 스프링 MVC - 웹 페이지 만들기

### 요구사항 분석

**상품 도메인 모델**
- 상품 ID
- 상품명
- 가격

**상품 관리 기능**
- 상품 목록
- 상품 상세
- 상품 등록
- 상품 수정

**서비스 화면**

![image](https://user-images.githubusercontent.com/83503188/205283242-f101d3b9-d065-4253-b2dc-e57ced21eb1f.png)

![image](https://user-images.githubusercontent.com/83503188/205283268-3b67f663-f52b-4f70-9b36-c2d27f922f18.png)

![image](https://user-images.githubusercontent.com/83503188/205283314-efc2e5ea-bca5-43f3-83cb-1138f8d758ba.png)

![image](https://user-images.githubusercontent.com/83503188/205283343-eb560ca2-e4a6-4ac3-941f-10ad1743596c.png)


**서비스 제공 흐름**

![image](https://user-images.githubusercontent.com/83503188/205446232-92d0156d-9acf-4400-820f-9c59e0dadafc.png)

### 상품 도메인 개발

**Item - 상품 객체**

```java
@Data
public class Item {
    private Long id;
    private String itemName;
    private Integer price;
    private Integer quantity;
    public Item() {}

    public Item(String itemName, Integer price, Integer quantity) {
        this.itemName = itemName;
        this.price = price;
        this.quantity = quantity;
    }
}
```

**ItemRepository - 상품 저장소**

```java
@Repository
public class ItemRepository {
    private static final Map<Long, Item> store = new HashMap<>(); //static 사용
    private static long sequence = 0L; //static 사용
    
    public Item save(Item item) {
        item.setId(++sequence);
        store.put(item.getId(), item);
        return item;
    }
    public Item findById(Long id) {
        return store.get(id);
    }
    
    public List<Item> findAll() {
        return new ArrayList<>(store.values());
    }
    
    public void update(Long itemId, Item updateParam) {
        Item findItem = findById(itemId);
        findItem.setItemName(updateParam.getItemName());
        findItem.setPrice(updateParam.getPrice());
        findItem.setQuantity(updateParam.getQuantity());
    }
    public void clearStore() {
        store.clear();
    }
}
```