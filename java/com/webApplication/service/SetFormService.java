package com.webApplication.service;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import com.webApplication.entity.DataEntity;
import com.webApplication.functions.Pub;
import com.webApplication.functions.SQL;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SetFormService {
	private final HttpSession session;
	//	private final MainRepository mr;
	private final SQL sql;
	
	public void setPageInfo(Model model) {
		DataEntity userData = (DataEntity)session.getAttribute("userSession");
		model.addAttribute("title2", "予約フォーム作成");
		model.addAttribute("formList", sql.getFormDataList(userData.getUser_def_stage()));
		model.addAttribute("ticketList", sql.getTicketDataList(userData.getUser_def_stage()));
		model.addAttribute("dateList", sql.getDateDataList(userData.getUser_def_stage()));
	}
	
	public String createForm(Model model, String mode, String sysStageId, String formName, String dateSt, String dateEd) {
		DataEntity userData = (DataEntity)session.getAttribute("userSession");
		switch (mode) {
		case "createForm": {
			model.addAttribute("mode", "createForm");
			break;
		}
		case "inputValue": {
//			model.addAttribute("mode", "createForm");
			sql.addForm(Pub.createUuid(), sysStageId, formName, Pub.convertStrToDateTime(dateSt), Pub.convertStrToDateTime(dateEd));
			model.addAttribute("formList", sql.getFormDataList(sysStageId));
			
            userData = sql.reGetUserData("sys_user_id", userData.getSys_user_id());
            DataEntity stageData = sql.getStageData("sys_stage_id", userData.getUser_def_stage());
            model.addAttribute("message", "フォームを新規作成しました");
            session.setAttribute("userSession", userData);
            session.setAttribute("defStSession", stageData);
            model.addAttribute("userData", userData);
			break;
		}
		default:
			break;
		}
		return "createForm";
	}
	
	public String createDate(Model model, String mode, String sysStageId, String stDate, String stInfo) {
		DataEntity userData = (DataEntity)session.getAttribute("userSession");
		switch (mode) {
		case "createDate": {
			model.addAttribute("mode", "createDate");
			break;
		}
		case "inputValue": {
//			model.addAttribute("mode", "createTicket");
			sql.addDate(Pub.createUuid(), sysStageId, stDate, null, stInfo);
			model.addAttribute("dateList", sql.getDateDataList(sysStageId));
			
            userData = sql.reGetUserData("sys_user_id", userData.getSys_user_id());
            DataEntity stageData = sql.getStageData("sys_stage_id", userData.getUser_def_stage());
            model.addAttribute("message", "公演日時を追加しました");
            session.setAttribute("userSession", userData);
            session.setAttribute("defStSession", stageData);
            model.addAttribute("userData", userData);
			break;
		}
		default:
			break;
		}
		return "createForm";
	}
	
	public String createTicket(Model model, String mode, String sysStageId, String ticketName, Integer ticketPrice) {
		DataEntity userData = (DataEntity)session.getAttribute("userSession");
		switch (mode) {
		case "createTicket": {
			model.addAttribute("mode", "createTicket");
			break;
		}
		case "inputValue": {
//			model.addAttribute("mode", "createTicket");
			sql.addTicket(Pub.createUuid(), sysStageId, ticketName, ticketPrice);
			model.addAttribute("ticketList", sql.getTicketDataList(sysStageId));
			
            userData = sql.reGetUserData("sys_user_id", userData.getSys_user_id());
            DataEntity stageData = sql.getStageData("sys_stage_id", userData.getUser_def_stage());
            model.addAttribute("message", "チケットを新規作成しました");
            session.setAttribute("userSession", userData);
            session.setAttribute("defStSession", stageData);
            model.addAttribute("userData", userData);
			break;
		}
		default:
			break;
		}
		return "createForm";
	}
	
    public String setDateToForm(Model model, String mode, String sysStageId, String sysFormId, String[] selectArrays) {
        DataEntity userData = (DataEntity)session.getAttribute("userSession");
        switch (mode) {
        case "setDateToForm": {
            model.addAttribute("mode", "setDateToForm");
            model.addAttribute("sysFormId", sysFormId);
            model.addAttribute("dateFormList", sql.getFormsetDataListGroupByColumn(sysStageId, sysFormId, "sys_date_id"));
            break;
        }
        case "inputValue": {
            sql.deleteFormsetData(sysStageId, sysFormId, "sys_date_id");
            if(selectArrays != null) {
                for(int i = 0; i < selectArrays.length; i++) {
                    sql.addFormset(sysStageId, sysFormId, null, selectArrays[i]);
                }
            }
//          model.addAttribute("mode", "createDate");
            model.addAttribute("dateFormList", sql.getFormsetDataListGroupByColumn(sysStageId, sysFormId, "sys_date_id"));
            model.addAttribute("dateList", sql.getDateDataList(sysStageId));
            
            userData = sql.reGetUserData("sys_user_id", userData.getSys_user_id());
            DataEntity stageData = sql.getStageData("sys_stage_id", userData.getUser_def_stage());
            model.addAttribute("message", "公演日時を設定しました");
            session.setAttribute("userSession", userData);
            session.setAttribute("defStSession", stageData);
            model.addAttribute("userData", userData);
            break;
        }
        default:
            break;
        }
        return "createForm";
    }
	
	public String setTicketToForm(Model model, String mode, String sysStageId, String sysFormId, String[] selectArrays) {
		DataEntity userData = (DataEntity)session.getAttribute("userSession");
		switch (mode) {
		case "setTicketToForm": {
			model.addAttribute("mode", "setTicketToForm");
			model.addAttribute("sysFormId", sysFormId);
			model.addAttribute("ticketFormList", sql.getFormsetDataListGroupByColumn(sysStageId, sysFormId, "sys_ticket_id"));
			break;
		}
		case "inputValue": {
			sql.deleteFormsetData(sysStageId, sysFormId, "sys_ticket_id");
			if(selectArrays != null) {
				for(int i = 0; i < selectArrays.length; i++) {
					sql.addFormset(sysStageId, sysFormId, selectArrays[i], null);
				}
			}
//			sql.addTicket(Pub.createUuid(), sysStageId, ticketName, ticketPrice);
			model.addAttribute("ticketFormList", sql.getFormsetDataListGroupByColumn(sysStageId, sysFormId, "sys_ticket_id"));
			model.addAttribute("ticketList", sql.getTicketDataList(sysStageId));
			
            userData = sql.reGetUserData("sys_user_id", userData.getSys_user_id());
            DataEntity stageData = sql.getStageData("sys_stage_id", userData.getUser_def_stage());
            model.addAttribute("message", "チケットを設定しました");
            session.setAttribute("userSession", userData);
            session.setAttribute("defStSession", stageData);
            model.addAttribute("userData", userData);
			break;
		}
		default:
			break;
		}
		return "createForm";
	}
	
	public void deleteDate(Model model, String sysDateId) {
		sql.deleteDateOrTicket("dates", sysDateId);
	}
	
	public void deleteTicket(Model model, String sysTicketId) {
		sql.deleteDateOrTicket("tickets", sysTicketId);
	}
}
