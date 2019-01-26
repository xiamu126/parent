package com.sybd.znld.config;

import com.google.code.kaptcha.impl.DefaultKaptcha
import com.google.code.kaptcha.util.Config
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component
import java.util.*

@Component
@ConfigurationProperties(prefix = "captcha")
class KaptchaConfig{

    lateinit var border: String
    lateinit var borderColor: String
    lateinit var fontColor:String
    lateinit var imageWidth:String
    lateinit var imageHeight:String
    lateinit var fontSize:String
    lateinit var length:String
    lateinit var fontNames: String

    @Bean
    fun getDefaultKaptcha(): DefaultKaptcha {
        val defaultKaptcha = DefaultKaptcha();
        val properties = Properties();
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
        val config = Config(properties);
        defaultKaptcha.config = config;
        return defaultKaptcha;
    }
}