package com.webApplication.service;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import com.webApplication.entity.DataEntity;
import com.webApplication.functions.Pub;
import com.webApplication.functions.SQL;
import com.webApplication.repository.MainRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SetAdvertisementService {
	private final HttpSession session;
	private final MainRepository mr;
	private final SQL sql;

	public void setPageInfo(Model model) {
		DataEntity userData = (DataEntity)session.getAttribute("userSession");
		model.addAttribute("title2", "宣伝情報");
		model.addAttribute("castList", sql.getCastOrStaffDataListGroupByColumn("cast", userData.getUser_def_stage()));
		model.addAttribute("staffList", sql.getCastOrStaffDataListGroupByColumn("staff", userData.getUser_def_stage()));
	}

	public void addCast(Model model, String mode, String sysStageId, String sysUserId, String castCharaName) {
		switch (mode) {
		case "addCast": {
			model.addAttribute("mode", "addCast");
			model.addAttribute("memberList", sql.getMemberList("stage_login_list sll", sysStageId));
			break;
		}
		case "inputValue": {
			if(sql.getCastOrStaffDataList("cast", sysStageId, sysUserId, "cast_chara_name", castCharaName).size() == 0) {
				sql.addCast(Pub.createUuid(), sysStageId, castCharaName, sysUserId, 1, 1);
			} else {
				model.addAttribute("mode", "addCast");
				model.addAttribute("memberList", sql.getMemberList("stage_login_list sll", sysStageId));
				model.addAttribute("message", "その条件に一致する情報は既に登録されています");
			}
			break;
		}
		default:
			break;
		}
	}

	public void changeCast(Model model, String mode, String sysStageId, String sysCastId, String[] sysCastIds, String[] castCharaNames, Integer[] SortNums) {
		switch (mode) {
		case "changeCast": {
			model.addAttribute("mode", "changeCast");
			model.addAttribute("memberList", sql.getCastOrStaffDataList("cast", sysStageId, null, null, null));
			model.addAttribute("castCharaNameList", sql.getCastCharaNames(sysStageId));
			break;
		}
		case "deleteCast": {
			sql.deleteCastOrStaffData("cast", sysCastId);		
			model.addAttribute("mode", "changeCast");
			model.addAttribute("memberList", sql.getCastOrStaffDataList("cast", sysStageId, null, null, null));
			model.addAttribute("castCharaNameList", sql.getCastCharaNames(sysStageId));
			break;
		}
		case "inputValue": {
			if(sysCastIds[0] != null) {
				for(int i = 0; i < sysCastIds.length; i++) {
					sql.updateCastOrStaffData("cast", sysCastIds[i], "cast_chara_name", castCharaNames[i]);
					sql.updateCastOrStaffData("cast", sysCastIds[i], "user_sort_num", SortNums[i]+"");
				}
			}
			model.addAttribute("mode", "changeCast");
			model.addAttribute("memberList", sql.getCastOrStaffDataList("cast", sysStageId, null, null, null));
			model.addAttribute("castCharaNameList", sql.getCastCharaNames(sysStageId));
			break;
		}
		case "changeSort": {
			if(castCharaNames[0] != null) {
				for(int i = 0; i < castCharaNames.length; i++) {
					mr.updateData("cast", "cast_sort_num", SortNums[i]+"", "where cast_chara_name = '" + castCharaNames[i] + "'");
				}
			}
			model.addAttribute("mode", "changeCast");
			model.addAttribute("memberList", sql.getCastOrStaffDataList("cast", sysStageId, null, null, null));
			model.addAttribute("castCharaNameList", sql.getCastCharaNames(sysStageId));
			break;
		}
		default:
			break;
		}
	}
}
