package com.sybd.znld.mapper.lamp;

import com.sybd.znld.mapper.db.DbSource;
import com.sybd.znld.model.lamp.Location;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
@DbSource("znld")
public interface LocationMapper {
    int insert(Location location);
    //int insertChild(String rootId, String parentId, String name, int position);
    Location selectById(String id);
    Location selectByOrganIdName(@Param("organId") String organId, @Param("name") String name);
    List<Location> selectRoots(@Param("organId") String organId); // 获取这个组织下的所有根节点（顶级路径）
    Location selectRoot(@Param("organId") String organId, @Param("name") String name);
    List<Location> selectChild(String parentId); // 获取直接子节点
    List<Location> selectChildren(String parentId); // 获取所有子节点
    //int deleteChild(String childId);
    //int deleteChildren(String childId);
}
