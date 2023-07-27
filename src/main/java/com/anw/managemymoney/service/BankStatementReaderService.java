package com.anw.managemymoney.service;

import java.io.IOException;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;
import com.anw.managemymoney.model.BankStatementSummary;
import com.anw.managemymoney.model.BankTransaction;

public interface BankStatementReaderService {
	
	public List<BankTransaction> getAllTransactions(MultipartFile file) throws IOException;
	
	public BankStatementSummary getBankStatementSummary(List<BankTransaction> bankTrans);
}
