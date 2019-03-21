package com.sybd.znld.service.znld;

import com.sybd.znld.onenet.dto.OneNetExecuteParams;
import com.sybd.znld.onenet.dto.OneNetKey;
import com.sybd.znld.service.znld.model.ExecuteCommandModel;

public interface IExecuteCommandService {
    ExecuteCommandModel getExecuteCommandById(Integer id);
    ExecuteCommandModel getExecuteCommandByValue(String value);
    OneNetKey getOneNetKeyByCommand(String cmd, boolean evict);
    OneNetExecuteParams getParamsByCommand(String cmd, boolean evict);
}
