package com.mapmory.services.call.domain;

import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CallRequest {
    private String fromUser;
    private String toUser;
    private long timestamp;
}