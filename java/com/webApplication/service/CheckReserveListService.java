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
public class CheckReserveListService {
	private final HttpSession session;
//	private final MainRepository mr;
	private final SQL sql;
	
	public void setPageInfo(Model model) {
		model.addAttribute("title2", Env.reserveListView);
		setTransactionList(model);
	}
	
	public void setTransactionList(Model model) {
		DataEntity stageData = (DataEntity)session.getAttribute("defStSession");
		ArrayList<DataEntity> transactionList = sql.getTransactionList(stageData.getSys_stage_id());
		model.addAttribute("transactionList", transactionList);
	}
	
	public void startEditMode(Model model, String sysTransactionId) {
		model.addAttribute("edit", true);
		model.addAttribute("sysTransactionId", sysTransactionId);
	}
	
	public void finishEditMode(Model model, String sysTransactionId) {
		model.addAttribute("edit", false);
	}
	
	public void setSelectList(Model model) {
		model.addAttribute("dateFormList", sql.getFormsetDataListGroupByColumn(formData.getSys_stage_id(), sysFormId, "sys_date_id"));
		model.addAttribute("ticketFormList", sql.getFormsetDataListGroupByColumn(formData.getSys_stage_id(), sysFormId, "sys_ticket_id"));
	}
}
