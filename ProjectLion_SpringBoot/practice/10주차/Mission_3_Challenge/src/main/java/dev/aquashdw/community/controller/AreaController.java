package dev.aquashdw.community.controller;

import dev.aquashdw.community.controller.dto.AreaDto;
import dev.aquashdw.community.service.AreaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

/**
 * @Profile("area")
 * 특정 프로파일일 경우에만 빈이 생성되고 객체로 관리
 * 결과적으로 요청을 받을 수 없는 상태
 */
@Profile("area")
@RestController
@RequestMapping("area")
public class AreaController {
    private static final Logger logger = LoggerFactory.getLogger(AreaController.class);
    private final AreaService areaService;

    public AreaController(AreaService areaService) {
        this.areaService = areaService;
    }

    @PostMapping
    public ResponseEntity<AreaDto> createArea(@RequestBody AreaDto dto){
        return ResponseEntity.ok(this.areaService.createArea(dto));
    }

    @GetMapping("{id}")
    public ResponseEntity<AreaDto> readArea(@PathVariable("id") Long id){
        return ResponseEntity.ok(this.areaService.readArea(id));
    }

    @GetMapping
    public ResponseEntity<Collection<AreaDto>> readAreaAll() {
        return ResponseEntity.ok(this.areaService.readAreaAll());
    }
}
