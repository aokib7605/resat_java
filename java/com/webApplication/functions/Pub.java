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

import com.webApplication.entity.DataEntity;
import com.webApplication.entity.Env;

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
			System.out.println(e);
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
				String strArr = intArr[i] + "";
				strArr = strArr.replace("[", "").replace("]", "");
				intList.add(Integer.parseInt(strArr));
			}
			return intList;
		} catch (Exception e) {
			// TODO: handle exception
			return null;
		}
	}

	public static ArrayList<Integer> createIntArrayList(String[] strArr){
		try {
			ArrayList<Integer> intList = new ArrayList<Integer>();
			for(int i = 0; i < strArr.length; i++) {
				strArr[i] = strArr[i].replace("[", "").replace("]", "");
				intList.add(Integer.parseInt(strArr[i]));
			}
			return intList;
		} catch (Exception e) {
			// TODO: handle exception
			return null;
		}
	}

	public static ArrayList<Integer> nullIfAllZeroOrNull(ArrayList<Integer> inputList) {
		if (inputList == null || inputList.isEmpty()) {
			return null;
		}

		for (Integer value : inputList) {
			if(value < 0) {
				// 1つでも0未満が選択されていた場合、nullを返す
				return null;
			}

			if (value != null && !(value <= 0)) {
				return inputList; // 一つでも 0/以外/null 以外があればそのまま返す
			}
		}

		return null; // 全て 0以下 または null の場合
	}

	public static String createCompReserveMailTitle(String groupName) {
		return "【" + groupName + "】" + Env.compReserveMailTitle;
	}

	public static String createCompReserveMailMessage(String groupName, String groupMail, String custName, String stageDate,
			Integer traAmount, DataEntity stageData, boolean noLogin, String ticketName, Integer ticketPrice) {

		String content = ""
				+ custName + " 様\n\n"
				+ "この度は " + stageData.getStage_name() + " にご予約いただき、誠にありがとうございます。 \n"
				+ "以下の内容でご予約を承りました。\n\n"
				+ "【公演名】" + stageData.getStage_name() + "\r\n"
				+ "【日時】" + stageDate + "開演（開場は開演の" + stageData.getStage_open_minutes() + "分前）\r\n"
				+ "【会場】" + stageData.getStage_place_name() + "（住所：" + stageData.getStage_place_address() + "） \r\n"
				//				+ "【予約番号】ABC12345  \r\n"
				+ "【チケット】" + ticketName + "（" + ticketPrice + "円）\n"
				+ "【ご予約枚数】" + traAmount + "枚\n\n\n"
				+ "【お支払】" + ticketPrice * traAmount + "円\n"
				+ "※受付にてお名前をお伝えください。\r\n"
				+ "※開演の" + stageData.getStage_open_minutes() + "分前までにお越しいただけますようお願い申し上げます。\n\n\n";

		if(noLogin == false) {
			content += "万が一キャンセルをご希望の場合は、マイページからお手続きください：\r\n"
					+ Env.domainName + "/";
		} else {
			content += "万が一キャンセルをご希望の場合は、下記メールアドレス宛にお問合せください：\r\n"
					+ groupMail;
		}

		return content;
	}

	//	LocalDateTime型　⇒　String型に変換（フォーマット変換）
	public static String convertStrDateFromLocalDateTime(LocalDateTime localDateTime) {
		if (localDateTime == null) return "";

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy年 M月 d日 H時 m分");
		return localDateTime.format(formatter);
	}
}
