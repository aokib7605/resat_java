package com.webApplication.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import com.webApplication.entity.DataEntity;
import com.webApplication.entity.Env;
import com.webApplication.functions.Pub;
import com.webApplication.repository.MainRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CreateUserService {
	private final MainRepository mr;
	private final HttpSession session;
	private final JavaMailSenderService mailSender;

	public void setPageInfo(Model model) {
		model.addAttribute("title2", Env.createManageUserView);
	}

	public String start(Model model) {
		model.addAttribute("mode", "checkMail");
		return "createUser";
	}

	public boolean checkMail(Model model, String userMail) {
		List<String> columns = mr.getUsersTableColumns();
		String where = " where user_mail = '" + userMail + "'";
		if(mr.getData("users", columns, where) == null) {
			
			// メールアドレスをBase64に変換
			String mailBase64 = Base64.getUrlEncoder().encodeToString(userMail.getBytes());
			
			model.addAttribute("message", Env.sendMailMessageForCreateuser);
			model.addAttribute("mode", "confiMail");
			
			// メール送信に必要な情報の準備
			String content = Pub.createUserMailMessage(mailBase64);
			String title = Env.custApplicationTitle + "アカウント新規登録手続き";
			
			System.out.println(Env.domainName + "/createUser?mode=confiData&data=" + mailBase64);
			mailSender.sendMail(userMail, title, content);
			return true;
		} else {
			model.addAttribute("message", Env.mailAddressIsUsed);
			model.addAttribute("mode", "start");
			return false;
		}
	}

	public String confiMail(Model model, String mode) {
		byte[] decodedBytes = Base64.getUrlDecoder().decode(mode);
		String userMail = new String(decodedBytes);
		mode = "inputStart";
		model.addAttribute("userMail", userMail);
		model.addAttribute("mode", "inputUserId");
		return mode;
	}

	public String inputUserId(Model model, String mode, String userMail, String userId) {
		model.addAttribute("userMail", userMail);
		model.addAttribute("userId", userId);
		List<String> columns = mr.getUsersTableColumns();
		String where = " where user_id = '" + userId + "'";
		if(mr.getData("users", columns, where) == null) {
			model.addAttribute("mode", "inputBaseData");
			return "inputBaseData";
		} else {
			model.addAttribute("message", Env.userIdisAlreadyExist);
			model.addAttribute("mode", "inputUserId");
			return "inputUserId";
		}
	}

	public String inputBaseData(Model model, String mode, String back, String userMail, String userId, String userName, String userKanaName, String userStageName, String userStageKanaName, String userPass, String rePass) {
		setBaseDataModel(model, userMail, userId, userName, userKanaName, userStageName, userStageKanaName, userPass, rePass);
		if(back != null) {
			if(back.equals("back")) {
				mode = "inputUserId";
				model.addAttribute("mode", "inputUserId");
				return mode;
			}
		}
		if(userPass.equals(rePass)) {
			mode = "inputContactData";
			model.addAttribute("mode", "inputContactData");
		} else {
			mode = "inputBaseData";
			model.addAttribute("message", Env.passwordIsUnMatchMessage);
			model.addAttribute("mode", "inputBaseData");
		}
		return mode;
	}

	public String inputContactData(Model model, String mode, String back, String userMail, String userId, String userName, String userKanaName, String userStageName, String userStageKanaName, String userPass, String userTel, String userBirthday, String hideBirthYear, String userMode) {
		setBaseDataModel(model, userMail, userId, userName, userKanaName, userStageName, userStageKanaName, userPass, userPass);
		setContactData(model, userTel, userBirthday, hideBirthYear, userMode);
		if(back != null) {
			if(back.equals("back")) {
				mode = "inputBaseData";
				model.addAttribute("mode", "inputBaseData");
				return mode;
			}
		}
		mode = "confiResult";
		model.addAttribute("mode", "confiResult");
		return mode;
	}

	public String confiResult(Model model, String mode, String back, String userMail, String userId, String userName, String userKanaName, String userStageName, String userStageKanaName, String userPass, String userTel, String userBirthday, String hideBirthYear, String userMode) {
		if(back != null) {
			if(back.equals("back")) {
				setBaseDataModel(model, userMail, userId, userName, userKanaName, userStageName, userStageKanaName, userPass, userPass);
				setContactData(model, userTel, userBirthday, hideBirthYear, userMode);
				mode = "inputContactData";
				model.addAttribute("mode", "inputContactData");
				return "createUser";
			}
		}
		createUser(model, setInputDataObject(userMail, userId, userName, userKanaName, userStageName, userStageKanaName, userPass, userTel, userBirthday, hideBirthYear, userMode));
		return "index";
	}

	private void setBaseDataModel(Model model, String userMail, String userId, String userName, String userKanaName, String userStageName, String userStageKanaName, String userPass, String rePass) {
		model.addAttribute("userMail", userMail);
		model.addAttribute("userId", userId);
		model.addAttribute("userName", userName);
		model.addAttribute("userKanaName", userKanaName);
		model.addAttribute("userStageName", userStageName);
		model.addAttribute("userStageKanaName", userStageKanaName);
		model.addAttribute("userPass", userPass);
		model.addAttribute("rePass", rePass);
	}

	private void setContactData(Model model, String userTel, String userBirthday, String hideBirthYear, String userMode) {
		model.addAttribute("userTel", userTel);
		model.addAttribute("userBirthday", userBirthday);
		model.addAttribute("hideBirthYear", hideBirthYear);
		model.addAttribute("userMode", userMode);
	}

	private DataEntity setInputDataObject(String userMail, String userId, String userName, String userKanaName, String userStageName, String userStageKanaName, String userPass, String userTel, String userBirthday, String hideBirthYear, String userMode) {
		DataEntity inputData = new DataEntity();
		if(userMode != null && !(userMode.equals(""))) {
			inputData.setSys_user_mode("doubleUser");
		} else {
			inputData.setSys_user_mode("sysUser");
		}
		inputData.setUser_mail(userMail);
		inputData.setUser_id(userId);
		inputData.setUser_name(userName);
		inputData.setUser_kana_name(userKanaName);
		inputData.setUser_stage_name(userStageName);
		inputData.setUser_stage_kana_name(userStageKanaName);
		inputData.setUser_pass(userPass);
		inputData.setUser_tell(userTel);
		inputData.setUser_birthday(Pub.convertStringToSqlDate(userBirthday));
		inputData.setUser_hide_age(Pub.convertCheckboxToInteger(hideBirthYear));
		return inputData;
	}

	private void createUser(Model model, DataEntity data) {
		List<String> columns = mr.getUsersTableColumns();
		List<String> values = new ArrayList<String>(Arrays.asList(
				Pub.createUuid(),				//sys_user_id
				data.getSys_user_mode(),		//sys_user_mode
				0 + "",							//sys_user_ev
				data.getUser_id(),				//user_id
				data.getUser_mail(),			//user_mail
				data.getUser_tell(),			//user_tell
				data.getUser_name(),			//user_name
				data.getUser_kana_name(),		//user_kana_name
				data.getUser_stage_name(),		//user_stage_name
				data.getUser_stage_kana_name(),	//user_stage_kana_name
				data.getUser_pass(),			//user_pass
				null,							//user_def_stage
				Pub.getCurrentDate() + "",		//user_cre_date
				Pub.getCurrentDate() + "",		//user_last_login
				data.getUser_birthday() + "",	//user_birthday
				data.getUser_hide_age() + "",	//user_hide_age
				data.getUser_def_group(),		//user_def_group
				null							// deleteFlg
				));
		mr.insertData("users", columns, values);

		String where = "left outer join groupes on sys_group_id = user_def_group where user_id =  '" + data.getUser_id() + "'";
		columns = Stream.concat(mr.getUsersTableColumns().stream(), mr.getGroupeLoginListTableColumns().stream()).collect(Collectors.toList());
		columns = Stream.concat(columns.stream(), mr.getGroupesTableColumns().stream()).collect(Collectors.toList());
		if(mr.getData("users u", columns, where) != null) {
			session.setAttribute("userSession", mr.getData("users u", columns, where));
		} else {
			model.addAttribute("message", Env.registAccountErrorMessage);
		}
	}
}
