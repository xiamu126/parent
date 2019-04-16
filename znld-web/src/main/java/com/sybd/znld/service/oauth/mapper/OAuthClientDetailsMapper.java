package com.sybd.znld.service.oauth.mapper;

import com.sybd.znld.db.DbSource;
import com.sybd.oauth.model.OAuthClientDetailsModel;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
@DbSource("oauth")
public interface OAuthClientDetailsMapper {
    List<OAuthClientDetailsModel> selectAll();
    OAuthClientDetailsModel selectByClientId(String clientId);
    int insert(OAuthClientDetailsModel model);
    int updateByClientId(OAuthClientDetailsModel model);
    int deleteByClientId(String clientId);
}
