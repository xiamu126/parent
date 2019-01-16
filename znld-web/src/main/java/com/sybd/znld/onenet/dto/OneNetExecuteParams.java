package com.sybd.znld.onenet.dto;

import com.sybd.znld.onenet.dto.OneNetKey;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class OneNetExecuteParams implements Serializable {
    private OneNetKey oneNetKey;
    private int timeout;
}
