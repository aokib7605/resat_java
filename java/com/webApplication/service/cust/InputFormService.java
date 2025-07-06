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
import com.webApplication.service.JavaMailSenderService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InputFormService {
	private final HttpSession session;
	//	private final MainRepository mr;
	private final SQL sql;
	private final JavaMailSenderService mailSender;

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
				model.addAttribute("message", Env.inputTraAmountIsMissedMessage);
			} else {
				model.addAttribute("mode", "inputMemo");
			}
			break;
		}
		default:
			break;
		}
	}

	public void inputMemo(Model model, String mode, String sysFormId, String sysDateId, String[] traAmounts, String traMemo, String sysManagerId) {
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
			model.addAttribute("sysDateId", sysDateId);
			model.addAttribute("traAmounts", Pub.createIntArrayList(traAmounts));
			model.addAttribute("traMemo", traMemo);

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
					//					if(Integer.parseInt(traAmounts[i]) != 0) {
					DataEntity tempReception = new DataEntity();
					tempReception.setSys_stage_id(stageData.getSys_stage_id()); 				// sys_stage_id
					tempReception.setSys_date_id(sysDateId); 									// sys_date_id
					tempReception.setSys_ticket_id(ticketFormList.get(i).getSys_ticket_id()); 	// sys_ticket_id
					tempReception.setTra_amount(Integer.parseInt(traAmounts[i])); 				// sys_tra_amount
					tempReception.setTra_manager_id(sysManagerId); 								// tra_manager_id
					tempReception.setTra_memo(traMemo); 										// tra_memo

					tempReceptionList.add(tempReception);
					//					}
				}
			}

			// 仮受付データをセッションに保持
			session.setAttribute("tempReceptionList", tempReceptionList);
			setTotalPriceAndDate(model, sysFormId, Pub.createIntArrayList(traAmounts), sysDateId);
			model.addAttribute("sysDateId", sysDateId);
			model.addAttribute("traAmounts", Pub.createIntArrayList(traAmounts));
			model.addAttribute("traMemo", traMemo);
			break;
		}
		default:
			break;
		}
	}

	public void inputUser(Model model, String mode, String sysFormId, String sysDateId, String[] traAmounts, String traMemo,
			String custName, String custKanaName, String custMail, String custTel) {
		switch (mode) {
		case "inputUser": {
			// セッションから仮受付データを取得
			@SuppressWarnings("unchecked")
			ArrayList<DataEntity> tempReceptionList = (ArrayList<DataEntity>) session.getAttribute("tempReceptionList");

			// ステージデータを取得
			String sysStageId = (String)tempReceptionList.get(0).getSys_stage_id();
			DataEntity stageData = sql.getStageData("sys_stage_id", sysStageId);

			// 仮受付データをmodelに保持
			Integer[] tempTraAmounts = new Integer[tempReceptionList.size()];
			for(int i = 0; i < tempReceptionList.size(); i++) {
				tempTraAmounts[i] = tempReceptionList.get(i).getTra_amount();
			}
			model.addAttribute("stageData", stageData);
			model.addAttribute("traAmounts", Pub.createIntArrayList(tempTraAmounts));
			model.addAttribute("sysDateId", tempReceptionList.get(0).getSys_date_id());
			model.addAttribute("traMemo", tempReceptionList.get(0).getTra_memo());

			// フロント画面に渡すmodeに値を保持
			model.addAttribute("mode", "inputUser");
			break;
		}

		// 「戻る」ボタン押下時の処理
		case "back": {
			model.addAttribute("sysDateId", sysDateId);
			model.addAttribute("traAmounts", Pub.createIntArrayList(traAmounts));
			model.addAttribute("traMemo", traMemo);
			model.addAttribute("mode", "inputMemo");
			break;
		}

		// 個人情報の入力
		case "inputValue": {
			setTotalPriceAndDate(model, sysFormId, Pub.createIntArrayList(traAmounts), sysDateId);
			model.addAttribute("sysDateId", sysDateId);
			model.addAttribute("traAmounts", Pub.createIntArrayList(traAmounts));
			model.addAttribute("traMemo", traMemo);
			model.addAttribute("custName", custName);
			model.addAttribute("custKanaName", custKanaName);
			model.addAttribute("custMail", custMail);
			model.addAttribute("custTel", custTel);
			model.addAttribute("mode", "confiResult");
			model.addAttribute("nextMode", "inputUser");
			break;
		}

		default:
			break;
		}
	}

	public void inputLoginUser(Model model, String mode) {
		switch (mode) {
		case "inputLoginUser": {
			// セッションから仮受付データを取得
			@SuppressWarnings("unchecked")
			ArrayList<DataEntity> tempReceptionList = (ArrayList<DataEntity>) session.getAttribute("tempReceptionList");
			FormEntity formData = (FormEntity) session.getAttribute("formDataSession");

			// ステージデータを取得
			String sysStageId = (String)tempReceptionList.get(0).getSys_stage_id();
			DataEntity stageData = sql.getStageData("sys_stage_id", sysStageId);

			// 選択中の日付IDを取得
			String sysDateId = tempReceptionList.get(0).getSys_date_id();

			// お客様データを取得
			DataEntity custData = (DataEntity) session.getAttribute("custSession");

			// 仮受付データをmodelに保持
			Integer[] tempTraAmounts = new Integer[tempReceptionList.size()];
			for(int i = 0; i < tempReceptionList.size(); i++) {
				tempTraAmounts[i] = tempReceptionList.get(i).getTra_amount();
			}
			setTotalPriceAndDate(model, formData.getSysFormId(), Pub.createIntArrayList(tempTraAmounts), sysDateId);
			model.addAttribute("stageData", stageData);
			model.addAttribute("traAmounts", Pub.createIntArrayList(tempTraAmounts));
			model.addAttribute("sysDateId", tempReceptionList.get(0).getSys_date_id());
			model.addAttribute("traMemo", tempReceptionList.get(0).getTra_memo());
			model.addAttribute("custName", custData.getUser_name());
			model.addAttribute("custKanaName", custData.getUser_kana_name());
			model.addAttribute("custMail", custData.getUser_mail());
			model.addAttribute("custTel", custData.getUser_tell());

			// フロント画面に渡すmodeに値を保持
			model.addAttribute("nextMode", "inputLoginUser");
			break;
		}

		default:
			break;
		}
	}

	public String confiResult(Model model, String mode, String sysFormId, String sysDateId, String[] traAmounts, String traMemo, 
			String sysManagerId, String custName, String custKanaName, String custMail, String custTel) {
		switch (mode) {
		case "confiResult": {
			model.addAttribute("sysDateId", sysDateId);
			model.addAttribute("traAmounts", Pub.createIntArrayList(traAmounts));
			model.addAttribute("traMemo", traMemo);
			model.addAttribute("custName", custName);
			model.addAttribute("custKanaName", custKanaName);
			model.addAttribute("custMail", custMail);
			model.addAttribute("custTel", custTel);
			model.addAttribute("mode", "confiResult");
			break;
		}

		// 「戻る」ボタン押下時の処理
		case "back": {
			model.addAttribute("sysDateId", sysDateId);
			model.addAttribute("traAmounts", Pub.createIntArrayList(traAmounts));
			model.addAttribute("traMemo", traMemo);
			model.addAttribute("custName", custName);
			model.addAttribute("custKanaName", custKanaName);
			model.addAttribute("custMail", custMail);
			model.addAttribute("custTel", custTel);
			model.addAttribute("mode", "inputMemo");
			break;
		}

		// 「決定」ボタン押下時の処理
		case "inputUser": {
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
						tempReception.setTicket_name(ticketFormList.get(i).getTicket_name());
						tempReception.setTicket_price(ticketFormList.get(i).getTicket_price());
						tempReception.setTra_amount(Integer.parseInt(traAmounts[i])); // sys_tra_amount
						tempReception.setTra_manager_id(sysManagerId); // tra_manager_id
						tempReception.setTra_memo(traMemo); // tra_memo
						String sysNoneUserId = Pub.createUuid();
						tempReception.setSys_none_user_id(sysNoneUserId);
						tempReception.setNone_user_name(custName);
						tempReception.setNone_user_kana_name(custKanaName);
						tempReception.setNone_user_mail(custMail);
						tempReception.setNone_user_tell(custTel);

						tempReceptionList.add(tempReception);
					}
				}
			}

			model.addAttribute("mode", "confiResult");
			return registTransaction(model, "true", tempReceptionList);					
		}

		// 「決定」ボタン押下時の処理
		case "inputLoginUser": {
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
						tempReception.setSys_stage_id(stageData.getSys_stage_id());
						tempReception.setStage_name(stageData.getStage_name());
						tempReception.setSys_date_id(sysDateId);
						tempReception.setSys_ticket_id(ticketFormList.get(i).getSys_ticket_id());
						tempReception.setTicket_name(ticketFormList.get(i).getTicket_name());
						tempReception.setTicket_price(ticketFormList.get(i).getTicket_price());
						tempReception.setTra_amount(Integer.parseInt(traAmounts[i]));
						tempReception.setTra_manager_id(sysManagerId);
						tempReception.setTra_memo(traMemo);

						tempReceptionList.add(tempReception);
					}
				}
			}

			model.addAttribute("mode", "confiResult");
			return registTransaction(model, "false", tempReceptionList);		
		}
		default:
			break;
		}

		return null;
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

	public String loginCheck(Model model) {
		if(checkCustSession().equals("login")) {
			model.addAttribute("message", Env.reserveAccountLoginMessage);
			return "login";
		} else {
			return null;
		}
	}

	/*
	 * 予約登録を実行するメソッド
	 * noLogin ... true = ログインなしで登録, false = ログインユーザーで登録
	 */
	public String registTransaction(Model model, String noLogin, ArrayList<DataEntity> tempReceptionList) {

		// ユーザーがログインしていないなら、登録処理を中断する
		if(checkCustSession().equals("login") && noLogin.equals("false")) {
			model.addAttribute("message", Env.reserveAccountLoginMessage);
			return "login";
		}

		DataEntity custData = null;
		String noneUserName = "";
		String noneUserKanaName = "";
		String noneUserMail = "";
		String noneUserTel = "";

		// ログインなしの場合
		if(noLogin.equals("true")) {
			// none_userテーブルにユーザー情報を登録
			String sysNoneUserId = tempReceptionList.get(0).getSys_none_user_id();
			noneUserName = tempReceptionList.get(0).getNone_user_name();
			noneUserKanaName = tempReceptionList.get(0).getNone_user_kana_name();
			noneUserMail = tempReceptionList.get(0).getNone_user_mail();
			noneUserTel = tempReceptionList.get(0).getNone_user_tell();
			sql.addNoneUser(sysNoneUserId, noneUserName, noneUserKanaName, noneUserMail, noneUserTel);

			// 仮受付データにユーザー登録方式を格納
			for(DataEntity data: tempReceptionList) {
				data.setSys_user_id(sysNoneUserId);
				data.setNo_login("true");
			}
		}

		// ログインありの場合
		if(noLogin.equals("false")) {
			custData = (DataEntity) session.getAttribute("custSession");
			for(DataEntity data: tempReceptionList) {
				data.setSys_user_id(custData.getSys_user_id());
				data.setNo_login("false");
			}
		} else {
			custData = new DataEntity();
			custData.setUser_name(noneUserName);
		}

		// 予約完了メールの作成（1）
		String sysStageId = tempReceptionList.get(0).getSys_stage_id();
		DataEntity stageData = sql.getStageData("sys_stage_id", sysStageId);
		String groupName = stageData.getGroup_name();
		String groupMail = stageData.getGroup_mail();
		String custName = custData.getUser_name();
		String mailText = "";
		String mailTitle = Pub.createCompReserveMailTitle(groupName);

		// チケット予約登録実行
		for(int i = 0; i < tempReceptionList.size(); i++) {
			sql.addTransaction(tempReceptionList.get(i));

			// 受付完了メールの作成（2）
			DataEntity dateData = sql.getDateData(tempReceptionList.get(i).getSys_date_id());
			String stageDate = Pub.convertStrDateFromLocalDateTime(dateData.getSt_date());
			Integer traAmount = tempReceptionList.get(i).getTra_amount();
			String ticketName = tempReceptionList.get(i).getTicket_name();
			Integer ticketPrice = tempReceptionList.get(i).getTicket_price();
			if(noLogin.equals("false")) {
				mailText = Pub.createCompReserveMailMessage(groupName, groupMail, custName, stageDate, traAmount, stageData, false, ticketName, ticketPrice);
				mailSender.sendMail(custData.getUser_mail(), mailTitle, mailText);
			} else {
				mailText = Pub.createCompReserveMailMessage(groupName, groupMail, custName, stageDate, traAmount, stageData, true, ticketName, ticketPrice);
				mailSender.sendMail(noneUserMail, mailTitle, mailText);
			}
		}

		// 仮受付データを削除
		session.setAttribute("tempReceptionList", null);
		
		if(noLogin.equals("false")) {
			return "myPage";
		} else {
			model.addAttribute("mode", "noneUserReserveComp");
			return "noneUserReserveComp";
		}
	}
}
