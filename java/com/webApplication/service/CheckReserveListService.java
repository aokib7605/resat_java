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
	
	public void finishEditMode(Model model) {
		model.addAttribute("edit", false);
	}
	
	public void setSelectList(Model model) {
		DataEntity stageData = (DataEntity)session.getAttribute("defStSession");
		model.addAttribute("dateFormList", sql.getFormsetDataListGroupByColumn(stageData.getSys_stage_id(), null, "sys_date_id"));
		model.addAttribute("ticketFormList", sql.getFormsetDataListGroupByColumn(stageData.getSys_stage_id(), null, "sys_ticket_id"));
	}
	
	public void updateValue(Model model, String sysTransactionId, String sysDateId, String sysTicketId, Integer traAmount, String traComment) {
		sql.updateTransaction(sysTransactionId, sysDateId, sysTicketId, traAmount, traComment);
		model.addAttribute("message", Env.changeTransactionCompMessage);
		finishEditMode(model);
	}
	
	public void deleteTransaction(Model model, String sysTransactionId, String mode) {
		switch (mode) {
		case "first": {
			startEditMode(model, sysTransactionId);
			setSelectList(model);
			model.addAttribute("deleteMode", true);
			model.addAttribute("message", Env.confiDeleteMessage);
			break;
		}
		case "second":{
			sql.deleteTransaction(sysTransactionId);
			finishEditMode(model);
			model.addAttribute("deleteMode", false);
			model.addAttribute("message", Env.deleteTransactionCompMessage);
		}
		default:
			break;
		}
	}
}
