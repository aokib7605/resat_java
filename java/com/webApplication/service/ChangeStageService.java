package com.webApplication.service;

import java.util.ArrayList;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import com.webApplication.entity.DataEntity;
import com.webApplication.entity.Env;
import com.webApplication.functions.SQL;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChangeStageService {
	private final HttpSession session;
//	private final MainRepository mr;
	private final SQL sql;

	public void setPageInfo(Model model, Integer offset, String page) {
		model.addAttribute("title2", Env.enterStageAndListView);
		setStageList(model, offset, page);
	}
	
	public Integer setSearchStageList(Model model, String mode, String keyword, Integer offset, String page) {
        if(offset == null) {
            offset = 0;
        }
        if(page == null) {
            page = "";
        }
        if(page.equals("searchNext")) {
            offset++;
        } else if(page.equals("searchPrev")){
            offset--;
        }
        setSearchData(model, mode, keyword, offset);
        model.addAttribute("searchStageList", searchStage(model, mode, keyword, offset));
        return offset;
    }
	
	public Integer setStageList(Model model, Integer offset, String page) {
		if(offset == null) {
			offset = 0;
		}
		if(page == null) {
			page = "";
		}
		if(page.equals("next")) {
			offset++;
		} else if(page.equals("prev")){
			offset--;
		}
		setViewData(model, offset);
		model.addAttribute("stageList", getStageList(model, offset, page));
		return offset;
	}
	
    public String loginStage(Model model, String loginMode, String sysStageId, String stagePass) {
    	// 実行モードの調整
        if(loginMode == null) {
            loginMode = "start";
        }
        
        // loginMode の内容で処理を分岐
        switch (loginMode) {
        
        // 画面初期表示時の処理
        case "start": {
            setBaseLoginStageData(model, sysStageId);
            model.addAttribute("mode", "login");
            model.addAttribute("loginMode", "start");
            break;
        }
        // 公演パスワード入力した際の処理
        case "inputPass": {
            DataEntity stageData = sql.getStageData("sys_stage_id", sysStageId);
            if(stageData.getStage_pass().equals(stagePass)) {
                DataEntity userData = sql.updateUserDefStage("sys_stage_id", sysStageId, true);
                model.addAttribute("userData", userData);
                session.setAttribute("userSession", userData);
                session.setAttribute("defStSession", stageData);
                model.addAttribute("message", Env.enterStageMessage);
            } else {
            	// パスワード入力画面を表示する処理
                setBaseLoginStageData(model, sysStageId);
                model.addAttribute("mode", "login");
                model.addAttribute("loginMode", "start");
                model.addAttribute("loginMessage", Env.passwordIsFalse);
            }
            break;
        }
        default:
            break;
        }
        return loginMode;
    }
	
    public String selectStage(Model model, String sysStageId) {
        DataEntity userData = sql.updateUserDefStage("sys_stage_id", sysStageId, false);
        DataEntity stageData = sql.getStageData("sys_stage_id", userData.getUser_def_stage());
        session.setAttribute("userSession", userData);
        session.setAttribute("defStSession", stageData);
        model.addAttribute("userData", userData);
        return "changeStage";
    }
	
	private ArrayList<DataEntity> getStageList(Model model, Integer offset, String page){
		if(sql.getStageDataList("sll.sys_user_id", null, null, null).size() <= 0) {
			model.addAttribute("listMessage", Env.enterStageNotExistMessage);
			return null;
		}

		if(sql.getStageDataList("sll.sys_user_id", null, null, null).size() - (offset + 1) * 10 > 0) {
			model.addAttribute("next", "次へ");
		}
		if(offset > 0) {
			model.addAttribute("prev", "前へ");
		}
		return sql.getStageDataList("sll.sys_user_id", null, 10, offset);
	}
	
    private ArrayList<DataEntity> searchStage(Model model, String mode, String keyword, Integer offset) {
        String column = "";
        if(mode.equals("idSearch")) {
            column = "s.stage_id";
        } else {
            column = "s.stage_name";
        }

        if(sql.getStageDataList(column, keyword, null, null).size() <= 0) {
            model.addAttribute("message", Env.stageNotExist);
            return null;
        }

        if(sql.getStageDataList(column, keyword, null, null).size() - (offset + 1) * 5 > 0) {
            model.addAttribute("searchNext", "次へ");
        }
        if(offset > 0) {
            model.addAttribute("searchPrev", "前へ");
        }
        return sql.getStageDataList(column, keyword, 5, offset);
    }
	
	private void setViewData(Model model, Integer offset) {
		model.addAttribute("listOffset", offset);
	}
	
	private void setSearchData(Model model, String mode, String keyword, Integer offset) {
		model.addAttribute("mode", mode);
		model.addAttribute("keyword", keyword);
		model.addAttribute("searchOffset", offset);
	}
	
	private void setBaseLoginStageData(Model model, String sysStageId) {
		model.addAttribute("loginStageData", sql.getStageData("sys_stage_id", sysStageId));
	}
}
