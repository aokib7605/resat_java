package com.webApplication.service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import com.webApplication.entity.DataEntity;
import com.webApplication.functions.Pub;
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
		List<String> columns = Stream.concat(mr.getUsersTableColumns().stream(), mr.getGroupe_login_listTableColumns().stream()).collect(Collectors.toList());
		columns = Stream.concat(columns.stream(), mr.getGroupesTableColumns().stream()).collect(Collectors.toList());
		String where = "left outer join group_login_list gll on u.sys_user_id = gll.sys_user_id left outer join groupes g on gll.sys_group_id = g.sys_group_id where user_id =  '" + userId + "'";
		DataEntity userData = mr.getData("users u", columns, where);
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
