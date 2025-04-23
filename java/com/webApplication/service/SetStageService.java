package com.webApplication.service;

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
                DataEntity userData = (DataEntity)session.getAttribute("userSession");
                sql.updateStageData("stage_id", stageId, null);
                userData = sql.getUserData("sys_user_id", userData.getSys_user_id());
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
                
                userData = sql.getUserData("sys_user_id", userData.getSys_user_id());
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
            DataEntity userData = (DataEntity)session.getAttribute("userSession");
            sql.updateStageData("stage_name", stageName, null);
            model.addAttribute("message", "公演名を変更しました");
            
            userData = sql.getUserData("sys_user_id", userData.getSys_user_id());
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
}
