package com.taotaoapi.controller;


import com.taotaoapi.response.ApiResponse;
import com.taotaoapi.service.UploadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/taotao")
public class UploadController {
    private final UploadService uploadService;

    @PostMapping("/upload")
    public ApiResponse<List<String>> upload(@RequestPart("files") List<MultipartFile> files) {

        List<String> urls = uploadService.uploadFiles(files);
        return ApiResponse.success("圖片上傳成功",urls);
    }

    @PostMapping("/delete")
    public ApiResponse delete(@RequestParam String imageUrl) {
        uploadService.deleteByUrl(imageUrl);
        return ApiResponse.success("刪除成功",null);
    }
}
