package dev.yoon.controllerdemo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import java.util.ArrayList;
import java.util.List;

@Controller
// Controller class가 Bean으로 관리
public class SampleController {
    private static final Logger logger
            = LoggerFactory.getLogger(SampleController.class);

    //JSP
//    @GetMapping("/sample-jsp")
//    public String sampleJsp(Model model) {
//
//        logger.info("in sample jsp");
//        List<SamplePayload> samplePayloadList = new ArrayList<>();
//        samplePayloadList.add(new SamplePayload("yoon", 12, "student"));
//        samplePayloadList.add(new SamplePayload("young", 12, "student"));
//        samplePayloadList.add(new SamplePayload("gin", 12, "student"));
//
//        // 데이터 전달 -> spring ioc가 작동
//        model.addAttribute("profiles", samplePayloadList);
//
////        // model과 view를 합쳐서 보여주기 위한 객체
////        ModelAndView modelAndView = new ModelAndView();
////        modelAndView.setViewName();
//        return "view-jsp";
//    }

    // ThymeLeaf
    // static폴더가 아닌 templates폴더를 사용하게 됨
    @GetMapping("/sample-thyme")
    public ModelAndView sampleThyme() {
        logger.info("in sample thyme");
        ModelAndView modelAndView = new ModelAndView();
        List<SamplePayload> profiles = new ArrayList<>();
        profiles.add(new SamplePayload("yoon", 12, "student"));
        profiles.add(new SamplePayload("young", 12, "student"));
        profiles.add(new SamplePayload("gin", 12, "student"));
        modelAndView.addObject("profiles", profiles);
        modelAndView.setViewName("thyme");
        return modelAndView;
    }


    // 경로설정 -> 경로에 어떤 함수가 들어갈지 결정할 때 사용
    // value : 요청의 url의 path => localhost:8080/hello
    // method : value값으로 요청 시 반응할 행동
    // return : 경로의 요청에 대한 결과값 -> 해당 파일을 가리키는 것은 아님
    @RequestMapping(value = "/hello", method = RequestMethod.GET)
    public String hello() {
        return "/hello.html";
    }

    //GetMapping은 method가 get으로 고정 되어있음
    //value : localhost:8080/hello/{id} => id는 변수값 : @PathVariable String id
    @GetMapping(
            value = "/hello/{id}"
    )
    public String helloPath(@PathVariable String id) {
        logger.info("Path Variable is : " + id);
        return "/hello.html";
    }

    // RequestParam : Query의 내용을 가져올 때 사용
    // -> http://localhost:8080/hello2?id=yoon
    @GetMapping(value = "/hello2")
    public String hello2(@RequestParam(name = "id", required = false, defaultValue = "") String id) {
        logger.info("Path : Hello");
        logger.info("Query Param id : " + id);
        return "/hello.html";
    }

    // JSON을 많이 사용함
    // ResponseBody : 데이터가 http요청 응답을 body에 작성됨을 명시
    // ResponseBody가 없으면 응답으로 돌아가는 (위의 hello형태)String이 viewresolver에 들어가게 되면서 해당 html을 가져오는 과정으로 가져옴
    // ResponseBody가 있으면 View를 찾는 과정이 아닌 해당 데이터를 자체로 body로 사용
    // spring의 장점 : 일반적인 자바 객체를 json,xml같은 형태로 만들어서 넘겨줄 수 있다.
    @GetMapping("/get-profile")
    public @ResponseBody
    SamplePayload getProfile() {
        SamplePayload samplePayload = new SamplePayload("yoon", 10, "Student");
        System.out.println(samplePayload.getName());
        return samplePayload;
    }

}
