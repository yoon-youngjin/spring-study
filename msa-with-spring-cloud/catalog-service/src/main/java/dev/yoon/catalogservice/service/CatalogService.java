package dev.yoon.catalogservice.service;

import dev.yoon.catalogservice.repository.CatalogEntity;

import java.util.List;

public interface CatalogService {

    List<CatalogEntity> getAllCatalogs();

}
