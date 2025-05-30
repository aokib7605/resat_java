package com.webApplication.service.cust;

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
public class ReserveListService {
//	private final MainRepository mr;
	private final HttpSession session;
	private final SQL sql;
	
	public void setPageInfo(Model model) {
		model.addAttribute("title2", Env.myReserveListView);
		setReserveList(model);
		setPastReserveList(model);
	}
	
	public void setReserveList(Model model) {
		DataEntity custData = (DataEntity)session.getAttribute("custSession");
		ArrayList<DataEntity> reserveList = sql.getCustReserveList(custData.getSys_user_id());
		model.addAttribute("reserveList", reserveList);
	}
	
	public void setPastReserveList(Model model) {
		DataEntity custData = (DataEntity)session.getAttribute("custSession");
		ArrayList<DataEntity> reserveList = sql.getCustPastReserveList(custData.getSys_user_id());
		model.addAttribute("pastReserveList", reserveList);
	}
	
	public void showPast(Model model) {
		model.addAttribute("showPast", true);
	}
	
	public void closePast(Model model) {
		model.addAttribute("showPast", false);
	}
}
