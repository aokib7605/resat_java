package com.webApplication.controller;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.webApplication.service.CreateGroupService;
import com.webApplication.service.CreateStageService;
import com.webApplication.service.CreateUserService;
import com.webApplication.service.EnvService;
import com.webApplication.service.IndexService;
import com.webApplication.service.LoginService;

import lombok.RequiredArgsConstructor;


@Controller
@RequiredArgsConstructor
public class MainController {

	// ===================================================================
	private final HttpSession session;
	private final EnvService es;
	private String pageName = "";	//遷移先のページ名

	private String checkSession(Model model, String pageName) {
		if(pageName.equals("setFirstStage") || pageName.equals("setStage")) {
			return pageName;
		}
		if(checkDefStSession(model, pageName).equals("setFirstStage")) {
			pageName = "setFirstStage";
		}
		if(checkUserSession(model, pageName).equals("login")) {
			pageName = "login";
		}
		return pageName;
	}

	private String checkUserSession(Model model, String pageName) {
		if(session.getAttribute("userSession") == null) {
			return goLoginPage(model);
		} else {
			return pageName;
		}
	}

	private String checkDefStSession(Model model, String pageName) {
		if(session.getAttribute("defStSession") == null) {
			return goSetFirstStage(model);
		} else {
			return pageName;
		}
	}

	private void setEnvData(Model model, String mode) {
		model.addAttribute("title", "予約管理システム りざっと");
		setMenuList(model, mode);
	}

	private void setMenuList(Model model, String mode) {
		//mode には「manager」「customer」のいずれかを格納
		es.setMenuList(model, mode);
	}

	// ===================================================================
	// 各メニューへの遷移メソッド
	private final LoginService ls;
	private final IndexService is;
	private final CreateUserService cus;
	private final CreateGroupService cgs;
	private final CreateStageService css;

	private String goAnyPage(Model model, String pageName) {
		switch (pageName) {
		case "login": {
			return pageName;
		}
		case "createUser": {
			return pageName;
		}
		default:
			pageName = checkSession(model, pageName);
		}
		return pageName;
	}

	@GetMapping("/login")
	public String goLoginPage(Model model) {
		setEnvData(model, "login");
		ls.setPageInfo(model);
		return goAnyPage(model, "login");
	}

	@PostMapping("/login")
	public String accessLogin(Model model, String mode) {
		if(mode.equals("logout")) {
			session.invalidate();
			model.addAttribute("message", "ログアウトしました");
		}
		return goLoginPage(model);
	}

	@GetMapping("/index")
	public String goRootPage(Model model) {
		if(session.getAttribute("defStSession") == null) {
			setEnvData(model, "setFirstStage");
		} else {
			setEnvData(model, "manager");
		}
		is.setPageInfo(model);
		return goAnyPage(model, "index");
	}

	@PostMapping("/index")
	public String accessRoot(Model model, String mode, String userId, String userPass) {
		if(mode.equals("login")) {
			pageName = ls.checkLoginData(model, mode, userId, userPass);
			if(pageName.equals("login")) {
				return goLoginPage(model);
			} else {
				return goRootPage(model);
			}
		}
		return pageName;
	}

	@GetMapping("/createUser")
	public String goCreateUser(Model model, String mode, String data) {
		setEnvData(model, "createUser");
		cus.setPageInfo(model);
		if(mode.equals("start")) {
			cus.start(model);
		} else if(mode.equals("confiData")){
			cus.confiMail(model, data);
		}
		return goAnyPage(model, "createUser");
	}

	@PostMapping("/createUser")
	public String accessCreateUser(Model model, String back, String mode, String userMail, String userId, String userName, String userKanaName, String userStageName, String userStageKanaName, String userPass, String rePass, String userTel, String userBirthday, String hideBirthYear, String userMode) {
		String pageName = "createUser";
		switch (mode) {
		case "checkMail": {
			if(cus.checkMail(model, userMail) == false) {
				mode = "start";
			}
			break;
		}
		case "confiMail": {
			cus.confiMail(model, mode);
			break;
		}
		case "inputUserId": {
			mode = cus.inputUserId(model, mode, userMail, userId);
			break;
		}
		case "inputBaseData": {
			mode = cus.inputBaseData(model, mode, back, userMail, userId, userName, userKanaName, userStageName, userStageKanaName, userPass, rePass);
			break;
		}
		case "inputContactData": {
			mode = cus.inputContactData(model, mode, back, userMail, userId, userName, userKanaName, userStageName, userStageKanaName, userPass, userTel, userBirthday, hideBirthYear, userMode);
			break;
		}
		case "confiResult": {
			pageName = cus.confiResult(model, mode, back, userMail, userId, userName, userKanaName, userStageName, userStageKanaName, userPass, userTel, userBirthday, hideBirthYear, userMode);
			break;
		}
		default:
			System.out.println("新規ユーザー作成で例外フローが発生しました");
		}
		if(pageName.equals("createUser")) {
			return goCreateUser(model, mode, null);
		} else {
			return goRootPage(model);
		}
	}

	@GetMapping("/createStage")
	public String goCreateStage(Model model, String mode) {
		setEnvData(model, "manager");
		css.setPageInfo(model);
		switch (mode) {
		case "inputSysGroupId": {
			model.addAttribute("mode", "inputSysGroupId");
		}
		default:
			break;
		}
		return goAnyPage(model, "createStage");
	}
	
	@PostMapping("/createStage")
	public String accessCreateStage(
			Model model, String mode, String back, String sysGroupId, String stageId, String stagePass, String rePass, String stageName, 
			Integer stageAttractCustomers, String stageUrlTitle, String stagePlaceName, String stagePlaceAddress, String keyword, 
			@RequestParam(required = false) MultipartFile file1, @RequestParam(required = false) MultipartFile file2) {
		pageName = "createStage";
		switch (mode) {
		case "inputSysGroupId": {
			mode = css.inputSysGroupId(model, sysGroupId);
			break;
		}
		case "inputStageId": {
			mode = css.inputStageId(model, back, sysGroupId, stageId);
			break;
		}
		case "inputBaseData": {
			mode = css.inputBaseData(model, back, sysGroupId, stageId, stagePass, rePass, stageName, stageAttractCustomers, stageUrlTitle);
			break;
		}
		case "inputPlaceData": {
			mode = css.inputPlaceData(model, back, sysGroupId, stageId, stagePass, rePass, stageName, stageAttractCustomers, stageUrlTitle, stagePlaceName, stagePlaceAddress, keyword);
			break;
		}
		case "uploadImages": {
			mode = css.uploadImages(model, back, sysGroupId, stageId, stagePass, rePass, stageName, stageAttractCustomers, stageUrlTitle, stagePlaceName, stagePlaceAddress, keyword, file1, file2);
			break;
		}
		case "confiResult": {
			pageName = css.confiResult(model, back, sysGroupId, stageId, stagePass, rePass, stageName, stageAttractCustomers, stageUrlTitle, stagePlaceName, stagePlaceAddress, keyword, file1, file2);
			if(pageName.equals("uploadImages")) {
				pageName = "createStage";
			}
			break;
		}
		default:
			break;
		}
		if(pageName.equals("createStage")) {
			return goCreateStage(model, mode);
		} else {
			return goRootPage(model);
		}
	}

	@GetMapping("/setStage")
	public String goSetStage(Model model) {
		setEnvData(model, "manager");
		is.setPageInfo(model);

		return goAnyPage(model, "setStage");
	}

	@GetMapping("setFirstStage")
	public String goSetFirstStage(Model model) {
		setEnvData(model, "setFirstStage");
		is.setPageInfo(model);
		return goAnyPage(model, "setFirstStage");
	}

	@PostMapping("setFirstStage")
	public String accessSetFirstStage(Model model, String mode) {
		switch (mode) {
		case "changeStage": {
			System.out.println(mode + "が選択されましたA");
			break;
		}
		case "changeGroup": {
			System.out.println(mode + "が選択されましたB");
			break;
		}
		case "setUser": {
			System.out.println(mode + "が選択されましたC");
			break;
		}
		case "createGroup": {
			System.out.println(mode + "が選択されましたD");
			break;
		}
		default:
			break;
		}
		return goSetFirstStage(model);
	}

	@GetMapping("/createGroup")
	public String goCreateGroup(Model model, String mode) {
		setEnvData(model, "manager");
		cgs.setPageInfo(model);
		switch (mode) {
		case "inputGroupId": {
			model.addAttribute("mode", "inputGroupId");
			break;
		}
		default:
			break;
		}
		return goAnyPage(model, "createGroup");
	}

	@PostMapping("/createGroup")
	public String accessCreateGroup(Model model, String mode, String back, String groupId, String groupName, String groupKanaName, String groupPass, String rePass, String groupMail) {
		pageName = "createGroup";
		switch (mode) {
		case "inputGroupId": {
			mode = cgs.inputGroupId(model, groupId);
			break;
		}
		case "inputGroupData": {
			mode = cgs.inputGroupData(model, back, groupId, groupName, groupKanaName, groupPass, rePass, groupMail);
			break;
		}
		case "confiResult": {
			pageName = cgs.confiResult(model, back, groupId, groupName, groupKanaName, groupPass, rePass, groupMail);
			if(pageName.equals("inputGroupData")) {
				pageName = "createGroup";
			}
			break;
		}
		default:
			break;
		}
		if(pageName.equals("createGroup")) {
			return goCreateGroup(model, mode);
		} else {
			return goRootPage(model);
		}
	}
}
