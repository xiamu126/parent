package com.sybd.znld.model.http;

public class HttpMethod {
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
