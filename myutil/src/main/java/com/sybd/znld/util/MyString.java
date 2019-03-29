package com.sybd.znld.util;

import java.time.LocalDate;
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
    public static boolean isAllEmpty(String ...params){
        return Arrays.stream(params).allMatch(MyString::isEmpty);
    }
    public static boolean isAnyEmpty(String ...params){
        return Arrays.stream(params).anyMatch(MyString::isEmpty);
    }

    public static boolean isEmptyOrNull(String str){
        return str == null || str.equals("");
    }
    public static boolean isAllEmptyOrNull(String ...params){
        return Arrays.stream(params).allMatch(MyString::isEmptyOrNull);
    }
    public static boolean isAnyEmptyOrNull(String ...params){
        return Arrays.stream(params).anyMatch(MyString::isEmptyOrNull);
    }

    public static String toUrlParams(Map<String, String> nameAndValues){
        StringBuilder tmp = new StringBuilder("?");
        for(var item : nameAndValues.entrySet()){
            tmp.append(item.getKey()).append("=").append(item.getValue()).append("&");
        }
        return tmp.deleteCharAt(tmp.length()-1).toString();
    }

    public static String bytesToHex(byte[] nums){
        StringBuilder result = new StringBuilder();
        for(var num : nums){
            result.append(byteToHex(num));
        }
        return result.toString();
    }

    public static String byteToHex(byte num){
        char[] hexDigits = new char[2];
        hexDigits[0] = Character.forDigit((num >> 4) & 0xf, 16);
        hexDigits[1] = Character.forDigit(num & 0xf, 16);
        return new String(hexDigits);
    }
    private static int toDigit(char hexChar) {
        int digit = Character.digit(hexChar, 16);
        if(digit == -1) {
            throw new IllegalArgumentException("Invalid Hexadecimal Character: "+ hexChar);
        }
        return digit;
    }
    public static byte hexToByte(String hexString) {
        int firstDigit = toDigit(hexString.charAt(0));
        int secondDigit = toDigit(hexString.charAt(1));
        return (byte) ((firstDigit << 4) + secondDigit);
    }
    public static boolean isUuid(String str){
        if(str == null || str.equals("")) return false;
        return str.matches("^[0-9a-zA-Z]{32}$");
    }
    public static boolean isPhoneNo(String str){
        if(str == null || str.equals("")) return false;
        return str.matches("^1\\d{10}$");
    }
    public static boolean isIdCardNo(String str){
        if(str == null || str.equals("")) return false;
        //身份证号15位或18位，最后一位可以为字母
        if(!str.matches("(^\\d{17}[0-9a-zA-Z]|\\d{14}[0-9a-zA-Z])$")){
            return false;
        }
        //接着验证身份证中的日期是否合法，分为年月日，从第7位开始
        var tmp = Pattern.compile("\\d{6}(\\d{8}).*").matcher(str);
        if(tmp.find()){
            var birthDay = tmp.group(1);
            var yearMonthDay = Pattern.compile("(\\d{4})(\\d{2})(\\d{2})").matcher(birthDay);
            if(yearMonthDay.find()){
                var year = yearMonthDay.group(1);
                var month = yearMonthDay.group(2);
                var day = yearMonthDay.group(3);
                return MyNumber.isBetween(Integer.parseInt(year), 1900, LocalDate.now().getYear()) &&
                        MyNumber.isBetween(Integer.parseInt(month), 1, 12) &&
                        MyNumber.isBetween(Integer.parseInt(day), 1, 32);
            } else return false;
        }else return false;
    }
    public static boolean isEmail(String str){
        if(str == null || str.equals("")) return false;
        return str.matches("^(\\w)+(\\.\\w+)*@(\\w)+(\\.\\w+)+$");
    }

    public static boolean isIPv4(String ip){
        return ip.matches("((25[0-5]|2[0-4]\\d|[01]?\\d\\d?)(?:\\.)){3}(25[0-5]|2[0-4]\\d|[01]?\\d\\d?)");
    }

    public static void main(String[] args){
        var map = new HashMap<String, String>();
        map.put("start", "2015-01-10T08:00:35");
        map.put("limit", "100");
        map.put("cursor", "191811_505253765_1544055568061");
        System.out.println(toUrlParams(map));
        System.out.println(isEmail("xxx@xxx.com.cn.cn"));
        System.out.println(isIPv4("00.1.1.1"));

        var bytes = "DGXXX".getBytes();
        var DG = "DG".getBytes();
        if(!MyByte.equals(DG, Arrays.copyOfRange(bytes,0,2))){
            System.out.println("!=");
        }else {
            System.out.println("==");
        }
    }
}

