# Chapter 6: 상품 등록 및 조회하기

## 상품 등록하기



```java
@Getter
@Table(name = "item_img")
@Entity
public class ItemImg extends BaseEntity {

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String imgName;

    private String oriImgName;

    private String imgUrl;

    private Boolean repImg;

    @ManyToOne(fetch = FetchType.LAZY) // 1)
    @JoinColumn(name = "item_id")
    private Item item; 

    public void updateItemImg(String oriImgName, String imgName, String imgUrl) { // 2)
        this.oriImgName = oriImgName;
        this.imgName = imgName;
        this.imgUrl = imgUrl;
    }
    
}
```

1. 상품 엔티티와 다대일 단뱡항 관계로 매핑, 지연 로딩을 설정하여 매핑된 상품 엔티티 정보가 필요한 경우 데이터를 조회하도록 한다.
2. 원본 이미지 파일명, 업데이트할 이미지 파일명, 이미지 경로를 파라미터로 입력 받아서 이미지 정보를 업데이트하는 메소드

상품을 등록할 때는 화면으로부터 전달받은 DTO 객체를 엔티티 객체로 변환하는 작업을 해야 하고, 상품을 조회할 때는 엔티티 객체를 DTO 객체로 바꿔주는 작업을 해야한다. 멤버 변수가 몇 개 없다면 금방 할 수도 있지만 멤버 변수가 많아진다면 상당한 시간을 소모한다.

엔티티 <-> DTO 서로 반환해주는 반복적인 작업을 도와주는 라이브러리로 modelmapper가 있다. 해당 라이브러리는 서로 다른 클래스의 값을 필드의 이름과 자료형이 같으면 getter, setter를 통해 값을 복사해서 객체를 반환해준다.

```java
@Getter @Setter
public class ItemImgDto {

    private Long id;

    private String imgName;

    private String oriImgName;

    private String imgUrl;

    private Boolean repImg;

    private static ModelMapper modelMapper = new ModelMapper();

    public static ItemImgDto of(ItemImg itemImage) {
        return modelMapper.map(itemImage,ItemImgDto.class);
    }

}
```

상품 등록 같은 관리자 페이지에서 중요한 것은 데이터의 무결성을 보장해야 한다는 것이다. 데이터가 의도와 다르게 저장된다거나, 잘못된 값이 저장되지 않도록 밸리데이션(validation)을 해야 한다. 
특히 데이터끼리 서로 연관이 되어 있으면 어떤 데이터가 변함에 따라서 다른 데이터도 함께 체크를 해야 하는 경우가 많다.


![image](https://user-images.githubusercontent.com/83503188/169641399-37e0cb32-488a-4cc9-8016-1ea28cf8cc57.png)

### 이미지 파일에 대한 설정 

이미지 파일을 등록할 때 서버에서 각 파일의 최대 사이즈와 한번에 다운 요청할 수 있는 파일의 크기를 지정할 수 있다. 또한 컴퓨터에서 어떤 경로에 저장할지를 관리하기 위해서 프로퍼티에 itemImgLocation을 추가한다.

```java
  servlet:
    multipart:
      max-file-size: 10MB # 파일 업로드 요청 시 하나의 파일 크기 10MB로 제한
      max-request-size: 100MB # 파일 업로드 요청 시 모든 파일의 크기의합 100MB로 제한

file:
  upload:
    path: /C:/Users/dudwl/temp/
```

### 업로드한 파일을 읽어올 경로를 설정

WebMvcConfigurer 인터페이스를 구현하는 WebMvcConfig.java 파일을 작성한다. addResourceHandlers 메소드를 통해서 자신의 로컬 컴퓨터에 업로드한 파일을 찾을 위치를 설정한다.

```java
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Value("${file.upload.path}") // 1)
    private String uploadPath;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        registry.addResourceHandler("/images/**")   // 2)
                .addResourceLocations("file://" + uploadPath); // 3)
    }

}
```

1. application.yml에 설정한 경로를 불러온다.
2. 웹 브라우저에 입력하는 url에 /images로 시작하는 경우 `file.upload.path`에 설정한 폴더를 기준으로 파일을 읽어오도록 설정
3. 로컬 컴퓨터에 저장된 파일을 읽어올 root경로를 설정한다.

### 파일을 처리하는 FileService 

```java
@Slf4j
@Service
public class FileService {

    @Value("${file.upload.path}")
    private String fileUploadPath;

    public String getFullFileUploadPath(String filename) {
        return fileUploadPath + filename;
    }

    public UploadFile storeFile(MultipartFile multipartFile) throws IOException {

        if(multipartFile.isEmpty()) {
            return new UploadFile("", "", "");
        }

        String originalFilename = multipartFile.getOriginalFilename();
        String storeFileName = createStoreFileName(originalFilename);
        String fileUploadUrl = getFullFileUploadPath(storeFileName);
        multipartFile.transferTo(new File(getFullFileUploadPath(storeFileName)));

        return new UploadFile(originalFilename, storeFileName, fileUploadUrl);

    }

    private String  createStoreFileName(String originalFilename) {
        String uuid = UUID.randomUUID().toString();
        String ext = extractExt(originalFilename);
        return uuid + "." + ext;
    }

    private String extractExt(String originalFilename) {
        int pos = originalFilename.lastIndexOf(".");
        return originalFilename.substring(pos + 1);
    }

    public List<UploadFile> storeFiles(List<MultipartFile> multipartFiles) throws IOException {
        List<UploadFile> storeFileResult = new ArrayList<>();
        for (MultipartFile multipartFile : multipartFiles) {
            if(!multipartFile.isEmpty()) {
                UploadFile uploadFile = storeFile(multipartFile);
                storeFileResult.add(uploadFile);
            }
        }
        return storeFileResult;
    }

    public void deleteFile(String fileUploadUrl) {
        File deleteFile = new File(fileUploadUrl);
        if(deleteFile.exists()) {
            deleteFile.delete();
            log.info("파일을 삭제하였습니다.");
        } else {
            log.info("파일이 존재하지 않습니다.");
        }
    }

}
```

### 상품 등록하는 AdminItemService

```java
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AdminItemService {

    private final ItemService itemService;
    private final MemberService memberService;
    private final ItemImageService itemImageService;

    @Transactional
    public Item saveItem(ItemFormDto dto, String email) throws IOException {

        // 회원 조회
        Member member = memberService.getMemberByEmail(email);

        // 상품 등록
        Item item = dto.toEntity();
        Item saveItem = Item.createItem(item, member);
        saveItem = itemService.saveItem(saveItem);

        // 상품 이미지 등록
        itemImageService.saveItemImages(saveItem, dto.getItemImageFiles());

        return saveItem;
    }

}
```

### 상품을 등록하는 AdminItemController

```java
@Slf4j
@Controller
@RequestMapping("/admin/items")
@RequiredArgsConstructor
public class AdminItemController {

    private final AdminItemService adminItemService;
    
    ...

    @PostMapping("/new")
    private String itemNew(
            @Valid @ModelAttribute("itemFormDto") ItemFormDto dto,
            BindingResult bindingResult,
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            RedirectAttributes redirectAttributes
    ) {

        if (dto.getItemImageFiles().get(0).isEmpty()) { // 1)
            bindingResult.reject("requiredFirstItemImage", ErrorCode.REQUIRED_REPRESENT_IMAGE.getMessage());
            return "adminitem/registeritemform";
        }
        if (bindingResult.hasErrors()) { // 2)
            return "adminitem/registeritemform";
        }

        String email = userDetails.getUsername();
        try {
            Item savedItem = adminItemService.saveItem(dto, email);
        } catch (Exception e) {
            log.error(e.getMessage());
            bindingResult.reject("globalError", "상품 등록 중 에러가 발생하였습니다.");
            return "adminitem/registeritemform";
        }

        return "redirect:/";
    }

}
```

1. 상품 등록 시 첫 번째 이미지가 없다면 에러 메시지와 함께 상품 등록 페이지로 전환한다. 상품의 첫 번째 이미지는 메인 페이지에서 보여줄 상품 이미지로 사용하기 위해서 필수 값으로 지정
2. 상품 등록 시 DTO에 정의한 Validation에 어긋나는 경우 다시 상품 등록 페이지로 전환한다.




