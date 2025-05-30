package com.webApplication.entity;

public class Env {
	//アプリケーション名
	public static String applicationTitle = "予約管理システム りざっと";
	public static String custApplicationTitle = "りざっと";
	
	//ドメイン名
	public static String domainName = "http://localhost:8080";
	
	/*
	 * 各ページのメニュータイトル
	 */
	
	/*
	 * 各ページのメニュータイトル
	 * 顧客画面
	 */
	public static String myPageMenu = "ホーム";
	public static String myReserveListMenu = "予約公演一覧";
	public static String changeModeMenu = "管理者画面切替";
	
	/**
	 *  各ページのタイトル
	 */
	
	/**
	 *  管理者画面
	 */
	public static String systemLoginView = "システムログイン画面";
	public static String reserveListView = "予約一覧";
	public static String groupListView = "団体一覧・参加";
	
	
	/**
	 *  顧客画面
	 */
	public static String inputFormView = "予約登録画面";
	public static String custLoginView = "アカウントログイン画面";
	public static String myPageView = "マイページ";
	public static String myReserveListView = "予約公演一覧";
	
	/**
	 *  各種メッセージ一覧
	 */
	public static String userAccountNotExist = "そのユーザーIDは存在しません";
	public static String passwordIsFalse = "パスワードが間違っています";
	public static String sysUserLoginToCustPage = 
			"管理者アカウントでログインしようとしています。<br>"
			+ "このアカウントを顧客として併用利用する場合、管理者画面のユーザー設定からモードを変更してください。";
	public static String reserveAccountLoginMessage = "アカウントにログインして予約を完了してください";
	public static String compReserveMessage = "予約登録が完了しました";
	public static String logoutCompMessage = "ログアウトしました";
}
