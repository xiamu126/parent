package com.sybd.znld.dto;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class MyMqMessage implements Serializable {
    private String msg;
}
