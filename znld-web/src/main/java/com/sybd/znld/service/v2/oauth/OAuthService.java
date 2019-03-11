package com.sybd.znld.service.v2.oauth;

import com.sybd.znld.db.DbSource;
import com.sybd.znld.mapper.OAuthClientDetailsMapper;
import com.sybd.znld.model.v2.oauth.OAuthClientDetailsModel;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@DbSource("oauth")
public class OAuthService implements IOAuthService {
    private final OAuthClientDetailsMapper oAuthClientDetailsMapper;

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    public OAuthService(OAuthClientDetailsMapper oAuthClientDetailsMapper) {
        this.oAuthClientDetailsMapper = oAuthClientDetailsMapper;
    }

    @Override
    @DbSource("oauth")
    public List<OAuthClientDetailsModel> getClientDetails() {
        return this.oAuthClientDetailsMapper.getClientDetails();
    }

    @Override
    @DbSource("oauth")
    public OAuthClientDetailsModel getClientDetailsByClientId(String clientId) {
        return this.oAuthClientDetailsMapper.getClientDetailsByClientId(clientId);
    }
}
