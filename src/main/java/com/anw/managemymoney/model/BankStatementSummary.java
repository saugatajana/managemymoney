package com.anw.managemymoney.model;

import java.math.BigDecimal;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Builder
@Getter
@Setter
public class BankStatementSummary {
	
	private BigDecimal totalWithdrawalAmount;
    private BigDecimal totalDepositAmount;

}
