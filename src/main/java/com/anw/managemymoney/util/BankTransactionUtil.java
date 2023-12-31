package com.anw.managemymoney.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.apache.commons.lang3.StringUtils;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BankTransactionUtil {
	
	private static SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yy");
	private static SimpleDateFormat formatter1 = new SimpleDateFormat("MM-yy");
	private static SimpleDateFormat formatter2 = new SimpleDateFormat("dd/MM/yyyy");
	
	/**
	 * Accepts date string in dd/MM/yy format
	 * @param dateString
	 * @return date object
	 */
	public static Date getDateString(String dateString) {
		if(!dateString.contains("/"))
			return null;
		try {
			return formatter.parse(dateString);
		} catch (ParseException e) {
			log.error("BankTransactionUtil:getDateString - {} ", e.getMessage());
		}
		return null;
	}
	
	public static Date getDateStringV1(String dateString) {
		if(!dateString.contains("/"))
			return null;
		try {
			return formatter2.parse(dateString);
		} catch (ParseException e) {
			log.error("BankTransactionUtil:getDateStringV1 - {} ", e.getMessage());
		}
		return null;
	}
	
	public static String getMonthYear(Date date) {
		return formatter1.format(date);
	}
	
	/**
	 * Accepts double value string 
	 * @param valString
	 * @return double value
	 */
	public static double getDoubleValue(String valString) {
		double val = 0;
		try {
			if(StringUtils.isNotBlank(valString))
				val = Double.parseDouble(valString);
		} catch(Exception e) {
			log.warn("BankTransactionUtil:getDoubleValue - {}", e.getMessage());
		}
		return val;
	}
	
	public static void getCategory(String narration) {
		log.info("Narration {}", narration);
	}
	
}
