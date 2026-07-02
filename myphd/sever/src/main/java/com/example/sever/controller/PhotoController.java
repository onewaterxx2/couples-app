package com.example.sever.controller;

import com.example.sever.entity.Photo;
import com.example.sever.service.PhotoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/photos")
public class PhotoController {

    @Autowired
    private PhotoService photoService;

    @Value("${file.upload-dir}")
    private String uploadDir;

    @PostMapping("/upload")
    public ResponseEntity<Map<String, Object>> uploadPhoto(
            @RequestParam("coupleId") Long coupleId,
            @RequestParam("userId") Long userId,
            @RequestParam("description") String description,
            @RequestParam("file") MultipartFile file) {

        Map<String, Object> result = new HashMap<>();

        if (file.isEmpty()) {
            result.put("success", false);
            result.put("message", "请选择图片");
            return ResponseEntity.badRequest().body(result);
        }

        try {
            String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            Path filePath = Paths.get(uploadDir, fileName);

            File uploadFolder = new File(uploadDir);
            if (!uploadFolder.exists()) {
                uploadFolder.mkdirs();
            }

            Files.copy(file.getInputStream(), filePath);

            String imageUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/uploads/")
                    .path(fileName)
                    .toUriString();

            Photo photo = photoService.uploadPhoto(coupleId, userId, imageUrl, description);

            result.put("success", true);
            result.put("message", "上传成功");
            result.put("photo", photo);

            return ResponseEntity.ok(result);

        } catch (IOException e) {
            result.put("success", false);
            result.put("message", "上传失败：" + e.getMessage());
            return ResponseEntity.internalServerError().body(result);
        }
    }

    @GetMapping("/list")
    public ResponseEntity<Page<Photo>> getPhotos(
            @RequestParam("coupleId") Long coupleId,
            @RequestParam("page") int page,
            @RequestParam("size") int size) {

        Page<Photo> photos = photoService.getPhotosByCoupleId(coupleId, page, size);
        return ResponseEntity.ok(photos);
    }

    @GetMapping("/all")
    public ResponseEntity<List<Photo>> getAllPhotos(@RequestParam("coupleId") Long coupleId) {
        List<Photo> photos = photoService.getAllPhotosByCoupleId(coupleId);
        return ResponseEntity.ok(photos);
    }

    @PostMapping("/{id}/like")
    public ResponseEntity<Map<String, Object>> likePhoto(@PathVariable Long id) {
        Map<String, Object> result = photoService.likePhoto(id);

        if ((Boolean) result.get("success")) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.badRequest().body(result);
        }
    }

    @DeleteMapping("/{photoId}")
    public ResponseEntity<Map<String, Object>> deletePhoto(
            @PathVariable Long photoId,
            @RequestParam Long userId) {
        Map<String, Object> result = photoService.deletePhoto(photoId, userId);

        if ((Boolean) result.get("success")) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.badRequest().body(result);
        }
    }
}