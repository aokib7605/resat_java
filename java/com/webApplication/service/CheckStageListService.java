package com.webApplication.service;

import java.util.ArrayList;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import com.webApplication.entity.DataEntity;
import com.webApplication.functions.SQL;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CheckStageListService {
	private final HttpSession session;
//	private final MainRepository mr;
	private final SQL sql;
	
	public void setPageInfo(Model model, Integer offset, String page) {
		model.addAttribute("title2", "団体公演一覧・参加");
		setGroupStageList(model, offset, page);
	}
	
	public Integer setGroupStageList(Model model, Integer offset, String page) {
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
		model.addAttribute("stageList", getGroupStageList(model, offset, page));
		return offset;
	}
	
	private ArrayList<DataEntity> getGroupStageList(Model model, Integer offset, String page){
		DataEntity userData = (DataEntity)session.getAttribute("userSession");
		if(sql.getStageDataList("s.sys_group_id", userData.getUser_def_group(), null, null).size() <= 0) {
			model.addAttribute("listMessage", "現在、団体で公演を作成していません");
			return null;
		}

		if(sql.getStageDataList("s.sys_group_id", userData.getUser_def_group(), null, null).size() - (offset + 1) * 10 > 0) {
			model.addAttribute("next", "次へ");
		}
		if(offset > 0) {
			model.addAttribute("prev", "前へ");
		}
		return sql.getStageDataList("s.sys_group_id", userData.getUser_def_group(), 10, offset);
	}
	
	private void setViewData(Model model, Integer offset) {
		model.addAttribute("listOffset", offset);
	}
}
