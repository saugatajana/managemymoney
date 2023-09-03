package com.anw.managemymoney.controller;

import org.apache.pdfbox.pdmodel.encryption.InvalidPasswordException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.anw.managemymoney.model.APIHelper;
import com.anw.managemymoney.model.BankStatementSummary;
import com.anw.managemymoney.model.BankTransaction;
import com.anw.managemymoney.service.impl.HDFCBankStatementReaderService;
import com.anw.managemymoney.service.impl.HDFCCreditCardStatementReaderService;
import lombok.extern.slf4j.Slf4j;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

@Slf4j
@Controller
public class BankStatementController {

	@Autowired
	private HDFCBankStatementReaderService hdfcBankStatementReaderService;

	@Autowired
	private HDFCCreditCardStatementReaderService hdfcCreditCardStatementReaderService;

	@GetMapping("/upload")
	public String showUploadPage() {
		return "upload";
	}

	@GetMapping("/upload1")
	public String showUploadPage1() {
		return "upload1";
	}

	@PostMapping("/upload")
	public String handleFileUpload(@RequestParam("file") MultipartFile file, Model model) throws IOException {
		if (!file.isEmpty() && file.getOriginalFilename().endsWith(".xls")) {
			// Process the uploaded file and generate the statement summary
			List<BankTransaction> statementData = hdfcBankStatementReaderService.getAllTransactions(file, null);
			BankStatementSummary summary = hdfcBankStatementReaderService.getBankStatementSummary(statementData);

			// Add the summary to the model to display on the web page
			model.addAttribute("statementSummary", summary);
			// Extract month/year keys from categoryTotalMap and add them to the model
			Set<String> monthYearKeys = extractMonthYearKeys(summary);
			model.addAttribute("monthYearKeys", monthYearKeys);
		}

		return "upload";
	}

	@PostMapping("/upload1")
	public String handleFileUpload1(@RequestParam("file") MultipartFile file, Model model, @RequestParam("password") String password) throws IOException {
		try {
			if (!file.isEmpty() && (file.getOriginalFilename().endsWith(".pdf") ||  file.getOriginalFilename().endsWith(".PDF"))) {
				List<BankTransaction> statementData = hdfcCreditCardStatementReaderService.getAllTransactions(file, APIHelper.builder().documentPwd(password).build());
				BankStatementSummary summary = hdfcCreditCardStatementReaderService.getBankStatementSummary(statementData);
				// Add the summary to the model to display on the web page
				model.addAttribute("statementSummary", summary);
				// Extract month/year keys from categoryTotalMap and add them to the model
				Set<String> monthYearKeys = extractMonthYearKeys(summary);
				model.addAttribute("monthYearKeys", monthYearKeys);
			}	
		} catch (InvalidPasswordException e) {
			log.error("Invalid password provided.");
			model.addAttribute("message", "Incorrect password. Please try again.");
		} catch (IOException e) {
			model.addAttribute("message", "Error processing the PDF file.");
		} 
		return "upload1";
	}

	private Set<String> extractMonthYearKeys(BankStatementSummary statementSummary) {
		Set<String> monthYearKeys = new TreeSet<>();
		for (Map<String, BigDecimal> innerMap : statementSummary.getCategoryTotalMap().values()) {
			monthYearKeys.addAll(innerMap.keySet());
		}
		return monthYearKeys;
	}
}
