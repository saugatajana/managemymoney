package com.anw.managemymoney.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Builder
@Getter
@Setter
public class BankStatementSummary {
	
	private double totalWithdrawalAmount;
    private double totalDepositAmount;

}
