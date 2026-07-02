package com.example.sever.service;

import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 内存验证码存储：邮箱 -> (验证码, 过期时间)。
 * 单实例够用；多实例部署需换成 Redis。
 */
@Service
public class VerificationCodeService {

    private static final long EXPIRE_MILLIS = 5 * 60 * 1000L; // 5 分钟

    private static class Entry {
        final String code;
        final long expireAt;

        Entry(String code, long expireAt) {
            this.code = code;
            this.expireAt = expireAt;
        }
    }

    private final ConcurrentHashMap<String, Entry> store = new ConcurrentHashMap<>();
    private final Random random = new Random();

    /** 生成 6 位数字码，存入并设 5 分钟有效期，返回该码。 */
    public String generateAndStore(String email) {
        String code = String.format("%06d", random.nextInt(1_000_000));
        store.put(email, new Entry(code, System.currentTimeMillis() + EXPIRE_MILLIS));
        return code;
    }

    /** 校验验证码；成功后移除（一次性）。过期或不匹配返回 false。 */
    public boolean verify(String email, String code) {
        Entry entry = store.get(email);
        if (entry == null) {
            return false;
        }
        if (System.currentTimeMillis() > entry.expireAt) {
            store.remove(email);
            return false;
        }
        if (!entry.code.equals(code)) {
            return false;
        }
        store.remove(email);
        return true;
    }
}
