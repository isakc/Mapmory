package com.mapmory.services.purchase.domain;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Subscription {
	private int subscriptionNo;
	private String userId;
	private int nextPaymentMethod;
	private String nextSubscriptionCardType;
	private String nextSubscriptionLastFourDigits;
	private LocalDateTime nextSubscriptionPaymentDate;
	private LocalDateTime subscriptionStartDate;
	private LocalDateTime subscriptionEndDate;
	private boolean isSubscribed;
	private String customerUid;
	private String merchantUid;
}
