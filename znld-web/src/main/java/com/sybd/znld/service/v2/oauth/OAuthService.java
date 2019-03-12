package com.sybd.znld.service.v2.oauth;

import com.sybd.znld.db.DbSource;
import com.sybd.znld.service.mapper.OAuthClientDetailsMapper;
import com.sybd.znld.model.v2.oauth.OAuthClientDetailsModel;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@DbSource("oauth")
public class OAuthService implements IOAuthService {
    private final OAuthClientDetailsMapper oauthClientDetailsMapper;

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    public OAuthService(OAuthClientDetailsMapper oauthClientDetailsMapper) {
        this.oauthClientDetailsMapper = oauthClientDetailsMapper;
    }

    @Override
    public List<OAuthClientDetailsModel> getClientDetails() {
        return this.oauthClientDetailsMapper.selectAll();
    }

    @Override
    public OAuthClientDetailsModel getClientDetailsByClientId(String clientId) {
        return this.oauthClientDetailsMapper.selectByClientId(clientId);
    }

    @Override
    public boolean insertClientDetails(OAuthClientDetailsModel model) {
        return this.oauthClientDetailsMapper.insert(model) > 0;
    }

    @Override
    public boolean updateClientDetailsByClientId(OAuthClientDetailsModel model) {
        return this.oauthClientDetailsMapper.updateByClientId(model) > 0;
    }
}
