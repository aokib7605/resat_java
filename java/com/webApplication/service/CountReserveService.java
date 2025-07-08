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
public class CountReserveService {
	private final HttpSession session;
	//	private final MainRepository mr;
	private final SQL sql;

	public void setPageInfo(Model model) {
		model.addAttribute("title2", Env.countReserveView);
		setCountList(model);
		//		setManagerCountList(model);
	}

	public void setCountList(Model model) {
		// セッションから公演情報を取得
		DataEntity stageData = (DataEntity)session.getAttribute("defStSession");

		// DBから情報を取得
		ArrayList<DataEntity> dateList = sql.getDateDataList(stageData.getSys_stage_id());
		ArrayList<DataEntity> ticketList = sql.getTicketDataList(stageData.getSys_stage_id());

		ArrayList<ArrayList<Integer>> allDateTransactionList = new ArrayList<ArrayList<Integer>>();
		ArrayList<Integer> allTicketTransactionList = new ArrayList<Integer>();

		// 総座席数を取得
		// 日時ごとのチケット別予約数を取得
		Integer totalSeat = 0;
		Integer allTotalAmount = 0;	// 総予約数を格納する変数
		for(DataEntity date: dateList) {
			ArrayList<Integer> oneStageTransactionList = new ArrayList<Integer>();
			totalSeat += date.getSt_seat();

			// 日時ごとの総予約数を格納する変数
			Integer totalAmountByDate = 0;

			for(DataEntity ticket: ticketList) {
				if(sql.getTotalAmountByDateAndTicket(date.getSys_date_id(), ticket.getSys_ticket_id(), stageData.getSys_stage_id()) != null) {
					Integer oneStageTransaction = sql.getTotalAmountByDateAndTicket(date.getSys_date_id(), ticket.getSys_ticket_id(), stageData.getSys_stage_id());
					totalAmountByDate += oneStageTransaction;
					oneStageTransactionList.add(oneStageTransaction);
				} else {
					oneStageTransactionList.add(0);
				}
			}
			oneStageTransactionList.add(totalAmountByDate);
			allDateTransactionList.add(oneStageTransactionList);
		}

		// チケットごとのトータル予約数を取得
		for(DataEntity ticket: ticketList) {

			// チケットごとの総予約数を格納する変数
			Integer totalAmountByTicket = 0;
			
			for(DataEntity date: dateList) {
				if(sql.getTotalAmountByDateAndTicket(date.getSys_date_id(), ticket.getSys_ticket_id(), stageData.getSys_stage_id()) != null) {
					Integer oneStageTransaction = sql.getTotalAmountByDateAndTicket(date.getSys_date_id(), ticket.getSys_ticket_id(), stageData.getSys_stage_id());
					totalAmountByTicket += oneStageTransaction;
				} else {
					totalAmountByTicket += 0;
				}
			}
			allTotalAmount += totalAmountByTicket;
			allTicketTransactionList.add(totalAmountByTicket);
		}

		model.addAttribute("dateList", dateList);
		model.addAttribute("ticketList", ticketList);
		model.addAttribute("totalSeat", totalSeat);
		model.addAttribute("allDateTransactionList", allDateTransactionList);
		model.addAttribute("allTicketTransactionList", allTicketTransactionList);
		model.addAttribute("allTotalAmount", allTotalAmount);
	}

	//	public void setManagerCountList(Model model) {
	//		DataEntity stageData = (DataEntity)session.getAttribute("defStSession");
	//		ArrayList<DataEntity> managerList = sql.getMemberList("stage_login_list sll", stageData.getSys_stage_id());
	//		ArrayList<DataEntity> allTicketList = sql.getCountTransactionByTicket(stageData.getSys_stage_id());
	//		ArrayList<ArrayList<DataEntity>> managerTicketList = new ArrayList<ArrayList<DataEntity>>();
	//		for(DataEntity manager: managerList) {
	//			ArrayList<DataEntity> managerTicket = new ArrayList<DataEntity>();
	//			for(DataEntity ticket: allTicketList) {
	//				try {
	//					DataEntity data = sql.getCountTicketTransactionByManagerAndTicket(stageData.getSys_stage_id(), manager.getSys_user_id(), ticket.getSys_ticket_id());
	//					managerTicket.add(data);
	//				} catch (Exception e) {
	//					DataEntity data = new DataEntity();
	//					managerTicket.add(data);
	//				}
	//			}
	//			managerTicketList.add(managerTicket);
	//		}
	//		ArrayList<DataEntity> groupTicket = new ArrayList<DataEntity>();
	//		for(DataEntity ticket: allTicketList) {
	//			try {
	//				DataEntity data = sql.getCountTicketTransactionByGroupAndTicket(stageData.getSys_stage_id(), ticket.getSys_ticket_id());
	//				groupTicket.add(data);
	//			} catch (Exception e) {
	//				DataEntity data = new DataEntity();
	//				groupTicket.add(data);
	//			}
	//		}
	//		model.addAttribute("managerList", managerList);
	//		model.addAttribute("managerTicketList", managerTicketList);
	//		model.addAttribute("groupTicket", groupTicket);
	//		model.addAttribute("allTicketList", allTicketList);
	//	}
}
