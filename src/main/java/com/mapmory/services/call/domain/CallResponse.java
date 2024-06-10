package com.mapmory.services.call.domain;

import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CallResponse {
    private String userId;
    private boolean isOnline;
}