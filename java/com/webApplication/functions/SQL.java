package com.webApplication.functions;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Component;

import com.webApplication.entity.DataEntity;
import com.webApplication.repository.MainRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SQL {
	private final HttpSession session;
	private final MainRepository mr;
	String column = "";
	String where = "";

	public ArrayList<DataEntity> getAuthorityList(String tableName){
		List<String> columns = null;
		if(tableName.equals("group_authorities")) {
			columns = mr.getGroupAuthorityTableColumns();
		} else if(tableName.equals("stage_authorities")) {
			columns = mr.getStageAuthorityTableColumns();
		}
		return mr.getDataList(tableName, columns, "");
	}

	public ArrayList<DataEntity> getLoginList(String tableName){
		List<String> columns = null;
		if(tableName.equals("group_login_list")) {
			columns = mr.getGroupeLoginListTableColumns();
		} else if(tableName.equals("stage_login_list")) {
			columns = mr.getStageLoginListTableColumns();
		}
		return mr.getDataList(tableName, columns, "");
	}

	public ArrayList<DataEntity> getPlaceList(String keyword){
		List<String> columns = new ArrayList<String>(Arrays.asList(
				"stage_place_name",
				"stage_place_address"
				));
		String where = " where stage_place_name like '%" + keyword + "%' group by stage_place_name, stage_place_address";
		return mr.getDataListBySelectColumn("stages", columns, where);
	}

	public DataEntity getUserData(String column, String anyValue) {
		DataEntity userData = (DataEntity)session.getAttribute("userSession");
		where = "left outer join groupes on sys_group_id = user_def_group where sys_user_id =  '" + userData.getSys_user_id() + "'";
		switch (column) {
		case "sys_user_id": {
			if(anyValue != null) {
				where = where.replace(userData.getSys_user_id(), anyValue);
			}
			break;
		}
		case "user_id": {
			where = where.replace("where sys_user_id =  '" + userData.getSys_user_id(), "where user_id =  '" + anyValue);
			break;
		}
		case "user_mail": {
			where = where.replace("where sys_user_id =  '" + userData.getSys_user_id(), "where user_mail =  '" + anyValue);
			break;
		}
		default:
		}

		List<String> columns = Stream.concat(mr.getUsersTableColumns().stream(), mr.getGroupeLoginListTableColumns().stream()).collect(Collectors.toList());
		columns = Stream.concat(columns.stream(), mr.getGroupesTableColumns().stream()).collect(Collectors.toList());
		return mr.getData("users u", columns, where);
	}

	public DataEntity getGroupData(String column, String anyId) {
		List<String> columns = mr.getGroupesTableColumns();
		where = " where <ID> = '" + anyId + "' limit 1";

		switch (column) {
		case "group_id": {
			where = where.replace("<ID>", "group_id");
			break;
		}
		case "sys_group_id": {
			where = where.replace("<ID>", "sys_group_id");
			break;
		}
		default : {
			break;
		}
		}	
		return mr.getData("groupes", columns, where);
	}

	public DataEntity getStageData(String column, String anyId) {
		List<String> columns = Stream.concat(mr.getStagesTableColumns().stream(), mr.getImagesTableColumns().stream()).collect(Collectors.toList());
		String joinTable = " left outer join images i1 on stage_flyer_1 = i1.sys_image_id"
				+ " left outer join images i2 on stage_flyer_2 = i2.sys_image_id";
		where = joinTable + " where <ID> = '" + anyId + "' limit 1";

		switch (column) {
		case "stage_id": {
			where = where.replace("<ID>", "stage_id");
			break;
		}
		case "sys_stage_id": {
			where = where.replace("<ID>", "sys_stage_id");
			break;
		}
		default : {
			break;
		}
		}   
		return mr.getData("stages", columns, where);
	}

	public DataEntity getFormData(String sysFormId) {
		List<String> columns = mr.getFormsTableColumns();
		where = " where sys_form_id = '" + sysFormId + "'";
		return mr.getData("forms", columns, where);
	}

	public DataEntity getDateData(String sysDateId) {
		List<String> columns = mr.getDatesTableColumns();
		where = " where sys_date_id = '" + sysDateId + "'";
		return mr.getData("dates", columns, where);
	}

	public DataEntity getTicketData(String sysTicketId) {
		List<String> columns = mr.getTicketsTableColumns();
		where = " where sys_ticket_id = '" + sysTicketId + "'";
		return mr.getData("tickets", columns, where);
	}

	public DataEntity getFormsetData(String sysStageId, String sysFormId, String sysTicketId, String sysDateId) {
		if(sysStageId == null) {
			DataEntity userData = (DataEntity)session.getAttribute("userSession");
			sysStageId = userData.getUser_def_stage();
		}

		List<String> columns = mr.getFormsetTableColumns();
		where = " where sys_stage_id = '" + sysStageId + "'";
		if(sysFormId != null) {
			where = where + " && sys_form_id = '" + sysFormId + "'";
		}
		if(sysTicketId != null) {
			where = where + " && sys_ticket_id = '" + sysTicketId + "'";
		}
		if(sysDateId != null) {
			where = where + " && sys_date_id = '" + sysDateId + "'";
		}
		return mr.getData("formset", columns, where);
	}

	public DataEntity getImageData(String column, String value, String contentType, String sysAnyId) {
		List<String> columns = mr.getImagesTableColumns();
		String where = " where <COLUMN> = '<VALUE>' && content_type = '" + contentType + "' && sys_any_id = '" + sysAnyId + "' limit 1";
		if(column != null) {
			where = where.replace("<COLUMN>", column);
			where = where.replace("<VALUE>", value);
		} else {
			where = where.replace("<COLUMN> = '<VALUE>' && ", "");
		}
		return mr.getData("images", columns, where);
	}

	public DataEntity getGroupLoginData(String sysUserId, String sysGroupId) {
		List<String> columns = mr.getGroupeLoginListTableColumns();

		String where = "where ";
		if(sysUserId != null) {
			where = where + " sys_user_id = '" + sysUserId + "' ";
		}
		if(sysUserId != null && sysGroupId != null) {
			where = where + " && ";
		}
		if(sysGroupId != null) {
			where = where + " sys_group_id = '" + sysGroupId + "' ";
		}
		return mr.getData("group_login_list", columns, where);
	}

	public DataEntity getStageLoginData(String sysUserId, String sysStageId) {
		List<String> columns = mr.getStageLoginListTableColumns();

		String where = "where ";
		if(sysUserId != null) {
			where = where + " sys_user_id = '" + sysUserId + "' ";
		}
		if(sysUserId != null && sysStageId != null) {
			where = where + " && ";
		}
		if(sysStageId != null) {
			where = where + " sys_stage_id = '" + sysStageId + "' ";
		}
		return mr.getData("stage_login_list", columns, where);
	}

	public ArrayList<DataEntity> getGroupDataList(String column, String keyword, Integer limit, Integer offset){
		DataEntity userData = (DataEntity)session.getAttribute("userSession");
		List<String> columns = Stream.concat(mr.getGroupesTableColumns().stream(), mr.getGroupAuthorityTableColumns().stream()).collect(Collectors.toList());
		columns = Stream.concat(columns.stream(), mr.getGroupeLoginListTableColumns().stream()).collect(Collectors.toList());

		String joinTable = " left outer join group_login_list gll on g.sys_group_id = gll.sys_group_id left outer join group_authorities ga on  ga.authority_id = gll.group_authority";
		String where = "";
		if(offset != null) {
			where = joinTable + " where " + column + " = '" + userData.getSys_user_id() + "' order by ga.authority_id desc limit " + limit + " offset " + offset * limit;
		} else {
			where = joinTable + " where " + column + " = '" + userData.getSys_user_id() + "'";
		}
		if(keyword != null) {
			where = where.replace(column + " = '" + userData.getSys_user_id() + "'", "sys_user_id = '" + userData.getSys_user_id() + "' && " + column + " like '%" + keyword + "%'");
		}
		return mr.getDataList("groupes g", columns, where);
	}

	public ArrayList<DataEntity> getStageDataList(String column, String keyword, Integer limit, Integer offset){
		DataEntity userData = (DataEntity)session.getAttribute("userSession");
		List<String> columns = Stream.concat(mr.getStagesTableColumns().stream(), mr.getStageAuthorityTableColumns().stream()).collect(Collectors.toList());
		columns = Stream.concat(columns.stream(), mr.getStageLoginListTableColumns().stream()).collect(Collectors.toList());
		columns = Stream.concat(columns.stream(), mr.getGroupesTableColumns().stream()).collect(Collectors.toList());

		String joinTable = " left outer join stage_login_list sll on s.sys_stage_id = sll.sys_stage_id left outer join stage_authorities sa on sa.authority_id = sll.stage_authority left outer join groupes g on s.sys_group_id = g.sys_group_id";
		String where = "";
		if(offset != null) {
			where = joinTable + " where " + column + " = '" + userData.getSys_user_id() + "' order by sa.authority_id desc limit " + limit + " offset " + offset * limit;
		} else {
			where = joinTable + " where " + column + " = '" + userData.getSys_user_id() + "'";
		}
		if(keyword != null) {
			where = where.replace(column + " = '" + userData.getSys_user_id() + "'", "sys_user_id = '" + userData.getSys_user_id() + "' && " + column + " like '%" + keyword + "%'");
		}
		return mr.getDataList("stages s", columns, where);
	}

	public ArrayList<DataEntity> getUserDataList(String column, String keyword, Integer limit, Integer offset){
		List<String> columns = mr.getUsersTableColumns();
		String where = "";
		if(offset != null) {
			where = " where " + column + " = '" + keyword + "' limit " + limit + " offset " + offset * limit;
		} else {
			where = " where " + column + " = '" + keyword + "'";
		}
		if(keyword != null) {
			where = where.replace(" = '" + keyword + "'", " like '%" + keyword + "%'");
		}
		return mr.getDataList("users u", columns, where);
	}

	public ArrayList<DataEntity> getMemberList(String tableName, String sysId){
		List<String> columns = null;
		String joinTable = "";
		String column = "";
		String where = "";
		switch (tableName) {
		case "group_login_list gll": {
			columns = Stream.concat(mr.getGroupesTableColumns().stream(), mr.getGroupeLoginListTableColumns().stream()).collect(Collectors.toList());
			columns = Stream.concat(columns.stream(), mr.getGroupAuthorityTableColumns().stream()).collect(Collectors.toList());
			columns = Stream.concat(columns.stream(), mr.getUsersTableColumns().stream()).collect(Collectors.toList());
			joinTable = " left outer join groupes g on gll.sys_group_id = g.sys_group_id left outer join group_authorities ga on gll.group_authority = ga.authority_id left outer join users u on u.sys_user_id = gll.sys_user_id ";
			column = "gll.sys_group_id";
			break;
		}
		case "stage_login_list sll": {
			columns = Stream.concat(mr.getStagesTableColumns().stream(), mr.getStageLoginListTableColumns().stream()).collect(Collectors.toList());
			columns = Stream.concat(columns.stream(), mr.getStageAuthorityTableColumns().stream()).collect(Collectors.toList());
			columns = Stream.concat(columns.stream(), mr.getUsersTableColumns().stream()).collect(Collectors.toList());
			joinTable = " left outer join stages s on sll.sys_stage_id = s.sys_stage_id left outer join stage_authorities sa on sll.stage_authority = sa.authority_id left outer join users u on u.sys_user_id = sll.sys_user_id ";
			column = "sll.sys_stage_id";
			break;
		}
		default:
			break;
		}
		where = joinTable + " where " + column + " = '" + sysId + "'";
		return mr.getDataList(tableName, columns, where);
	}

	public ArrayList<DataEntity> getFormDataList(String sysStageId){
		List<String> columns = mr.getFormsTableColumns();
		where = " where sys_stage_id = '" + sysStageId + "' order by date_st ";
		return mr.getDataList("forms", columns, where);
	}

	public ArrayList<DataEntity> getTicketDataList(String sysStageId){
		List<String> columns = mr.getTicketsTableColumns();
		where = " where sys_stage_id = '" + sysStageId + "' ";
		return mr.getDataList("tickets", columns, where);
	}

	public ArrayList<DataEntity> getFormsetDataList(String column, String sysAnyId){
		List<String> columns = mr.getFormsetTableColumns();
		where = " where " + column + " = '" + sysAnyId + "' ";
		return mr.getDataList("formset", columns, where);
	}

	public ArrayList<DataEntity> getDateDataList(String sysStageId){
		List<String> columns = mr.getDatesTableColumns();
		where = " where sys_stage_id = '" + sysStageId + "' ";
		return mr.getDataList("dates", columns, where);
	}
	
	public ArrayList<DataEntity> getFormsetDataListGroupByColumn(String sysStageId, String sysFormId, String column){
		List<String> columns = new ArrayList<String>(Arrays.asList(column));
		where = " where sys_stage_id = '" + sysStageId + "'";
		if(sysFormId != null) {
			where = where + " && sys_form_id = '" + sysFormId + "'";
		}
		where = where + " group by " + column;
		return mr.getDataListBySelectColumn("formset", columns, where);
	}

	public String getSysImageId(String column, String value, String contentType, String sysAnyId) {
		try {
			DataEntity image = getImageData(column, value, contentType, sysAnyId);
			return image.getSys_image_id();
		} catch (Exception e) {
			// TODO: handle exception
		}
		return null;
	}

	public void addGroupLoginList(String sysGroupId, String sysUserId) {
		List<String> columns = mr.getGroupeLoginListTableColumns();
		List<String> values = new ArrayList<String>(Arrays.asList(
				sysGroupId,		//sys_group_id
				sysUserId,		//sys_user_id
				1 + "",			//group_authority
				null,			//user_spe_name
				0 + ""			//user_spe_name_ev
				));
		String where = " where sys_user_id = '" + sysUserId + "' && sys_group_id = '" + sysGroupId + "'";
		if(mr.getData("group_login_list", columns, where) == null) {
			mr.insertData("group_login_list", columns, values);
		}
	}

	public void addStageLoginList(String sysStageId, String sysUserId) {
		List<String> columns = mr.getStageLoginListTableColumns();
		List<String> values = new ArrayList<String>(Arrays.asList(
				sysStageId,		//sys_stage_id
				sysUserId,		//sys_user_id
				1 + ""			//stage_authority
				));
		String where = " where sys_user_id = '" + sysUserId + "' && sys_stage_id = '" + sysStageId + "'";
		if(mr.getData("stage_login_list", columns, where) == null) {
			mr.insertData("stage_login_list", columns, values);
		}
	}

	public DataEntity addForm(String sysFormId, String sysStageId, String formName, LocalDateTime dateSt, LocalDateTime dateEd) {
		if(sysStageId == null) {
			DataEntity userData = (DataEntity)session.getAttribute("userSession");
			sysStageId = userData.getUser_def_stage();
		}
		List<String> columns = mr.getFormsTableColumns();
		List<String> values = new ArrayList<String>(Arrays.asList(
				sysFormId,		//sys_form_id
				sysStageId,		//sys_stage_id
				formName,		//form_name
				dateSt + "",	//date_st
				dateEd + ""		//date_ed
				));
		mr.insertData("forms", columns, values);

		return getFormData(sysFormId);
	}

	public DataEntity addDate(String sysDateId, String sysStageId, String stDate, Integer stSeat, String stInfo) {
		if(sysStageId == null) {
			DataEntity userData = (DataEntity)session.getAttribute("userSession");
			sysStageId = userData.getUser_def_stage();
		}
		List<String> columns = mr.getDatesTableColumns();
		List<String> values = new ArrayList<String>(Arrays.asList(
				sysDateId,		//sys_date_id
				sysStageId,		//sys_stage_id
				stDate,			//st_date
				stSeat + "",	//st_seat
				stInfo			//st_info
				));
		mr.insertData("dates", columns, values);

		return getDateData(sysDateId);
	}

	public DataEntity addTicket(String sysTicketId, String sysStageId, String ticketName, Integer ticketPrice) {
		if(sysStageId == null) {
			DataEntity userData = (DataEntity)session.getAttribute("userSession");
			sysStageId = userData.getUser_def_stage();
		}
		List<String> columns = mr.getTicketsTableColumns();
		List<String> values = new ArrayList<String>(Arrays.asList(
				sysTicketId,		//sys_ticket_id
				sysStageId,			//sys_stage_id
				ticketName,			//ticket_name
				ticketPrice + ""	//ticket_price
				));
		mr.insertData("tickets", columns, values);

		return getTicketData(sysTicketId);
	}

	public DataEntity addFormset(String sysStageId, String sysFormId, String sysTicketId, String sysDateId) {
		if(sysStageId == null) {
			DataEntity userData = (DataEntity)session.getAttribute("userSession");
			sysStageId = userData.getUser_def_stage();
		}
		List<String> columns = mr.getFormsTableColumns();
		List<String> values = new ArrayList<String>(Arrays.asList(
				sysStageId,		//sys_stage_id
				sysFormId,		//sys_form_id
				sysTicketId,	//sys_ticket_id
				sysDateId		//sys_date_id
				));
		mr.insertData("formset", columns, values);

		return getFormData(sysFormId);
	}

	public void addImage(String fileName, String fileType, byte[] bytes, String sysAnyId, String contentType) throws IOException {
		mr.insertImage(Pub.createUuid(), fileName, fileType, bytes, sysAnyId, contentType);
	}

	public DataEntity updateUserData(String column, String value, String sysUserId) {
		DataEntity userData = (DataEntity)session.getAttribute("userSession");
		if(sysUserId != null) {
			userData = getUserData("sys_user_id", sysUserId);
		}

		String where = " where sys_user_id = '" + userData.getSys_user_id() + "'";
		mr.updateData("users", column, value, where);

		return getUserData("sys_user_id", userData.getSys_user_id());
	}

	public DataEntity updateStageData(String column, String value, String sysStageId) {
		if(sysStageId == null) {
			DataEntity userData = (DataEntity)session.getAttribute("userSession");
			sysStageId = userData.getUser_def_stage();
		}

		String where = " where sys_stage_id = '" + sysStageId + "'";
		mr.updateData("stages", column, value, where);

		return getStageData("sys_stage_id", sysStageId);
	}

	public DataEntity updateGroupData(String column, String value, String sysGroupId) {
		if(sysGroupId == null) {
			DataEntity userData = (DataEntity)session.getAttribute("userSession");
			sysGroupId = userData.getUser_def_group();
		}

		String where = " where sys_group_id = '" + sysGroupId + "'";
		mr.updateData("groupes", column, value, where);

		return getGroupData("sys_group_id", sysGroupId);
	}

	public DataEntity updateFormsetData(String column, String value, String sysStageId, String sysFormId, String sysTicketId, String sysDateId) {
		if(sysStageId == null) {
			DataEntity userData = (DataEntity)session.getAttribute("userSession");
			sysStageId = userData.getUser_def_stage();
		}

		String where = " where sys_stage_id = '" + sysStageId + "'";
		if(sysFormId != null) {
			where = where + " && sys_form_id = '" + sysFormId + "'";
		}
		if(sysTicketId != null) {
			where = where + " && sys_ticket_id = '" + sysTicketId + "'";
		}
		if(sysDateId != null) {
			where = where + " && sys_date_id = '" + sysDateId + "'";
		}
		mr.updateData("formset", column, value, where);

		return getFormsetData(sysStageId, sysFormId, sysTicketId, sysDateId);
	}

	public DataEntity updateUserDefGroup(String column, String id, boolean newGroup) {
		DataEntity userData = (DataEntity)session.getAttribute("userSession");
		if(getGroupData(column, id) != null) {
			DataEntity groupData = getGroupData(column, id);

			where = " where sys_user_id = '" + userData.getSys_user_id() + "'";
			mr.updateData("users", "user_def_group", groupData.getSys_group_id(), where);

			if(newGroup == true) {
				addGroupLoginList(groupData.getSys_group_id(), userData.getSys_user_id());
			}
			return getUserData("sys_user_id", null);
		} else {
			return null;
			//model.addAttribute("message", "団体登録時にエラーが発生しました");
		}
	}

	public DataEntity updateUserDefStage(String column, String id, boolean newStage) {
		DataEntity userData = (DataEntity)session.getAttribute("userSession");
		if(getStageData(column, id) != null) {
			DataEntity stageData = getStageData(column, id);

			where = " where sys_user_id = '" + userData.getSys_user_id() + "'";
			mr.updateData("users", "user_def_stage", stageData.getSys_stage_id(), where);

			if(newStage == true) {
				addStageLoginList(stageData.getSys_stage_id(), userData.getSys_user_id());
			}
			return getUserData("sys_user_id", null);
		} else {
			return null;
			//model.addAttribute("message", "公演登録時にエラーが発生しました");
		}
	}

	public ArrayList<DataEntity> updateLoginList(String tableName, String column, String value, String sysUserId){
		DataEntity userData = (DataEntity)session.getAttribute("userSession");
		if(sysUserId != null) {
			userData = getUserData("sys_user_id", sysUserId);
		}

		String where = " where sys_user_id = '" + userData.getSys_user_id() + "'";
		mr.updateData(tableName, column, value, where);

		return getLoginList(tableName);
	}

	public void updateImage(String sysImageId, String fileName, String fileType, byte[] binaryData) {
		where = " where sys_image_id = '" + sysImageId + "'";
		mr.updateData("images", "file_name", fileName, where);
		mr.updateData("images", "file_type", fileType, where);
		mr.updateData("images", "binary_data", binaryData, where);
	}

	public ArrayList<DataEntity> deleteLoginData(String tableName, String sysUserId, String sysAnyId){
		where = " where ";
		if(sysUserId != null) {
			where = where + " sys_user_id = '" + sysUserId + "'";
		}
		if(sysUserId != null && sysAnyId != null) {
			where = where + " && ";
		}
		if(sysAnyId != null) {
			if(tableName.equals("group_login_list")) {
				where = where + " sys_group_id = '" + sysAnyId + "'";
			} else if(tableName.equals("stage_login_list")) {
				where = where + " sys_stage_id = '" + sysAnyId + "'";
			}
		}
		mr.deleteData(tableName, where);
		return getLoginList(tableName);
	}

	public void deleteFormsetData(String sysStageId, String sysFormId, String column) {
		where = " where sys_stage_id = '" + sysStageId + "'";
		if(sysFormId != null) {
			where = where + " && sys_form_id = '" + sysFormId + "'";
		}
		if(column != null) {
			where = where + " && " + column + " = null";
		}
		mr.deleteData("formset", where);
	}
}