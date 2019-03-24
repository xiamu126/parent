package com.sybd.security.oauth2.server.mapper;

import org.apache.ibatis.annotations.Mapper;
import com.sybd.znld.model.oauth.OAuthClientDetailsModel;

import java.util.List;

@Mapper
public interface OAuthClientDetailsMapper {
    List<OAuthClientDetailsModel> selectAll();
}
