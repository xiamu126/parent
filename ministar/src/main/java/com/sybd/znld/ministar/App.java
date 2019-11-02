package com.sybd.znld.ministar;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@Slf4j
@SpringBootApplication(scanBasePackages = {"com.sybd.znld"})
public class App {
    //@Reference(version = "1.0.0", url = "dubbo://localhost:12345?version=1.0.0")
    //private IDubboService dubboService;

    /*@Bean
    public ApplicationRunner runner() {
        return args -> {
            log.debug(dubboService.sayHello("mercyblitz"));
        };
    }*/
    public static void main(String[] args) {
        SpringApplication.run(App.class, args);


        /*var credential = MongoCredential.createCredential("root","admin", "znld@MON#188188".toCharArray());
        var mongoClient = new MongoClient(new ServerAddress("192.168.11.101", 27017), credential, MongoClientOptions.builder().build());
        var db = mongoClient.getDatabase( "test" );*/

        /*var collection = db.getCollection("c1");
        var fi = collection.find();
        for (var document : fi) {
            System.out.println(document.get("name"));
        }*/

        /*var c1 = db.getCollection("com.sybd.znld.account.profile");
        var d1 = new Document();
        d1.append("id", "a6b354d551f111e9804a0242ac110007");
        d1.append("menu", new Document().append("light_control_url", "http://39.104.138.0:8800/web/"));

        c1.insertOne(d1);*/

        //c1.updateOne(Filters.eq("id", "user id"), new Document("$set", new Document("id", "c9a45d5d972011e9b0790242c0a8b006")));

        /*var myDoc = c1.find(Filters.eq("id", "c9a45d5d972011e9b0790242c0a8b006")).first();
        myDoc.remove("_id");
        System.out.println(myDoc.toJson());*/
    }
}
