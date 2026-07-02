package com.example.sever.service;

import com.example.sever.entity.Message;
import com.example.sever.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MessageService {

    @Autowired
    private MessageRepository messageRepository;

    public Message sendMessage(Long coupleId, Long senderId, String content) {
        Message message = new Message(coupleId, senderId, content);
        return messageRepository.save(message);
    }

    public Page<Message> getMessagesByCoupleId(Long coupleId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return messageRepository.findByCoupleIdOrderByCreatedAtDesc(coupleId, pageable);
    }

    public List<Message> getAllMessagesByCoupleId(Long coupleId) {
        return messageRepository.findByCoupleIdOrderByCreatedAtDesc(coupleId);
    }

    public List<Message> getMessagesBefore(Long coupleId, Long lastId, int size) {
        Pageable pageable = PageRequest.of(0, size);
        return messageRepository.findByCoupleIdAndIdLessThanOrderByCreatedAtDesc(coupleId, lastId, pageable);
    }
}