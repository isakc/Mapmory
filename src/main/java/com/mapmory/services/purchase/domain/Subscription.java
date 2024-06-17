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
	private int nextSubscriptionPaymentMethod;
	private String nextSubscriptionCardType;
	private String nextSubscriptionLastFourDigits;
	private LocalDateTime nextSubscriptionPaymentDate;
	private String nextSubscriptionPaymentDateString;
	private LocalDateTime subscriptionStartDate;
	private String subscriptionStartDateString;
	private LocalDateTime subscriptionEndDate;
	private String subscriptionEndDateString;
	private boolean isSubscribed;
	private String customerUid;
	private String merchantUid;
}
