package com.webApplication.service.cust;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import com.webApplication.entity.DataEntity;
import com.webApplication.entity.Env;
import com.webApplication.entity.FormEntity;
import com.webApplication.functions.Pub;
import com.webApplication.functions.SQL;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InputFormService {
	private final HttpSession session;
//	private final MainRepository mr;
	private final SQL sql;
	
	public void setPageInfo(Model model, String sysFormId, String sysUserId) {
		DataEntity formData = sql.getFormData(sysFormId);
		DataEntity stageData = sql.getStageData("sys_stage_id", formData.getSys_stage_id());
		model.addAttribute("title2", Env.inputFormView);
		model.addAttribute("dateFormList", sql.getFormsetDataListGroupByColumn(formData.getSys_stage_id(), sysFormId, "sys_date_id"));
		model.addAttribute("ticketFormList", sql.getFormsetDataListGroupByColumn(formData.getSys_stage_id(), sysFormId, "sys_ticket_id"));
		model.addAttribute("stageData", stageData);
		model.addAttribute("sysFormId", sysFormId);
		model.addAttribute("sysManagerId", sysUserId);
		setFormDataSession(sysFormId, sysUserId);
	}
	
	public FormEntity setFormDataSession(String sysFormId, String sysUserId) {
		DataEntity formData = sql.getFormData(sysFormId);
		DataEntity stageData = sql.getStageData("sys_stage_id", formData.getSys_stage_id());
		
		FormEntity fe = new FormEntity();
		String formUrl = fe.getFormUrl().replace("<STAGE_URL_TITLE>", stageData.getStage_url_title()).replace("<SYS_FORM_ID>", sysFormId).replace("<SYS_USER_ID>", sysUserId);
		
		fe.setFormUrl(formUrl);
		fe.setSysStageId(stageData.getSys_stage_id());
		fe.setStageName(stageData.getStage_name());
		fe.setSysFormId(sysFormId);
		fe.setFormName(formData.getForm_name());
		
		session.setAttribute("formDataSession", fe);
		return fe;
	}
	
	public void inputDate(Model model, String mode, String sysDateId) {
		switch (mode) {
		case "inputDate": {
			model.addAttribute("sysDateId", sysDateId);
			model.addAttribute("mode", "inputDate");
			break;
		}
		case "inputValue": {
			model.addAttribute("sysDateId", sysDateId);
			model.addAttribute("mode", "inputTicket");
			break;
		}
		default:
			break;
		}
	}
	
	public void inputTicket(Model model, String mode, String sysDateId, String[] sysTicketIds, Integer[] traAmounts) {
		switch (mode) {
		case "inputTicket": {
			model.addAttribute("sysDateId", sysDateId);
			model.addAttribute("sysTicketIds", Pub.createStrArrayList(sysTicketIds));
			model.addAttribute("traAmounts", Pub.createStrArrayList(traAmounts));
			model.addAttribute("mode", "inputTicket");
			break;
		}
		case "back": {
			model.addAttribute("sysDateId", sysDateId);
			model.addAttribute("mode", "inputDate");
			break;
		}
		case "inputValue": {
			model.addAttribute("sysDateId", sysDateId);
			model.addAttribute("sysTicketIds", Pub.createStrArrayList(sysTicketIds));
			model.addAttribute("traAmounts", Pub.createStrArrayList(traAmounts));
			model.addAttribute("mode", "inputMemo");
			break;
		}
		default:
			break;
		}
	}
	
	public void inputMemo(Model model, String mode, String sysDateId, String[] sysTicketIds, Integer[] traAmounts, String traMemo) {
		switch (mode) {
		case "inputMemo": {
			model.addAttribute("mode", "inputMemo");
			break;
		}
		case "back": {
			model.addAttribute("sysDateId", sysDateId);
			model.addAttribute("sysTicketIds", Pub.createStrArrayList(sysTicketIds));
			model.addAttribute("traAmounts", Pub.createStrArrayList(traAmounts));
			model.addAttribute("traMemo", traMemo);
			model.addAttribute("mode", "inputTicket");
			break;
		}
		case "inputValue": {
			model.addAttribute("sysDateId", sysDateId);
			model.addAttribute("sysTicketIds", Pub.createStrArrayList(sysTicketIds));
			model.addAttribute("traAmounts", Pub.createStrArrayList(traAmounts));
			model.addAttribute("traMemo", traMemo);
			model.addAttribute("mode", "inputMemo");
			break;
		}
		default:
			break;
		}
	}
	
	
}
