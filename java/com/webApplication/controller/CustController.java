package com.webApplication.controller;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import com.webApplication.entity.Env;
import com.webApplication.entity.FormEntity;
import com.webApplication.service.EnvService;
import com.webApplication.service.cust.ChangeModeService;
import com.webApplication.service.cust.CustCreateUserService;
import com.webApplication.service.cust.CustLoginService;
import com.webApplication.service.cust.InputFormService;
import com.webApplication.service.cust.MyPageService;
import com.webApplication.service.cust.ReserveListService;
import com.webApplication.service.cust.SetCustService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class CustController {
	private final HttpSession session;
	private final EnvService es;
	private String pageName = "";	//遷移先のページ名

	private String checkSession(Model model, String pageName) {
		if(checkCustSession(model, pageName).equals("reserve/custLogin")) {
			pageName = "reserve/custLogin";
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
		case "reserve/createUser": {
			return pageName;
		}
		case "reserve/inputForm": {
			return pageName;
		}
		default:
			pageName = checkSession(model, pageName);
			if(pageName.equals("reserve/custLogin")) {
				System.out.println("セッション接続切れ");
				return goLoginPage(model);
			}
		}
		return pageName;
	}

	// ===================================================================
	// 各メニューへの遷移メソッド
	private final CustLoginService cls;
	private final InputFormService ifs;
	private final MyPageService mps;
	private final ReserveListService rls;
	private final CustCreateUserService ccus;
	private final SetCustService scs;
	private final ChangeModeService cms;

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

	/**
	 * 
	 * @param model
	 * @param form
	 * @param sys ... sysFormId
	 * @param ma ... sysUserId(Manager)
	 * @return
	 */
	@GetMapping("/reserve")
	public String goInputForm(Model model, String form, String sys, String ma) {
		// sys = sysFormId, ma = sysUserId
		try {
			pageName = "reserve/inputForm";
			model.addAttribute("title", Env.custApplicationTitle);

			// 予約フォームURL初回アクセス時
			if(sys != null && ma != null) {
				ifs.setPageInfo(model, sys, ma);
			}

			if(form != null) {
				switch (form) {
				case "login": {
					setEnvData(model, "login");
					cls.setPageInfo(model);
					pageName = "reserve/custLogin";
					break;
				}

				case "myPage": {
					setEnvData(model, "customer");
					rls.setPageInfo(model);
					model.addAttribute("message", Env.compReserveMessage);
					pageName = "reserve/myPage/reserveList";
					break;
				}

				case "noLoginPage": {
					pageName = "reserve/noLoginPage";
					break;
				}
				default:
					// 初回遷移時のみ実行
					ifs.inputDate(model, "inputDate", null);
					break;
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
			String sysManagerId, String[] traAmounts, String traMemo, String custName, String custKanaName, String custMail,
			String custTel) {
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
				ifs.inputMemo(model, mode, sysFormId, sysDateId, traAmounts, traMemo, sysManagerId);
				if(!mode.equals("back")) {
					// 決定ボタンが押下された場合の処理
					pageName = ifs.loginCheck(model);

					if(pageName != null && pageName.equals("login")) {
						// 未ログインの場合、ログイン画面に遷移
						model.addAttribute("mode", "reserveLogin");
						return goLoginPage(model);
					} else {
						// ログイン済の場合、入力確認画面に遷移
						ifs.inputLoginUser(model, "inputLoginUser");
						model.addAttribute("mode", "confiResult");

						// セッションから各種フォームデータをmodelに保持
						FormEntity formData = (FormEntity) session.getAttribute("formDataSession");
						sysFormId = formData.getSysFormId();	// フォームID
						sysManagerId = formData.getSysUserId();	// 担当者ID
					}
				}
				break;
			}

			// ログインせずに予約登録を完了する
			case "inputUser": {
				if(nextMode != null) {
					mode = nextMode;
				}
				ifs.inputUser(model, mode, sysFormId, sysDateId, traAmounts, traMemo, custName, custKanaName, custMail, custTel);

				// セッションから各種フォームデータをmodelに保持
				FormEntity formData = (FormEntity) session.getAttribute("formDataSession");
				sysFormId = formData.getSysFormId();	// フォームID
				sysManagerId = formData.getSysUserId();	// 担当者ID
				break;
			}

			// ログインして予約登録を完了する
			case "inputLoginUser": {
				if(nextMode != null) {
					mode = nextMode;
				}
				ifs.inputLoginUser(model, "inputLoginUser");
				model.addAttribute("mode", "confiResult");

				// セッションから各種フォームデータをmodelに保持
				FormEntity formData = (FormEntity) session.getAttribute("formDataSession");
				sysFormId = formData.getSysFormId();	// フォームID
				sysManagerId = formData.getSysUserId();	// 担当者ID
				break;
			}

			// 予約内容確認
			case "confiResult": {
				if(nextMode != null) {
					mode = nextMode;
				}

				pageName = ifs.confiResult(model, mode, sysFormId, sysDateId, traAmounts, traMemo, sysManagerId, custName, custKanaName, custMail, custTel);
				if(pageName.equals("myPage")) {
					return "redirect:/myPage/reserveList";
				} else if(pageName.equals("noneUserReserveComp")) {
					session.setAttribute("custSession", null);
					return goLoginPage(model);
				}
				break;
			}
			default:
				break;
			}
		} catch (Exception e) {
			System.out.println(e);
			// TODO: handle exception
		}
		if(sysFormId == null) {
			sysFormId = "";
		}
		if(sysManagerId == null) {
			sysManagerId = "";
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
					// セッションに仮受付データが存在する場合
					if(session.getAttribute("tempReceptionList") != null) {

						ifs.inputLoginUser(model, "inputLoginUser");
						model.addAttribute("mode", "confiResult");

						// セッションから各種フォームデータをmodelに保持
						FormEntity formData = (FormEntity) session.getAttribute("formDataSession");
						String sysFormId = formData.getSysFormId();	// フォームID
						String sysManagerId = formData.getSysUserId();	// 担当者ID
						if(sysFormId == null) {
							sysFormId = "";
						}
						if(sysManagerId == null) {
							sysManagerId = "";
						}
						return goInputForm(model, null, sysFormId, sysManagerId);
					}
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		return goMyPage(model);
	}

	@GetMapping("/reserve/createUser")
	public String goCreateUser(Model model, String mode, String data) {
		try {
			setEnvData(model, "createUser");
			ccus.setPageInfo(model);
			if(mode.equals("start")) {
				ccus.start(model);
			} else if(mode.equals("confiData")){
				ccus.confiMail(model, data);
			}
		} catch (Exception e) {

		}
		return goAnyPage(model, "reserve/createUser");
	}

	@PostMapping("/reserve/createUser")
	public String accessCreateUser(Model model, String back, String mode, String userMail, String userId, String userName, String userKanaName, 
			String userPass, String rePass, String userTel, String userBirthday, String hideBirthYear) {
		String pageName = "createUser";
		try {
			switch (mode) {
			case "checkMail": {
				if(ccus.checkMail(model, userMail) == false) {
					mode = "start";
				}
				break;
			}
			case "confiMail": {
				ccus.confiMail(model, mode);
				break;
			}
			case "inputUserId": {
				mode = ccus.inputUserId(model, mode, userMail, userId);
				break;
			}
			case "inputBaseData": {
				mode = ccus.inputBaseData(model, mode, back, userMail, userId, userName, userKanaName, userPass, rePass);
				break;
			}
			case "inputContactData": {
				mode = ccus.inputContactData(model, mode, back, userMail, userId, userName, userKanaName, userPass, userTel, userBirthday, hideBirthYear);
				break;
			}
			case "confiResult": {
				pageName = ccus.confiResult(model, mode, back, userMail, userId, userName, userKanaName, userPass, userTel, userBirthday, hideBirthYear);
				break;
			}
			default:
				System.out.println("新規ユーザー作成で例外フローが発生しました");
				break;
			}
		} catch (Exception e) {

		}
		if(pageName.equals("createUser")) {
			return goCreateUser(model, mode, null);
		} else {
			// セッションに仮受付データが存在する場合
			if(session.getAttribute("tempReceptionList") != null) {

				ifs.inputLoginUser(model, "inputLoginUser");
				model.addAttribute("mode", "confiResult");

				// セッションから各種フォームデータをmodelに保持
				FormEntity formData = (FormEntity) session.getAttribute("formDataSession");
				String sysFormId = formData.getSysFormId();	// フォームID
				String sysManagerId = formData.getSysUserId();	// 担当者ID
				if(sysFormId == null) {
					sysFormId = "";
				}
				if(sysManagerId == null) {
					sysManagerId = "";
				}
				return goInputForm(model, null, sysFormId, sysManagerId);
			} else {
				return goMyPage(model);
			}
		}
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
	public String accessReserveList(Model model, String mode, String sysTraId) {
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
			case "openStage": {
				rls.openStage(model, mode, sysTraId);
				break;
			}
			case "traCancel": {
				rls.traCancel(model, mode, sysTraId);
				break;
			}
			default:
				break;
			}
		} catch (Exception e) {
			System.out.println(e);
			// TODO: handle exception
		}
		return goReserveList(model);
	}

	@GetMapping("/myPage/setCust")
	public String goSetCust(Model model) {
		pageName = "reserve/myPage/setCust";
		try {
			setEnvData(model, "customer");
			scs.setPageInfo(model);
		} catch (Exception e) {
			// TODO: handle exception
		}
		return goAnyPage(model, pageName);
	}

	@PostMapping("/myPage/setCust")
	public String accessSetCust(Model model, String mode, String nextMode, String sysUserId, String userId, 
			String userMail, String userPass, String rePass, String userTel, String userName, String userKanaName, 
			String userBirthday, String hideBirthYear) {
		try {
			switch (mode) {
			case "setUserId": {
				if(nextMode != null) {
					mode = nextMode;
				}
				scs.setUserId(model, mode, sysUserId, userId);
				break;
			}
			case "setMail": {
				if(nextMode != null) {
					mode = nextMode;
				}
				scs.setUserMail(model, mode, sysUserId, userMail);
				break;
			}
			case "setUserPass": {
				if(nextMode != null) {
					mode = nextMode;
				}
				scs.setUserPass(model, mode, sysUserId, userPass, rePass);
				break;
			}
			case "setUserTel": {
				if(nextMode != null) {
					mode = nextMode;
				}
				scs.setUserTel(model, mode, sysUserId, userTel);
				break;
			}
			case "setUserName": {
				if(nextMode != null) {
					mode = nextMode;
				}
				scs.setUserName(model, mode, sysUserId, userName, userKanaName);
				break;
			}
			case "setUserBirthday": {
				if(nextMode != null) {
					mode = nextMode;
				}
				scs.setUserBirthday(model, mode, sysUserId, userBirthday, hideBirthYear);
				break;
			}
			case "deleteUser": {
				scs.deleteUser(model);
				return goLoginPage(model);
			}
			default:
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		return goSetCust(model);
	}

	@GetMapping("/changeMode")
	public String goChangeMode() {
		try {
			cms.changeMode();
		} catch (Exception e) {
			// TODO: handle exception
		}
		return "redirect:/index";
	}
}
