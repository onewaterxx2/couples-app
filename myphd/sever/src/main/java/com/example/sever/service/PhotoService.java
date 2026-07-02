package com.example.sever.service;

import com.example.sever.entity.Photo;
import com.example.sever.repository.PhotoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class PhotoService {

    @Autowired
    private PhotoRepository photoRepository;

    public Photo uploadPhoto(Long coupleId, Long userId, String imageUrl, String description) {
        Photo photo = new Photo(coupleId, userId, imageUrl, description);
        return photoRepository.save(photo);
    }

    public Page<Photo> getPhotosByCoupleId(Long coupleId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return photoRepository.findByCoupleIdOrderByCreatedAtDesc(coupleId, pageable);
    }

    public List<Photo> getAllPhotosByCoupleId(Long coupleId) {
        return photoRepository.findByCoupleIdOrderByCreatedAtDesc(coupleId);
    }

    public Map<String, Object> likePhoto(Long photoId) {
        Map<String, Object> result = new HashMap<>();

        Optional<Photo> photoOpt = photoRepository.findById(photoId);

        if (photoOpt.isEmpty()) {
            result.put("success", false);
            result.put("message", "照片不存在");
            return result;
        }

        Photo photo = photoOpt.get();
        photo.setLikes(photo.getLikes() + 1);
        photoRepository.save(photo);

        result.put("success", true);
        result.put("message", "点赞成功");
        result.put("likes", photo.getLikes());

        return result;
    }

    public Map<String, Object> deletePhoto(Long photoId, Long userId) {
        Map<String, Object> result = new HashMap<>();

        Optional<Photo> photoOpt = photoRepository.findById(photoId);

        if (photoOpt.isEmpty()) {
            result.put("success", false);
            result.put("message", "照片不存在");
            return result;
        }

        Photo photo = photoOpt.get();

        // 只有上传者本人可以删除照片
        if (!photo.getUserId().equals(userId)) {
            result.put("success", false);
            result.put("message", "只能删除自己上传的照片");
            return result;
        }

        photoRepository.delete(photo);

        result.put("success", true);
        result.put("message", "删除成功");

        return result;
    }
}