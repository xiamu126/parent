package com.sybd.znld.config;

import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.code.kaptcha.util.Config;
import lombok.Getter;
import lombok.Setter;
import lombok.var;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.Properties;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "captcha")
public class KaptchaConfig {
    private String border;
    private String borderColor;
    private String fontColor;
    private String imageWidth;
    private String imageHeight;
    private String fontSize;
    private String length;
    private String fontNames;
    @Bean
    public DefaultKaptcha getDefaultKaptcha() {
        var defaultKaptcha = new DefaultKaptcha();
        var properties = new Properties();
        // 图片边框
        properties.setProperty("kaptcha.border", border);
        // 边框颜色
        properties.setProperty("kaptcha.border.color", borderColor);
        // 字体颜色
        properties.setProperty("kaptcha.textproducer.font.color", fontColor);
        // 图片宽
        properties.setProperty("kaptcha.image.width", imageWidth);
        // 图片高
        properties.setProperty("kaptcha.image.height", imageHeight);
        // 字体大小
        properties.setProperty("kaptcha.textproducer.font.size", fontSize);
        // 验证码长度
        properties.setProperty("kaptcha.textproducer.char.length", length);
        // 字体
        properties.setProperty("kaptcha.textproducer.font.names", fontNames);
        var config = new Config(properties);
        defaultKaptcha.setConfig(config);
        return defaultKaptcha;
    }
}