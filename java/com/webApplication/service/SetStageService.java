package com.webApplication.service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import com.webApplication.entity.DataEntity;
import com.webApplication.functions.SQL;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SetStageService {
	private final HttpSession session;
	//	private final MainRepository mr;
	private final SQL sql;

	public void setPageInfo(Model model) {
		model.addAttribute("title2", "公演基本情報");
	}

	public void setStageId(Model model, String mode, String sysStageId, String stageId) {
		switch (mode) {
		case "setStageId": {
			model.addAttribute("mode", "setStageId");
			break;
		}
		case "inputValue": {
			if(sql.getStageData("stage_id", stageId) != null) {
				model.addAttribute("mode", "setStageId");
				model.addAttribute("stageId", stageId);
				model.addAttribute("message", "そのIDは既に使用されています");
			} else {
				model.addAttribute("mode", "setStageId");
				DataEntity userData = (DataEntity)session.getAttribute("userSession");
				sql.updateStageData("stage_id", stageId, null);
				userData = sql.reGetUserData("sys_user_id", userData.getSys_user_id());
				DataEntity stageData = sql.getStageData("sys_stage_id", userData.getUser_def_stage());
				model.addAttribute("message", "公演IDを変更しました");
				session.setAttribute("userSession", userData);
				session.setAttribute("defStSession", stageData);
				model.addAttribute("userData", userData);
			}
			break;
		}
		default:
			break;
		}
	}

	public void setStagePass(Model model, String mode, String sysStageId, String stagePass, String rePass) {
		switch (mode) {
		case "setStagePass": {
			model.addAttribute("mode", "setStagePass");
			break;
		}
		case "inputValue": {
			if(!stagePass.equals(rePass)) {
				model.addAttribute("mode", "setStagePass");
				model.addAttribute("message", "パスワードが一致していません");
			} else {
				DataEntity userData = (DataEntity)session.getAttribute("userSession");
				sql.updateStageData("stage_pass", stagePass, null);
				model.addAttribute("message", "パスワードを変更しました");

				userData = sql.reGetUserData("sys_user_id", userData.getSys_user_id());
				DataEntity stageData = sql.getStageData("sys_stage_id", userData.getUser_def_stage());
				session.setAttribute("userSession", userData);
				session.setAttribute("defStSession", stageData);
				model.addAttribute("userData", userData);
			}
			break;
		}
		default:
			break;
		}
	}

	public void setStageName(Model model, String mode, String sysStageId, String stageName) {
		switch (mode) {
		case "setStageName": {
			model.addAttribute("mode", "setStageName");
			break;
		}
		case "inputValue": {
			model.addAttribute("mode", "setStageName");
			DataEntity userData = (DataEntity)session.getAttribute("userSession");
			sql.updateStageData("stage_name", stageName, null);
			model.addAttribute("message", "公演名を変更しました");

			userData = sql.reGetUserData("sys_user_id", userData.getSys_user_id());
			DataEntity stageData = sql.getStageData("sys_stage_id", userData.getUser_def_stage());
			session.setAttribute("userSession", userData);
			session.setAttribute("defStSession", stageData);
			model.addAttribute("userData", userData);
			break;
		}
		default:
			break;
		}
	}

	public void setAttractCustomers(Model model, String mode, String sysStageId, Integer stageAttractCustomers) {
		switch (mode) {
		case "setAttractCustomers": {
			model.addAttribute("mode", "setAttractCustomers");
			break;
		}
		case "inputValue": {
			model.addAttribute("mode", "setAttractCustomers");
			DataEntity userData = (DataEntity)session.getAttribute("userSession");
			sql.updateStageData("stage_attract_customers", stageAttractCustomers + "", null);
			model.addAttribute("message", "集客目標を変更しました");

			userData = sql.reGetUserData("sys_user_id", userData.getSys_user_id());
			DataEntity stageData = sql.getStageData("sys_stage_id", userData.getUser_def_stage());
			session.setAttribute("userSession", userData);
			session.setAttribute("defStSession", stageData);
			model.addAttribute("userData", userData);
			break;
		}
		default:
			break;
		}
	}

	public void setStagePlace(Model model, String mode, String sysStageId, String stagePlaceName, String stagePlaceAddress, String keyword) {
		switch (mode) {
		case "setStagePlace": {
			model.addAttribute("mode", "setStagePlace");
			model.addAttribute("placeList", sql.getPlaceList(""));
			break;
		}
		case "inputKeyword": {
			model.addAttribute("mode", "setStagePlace");
			model.addAttribute("keyword", keyword);
			model.addAttribute("placeList", sql.getPlaceList(keyword));
			break;
		}
		case "inputValue": {
			model.addAttribute("mode", "setStagePlace");
			model.addAttribute("keyword", keyword);

			if (stagePlaceAddress.equals("selected")) {
				// 正規表現で「末尾の括弧内住所」を抽出
				Pattern pattern = Pattern.compile("^(.*)[\\(（](.+)[\\)）]$");
				Matcher matcher = pattern.matcher(stagePlaceName);
				if (matcher.matches()) {
					// 会場名と住所を振り分ける
					stagePlaceName = matcher.group(1).trim();
					stagePlaceAddress = matcher.group(2).trim();
				}
			}

			DataEntity userData = (DataEntity)session.getAttribute("userSession");
			sql.updateStageData("stage_place_name", stagePlaceName, null);
			sql.updateStageData("stage_place_address", stagePlaceAddress, null);
			model.addAttribute("message", "会場を変更しました");
			model.addAttribute("placeList", sql.getPlaceList(""));

			userData = sql.reGetUserData("sys_user_id", userData.getSys_user_id());
			DataEntity stageData = sql.getStageData("sys_stage_id", userData.getUser_def_stage());
			session.setAttribute("userSession", userData);
			session.setAttribute("defStSession", stageData);
			model.addAttribute("userData", userData);
			break;
		}
		default:
			break;
		}
	}

	public void setStageFlyer(Model model, String mode, String sysStageId, int num) {
		//        public void setStageFlyer(Model model, String mode, String sysStageId, int num, MultipartFile file) throws IOException {
		switch (mode) {
		case "setStageFlyer1": {
			model.addAttribute("mode", "setStageFlyer1");
			break;
		}
		case "setStageFlyer2": {
			model.addAttribute("mode", "setStageFlyer2");
			break;
		}
		case "inputValue": {
			model.addAttribute("mode", "setStageFlyer" + num);

			DataEntity userData = (DataEntity)session.getAttribute("userSession");
			DataEntity stageData = (DataEntity)session.getAttribute("defStSession");

//			String fileName = file.getOriginalFilename();
//			String fileType = file.getContentType();
//			byte[] fileBytes = file.getBytes();

//			String fileId = "";
			switch (num) {
			case 1: {
				if(stageData.getStage_flyer_1() == null) {
//					sql.addImage(fileName, fileType, fileBytes, stageData.getSys_stage_id(), "flyer1");
//					fileId = sql.getSysImageId("file_name", fileName, "flyer1", stageData.getSys_stage_id());
				} else {
//					fileId = sql.getSysImageId(null, null, "flyer1", stageData.getSys_stage_id());
//					sql.updateImage(fileId, fileName, fileType, fileBytes);
				}
				break;
			}
			case 2: {
				if(stageData.getStage_flyer_2() == null) {
//					sql.addImage(fileName, fileType, fileBytes, stageData.getSys_stage_id(), "flyer2");
//					fileId = sql.getSysImageId("file_name", fileName, "flyer2", stageData.getSys_stage_id());
				} else {
//					fileId = sql.getSysImageId(null, null, "flyer2", stageData.getSys_stage_id());
//					sql.updateImage(fileId, fileName, fileType, fileBytes);
				}
				break;
			}
			default:
				break;
			}
//			stageData = sql.updateStageData("stage_flyer_" + num, fileId, null);

			//sql.updateStageData("stage_attract_customers", stageAttractCustomers + "", null);
			model.addAttribute("message", "チラシ画像を変更しました");

			userData = sql.reGetUserData("sys_user_id", userData.getSys_user_id());
			session.setAttribute("userSession", userData);
			session.setAttribute("defStSession", stageData);
			model.addAttribute("userData", userData);
			break;
		}
		default:
			break;
		}
	}
}
