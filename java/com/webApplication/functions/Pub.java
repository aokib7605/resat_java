package com.webApplication.functions;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
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
    
    public static String convertByteArrayToString(byte[] bytes) {
        if (bytes == null) return null;
        return new String(bytes, StandardCharsets.UTF_8);
    }
    
    public static LocalDateTime convertStrToDateTime(String dateTimeStr) {
    	dateTimeStr = dateTimeStr.replace("T", " ");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        try {
            return LocalDateTime.parse(dateTimeStr, formatter);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("日付の形式が正しくありません: " + dateTimeStr, e);
        }
    }
    
    public static String[] splitStrToArrayStr(String input) {
        if (input == null || input.isEmpty()) {
            return new String[0];
        }
        return input.split(",");
    }
    
    public static Integer[] splitStrToArrayInteger(String input) {
        if (input == null || input.isEmpty()) {
            return new Integer[0];
        }
        String[] parts = input.split(",");
        Integer[] result = new Integer[parts.length];
        for (int i = 0; i < parts.length; i++) {
            result[i] = Integer.parseInt(parts[i].trim());
        }
        return result;
    }
    
    public static ArrayList<String> createStrArrayList(String[] strArr){
    	try {
    		ArrayList<String> strList = new ArrayList<String>();
			for(int i = 0; i < strArr.length; i++) {
				strList.add(strArr[i]);
			}
			return strList;
		} catch (Exception e) {
			// TODO: handle exception
			return null;
		}
    }
    
    public static ArrayList<String> createStrArrayList(Integer[] intArr){
    	try {
    		ArrayList<String> strList = new ArrayList<String>();
			for(int i = 0; i < intArr.length; i++) {
				strList.add(intArr[i]+"");
			}
			return strList;
		} catch (Exception e) {
			// TODO: handle exception
			return null;
		}
    }
    
    public static ArrayList<Integer> createIntArrayList(Integer[] intArr){
    	try {
    		ArrayList<Integer> intList = new ArrayList<Integer>();
			for(int i = 0; i < intArr.length; i++) {
				intList.add(intArr[i]);
			}
			System.out.println(intList);
			return intList;
		} catch (Exception e) {
			// TODO: handle exception
			return null;
		}
    }
}
