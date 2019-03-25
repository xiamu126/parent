package com.sybd.znld.model.znld;

import com.sybd.znld.util.MyString;

import java.io.Serializable;
import java.time.LocalDateTime;

public class HttpLogModel implements Serializable {
    public String id;
    public String path;
    public String method;
    public String header;
    public String body;
    public String ip;
    public LocalDateTime triggerTime = LocalDateTime.now();

    public static class HttpMethod{
       public static final String GET = "GET";
       public static final String HEAD = "HEAD";
       public static final String POST = "POST";
       public static final String PUT = "PUT";
       public static final String PATCH = "PATCH";
       public static final String DELETE = "DELETE";
       public static final String OPTIONS = "OPTIONS";
       public static final String TRACE = "TRACE";

       public static boolean isValid(String v){
           switch (v){
               case GET: case HEAD: case POST: case PUT: case PATCH: case DELETE: case OPTIONS: case TRACE: return true;
               default: return false;
           }
       }
    }
}
