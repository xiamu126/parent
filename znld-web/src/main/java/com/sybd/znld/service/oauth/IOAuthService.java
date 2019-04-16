package com.sybd.znld.service.oauth;

import com.sybd.oauth.model.OAuthClientDetailsModel;

import java.util.List;

public interface IOAuthService {
    List<OAuthClientDetailsModel> getClientDetails();
    OAuthClientDetailsModel getClientDetailsByClientId(String clientId);
    OAuthClientDetailsModel insertClientDetails(OAuthClientDetailsModel model);
    boolean updateClientDetailsByClientId(OAuthClientDetailsModel model);
    boolean deleteClientDetailsByClientId(String clientId);
}
