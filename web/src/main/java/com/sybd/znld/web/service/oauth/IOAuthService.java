package com.sybd.znld.web.service.oauth;

import com.sybd.znld.model.oauth.OAuthClientDetailsModel;

import java.util.List;

public interface IOAuthService {
    List<OAuthClientDetailsModel> getClientDetails();
    OAuthClientDetailsModel getClientDetailsByClientId(String clientId);
    OAuthClientDetailsModel insertClientDetails(OAuthClientDetailsModel model);
    boolean updateClientDetailsByClientId(OAuthClientDetailsModel model);
    boolean deleteClientDetailsByClientId(String clientId);
}
