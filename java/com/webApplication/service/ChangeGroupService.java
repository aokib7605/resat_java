package com.webApplication.service;

import java.util.ArrayList;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import com.webApplication.entity.DataEntity;
import com.webApplication.entity.Env;
import com.webApplication.functions.SQL;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChangeGroupService {
	private final HttpSession session;
//	private final MainRepository mr;
	private final SQL sql;

	public void setPageInfo(Model model, Integer offset, String page) {
		model.addAttribute("title2", Env.groupListView);
		setGroupList(model, offset, page);

	}

	public Integer setGroupList(Model model, Integer offset, String page) {
		if(offset == null) {
			offset = 0;
		}
		if(page == null) {
			page = "";
		}
		if(page.equals("next")) {
			offset++;
		} else if(page.equals("prev")){
			offset--;
		}
		setViewData(model, offset);
		model.addAttribute("groupList", getGroupList(model, offset, page));
		return offset;
	}

	public Integer setSearchGroupList(Model model, String mode, String keyword, Integer offset, String page) {
		if(offset == null) {
			offset = 0;
		}
		if(page == null) {
			page = "";
		}
		if(page.equals("searchNext")) {
			offset++;
		} else if(page.equals("searchPrev")){
			offset--;
		}
		setSearchData(model, mode, keyword, offset);
		model.addAttribute("searchGroupList", searchGroup(model, mode, keyword, offset));
		return offset;
	}

	public String loginGroup(Model model, String loginMode, String sysGroupId, String groupPass) {
		if(loginMode == null) {
			loginMode = "start";
		}
		switch (loginMode) {
		case "start": {
			setBaseLoginGroupData(model, sysGroupId);
			model.addAttribute("mode", "login");
			model.addAttribute("loginMode", "start");
			break;
		}
		case "inputPass": {
			DataEntity groupData = sql.getGroupData("sys_group_id", sysGroupId);
			if(groupData.getGroup_pass().equals(groupPass)) {
				DataEntity userData = sql.updateUserDefGroup("sys_group_id", sysGroupId, true);
				model.addAttribute("userData", userData);
				session.setAttribute("userSession", userData);
				model.addAttribute("message", "団体に参加しました");
			} else {
				setBaseLoginGroupData(model, sysGroupId);
				model.addAttribute("mode", "login");
				model.addAttribute("loginMode", "start");
				model.addAttribute("loginMessage", "パスワードが間違っています");
			}
			break;
		}
		default:
			break;
		}
		return loginMode;
	}

	public String selectGroup(Model model, String sysGroupId) {
		DataEntity userData = sql.updateUserDefGroup("sys_group_id", sysGroupId, false);
		session.setAttribute("userSession", userData);
		model.addAttribute("userData", userData);
		return "changeGroup";
	}

	private ArrayList<DataEntity> getGroupList(Model model, Integer offset, String page){
		if(sql.getGroupDataList("sys_user_id", null, null, null).size() <= 0) {
			model.addAttribute("listMessage", "現在、団体に参加していません");
			return null;
		}

		if(sql.getGroupDataList("sys_user_id", null, null, null).size() - (offset + 1) * 10 > 0) {
			model.addAttribute("next", "次へ");
		}
		if(offset > 0) {
			model.addAttribute("prev", "前へ");
		}
		return sql.getGroupDataList("sys_user_id", null, 10, offset);
	}

	private ArrayList<DataEntity> searchGroup(Model model, String mode, String keyword, Integer offset) {
		String column = "";
		if(mode.equals("idSearch")) {
			column = "group_id";
		} else {
			column = "group_name";
		}

		if(sql.getGroupDataList(column, keyword, null, null).size() <= 0) {
			model.addAttribute("message", Env.enterGroupNotExistMessage);
			return null;
		}

		if(sql.getGroupDataList(column, keyword, null, null).size() - (offset + 1) * 5 > 0) {
			model.addAttribute("searchNext", "次へ");
		}
		if(offset > 0) {
			model.addAttribute("searchPrev", "前へ");
		}
		return sql.getGroupDataList(column, keyword, 5, offset);
	}

	private void setSearchData(Model model, String mode, String keyword, Integer offset) {
		model.addAttribute("mode", mode);
		model.addAttribute("keyword", keyword);
		model.addAttribute("searchOffset", offset);
	}

	private void setViewData(Model model, Integer offset) {
		model.addAttribute("listOffset", offset);
	}
	
	private void setBaseLoginGroupData(Model model, String sysGroupId) {
		model.addAttribute("groupData", sql.getGroupData("sys_group_id", sysGroupId));
	}
}
