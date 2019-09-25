package com.sybd.znld.web.config;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.HttpClientBuilder;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.boot.convert.ApplicationConversionService;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.BufferedImageHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.*;
import javax.servlet.ServletContextListener;
import java.awt.image.BufferedImage;
import java.security.KeyStore;

@Configuration
public class SpringConfig {
    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jsonCustomizer() {
        return jacksonObjectMapperBuilder -> {
            jacksonObjectMapperBuilder.failOnUnknownProperties(false);
        };
    }

    @Bean //用以支持从yml文件中读取的数据映射到如Duration这样的数据类型
    public ApplicationConversionService conversionService()
    {
        return new ApplicationConversionService();
    }

    @Bean
    public HttpMessageConverter<BufferedImage> bufferedImageHttpMessageConverter() {
        return new BufferedImageHttpMessageConverter();
    }

    @Bean
    public ModelMapper modelMapper(){
        var modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        return modelMapper;
    }

    @Bean
    ServletListenerRegistrationBean<ServletContextListener> servletListener() {
        var srb = new ServletListenerRegistrationBean<ServletContextListener>();
        srb.setListener(new MyServletContextListener());
        return srb;
    }

    @Bean("restTemplate")
    public RestTemplate restTemplate(){
        int timeout = 30*1000;
        var config = RequestConfig.custom()
                .setConnectTimeout(timeout)
                .setConnectionRequestTimeout(timeout)
                .setSocketTimeout(timeout)
                .build();
        var client = HttpClientBuilder.create().setDefaultRequestConfig(config).build();
        var httpComponentsClientHttpRequestFactory = new HttpComponentsClientHttpRequestFactory(client);
        return new RestTemplate(httpComponentsClientHttpRequestFactory);
    }

    @Bean("sslRestTemplate")
    public RestTemplate sslRestTemplate() throws Exception {
        // 1 Import your own certificate
        var selfCert = KeyStore.getInstance("pkcs12");
        var selfCertFile = new ClassPathResource("cert/outgoing.CertwithKey.pkcs12");
        var selfCertPwd = "IoM@1234";
        selfCert.load(selfCertFile.getInputStream(), selfCertPwd.toCharArray());
        var kmf = KeyManagerFactory.getInstance("sunx509");
        kmf.init(selfCert, selfCertPwd.toCharArray());

        // 2 Import the CA certificate of the server,
        var caCert = KeyStore.getInstance("jks");
        var caCertFile = new ClassPathResource("cert/ca.jks");
        var caCertPwd = "Huawei@123";
        caCert.load(caCertFile.getInputStream(), caCertPwd.toCharArray());
        var tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        tmf.init(caCert);

        var sc = SSLContext.getInstance("TLS");
        sc.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);

        // 3 Set the domain name to not verify
        // (Non-commercial IoT platform, no use domain name access generally.)
        var sslConnectionSocketFactory = new SSLConnectionSocketFactory(sc, (hostname, session) -> {
            return true; // 不校验hostname
        });
        int timeout = 30*1000;
        var config = RequestConfig.custom()
                .setConnectTimeout(timeout)
                .setConnectionRequestTimeout(timeout)
                .setSocketTimeout(timeout).build();

        var client = HttpClientBuilder.create()
                .setDefaultRequestConfig(config)
                .setSSLSocketFactory(sslConnectionSocketFactory).build();

        var httpComponentsClientHttpRequestFactory = new HttpComponentsClientHttpRequestFactory(client);
        return new RestTemplate(httpComponentsClientHttpRequestFactory);
    }
}
