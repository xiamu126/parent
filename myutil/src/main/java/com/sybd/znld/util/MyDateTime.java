package com.sybd.znld.util;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public final class MyDateTime {
    public static final String format = "yyyy-MM-ddTHH:mm:ss";
    public static final String format1 = "yyyy-MM-dd HH:mm:ss";
    public static final String format2 = "yyyy-MM-dd HH:mm:ss.SSS";

    public static LocalDateTime now(){
        return LocalDateTime.now();
    }

    /*public static LocalDateTime toLocalDateTime(String value){
        return LocalDateTime.parse(value, DateTimeFormatter.ofPattern(format1));
    }*/
    public static LocalDateTime toLocalDateTime(String value, String format){
        return LocalDateTime.parse(value, DateTimeFormatter.ofPattern(format));
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

    // 时间戳转换为本地时间
    public static LocalDateTime toLocalDateTime(Long timestamp){
        var instant = Instant.ofEpochMilli(timestamp);
        var zoneId = ZoneId.systemDefault();
        var localDateTime = LocalDateTime.ofInstant(instant, zoneId);
        /*if(localDateTime.getSecond() == 0){
            localDateTime = localDateTime.plusSeconds(1);
        }*/
        return localDateTime;
    }
    public static LocalDateTime toLocalDateTime(Long timestamp, ZoneOffset zoneOffset){
        var instant = Instant.ofEpochMilli(timestamp);
        return LocalDateTime.ofInstant(instant, zoneOffset);
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
    //全部为未来时间，并且它们按严格升序排列（越往后离现在越远）、即没有相等
    public static boolean isAllFutureAndStrictAsc(LocalDateTime ...its){
        for(var i = 0; i < its.length - 2; i++){
            if(its[i].isBefore(its[i+1]) || its[i].isEqual(its[i+1])) return false;
        }
        var now = LocalDateTime.now();
        return !its[its.length - 1].isBefore(now);
    }
    public static boolean isAllFutureAndStrictAsc(Long ...its){
        for(var i = 0; i < its.length - 2; i++){
            if(toLocalDateTime(its[i]).isBefore(toLocalDateTime(its[i+1])) ||
               toLocalDateTime(its[i]).isEqual(toLocalDateTime(its[i+1]))) return false;
        }
        var now = LocalDateTime.now();
        return !toLocalDateTime(its[its.length - 1]).isBefore(now);
    }
    public static boolean isAllFutureAndStrictAsc(ZoneOffset zoneOffset, LocalDateTime ...its){
        for(var i = 0; i < its.length - 2; i++){
            if(its[i].isBefore(its[i+1]) || its[i].isEqual(its[i+1])) return false;
        }
        var now = LocalDateTime.now(zoneOffset);
        return !its[its.length - 1].isBefore(now);
    }
    public static boolean isAllFutureAndStrictAsc(ZoneOffset zoneOffset, Long ...its){
        for(var i = 0; i < its.length - 2; i++){
            if(toLocalDateTime(its[i]).isBefore(toLocalDateTime(its[i+1])) ||
               toLocalDateTime(its[i]).isEqual(toLocalDateTime(its[i+1]))) return false;
        }
        var now = LocalDateTime.now(zoneOffset);
        return !toLocalDateTime(its[its.length - 1]).isBefore(now);
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
    //全部为过去时间，并且它们按严格降序排列（越往前离现在越远）、即没有相等
    public static boolean isAllPastAndStrictDesc(LocalDateTime ...its){
        for(var i = 0; i < its.length - 2; i++){
            if(its[i].isAfter(its[i+1]) || its[i].isEqual(its[i+1])) return false;
        }
        var now = LocalDateTime.now();
        return !its[0].isAfter(now);
    }
    public static boolean isAllPastAndStrictDesc(Long ...its){
        for(var i = 0; i < its.length - 2; i++){
            if(toLocalDateTime(its[i]).isAfter(toLocalDateTime(its[i+1])) ||
               toLocalDateTime(its[i]).isEqual(toLocalDateTime(its[i+1]))) return false;
        }
        var now = LocalDateTime.now();
        return !toLocalDateTime(its[0]).isAfter(now);
    }
    public static boolean isAllPastAndStrictDesc(ZoneOffset zoneOffset, LocalDateTime ...its){
        for(var i = 0; i < its.length - 2; i++){
            if(its[i].isAfter(its[i+1]) || its[i].isEqual(its[i+1])) return false;
        }
        var now = LocalDateTime.now(zoneOffset);
        return !its[0].isAfter(now);
    }
    public static boolean isAllPastAndStrictDesc(ZoneOffset zoneOffset, Long ...its){
        for(var i = 0; i < its.length - 2; i++){
            if(toLocalDateTime(its[i]).isAfter(toLocalDateTime(its[i+1])) ||
               toLocalDateTime(its[i]).isEqual(toLocalDateTime(its[i+1]))) return false;
        }
        var now = LocalDateTime.now(zoneOffset);
        return !toLocalDateTime(its[0]).isAfter(now);
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

    public static LocalDateTime maxOfDay(LocalDateTime date){
        var localDate = LocalDate.of(date.getYear(), date.getMonth(), date.getDayOfMonth());
        var localTime = LocalTime.MAX;
        return LocalDateTime.of(localDate, localTime);
    }

    public static void main(String[] args){
        var it = LocalDateTime.now();
        System.out.println(it);
        it = it.minusSeconds(it.getSecond());
        System.out.println(toLocalDateTime(1555311229471L));
    }
}
