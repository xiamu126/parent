package com.whatever.util;

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
}
