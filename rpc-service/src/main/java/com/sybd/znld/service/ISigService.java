package com.sybd.znld.service;

import com.sybd.znld.model.ApiResult;

import java.util.Map;

public interface ISigService {
    ApiResult checkSig(final Map<String, String> params, String secretKey, Long now, String nonce, String sig);
}
