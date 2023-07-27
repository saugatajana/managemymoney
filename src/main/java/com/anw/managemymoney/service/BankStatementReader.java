package com.anw.managemymoney.service;

import java.io.IOException;
import java.util.List;

import com.anw.managemymoney.model.BankStatementSummary;
import com.anw.managemymoney.model.BankTransaction;

public interface BankStatementReader {
	
	public List<BankTransaction> getAllTransactions(String fileName) throws IOException;
	
	public BankStatementSummary getBankStatementSummary(List<BankTransaction> bankTrans);
}
