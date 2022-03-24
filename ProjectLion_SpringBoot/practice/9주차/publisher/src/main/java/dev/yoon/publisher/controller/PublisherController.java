package dev.yoon.publisher.controller;

import dev.yoon.publisher.service.PublisherService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class PublisherController {

    private final PublisherService publisherService;

    @GetMapping("/")
    public void sendMsg() {
        publisherService.publishMessage();

    }
}
