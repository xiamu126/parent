package com.sybd.znld.ministar.config;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCredential;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

@Configuration
public class MongoConfig {
    @Value("${spring.data.mongodb.username}")
    private String user;
    @Value("${spring.data.mongodb.authentication-database}")
    private String database;
    @Value("${spring.data.mongodb.password}")
    private String password;

    @Bean("MongoClient")
    public MongoClient mongoClient(){
        var credential = MongoCredential.createCredential(user, database, password.toCharArray());
        var connectionString = new ConnectionString("mongodb://192.168.11.101:27017");
        var pojoCodecRegistry = fromRegistries(com.mongodb.MongoClient.getDefaultCodecRegistry(),
                fromProviders(PojoCodecProvider.builder().automatic(true).build()));
        var settings = MongoClientSettings.builder()
                .credential(credential)
                .codecRegistry(pojoCodecRegistry)
                .applyConnectionString(connectionString)
                .build();
        return MongoClients.create(settings);
    }
}
