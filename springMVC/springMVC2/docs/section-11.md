# 파일 업로드

## 파일 업로드 소개 

일반적으로 사용하는 HTML Form을 통한 파일 업로드를 이해하려면 먼저 폼을 전송하는 다음 두 가지 방식의 차이를 이해해야 한다.

**HTML 폼 전송 방식**
- `application/x-www-form-urlencoded`
- `multipart/form-data`

**application/x-www-form-urlencoded 방식**

![image](https://user-images.githubusercontent.com/83503188/212910824-ab4b5053-deac-4a64-9dc8-218c22bf2161.png)
- Form 태그에 별도의 enctype 옵션이 없으면 웹 브라우저는 요청 HTTP 메시지의 헤더에 다음 내용을 추가한다.
  - `Content-Type: application/x-www-form-urlencoded`
- 폼에 입력한 전송할 항목을 HTTP Body에 문자로 `username=kim&age=20` 와 같이 `&` 로 구분해서 전송한다.

파일을 업로드 하려면 파일은 문자가 아니라 바이너리 데이터를 전송해야 한다. 문자를 전송하는 이 방식으로 파일을 전송하기는 어렵다.
그리고 또 한가지 문제가 더 있는데, 보통 폼을 전송할 때 파일만 전송하는 것이 아니라는 점이다.

다음 예를 보자.

```text
- 이름
- 나이
- 첨부파일
```

문자와 바이너리를 동시에 전송해야 하는 상황이다.

이 문제를 해결하기 위해 HTTP는 `multipart/form-data` 라는 전송 방식을 제공한다.

**multipart/form-data 방식**

![image](https://user-images.githubusercontent.com/83503188/212911445-23d3db22-85ca-45ce-adb7-6bdaa02db31d.png)
- 이 방식을 사용하려면 Form 태그에 별도의 `enctype="multipart/form-data"` 를 지정해야 한다.
- `multipart/form-data` 방식은 다른 종류의 여러 파일과 폼의 내용 함께 전송할 수 있다.

폼의 입력 결과로 생성된 HTTP 메시지를 보면 각각의 전송 항목이 구분이 되어있다. `Content-Disposition` 이라는 항목별 헤더가 추가되어 있고 여기에 부가 정보가 있다.
예제에서는 `username` , `age` , `file1` 이 각각 분리되어 있고, 폼의 일반 데이터는 각 항목별로 문자가 전송되고, 파일의 경우 파일 이름과 `Content-Type`이 추가되고 바이너리 데이터가 전송된다.

`multipart/form-data` 는 이렇게 각각의 항목을 구분해서, 한번에 전송하는 것이다.

**Part**

`multipart/form-data` 는 `application/x-www-form-urlencoded` 와 비교해서 매우 복잡하고 각각의 부분( `Part` )로 나누어져 있다.
그렇다면 이렇게 복잡한 HTTP 메시지를 서버에서 어떻게 사용할 수 있을까?

## 서블릿과 파일 업로드1

**ServletUploadControllerV1**

```java
@Slf4j
@Controller
public class ServletUploadControllerV1 {
    @GetMapping("/upload")
    public String newFile() {
        return "upload-form";
    }

    @PostMapping("/upload")
    public String saveFileV1(HttpServletRequest request) throws ServletException, IOException {
        log.info("request={}", request);
        String itemName = request.getParameter("itemName");
        log.info("itemName={}", itemName);
        Collection<Part> parts = request.getParts();
        log.info("parts={}", parts);
        return "upload-form";
    }
    
}

```

`resources/templates/upload-form.html`

```html
<!DOCTYPE HTML>
<html xmlns:th="http://www.thymeleaf.org">
<head>
    <meta charset="utf-8">
</head>
<body>
<div class="container">
    <div class="py-5 text-center">
        <h2>상품 등록 폼</h2>
    </div>
    <h4 class="mb-3">상품 입력</h4>
    <form th:action method="post" enctype="multipart/form-data">
        <ul>
            <li>상품명 <input type="text" name="itemName"></li>
            <li>파일<input type="file" name="file" ></li>
        </ul>
        <input type="submit"/>
    </form>
</div> <!-- /container -->
</body>
</html>
```

**결과**

![image](https://user-images.githubusercontent.com/83503188/212917316-23fdd9a4-3d50-4052-9532-497772d52e15.png)

![image](https://user-images.githubusercontent.com/83503188/212915866-cd28df97-2c70-42fc-be0b-5d62e121c4fa.png)

### 멀티파트 사용 옵션

**업로드 사이즈 제한**

```properties
spring.servlet.multipart.max-file-size=1MB
spring.servlet.multipart.max-request-size=10MB
```
- 큰 파일을 무제한 업로드하게 둘 수는 없으므로 업로드 사이즈를 제한할 수 있다.
- 사이즈를 넘으면 예외( `SizeLimitExceededException` )가 발생한다.
- `max-file-size` : 파일 하나의 최대 사이즈, 기본 1MB
- `max-request-size`: 멀티파트 요청 하나에 여러 파일을 업로드 할 수 있는데, 그 전체 합이다. 기본 10MB

**spring.servlet.multipart.enabled 끄기**
```properties
spring.servlet.multipart.enabled=false
```
- `spring.servlet.multipart.enabled` 옵션을 끄면 서블릿 컨테이너는 멀티파트와 관련된 처리를 하지 않는다.

> 참고
> 
> `spring.servlet.multipart.enabled` 옵션을 켜면 스프링의 `DispatcherServlet` 에서 멀티파트 리졸버( `MultipartResolver` )를 실행한다.
> 멀티파트 리졸버는 멀티파트 요청인 경우 서블릿 컨테이너가 전달하는 일반적인 `HttpServletRequest` 를 `MultipartHttpServletRequest` 로 변환해서 반환한다.
> 
> 스프링이 제공하는 기본 멀티파트 리졸버는 `MultipartHttpServletRequest` 인터페이스를 구현한 `StandardMultipartHttpServletRequest` 를 반환한다.

## 서블릿과 파일 업로드2

서블릿이 제공하는 `Part` 에 대해 알아보고 실제 파일도 서버에 업로드 해보자.

먼저 파일을 업로드를 하려면 실제 파일이 저장되는 경로가 필요하다.

```properties
file.dir= C:/Users/dudwl/workspace/spring-study-by-myself/springMVC/springMVC2/upload/file/
```

**ServletUploadControllerV2**

```java
@Slf4j
@Controller
@RequestMapping("/servlet/v2")
public class ServletUploadControllerV2 {

    @Value("${file.dir}")
    private String fileDir;

    @GetMapping("/upload")
    public String newFile() {
        return "upload-form";
    }

    @PostMapping("/upload")
    public String saveFileV1(HttpServletRequest request) throws
            ServletException, IOException {
        log.info("request={}", request);
        String itemName = request.getParameter("itemName");
        log.info("itemName={}", itemName);
        Collection<Part> parts = request.getParts();
        log.info("parts={}", parts);
        for (Part part : parts) {
            log.info("==== PART ====");
            log.info("name={}", part.getName());
            Collection<String> headerNames = part.getHeaderNames();
            for (String headerName : headerNames) {
                log.info("header {}: {}", headerName,
                        part.getHeader(headerName));
            }

            //편의 메서드
            //content-disposition; filename
            log.info("submittedFileName={}", part.getSubmittedFileName());
            log.info("size={}", part.getSize()); //part body size

            //데이터 읽기
            InputStream inputStream = part.getInputStream();
            String body = StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8);
            log.info("body={}", body);

            //파일에 저장하기
            if (StringUtils.hasText(part.getSubmittedFileName())) {
                String fullPath = fileDir + part.getSubmittedFileName();
                log.info("파일 저장 fullPath={}", fullPath);
                part.write(fullPath);
            }
        }
        return "upload-form";
    }

}

```

**Part 주요 메서드**
- `part.getSubmittedFileName()` : 클라이언트가 전달한 파일명
- `part.getInputStream()`: Part의 전송 데이터를 읽을 수 있다.
- `part.write(...)`: Part를 통해 전송된 데이터를 저장할 수 있다.

**결과**

![image](https://user-images.githubusercontent.com/83503188/212920743-40e3d0bc-0170-425f-924e-ce3ac892ad8d.png)

## 스프링과 파일 업로드

`스프링은 MultipartFile 이라는 인터페이스로 멀티파트 파일을 매우 편리하게 지원한다.`

**SpringUploadController**

```java
@Slf4j
@Controller
@RequestMapping("/spring")
public class SpringUploadController {

    @Value("${file.dir}")
    private String fileDir;

    @GetMapping("/upload")
    public String newFile() {
        return "upload-form";
    }

    @PostMapping("/upload")
    public String saveFile(@RequestParam String itemName, @RequestParam MultipartFile file, HttpServletRequest request) throws IOException {
        log.info("request={}", request);
        log.info("itemName={}", itemName);
        log.info("multipartFile={}", file);
        if (!file.isEmpty()) {
            String fullPath = fileDir + file.getOriginalFilename();
            log.info("파일 저장 fullPath={}", fullPath);
            file.transferTo(new File(fullPath));
        }
        return "upload-form";
    }
}
```
- `@RequestParam MultipartFile file`
  - 업로드하는 HTML Form의 name에 맞추어 `@RequestParam` 을 적용하면 된다.
  - `@ModelAttribute` 에서도 `MultipartFile` 을 동일하게 사용할 수 있다.

**MultipartFile 주요 메서드**
- `file.getOriginalFilename()` : 업로드 파일 명
- `file.transferTo(...)` : 파일 저장

**결과**

![image](https://user-images.githubusercontent.com/83503188/212921862-3271780f-6e69-4739-89cc-545da3cc29d4.png)

## 예제로 구현하는 파일 업로드, 다운로드

**요구사항**
- 상품을 관리
  - 상품 이름
  - 첨부파일 하나
  - 이미지 파일 여러개
- 첨부파일을 업로드 다운로드 할 수 있다.
- 업로드한 이미지를 웹 브라우저에서 확인할 수 있다.

**Item - 상품 도메인**
```java
@Data
public class Item {
    private Long id;
    private String itemName;
    private UploadFile attachFile; // 첨부파일
    private List<UploadFile> imageFiles; // 이미지 파일 여러개 
}
```

**ItemRepository - 상품 리포지토리**

```java
@Repository
public class ItemRepository {
    private final Map<Long, Item> store = new HashMap<>();
    private long sequence = 0L;

    public Item save(Item item) {
        item.setId(++sequence);
        store.put(item.getId(), item);
        return item;
    }

    public Item findById(Long id) {
        return store.get(id);
    }
}
```

**UploadFile - 업로드 파일 정보 보관**

```java
@Data
public class UploadFile {
    private String uploadFileName;
    private String storeFileName;

    public UploadFile(String uploadFileName, String storeFileName) {
        this.uploadFileName = uploadFileName;
        this.storeFileName = storeFileName;
    }
}
```
- `uploadFileName` : 고객이 업로드한 파일명
- `storeFileName` : 서버 내부에서 관리하는 파일명

고객이 업로드한 파일명으로 서버 내부에 파일을 저장하면 안된다. 왜냐하면 서로 다른 고객이 같은 파일이름을 업로드 하는 경우 기존 파일 이름과 충돌이 날 수 있다.
서버에서는 저장할 파일명이 겹치지 않도록 내부에서 관리하는 별도의 파일명이 필요하다.

**FileStore - 파일 저장과 관련된 업무 처리**

```java
@Component
public class FileStore {

    @Value("${file.dir}")
    private String fileDir;

    public String getFullPath(String filename) {
        return fileDir + filename;
    }

    public List<UploadFile> storeFiles(List<MultipartFile> multipartFiles) throws IOException {
        List<UploadFile> storeFileResult = new ArrayList<>();

        for (MultipartFile multipartFile : multipartFiles) {
            if (!multipartFile.isEmpty()) {
                storeFileResult.add(storeFile(multipartFile));
            }
        }
        return storeFileResult;
    }

    public UploadFile storeFile(MultipartFile multipartFile) throws IOException {
        if (multipartFile.isEmpty()) return null;

        String originalFilename = multipartFile.getOriginalFilename();
        String storeFileName = createStoreFileName(originalFilename);
        multipartFile.transferTo(new File(getFullPath(storeFileName)));
        return new UploadFile(originalFilename, storeFileName);
    }

    private String createStoreFileName(String originalFilename) {
        String ext = extractExt(originalFilename);
        String uuid = UUID.randomUUID().toString();
        return uuid + "." + ext;
    }

    private String extractExt(String originalFilename) {
        int pos = originalFilename.lastIndexOf(".");
        return originalFilename.substring(pos + 1);
    }

}
```

**ItemForm**

```java
@Data
public class ItemForm {
    private Long itemId;
    private String itemName;
    private List<MultipartFile> imageFiles;
    private MultipartFile attachFile;
}
```
- `List<MultipartFile> imageFiles` : 이미지를 다중 업로드 하기 위해 `MultipartFile` 를 사용했다.
- `MultipartFile attachFile` : 멀티파트는 `@ModelAttribute` 에서 사용할 수 있다.

**ItemController**

```java
@Slf4j
@Controller
@RequiredArgsConstructor
public class ItemController {

    private final ItemRepository itemRepository;
    private final FileStore fileStore;

    @GetMapping("/items/new")
    public String newItem(@ModelAttribute ItemForm form) {
        return "item-form";
    }

    @PostMapping("/items/new")
    public String saveItem(@ModelAttribute ItemForm form, RedirectAttributes redirectAttributes) throws IOException {

        UploadFile attachFile = fileStore.storeFile(form.getAttachFile());
        List<UploadFile> storeImageFiles = fileStore.storeFiles(form.getImageFiles());

        Item item = new Item();
        item.setItemName(form.getItemName());
        item.setAttachFile(attachFile);
        item.setImageFiles(storeImageFiles);
        itemRepository.save(item);

        redirectAttributes.addAttribute("itemId", item.getId());
        return "redirect:/items/{itemId}";
    }

    @GetMapping("/items/{id}")
    public String items(@PathVariable Long id, Model model) {
        Item item = itemRepository.findById(id);
        model.addAttribute("item", item);
        return "item-view";
    }

    @ResponseBody
    @GetMapping("/images/{filename}")
    public Resource downloadImage(@PathVariable String filename) throws MalformedURLException {
        return new UrlResource("file:" + fileStore.getFullPath(filename));
    }

    @GetMapping("/attach/{itemId}")
    public ResponseEntity<Resource> downloadAttach(@PathVariable Long itemId) throws MalformedURLException {

        Item item = itemRepository.findById(itemId);
        String storeFileName = item.getAttachFile().getStoreFileName();
        String uploadFileName = item.getAttachFile().getUploadFileName();

        UrlResource resource = new UrlResource("file:" + fileStore.getFullPath(storeFileName));

        String encodedUploadFileName = UriUtils.encode(uploadFileName, StandardCharsets.UTF_8); 
        String contentDisposition = "attachment; filename=\"" + encodedUploadFileName + "\"";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition)
                .body(resource);
    }
}
```
- `@GetMapping("/items/new")` : 등록 폼을 보여준다.
- `@PostMapping("/items/new")` : 폼의 데이터를 저장하고 보여주는 화면으로 리다이렉트 한다.
- `@GetMapping("/items/{id}")` : 상품을 보여준다.
- `@GetMapping("/images/{filename}")` : <img> 태그로 이미지를 조회할 때 사용한다. `UrlResource` 로 이미지 파일을 읽어서 `@ResponseBody` 로 이미지 바이너리를 반환한다. -> `file:`을 통해 내부 파일에 직접 접근
- `@GetMapping("/attach/{itemId}")` : 파일을 다운로드 할 때 실행한다. 예제를 더 단순화 할 수 있지만, 파일 다운로드 시 권한 체크같은 복잡한 상황까지 가정한다 생각하고 이미지 id 를 요청하도록 했다. -> 이미지 Id를 통해 유저 권한 검증, ...
파일 다운로드시에는 고객이 업로드한 파일 이름으로 다운로드 하는게 좋다. 이때는 `Content-Disposition` 해더에 표준 규약(`attachment; filename="업로드 파일명"`) 값을 주면 된다. -> 
- `UriUtils.encode(uploadFileName, StandardCharsets.UTF_8);`: 한글이 깨질 수 있기 때문에 인코딩

보통 파일은 데이터베이스에 저장하는 것이 아닌 스토리지(AWS S3, ..)에 저장하고, 데이터베이스에는 파일이 저장된 경로 정도 저장한다. 
경로도 보통 FullPath가 아닌 Path의 형식을 맞춰두고 형식 이후 상대적인 경로만 저장한다.

