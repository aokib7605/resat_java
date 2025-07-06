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
	
	public void openStage(Model model, String mode, String sysTraId) {
		switch (mode) {
		case "openStage": {
			// トランザクションIDから公演情報を取得
			DataEntity transactionData = sql.getTransaction(sysTraId);
			DataEntity stageData = sql.getStageData("sys_stage_id", transactionData.getSys_stage_id());
			
			// 公演日時のリストを取得
			ArrayList<DataEntity> dateList = sql.getDateDataList(stageData.getSys_stage_id());
			
			// 出演者のリストを取得
			ArrayList<DataEntity> castList = sql.getCastOrStaffDataListGroupByColumn("cast", stageData.getSys_stage_id());
			
			// スタッフのリストを取得
			ArrayList<DataEntity> staffList = sql.getCastOrStaffDataListGroupByColumn("staff", stageData.getSys_stage_id()); 
			
			// 取得した公演情報を画面に渡す
			model.addAttribute("mode", "openStage");
			model.addAttribute("stageInfo", stageData);
			model.addAttribute("traData", transactionData);
			model.addAttribute("dateList", dateList);
			model.addAttribute("castList", castList);
			model.addAttribute("staffList", staffList);
			break;
		}
		default:
			break;
		}
	}
	
	public void traCancel(Model model, String mode, String sysTraId) {
		switch (mode) {
		case "traCancel": {
			sql.deleteTransaction(sysTraId);
			model.addAttribute("message", Env.reserveCancelMessage);
			break;
		}
		default:
			break;
		}
	}
}
