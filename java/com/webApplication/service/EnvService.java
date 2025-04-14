package com.webApplication.service;

import java.util.ArrayList;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import com.webApplication.entity.DataEntity;
import com.webApplication.entity.MenuEntity;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EnvService {
	private final HttpSession session;
	
	public void setMenuList(Model model, String mode) {
		DataEntity userData = (DataEntity)session.getAttribute("userSession");
		DataEntity stageData = (DataEntity)session.getAttribute("defStSession");
		
		if(mode.equals("manager")) {
			model.addAttribute("menuList_ctegory_stage", "公演メニュー");
			model.addAttribute("menuList_ctegory_group", "団体メニュー");
			model.addAttribute("menuList_ctegory_user", "個人メニュー");
			
			ArrayList<MenuEntity> menuList_stage = new ArrayList<MenuEntity>();
			menuList_stage.add(getMenuObject("公演基本情報", "setStage"));
			menuList_stage.add(getMenuObject("座席設定", "setSeat"));
			menuList_stage.add(getMenuObject("予約フォーム作成", "setForm"));
			menuList_stage.add(getMenuObject("公演メンバー", "setStageMember"));
			model.addAttribute("menuList_stage", menuList_stage);
			model.addAttribute("menuList_stage_title", stageData.getStage_name());
			
			ArrayList<MenuEntity> menuList_manage = new ArrayList<MenuEntity>();
			menuList_manage.add(getMenuObject("予約一覧", "checkReserveList"));
			menuList_manage.add(getMenuObject("予約枚数集計", "countReserve"));
			menuList_manage.add(getMenuObject("予約フォーム一覧", "checkFormList"));
			model.addAttribute("menuList_manage", menuList_manage);
			model.addAttribute("menuList_manage_title", "票券管理");
			
			ArrayList<MenuEntity> menuList_tools = new ArrayList<MenuEntity>();
			menuList_tools.add(getMenuObject("メール配信", "sendMail"));
			menuList_tools.add(getMenuObject("Excel出力", "exportExcel"));
			model.addAttribute("menuList_tools", menuList_tools);
			model.addAttribute("menuList_tools_title", "便利機能");
			
			ArrayList<MenuEntity> menuList_group = new ArrayList<MenuEntity>();
			menuList_group.add(getMenuObject("公演の新規作成", "createStage"));
			menuList_group.add(getMenuObject("公演一覧", "checkStageList"));
			menuList_group.add(getMenuObject("団体基本情報", "setGroup"));
			menuList_group.add(getMenuObject("団体メンバー", "setGroupGroupMember"));
			model.addAttribute("menuList_group", menuList_group);
			model.addAttribute("menuList_group_title", userData.getGroup_name());
			
			ArrayList<MenuEntity> menuList_user = new ArrayList<MenuEntity>();
			menuList_user.add(getMenuObject("公演の切替・参加", "changeStage"));
			menuList_user.add(getMenuObject("団体の切替・参加", "changeGroup"));
			menuList_user.add(getMenuObject("ユーザー基本情報", "setUser"));
			model.addAttribute("menuList_user", menuList_user);
			model.addAttribute("menuList_user_title", userData.getUser_name());
		}
	}
	
	private MenuEntity getMenuObject(String pageName, String pageUrl) {
		MenuEntity menu = new MenuEntity();
		menu.setPage_name(pageName);
		menu.setPage_url(pageUrl);
		return menu;
	}

}
