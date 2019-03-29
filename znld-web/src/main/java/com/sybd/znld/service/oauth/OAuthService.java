package com.sybd.znld.service.oauth;

import com.sybd.znld.db.DbSource;
import com.sybd.znld.model.oauth.OAuthClientDetailsModel;
import com.sybd.znld.service.oauth.mapper.OAuthClientDetailsMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
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
    public OAuthClientDetailsModel insertClientDetails(OAuthClientDetailsModel model) {
        if(this.oauthClientDetailsMapper.insert(model) > 0 ) return model;
        else return null;
    }

    @Override
    public boolean updateClientDetailsByClientId(OAuthClientDetailsModel model) {
        return this.oauthClientDetailsMapper.updateByClientId(model) > 0;
    }

    @Override
    public boolean deleteClientDetailsByClientId(String clientId) {
        return this.oauthClientDetailsMapper.deleteByClientId(clientId) > 0;
    }
}
