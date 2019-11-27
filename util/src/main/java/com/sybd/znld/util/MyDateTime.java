package com.sybd.znld.util;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public final class MyDateTime {
    public static final String FORMAT = "yyyy-MM-ddTHH:mm:ss";
    public static final String FORMAT1 = "yyyy-MM-dd HH:mm:ss";
    public static final String FORMAT2 = "yyyy-MM-dd HH:mm:ss.SSS";
    public static final String FORMAT3 = "yyyyMMddHHmmss_SSS";
    public static final String FORMAT4 = "yyyy-MM-dd";
    public static final String ISO8601 = "yyyy-MM-dd'T'HH:mm:ss";

    public static LocalDateTime now(){
        return LocalDateTime.now();
    }

    public static LocalDateTime toLocalDateTime(String value){
        return LocalDateTime.parse(value, DateTimeFormatter.ofPattern(FORMAT1));
    }
    public static LocalDateTime toLocalDateTime(String value, String format){
        return LocalDateTime.parse(value, DateTimeFormatter.ofPattern(format));
    }

    public static String format(LocalDateTime dateTime, String formatString){
        return dateTime.format(DateTimeFormatter.ofPattern(formatString));
    }

    // 检测时间戳有效性
    public static boolean isValid(Long timestamp){
        var max = Instant.MAX.toEpochMilli();
        var min = Instant.MIN.toEpochMilli();
        return timestamp <= max && timestamp >= min;
    }

    public static boolean isBefore(Long timestamp1, Long timestamp2){
        var a = toLocalDateTime(timestamp1);
        var b = toLocalDateTime(timestamp2);
        return a.isBefore(b);
    }
    public static boolean isBefore(Long timestamp1, Long timestamp2, ZoneOffset zoneOffset){
        var a = toLocalDateTime(timestamp1, zoneOffset);
        var b = toLocalDateTime(timestamp2, zoneOffset);
        return a.isBefore(b);
    }
    public static boolean isAfter(Long timestamp1, Long timestamp2){
        var a = toLocalDateTime(timestamp1);
        var b = toLocalDateTime(timestamp2);
        return a.isAfter(b);
    }
    public static boolean isAfter(Long timestamp1, Long timestamp2, ZoneOffset zoneOffset){
        var a = toLocalDateTime(timestamp1, zoneOffset);
        var b = toLocalDateTime(timestamp2, zoneOffset);
        return a.isAfter(b);
    }

    // 时间字符串转换为时间戳
    public static Long toTimestamp(String value, String format){
        var tmp = toLocalDateTime(value, format);
        var zoneId = ZoneId.systemDefault();
        var zoneOffset = Instant.now().atZone(zoneId).getOffset();
        return tmp.toInstant(zoneOffset).toEpochMilli();
    }
    public static Long toTimestamp(String value, String format, ZoneOffset zoneOffset){
        var tmp = toLocalDateTime(value, format);
        return tmp.toInstant(zoneOffset).toEpochMilli();
    }
    public static Long toTimestamp(LocalDateTime dateTime){
        var zoneId = ZoneId.systemDefault();
        var zoneOffset = Instant.now().atZone(zoneId).getOffset();
        return dateTime.toInstant(zoneOffset).toEpochMilli();
    }
    public static Long toTimestamp(LocalDateTime dateTime, ZoneOffset zoneOffset){
        return dateTime.toInstant(zoneOffset).toEpochMilli();
    }

    public static Long toTimestamp(LocalDateTime dateTime, int seconds){
        var tmp = dateTime.plusSeconds(seconds);
        var zoneId = ZoneId.systemDefault();
        var zoneOffset = Instant.now().atZone(zoneId).getOffset();
        return tmp.toInstant(zoneOffset).toEpochMilli();
    }

    // 时间戳转换为本地时间
    public static LocalDateTime toLocalDateTime(Long timestamp){
        try{
            var instant = Instant.ofEpochMilli(timestamp);
            var zoneId = ZoneId.systemDefault();
            return LocalDateTime.ofInstant(instant, zoneId);
        }catch (Exception ex){
            return null;
        }
    }

    public static LocalDateTime toLocalDateTime(Long timestamp, ZoneOffset zoneOffset){
        var instant = Instant.ofEpochMilli(timestamp);
        return LocalDateTime.ofInstant(instant, zoneOffset);
    }
    public static LocalDateTime toLocalDateTime(Long timestamp, ZoneId zoneId){
        var instant = Instant.ofEpochMilli(timestamp);
        return LocalDateTime.ofInstant(instant, zoneId);
    }

    public static Date toDate(LocalDateTime localDateTime){
        var zoneId = ZoneId.systemDefault();
        var zdt = localDateTime.atZone(zoneId);
        return Date.from(zdt.toInstant());
    }

    public static LocalDateTime toLocalDateTime(Date date){
        var instant = date.toInstant();
        var zoneId = ZoneId.systemDefault();
        return instant.atZone(zoneId).toLocalDateTime();
    }

    public static boolean isBetween(LocalDateTime it, LocalDateTime min, LocalDateTime max){
        return it.isAfter(min) && (it.isBefore(max) || it.isEqual(max));
    }

    // 测试是否为未来时间
    public static boolean isFuture(Long timestamp){
        var it = toLocalDateTime(timestamp);
        return it.isAfter(LocalDateTime.now());
    }
    public static boolean isFuture(Long timestamp, ZoneOffset zoneOffset){
        var it = toLocalDateTime(timestamp, zoneOffset);
        var now = LocalDateTime.now(zoneOffset);
        return it.isAfter(now);
    }
    public static boolean isFuture(LocalDateTime it){
        return it.isAfter(LocalDateTime.now());
    }
    public static boolean isFuture(LocalDateTime it, ZoneOffset zoneOffset){
        var now = LocalDateTime.now(zoneOffset);
        return it.isAfter(now);
    }
    public static boolean isAnyFuture(LocalDateTime ...its){
        var now = LocalDateTime.now();
        for(var it: its){
            if(it.isAfter(now)) return true;
        }
        return false;
    }
    public static boolean isAnyFuture(Long ...its){
        var now = LocalDateTime.now();
        for(var it: its){
            if(toLocalDateTime(it).isAfter(now)) return true;
        }
        return false;
    }
    public static boolean isAnyFuture(ZoneOffset zoneOffset, LocalDateTime ...its){
        var now = LocalDateTime.now(zoneOffset);
        for(var it: its){
            if(it.isAfter(now)) return true;
        }
        return false;
    }
    public static boolean isAnyFuture(ZoneOffset zoneOffset, Long ...its){
        var now = LocalDateTime.now(zoneOffset);
        for(var it: its){
            if(toLocalDateTime(it).isAfter(now)) return true;
        }
        return false;
    }
    public static boolean isAllFuture(LocalDateTime ...its){
        var now = LocalDateTime.now();
        for(var it: its){
            if(it.isBefore(now)) return false;
        }
        return true;
    }
    public static boolean isAllFuture(Long ...its){
        var now = LocalDateTime.now();
        for(var it: its){
            if(toLocalDateTime(it).isBefore(now)) return false;
        }
        return true;
    }
    public static boolean isAllFuture(ZoneOffset zoneOffset, LocalDateTime ...its){
        var now = LocalDateTime.now(zoneOffset);
        for(var it: its){
            if(it.isBefore(now)) return false;
        }
        return true;
    }
    public static boolean isAllFuture(ZoneOffset zoneOffset, Long ...its){
        var now = LocalDateTime.now(zoneOffset);
        for(var it: its){
            if(toLocalDateTime(it).isBefore(now)) return false;
        }
        return true;
    }
    //全部为未来时间，并且它们严格排列（从左往右离现在越来越远）、没有相等
    public static boolean isAllFutureAndStrict(LocalDateTime ...its){
        for(var i = 0; i < its.length-1; i++){
            var tmp1 = its[i];
            var tmp2 = its[i+1];
            if(tmp2.isBefore(tmp1) || tmp2.isEqual(tmp1)) return false;
        }
        var now = LocalDateTime.now();
        return now.isBefore(its[0]);
    }
    public static boolean isAllFutureAndStrict(Long ...its){
        for(var i = 0; i < its.length-1; i++){
            var tmp1 = toLocalDateTime(its[i]);
            var tmp2 = toLocalDateTime(its[i+1]);
            if(tmp2.isBefore(tmp1) || tmp2.isEqual(tmp1)) return false;
        }
        var now = LocalDateTime.now();
        var tmp = toLocalDateTime(its[0]);
        return now.isBefore(tmp);
    }
    public static boolean isAllFutureAndStrict(ZoneOffset zoneOffset, LocalDateTime ...its){
        for(var i = 0; i < its.length-1; i++){
            var tmp1 = its[i];
            var tmp2 = its[i+1];
            if(tmp2.isBefore(tmp1) || tmp2.isEqual(tmp1)) return false;
        }
        var now = LocalDateTime.now(zoneOffset);
        return now.isBefore(its[0]);
    }
    public static boolean isAllFutureAndStrict(ZoneOffset zoneOffset, Long ...its){
        for(var i = 0; i < its.length-1; i++){
            var tmp1 = toLocalDateTime(its[i]);
            var tmp2 = toLocalDateTime(its[i+1]);
            if(tmp2.isBefore(tmp1) || tmp2.isEqual(tmp1)) return false;
        }
        var now = LocalDateTime.now(zoneOffset);
        var tmp = toLocalDateTime(its[0]);
        return now.isBefore(tmp);
    }
    // 测试是否为过去时间
    public static boolean isPast(LocalDateTime it){
        return it.isBefore(LocalDateTime.now());
    }
    public static boolean isPast(Long timestamp){
        var it = toLocalDateTime(timestamp);
        return it.isBefore(LocalDateTime.now());
    }
    public static boolean isPast(Long it, ZoneOffset zoneOffset){
        var now = LocalDateTime.now(zoneOffset);
        var tmp = LocalDateTime.ofInstant(Instant.ofEpochMilli(it), zoneOffset);
        return tmp.isBefore(now);
    }
    public static boolean isPast(LocalDateTime it, ZoneOffset zoneOffset){
        var now = LocalDateTime.now(zoneOffset);
        return it.isBefore(now);
    }
    public static boolean isAnyPast(LocalDateTime ...its){
        var now = LocalDateTime.now();
        for(var it: its){
            if(it.isBefore(now)) return true;
        }
        return false;
    }
    public static boolean isAnyPast(Long ...its){
        var now = LocalDateTime.now();
        for(var it: its){
            if(toLocalDateTime(it).isBefore(now)) return true;
        }
        return false;
    }
    public static boolean isAnyPast(ZoneOffset zoneOffset, LocalDateTime ...its){
        var now = LocalDateTime.now(zoneOffset);
        for(var it: its){
            if(it.isBefore(now)) return true;
        }
        return false;
    }
    public static boolean isAnyPast(ZoneOffset zoneOffset, Long ...its){
        var now = LocalDateTime.now(zoneOffset);
        for(var it: its){
            if(toLocalDateTime(it).isBefore(now)) return true;
        }
        return false;
    }
    public static boolean isAllPast(LocalDateTime ...its){
        var now = LocalDateTime.now();
        for(var it: its){
            if(it.isAfter(now)) return false;
        }
        return true;
    }
    public static boolean isAllPast(Long ...its){
        var now = LocalDateTime.now();
        for(var it: its){
            if(toLocalDateTime(it).isAfter(now)) return false;
        }
        return true;
    }
    public static boolean isAllPast(ZoneOffset zoneOffset, LocalDateTime ...its){
        var now = LocalDateTime.now(zoneOffset);
        for(var it: its){
            if(it.isAfter(now)) return false;
        }
        return true;
    }
    public static boolean isAllPast(ZoneOffset zoneOffset, Long ...its){
        var now = LocalDateTime.now(zoneOffset);
        for(var it: its){
            if(toLocalDateTime(it).isAfter(now)) return false;
        }
        return true;
    }
    //全部为过去时间，并且它们严格排列（从左往右离现在越来越近）、没有相等
    public static boolean isAllPastAndStrict(LocalDateTime ...its){
        for(var i = 0; i < its.length-1; i++){
            if(its[i].isAfter(its[i+1]) || its[i].isEqual(its[i+1])) return false;
        }
        var now = LocalDateTime.now();
        return now.isAfter(its[its.length-1]);
    }
    public static boolean isAllPastAndStrict(Long ...its){
        for(var i = 0; i < its.length-1; i++){
            var tmp1 = toLocalDateTime(its[i]);
            var tmp2 = toLocalDateTime(its[i+1]);
            if(tmp1.isAfter(tmp2) || tmp1.isEqual(tmp2)) return false;
        }
        var now = LocalDateTime.now();
        var tmp = toLocalDateTime(its[its.length-1]);
        return now.isAfter(tmp);
    }
    public static boolean isAllPastAndStrict(ZoneOffset zoneOffset, LocalDateTime ...its){
        for(var i = 0; i < its.length-1; i++){
            if(its[i].isAfter(its[i+1]) || its[i].isEqual(its[i+1])) return false;
        }
        var now = LocalDateTime.now(zoneOffset);
        return now.isAfter(its[its.length-1]);
    }
    public static boolean isAllPastAndStrict(ZoneOffset zoneOffset, Long ...its){
        for(var i = 0; i < its.length-1; i++){
            if(toLocalDateTime(its[i]).isAfter(toLocalDateTime(its[i+1])) ||
               toLocalDateTime(its[i]).isEqual(toLocalDateTime(its[i+1]))) return false;
        }
        var now = LocalDateTime.now(zoneOffset);
        return now.isAfter(toLocalDateTime(its[its.length-1]));
    }

    public static boolean isBeforeOrEqual(Long a, Long b, ZoneOffset zoneOffset){
        var t1 = LocalDateTime.ofInstant(Instant.ofEpochMilli(a), zoneOffset);
        var t2 = LocalDateTime.ofInstant(Instant.ofEpochMilli(b), zoneOffset);
        return t1.isBefore(t2) || t1.isEqual(t2);
    }
    // 格式化
    public static String toString(LocalDateTime dateTime, String format){
        return DateTimeFormatter.ofPattern(format).format(dateTime);
    }
    public static String toString(LocalDate date, String format){
        return DateTimeFormatter.ofPattern(format).format(date);
    }
    public static String toString(Long timestamp, String format){
        var dateTime = toLocalDateTime(timestamp);
        return toString(dateTime, format);
    }

    public static LocalDateTime maxOfDay(LocalDateTime date){
        var localDate = LocalDate.of(date.getYear(), date.getMonth(), date.getDayOfMonth());
        var localTime = LocalTime.MAX;
        return LocalDateTime.of(localDate, localTime);
    }

    public static void main(String[] args){
        System.out.println(MyDateTime.toString(LocalDateTime.now(), MyDateTime.FORMAT4));
    }
}
