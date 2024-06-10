package com.mapmory.services.call.domain;

import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class SdpAnswer {
    private String fromUser;
    private String toUser;
    private String sdp;
}