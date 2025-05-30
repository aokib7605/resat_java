package com.webApplication.controller;

import java.io.IOException;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.webApplication.entity.Env;
import com.webApplication.service.ChangeGroupService;
import com.webApplication.service.ChangeStageService;
import com.webApplication.service.CheckFormListService;
import com.webApplication.service.CheckReserveListService;
import com.webApplication.service.CheckStageListService;
import com.webApplication.service.CreateGroupService;
import com.webApplication.service.CreateStageService;
import com.webApplication.service.CreateUserService;
import com.webApplication.service.EnvService;
import com.webApplication.service.IndexService;
import com.webApplication.service.LoginService;
import com.webApplication.service.SetAdvertisementService;
import com.webApplication.service.SetFormService;
import com.webApplication.service.SetGroupMemberService;
import com.webApplication.service.SetGroupService;
import com.webApplication.service.SetSeatService;
import com.webApplication.service.SetStageMemberService;
import com.webApplication.service.SetStageService;
import com.webApplication.service.SetUserService;

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
			accessLogin(model, "logout");
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
		model.addAttribute("title", Env.applicationTitle);
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
	private final ChangeStageService chss;
	private final ChangeGroupService chgs;
	private final CreateGroupService cgs;
	private final CreateStageService css;
	private final CheckStageListService csls;
	private final SetGroupMemberService sgms;
	private final SetUserService sus;
	private final SetGroupService sgs;
	private final SetStageMemberService ssms;
	private final SetStageService sss;
	private final SetFormService sfs;
	private final SetSeatService sss2;
	private final SetAdvertisementService sas;
	private final CheckFormListService cfls;
	private final CheckReserveListService crls;

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
		try {
			if(session.getAttribute("defStSession") == null) {
				setEnvData(model, "setFirstStage");
			} else {
				setEnvData(model, "manager");
			}
			is.setPageInfo(model);
		} catch (Exception e) {

		}
		return goAnyPage(model, "index");
	}

	@PostMapping("/index")
	public String accessRoot(Model model, String mode, String userId, String userPass) {
		try {
			if(mode.equals("login")) {
				pageName = ls.checkLoginData(model, mode, userId, userPass);
				if(pageName.equals("login")) {
					return goLoginPage(model);
				} else {
					return goRootPage(model);
				}
			}
		} catch (Exception e) {

		}

		return pageName;
	}

	@GetMapping("/createUser")
	public String goCreateUser(Model model, String mode, String data) {
		try {
			setEnvData(model, "createUser");
			cus.setPageInfo(model);
			if(mode.equals("start")) {
				cus.start(model);
			} else if(mode.equals("confiData")){
				cus.confiMail(model, data);
			}
		} catch (Exception e) {

		}
		return goAnyPage(model, "createUser");
	}

	@PostMapping("/createUser")
	public String accessCreateUser(Model model, String back, String mode, String userMail, String userId, String userName, String userKanaName, 
			String userStageName, String userStageKanaName, String userPass, String rePass, String userTel, String userBirthday, String hideBirthYear, String userMode) {
		String pageName = "createUser";
		try {
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
		} catch (Exception e) {

		}
		if(pageName.equals("createUser")) {
			return goCreateUser(model, mode, null);
		} else {
			return goRootPage(model);
		}
	}

	@GetMapping("setFirstStage")
	public String goSetFirstStage(Model model) {
		try {
			setEnvData(model, "setFirstStage");
			is.setPageInfo(model);
		} catch (Exception e) {
			// TODO: handle exception
		}
		return goAnyPage(model, "setFirstStage");
	}

	@PostMapping("setFirstStage")
	public String accessSetFirstStage(Model model, String mode) {
		try {
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
		} catch (Exception e) {
			// TODO: handle exception
		}
		return goSetFirstStage(model);
	}

	@GetMapping("/setStage")
	public String goSetStage(Model model){
		try {
			setEnvData(model, "manager");
			sss.setPageInfo(model);
		} catch (Exception e) {
			// TODO: handle exception
		}
		return goAnyPage(model, "setStage");
	}

	@PostMapping("/setStage")
	public String accessSetStage(Model model, String mode, String nextMode, String stageId,
			String stagePass, String rePass, String stageName, Integer stageAttractCustomers, String stagePlaceName, String stagePlaceAddress, String keyword,
			MultipartFile file) throws IOException {
		try {
			switch (mode) {
			case "setStageId": {
				if(nextMode != null) {
					mode = nextMode;
				}
				sss.setStageId(model, mode, null, stageId);
				break;
			}
			case "setStagePass": {
				if(nextMode != null) {
					mode = nextMode;
				}
				sss.setStagePass(model, mode, null, stagePass, rePass);
				break;
			}
			case "setStageName": {
				if(nextMode != null) {
					mode = nextMode;
				}
				sss.setStageName(model, mode, null, stageName);
				break;
			}
			case "setAttractCustomers": {
				if(nextMode != null) {
					mode = nextMode;
				}
				sss.setAttractCustomers(model, mode, null, stageAttractCustomers);
				break;
			}
			case "setStagePlace": {
				if(nextMode != null) {
					mode = nextMode;
				}
				sss.setStagePlace(model, mode, null, stagePlaceName, stagePlaceAddress, keyword);
				break;
			}
			case "setStageFlyer1": {
				if(nextMode != null) {
					mode = nextMode;
				}
				sss.setStageFlyer(model, mode, null, 1, file);
				break;
			}
			case "setStageFlyer2": {
				if(nextMode != null) {
					mode = nextMode;
				}
				sss.setStageFlyer(model, mode, null, 2, file);
				break;
			}
			default:
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		return goSetStage(model);
	}

	@GetMapping("/setForm")
	public String goSetForm(Model model) {
		try {
			setEnvData(model, "manager");
			sfs.setPageInfo(model);
		} catch (Exception e) {
			// TODO: handle exception
		}
		return goAnyPage(model, "setForm");
	}

	@PostMapping("/setForm")
	public String accessSetForm(Model model, String mode, String nextMode, String sysStageId, String formName, String dateSt, String dateEd,
			String stDate, String stInfo, String ticketName, Integer ticketPrice, String sysFormId, String[] selectArrays,
			String sysDateId, String sysTicketId) {
		try {
			switch (mode) {
			case "createForm": {
				if(nextMode != null) {
					mode = nextMode;
				}
				sfs.createForm(model, mode, sysStageId, formName, dateSt, dateEd);
				break;
			}
			case "createDate": {
				if(nextMode != null) {
					mode = nextMode;
				}
				sfs.createDate(model, mode, sysStageId, stDate, stInfo);
				break;
			}
			case "createTicket": {
				if(nextMode != null) {
					mode = nextMode;
				}
				sfs.createTicket(model, mode, sysStageId, ticketName, ticketPrice);
				break;
			}
			case "setDateToForm": {
				if(nextMode != null) {
					mode = nextMode;
				}
				sfs.setDateToForm(model, mode, sysStageId, sysFormId, selectArrays);
				break;
			}
			case "setTicketToForm": {
				if(nextMode != null) {
					mode = nextMode;
				}
				sfs.setTicketToForm(model, mode, sysStageId, sysFormId, selectArrays);
				break;
			}
			case "deleteDate": {
				if(nextMode != null) {
					mode = nextMode;
				}
				sfs.deleteDate(model, sysDateId);
				break;
			}
			case "deleteTicket": {
				if(nextMode != null) {
					mode = nextMode;
				}
				sfs.deleteTicket(model, sysTicketId);
				break;
			}
			default:
				break;
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		return goSetForm(model);
	}

	@GetMapping("/setSeat")
	public String goSetSeat(Model model) {
		try {
			setEnvData(model, "manager");
			sss2.setPageInfo(model);
		} catch (Exception e) {
			// TODO: handle exception
		}
		return goAnyPage(model, "setSeat");
	}

	@PostMapping("/setSeat")
	public String accessSetSeat(Model model, String mode, String[] sysDateId, Integer[] stSeat) {
		try {
			switch (mode) {
			case "setSeat": {
				sss2.setSeat(model, sysDateId, stSeat);
				break;
			}
			case "setAllSeat": {
				sss2.setAllSeat(model, sysDateId, stSeat);
				break;
			}
			default:
				break;
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		return goSetSeat(model);
	}
	
	@GetMapping("/setAdvertisement")
	public String goSetAdvertisement(Model model) {
		try {
			setEnvData(model, "manager");
			sas.setPageInfo(model);
		} catch (Exception e) {
			// TODO: handle exception
		}
		return goAnyPage(model, "setAdvertisement");
	}
	
	@PostMapping("/setAdvertisement")
	public String accessSetAdvertisement(Model model, String mode, String nextMode, String sysStageId, String sysUserId, String castCharaName, String[] castCharaNames,
			String sysCastId, String[] sysCastIds, String deleteId, Integer[] sortNums,
			String sysStaffId, String[] sysStaffIds, String staffDepName, String[] staffDepNames, String stageOpenMinutes, String stageRuntime, String stageStory) {
		try {
			switch (mode) {
			case "addCast": {
				if(nextMode != null) {
					mode = nextMode;
				}
				sas.addCast(model, mode, sysStageId, sysUserId, castCharaName);
				break;
			}
			case "changeCast": {
				if(nextMode != null) {
					mode = nextMode;
				}
				if(deleteId != null) {
					mode = "deleteCast";
					sysCastId = deleteId;
				}
				sas.changeCast(model, mode, sysStageId, sysCastId, sysCastIds, castCharaNames, sortNums);
				break;
			}
            case "addStaff": {
                if(nextMode != null) {
                    mode = nextMode;
                }
                sas.addStaff(model, mode, sysStageId, sysUserId, staffDepName);
                break;
            }
            case "changeStaff": {
                if(nextMode != null) {
                    mode = nextMode;
                }
                if(deleteId != null) {
                    mode = "deleteStaff";
                    sysStaffId = deleteId;
                }
                sas.changeStaff(model, mode, sysStageId, sysStaffId, sysStaffIds, staffDepNames, sortNums);
                break;
            }
            case "setStageOpenMinutes": {
                if(nextMode != null) {
                    mode = nextMode;
                }
                sas.setStageOpenMinutes(model, mode, stageOpenMinutes);
                break;
            }
            case "setStageRuntime": {
                if(nextMode != null) {
                    mode = nextMode;
                }
                sas.setStageRuntime(model, mode, stageRuntime);
                break;
            }
            case "setStageStory": {
                if(nextMode != null) {
                    mode = nextMode;
                }
                sas.setStageStory(model, mode, stageStory);
                break;
            }
			default:
				break;
			}
		} catch (Exception e) {
			System.out.println(e);
			// TODO: handle exception
		}
		return goSetAdvertisement(model);
	}

	@GetMapping("/setStageMember")
	public String goSetStageMember(Model model, Integer offset, String page) {
		try {
			setEnvData(model, "manager");
			ssms.setPageInfo(model);
		} catch (Exception e) {
			// TODO: handle exception
		}
		return goAnyPage(model, "setStageMember");
	}

	@PostMapping("/setStageMember")
	public String accessSetStageMember(Model model, String mode, String loginMode, String keyword, 
			Integer offset, Integer searchOffset, Integer listOffset, String page, String sysUserId, 
			String nextMode, String stageAuthority) {
		try {
			if(loginMode != null) {
				String tmp = mode;
				mode = loginMode;
				loginMode = tmp;
			}
			switch (mode) {
			case "idSearch": {
				offset = searchOffset;
				offset = ssms.setSearchUserList(model, mode, keyword, offset, page);
				break;
			}
			case "nameSearch": {
				offset = searchOffset;
				offset = ssms.setSearchUserList(model, mode, keyword, offset, page);
				break;
			}
			case "mailSearch": {
				offset = searchOffset;
				offset = ssms.setSearchUserList(model, mode, keyword, offset, page);
				break;
			}
			case "select": {
				if(nextMode != null) {
					mode = nextMode;
				}
				pageName = ssms.setMember(model, mode, stageAuthority, sysUserId);
				break;
			}
			case "invite": {
				offset = searchOffset;
				mode = ssms.inviteStage(model, sysUserId);
				offset = ssms.setSearchUserList(model, loginMode, keyword, listOffset, page);
				break;
			}
			default:
				break;
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		return goSetStageMember(model, offset, page);
	}
	
	@GetMapping("checkReserveList")
	public String goCheckReserveList(Model model) {
		try {
			setEnvData(model, "manager");
			crls.setPageInfo(model);
		} catch (Exception e) {
			System.out.println(e);
			// TODO: handle exception
		}
		return goAnyPage(model, "checkReserveList");
	}
	
	@PostMapping("checkReserveList")
	public String accessCheckReserveList(Model model, String mode, String sysTransactionId) {
		try {
			switch (mode) {
			case "edit": {
				crls.startEditMode(model, sysTransactionId);
				break;
			}
			default:
				break;
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		return goCheckReserveList(model);
	}
	
	@GetMapping("checkFormList")
	public String goCheckFormList(Model model) {
		try {
			setEnvData(model, "manager");
			cfls.setPageInfo(model);
		} catch (Exception e) {
			// TODO: handle exception
		}
		return goAnyPage(model, "checkFormList");
	}

	@GetMapping("/createStage")
	public String goCreateStage(Model model, String mode) {
		try {
			setEnvData(model, "manager");
			css.setPageInfo(model);
			switch (mode) {
			case "inputSysGroupId": {
				model.addAttribute("mode", "inputSysGroupId");
			}
			default:
				break;
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		return goAnyPage(model, "createStage");
	}

	@PostMapping("/createStage")
	public String accessCreateStage(
			Model model, String mode, String back, String sysGroupId, String stageId, String stagePass, String rePass, String stageName, 
			Integer stageAttractCustomers, String stageUrlTitle, String stagePlaceName, String stagePlaceAddress, String keyword, 
			@RequestParam(required = false) MultipartFile file1, @RequestParam(required = false) MultipartFile file2) throws IOException {
		pageName = "createStage";
		try {
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
				pageName = css.confiResult(model, back, sysGroupId, stageId, stagePass, rePass, stageName, stageAttractCustomers, stageUrlTitle, stagePlaceName, stagePlaceAddress, keyword);
				if(pageName.equals("uploadImages")) {
					pageName = "createStage";
				}
				break;
			}
			default:
				break;
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		if(pageName.equals("createStage")) {
			return goCreateStage(model, mode);
		} else {
			return goRootPage(model);
		}
	}

	@GetMapping("/checkStageList")
	public String goCheckStageList(Model model, Integer offset, String page) {
		try {
			setEnvData(model, "manager");
			csls.setPageInfo(model, offset, page);
		} catch (Exception e) {
			// TODO: handle exception
		}
		return goAnyPage(model, "checkStageList");
	}

	@PostMapping("/checkStageList")
	public String accessCheckStageList(Model model, String mode, String loginMode, Integer offset, Integer listOffset, String page, String sysStageId, String stagePass) {
		try {
			switch (mode) {
			case "stageList": {
				offset = listOffset;
				break;
			}
			case "select": {
				pageName = chss.selectStage(model, sysStageId);
				break;
			}
			case "login": {
				loginMode = chss.loginStage(model, loginMode, sysStageId, stagePass);
				break;
			}
			default:
				break;
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		return goCheckStageList(model, offset, page);
	}

	@GetMapping("/setGroup")
	public String goSetGroup(Model model) {
		try {
			setEnvData(model, "manager");
			sgs.setPageInfo(model);
		} catch (Exception e) {
			// TODO: handle exception
		}
		return goAnyPage(model, "setGroup");
	}

	@PostMapping("/setGroup")
	public String accessSetGroup(Model model, String mode, String nextMode, String groupId, String groupMail,
			String groupPass, String rePass, String groupName, String groupKanaName) {
		try {
			switch (mode) {
			case "setGroupId": {
				if(nextMode != null) {
					mode = nextMode;
				}
				sgs.setGroupId(model, mode, null, groupId);
				break;
			}
			case "setMail": {
				if(nextMode != null) {
					mode = nextMode;
				}
				sgs.setGroupMail(model, mode, null, groupMail);
				break;
			}
			case "setGroupPass": {
				if(nextMode != null) {
					mode = nextMode;
				}
				sgs.setGroupPass(model, mode, null, groupPass, rePass);
				break;
			}
			case "setGroupName": {
				if(nextMode != null) {
					mode = nextMode;
				}
				sgs.setGroupName(model, mode, null, groupName, groupKanaName);
				break;
			}
			default:
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		return goSetGroup(model);
	}

	@GetMapping("/setGroupMember")
	public String goSetGroupMember(Model model, Integer offset, String page) {
		try {
			setEnvData(model, "manager");
			sgms.setPageInfo(model);
		} catch (Exception e) {
			// TODO: handle exception
		}
		return goAnyPage(model, "setGroupMember");
	}

	@PostMapping("/setGroupMember")
	public String accessSetGroupMember(Model model, String mode, String loginMode, String keyword, 
			Integer offset, Integer searchOffset, Integer listOffset, String page, String sysUserId, 
			String nextMode, String groupAuthority) {
		try {
			if(loginMode != null) {
				String tmp = mode;
				mode = loginMode;
				loginMode = tmp;
			}
			switch (mode) {
			case "idSearch": {
				offset = searchOffset;
				offset = sgms.setSearchUserList(model, mode, keyword, offset, page);
				break;
			}
			case "nameSearch": {
				offset = searchOffset;
				offset = sgms.setSearchUserList(model, mode, keyword, offset, page);
				break;
			}
			case "mailSearch": {
				offset = searchOffset;
				offset = sgms.setSearchUserList(model, mode, keyword, offset, page);
				break;
			}
			case "select": {
				if(nextMode != null) {
					mode = nextMode;
				}
				pageName = sgms.setMember(model, mode, groupAuthority, sysUserId);
				break;
			}
			case "invite": {
				offset = searchOffset;
				mode = sgms.inviteGroup(model, sysUserId);
				offset = sgms.setSearchUserList(model, loginMode, keyword, listOffset, page);
				break;
			}
			default:
				break;
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		return goSetGroupMember(model, offset, page);
	}

	@GetMapping("/changeStage")
	public String goChangeStage(Model model, Integer offset, String page) {
		try {
			setEnvData(model, "manager");
			chss.setPageInfo(model, offset, page);
		} catch (Exception e) {
			// TODO: handle exception
		}
		return goAnyPage(model, "changeStage");
	}

	@PostMapping("/changeStage")
	public String accessChangeStage(Model model, String mode, String loginMode, String keyword, Integer offset, Integer searchOffset, Integer listOffset, String page, String sysStageId, String stagePass) {
		try {
			switch (mode) {
			case "stageList": {
				offset = listOffset;
				break;
			}
			case "idSearch": {
				offset = searchOffset;
				offset = chss.setSearchStageList(model, mode, keyword, offset, page);
				break;
			}
			case "nameSearch": {
				offset = searchOffset;
				offset = chss.setSearchStageList(model, mode, keyword, offset, page);
				break;
			}
			case "select": {
				pageName = chss.selectStage(model, sysStageId);
				break;
			}
			case "login": {
				loginMode = chss.loginStage(model, loginMode, sysStageId, stagePass);
				break;
			}
			default:
				break;
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		return goChangeStage(model, offset, page);
	}

	@GetMapping("/changeGroup")
	public String goChangeGroup(Model model, Integer offset, String page) {
		try {
			setEnvData(model, "manager");
			chgs.setPageInfo(model, offset, page);
		} catch (Exception e) {
			// TODO: handle exception
		}
		return goAnyPage(model, "changeGroup");
	}

	@PostMapping("changeGroup")
	public String accessChangeGroup(Model model, String mode, String loginMode, String keyword, Integer offset, Integer searchOffset, Integer listOffset, String page, String sysGroupId, String groupPass) {
		try {
			switch (mode) {
			case "groupList": {
				offset = listOffset;
				break;
			}
			case "idSearch": {
				offset = searchOffset;
				offset = chgs.setSearchGroupList(model, mode, keyword, offset, page);
				break;
			}
			case "nameSearch": {
				offset = searchOffset;
				offset = chgs.setSearchGroupList(model, mode, keyword, offset, page);
				break;
			}
			case "select": {
				pageName = chgs.selectGroup(model, sysGroupId);
				break;
			}
			case "login": {
				loginMode = chgs.loginGroup(model, loginMode, sysGroupId, groupPass);
				break;
			}
			default:
				break;
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		return goChangeGroup(model, offset, page);
	}

	@GetMapping("/setUser")
	public String goSetUser(Model model) {
		try {
			setEnvData(model, "manager");
			sus.setPageInfo(model);
		} catch (Exception e) {
			// TODO: handle exception
		}
		return goAnyPage(model, "setUser");
	}

	@PostMapping("/setUser")
	public String accessSetUser(Model model, String mode, String nextMode, String sysUserId, String userId, String userMail, String userPass, String rePass, 
			String userTel, String userName, String userKanaName, String userBirthday, String hideBirthYear) {
		try {
			switch (mode) {
			case "setUserId": {
				if(nextMode != null) {
					mode = nextMode;
				}
				sus.setUserId(model, mode, sysUserId, userId);
				break;
			}
			case "setMail": {
				if(nextMode != null) {
					mode = nextMode;
				}
				sus.setUserMail(model, mode, sysUserId, userMail);
				break;
			}
			case "setUserPass": {
				if(nextMode != null) {
					mode = nextMode;
				}
				sus.setUserPass(model, mode, sysUserId, userPass, rePass);
				break;
			}
			case "setUserTel": {
				if(nextMode != null) {
					mode = nextMode;
				}
				sus.setUserTel(model, mode, sysUserId, userTel);
				break;
			}
			case "setUserName": {
				if(nextMode != null) {
					mode = nextMode;
				}
				sus.setUserName(model, mode, sysUserId, userName, userKanaName);
				break;
			}
			case "setUserStageName": {
				if(nextMode != null) {
					mode = nextMode;
				}
				sus.setUserStageName(model, mode, sysUserId, userName, userKanaName);
				break;
			}
			case "setUserBirthday": {
				if(nextMode != null) {
					mode = nextMode;
				}
				sus.setUserBirthday(model, mode, sysUserId, userBirthday, hideBirthYear);
				break;
			}
			default:
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		return goSetUser(model);
	}

	@GetMapping("/createGroup")
	public String goCreateGroup(Model model, String mode) {
		try {
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
		} catch (Exception e) {
			// TODO: handle exception
		}
		return goAnyPage(model, "createGroup");
	}

	@PostMapping("/createGroup")
	public String accessCreateGroup(Model model, String mode, String back, String groupId, String groupName, String groupKanaName, String groupPass, String rePass, String groupMail) {
		pageName = "createGroup";
		try {
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
		} catch (Exception e) {
			// TODO: handle exception
		}
		if(pageName.equals("createGroup")) {
			return goCreateGroup(model, mode);
		} else {
			return goRootPage(model);
		}
	}
}
