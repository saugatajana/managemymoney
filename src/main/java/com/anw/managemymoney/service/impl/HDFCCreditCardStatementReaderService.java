package com.anw.managemymoney.service.impl;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;
import com.anw.managemymoney.enums.CategoryEnum;
import com.anw.managemymoney.model.APIHelper;
import com.anw.managemymoney.model.BankStatementSummary;
import com.anw.managemymoney.model.BankTransaction;
import com.anw.managemymoney.repository.impl.PropFileKeywordsRepository;
import com.anw.managemymoney.service.BankStatementReaderService;
import com.anw.managemymoney.util.BankTransactionUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class HDFCCreditCardStatementReaderService implements BankStatementReaderService {

	private static final String SPACE = "---------";

	@Autowired
	private PropFileKeywordsRepository keywordsRepository;

	@Override
	public List<BankTransaction> getAllTransactions(MultipartFile file, APIHelper apiHelper) throws IOException {
		List<BankTransaction> bankTrans = new ArrayList<>();
		byte[] pdfBytes = file.getBytes();
		PDDocument document = PDDocument.load(pdfBytes, apiHelper.getDocumentPwd());
		PDFTextStripper pdfTextStripper = new PDFTextStripper();
		String text = pdfTextStripper.getText(document);
		String regex = "\\d{2}/\\d{2}/\\d{4} \\d{2}:\\d{2}:\\d{2}.*";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(text);
		while (matcher.find()) {
			String transactionLine = matcher.group();
			if(!transactionLine.endsWith("Cr")) {
				//Extract Narration
				String narration = transactionLine.substring(transactionLine.indexOf(" ", 11) + 1);
				CategoryEnum categoryEnum = getCategoryFromNarration(narration);
				//Extract Amount
				String[] words = transactionLine.split(" ");
				String amountString = words[words.length - 1].replace(",", "");;
				//Extract Date
				String dateString = words[0];
				BankTransaction transaction = BankTransaction.builder()
						.category(categoryEnum)
						.date(BankTransactionUtil.getDateStringV1(dateString))
						.valueDate(BankTransactionUtil.getDateStringV1(dateString))
						.narration(narration)
						.withdrawalAmount(BankTransactionUtil.getDoubleValue(amountString))
						.build();
				bankTrans.add(transaction);
				log.info("Transaction {}", transaction);
			}
		}
		return bankTrans;
	}

	@Override
	public BankStatementSummary getBankStatementSummary(List<BankTransaction> bankTrans) {

		Map<String, Map<String, BigDecimal>> categoryTotalMap = new HashMap<>();
		Map<String, List<String>> transactionsMap = new HashMap<>();
		BigDecimal totalWithdrawl = new BigDecimal("0");

		for(BankTransaction transaction : bankTrans) { 
			BigDecimal withdrawlAmt = BigDecimal.valueOf(transaction.getWithdrawalAmount());
			totalWithdrawl = totalWithdrawl.add(withdrawlAmt);			
			Map<String, BigDecimal> categoryMonthlyMap = null;
			String category = transaction.getCategory().getDisplayName();
			if(withdrawlAmt.compareTo(BigDecimal.ZERO) > 0) {
				populateTransactionMap(categoryTotalMap, categoryMonthlyMap, transactionsMap, category, transaction, withdrawlAmt);
			}
		}

		BankStatementSummary statementSummary = BankStatementSummary.builder()
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
