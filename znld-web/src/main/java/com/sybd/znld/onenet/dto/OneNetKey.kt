package com.sybd.znld.onenet.dto;

import java.io.Serializable;

class OneNetKey(var objId: Int = 0, var objInstId: Int = 0, var resId: Int = 0): Serializable {

    fun  toDataStreamId(): String{
        return "${objId}_${objInstId}_$resId";
    }

    companion object {
        @JvmStatic fun toDataStreamId(objId: Int, objInstId: Int, resId: Int): String{
            return "${objId}_${objInstId}_$resId";
        }
        @JvmStatic fun  from(id: String ): OneNetKey?{
          /*val regex = "^\\d+_\\d+_\\d+$";
            val pattern = Pattern.compile(regex);
            val matcher = pattern.matcher(id);
            if (!matcher.matches()) throw AssertionError();*/
            val regex = "^\\d+_\\d+_\\d+$".toRegex();
            if(!regex.matches(id)) return null
            val array = id.split("_");
            return OneNetKey(Integer.parseInt(array[0]), Integer.parseInt(array[1]), Integer.parseInt(array[2]));
        }
    }
}
