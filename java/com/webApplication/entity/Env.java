package com.webApplication.entity;

public class Env {
	//アプリケーション名
	public static String applicationTitle = "予約管理システム Globe";
	public static String custApplicationTitle = "Globe";
	public static String customerServiceName = custApplicationTitle + "運営事務局";
	
	//ドメイン名
	public static String domainName = "http://localhost:8080";
//	public static String domainName = "https://grobe-reserve.com";
	public static String sendFromMail = "globe.reserveticket@gmail.com";
	
	// バージョン
	public static String applicationVer = "1.0.0";
	
	/*
	 * 各ページのメニュータイトル
	 */
	
	/*
	 * 各ページのメニュータイトル
	 * 顧客画面
	 */
	public static String myPageMenu = "ホーム";
	public static String myReserveListMenu = "予約公演一覧";
	public static String setCustMenu = "アカウント設定";
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
	public static String setUserView = "ユーザー基本情報";
	
	
	/**
	 *  顧客画面
	 */
	public static String inputFormView = "予約登録画面";
	public static String custLoginView = "アカウントログイン画面";
	public static String myPageView = "マイページ";
	public static String myReserveListView = "予約公演一覧";
	public static String setCustView = "アカウント設定";
	
	/**
	 *  各種メッセージ一覧
	 */
	// エラーメッセージ
	public static String userIdisAlreadyExist = "そのIDは既に使用されています";
	public static String userAccountNotExist = "そのユーザーIDは存在しません";
	public static String userMailNotExist = "そのメールアドレスは存在しません";
	public static String mailAddressIsUsed = "そのメールアドレスは既に使用されています";
	public static String passwordIsFalse = "パスワードが間違っています";
	public static String passwordIsUnMatchMessage = "パスワードが一致していません";
	public static String stageNotExist = "検索条件に一致する公演は見つかりませんでした";
	public static String inputTraAmountIsMissedMessage = "予約枚数が不正です";
	public static String registAccountErrorMessage = "アカウント登録時にエラーが発生しました";
	
	// 処理完了メッセージ
	public static String updateUserIdMessage = "ユーザーIDを変更しました";
	public static String enterStageMessage = "公演に参加しました";
	public static String sysUserLoginToCustPage = "管理者専用アカウントです。";
	public static String reserveAccountLoginMessage = "ログインして予約を完了してください";
	public static String compReserveMessage = "予約登録が完了しました";
	public static String changeTransactionCompMessage = "予約内容を変更しました";
	public static String deleteTransactionCompMessage = "予約を削除しました";
	public static String removeUserFromGroupMessage = "該当ユーザーが団体から退会しました";
	public static String enterStageNotExistMessage = "現在、公演に参加していません";
	public static String enterGroupNotExistMessage = "検索条件に一致する団体は見つかりませんでした";
	public static String confiDeleteMessage = "本当に削除してもよろしいですか？";
	public static String reserveCancelMessage = "予約をキャンセルしました";
	public static String deleteUserAccount = "アカウントを削除しました";
	public static String logoutCompMessage = "ログアウトしました";
	
	public static String sendMailMessageForCreateuser =
			" にアカウント作成手続きのメールを送信しました。"
			+ "<p>メールが届かない場合、<br>迷惑メールの設定にて " + sendFromMail + " からの受信設定を確認してください。</p>";
	
	/**
	 * メール内文言
	 */
	public static String createUserAccountTitle = "アカウント新規登録手続き";
	public static String compReserveMailTitle = "予約完了のお知らせ";
	public static String notCompMessage = "アカウント作成手続きはまだ完了しておりません。\n下記URLから以降の登録処理を進めてください。\n";
}
