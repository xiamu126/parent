package com.sybd.znld.mapper.lamp;

import com.sybd.znld.mapper.db.DbSource;
import com.sybd.znld.model.onenet.DataPushModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
@DbSource("znld")
public interface DataDeviceOnOffMapper {
    int insert(DataPushModel model);
    DataPushModel selectByDeviceIdAndResourceName(@Param("deviceId") Integer deviceId, @Param("name") String name);
    int updateByDeviceIdAndResourceName(DataPushModel model);
}
