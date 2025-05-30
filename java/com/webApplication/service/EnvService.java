package com.webApplication.service;

import java.util.ArrayList;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import com.webApplication.entity.DataEntity;
import com.webApplication.entity.Env;
import com.webApplication.entity.MenuEntity;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EnvService {
	private final HttpSession session;
	
	public void setMenuList(Model model, String mode) {
		DataEntity userData = null;
		DataEntity stageData = null;
		DataEntity custData = null;
		
		model.addAttribute("userData", userData);
		model.addAttribute("stageData", stageData);
		
		switch (mode) {
		case "manager": {
			userData = (DataEntity)session.getAttribute("userSession");
			stageData = (DataEntity)session.getAttribute("defStSession");
			
			model.addAttribute("userData", userData);
			model.addAttribute("stageData", stageData);
			break;
		}
		case "setFirstStage": {
			userData = (DataEntity)session.getAttribute("userSession");
			stageData = (DataEntity)session.getAttribute("defStSession");
			
			model.addAttribute("userData", userData);
			model.addAttribute("stageData", stageData);
			break;
		}
		case "customer": {
			custData = (DataEntity)session.getAttribute("custSession");
			model.addAttribute("custData", custData);
		}
		default:
			break;
		}

		switch (mode) {
			case "manager":
				if(stageData != null) {
					model.addAttribute("menuList_ctegory_stage", "公演メニュー");
					
					ArrayList<MenuEntity> menuList_stage = new ArrayList<>();
					menuList_stage.add(getMenuObject("公演基本情報", "/setStage"));
					menuList_stage.add(getMenuObject("予約フォーム作成", "/setForm"));
					menuList_stage.add(getMenuObject("座席設定", "/setSeat"));
					menuList_stage.add(getMenuObject("宣伝情報", "/setAdvertisement"));
					menuList_stage.add(getMenuObject("公演メンバー", "/setStageMember?offset=&page="));
					model.addAttribute("menuList_stage", menuList_stage);
					model.addAttribute("menuList_stage_title", stageData.getStage_name());

					ArrayList<MenuEntity> menuList_manage = new ArrayList<>();
					menuList_manage.add(getMenuObject("予約一覧", "/checkReserveList"));
					menuList_manage.add(getMenuObject("予約枚数集計", "/countReserve"));
					menuList_manage.add(getMenuObject("予約フォーム一覧", "/checkFormList"));
					model.addAttribute("menuList_manage", menuList_manage);
					model.addAttribute("menuList_manage_title", "票券管理");

					ArrayList<MenuEntity> menuList_tools = new ArrayList<>();
					menuList_tools.add(getMenuObject("メール配信", "/sendMail"));
					menuList_tools.add(getMenuObject("Excel出力", "/exportExcel"));
					model.addAttribute("menuList_tools", menuList_tools);
					model.addAttribute("menuList_tools_title", "便利機能");
				}

				if(userData.getUser_def_group() != null) {
					model.addAttribute("menuList_ctegory_group", "団体メニュー");
					
					ArrayList<MenuEntity> menuList_group = new ArrayList<>();
					menuList_group.add(getMenuObject("公演の新規作成", "/createStage?mode=inputSysGroupId"));
					menuList_group.add(getMenuObject("団体公演一覧", "/checkStageList?offset=&page="));
					menuList_group.add(getMenuObject("団体基本情報", "/setGroup"));
					menuList_group.add(getMenuObject("団体メンバー", "/setGroupMember?offset=&page="));
					model.addAttribute("menuList_group", menuList_group);
					model.addAttribute("menuList_group_title", userData.getGroup_name());
				}

				model.addAttribute("menuList_ctegory_user", "個人メニュー");
				
				ArrayList<MenuEntity> menuList_user = new ArrayList<>();
				menuList_user.add(getMenuObject("公演一覧・参加", "/changeStage?offset=&page="));
				menuList_user.add(getMenuObject("団体一覧・参加", "/changeGroup?offset=&page="));
				menuList_user.add(getMenuObject("ユーザー基本情報", "/setUser"));
				menuList_user.add(getMenuObject("団体の新規作成", "/createGroup?mode=inputGroupId"));
				model.addAttribute("menuList_user", menuList_user);
				model.addAttribute("menuList_user_title", userData.getUser_name());
				break;

			case "setFirstStage":
				if (userData.getSys_group_id() != null) {
					model.addAttribute("menuList_ctegory_group", "団体メニュー");
					ArrayList<MenuEntity> menuList_group2 = new ArrayList<>();
					menuList_group2.add(getMenuObject("公演の新規作成", "createStage?mode=inputSysGroupId"));
					menuList_group2.add(getMenuObject("団体公演一覧", "checkStageList?offset=&page="));
					model.addAttribute("menuList_group", menuList_group2);
					model.addAttribute("menuList_group_title", userData.getGroup_name());
				}

				model.addAttribute("menuList_ctegory_user", "個人メニュー");
				ArrayList<MenuEntity> menuList_user2 = new ArrayList<>();
				menuList_user2.add(getMenuObject("公演一覧・参加", "/changeStage?offset=&page="));
				menuList_user2.add(getMenuObject("団体一覧・参加", "/changeGroup?offset=&page="));
				menuList_user2.add(getMenuObject("ユーザー基本情報", "/setUser"));
				menuList_user2.add(getMenuObject("団体の新規作成", "/createGroup?mode=inputGroupId"));
				model.addAttribute("menuList_user", menuList_user2);
				model.addAttribute("menuList_user_title", userData.getUser_name());
				break;

			case "customer": {
				ArrayList<MenuEntity> custMenuList = new ArrayList<MenuEntity>();
				custMenuList.add(getMenuObject(Env.myPageMenu, "/myPage"));
				custMenuList.add(getMenuObject(Env.myReserveListMenu, "/myPage/reserveList"));
				if(custData.getSys_user_mode().equals("doubleUser")) {
					custMenuList.add(getMenuObject(Env.changeModeMenu, "/changeMode"));
				}
				model.addAttribute("custMenuList", custMenuList);
				break;
			}
			default:
				break;
		}
	}
	
	private MenuEntity getMenuObject(String pageName, String pageUrl) {
		MenuEntity menu = new MenuEntity();
		menu.setPage_name(pageName);
		menu.setPage_url(pageUrl);
		return menu;
	}

}
