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
		setManagerCountList(model);
	}

	public void setCountList(Model model) {
		DataEntity stageData = (DataEntity)session.getAttribute("defStSession");
		System.out.println("dateList");
		ArrayList<DataEntity> dateList = sql.getCountTransactionByDates(stageData.getSys_stage_id());
		System.out.println("allTicketList");
		ArrayList<DataEntity> allTicketList = sql.getCountTransactionByTicket(stageData.getSys_stage_id());
		ArrayList<ArrayList<DataEntity>> dateTicketList = new ArrayList<ArrayList<DataEntity>>();
		for(DataEntity date: dateList) {
			ArrayList<DataEntity> dateTicket = new ArrayList<DataEntity>();
			for(DataEntity ticket: allTicketList) {
				try {
					System.out.println("data");
					DataEntity data = sql.getCountTicketTransactionByDateAndTicket(stageData.getSys_stage_id(), date.getSys_date_id(), ticket.getSys_ticket_id());
					dateTicket.add(data);
				} catch (Exception e) {
					DataEntity data = new DataEntity();
					dateTicket.add(data);
				}
			}
			dateTicketList.add(dateTicket);
		}
		model.addAttribute("dateList", dateList);
		model.addAttribute("dateTicketList", dateTicketList);
	}
	
	public void setManagerCountList(Model model) {
		DataEntity stageData = (DataEntity)session.getAttribute("defStSession");
		System.out.println("managerList");
		ArrayList<DataEntity> managerList = sql.getMemberList("stage_login_list sll", stageData.getSys_stage_id());
		System.out.println("allTicketList");
		ArrayList<DataEntity> allTicketList = sql.getCountTransactionByTicket(stageData.getSys_stage_id());
		ArrayList<ArrayList<DataEntity>> managerTicketList = new ArrayList<ArrayList<DataEntity>>();
		for(DataEntity manager: managerList) {
			ArrayList<DataEntity> managerTicket = new ArrayList<DataEntity>();
			for(DataEntity ticket: allTicketList) {
				try {
					System.out.println("data");
					DataEntity data = sql.getCountTicketTransactionByManagerAndTicket(stageData.getSys_stage_id(), manager.getSys_user_id(), ticket.getSys_ticket_id());
					managerTicket.add(data);
				} catch (Exception e) {
					DataEntity data = new DataEntity();
					managerTicket.add(data);
				}
			}
			managerTicketList.add(managerTicket);
		}
		ArrayList<DataEntity> groupTicket = new ArrayList<DataEntity>();
		for(DataEntity ticket: allTicketList) {
			try {
				System.out.println("data");
				DataEntity data = sql.getCountTicketTransactionByGroupAndTicket(stageData.getSys_stage_id(), ticket.getSys_ticket_id());
				groupTicket.add(data);
			} catch (Exception e) {
				DataEntity data = new DataEntity();
				groupTicket.add(data);
			}
		}
		model.addAttribute("managerList", managerList);
		model.addAttribute("managerTicketList", managerTicketList);
		model.addAttribute("groupTicket", groupTicket);
		model.addAttribute("allTicketList", allTicketList);
	}
}
