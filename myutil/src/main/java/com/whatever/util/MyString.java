package com.whatever.util;

import lombok.var;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class MyString {
    public static String Empty = "";

    public static String replace(String template, Map<String, Object> params) {
        StringBuffer sb = new StringBuffer();
        Matcher m = Pattern.compile("\\$\\{\\w+}").matcher(template);
        while (m.find()) {
            String param = m.group();
            Object value = params.get(param.substring(2, param.length() - 1));
            m.appendReplacement(sb, value == null ? "" : value.toString());
        }
        m.appendTail(sb);
        return sb.toString();
    }

    public static String replace(String template, String... params){
        var sb = new StringBuffer();
        var m = Pattern.compile("\\{\\w*}").matcher(template);
        var queue = new ArrayDeque<String>(Arrays.asList(params));
        while (m.find()) {
            var value = queue.poll();
            assert value != null;
            m.appendReplacement(sb, value);
        }
        m.appendTail(sb);
        return sb.toString();
    }

    public static String combine(List<String> values, String split){
        StringBuilder tmp = new StringBuilder();
        int count = 1;
        for(var value : values){
            if(count < values.size()) tmp.append(value).append(split);
            else tmp.append(value);
            count++;
        }
        return tmp.toString();
    }

    public static boolean isEmpty(String str){
        return str != null && str.equals("");
    }

    public static boolean isEmptyOrNull(String str){
        return str == null || str.equals("");
    }

    public static String toUrlParams(Map<String, String> nameAndValues){
        StringBuilder tmp = new StringBuilder("?");
        for(var item : nameAndValues.entrySet()){
            tmp.append(item.getKey()).append("=").append(item.getValue()).append("&");
        }
        return tmp.deleteCharAt(tmp.length()-1).toString();
    }

    public static void main(String[] args){
        var map = new HashMap<String, String>();
        map.put("start", "2015-01-10T08:00:35");
        map.put("limit", "100");
        map.put("cursor", "191811_505253765_1544055568061");
        System.out.println(toUrlParams(map));
    }
}
