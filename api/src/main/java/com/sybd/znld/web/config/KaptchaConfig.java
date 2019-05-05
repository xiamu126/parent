package com.sybd.znld.web.config;

import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.code.kaptcha.util.Config;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import java.util.*;

@Component
@ConfigurationProperties(prefix = "captcha")
@Getter @Setter
public class KaptchaConfig{
    public String border;
    public String borderColor;
    public String fontColor;
    public String imageWidth;
    public String imageHeight;
    public String fontSize;
    public String length;
    public String fontNames;

    @Bean
    public DefaultKaptcha getDefaultKaptcha(){
        DefaultKaptcha defaultKaptcha = new DefaultKaptcha();
        Properties properties = new Properties();
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
        Config config = new Config(properties);
        defaultKaptcha.setConfig(config);
        return defaultKaptcha;
    }
}