package com.sybd.znld.service.znld.mapper;

import com.sybd.znld.db.DbSource;
import com.sybd.onenet.model.OneNetKey;
import com.sybd.znld.model.OneNetResourceModel;
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
}
