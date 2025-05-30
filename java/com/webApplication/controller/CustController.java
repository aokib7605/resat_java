package com.webApplication.controller;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import com.webApplication.entity.Env;
import com.webApplication.service.EnvService;
import com.webApplication.service.cust.CustLoginService;
import com.webApplication.service.cust.InputFormService;
import com.webApplication.service.cust.MyPageService;
import com.webApplication.service.cust.ReserveListService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class CustController {
	private final HttpSession session;
	private final EnvService es;
	private String pageName = "";	//遷移先のページ名

	private String checkSession(Model model, String pageName) {
		if(checkCustSession(model, pageName).equals("login")) {
			pageName = "login";
			accessLogin(model, "logout");
		}
		return pageName;
	}

	private String checkCustSession(Model model, String pageName) {
		if(session.getAttribute("custSession") == null) {
			return goLoginPage(model);
		} else {
			return pageName;
		}
	}

	private void setEnvData(Model model, String mode) {
		model.addAttribute("title", Env.custApplicationTitle);
		setMenuList(model, mode);
	}

	private void setMenuList(Model model, String mode) {
		//mode には「manager」「customer」のいずれかを格納
		es.setMenuList(model, mode);
	}

	private String goAnyPage(Model model, String pageName) {
		switch (pageName) {
		case "reserve/custLogin": {
			return pageName;
		}
		case "createUser": {
			return pageName;
		}
		case "reserve/inputForm": {
			return pageName;
		}
		default:
			pageName = checkSession(model, pageName);
		}
		return pageName;
	}

	// ===================================================================
	// 各メニューへの遷移メソッド
	private final CustLoginService cls;
	private final InputFormService ifs;
	private final MyPageService mps;
	private final ReserveListService rls;

	@GetMapping("/")
	public String goLoginPage(Model model) {
		setEnvData(model, "login");
		cls.setPageInfo(model);
		return goAnyPage(model, "reserve/custLogin");
	}

	@PostMapping("/custLogin")
	public String accessLogin(Model model, String mode) {
		if(mode.equals("logout")) {
			session.invalidate();
			model.addAttribute("message", Env.logoutCompMessage);
		}
		return goLoginPage(model);
	}

	@GetMapping("/reserve")
	public String goInputForm(Model model, String form, String sys, String ma) {
		// sys = sysFormId, ma = sysUserId
		try {
			pageName = "reserve/inputForm";
			model.addAttribute("title", Env.custApplicationTitle);
			if(sys != null && ma != null) {
				ifs.setPageInfo(model, sys, ma);
			}

			if(form != null) {
				if(form.equals("login")) {
					setEnvData(model, "login");
					cls.setPageInfo(model);
					pageName = "reserve/custLogin";
				} else if(form.equals("myPage")) {
					setEnvData(model, "customer");
					rls.setPageInfo(model);
					model.addAttribute("message", Env.compReserveMessage);
					pageName = "reserve/myPage/reserveList";
				} else if(form.equals("noLoginPage")) {
					pageName = "reserve/noLoginPage";
				} else {
					// 初回遷移時のみ実行
					ifs.inputDate(model, "inputDate", null);
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println(e);
		}
		return goAnyPage(model, pageName);
	}

	@PostMapping("/reserve")
	public String accessInputForm(Model model, String mode, String nextMode, String sysFormId, String sysStageId, String sysDateId, 
			String sysManagerId, String[] traAmounts, String traMemo) {
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
				}
				ifs.inputTicket(model, mode, sysDateId, traAmounts);
				break;
			}
			case "inputMemo": {
				if(nextMode != null) {
					mode = nextMode;
				}
				ifs.inputMemo(model, mode, sysFormId, sysDateId, traAmounts, traMemo);
				break;
			}
			case "confiResult": {
				if(nextMode != null) {
					mode = nextMode;
				}
				ifs.confiResult(model, mode, sysFormId, sysDateId, traAmounts, traMemo, sysManagerId);
				pageName = ifs.registTransaction(model, false);
				break;
			}
			default:
				break;
			}
		} catch (Exception e) {
			System.out.println(e);
			// TODO: handle exception
		}
		return goInputForm(model, pageName, sysFormId, sysManagerId);
	}

	@GetMapping("/myPage")
	public String goMyPage(Model model) {
		try {
			setEnvData(model, "customer");
			mps.setPageInfo(model);
		} catch (Exception e) {
			// TODO: handle exception
		}
		return goAnyPage(model, "reserve/myPage");
	}

	@PostMapping("/myPage")
	public String accessMyPage(Model model, String mode, String userId, String userPass) {
		try {
			if(mode.equals("login")) {
				pageName = cls.checkLoginData(model, mode, userId, userPass);
				if(pageName.equals("login")) {
					return goLoginPage(model);
				} else {
					if(session.getAttribute("tempReceptionList") != null) {
						pageName = ifs.registTransaction(model, false);
						return goInputForm(model, pageName, null, null);
					}
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		return goMyPage(model);
	}
	
	@GetMapping("/myPage/reserveList")
	public String goReserveList(Model model) {
		try {
			setEnvData(model, "customer");
			rls.setPageInfo(model);
		} catch (Exception e) {
			// TODO: handle exception
		}
		return goAnyPage(model, "reserve/myPage/reserveList");
	}
	
	@PostMapping("/myPage/reserveList")
	public String accessReserveList(Model model, String mode, String nextMode) {
		try {
			switch (mode) {
			case "showPast": {
				rls.showPast(model);
				break;
			}
			case "closePast": {
				rls.closePast(model);
				break;
			}
			default:
				break;
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		return goReserveList(model);
	}
}
