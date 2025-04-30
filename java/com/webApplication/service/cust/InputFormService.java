package com.webApplication.service.cust;

import java.time.LocalDateTime;
import java.util.ArrayList;

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

	public void inputTicket(Model model, String mode, String sysDateId, String[] traAmounts) {
		switch (mode) {
		case "inputTicket": {
			model.addAttribute("sysDateId", sysDateId);
			model.addAttribute("traAmounts", Pub.createIntArrayList(traAmounts));
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
			model.addAttribute("traAmounts", Pub.createIntArrayList(traAmounts));
			if(Pub.nullIfAllZeroOrNull(Pub.createIntArrayList(traAmounts)) == null) {
				model.addAttribute("mode", "inputTicket");
			} else {
				model.addAttribute("mode", "inputMemo");
			}
			break;
		}
		default:
			break;
		}
	}

	public void inputMemo(Model model, String mode, String sysFormId, String sysDateId, String[] traAmounts, String traMemo) {
		switch (mode) {
		case "inputMemo": {
			model.addAttribute("sysDateId", sysDateId);
			model.addAttribute("traAmounts", Pub.createIntArrayList(traAmounts));
			model.addAttribute("mode", "inputMemo");
			break;
		}
		case "back": {
			model.addAttribute("sysDateId", sysDateId);
			model.addAttribute("traAmounts", Pub.createIntArrayList(traAmounts));
			model.addAttribute("mode", "inputTicket");
			break;
		}
		case "inputValue": {
			setTotalPriceAndDate(model, sysFormId, Pub.createIntArrayList(traAmounts), sysDateId);
			model.addAttribute("sysDateId", sysDateId);
			model.addAttribute("traAmounts", Pub.createIntArrayList(traAmounts));
			model.addAttribute("traMemo", traMemo);
			model.addAttribute("mode", "confiResult");
			break;
		}
		default:
			break;
		}
	}
	
	public void confiResult(Model model, String mode, String sysFormId, String sysDateId, String[] traAmounts, String traMemo) {
		switch (mode) {
		case "confiResult": {
			model.addAttribute("sysDateId", sysDateId);
			model.addAttribute("traAmounts", Pub.createIntArrayList(traAmounts));
			model.addAttribute("traMemo", traMemo);
			model.addAttribute("mode", "confiResult");
			break;
		}
		case "back": {
			model.addAttribute("sysDateId", sysDateId);
			model.addAttribute("traAmounts", Pub.createIntArrayList(traAmounts));
			model.addAttribute("traMemo", traMemo);
			model.addAttribute("mode", "inputMemo");
			break;
		}
		case "inputValue": {
			setTotalPriceAndDate(model, sysFormId, Pub.createIntArrayList(traAmounts), sysDateId);
			model.addAttribute("sysDateId", sysDateId);
			model.addAttribute("traAmounts", Pub.createIntArrayList(traAmounts));
			model.addAttribute("traMemo", traMemo);
			model.addAttribute("mode", "confiResult");
			break;
		}
		default:
			break;
		}
	}

	private void setTotalPriceAndDate(Model model, String sysFormId, ArrayList<Integer> traAmounts, String sysDateId) {
		DataEntity formData = sql.getFormData(sysFormId);
		Integer totalPrice = getTotalPrice(formData.getSys_stage_id(), sysFormId, traAmounts);
		model.addAttribute("stDate", getDateValue(sysDateId));
		model.addAttribute("totalPrice", totalPrice);
	}
	
	private Integer getTotalPrice(String sysStageId, String sysFormId, ArrayList<Integer> traAmounts){
		ArrayList<DataEntity> ticketDataList = sql.getFormsetDataListGroupByColumn(sysStageId, sysFormId, "sys_ticket_id");
		Integer totalPrice = 0;
		for(int i = 0; i < ticketDataList.size(); i++) {
			totalPrice += ticketDataList.get(i).getTicket_price() * traAmounts.get(i);
		}
		return totalPrice;
	}
	
	private LocalDateTime getDateValue(String sysDateId) {
		return sql.getDateData(sysDateId).getSt_date();
	}
}
