package com.sybd.znld.service.znld.mapper;

import com.sybd.znld.model.znld.HttpLogModel;
import org.apache.ibatis.annotations.Mapper;
import com.sybd.znld.model.znld.HttpLogModel.HttpMethod;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface HttpLogMapper {
    int insert(HttpLogModel model);
    HttpLogModel selectById(String id);
    HttpLogModel selectByAll(HttpLogModel model);
    List<HttpLogModel> selectByTimeAfter(LocalDateTime time);
    List<HttpLogModel> selectByTimeBetween(LocalDateTime begin, LocalDateTime end);
    List<HttpLogModel> selectByTimeAfterAndMethod(LocalDateTime time, HttpMethod method);
    List<HttpLogModel> selectByTimeBetweenAndMethod(LocalDateTime begin, LocalDateTime end, HttpMethod method);
}
