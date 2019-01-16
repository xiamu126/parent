package com.sybd.security.oauth2.server.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sybd.security.oauth2.server.model.OauthClientDetails;
import java.util.List;

public interface OauthClientDetailsMapper extends BaseMapper<OauthClientDetails> {
    List<OauthClientDetails> selectAll();
}
