package com.sybd.znld.mapper.lamp;

import com.sybd.znld.model.lamp.GpggaModel;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface GpggaMapper {
    int insert(GpggaModel model);
    int append(@Param("id") String id, @Param("content") String content);
    int appendByFilename(@Param("filename") String filename, @Param("content") String content);
    GpggaModel selectByFilename(@Param("filename") String filename);
    List<GpggaModel> selectBetween(@Param("beginTime") LocalDateTime beginTime, @Param("endTime") LocalDateTime endTime);
}
