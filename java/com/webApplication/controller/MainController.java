package com.webApplication.controller;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.webApplication.entity.FormData;
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
		pageName = checkDefStSession(model, pageName);
		pageName = checkUserSession(model, pageName);
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
	
	@GetMapping("/login")
	public String goLoginPage(Model model) {
		pageName = "login";
		setEnvData(model, "login");
		ls.setPageInfo(model);
		return pageName;
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
		pageName = "index";
		pageName = checkSession(model, pageName);
		if(pageName.equals("index")) {
			setEnvData(model, "manager");
			is.setPageInfo(model);
		}
		return pageName;
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
//		if(mode.equals("stageLogin")) {
//			return oc.exeStageLogin(model, fd);
//		}
//		if(mode.equals("newStage")) {
//			return oc.createStage(model, fd);
//		}
		return pageName;
	}

//	@PostMapping("/")
//	public String exeLogout(Model model, @ModelAttribute  FormData fd) {
//		if (fd.getMode().equals("logout")) {
//			sc.exeLogout(model);
//		}
//		return welcom(model);
//	}
//
//	@GetMapping("/topMenu")
//	public String clickTopMenu(Model model) {
//		pageName = "option";
//		pageName = checkSession(pageName);
//		return pageName;
//	}

	@GetMapping("/forms")
	public String clickForms(Model model) {
		pageName = "option";
		pageName = checkSession(model, pageName);
		return pageName;
	}

//	@GetMapping("/forms/ownerForm")
//	public String clickOwnerForm(Model model) {
//		pageName = "option";
//		pageName = checkSession(pageName);
//		return pageName;
//	}
//
//	@PostMapping("/forms/ownerForm")
//	public String clickOwnerForm(Model model, @ModelAttribute  Customers data) {
//		if(data.getMode() == null) {
//			if(data.getSelect() != null) {
//				return fc.goReservePage(model, data);
//			}
//		}
//		if(data.getMode().equals("familyName") || data.getMode().equals("firstName") || data.getMode().equals("customerId")) {
//			return fc.getCustomerList(model, data);
//		}
//		return fc.goOwnerFormsPage(model);
//	}
//
//	@GetMapping("/forms/openForms")
//	public String clickOpenForms(Model model) {
//		pageName = "option";
//		pageName = checkSession(pageName);
//		return pageName;
//	}
//
//	@GetMapping("/reservation")
//	public String callReservationForm(Model model, @ModelAttribute FormData fd) {
//		if(fd.getMa() == null) {
//			return rc.goReservationPage(model, fd.getForm(), fd.getSt());
//		} else {
//			return rc.goReservationPage(model, fd.getForm(), fd.getSt(), fd.getMa());
//		}
//	}

//	@PostMapping("/reservation")
//	public String confiInputForm(Model model, @ModelAttribute FormData fd) {
//		if(fd.getMode().equals("inputForm")) {
//			return rc.goConfiInputForm(model, fd);
//		}
//		if(fd.getMode().equals("Yes")) {
//			return rc.goReservedPage(model, fd);
//		}
//		if(fd.getMode().equals("ownerReserve")) {
//			return rc.ownerReserved(model, fd);
//		}
//		return callReservationForm(model, fd);
//	}
//
//	@GetMapping("/myPage")
//	public String clickMyPage(Model model) {
//		if(mpc.sessionCheck(model) == true) {
//			return mpc.goMyPage(model);
//		} else {
//			return mpc.goLoginMyPage(model);
//		}
//	}
//
//	@PostMapping("/myPage")
//	public String clickMyPage(Model model, @ModelAttribute  Customers data) {
//		if(data.getMode().equals("login")) {
//			return mpc.confiCustomerLogin(model, data);
//		}
//		if(data.getMode().equals("logout")) {
//			return mpc.logoutCustomer(model);
//		}
//		if(data.getMode().equals("myPage")) {
//			mpc.invalidateFormSession(model);
//		}
//		if(mpc.sessionCheck(model) == true) {
//			return clickMyPage(model);
//		}
//		return clickMyPage(model);
//	}
//
//	@GetMapping("/newCustomer")
//	public String clickNewCustomer(Model model) {
//		pageName = "option";
//		pageName = checkSession(pageName);
//		return pageName;
//	}
//
//	@PostMapping("/newCustomer")
//	public String clickNewCustomer(Model model, @ModelAttribute  Customers data) {
//		if(data.getMode().equals("newCustomer")) {
//			return mpc.confiNewCustomerData(model, data);
//		}
//		return clickNewCustomer(model);
//	}
//
//	@GetMapping("/myPage/stageList")
//	public String clickCStageList(Model model) {
//		pageName = "option";
//		pageName = checkSession(pageName);
//		return pageName;
//	}
//
//	@PostMapping("/myPage/stageList")
//	public String clickCStageList(Model model, @ModelAttribute  Transactions data) {
//		pageName = "option";
//		pageName = checkSession(pageName);
//		return pageName;
//	}
//
//	@GetMapping("/myPage/myAccount")
//	public String clickMyCAccount(Model model) {
//		pageName = "option";
//		pageName = checkSession(pageName);
//		return pageName;
//	}
//
//	@PostMapping("/myPage/myAccount")
//	public String clickMyCAccount(Model model, @ModelAttribute  Customers data) {
//		if(data.getMode().equals("update")) {
//			return mpc.updateMyAccount(model, data);
//		}
//		return clickMyCAccount(model);
//	}
//
//	@GetMapping("/private")
//	public String clickPrivate(Model model) {
//		pageName = "option";
//		pageName = checkSession(pageName);
//		return pageName;
//	}
//
//	@GetMapping("/private/myReserveList")
//	public String clickMyReserveList(Model model) {
//		pageName = "option";
//		pageName = checkSession(pageName);
//		return pageName;
//	}
//
//	@GetMapping("/private/myReserveCount")
//	public String clickMyReserveCount(Model model) {
//		pageName = "option";
//		pageName = checkSession(pageName);
//		return pageName;
//	}
//
//	@GetMapping("/private/availability")
//	public String clickMyAvailability(Model model) {
//		pageName = "option";
//		pageName = checkSession(pageName);
//		return pageName;
//	}
//
//	@GetMapping("/allReservation")
//	public String clickAllReservation(Model model) {
//		pageName = "option";
//		pageName = checkSession(pageName);
//		return pageName;
//	}
//
//	@GetMapping("/allReservation/allReserveList")
//	public String clickAllReserveList(Model model) {
//		pageName = "option";
//		pageName = checkSession(pageName);
//		return pageName;
//	}
//
//	@PostMapping("/allReservation/allReserveList")
//	public String clickAllReserveList(Model model, @ModelAttribute Transactions data) {
//		if(data.getMode().equals("showInfo")) {
//			return arc.showTransactionInfo(model, data.getTransactionNo());
//		}
//		if(data.getMode().equals("updateTransaction")) {
//			return arc.updateTransaction(model, data);
//		}
//		if(data.getMode().equals("cancel")) {
//			return arc.cancelTransaction(model, data);
//		}
//		return clickAllReserveList(model);
//	}
//
//	@GetMapping("/allReservation/allReserveCount")
//	public String clickAllReserveCount(Model model) {
//		pageName = "option";
//		pageName = checkSession(pageName);
//		return pageName;
//	}
//
//	@GetMapping("/allReservation/availability")
//	public String clickAvailability(Model model) {
//		pageName = "option";
//		pageName = checkSession(pageName);
//		return pageName;
//	}

	@GetMapping("/setStage")
	public String goSetStage(Model model) {
		pageName = "setStage";
		pageName = checkSession(model, pageName);
		if(pageName.equals("setStage")) {
			setEnvData(model, "manager");
			is.setPageInfo(model);
		}
		return pageName;
	}

//	@GetMapping("/stageSetting/stageInfo")
//	public String clickStageInfo(Model model) {
//		pageName = "option";
//		pageName = checkSession(pageName);
//		return pageName;
//	}
//
//	@PostMapping("/stageSetting/stageInfo")
//	public String clickStageInfo(Model model, @ModelAttribute Stages input) {
//		if(!input.getMode().equals("deleteStage") && input.getMode() != null) {
//			ssc.updateStageInfo(model, input);
//		}
//		if(input.getMode().equals("deleteStage")) {
//			return ssc.deleteStage(model, input);
//		}
//		return clickStageInfo(model);
//	}
//
//	@GetMapping("/stageSetting/stageDateSetting")
//	public String clickStageDateSetting(Model model) {
//		pageName = "option";
//		pageName = checkSession(pageName);
//		return pageName;
//	}
//
//	@PostMapping("/stageSetting/stageDateSetting")
//	public String clickStageDateSetting(Model model, @ModelAttribute StageDates fd) {
//		pageName = "option";
//		pageName = checkSession(pageName);
//		return pageName;
//	}
//
//	@GetMapping("/stageSetting/ticketSetting")
//	public String clickTicketSetting(Model model) {
//		pageName = "option";
//		pageName = checkSession(pageName);
//		return pageName;
//	}
//
//	@PostMapping("/stageSetting/ticketSetting")
//	public String clickTicketSetting(Model model, @ModelAttribute Tickets t) {
//		pageName = "option";
//		pageName = checkSession(pageName);
//		return pageName;
//	}
//
//	@GetMapping("/stageSetting/seatSetting")
//	public String clickSeatSetting(Model model) {
//		pageName = "option";
//		pageName = checkSession(pageName);
//		return pageName;
//	}
//
//	@PostMapping("/stageSetting/seatSetting")
//	public String clickSeatSetting(Model model, StageDates data) {
//		pageName = "option";
//		pageName = checkSession(pageName);
//		return pageName;
//	}
//	
//	@GetMapping("/stageSetting/opusInfo")
//	public String clickOpusInfo(Model model) {
//		pageName = "option";
//		pageName = checkSession(pageName);
//		return pageName;
//	}
//	
//	@GetMapping("/opusInfo/place")
//	public String clickPlace(Model model) {
//		pageName = "option";
//		pageName = checkSession(pageName);
//		return pageName;
//	}
//	
//	@PostMapping("/opusInfo/place")
//	public String clickPlace(Model model, @ModelAttribute Stages data) {
//		pageName = "option";
//		pageName = checkSession(pageName);
//		return pageName;
//	}
//	
//	@GetMapping("/opusInfo/openMinutes")
//	public String clickOpenMunutes(Model model) {
//		pageName = "option";
//		pageName = checkSession(pageName);
//		return pageName;
//	}
//	
//	@PostMapping("/opusInfo/openMinutes")
//	public String clickOpenMunutes(Model model, @ModelAttribute Stages data) {
//		pageName = "option";
//		pageName = checkSession(pageName);
//		return pageName;
//	}
//	
//	@GetMapping("/opusInfo/runTime")
//	public String clickRunTime(Model model) {
//		pageName = "option";
//		pageName = checkSession(pageName);
//		return pageName;
//	}
//	
//	@PostMapping("/opusInfo/runTime")
//	public String clickRunTime(Model model, @ModelAttribute Stages data) {
//		pageName = "option";
//		pageName = checkSession(pageName);
//		return pageName;
//	}
//	
//	@GetMapping("/opusInfo/cast")
//	public String clickCast(Model model) {
//		pageName = "option";
//		pageName = checkSession(pageName);
//		return pageName;
//	}
//	
//	@PostMapping("/opusInfo/cast")
//	public String clickCast(Model model, @ModelAttribute Stages data) {
//		pageName = "option";
//		pageName = checkSession(pageName);
//		return pageName;
//	}
//	
//	@GetMapping("/opusInfo/staff")
//	public String clickStaff(Model model) {
//		pageName = "option";
//		pageName = checkSession(pageName);
//		return pageName;
//	}
//	
//	@PostMapping("/opusInfo/staff")
//	public String clickStaff(Model model, @ModelAttribute Stages data) {
//		pageName = "option";
//		pageName = checkSession(pageName);
//		return pageName;
//	}
//	
//	@GetMapping("/opusInfo/story")
//	public String clickStory(Model model) {
//		pageName = "option";
//		pageName = checkSession(pageName);
//		return pageName;
//	}
//	
//	@PostMapping("/opusInfo/story")
//	public String clickStory(Model model, @ModelAttribute Stages data) {
//		pageName = "option";
//		pageName = checkSession(pageName);
//		return pageName;
//	}
//
//	@GetMapping("/stageSetting/members")
//	public String clickMembers(Model model) {
//		pageName = "option";
//		pageName = checkSession(pageName);
//		return pageName;
//	}
//
//	@PostMapping("/stageSetting/members")
//	public String clickMembers(Model model, FormData data) {
//		if(data.getMode().equals("addMember")) {
//			ssc.addMember(model, data);
//		}
//		if(data.getMode().equals("remove")) {
//			ssc.removeMember(model, data);
//		}
//		return ssc.goMembersPage(model);
//	}
//
//	@GetMapping("/customerCheck")
//	public String clickCustomerCheck(Model model) {
//		pageName = "option";
//		pageName = checkSession(pageName);
//		return pageName;
//	}
//
//	@GetMapping("/customerCheck/check")
//	public String clickCheck(Model model) {
//		pageName = "option";
//		pageName = checkSession(pageName);
//		return pageName;
//	}
//
//	@GetMapping("/option")
//	public String clickOption(Model model) {
//		pageName = "option";
//		pageName = checkSession(model, pageName);
//		return pageName;
//	}

	@GetMapping("setFirstStage")
	public String goSetFirstStage(Model model) {
		pageName = "setFirstStage";
		pageName = checkSession(model, pageName);
		if(pageName.equals("index")) {
			setEnvData(model, "manager");
			is.setPageInfo(model);
		}
		return pageName;
	}

	@PostMapping("setFirstStage")
	public String accessSetFirstStage(Model model, @ModelAttribute FormData fd) {

		return goSetFirstStage(model);
	}

//	@GetMapping("/stageSelect/stageLogin")
//	public String clickStageLogin(Model model) {
//		pageName = "option";
//		pageName = checkSession(pageName);
//		return pageName;
//	}
//
//	@GetMapping("/stageSelect/newStage")
//	public String clickNewStage(Model model) {
//		pageName = "option";
//		pageName = checkSession(pageName);
//		return pageName;
//	}
//
//	@GetMapping("/stageSelect/asignGroup")
//	public String clickAsignGroup(Model model) {
//		pageName = "option";
//		pageName = checkSession(pageName);
//		return pageName;
//	}
//
//	@PostMapping("/stageSelect/asignGroup")
//	public String enterGroup(Model model, @ModelAttribute Groupes data) {
//		oc.asignGroup(model, data.getGroupId(), data.getGroupPass());
//		return oc.goStageSelectPage(model);
//	}
//
//	@GetMapping("/stageSelect/groupStage")
//	public String clickGroupStage(Model model) {
//		System.out.println("execute : [GET]/stageSelect/groupStage");
//		pageName = "option";
//		pageName = checkSession(pageName);
//		return pageName;
//	}
//
//	@PostMapping("/stageSelect/groupStage")
//	public String asignGroupStage(Model model, int stageNo) {
//		pageName = "option";
//		pageName = checkSession(pageName);
//		return pageName;
//	}
//
//	@GetMapping("/option/myAccount")
//	public String clickMyAccount(Model model) {
//		pageName = "option";
//		pageName = checkSession(pageName);
//		return pageName;
//	}
//
//	@PostMapping("/option/myAccount")
//	public String clickMyAccount(Model model, @ModelAttribute FormData fd) {
//		if(fd.getMode() != null) {
//			return oc.updateMyAccount(model, fd);
//		}
//		return oc.goMyAccountPage(model);
//	}
//
//	@GetMapping("/option/groupAccount")
//	public String clickGroupAccount(Model model) {
//		pageName = "option";
//		pageName = checkSession(pageName);
//		return pageName;
//	}
//
//	@GetMapping("/groupAccount/accountInfo")
//	public String clickAccountInfo(Model model) {
//		pageName = "option";
//		pageName = checkSession(pageName);
//		return pageName;
//	}
//
//	@PostMapping("/groupAccount/accountInfo")
//	public String clickAccountInfo(Model model, @ModelAttribute Groupes data) {
//		pageName = "option";
//		pageName = checkSession(pageName);
//		return pageName;
//	}
//
//	@GetMapping("/groupAccount/stageList")
//	public String clickStageList(Model model) {
//		pageName = "option";
//		pageName = checkSession(pageName);
//		return pageName;
//	}
//
//	@PostMapping("/groupAccount/stageList")
//	public String clickStageList(Model model, @RequestParam int stageNo) {
//		pageName = "option";
//		pageName = checkSession(pageName);
//		return pageName;
//	}
//
//	@GetMapping("/groupAccount/memberList")
//	public String clickMemberList(Model model) {
//		pageName = "option";
//		pageName = checkSession(pageName);
//		return pageName;
//	}
//
//	@PostMapping("/groupAccount/memberList")
//	public String clickMemberList(Model model, @ModelAttribute GroupLoginList data) {
//		if(data.getUserId() != null && data.getGroupNo() != null) {
//			return oc.addGroupMember(model, data.getGroupNo(), data.getUserId(), data.getUserAuthority());
//		}
//		if(data.getMode().equals("update")) {
//			return oc.updateMemberAuthority(model, data.getGroupNo(), data.getUserNo(), data.getUserAuthority());
//		}
//		if(data.getMode().equals("remove")) {
//			return oc.removeMember(model, data.getGroupNo(), data.getUserNo());
//		}
//		return oc.getMemberList(model);
//	}
//
//	@GetMapping("/groupAccount/changeGroup")
//	public String clickChangeGroup(Model model) {
//		pageName = "option";
//		pageName = checkSession(pageName);
//		return pageName;
//	}
//
//	@PostMapping("/groupAccount/changeGroup")
//	public String clickChangeGroup(Model model, @ModelAttribute Groupes data) {
//		if(data.getGroupPass() != null) {
//			return oc.enterGroup(model, data);
//		}
//		if(data.getUserNo() != null && data.getGroupNo() != null) {
//			return oc.changeGroup(model, data);
//		}
//		return oc.getGroupList(model);
//	}
//
//	@GetMapping("/tools")
//	public String clickTools(Model model) {
//		pageName = "option";
//		pageName = checkSession(pageName);
//		return pageName;
//	}
//
//	@GetMapping("/tools/exportList")
//	public String clickExportList(Model model) {
//		pageName = "option";
//		pageName = checkSession(pageName);
//		return pageName;
//	}
//
//	@GetMapping("/tools/exportTicket")
//	public String clickExportTicket(Model model) {
//		pageName = "option";
//		pageName = checkSession(pageName);
//		return pageName;
//	}
//
//	@GetMapping("/tools/deliverMail")
//	public String clickDeliverMail(Model model) {
//		pageName = "option";
//		pageName = checkSession(pageName);
//		return pageName;
//	}

	@GetMapping("/createUser")
	public String goCreateUser(Model model, String mode, String data) {
		pageName = "createUser";
		setEnvData(model, "createUser");
		cus.setPageInfo(model);
		
		if(mode.equals("start")) {
			cus.start(model);
		} else if(mode.equals("confiData")){
			cus.confiMail(model, data);
		}
		return pageName;
	}

	@PostMapping("/createUser")
	public String inputUserCreateForm(Model model, String back, String mode, String userMail, String userId, String userName, String userKanaName, String userStageName, String userStageKanaName, String userPass, String rePass, String userTel, String userBirthday, String hideBirthYear, String userMode) {
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
}
