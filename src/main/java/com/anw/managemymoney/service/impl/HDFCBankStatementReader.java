package com.anw.managemymoney.service.impl;

import com.anw.managemymoney.model.BankStatementSummary;
import com.anw.managemymoney.model.BankTransaction;
import com.anw.managemymoney.service.BankStatementReader;
import com.anw.managemymoney.util.BankTransactionUtil;
import com.anw.managemymoney.util.FileReader;
import lombok.extern.slf4j.Slf4j;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


@Slf4j
public class HDFCBankStatementReader implements BankStatementReader {

	@Override
	public List<BankTransaction> getAllTransactions(String fileName) throws IOException {
		List<List<String>> rowLists = FileReader.readExcelFile(fileName);
		List<BankTransaction> bankTrans = new ArrayList<>();
		boolean inBetweenStars = false;
		for(List<String> colmsList : rowLists) {
			if(colmsList.size() == 1 && Objects.nonNull(colmsList.get(0)) && !colmsList.get(0).matches("\\*+")) {
				inBetweenStars = !inBetweenStars;
			}
			if(inBetweenStars && colmsList.size() >= 7 && Objects.nonNull(colmsList.get(0)) && !colmsList.get(0).matches("\\*+")) {
				BankTransaction transaction = BankTransaction.builder().build();
				int colNo = 1;
				for(String col : colmsList) {	
					switch (colNo) {
						case 1:
							transaction.setDate(BankTransactionUtil.getDateString(col));
							break;
						case 2:
							transaction.setNarration(col);
							break;
						case 3:
							transaction.setChequeOrRefNo(col);
							break;
						case 4:
							transaction.setValueDate(BankTransactionUtil.getDateString(col));
							break;
						case 5:
							transaction.setWithdrawalAmount(BankTransactionUtil.getDoubleValue(col));
							break;
						case 6:
							transaction.setDepositAmount(BankTransactionUtil.getDoubleValue(col));
							break;
						case 7:
							transaction.setClosingBalance(BankTransactionUtil.getDoubleValue(col));
							break;
					}
					if(colNo == 7)
						colNo = 1;
					else
						colNo++;
				}
				bankTrans.add(transaction);
			}
		}
		return bankTrans;
	}

	@Override
	public BankStatementSummary getBankStatementSummary(List<BankTransaction> bankTrans) {
		
		double totalWithdrawl = 0;
		double totalDeposit = 0;
		for(BankTransaction transaction : bankTrans) { 
			totalWithdrawl += transaction.getWithdrawalAmount();			
			totalDeposit += transaction.getDepositAmount();
		}
		return BankStatementSummary.builder()
				.totalDepositAmount(totalDeposit)
				.totalWithdrawalAmount(totalWithdrawl)
				.build();
	}
	
	

}
