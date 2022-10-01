package dev.yoon.catalogservice.controller;

import dev.yoon.catalogservice.repository.CatalogEntity;
import dev.yoon.catalogservice.service.CatalogService;
import dev.yoon.catalogservice.vo.ResponseCatalog;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RequestMapping("catalog-service")
@RestController
@RequiredArgsConstructor
public class CatalogController {


    private final Environment env;
    private final CatalogService catalogService;

    @GetMapping("/health_check")
    public String status() {
        return String.format("It's Working in Catalog Service on PORT %s",
                env.getProperty("local.server.port"));
    }

    @GetMapping("/catalogs")
    public ResponseEntity<List<ResponseCatalog>> getUsers() {

        List<CatalogEntity> userList = catalogService.getAllCatalogs();
        List<ResponseCatalog> result = new ArrayList<>();
        ModelMapper mapper = new ModelMapper();
        userList.forEach(v -> result.add(mapper.map(v, ResponseCatalog.class)));

        return ResponseEntity.status(HttpStatus.OK).body(result);
    }



}
