package com.webApplication.service;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import com.webApplication.entity.DataEntity;
import com.webApplication.functions.SQL;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SetSeatService {
	private final HttpSession session;
	//	private final MainRepository mr;
	private final SQL sql;
	
	public void setPageInfo(Model model) {
		DataEntity userData = (DataEntity)session.getAttribute("userSession");
		model.addAttribute("title2", "座席設定");
		model.addAttribute("dateList", sql.getDateDataList(userData.getUser_def_stage()));
	}
	
	public void setSeat(Model model, String[] sysDateId, Integer[] stSeat) {
		if(sysDateId[0] != null) {
			for(int i = 0; i < sysDateId.length; i++) {
				sql.updateDateData(sysDateId[i], "st_seat", stSeat[i]+"");
			}
		}
	}
	
	public void setAllSeat(Model model, String[] sysDateId, Integer[] stSeat) {
		if(sysDateId[0] != null) {
			for(int i = 0; i < sysDateId.length; i++) {
				sql.updateDateData(sysDateId[i], "st_seat", stSeat[0]+"");
			}
		}
	}
}
