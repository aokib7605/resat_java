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
	
	public void confiResult(Model model, String mode, String sysFormId, String sysDateId, String[] traAmounts, String traMemo, String sysManagerId) {
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
			// stageDataの取得
			DataEntity formData = sql.getFormData(sysFormId);
			DataEntity stageData = sql.getStageData("sys_stage_id", formData.getSys_stage_id());
			
			ArrayList<Integer> traAmountsArr = Pub.createIntArrayList(traAmounts);
			
			// チケット一覧を取得
			ArrayList<DataEntity> ticketFormList = sql.getFormsetDataListGroupByColumn(formData.getSys_stage_id(), sysFormId, "sys_ticket_id");
			
			// ※チケット種別の数だけ仮受付データを作成（選択数が0の場合は作成しない）
			ArrayList<DataEntity> tempReceptionList = new ArrayList<DataEntity>();
			for(int i = 0; i < ticketFormList.size(); i++) {
				if(traAmountsArr.get(i) != null) {
					if(Integer.parseInt(traAmounts[i]) != 0) {
						DataEntity tempReception = new DataEntity();
						tempReception.setSys_stage_id(stageData.getSys_stage_id()); // sys_stage_id
						tempReception.setSys_date_id(sysDateId); // sys_date_id
						tempReception.setSys_ticket_id(ticketFormList.get(i).getSys_ticket_id()); // sys_ticket_id
						tempReception.setTra_amount(Integer.parseInt(traAmounts[i])); // sys_tra_amount
						tempReception.setTra_manager_id(sysManagerId); // tra_manager_id
						tempReception.setTra_memo(traMemo); // tra_memo
						
						tempReceptionList.add(tempReception);
					}
				}
			}
			
			// 仮受付データをセッションに保持
			session.setAttribute("tempReceptionList", tempReceptionList);
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
	
	// ユーザーのログイン状態を確認するメソッド
	private String checkCustSession() {
		if(session.getAttribute("custSession") == null) {
			return "login";
		} else {
			return "myPage";
		}
	}
	
	/*
	 * 予約登録を実行するメソッド
	 * noLogin ... true = ログインなしで登録, false = ログインユーザーで登録
	 */
	public String registTransaction(Model model, boolean noLogin) {
		// ユーザーがログインしていないなら、登録処理を中断する
		if(checkCustSession().equals("login") && noLogin == false) {
			model.addAttribute("message", Env.reserveAccountLoginMessage);
			return "login";
		}
		
		// セッションから仮受付データを取得
		@SuppressWarnings("unchecked")
		ArrayList<DataEntity> tempReceptionList = (ArrayList<DataEntity>) session.getAttribute("tempReceptionList");
		DataEntity custData = (DataEntity)session.getAttribute("custSession");
		
		// チケット予約登録実行
		for(int i = 0; i < tempReceptionList.size(); i++) {
			tempReceptionList.get(i).setSys_user_id(custData.getSys_user_id());
			sql.addTransaction(tempReceptionList.get(i));
		}
		if(noLogin == true) {
			return "noLoginPage";
		} else {
			return "myPage";
		}
	}
}
