package com.webApplication.entity;

public class Env {
	//アプリケーション名
	public static String applicationTitle = "予約管理システム りざっと";
	public static String custApplicationTitle = "りざっと";
	
	//ドメイン名
	public static String domainName = "http://localhost:8080";
	public static String sendFromMail = "nukikugi@gmail.com";
	
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
	public static String systemLoginView = "管理者ログイン画面";
	public static String createManageUserView = "新規ユーザー作成";
	public static String enterStageAndListView = "公演一覧・参加";
	public static String reserveListView = "予約一覧";
	public static String countReserveView = "予約枚数集計";
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
	// エラーメッセージ
	public static String userAccountNotExist = "そのユーザーIDは存在しません";
	public static String userMailNotExist = "そのメールアドレスは存在しません";
	public static String mailAddressIsUsed = "そのメールアドレスは既に使用されています";
	public static String passwordIsFalse = "パスワードが間違っています";
	public static String stageNotExist = "検索条件に一致する公演は見つかりませんでした";
	public static String enterStageMessage = "公演に参加しました";
	public static String sysUserLoginToCustPage = 
			"管理者アカウントでログインしようとしています。<br>"
			+ "このアカウントを顧客として併用利用する場合、管理者画面のユーザー設定からモードを変更してください。";
	public static String reserveAccountLoginMessage = "アカウントにログインして予約を完了してください";
	public static String compReserveMessage = "予約登録が完了しました";
	public static String changeTransactionCompMessage = "予約内容を変更しました";
	public static String deleteTransactionCompMessage = "予約を削除しました";
	public static String enterStageNotExistMessage = "現在、公演に参加していません";
	public static String enterGroupNotExistMessage = "検索条件に一致する団体は見つかりませんでした";
	public static String confiDeleteMessage = "本当に削除してもよろしいですか？";
	public static String logoutCompMessage = "ログアウトしました";
}
