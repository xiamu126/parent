package com.sybd.znld.service.znld;

import com.sybd.znld.model.znld.ExecuteCommandModel;
import com.sybd.znld.onenet.dto.OneNetExecuteParams;
import com.sybd.znld.onenet.dto.OneNetKey;

public interface IExecuteCommandService {
    ExecuteCommandModel getExecuteCommandById(Integer id);
    ExecuteCommandModel getExecuteCommandByValue(String value);
    OneNetKey getOneNetKeyByCommand(String cmd);
    OneNetExecuteParams getParamsByCommand(String cmd);
}
