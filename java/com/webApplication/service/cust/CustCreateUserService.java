package com.webApplication.service.cust;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import jakarta.servlet.http.HttpSession;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import com.webApplication.entity.DataEntity;
import com.webApplication.entity.Env;
import com.webApplication.functions.Pub;
import com.webApplication.repository.MainRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustCreateUserService {
	private final MainRepository mr;
	private final HttpSession session;
	private final JavaMailSender mailSender;
	
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
			String mailBase64 = Base64.getUrlEncoder().encodeToString(userMail.getBytes());
			System.out.println(mailBase64);
			model.addAttribute("message", userMail + Env.sendMailMessageForCreateuser);
			model.addAttribute("mode", "confiMail");
			sendMail(userMail, mailBase64);
			return true;
		} else {
			model.addAttribute("message", Env.mailAddressIsUsed);
			model.addAttribute("mode", "start");
			return false;
		}
	}

	private void sendMail(String userMail, String mailBase64) {
		//build.gradleに下記の記述が必要
		/*
		plugins {
			    id 'org.springframework.boot' version '3.2.4'
			    id 'io.spring.dependency-management' version '1.1.4'
			    id 'java'
		}
		group = 'com.example'
		version = '0.0.1-SNAPSHOT'
		sourceCompatibility = '17'  // Javaバージョンに応じて調整

		repositories {
		    mavenCentral()
		}
		dependencies {
		    implementation 'org.springframework.boot:spring-boot-starter-mail'
		    implementation 'org.springframework.boot:spring-boot-starter'
		}
		 */
		//application.propertiesに下記の記述が必要
		/*
		spring.mail.host=smtp.gmail.com
		spring.mail.port=587
		spring.mail.username=nukikugi@gmail.com
		spring.mail.password=kizm hdxv lpyr fbqx
		spring.mail.properties.mail.smtp.auth=true
		spring.mail.properties.mail.smtp.starttls.enable=true
		 */
		SimpleMailMessage message = new SimpleMailMessage();
		message.setTo(userMail);
		message.setFrom(Env.sendFromMail);
		message.setSubject(Env.custApplicationTitle + Env.createUserAccountTitle);
		message.setText(
				Env.custApplicationTitle + Env.notCompMessage
						+ Env.domainName + "/reserve/createUser?mode=confiData&data=" + mailBase64
				);
		System.out.println(Env.domainName + "/reserve/createUser?mode=confiData&data=" + mailBase64);

		// メール送信を実施する。
		mailSender.send(message);
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
			model.addAttribute("message", "そのユーザーIDは既に使用されています");
			model.addAttribute("mode", "inputUserId");
			return "inputUserId";
		}
	}

	public String inputBaseData(Model model, String mode, String back, String userMail, String userId, String userName, String userKanaName, String userPass, String rePass) {
		setBaseDataModel(model, userMail, userId, userName, userKanaName, userPass, rePass);
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
			model.addAttribute("message", "パスワードが一致していません");
			model.addAttribute("mode", "inputBaseData");
		}
		return mode;
	}

	public String inputContactData(Model model, String mode, String back, String userMail, String userId, String userName, String userKanaName, String userPass, String userTel, String userBirthday, String hideBirthYear) {
		setBaseDataModel(model, userMail, userId, userName, userKanaName, userPass, userPass);
		setContactData(model, userTel, userBirthday, hideBirthYear);
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

	public String confiResult(Model model, String mode, String back, String userMail, String userId, String userName, String userKanaName, String userPass, String userTel, String userBirthday, String hideBirthYear) {
		if(back != null) {
			if(back.equals("back")) {
				setBaseDataModel(model, userMail, userId, userName, userKanaName, userPass, userPass);
				setContactData(model, userTel, userBirthday, hideBirthYear);
				mode = "inputContactData";
				model.addAttribute("mode", "inputContactData");
				return "createUser";
			}
		}
		createUser(model, setInputDataObject(userMail, userId, userName, userKanaName, userPass, userTel, userBirthday, hideBirthYear));
		return "myPage";
	}

	private void setBaseDataModel(Model model, String userMail, String userId, String userName, String userKanaName, String userPass, String rePass) {
		model.addAttribute("userMail", userMail);
		model.addAttribute("userId", userId);
		model.addAttribute("userName", userName);
		model.addAttribute("userKanaName", userKanaName);
		model.addAttribute("userPass", userPass);
		model.addAttribute("rePass", rePass);
	}

	private void setContactData(Model model, String userTel, String userBirthday, String hideBirthYear) {
		model.addAttribute("userTel", userTel);
		model.addAttribute("userBirthday", userBirthday);
		model.addAttribute("hideBirthYear", hideBirthYear);
	}

	private DataEntity setInputDataObject(String userMail, String userId, String userName, String userKanaName, String userPass, String userTel, String userBirthday, String hideBirthYear) {
		DataEntity inputData = new DataEntity();
		inputData.setSys_user_mode("custUser");
		inputData.setUser_mail(userMail);
		inputData.setUser_id(userId);
		inputData.setUser_name(userName);
		inputData.setUser_kana_name(userKanaName);
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
				null,							//user_stage_name
				null,							//user_stage_kana_name
				data.getUser_pass(),			//user_pass
				null,							//user_def_stage
				Pub.getCurrentDate() + "",		//user_cre_date
				Pub.getCurrentDate() + "",		//user_last_login
				data.getUser_birthday() + "",	//user_birthday
				data.getUser_hide_age() + "",	//user_hide_age
				null							//user_def_group
				));
		mr.insertData("users", columns, values);

		String where = "left outer join groupes on sys_group_id = user_def_group where user_id =  '" + data.getUser_id() + "'";
		columns = Stream.concat(mr.getUsersTableColumns().stream(), mr.getGroupeLoginListTableColumns().stream()).collect(Collectors.toList());
		columns = Stream.concat(columns.stream(), mr.getGroupesTableColumns().stream()).collect(Collectors.toList());
		if(mr.getData("users u", columns, where) != null) {
			session.setAttribute("custSession", mr.getData("users u", columns, where));
		} else {
			model.addAttribute("message", "アカウント登録時にエラーが発生しました");
		}
	}
}
