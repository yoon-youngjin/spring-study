package dev.yoon.controllerdemo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServlet;
import java.io.IOException;
import java.io.InputStream;

@RestController
// 간단하게, Controller의 모든 함수에 ResponseBody를 붙인 것
@RequestMapping("/rest")
// localhost:8080/rest
public class SampleRestController {
    private static final Logger logger
            = LoggerFactory.getLogger(SampleController.class);

    // localhost:8080/rest/sample-payload
    // => Controller에서는 작동 안하던 부분이 RestController에서는 작동 (@ResponseBody가 없음)
    // => 근본적인 차이점 : Controller는 기본적으로 view를 제공하거나, data를 제공하는 용도로 조금 더 넓은 범위
    // => RestController는 주 용도가 데이터를 주고 받는 역할
    @GetMapping("/sample-payload")
    public SamplePayload samplePayloadGet() {
        return new SamplePayload("yoon", 26, "student");
    }


    // produces : MediaType을 설정해주기 위한 값
    // MediaType.IMAGE_PNG_VALUE : image를 return
    // image, 영상은 결과적으로 byte
    @GetMapping(value = "/sample-image", produces = MediaType.IMAGE_PNG_VALUE)
    public byte[] sampleImage() throws IOException {
        // getClass : 현재 class의 path를 받아옴
        //  getClass().getResourceAsStream("") : resource폴더 내에서 찾아들어감 -> resources >> static ...
        InputStream inputStream = getClass().getResourceAsStream("/static/IMG_0416.gif");
        // 파일의 경우
        // inputStream = new FileInputStream(new File(""));
        return inputStream.readAllBytes();
    }

    @PostMapping("/sample-payload")
    // 정상적으로 처리가 되었을 때 status가 어떻게 정의가 되어야할지를 어노테이션으로 정의
    // => 지금은 body가 없음을 status code로 바로 전달
    // RequestBody : post요청의 body임을 명시
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void samplePayloadPost(@RequestBody SamplePayload samplePayload) {
        logger.info(samplePayload.toString());
    }

    // consumes는 produces의 반대
    @PostMapping(value = "/sample-multipart", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void sampleMultipartPost(
            @RequestParam("name") String name,
            @RequestParam("age") Integer age,
            @RequestParam("occupation") String occupation,
            @RequestParam(value = "file", required = false) MultipartFile multipartFile
    ) throws IOException {
        logger.info("name : " + name);
        logger.info("age : " + age);
        logger.info("occupation : " + occupation);
        logger.info("file original name: " + multipartFile.getOriginalFilename());

//        multipartFile.getBytes();

    }


}
