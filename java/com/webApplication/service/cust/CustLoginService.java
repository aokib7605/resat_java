package com.webApplication.service.cust;

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
public class CustLoginService {
	private final MainRepository mr;
	private final HttpSession session;
	private final SQL sql;
	
	public void setPageInfo(Model model) {
		model.addAttribute("title2", Env.custLoginView);
	}
	
	public String checkLoginData(Model model, String pageName, String userId, String userPass) {
		DataEntity userData = sql.getUserData("user_id", userId);
		if(userData == null) {
			pageName = "login";
			model.addAttribute("message", Env.userAccountNotExist);
			return pageName;
		}
		if(!userData.getUser_pass().equals(userPass)) {
			pageName = "login";
			model.addAttribute("message", Env.passwordIsFalse);
			return pageName;
		}
		if(userData.getSys_user_mode().equals("sysUser")) {
			pageName = "login";
			model.addAttribute("message", Env.sysUserLoginToCustPage);
			return pageName;
		}
		pageName = "myPage";
		session.setAttribute("custSession", userData);
		updateLastLogin(userData.getSys_user_id(), userData.getSys_group_id());
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
