package com.anw.managemymoney.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.anw.managemymoney.model.BankStatementSummary;
import com.anw.managemymoney.model.BankTransaction;
import com.anw.managemymoney.service.BankStatementReaderService;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

@Controller
public class BankStatementController {
	
	@Autowired
	private BankStatementReaderService bankStatementReaderService;

    @GetMapping("/upload")
    public String showUploadPage() {
        return "upload";
    }

    @PostMapping("/upload")
    public String handleFileUpload(@RequestParam("file") MultipartFile file, Model model) throws IOException {
        if (!file.isEmpty() && file.getOriginalFilename().endsWith(".xls")) {
            // Process the uploaded file and generate the statement summary
        	List<BankTransaction> statementData = bankStatementReaderService.getAllTransactions(file);
            BankStatementSummary summary = bankStatementReaderService.getBankStatementSummary(statementData);

            // Add the summary to the model to display on the web page
            model.addAttribute("statementSummary", summary);
         // Extract month/year keys from categoryTotalMap and add them to the model
            Set<String> monthYearKeys = extractMonthYearKeys(summary);
            model.addAttribute("monthYearKeys", monthYearKeys);
        }

        return "upload";
    }
    
    private Set<String> extractMonthYearKeys(BankStatementSummary statementSummary) {
        Set<String> monthYearKeys = new TreeSet<>();
        for (Map<String, BigDecimal> innerMap : statementSummary.getCategoryTotalMap().values()) {
            monthYearKeys.addAll(innerMap.keySet());
        }
        return monthYearKeys;
    }
}
