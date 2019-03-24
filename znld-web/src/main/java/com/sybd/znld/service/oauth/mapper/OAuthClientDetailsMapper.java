package com.sybd.znld.service.oauth.mapper;

import com.sybd.znld.model.oauth.OAuthClientDetailsModel;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface OAuthClientDetailsMapper {
    List<OAuthClientDetailsModel> selectAll();
    OAuthClientDetailsModel selectByClientId(String clientId);
    int insert(OAuthClientDetailsModel model);
    int updateByClientId(OAuthClientDetailsModel model);
    int deleteByClientId(String clientId);
}