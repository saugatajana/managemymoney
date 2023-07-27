package com.anw.managemymoney.model;

import java.util.Date;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Builder
@Getter
@Setter
public class BankTransaction {
	
	private Date date;
    private String narration;
    private String chequeOrRefNo;
    private Date valueDate;
    private double withdrawalAmount;
    private double depositAmount;
    private double closingBalance;

}
