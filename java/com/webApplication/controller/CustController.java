package com.webApplication.controller;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import com.webApplication.entity.Env;
import com.webApplication.service.EnvService;
import com.webApplication.service.LoginService;
import com.webApplication.service.cust.InputFormService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class CustController {
	private final HttpSession session;
	private final EnvService es;
	private String pageName = "";	//遷移先のページ名
	
//	private String checkSession(Model model, String pageName) {
//		if(checkUserSession(model, pageName).equals("login")) {
//			pageName = "login";
//			accessLogin(model, "logout");
//		}
//		return pageName;
//	}

//	private String checkUserSession(Model model, String pageName) {
//		if(session.getAttribute("custSession") == null) {
//			return goLoginPage(model);
//		} else {
//			return pageName;
//		}
//	}

	private void setEnvData(Model model, String mode) {
		model.addAttribute("title", Env.ApplicationTitle);
		setMenuList(model, mode);
	}

	private void setMenuList(Model model, String mode) {
		//mode には「manager」「customer」のいずれかを格納
		es.setMenuList(model, mode);
	}
	
	private String goAnyPage(Model model, String pageName) {
		switch (pageName) {
		case "login": {
			return pageName;
		}
		case "createUser": {
			return pageName;
		}
		default:
			//pageName = checkSession(model, pageName);
		}
		return pageName;
	}
	
	// ===================================================================
	// 各メニューへの遷移メソッド
	private final LoginService ls;
	private final InputFormService ifs;
	
	public String goLoginPage(Model model) {
		setEnvData(model, "login");
		ls.setPageInfo(model);
		return goAnyPage(model, "login");
	}
	
	@PostMapping("/custlogin")
	public String accessLogin(Model model, String mode) {
		if(mode.equals("logout")) {
			session.invalidate();
			model.addAttribute("message", "ログアウトしました");
		}
		return goLoginPage(model);
	}
	
	@GetMapping("/reserve")
	public String goInputForm(Model model, String form, String sys, String ma) {
		// sys = sysFormId, ma = sysUserId
		try {
			setEnvData(model, "customer");
			ifs.setPageInfo(model, sys, ma);
			if(form != null) {
				ifs.inputDate(model, "inputDate", null);
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		return goAnyPage(model, "reserve/inputForm");
	}
	
	@PostMapping("/reserve")
	public String accessInputForm(Model model, String mode, String nextMode, String sysFormId, String sysStageId, String sysDateId, 
			String sysManagerId, String[] sysTicketIds, Integer[]traAmounts, String traMemo) {
		pageName = null;
		try {
			switch (mode) {
			case "inputDate": {
				if(nextMode != null) {
					mode = nextMode;
				}
				ifs.inputDate(model, mode, sysDateId);
				break;
			}
			case "inputTicket": {
				if(nextMode != null) {
					mode = nextMode;
					System.out.println(sysTicketIds[0]);
					System.out.println(traAmounts[0]);
					System.out.println(sysTicketIds.length);
				}
				ifs.inputTicket(model, mode, sysDateId, sysTicketIds, traAmounts);
				break;
			}
			case "inputMemo": {
				if(nextMode != null) {
					mode = nextMode;
				}
				ifs.inputMemo(model, mode, sysDateId, sysTicketIds, traAmounts, traMemo);
				break;
			}
			default:
				break;
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		return goInputForm(model, pageName, sysFormId, sysManagerId);
	}
}
