package com.example.sever.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

/**
 * 发送注册验证码邮件。
 */
@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public void sendCode(String toEmail, String code) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject("【四叶草】欢迎注册 - 验证码");

            String htmlContent = buildHtmlContent(code);
            helper.setText(htmlContent, true); // true 表示是 HTML 格式

            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("邮件发送失败", e);
        }
    }

    private String buildHtmlContent(String code) {
        return "<!DOCTYPE html>" +
                "<html lang=\"zh-CN\">" +
                "<head>" +
                "    <meta charset=\"UTF-8\">" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">" +
                "</head>" +
                "<body style=\"margin: 0; padding: 0; font-family: 'PingFang SC', 'Microsoft YaHei', sans-serif; background-color: #f5f5f5;\">" +
                "    <table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" style=\"background-color: #f5f5f5; padding: 40px 20px;\">" +
                "        <tr>" +
                "            <td align=\"center\">" +
                "                <!-- 主容器 -->" +
                "                <table width=\"600\" cellpadding=\"0\" cellspacing=\"0\" style=\"background-color: #ffffff; border-radius: 12px; box-shadow: 0 2px 8px rgba(0,0,0,0.08); overflow: hidden;\">" +
                "                    <!-- 头部 - 四叶草主题 -->" +
                "                    <tr>" +
                "                        <td style=\"background: linear-gradient(135deg, #4CAF50 0%, #66BB6A 100%); padding: 32px 40px; text-align: center;\">" +
                "                            <div style=\"font-size: 48px; line-height: 1;\">🍀</div>" +
                "                            <h1 style=\"margin: 12px 0 0 0; color: #ffffff; font-size: 28px; font-weight: 600; letter-spacing: 2px;\">四叶草</h1>" +
                "                            <p style=\"margin: 8px 0 0 0; color: rgba(255,255,255,0.9); font-size: 14px;\">情侣专属空间</p>" +
                "                        </td>" +
                "                    </tr>" +
                "                    " +
                "                    <!-- 内容区域 -->" +
                "                    <tr>" +
                "                        <td style=\"padding: 40px;\">" +
                "                            <h2 style=\"margin: 0 0 16px 0; color: #333333; font-size: 20px; font-weight: 500;\">你好</h2>" +
                "                            <p style=\"margin: 0 0 24px 0; color: #666666; font-size: 15px; line-height: 1.6;\">感谢你注册<strong>四叶草</strong>！为了确保账号安全，请使用以下验证码完成注册：</p>" +
                "                            " +
                "                            <!-- 验证码展示 -->" +
                "                            <div style=\"background-color: #f8f9fa; border: 2px dashed #4CAF50; border-radius: 8px; padding: 24px; text-align: center; margin: 0 0 24px 0;\">" +
                "                                <div style=\"color: #999999; font-size: 13px; margin-bottom: 8px;\">你的验证码</div>" +
                "                                <div style=\"font-size: 36px; font-weight: 700; color: #4CAF50; letter-spacing: 8px; font-family: 'Courier New', monospace;\">" + code + "</div>" +
                "                                <div style=\"color: #999999; font-size: 12px; margin-top: 8px;\">5 分钟内有效</div>" +
                "                            </div>" +
                "                            " +
                "                            <!-- 安全提示 -->" +
                "                            <div style=\"background-color: #fff3cd; border-left: 4px solid #ffc107; padding: 12px 16px; border-radius: 4px; margin-bottom: 24px;\">" +
                "                                <p style=\"margin: 0; color: #856404; font-size: 13px; line-height: 1.5;\">" +
                "                                    <strong>&#128274; 安全提示：</strong>请勿将验证码告知他人，四叶草工作人员不会索要你的验证码。" +
                "                                </p>" +
                "                            </div>" +
                "                            " +
                "                            <p style=\"margin: 0; color: #999999; font-size: 13px; line-height: 1.6;\">" +
                "                                如果这不是你本人的操作，请忽略此邮件。" +
                "                            </p>" +
                "                        </td>" +
                "                    </tr>" +
                "                    " +
                "                    <!-- 底部 -->" +
                "                    <tr>" +
                "                        <td style=\"background-color: #fafafa; padding: 24px 40px; text-align: center; border-top: 1px solid #eeeeee;\">" +
                "                            <p style=\"margin: 0 0 8px 0; color: #999999; font-size: 12px;\">此邮件由系统自动发送，请勿回复</p>" +
                "                            <p style=\"margin: 0; color: #cccccc; font-size: 12px;\">© 2025 四叶草 · 让爱更近一点</p>" +
                "                        </td>" +
                "                    </tr>" +
                "                </table>" +
                "            </td>" +
                "        </tr>" +
                "    </table>" +
                "</body>" +
                "</html>";
    }
}
