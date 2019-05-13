package com.sybd.znld.mapper.lamp;

import com.sybd.znld.mapper.db.DbSource;
import com.sybd.znld.model.onenet.OneNetKey;
import com.sybd.znld.model.lamp.OneNetResourceModel;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
@DbSource("znld")
public interface OneNetResourceMapper {
    int insert(OneNetResourceModel model);
    OneNetResourceModel selectByOneNetKey(OneNetKey oneNetKey);
    OneNetResourceModel selectById(String id);
    List<OneNetResourceModel> selectByIds(List<String> ids);
    OneNetResourceModel selectByCommandValue(String cmd);
    List<OneNetResourceModel> selectByResourceType(short type);
    OneNetResourceModel selectByResourceName(String resourceName);
}
