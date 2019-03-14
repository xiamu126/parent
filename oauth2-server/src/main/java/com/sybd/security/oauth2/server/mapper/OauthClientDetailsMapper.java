package com.sybd.security.oauth2.server.mapper;

import com.sybd.security.oauth2.server.model.OauthClientDetails;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface OauthClientDetailsMapper {
    List<OauthClientDetails> selectAll();
}
