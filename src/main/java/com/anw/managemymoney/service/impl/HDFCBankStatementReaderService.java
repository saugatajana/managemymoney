package com.anw.managemymoney.service.impl;

import com.anw.managemymoney.enums.CategoryEnum;
import com.anw.managemymoney.model.BankStatementSummary;
import com.anw.managemymoney.model.BankTransaction;
import com.anw.managemymoney.repository.impl.PropFileKeywordsRepository;
import com.anw.managemymoney.service.BankStatementReaderService;
import com.anw.managemymoney.util.BankTransactionUtil;
import com.anw.managemymoney.util.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
public class HDFCBankStatementReaderService implements BankStatementReaderService {
	
	private static final String SPACE = "---------";
	
	@Autowired
	private PropFileKeywordsRepository keywordsRepository;
	
	@Override
	public List<BankTransaction> getAllTransactions(MultipartFile file) throws IOException {
		List<List<String>> rowLists = FileReader.readExcelFile(file);
		List<BankTransaction> bankTrans = new ArrayList<>();
		boolean inBetweenStars = false;
		for(List<String> colmsList : rowLists) {
			if(colmsList.size() == 1 && Objects.nonNull(colmsList.get(0)) && colmsList.get(0).matches("\\*+")) {
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
				if(Objects.nonNull(transaction.getDate())) {
					transaction.setCategory(null);
					transaction.setCategory(getCategoryFromNarration(transaction.getNarration()));
					bankTrans.add(transaction);
				}
			}
		}
		return bankTrans;
	}

	@Override
	public BankStatementSummary getBankStatementSummary(List<BankTransaction> bankTrans) {
		Map<String, Map<String, BigDecimal>> categoryTotalMap = new HashMap<>();
		Map<String, List<String>> transactionsMap = new HashMap<>();
		BigDecimal totalWithdrawl = new BigDecimal("0");
		double totalDeposit = 0;
		
		for(BankTransaction transaction : bankTrans) { 
			BigDecimal withdrawlAmt = BigDecimal.valueOf(transaction.getWithdrawalAmount());
			BigDecimal depositAmt = BigDecimal.valueOf(transaction.getDepositAmount());
			totalWithdrawl = totalWithdrawl.add(withdrawlAmt);			
			totalDeposit += transaction.getDepositAmount();
			Map<String, BigDecimal> categoryMonthlyMap = null;
			String category = transaction.getCategory().getDisplayName();
			if(withdrawlAmt.compareTo(BigDecimal.ZERO) > 0) {
				populateTransactionMap(categoryTotalMap, categoryMonthlyMap, transactionsMap, category, transaction, withdrawlAmt);
			} else if(category.equals(CategoryEnum.DIVIDEND.getDisplayName()) && depositAmt.compareTo(BigDecimal.ZERO) > 0){
				/** Income **/
				populateTransactionMap(categoryTotalMap, categoryMonthlyMap, transactionsMap, category, transaction, depositAmt);
			}
		}
		
		BankStatementSummary statementSummary = BankStatementSummary.builder()
				.totalDepositAmount(BigDecimal.valueOf(totalDeposit).setScale(2, RoundingMode.HALF_UP))
				.totalWithdrawalAmount(totalWithdrawl.setScale(2, RoundingMode.HALF_UP))
				.categoryTotalMap(categoryTotalMap)
				.transactionsMap(transactionsMap)
				.build();
		
		return statementSummary;
	}
	
	private void populateTransactionMap(Map<String, Map<String, BigDecimal>> categoryTotalMap, 
			Map<String, BigDecimal> categoryMonthlyMap, Map<String, List<String>> transactionsMap,
			String category,BankTransaction transaction, BigDecimal amt) {
		if(!categoryTotalMap.containsKey(category)) {
			categoryMonthlyMap = new HashMap<>();
			categoryTotalMap.put(category, categoryMonthlyMap);
		} else
			categoryMonthlyMap = categoryTotalMap.get(category);
		String monthYear = BankTransactionUtil.getMonthYear(transaction.getValueDate());
		if(!categoryMonthlyMap.containsKey(monthYear))
			categoryMonthlyMap.put(monthYear, BigDecimal.ZERO);
		categoryMonthlyMap.put(monthYear, amt.add(categoryMonthlyMap.get(monthYear)));
		List<String> transList = transactionsMap.get(category);
		if(CollectionUtils.isEmpty(transList)) {
			transList = new ArrayList<>();
		}
		String tranNarration = transaction.getNarration() + SPACE
				+ transaction.getValueDate() + SPACE 
				+ amt;
		transList.add(tranNarration);
		transactionsMap.put(category, transList);
	}
	

	@Override
	public CategoryEnum getCategoryFromNarration(String narration) {
		Map<String, String> keywordsMap = keywordsRepository.getKeywordsMap("");
		String lowercaseNarration = narration.toLowerCase();
		for (Map.Entry<String, String> entry : keywordsMap.entrySet()) {
            String category = entry.getKey();
            String keywords = entry.getValue();

            // Split the keywords for the category
            String[] keywordArray = keywords.split(",");

            // Check if any of the keywords match with the transaction
            for (String keyword : keywordArray) {
                if (lowercaseNarration.contains(keyword.trim().toLowerCase())) {
                    return CategoryEnum.valueOf(category.toUpperCase());
                }
            }
        }
		return CategoryEnum.OTHERS;
	}

}
