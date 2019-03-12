package com.sybd.znld.service.v2.oauth;

import com.sybd.znld.model.v2.oauth.OAuthClientDetailsModel;

import java.util.List;

public interface IOAuthService {
    List<OAuthClientDetailsModel> getClientDetails();
    OAuthClientDetailsModel getClientDetailsByClientId(String clientId);
    OAuthClientDetailsModel insertClientDetails(OAuthClientDetailsModel model);
    boolean updateClientDetailsByClientId(OAuthClientDetailsModel model);
    boolean deleteClientDetailsByClientId(String clientId);
}
