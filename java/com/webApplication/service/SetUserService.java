package com.webApplication.service;

import java.util.Base64;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import com.webApplication.entity.DataEntity;
import com.webApplication.entity.Env;
import com.webApplication.functions.Pub;
import com.webApplication.functions.SQL;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SetUserService {
	private final HttpSession session;
	private final JavaMailSenderService mailSender;
	//	private final MainRepository mr;
	private final SQL sql;

	public void setPageInfo(Model model) {
		model.addAttribute("title2", Env.setUserView);
	}

	public void setUserId(Model model, String mode, String sysUserId, String userId) {
		switch (mode) {
		case "setUserId": {
			model.addAttribute("mode", "setUserId");
			break;
		}
		case "inputValue": {
			if(sql.reGetUserData("user_id", userId) != null) {
				model.addAttribute("mode", "setUserId");
				model.addAttribute("message", "そのIDは既に使用されています");
			} else {
				DataEntity userData = sql.updateUserData("user_id", userId, null);
				model.addAttribute("message", "ユーザーIDを変更しました");
				session.setAttribute("userSession", userData);
				model.addAttribute("userData", userData);
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
			if(sql.reGetUserData("user_mail", userMail) != null) {
				model.addAttribute("mode", "setMail");
				model.addAttribute("message", "そのメールアドレスは既に使用されています");
			} else {
				DataEntity userData = (DataEntity)session.getAttribute("userSession");
				String mailBase64 = Base64.getUrlEncoder().encodeToString(userMail.getBytes());
				String title = "【" +Env.applicationTitle + "】メールアドレス変更手続き";
				String content = Pub.createUpdateMailMessage(userData.getSys_user_id(), mailBase64);
				mailSender.sendMail(userMail, title, content);
				System.out.println(Env.domainName + "/userMailUpdate?sysDiResu=" + userData.getSys_user_id() + "&liam=" + mailBase64);
				model.addAttribute("message", "入力されたアドレス宛てに送られたメールを確認してください");
			}
			break;
		}
		case "confiMail": {
			DataEntity userData = sql.updateUserData("user_mail", userMail, null);
			model.addAttribute("message", "メールアドレスを変更しました");
			session.setAttribute("userSession", userData);
			model.addAttribute("userData", userData);
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
				DataEntity userData = sql.updateUserData("user_pass", userPass, null);
				model.addAttribute("message", "パスワードを変更しました");
				session.setAttribute("userSession", userData);
				model.addAttribute("userData", userData);
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
			DataEntity userData = sql.updateUserData("user_tell", userTel, null);
			model.addAttribute("message", "電話番号を変更しました");
			session.setAttribute("userSession", userData);
			model.addAttribute("userData", userData);
			break;
		}
		case "crearValue": {
			DataEntity userData = sql.updateUserData("user_tell", "null", null);
			model.addAttribute("message", "電話番号を削除しました");
			session.setAttribute("userSession", userData);
			model.addAttribute("userData", userData);
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
			DataEntity userData = sql.updateUserData("user_name", userName, null);
			userData = sql.updateUserData("user_kana_name", userKanaName, null);
			model.addAttribute("message", "ユーザーネームを変更しました");
			session.setAttribute("userSession", userData);
			model.addAttribute("userData", userData);
			break;
		}
		default:
			break;
		}
	}

	public void setUserStageName(Model model, String mode, String sysUserId, String userName, String userKanaName) {
		switch (mode) {
		case "setUserStageName": {
			model.addAttribute("mode", "setUserStageName");
			break;
		}
		case "inputValue": {
			DataEntity userData = sql.updateUserData("user_stage_name", userName, null);
			userData = sql.updateUserData("user_stage_kana_name", userKanaName, null);
			model.addAttribute("message", "芸名を変更しました");
			session.setAttribute("userSession", userData);
			model.addAttribute("userData", userData);
			break;
		}
		case "crearValue": {
			DataEntity userData = sql.updateUserData("user_stage_name", "null", null);
			userData = sql.updateUserData("user_stage_kana_name", "null", null);
			model.addAttribute("message", "芸名を削除しました");
			session.setAttribute("userSession", userData);
			model.addAttribute("userData", userData);
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
			DataEntity userData = sql.updateUserData("user_birthday", userBirthday, null);
			userData = sql.updateUserData("user_hide_age", hideBirthYear, null);
			model.addAttribute("message", "生年月日を変更しました");
			session.setAttribute("userSession", userData);
			model.addAttribute("userData", userData);
			break;
		}
		default:
			break;
		}
	}

	// アカウント削除ボタン押下時の処理
	public void deleteUser(Model model) {
		// セッションからログイン中のユーザーデータを取得
		DataEntity userData = (DataEntity)session.getAttribute("userSession");

		// ユーザーアカウント削除
		sql.updateUserData("user_mail", "none", userData.getSys_user_id());
		sql.updateUserData("user_id", "none", userData.getSys_user_id());
		sql.updateUserData("deleteFlg", true, userData.getSys_user_id());

		model.addAttribute("message", Env.deleteUserAccount);
	}
}
