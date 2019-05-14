package com.sybd.znld.validate;

import java.util.regex.Pattern;

class baseValidator {
    protected boolean isValid(boolean required, String value, Pattern pattern){
        if(required){
            var matcher = pattern.matcher(value);
            return matcher.matches();
        }
        if(value == null) return true;
        var matcher = pattern.matcher(value);
        return matcher.matches();
    }
}
