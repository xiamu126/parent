package com.sybd.znld.model.lamp;

import com.sybd.znld.model.IValid;
import com.sybd.znld.util.MyString;
import lombok.*;

import java.io.Serializable;

@Getter @Setter @Builder @EqualsAndHashCode @NoArgsConstructor @AllArgsConstructor
public class RegionModel implements IValid, Serializable {
    public String id;
    public String name;
    public Short type = Type.PHYSICAL;
    public String organizationId;
    public Short status = Status.OK;

    @Override
    public boolean isValid() {
        return !MyString.isEmptyOrNull(name) &&
                Type.isValid(type) &&
                MyString.isUuid(organizationId) &&
                Status.isValid(status);
    }

    public static class Type{
        public static final short PHYSICAL = 0;
        public static final short VIRTUAL = 1;
        public static boolean isValid(short v){
            switch (v){
                case PHYSICAL: case VIRTUAL: return true;
                default: return false;
            }
        }
    }

    public static class Status{
        public static final short OK = 0;
        public static final short DELETED = 1;

        public static boolean isValid(short v){
            switch (v){
                case OK: case DELETED:
                    return true;
                default:
                    return false;
            }
        }
    }
}
