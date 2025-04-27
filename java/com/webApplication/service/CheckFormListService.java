package com.webApplication.service;

import java.util.ArrayList;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import com.webApplication.entity.DataEntity;
import com.webApplication.entity.FormEntity;
import com.webApplication.functions.SQL;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CheckFormListService {
	private final HttpSession session;
//	private final MainRepository mr;
	private final SQL sql;
	
	public void setPageInfo(Model model) {
		DataEntity userData = (DataEntity)session.getAttribute("userSession");
		DataEntity stageData = (DataEntity)session.getAttribute("defStSession");
		model.addAttribute("title2", "予約フォーム一覧");
		model.addAttribute("memberFormList", getMemberFormList(userData.getUser_def_stage(), stageData.getStage_url_title(), stageData.getStage_name()));
	}
	
	private ArrayList<ArrayList<FormEntity>> getMemberFormList(String sysStageId, String stageUrlTitle, String stageName){
		ArrayList<DataEntity> formList = getFormList(sysStageId);
		ArrayList<DataEntity> memberList = getMemberList(sysStageId);
		ArrayList<ArrayList<FormEntity>> memberFormList = setMemberFormList(formList, memberList, sysStageId, stageUrlTitle, stageName);
		return memberFormList;
		
	}
	
	private ArrayList<DataEntity> getFormList(String sysStageId){
		return sql.getFormDataList(sysStageId);
	}
	
	private ArrayList<DataEntity> getMemberList(String sysStageId){
		return sql.getMemberList("stage_login_list sll", sysStageId);
	}
	
	private ArrayList<ArrayList<FormEntity>> setMemberFormList(ArrayList<DataEntity> formList, ArrayList<DataEntity> memberList, 
			String sysStageId, String stageUrlTitle, String stageName) {
		ArrayList<ArrayList<FormEntity>> memberFormList = new ArrayList<ArrayList<FormEntity>>();
		for(DataEntity form : formList) {
			memberFormList.add(setFormEntity(form, memberList, sysStageId, stageUrlTitle, stageName));
		}
		return memberFormList;
	}
	
	private ArrayList<FormEntity> setFormEntity(DataEntity form, ArrayList<DataEntity> memberList, String sysStageId, String stageUrlTitle, String stageName){
		ArrayList<FormEntity> memberForm = new ArrayList<FormEntity>();
		
		FormEntity fe = new FormEntity();
		String formUrl = fe.getFormUrl().replace("<STAGE_URL_TITLE>", stageUrlTitle).replace("<SYS_FORM_ID>", form.getSys_form_id()).replace("&ma=<SYS_USER_ID>", "");

		fe.setFormUrl(formUrl);
		fe.setUserName("一般");
		fe.setSysStageId(sysStageId);
		fe.setStageName(stageName);
		fe.setSysFormId(form.getSys_form_id());
		fe.setFormName(form.getForm_name());
		memberForm.add(fe);
		
		for(DataEntity member : memberList) {
			fe = new FormEntity();
			formUrl = fe.getFormUrl().replace("<STAGE_URL_TITLE>", stageUrlTitle).replace("<SYS_FORM_ID>", form.getSys_form_id()).replace("<SYS_USER_ID>", member.getSys_user_id());

			fe.setFormUrl(formUrl);
			fe.setSysUserId(member.getSys_user_id());
			fe.setUserName(member.getUser_name());
			fe.setSysStageId(sysStageId);
			fe.setStageName(stageName);
			fe.setSysFormId(form.getSys_form_id());
			fe.setFormName(form.getForm_name());
			memberForm.add(fe);
		}
		return memberForm;
	}
}
