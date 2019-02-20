package com.sybd.znld.config;

import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.code.kaptcha.util.Config;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import java.util.*;

@Component
@ConfigurationProperties(prefix = "captcha")
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

    public String getBorder() {
        return border;
    }

    public void setBorder(String border) {
        this.border = border;
    }

    public String getBorderColor() {
        return borderColor;
    }

    public void setBorderColor(String borderColor) {
        this.borderColor = borderColor;
    }

    public String getFontColor() {
        return fontColor;
    }

    public void setFontColor(String fontColor) {
        this.fontColor = fontColor;
    }

    public String getImageWidth() {
        return imageWidth;
    }

    public void setImageWidth(String imageWidth) {
        this.imageWidth = imageWidth;
    }

    public String getImageHeight() {
        return imageHeight;
    }

    public void setImageHeight(String imageHeight) {
        this.imageHeight = imageHeight;
    }

    public String getFontSize() {
        return fontSize;
    }

    public void setFontSize(String fontSize) {
        this.fontSize = fontSize;
    }

    public String getLength() {
        return length;
    }

    public void setLength(String length) {
        this.length = length;
    }

    public String getFontNames() {
        return fontNames;
    }

    public void setFontNames(String fontNames) {
        this.fontNames = fontNames;
    }
}