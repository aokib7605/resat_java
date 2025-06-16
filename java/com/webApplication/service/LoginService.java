package com.webApplication.service;

import java.util.List;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import com.webApplication.entity.DataEntity;
import com.webApplication.entity.Env;
import com.webApplication.functions.Pub;
import com.webApplication.functions.SQL;
import com.webApplication.repository.MainRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LoginService {
	private final MainRepository mr;
	private final HttpSession session;
	private final SQL sql;
	
	public void setPageInfo(Model model) {
		model.addAttribute("title2", Env.systemLoginView);
	}
	
	public String checkLoginData(Model model, String pageName, String loginMethod, String userMail, String userId, String userPass) {
		// ユーザー情報
		DataEntity userData = null;
		
		if(loginMethod.equals("id")) {
			// user_id ベースでユーザー検索
			userData = sql.getUserData("user_id", userId);
		} else if(loginMethod.equals("mail")) {
			// user_mail ベースでユーザー検索
			userData = sql.getUserData("user_mail", userMail);
		}
		
		if(userData == null) {
			pageName = "login";
			if(loginMethod.equals("id")) {
				// 入力されたuser_idが存在しない場合
				model.addAttribute("message", Env.userAccountNotExist);
			} else if(loginMethod.equals("mail")) {
				// 入力されたuser_mailが存在しない場合
				model.addAttribute("message", Env.userMailNotExist);
			}
			return pageName;
		}
		
		// 入力されたuser_passがDB情報と一致しない場合
		if(!userData.getUser_pass().equals(userPass)) {
			pageName = "login";
			model.addAttribute("message", Env.passwordIsFalse);
			return pageName;
		}
		pageName = "index";
		session.setAttribute("userSession", userData);
		updateLastLogin(userData.getSys_user_id(), userData.getSys_group_id());
		setDefaultStage(userData);
		return pageName;
	}
	
	public void setDefaultStage(DataEntity userData) {
		List<String> columns = mr.getStagesTableColumns();
		String where = "where sys_stage_id = '" + userData.getUser_def_stage() + "'";
		DataEntity stageData = mr.getData("stages", columns, where);
		if(stageData != null) {
			session.setAttribute("defStSession", stageData);
		}
	}
	
	private void updateLastLogin(String sysUserId, String sysGroupId) {
		String where = "where sys_user_id = '" + sysUserId + "'";
		mr.updateData("users", "user_last_login", Pub.getCurrentDate() + "", where);
		if(sysGroupId != null) {
			where = "where sys_group_id = '" + sysGroupId + "'";
			mr.updateData("groupes", "group_last_login", Pub.getCurrentDate() + "", where);
		}
	}
}
