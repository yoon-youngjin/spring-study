package com.example.demo;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.FileUrlResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourceRegion;
import org.springframework.http.*;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

@RestController
public class TestController {

    @GetMapping(path = "/video")
    public ResponseEntity<StreamingResponseBody> video() {
        File file = new File("C:\\Users\\dudwl\\Desktop\\윤영진\\2022 1학기\\컴퓨터네트워크\\강의영상\\2주차\\2022-1_컴퓨터네트워크_2주차강의자료_1.mp4");
        if (!file.isFile()) {
            return ResponseEntity.notFound().build();
        }

        StreamingResponseBody streamingResponseBody = outputStream -> FileCopyUtils.copy(new FileInputStream(file), outputStream);

        final HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.add("Content-Type", "video/mp4");
        responseHeaders.add("Content-Length", Long.toString(file.length()));

        return ResponseEntity.ok().headers(responseHeaders).body(streamingResponseBody);
    }

    @GetMapping(path = "/video2", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public Resource video2() throws FileNotFoundException, IOException {
        return new ByteArrayResource(FileCopyUtils.copyToByteArray(new FileInputStream("C:\\Users\\dudwl\\Desktop\\윤영진\\2022 1학기\\컴퓨터네트워크\\강의영상\\2주차\\2022-1_컴퓨터네트워크_2주차강의자료_1.mp4")));
    }

//    @GetMapping(path = "/video3")
//    public ResponseEntity<List<ResourceRegion>> video(@RequestHeader HttpHeaders httpHeaders) throws IOException {
//        FileUrlResource resource = new FileUrlResource("C:\\Users\\dudwl\\Desktop\\윤영진\\2022 1학기\\컴퓨터네트워크\\강의영상\\2주차\\2022-1_컴퓨터네트워크_2주차강의자료_1.mp4");
//
//        List<ResourceRegion> resourceRegions = HttpRange.toResourceRegions(httpHeaders.getRange(), resource);
//
//        return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
//                .contentType(MediaTypeFactory.getMediaType(resource).orElse(MediaType.APPLICATION_OCTET_STREAM))
//                .body(resourceRegions);
//    }

    @GetMapping(path = "/video3")
    public ResponseEntity<ResourceRegion> video(@RequestHeader HttpHeaders httpHeaders) throws IOException {
        FileUrlResource resource = new FileUrlResource("C:\\Users\\dudwl\\Desktop\\윤영진\\2022 1학기\\컴퓨터네트워크\\강의영상\\2주차\\2022-1_컴퓨터네트워크_2주차강의자료_1.mp4");

        ResourceRegion resourceRegion = resourceRegion(resource, httpHeaders);
        return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
                .contentType(MediaTypeFactory.getMediaType(resource).orElse(MediaType.APPLICATION_OCTET_STREAM))
                .body(resourceRegion);
    }

    private ResourceRegion resourceRegion(Resource video, HttpHeaders httpHeaders) throws IOException {
        final long chunkSize = 1000000L;
        long contentLength = video.contentLength();

        if (httpHeaders.getRange().isEmpty()) {
            return new ResourceRegion(video, 0, Long.min(chunkSize, contentLength));
        }

        HttpRange httpRange = httpHeaders.getRange().stream().findFirst().get();
        long start = httpRange.getRangeStart(contentLength);
        long end = httpRange.getRangeEnd(contentLength);
        long rangeLength = Long.min(chunkSize, end - start + 1);
        return new ResourceRegion(video, start, rangeLength);
    }
}
