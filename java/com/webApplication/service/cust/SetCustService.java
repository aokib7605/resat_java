package com.webApplication.service.cust;

import java.util.Base64;

import jakarta.servlet.http.HttpSession;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import com.webApplication.entity.DataEntity;
import com.webApplication.entity.Env;
import com.webApplication.functions.SQL;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SetCustService {
	private final HttpSession session;
	private final JavaMailSender mailSender;
	//	private final MainRepository mr;
	private final SQL sql;

	public void setPageInfo(Model model) {
		model.addAttribute("title2", Env.setCustView);
	}

	public void setUserId(Model model, String mode, String sysUserId, String userId) {
		switch (mode) {
		case "setUserId": {

			// 画面をユーザーID更新モードに切り替えます
			model.addAttribute("mode", "setUserId");
			break;
		}
		case "inputValue": {
			if(sql.reGetCustData("user_id", userId) != null) {

				// DB.users => 入力したuserIdのユーザーデータが既に存在する場合、更新エラー
				model.addAttribute("mode", "setUserId");
				model.addAttribute("message", Env.userIdisAlreadyExist);
			} else {

				// DB.users => ユーザーデータ更新処理
				DataEntity custData = sql.updateCustData("user_id", userId, null);

				// セッションに更新後のユーザーデータを格納
				session.setAttribute("custSession", custData);

				// 画面に更新後の値を渡します
				model.addAttribute("custData", custData);
				model.addAttribute("message", Env.updateUserIdMessage);
			}
			break;
		}
		default:
			break;
		}
	}

	public void setUserMail(Model model, String mode, String sysUserId, String userMail) {
		switch (mode) {
		case "setMail": {
			model.addAttribute("mode", "setMail");
			break;
		}
		case "inputValue": {
			if(sql.reGetCustData("user_mail", userMail) != null) {
				model.addAttribute("mode", "setMail");
				model.addAttribute("message", "そのメールアドレスは既に使用されています");
			} else {
				DataEntity custData = (DataEntity)session.getAttribute("custSession");
				String mailBase64 = Base64.getUrlEncoder().encodeToString(userMail.getBytes());
				SimpleMailMessage message = new SimpleMailMessage();
				message.setTo(userMail);
				message.setFrom("nukikugi@gmail.com");
				message.setSubject("りざっとメールアドレス変更手続き");
				message.setText(
						"こんにちは！ \n"
								+ "りざっと運営・開発団体の劇団抜きにくい釘です。\n"
								+ "まだメールアドレスの変更手続きは完了しておりません。下記URLから手続きを完了してください。\n"
								+ "http://localhost:8080/resat/userMailUpdate?sysDiResu=" + custData.getSys_user_id() + "&liam=" + mailBase64
						);
				System.out.println("http://localhost:8080/resat/userMailUpdate?sysDiResu=" + custData.getSys_user_id() + "&liam=" + mailBase64);
				model.addAttribute("message", "入力されたアドレス宛てに送られたメールを確認してください");
				// メール送信を実施する。
				mailSender.send(message);
			}
			break;
		}
		case "confiMail": {
			DataEntity custData = sql.updateCustData("user_mail", userMail, null);
			model.addAttribute("message", "メールアドレスを変更しました");
			session.setAttribute("custSession", custData);
			model.addAttribute("custData", custData);
			model.addAttribute("mode", "setMail");
			break;
		}
		default:
			break;
		}
	}

	public void setUserPass(Model model, String mode, String sysUserId, String userPass, String rePass) {
		switch (mode) {
		case "setUserPass": {
			model.addAttribute("mode", "setUserPass");
			break;
		}
		case "inputValue": {
			if(!userPass.equals(rePass)) {
				model.addAttribute("mode", "setUserPass");
				model.addAttribute("message", "パスワードが一致していません");
			} else {
				DataEntity custData = sql.updateCustData("user_pass", userPass, null);
				model.addAttribute("message", "パスワードを変更しました");
				session.setAttribute("custSession", custData);
				model.addAttribute("custData", custData);
			}
			break;
		}
		default:
			break;
		}
	}

	public void setUserTel(Model model, String mode, String sysUserId, String userTel) {
		switch (mode) {
		case "setUserTel": {
			model.addAttribute("mode", "setUserTel");
			break;
		}
		case "inputValue": {
			DataEntity custData = sql.updateCustData("user_tell", userTel, null);
			model.addAttribute("message", "電話番号を変更しました");
			session.setAttribute("custSession", custData);
			model.addAttribute("custData", custData);
			break;
		}
		case "crearValue": {
			DataEntity custData = sql.updateCustData("user_tell", "null", null);
			model.addAttribute("message", "電話番号を削除しました");
			session.setAttribute("custSession", custData);
			model.addAttribute("custData", custData);
			break;
		}
		default:
			break;
		}
	}

	public void setUserName(Model model, String mode, String sysUserId, String userName, String userKanaName) {
		switch (mode) {
		case "setUserName": {
			model.addAttribute("mode", "setUserName");
			break;
		}
		case "inputValue": {
			DataEntity custData = sql.updateCustData("user_name", userName, null);
			custData = sql.updateCustData("user_kana_name", userKanaName, null);
			model.addAttribute("message", "ユーザーネームを変更しました");
			session.setAttribute("custSession", custData);
			model.addAttribute("custData", custData);
			break;
		}
		default:
			break;
		}
	}

	public void setUserBirthday(Model model, String mode, String sysUserId, String userBirthday, String hideBirthYear) {
		switch (mode) {
		case "setUserBirthday": {
			model.addAttribute("mode", "setUserBirthday");
			break;
		}
		case "inputValue": {
			if(hideBirthYear == null || hideBirthYear.equals("")) {
				hideBirthYear = 0 + "";
			} else {
				hideBirthYear = 1 + "";
			}
			DataEntity custData = sql.updateCustData("user_birthday", userBirthday, null);
			custData = sql.updateCustData("user_hide_age", hideBirthYear, null);
			model.addAttribute("message", "生年月日を変更しました");
			session.setAttribute("custSession", custData);
			model.addAttribute("custData", custData);
			break;
		}
		default:
			break;
		}
	}

	// アカウント削除ボタン押下時の処理
	public void deleteUser(Model model) {
		// セッションからログイン中のユーザーデータを取得
		DataEntity custData = (DataEntity)session.getAttribute("custSession");
		
		// ユーザーアカウント削除
		sql.updateCustData("user_mail", "none", custData.getSys_user_id());
		sql.updateCustData("user_id", "none", custData.getSys_user_id());
		sql.updateCustData("deleteFlg", true, custData.getSys_user_id());
		
		model.addAttribute("message", Env.deleteUserAccount);
	}
}
