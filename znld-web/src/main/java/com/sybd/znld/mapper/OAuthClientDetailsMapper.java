package com.sybd.znld.mapper;

import com.sybd.znld.model.v2.oauth.OAuthClientDetailsModel;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface OAuthClientDetailsMapper {
    List<OAuthClientDetailsModel> getClientDetails();
    OAuthClientDetailsModel getClientDetailsByClientId(String clientId);
}
