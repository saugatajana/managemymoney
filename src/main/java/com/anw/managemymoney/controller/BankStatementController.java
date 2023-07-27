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
import java.util.List;

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
        }

        return "upload";
    }
}
