package com.example.sever.service;

import com.example.sever.entity.Couple;
import com.example.sever.entity.User;
import com.example.sever.repository.CoupleRepository;
import com.example.sever.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CoupleRepository coupleRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private VerificationCodeService verificationCodeService;

    @Autowired
    private EmailService emailService;

    /** 注册前发送验证码到邮箱。邮箱已注册则拒绝。 */
    public Map<String, Object> sendCode(String email) {
        Map<String, Object> result = new HashMap<>();

        if (email == null || email.isBlank()) {
            result.put("success", false);
            result.put("message", "请输入邮箱");
            return result;
        }

        if (userRepository.existsByEmail(email)) {
            result.put("success", false);
            result.put("message", "邮箱已注册");
            return result;
        }

        try {
            String code = verificationCodeService.generateAndStore(email);
            emailService.sendCode(email, code);
            result.put("success", true);
            result.put("message", "验证码已发送，请查收邮箱");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "验证码发送失败，请稍后重试");
        }

        return result;
    }

    public Map<String, Object> register(String email, String password, String nickname, Boolean isMale, String code) {
        Map<String, Object> result = new HashMap<>();

        if (!verificationCodeService.verify(email, code)) {
            result.put("success", false);
            result.put("message", "验证码错误或已过期");
            return result;
        }

        if (userRepository.existsByEmail(email)) {
            result.put("success", false);
            result.put("message", "邮箱已注册");
            return result;
        }

        User user = new User();
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setNickname(nickname);
        user.setIsMale(isMale);
        user.setCoupleId(null);

        User savedUser = userRepository.save(user);

        result.put("success", true);
        result.put("message", "注册成功");
        result.put("userId", savedUser.getId());
        result.put("nickname", savedUser.getNickname());
        result.put("isMale", savedUser.getIsMale());

        return result;
    }

    public Map<String, Object> login(String email, String password) {
        Map<String, Object> result = new HashMap<>();

        Optional<User> userOpt = userRepository.findByEmail(email);

        if (userOpt.isEmpty()) {
            result.put("success", false);
            result.put("message", "用户不存在");
            return result;
        }

        User user = userOpt.get();

        if (!passwordEncoder.matches(password, user.getPassword())) {
            result.put("success", false);
            result.put("message", "密码错误");
            return result;
        }

        result.put("success", true);
        result.put("message", "登录成功");
        result.put("userId", user.getId());
        result.put("nickname", user.getNickname());
        result.put("isMale", user.getIsMale());
        result.put("coupleId", user.getCoupleId());

        if (user.getCoupleId() != null) {
            Optional<Couple> coupleOpt = coupleRepository.findById(user.getCoupleId());
            if (coupleOpt.isPresent()) {
                Couple couple = coupleOpt.get();
                result.put("startDate", couple.getStartDate());
                result.put("code", couple.getCode());
            }
        }

        return result;
    }

    public Map<String, Object> createCouple(Long userId) {
        Map<String, Object> result = new HashMap<>();

        Optional<User> userOpt = userRepository.findById(userId);

        if (userOpt.isEmpty()) {
            result.put("success", false);
            result.put("message", "用户不存在");
            return result;
        }

        User user = userOpt.get();

        if (user.getCoupleId() != null) {
            result.put("success", false);
            result.put("message", "您已经有情侣关系");
            return result;
        }

        String code = generateCode();
        while (coupleRepository.findByCode(code).isPresent()) {
            code = generateCode();
        }

        Couple couple = new Couple();
        couple.setStartDate(LocalDate.now());
        couple.setCode(code);
        if (user.getIsMale()) {
            couple.setMaleId(userId);
        } else {
            couple.setFemaleId(userId);
        }

        Couple savedCouple = coupleRepository.save(couple);

        user.setCoupleId(savedCouple.getId());
        userRepository.save(user);

        result.put("success", true);
        result.put("message", "情侣码创建成功，请将情侣码分享给对方");
        result.put("coupleId", savedCouple.getId());
        result.put("code", savedCouple.getCode());
        result.put("startDate", savedCouple.getStartDate());

        return result;
    }

    public Map<String, Object> joinCouple(Long userId, String code) {
        Map<String, Object> result = new HashMap<>();

        Optional<User> userOpt = userRepository.findById(userId);
        Optional<Couple> coupleOpt = coupleRepository.findByCode(code);

        if (userOpt.isEmpty()) {
            result.put("success", false);
            result.put("message", "用户不存在");
            return result;
        }

        if (coupleOpt.isEmpty()) {
            result.put("success", false);
            result.put("message", "情侣码无效");
            return result;
        }

        User user = userOpt.get();
        Couple couple = coupleOpt.get();

        if (user.getCoupleId() != null) {
            result.put("success", false);
            result.put("message", "您已经有情侣关系");
            return result;
        }

        if (user.getIsMale()) {
            if (couple.getMaleId() != null) {
                result.put("success", false);
                result.put("message", "该情侣码已有男方");
                return result;
            }
            couple.setMaleId(userId);
        } else {
            if (couple.getFemaleId() != null) {
                result.put("success", false);
                result.put("message", "该情侣码已有女方");
                return result;
            }
            couple.setFemaleId(userId);
        }

        // 检查是否配对成功（双方都有了），更新在一起日期为今天
        if (couple.getMaleId() != null && couple.getFemaleId() != null) {
            couple.setStartDate(LocalDate.now());
        }

        user.setCoupleId(couple.getId());
        userRepository.save(user);
        coupleRepository.save(couple);

        result.put("success", true);
        result.put("message", "加入成功");
        result.put("coupleId", couple.getId());
        result.put("startDate", couple.getStartDate());

        return result;
    }

    private String generateCode() {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder code = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 6; i++) {
            code.append(characters.charAt(random.nextInt(characters.length())));
        }
        return code.toString();
    }

    public Optional<User> getUserById(Long userId) {
        return userRepository.findById(userId);
    }

    public Map<String, Object> coupleStatus(Long coupleId) {
        Map<String, Object> result = new HashMap<>();

        Optional<Couple> coupleOpt = coupleRepository.findById(coupleId);
        if (coupleOpt.isEmpty()) {
            result.put("success", false);
            result.put("message", "情侣关系不存在");
            return result;
        }

        Couple couple = coupleOpt.get();
        boolean paired = couple.getMaleId() != null && couple.getFemaleId() != null;

        // 计算恋爱天数
        long daysTogether = java.time.temporal.ChronoUnit.DAYS.between(
            couple.getStartDate(),
            LocalDate.now()
        );

        result.put("success", true);
        result.put("paired", paired);
        result.put("coupleId", couple.getId());
        result.put("startDate", couple.getStartDate());
        result.put("daysTogether", daysTogether);
        return result;
    }
}