package com.anw.managemymoney.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BankTransactionUtil {
	
	private static SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yy");
	
	/**
	 * Accepts date string in dd/MM/yy format
	 * @param dateString
	 * @return date object
	 */
	public static Date getDateString(String dateString) {
		try {
			return formatter.parse(dateString);
		} catch (ParseException e) {
			log.error("BankTransactionUtil:getDateString - {} ", e);
		}
		return null;
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
			log.error("BankTransactionUtil:getDoubleValue - {}", e);
		}
		return val;
	}
	
}
