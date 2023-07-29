package com.anw.managemymoney.model;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

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
    private Map<String, Map<String, BigDecimal>> categoryTotalMap;
    private Map<String , List<String>> transactionsMap;

}
