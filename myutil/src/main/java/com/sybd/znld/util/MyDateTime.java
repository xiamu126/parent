package com.sybd.znld.util;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public final class MyDateTime {
    public static final String format1 = "yyyy-MM-dd HH:mm:ss";
    public static final String format2 = "yyyy-MM-dd HH:mm:ss.SSS";

    public static LocalDateTime toLocalDateTime(String value){
        return LocalDateTime.parse(value, DateTimeFormatter.ofPattern(format1));
    }
    public static LocalDateTime toLocalDateTime(String value, String format){
        return LocalDateTime.parse(value, DateTimeFormatter.ofPattern(format));
    }

    public static Long toLong(String value){
        var tmp = toLocalDateTime(value);
        var zoneId = ZoneId.systemDefault();
        var zoneOffset = Instant.now().atZone(zoneId).getOffset();
        return tmp.toInstant(zoneOffset).toEpochMilli();
    }

    public static LocalDateTime toLocalDateTime(Long timestamp){
        var instant = Instant.ofEpochMilli(timestamp);
        var zoneId = ZoneId.systemDefault();
        var localDateTime = LocalDateTime.ofInstant(instant, zoneId);
        if(localDateTime.getSecond() == 0){
            localDateTime = localDateTime.plusSeconds(1);
        }
        return localDateTime;
    }

    public static LocalDateTime now(){
        return LocalDateTime.now();
    }

    public static String toString(LocalDateTime dateTime){
        return DateTimeFormatter.ofPattern(format1).format(dateTime);
    }

    public static LocalDateTime maxOfDay(LocalDateTime date){
        var localDate = LocalDate.of(date.getYear(), date.getMonth(), date.getDayOfMonth());
        var localTime = LocalTime.MAX;
        return LocalDateTime.of(localDate, localTime);
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

    public static boolean isFuture(LocalDateTime it){
        return it.isAfter(LocalDateTime.now());
    }
    public static boolean isAllFuture(LocalDateTime ...its){
        var now = LocalDateTime.now();
        for(var it: its){
            if(it.isBefore(now)) return false;
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
    public static boolean isPast(LocalDateTime it){
        return it.isBefore(LocalDateTime.now());
    }
    public static boolean isAllPast(LocalDateTime ...its){
        var now = LocalDateTime.now();
        for(var it: its){
            if(it.isAfter(now)) return false;
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
}
