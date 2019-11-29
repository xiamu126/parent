package com.sybd.znld.model;

public interface IValidForDbInsertWithZoneId {
    boolean isValidForInsert(String zoneId);
}
