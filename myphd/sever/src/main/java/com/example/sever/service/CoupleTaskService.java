package com.example.sever.service;

import com.example.sever.entity.CoupleTask;
import com.example.sever.repository.CoupleTaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CoupleTaskService {

    @Autowired
    private CoupleTaskRepository taskRepository;

    public Map<String, Object> createTask(Long coupleId, Long creatorId, String title, String description) {
        Map<String, Object> result = new HashMap<>();

        if (title == null || title.trim().isEmpty()) {
            result.put("success", false);
            result.put("message", "任务标题不能为空");
            return result;
        }

        CoupleTask task = new CoupleTask(coupleId, creatorId, title.trim(), description);
        CoupleTask saved = taskRepository.save(task);

        result.put("success", true);
        result.put("message", "任务创建成功");
        result.put("task", convertToMap(saved));
        return result;
    }

    public Map<String, Object> getTasks(Long coupleId) {
        Map<String, Object> result = new HashMap<>();

        List<CoupleTask> tasks = taskRepository.findByCoupleIdOrderByCreatedAtDesc(coupleId);
        List<Map<String, Object>> taskMaps = tasks.stream()
                .map(this::convertToMap)
                .collect(Collectors.toList());

        result.put("success", true);
        result.put("tasks", taskMaps);
        return result;
    }

    public Map<String, Object> toggleComplete(Long taskId) {
        Map<String, Object> result = new HashMap<>();

        Optional<CoupleTask> taskOpt = taskRepository.findById(taskId);
        if (taskOpt.isEmpty()) {
            result.put("success", false);
            result.put("message", "任务不存在");
            return result;
        }

        CoupleTask task = taskOpt.get();
        task.setIsCompleted(!task.getIsCompleted());

        if (task.getIsCompleted()) {
            task.setCompletedAt(LocalDateTime.now());
        } else {
            task.setCompletedAt(null);
        }

        taskRepository.save(task);

        result.put("success", true);
        result.put("message", task.getIsCompleted() ? "任务已完成" : "任务标记为未完成");
        result.put("task", convertToMap(task));
        return result;
    }

    public Map<String, Object> deleteTask(Long taskId) {
        Map<String, Object> result = new HashMap<>();

        if (!taskRepository.existsById(taskId)) {
            result.put("success", false);
            result.put("message", "任务不存在");
            return result;
        }

        taskRepository.deleteById(taskId);

        result.put("success", true);
        result.put("message", "任务已删除");
        return result;
    }

    private Map<String, Object> convertToMap(CoupleTask task) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", task.getId());
        map.put("coupleId", task.getCoupleId());
        map.put("creatorId", task.getCreatorId());
        map.put("title", task.getTitle());
        map.put("description", task.getDescription());
        map.put("isCompleted", task.getIsCompleted());
        map.put("completedAt", task.getCompletedAt());
        map.put("createdAt", task.getCreatedAt());
        return map;
    }
}
