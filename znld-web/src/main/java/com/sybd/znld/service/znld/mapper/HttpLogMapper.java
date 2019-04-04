package com.sybd.znld.service.znld.mapper;

import com.sybd.znld.db.DbSource;
import com.sybd.znld.model.znld.HttpLogModel;
import org.apache.ibatis.annotations.Mapper;
import com.sybd.znld.model.znld.HttpLogModel.HttpMethod;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
@DbSource("znld")
public interface HttpLogMapper {
    int insert(HttpLogModel model);
    HttpLogModel selectById(String id);
    List<HttpLogModel> selectByAll(HttpLogModel model);
    List<HttpLogModel> selectByTimeAfter(LocalDateTime time);
    List<HttpLogModel> selectByTimeBetween(LocalDateTime begin, LocalDateTime end);
    List<HttpLogModel> selectByTimeAfterAndMethod(LocalDateTime time, HttpMethod method);
    List<HttpLogModel> selectByTimeBetweenAndMethod(LocalDateTime begin, LocalDateTime end, HttpMethod method);
}
