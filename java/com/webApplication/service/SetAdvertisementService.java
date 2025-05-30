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
	
	public void addStaff(Model model, String mode, String sysStageId, String sysUserId, String staffDepName) {
        switch (mode) {
        case "addStaff": {
            model.addAttribute("mode", "addStaff");
            model.addAttribute("memberList", sql.getMemberList("stage_login_list sll", sysStageId));
            break;
        }
        case "inputValue": {
            if(sql.getCastOrStaffDataList("staff", sysStageId, sysUserId, "staff_dep_name", staffDepName).size() == 0) {
                sql.addStaff(Pub.createUuid(), sysStageId, staffDepName, sysUserId, 1, 1);
            } else {
                model.addAttribute("mode", "addStaff");
                model.addAttribute("memberList", sql.getMemberList("stage_login_list sll", sysStageId));
                model.addAttribute("message", "その条件に一致する情報は既に登録されています");
            }
            break;
        }
        default:
            break;
        }
    }

    public void changeStaff(Model model, String mode, String sysStageId, String sysStaffId, String[] sysStaffIds, String[] staffDepNames, Integer[] SortNums) {
        switch (mode) {
        case "changeStaff": {
            model.addAttribute("mode", "changeStaff");
            model.addAttribute("memberList", sql.getCastOrStaffDataList("staff", sysStageId, null, null, null));
            model.addAttribute("staffDepNameList", sql.getStaffDepNames(sysStageId));
            break;
        }
        case "deleteStaff": {
            sql.deleteCastOrStaffData("staff", sysStaffId);       
            model.addAttribute("mode", "changeStaff");
            model.addAttribute("memberList", sql.getCastOrStaffDataList("staff", sysStageId, null, null, null));
            model.addAttribute("staffDepNameList", sql.getStaffDepNames(sysStageId));
            break;
        }
        case "inputValue": {
            if(sysStaffIds[0] != null) {
                for(int i = 0; i < sysStaffIds.length; i++) {
                    sql.updateCastOrStaffData("staff", sysStaffIds[i], "staff_dep_name", staffDepNames[i]);
                    sql.updateCastOrStaffData("staff", sysStaffIds[i], "user_sort_num", SortNums[i]+"");
                }
            }
//            model.addAttribute("mode", "changeStaff");
            model.addAttribute("memberList", sql.getCastOrStaffDataList("staff", sysStageId, null, null, null));
            model.addAttribute("staffDepNameList", sql.getStaffDepNames(sysStageId));
            break;
        }
        case "changeSort": {
            if(staffDepNames[0] != null) {
                for(int i = 0; i < staffDepNames.length; i++) {
                    mr.updateData("staff", "staff_sort_num", SortNums[i]+"", "where staff_dep_name = '" + staffDepNames[i] + "'");
                }
            }
//            model.addAttribute("mode", "changeStaff");
            model.addAttribute("memberList", sql.getCastOrStaffDataList("staff", sysStageId, null, null, null));
            model.addAttribute("staffDepNameList", sql.getStaffDepNames(sysStageId));
            break;
        }
        default:
            break;
        }
    }
    
    public void setStageOpenMinutes(Model model, String mode, String value) {
    	switch (mode) {
		case "setStageOpenMinutes": {
            model.addAttribute("mode", mode);
            break;
		}
		case "inputValue": {
			sql.updateAdvertisement(null, "stages", "stage_open_minutes", value);
			DataEntity userData = (DataEntity)session.getAttribute("userSession");
			userData = sql.reGetUserData("sys_user_id", userData.getSys_user_id());
			DataEntity stageData = sql.getStageData("sys_stage_id", userData.getUser_def_stage());
			session.setAttribute("userSession", userData);
			session.setAttribute("defStSession", stageData);
			break;
		}
		default:
			break;
		}
    }
    
    public void setStageRuntime(Model model, String mode, String value) {
    	switch (mode) {
		case "setStageRuntime": {
            model.addAttribute("mode", mode);
            break;
		}
		case "inputValue": {
			sql.updateAdvertisement(null, "stages", "stage_runtime", value);
			DataEntity userData = (DataEntity)session.getAttribute("userSession");
			userData = sql.reGetUserData("sys_user_id", userData.getSys_user_id());
			DataEntity stageData = sql.getStageData("sys_stage_id", userData.getUser_def_stage());
			session.setAttribute("userSession", userData);
			session.setAttribute("defStSession", stageData);
			break;
		}
		default:
			break;
		}
    }
    
    public void setStageStory(Model model, String mode, String value) {
    	switch (mode) {
		case "setStageStory": {
            model.addAttribute("mode", mode);
            break;
		}
		case "inputValue": {
			sql.updateAdvertisement(null, "stages", "stage_story", value);
			DataEntity userData = (DataEntity)session.getAttribute("userSession");
			userData = sql.reGetUserData("sys_user_id", userData.getSys_user_id());
			DataEntity stageData = sql.getStageData("sys_stage_id", userData.getUser_def_stage());
			session.setAttribute("userSession", userData);
			session.setAttribute("defStSession", stageData);
			break;
		}
		default:
			break;
		}
    }
}
