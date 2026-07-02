package com.example.sever.controller;

import com.example.sever.entity.Message;
import com.example.sever.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/messages")
public class MessageController {

    @Autowired
    private MessageService messageService;

    @PostMapping("/send")
    public ResponseEntity<Map<String, Object>> sendMessage(
            @RequestBody Map<String, Object> request) {

        Map<String, Object> result = new HashMap<>();

        Long coupleId = ((Number) request.get("coupleId")).longValue();
        Long senderId = ((Number) request.get("senderId")).longValue();
        String content = (String) request.get("content");

        if (content == null || content.trim().isEmpty()) {
            result.put("success", false);
            result.put("message", "内容不能为空");
            return ResponseEntity.badRequest().body(result);
        }

        Message message = messageService.sendMessage(coupleId, senderId, content);

        result.put("success", true);
        result.put("message", "发送成功");
        result.put("data", message);

        return ResponseEntity.ok(result);
    }

    @GetMapping("/list")
    public ResponseEntity<Page<Message>> getMessages(
            @RequestParam("coupleId") Long coupleId,
            @RequestParam("page") int page,
            @RequestParam("size") int size) {

        Page<Message> messages = messageService.getMessagesByCoupleId(coupleId, page, size);
        return ResponseEntity.ok(messages);
    }

    @GetMapping("/all")
    public ResponseEntity<List<Message>> getAllMessages(@RequestParam("coupleId") Long coupleId) {
        List<Message> messages = messageService.getAllMessagesByCoupleId(coupleId);
        return ResponseEntity.ok(messages);
    }

    @GetMapping("/before")
    public ResponseEntity<List<Message>> getMessagesBefore(
            @RequestParam("coupleId") Long coupleId,
            @RequestParam("lastId") Long lastId,
            @RequestParam("size") int size) {

        List<Message> messages = messageService.getMessagesBefore(coupleId, lastId, size);
        return ResponseEntity.ok(messages);
    }
}