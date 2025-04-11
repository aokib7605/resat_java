package com.webApplication.service;

import java.util.List;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import com.webApplication.entity.DataEntity;
import com.webApplication.repository.MainRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LoginService {
	private final MainRepository mr;
	private final HttpSession session;
	
	public void setPageInfo(Model model) {
		model.addAttribute("title2", "システムログイン");
	}
	
	public String checkLoginData(Model model, String pageName, String userId, String userPass) {
		List<String> columns = mr.getUsersTableColumns();
		String where = "where user_id = '" + userId + "'";
		DataEntity userData = mr.getData("users", columns, where);
		if(userData == null) {
			pageName = "login";
			model.addAttribute("message", "そのユーザーIDは存在しません");
			return pageName;
		}
		if(!userData.getUser_pass().equals(userPass)) {
			pageName = "login";
			model.addAttribute("message", "パスワードが間違っています");
			return pageName;
		}
		pageName = "index";
		session.setAttribute("userSession", userData);
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
}
