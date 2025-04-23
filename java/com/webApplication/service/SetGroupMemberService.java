package com.webApplication.service;

import java.util.ArrayList;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import com.webApplication.entity.DataEntity;
import com.webApplication.functions.SQL;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SetGroupMemberService {
	private final HttpSession session;
	//	private final MainRepository mr;
	private final SQL sql;

	public void setPageInfo(Model model) {
		model.addAttribute("title2", "団体メンバー・招待");
		model.addAttribute("memberList", getMemberList(model));
		model.addAttribute("authorityList", getAuthorityList());
	}

	public Integer setSearchUserList(Model model, String mode, String keyword, Integer offset, String page) {
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
		model.addAttribute("searchUserList", searchUser(model, mode, keyword, offset));
		return offset;
	}
	
	public String setMember(Model model, String mode, String groupAuthority, String sysUserId) {
		DataEntity userData = (DataEntity)session.getAttribute("userSession");
		switch (mode) {
		case "select": {
			model.addAttribute("mode", "select");
			model.addAttribute("sysUserId", sysUserId);
			break;
		}
		case "inputValue": {
			sql.updateLoginList("group_login_list", "group_authority", groupAuthority, sysUserId);
			model.addAttribute("mode", "select");
			model.addAttribute("sysUserId", sysUserId);
			model.addAttribute("message", "権限を変更しました");
			break;
		}
		case "logout": {
			sql.deleteLoginData("group_login_list", sysUserId, userData.getUser_def_group());
			model.addAttribute("message", "該当ユーザーが団体から退会しました");
			break;
		}
		default:
			break;
		}
		return "setGroupMember";
	}

	public String inviteGroup(Model model, String sysUserId) {
		DataEntity userData = (DataEntity)session.getAttribute("userSession");
		if(sql.getGroupLoginData(sysUserId, userData.getUser_def_group()) == null) {
			sql.addGroupLoginList(userData.getUser_def_group(), sysUserId);
			model.addAttribute("message", "団体に参加しました");
		} else {
			model.addAttribute("message", "既に参加しています");
		}
		return "setGroupMember";
	}

	private ArrayList<DataEntity> searchUser(Model model, String mode, String keyword, Integer offset) {
		String column = "";
		switch (mode) {
		case "idSearch": {
			column = "u.user_id";
			break;
		}
		case "nameSearch": {
			column = "u.user_name";
			break;
		}
		case "mailSearch": {
			column = "u.user_mail";
			break;
		}
		default:
		}

		if(sql.getUserDataList(column, keyword, null, null).size() <= 0) {
			model.addAttribute("message", "検索条件に一致するユーザーは見つかりませんでした");
			return null;
		}

		if(sql.getUserDataList(column, keyword, null, null).size() - (offset + 1) * 5 > 0) {
			model.addAttribute("searchNext", "次へ");
		}
		if(offset > 0) {
			model.addAttribute("searchPrev", "前へ");
		}
		return sql.getUserDataList(column, keyword, 5, offset);
	}

	public ArrayList<DataEntity> getMemberList(Model model){
		DataEntity userData = (DataEntity)session.getAttribute("userSession");
		ArrayList<DataEntity> memberList = sql.getMemberList("group_login_list gll", userData.getUser_def_group());
		if(memberList.size() < 0) {
			model.addAttribute("listMessage", "団体メンバーが存在しません");
			return null;
		}
		return memberList;
	}
	
	public ArrayList<DataEntity> getAuthorityList(){
		return sql.getAuthorityList("group_authorities");
	}

	private void setSearchData(Model model, String mode, String keyword, Integer offset) {
		model.addAttribute("mode", mode);
		model.addAttribute("keyword", keyword);
		model.addAttribute("searchOffset", offset);
	}
}
