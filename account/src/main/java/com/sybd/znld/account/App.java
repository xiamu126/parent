package com.sybd.znld.account;

import com.mongodb.*;
import org.bson.Document;

public class App {
    public static void main(String[] args) {
        var credential = MongoCredential.createCredential("root","admin", "znld@MON#188188".toCharArray());
        var mongoClient = new MongoClient(new ServerAddress("192.168.11.101", 27017), credential, MongoClientOptions.builder().build());
        var db = mongoClient.getDatabase( "test" );
        var collection = db.getCollection("c1");
        var fi = collection.find();
        for (var document : fi) {
            System.out.println(document.get("name"));
        }
        var c1 = db.getCollection("com.sybd.znld.account.profile");
        var d1 = new Document();
        d1.append("id", "user id");
        d1.append("menu", new Document().append("light_control_url", "http://183.129.159.166:11180"));
        c1.insertOne(d1);
    }
}
