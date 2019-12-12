package com.sybd.znld.model.lamp;

import com.sybd.znld.model.lamp.dto.Message;

public interface IStrategyMessage {
    Message<Message.Bundle> toMessage();
}
