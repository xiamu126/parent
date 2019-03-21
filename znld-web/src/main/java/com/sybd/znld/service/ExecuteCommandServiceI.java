package com.sybd.znld.service;

import com.sybd.znld.onenet.dto.OneNetExecuteParams;
import com.sybd.znld.onenet.dto.OneNetKey;

public interface ExecuteCommandServiceI extends IBaseService {
    OneNetKey getOneNetKeyByCommand(String cmd);
    OneNetExecuteParams getParamsByCommand(String command, boolean evict);
}
