package com.webApplication.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import com.webApplication.entity.DataEntity;
import com.webApplication.functions.Pub;
import com.webApplication.repository.MainRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CreateGroupService {
	private final HttpSession session;
	private final MainRepository mr;
	
	public void setPageInfo(Model model) {
		model.addAttribute("title2", "団体の新規作成");
	}
	
	public String inputGroupId(Model model, String groupId) {
		model.addAttribute("groupId", groupId);
		List<String> column = mr.getGroupesTableColumns();
		String where = " where group_id = '" + groupId + "'";
		if(mr.getData("groupes", column, where) != null) {
			model.addAttribute("message", "その団体IDは既に使用されています");
			model.addAttribute("mode", "inputGroupId");
			return "inputGroupId";
		}
		model.addAttribute("mode", "inputGroupData");
		return "inputGroupData";
	}
	
	public String inputGroupData(Model model, String back, String groupId, String groupName, String groupKanaName, String groupPass, String rePass, String groupMail) {
		setGroupData(model, groupId, groupName, groupKanaName, groupPass, groupMail);
		if(back != null) {
			if(back.equals("back")) {
				model.addAttribute("mode", "inputGroupData");
				return "inputGroupId";
			}
		}
		if(!groupPass.equals(rePass)) {
			model.addAttribute("mode", "inputGroupData");
			model.addAttribute("message", "パスワードが一致していません");
			return "inputGroupData";
		}
		model.addAttribute("mode", "confiResult");
		return "confiResult";
	}
	
	public String confiResult(Model model, String back, String groupId, String groupName, String groupKanaName, String groupPass, String rePass, String groupMail) {
		setGroupData(model, groupId, groupName, groupKanaName, groupPass, groupMail);
		if(back != null) {
			if(back.equals("back")) {
				model.addAttribute("mode", "inputGroupData");
				return "inputGroupData";
			}
		}
		createGroup(model, setInputDataObject(groupId, groupName, groupKanaName, groupPass, groupMail));
		setSession(model, groupId);
		return "index";
	}
	
	private void setGroupData(Model model, String groupId, String groupName, String groupKanaName, String groupPass, String groupMail) {
		model.addAttribute("groupId", groupId);
		model.addAttribute("groupName", groupName);
		model.addAttribute("groupKanaName", groupKanaName);
		model.addAttribute("groupPass", groupPass);
		model.addAttribute("groupMail", groupMail);
	}
	
	private DataEntity setInputDataObject(String groupId, String groupName, String groupKanaName, String groupPass, String groupMail) {
		DataEntity groupData = new DataEntity();
		groupData.setGroup_id(groupId);
		groupData.setGroup_name(groupName);
		groupData.setGroup_kana_name(groupKanaName);
		groupData.setGroup_pass(groupPass);
		groupData.setGroup_mail(groupMail);
		return groupData;
	}
	
	private void createGroup(Model model, DataEntity data) {
		List<String> columns = mr.getGroupesTableColumns();
		List<String> values = new ArrayList<String>(Arrays.asList(
				Pub.createUuid(),				//sys_group_id
				data.getGroup_id(),				//group_id
				data.getGroup_name(),			//group_name
				data.getGroup_kana_name(),		//group_kana_name
				data.getGroup_pass(),			//group_pass
				data.getGroup_mail(),			//group_mail
				Pub.getCurrentDate() + "",		//group_cre_date
				Pub.getCurrentDate() + ""		//group_last_login
				));
		mr.insertData("groupes", columns, values);
	}
	
	private void setSession(Model model, String groupId) {
		DataEntity userData = (DataEntity)session.getAttribute("userSession");
		String where = " where group_id = '" + groupId + "'";
		List<String> columns = mr.getGroupesTableColumns();
		if(mr.getData("groupes", columns, where) != null) {
			DataEntity groupData = mr.getData("groupes", columns, where);
			where = " where sys_user_id = '" + userData.getSys_user_id() + "'";
			mr.updateData("users", "user_def_group", groupData.getSys_group_id(), where);
			addGroupLoginList(model, groupData.getSys_group_id(), userData.getSys_user_id());
			
			where = "left outer join groupes on sys_group_id = user_def_group where sys_user_id =  '" + userData.getSys_user_id() + "'";
			columns = Stream.concat(mr.getUsersTableColumns().stream(), mr.getGroupe_login_listTableColumns().stream()).collect(Collectors.toList());
			columns = Stream.concat(columns.stream(), mr.getGroupesTableColumns().stream()).collect(Collectors.toList());
			session.setAttribute("userSession", mr.getData("users u", columns, where));
		} else {
			model.addAttribute("message", "団体登録時にエラーが発生しました");
		}
	}
	
	private void addGroupLoginList(Model model, String sysGroupId, String sysUserId) {
		List<String> columns = mr.getGroupe_login_listTableColumns();
		List<String> values = new ArrayList<String>(Arrays.asList(
				sysGroupId,		//sys_group_id
				sysUserId,		//sys_user_id
				1 + "",			//group_authority
				null,			//user_spe_name
				0 + ""			//user_spe_name_ev
				));
		mr.insertData("group_login_list", columns, values);
	}
}
