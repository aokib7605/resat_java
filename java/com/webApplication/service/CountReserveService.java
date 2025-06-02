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
	}
	
	public void setCountList(Model model) {
		DataEntity stageData = (DataEntity)session.getAttribute("defStSession");
		System.out.println("カラム確認");
		ArrayList<DataEntity> dateList = sql.getCountTransactionByDates(stageData.getSys_stage_id());
		System.out.println(dateList.get(0).getSt_date());
		System.out.println("カラム確認終了");
		ArrayList<ArrayList<DataEntity>> dateTicketList = new ArrayList<ArrayList<DataEntity>>();
//		for(DataEntity date: dateList) {
//			ArrayList<DataEntity> dateTicket = new ArrayList<DataEntity>();
//			ArrayList<DataEntity> ticketList = sql.getCountTicketTransactionByDates(stageData.getSys_stage_id(), date.getSys_date_id());
//			for(DataEntity ticket: ticketList) {
//				
//			}
//			System.out.println("date : " + date);
//			System.out.println("ticketList : " + ticketList);
//			ticketList.add(date);
//			dateTicketList.add(ticketList);
//		}
		model.addAttribute("dateTicketList", dateTicketList);
		
	}
}
