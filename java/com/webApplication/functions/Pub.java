package com.webApplication.functions;

import java.security.SecureRandom;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class Pub {
    private static final String CHARACTERS = "abcdefghijklmnopqrstuvwxyz0123456789";
    private static final int DEFAULT_LENGTH = 20; // uuidの長さ
    private static final SecureRandom RANDOM = new SecureRandom();

	public static Date convertStringToSqlDate(String dateString) {
		return Date.valueOf(dateString);
	}

	public static Integer convertCheckboxToInteger(String checkbox) {
		Integer num = null;
		if(checkbox != null) {
			num = 1;
		} else {
			num = 0;
		}
		return num;
	}
	
	public static String createUuid() {
        StringBuilder sb = new StringBuilder(DEFAULT_LENGTH);
        for (int i = 0; i < DEFAULT_LENGTH; i++) {
            int index = RANDOM.nextInt(CHARACTERS.length());
            sb.append(CHARACTERS.charAt(index));
        }
        return sb.toString();
    }
	
    public static Date getCurrentDate() {
        return Date.valueOf(LocalDate.now());
    }
    
    public static String convertListToStr(List<String> list) {
        return String.join(", ", list);
    }
    
    public static String convertListToQuotedStr(List<String> list) {
        return list.stream()
                .map(s -> {
                    if (s == null || s.trim().equalsIgnoreCase("null")) {
                        return "null"; // クォートなしの null
                    } else {
                        return "'" + s + "'";
                    }
                })
                .collect(Collectors.joining(", "));
    }
}
