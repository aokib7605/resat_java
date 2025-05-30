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
	String joinTable = "";
	String where = "";
	String orderBy = "";
	List<String> columns = null;

	public ArrayList<DataEntity> getAuthorityList(String tableName){
		if(tableName.equals("group_authorities")) {
			columns = mr.getGroupAuthorityTableColumns();
		} else if(tableName.equals("stage_authorities")) {
			columns = mr.getStageAuthorityTableColumns();
		}
		return mr.getDataList(tableName, columns, "");
	}

	public ArrayList<DataEntity> getLoginList(String tableName){
		if(tableName.equals("group_login_list")) {
			columns = mr.getGroupeLoginListTableColumns();
		} else if(tableName.equals("stage_login_list")) {
			columns = mr.getStageLoginListTableColumns();
		}
		return mr.getDataList(tableName, columns, "");
	}

	public ArrayList<DataEntity> getPlaceList(String keyword){
		columns = new ArrayList<String>(Arrays.asList(
				"stage_place_name",
				"stage_place_address"
				));
		where = " where stage_place_name like '%" + keyword + "%' group by stage_place_name, stage_place_address";
		return mr.getDataListBySelectColumn("stages", columns, null, where);
	}

	/**
	 * 
	 * @param where 句で検索するカラム名
	 * @param カラムに対してのvalue
	 * @return userData
	 */
	public DataEntity getUserData(String column, String anyValue) {
		columns = Stream.concat(mr.getUsersTableColumns().stream(), mr.getGroupeLoginListTableColumns().stream()).collect(Collectors.toList());
		columns = Stream.concat(columns.stream(), mr.getGroupesTableColumns().stream()).collect(Collectors.toList());
		joinTable = " left outer join groupes on sys_group_id = user_def_group ";
		where = " where <COLUMN> =  '" + anyValue + "'";
		where = joinTable + where.replace("<COLUMN>", column);
		return mr.getData("users u", columns, where);
	}

	public DataEntity reGetUserData(String column, String anyValue) {
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

		columns = Stream.concat(mr.getUsersTableColumns().stream(), mr.getGroupeLoginListTableColumns().stream()).collect(Collectors.toList());
		columns = Stream.concat(columns.stream(), mr.getGroupesTableColumns().stream()).collect(Collectors.toList());
		return mr.getData("users u", columns, where);
	}

	public DataEntity getGroupData(String column, String anyId) {
		columns = mr.getGroupesTableColumns();
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
		columns = Stream.concat(mr.getStagesTableColumns().stream(), mr.getImagesTableColumns().stream()).collect(Collectors.toList());
		joinTable = " left outer join images i1 on stage_flyer_1 = i1.sys_image_id"
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
		columns = mr.getFormsTableColumns();
		where = " where sys_form_id = '" + sysFormId + "'";
		return mr.getData("forms", columns, where);
	}

	public DataEntity getDateData(String sysDateId) {
		columns = mr.getDatesTableColumns();
		where = " where sys_date_id = '" + sysDateId + "'";
		return mr.getData("dates", columns, where);
	}

	public DataEntity getTicketData(String sysTicketId) {
		columns = mr.getTicketsTableColumns();
		where = " where sys_ticket_id = '" + sysTicketId + "'";
		return mr.getData("tickets", columns, where);
	}

	public DataEntity getFormsetData(String sysStageId, String sysFormId, String sysTicketId, String sysDateId) {
		if(sysStageId == null) {
			DataEntity userData = (DataEntity)session.getAttribute("userSession");
			sysStageId = userData.getUser_def_stage();
		}

		columns = mr.getFormsetTableColumns();
		where = " where sys_stage_id = '" + sysStageId + "'";
		if(sysFormId != null) {
			where = where + " and sys_form_id = '" + sysFormId + "'";
		}
		if(sysTicketId != null) {
			where = where + " and sys_ticket_id = '" + sysTicketId + "'";
		}
		if(sysDateId != null) {
			where = where + " and sys_date_id = '" + sysDateId + "'";
		}
		return mr.getData("formset", columns, where);
	}

	public DataEntity getImageData(String column, String value, String contentType, String sysAnyId) {
		columns = mr.getImagesTableColumns();
		where = " where <COLUMN> = '<VALUE>' and content_type = '" + contentType + "' and sys_any_id = '" + sysAnyId + "' limit 1";
		if(column != null) {
			where = where.replace("<COLUMN>", column);
			where = where.replace("<VALUE>", value);
		} else {
			where = where.replace("<COLUMN> = '<VALUE>' and ", "");
		}
		return mr.getData("images", columns, where);
	}

	public DataEntity getGroupLoginData(String sysUserId, String sysGroupId) {
		columns = mr.getGroupeLoginListTableColumns();

		where = "where ";
		if(sysUserId != null) {
			where = where + " sys_user_id = '" + sysUserId + "' ";
		}
		if(sysUserId != null && sysGroupId != null) {
			where = where + " and ";
		}
		if(sysGroupId != null) {
			where = where + " sys_group_id = '" + sysGroupId + "' ";
		}
		return mr.getData("group_login_list", columns, where);
	}

	public DataEntity getStageLoginData(String sysUserId, String sysStageId) {
		columns = mr.getStageLoginListTableColumns();

		where = "where ";
		if(sysUserId != null) {
			where = where + " sys_user_id = '" + sysUserId + "' ";
		}
		if(sysUserId != null && sysStageId != null) {
			where = where + " and ";
		}
		if(sysStageId != null) {
			where = where + " sys_stage_id = '" + sysStageId + "' ";
		}
		return mr.getData("stage_login_list", columns, where);
	}

	public ArrayList<DataEntity> getGroupDataList(String column, String keyword, Integer limit, Integer offset){
		DataEntity userData = (DataEntity)session.getAttribute("userSession");
		columns = Stream.concat(mr.getGroupesTableColumns().stream(), mr.getGroupAuthorityTableColumns().stream()).collect(Collectors.toList());
		columns = Stream.concat(columns.stream(), mr.getGroupeLoginListTableColumns().stream()).collect(Collectors.toList());

		joinTable = " left outer join group_login_list gll on g.sys_group_id = gll.sys_group_id left outer join group_authorities ga on  ga.authority_id = gll.group_authority";
		if(offset != null) {
			where = joinTable + " where " + column + " = '" + userData.getSys_user_id() + "' order by ga.authority_id desc limit " + limit + " offset " + offset * limit;
		} else {
			where = joinTable + " where " + column + " = '" + userData.getSys_user_id() + "'";
		}
		if(keyword != null) {
			where = where.replace(column + " = '" + userData.getSys_user_id() + "'", "sys_user_id = '" + userData.getSys_user_id() + "' and " + column + " like '%" + keyword + "%'");
		}
		return mr.getDataList("groupes g", columns, where);
	}

	public ArrayList<DataEntity> getStageDataList(String column, String keyword, Integer limit, Integer offset){
		DataEntity userData = (DataEntity)session.getAttribute("userSession");
		columns = Stream.concat(mr.getStagesTableColumns().stream(), mr.getStageAuthorityTableColumns().stream()).collect(Collectors.toList());
		columns = Stream.concat(columns.stream(), mr.getStageLoginListTableColumns().stream()).collect(Collectors.toList());
		columns = Stream.concat(columns.stream(), mr.getGroupesTableColumns().stream()).collect(Collectors.toList());

		joinTable = " left outer join stage_login_list sll on s.sys_stage_id = sll.sys_stage_id left outer join stage_authorities sa on sa.authority_id = sll.stage_authority left outer join groupes g on s.sys_group_id = g.sys_group_id";
		if(offset != null) {
			where = joinTable + " where " + column + " = '" + userData.getSys_user_id() + "' order by sa.authority_id desc limit " + limit + " offset " + offset * limit;
		} else {
			where = joinTable + " where " + column + " = '" + userData.getSys_user_id() + "'";
		}
		if(keyword != null) {
			where = where.replace(column + " = '" + userData.getSys_user_id() + "'", "sys_user_id = '" + userData.getSys_user_id() + "' and " + column + " like '%" + keyword + "%'");
		}
		return mr.getDataList("stages s", columns, where);
	}

	public ArrayList<DataEntity> getUserDataList(String column, String keyword, Integer limit, Integer offset){
		columns = mr.getUsersTableColumns();
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
		columns = mr.getFormsTableColumns();
		where = " where sys_stage_id = '" + sysStageId + "' order by date_st ";
		return mr.getDataList("forms", columns, where);
	}

	public ArrayList<DataEntity> getTicketDataList(String sysStageId){
		columns = mr.getTicketsTableColumns();
		where = " where sys_stage_id = '" + sysStageId + "' ";
		return mr.getDataList("tickets", columns, where);
	}

	public ArrayList<DataEntity> getFormsetDataList(String column, String sysAnyId){
		columns = mr.getFormsetTableColumns();
		where = " where " + column + " = '" + sysAnyId + "' ";
		return mr.getDataList("formset", columns, where);
	}

	public ArrayList<DataEntity> getDateDataList(String sysStageId){
		columns = mr.getDatesTableColumns();
		where = " where sys_stage_id = '" + sysStageId + "' ";
		return mr.getDataList("dates", columns, where);
	}

	public ArrayList<DataEntity> getFormsetDataListGroupByColumn(String sysStageId, String sysFormId, String column){
		if(column.equals("sys_ticket_id")) {
			joinTable = " left outer join tickets t on ft." + column + " = t." + column + "";
			orderBy = " order by t.ticket_price";
			columns = new ArrayList<String>(Arrays.asList("ft."+ column, "t.ticket_name", "t.ticket_price"));
		} else {
			joinTable = " left outer join dates d on ft." + column + " = d." + column + "";
			orderBy = " order by d.st_date";
			columns = new ArrayList<String>(Arrays.asList("ft."+ column, "d.st_date", "d.st_seat", "d.st_info"));
		}

		where = " where ft.sys_stage_id = '" + sysStageId + "'";
		if(sysFormId != null) {
			where = where + " and ft.sys_form_id = '" + sysFormId + "'";
		}

		where = joinTable + where + " and ft." + column + " is not null group by " + column + orderBy;
		return mr.getDataListBySelectColumn("formset ft", columns, null, where);
	}

	public ArrayList<DataEntity> getCastOrStaffDataList(String mode, String sysStageId, String sysUserId, String plusColumn, String plusValue){
		columns = mr.getUsersTableColumns();
		String tableName = "";
		joinTable = " left outer join users u on <tableMiniName>.sys_user_id = u.sys_user_id ";
		where = " where <tableMiniName>.sys_stage_id = '" + sysStageId + "' ";
		String orderBy = " order by <tableMiniNameAndColumn>, <tableMiniName>.user_sort_num ";

		if(sysUserId != null) {
			where = where + " and <tableMiniName>.sys_user_id = '" + sysUserId + "' ";
		}

		if(plusColumn != null) {
			where = where + " and " + plusColumn + " = '" + plusValue + "' ";
		}

		if(mode.equals("cast")) {
			tableName = "cast c";
			columns = Stream.concat(columns.stream(), mr.getCastTableColumns().stream()).collect(Collectors.toList());
			joinTable = joinTable.replace("<tableMiniName>", "c");
			where = where.replace("<tableMiniName>", "c");
			orderBy = orderBy.replace("<tableMiniNameAndColumn>", "c.cast_chara_name").replace("<tableMiniName>", "c");
		}

		if(mode.equals("staff")) {
			tableName = "staff s";
			columns = Stream.concat(columns.stream(), mr.getStaffTableColumns().stream()).collect(Collectors.toList());
			joinTable = joinTable.replace("<tableMiniName>", "s");
			where = where.replace("<tableMiniName>", "s");
			orderBy = orderBy.replace("<tableMiniNameAndColumn>", "s.staff_dep_name").replace("<tableMiniName>", "s");
		}
		where = joinTable + where + orderBy;
		return mr.getDataList(tableName, columns, where);
	}

	public ArrayList<DataEntity> getCastOrStaffDataListGroupByColumn(String mode, String sysStageId){
		List<String> getColumns = null;
		String tableName = "";

		joinTable = " left outer join users u on <tableMiniName>.sys_user_id = u.sys_user_id ";
		where = " where <tableMiniName>.sys_stage_id = '" + sysStageId + "' ";
		String groupBy = " group by <tableMiniNameAndColumn> ";
		String orderBy = " order by min(<tableMiniNameAndColumn>) ";

		if(mode.equals("cast")) {
			tableName = "cast c";
			columns = new ArrayList<String>(Arrays.asList("c.cast_chara_name", "GROUP_CONCAT( IFNULL(u.user_stage_name, u.user_name) ORDER BY c.user_sort_num SEPARATOR ', ') AS user_names"));
			getColumns = new ArrayList<String>(Arrays.asList("cast_chara_name", "user_names"));
			joinTable = joinTable.replace("<tableMiniName>", "c");
			where = where.replace("<tableMiniName>", "c");
			groupBy = groupBy.replace("<tableMiniNameAndColumn>", "c.cast_chara_name");
			orderBy = orderBy.replace("<tableMiniNameAndColumn>", "c.cast_sort_num");
		}
		if(mode.equals("staff")) {
			tableName = "staff s";
			columns = new ArrayList<String>(Arrays.asList("s.staff_dep_name", "GROUP_CONCAT( IFNULL(u.user_stage_name, u.user_name) ORDER BY s.user_sort_num SEPARATOR ', ') AS user_names"));
			getColumns = new ArrayList<String>(Arrays.asList("staff_dep_name", "user_names"));
			joinTable = joinTable.replace("<tableMiniName>", "s");
			where = where.replace("<tableMiniName>", "s");
			groupBy = groupBy.replace("<tableMiniNameAndColumn>", "s.staff_dep_name");
			orderBy = orderBy.replace("<tableMiniNameAndColumn>", "s.staff_sort_num");
		}

		where = joinTable + where + groupBy + orderBy;	
		return mr.getDataListBySelectColumn(tableName, columns, getColumns, where );
	}

	public ArrayList<DataEntity> getCastCharaNames(String sysStageId){
		columns = new ArrayList<String>(Arrays.asList("cast_chara_name", "cast_sort_num"));
		where = " where sys_stage_id = '" + sysStageId + "' ";
		String groupBy = " group by cast_chara_name, cast_sort_num ";
		String orderBy = " order by min(cast_sort_num) ";
		return mr.getDataListBySelectColumn("cast", columns, null, where + groupBy + orderBy);
	}

	public ArrayList<DataEntity> getStaffDepNames(String sysStageId){
		columns = new ArrayList<String>(Arrays.asList("staff_dep_name", "staff_sort_num"));
		where = " where sys_stage_id = '" + sysStageId + "' ";
		String groupBy = " group by staff_dep_name, staff_sort_num ";
		String orderBy = " order by min(staff_sort_num) ";
		return mr.getDataListBySelectColumn("staff", columns, null, where + groupBy + orderBy);
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

	public ArrayList<DataEntity> getTransactionList(String sysStageId){
		columns = Stream.concat(mr.getTransactionsTableColumns().stream(), mr.getDatesTableColumns().stream()).collect(Collectors.toList());
		columns = Stream.concat(columns.stream(), mr.getTicketsTableColumns().stream()).collect(Collectors.toList());
		columns = Stream.concat(columns.stream(), mr.getUsersTableColumns().stream()).collect(Collectors.toList());
		joinTable = " left outer join dates d on tra.sys_date_id = d.sys_date_id ";
		joinTable = joinTable + " left outer join tickets t on tra.sys_ticket_id = t.sys_ticket_id ";
		joinTable = joinTable + " left outer join users m on tra.tra_manager_id = m.sys_user_id ";
		joinTable = joinTable + " left outer join users u on tra.sys_user_id = u.sys_user_id ";
		where = " where tra.sys_stage_id = '" + sysStageId + "' ";
		String orderBy = " order by st_date";
		where = joinTable + where + orderBy;
		return mr.getDataList("transactions tra", columns, where);
	}

	public ArrayList<DataEntity> getCustReserveList(String sysUserId){
		columns = Stream.concat(mr.getTransactionsTableColumns().stream(), mr.getDatesTableColumns().stream()).collect(Collectors.toList());
		columns = Stream.concat(columns.stream(), mr.getTicketsTableColumns().stream()).collect(Collectors.toList());
		columns = Stream.concat(columns.stream(), mr.getStagesTableColumns().stream()).collect(Collectors.toList());
		columns = Stream.concat(columns.stream(), mr.getUsersTableColumns().stream()).collect(Collectors.toList());
		columns = Stream.concat(columns.stream(), mr.getGroupesTableColumns().stream()).collect(Collectors.toList());
		joinTable = " left outer join stages s on tra.sys_stage_id = s.sys_stage_id ";
		joinTable = joinTable + " left outer join dates d on tra.sys_date_id = d.sys_date_id ";
		joinTable = joinTable + " left outer join tickets t on tra.sys_ticket_id = t.sys_ticket_id ";
		joinTable = joinTable + " left outer join users u on tra.tra_manager_id = u.sys_user_id ";
		joinTable = joinTable + " left outer join groupes g on s.sys_group_id = g.sys_group_id ";
		where = " where tra.sys_user_id = '" + sysUserId + "' and current_date() <= st_date";
		String orderBy = " order by st_date ";
		where = joinTable + where + orderBy;
		return mr.getDataList("transactions tra", columns, where);
	}

	public ArrayList<DataEntity> getCustPastReserveList(String sysUserId){
		columns = Stream.concat(mr.getTransactionsTableColumns().stream(), mr.getDatesTableColumns().stream()).collect(Collectors.toList());
		columns = Stream.concat(columns.stream(), mr.getTicketsTableColumns().stream()).collect(Collectors.toList());
		columns = Stream.concat(columns.stream(), mr.getStagesTableColumns().stream()).collect(Collectors.toList());
		columns = Stream.concat(columns.stream(), mr.getUsersTableColumns().stream()).collect(Collectors.toList());
		columns = Stream.concat(columns.stream(), mr.getGroupesTableColumns().stream()).collect(Collectors.toList());
		joinTable = " left outer join stages s on tra.sys_stage_id = s.sys_stage_id ";
		joinTable = joinTable + " left outer join dates d on tra.sys_date_id = d.sys_date_id ";
		joinTable = joinTable + " left outer join tickets t on tra.sys_ticket_id = t.sys_ticket_id ";
		joinTable = joinTable + " left outer join users u on tra.tra_manager_id = u.sys_user_id ";
		joinTable = joinTable + " left outer join groupes g on s.sys_group_id = g.sys_group_id ";
		where = " where tra.sys_user_id = '" + sysUserId + "' and current_date() > st_date";
		String orderBy = " order by st_date ";
		where = joinTable + where + orderBy;
		return mr.getDataList("transactions tra", columns, where);
	}

	public void addGroupLoginList(String sysGroupId, String sysUserId) {
		columns = mr.getGroupeLoginListTableColumns();
		List<String> values = new ArrayList<String>(Arrays.asList(
				sysGroupId,		//sys_group_id
				sysUserId,		//sys_user_id
				1 + "",			//group_authority
				null,			//user_spe_name
				0 + ""			//user_spe_name_ev
				));
		where = " where sys_user_id = '" + sysUserId + "' and sys_group_id = '" + sysGroupId + "'";
		if(mr.getData("group_login_list", columns, where) == null) {
			mr.insertData("group_login_list", columns, values);
		}
	}

	public void addStageLoginList(String sysStageId, String sysUserId) {
		columns = mr.getStageLoginListTableColumns();
		List<String> values = new ArrayList<String>(Arrays.asList(
				sysStageId,		//sys_stage_id
				sysUserId,		//sys_user_id
				1 + ""			//stage_authority
				));
		where = " where sys_user_id = '" + sysUserId + "' and sys_stage_id = '" + sysStageId + "'";
		if(mr.getData("stage_login_list", columns, where) == null) {
			mr.insertData("stage_login_list", columns, values);
		}
	}

	public DataEntity addForm(String sysFormId, String sysStageId, String formName, LocalDateTime dateSt, LocalDateTime dateEd) {
		if(sysStageId == null) {
			DataEntity userData = (DataEntity)session.getAttribute("userSession");
			sysStageId = userData.getUser_def_stage();
		}
		columns = mr.getFormsTableColumns();
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
		columns = mr.getDatesTableColumns();
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
		columns = mr.getTicketsTableColumns();
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
		columns = mr.getFormsetTableColumns();
		List<String> values = new ArrayList<String>(Arrays.asList(
				sysStageId,		//sys_stage_id
				sysFormId,		//sys_form_id
				sysTicketId,	//sys_ticket_id
				sysDateId		//sys_date_id
				));
		mr.insertData("formset", columns, values);

		return getFormData(sysFormId);
	}

	public void addCast(String sysCastId, String sysStageId, String castCharaName, String sysUserId, Integer castSortNum, Integer userSortNum) {
		columns = mr.getCastTableColumns();
		List<String> values = new ArrayList<String>(Arrays.asList(
				sysCastId,
				sysStageId,			//sys_stage_id
				castCharaName,		//cast_chara_name
				sysUserId,			//sys_user_id
				castSortNum+"",		//cast_sort_num
				userSortNum+""		//user_sort_num
				));
		mr.insertData("cast ", columns, values);
	}

	public void addStaff(String sysStaffId, String sysStageId, String staffDepName, String sysUserId, Integer staffSortNum, Integer userSortNum) {
		columns = mr.getStaffTableColumns();
		List<String> values = new ArrayList<String>(Arrays.asList(
				sysStaffId,
				sysStageId,			//sys_stage_id
				staffDepName,		//cast_chara_name
				sysUserId,			//sys_user_id
				staffSortNum+"",	//cast_sort_num
				userSortNum+""		//user_sort_num
				));
		mr.insertData("staff", columns, values);
	}

	public void addTransaction(DataEntity tempReceptionList) {
		columns = mr.getTransactionsTableColumns();
		List<String> values = new ArrayList<String>(Arrays.asList(
				Pub.createUuid(), // sys_tra_id
				tempReceptionList.getSys_user_id(), //sys_user_id
				tempReceptionList.getSys_stage_id(), //sys_stage_id
				tempReceptionList.getSys_date_id(), //sys_date_id
				tempReceptionList.getSys_ticket_id(), //sys_ticket_id
				tempReceptionList.getTra_amount()+"", //tra_amount
				tempReceptionList.getTra_manager_id(), //tra_manager_id
				tempReceptionList.getTra_memo(), //tra_memo
				Pub.getCurrentDate()+"", //tra_cre_date
				tempReceptionList.getTra_comment(), //tra_comment
				tempReceptionList.getTra_discount()+"" //tra_discount
				));
		mr.insertData("transactions", columns, values);
	}

	public void addImage(String fileName, String fileType, byte[] bytes, String sysAnyId, String contentType) throws IOException {
		mr.insertImage(Pub.createUuid(), fileName, fileType, bytes, sysAnyId, contentType);
	}

	public DataEntity updateUserData(String column, String value, String sysUserId) {
		DataEntity userData = (DataEntity)session.getAttribute("userSession");
		if(sysUserId != null) {
			userData = reGetUserData("sys_user_id", sysUserId);
		}

		where = " where sys_user_id = '" + userData.getSys_user_id() + "'";
		mr.updateData("users", column, value, where);

		return reGetUserData("sys_user_id", userData.getSys_user_id());
	}

	public DataEntity updateStageData(String column, String value, String sysStageId) {
		if(sysStageId == null) {
			DataEntity userData = (DataEntity)session.getAttribute("userSession");
			sysStageId = userData.getUser_def_stage();
		}

		where = " where sys_stage_id = '" + sysStageId + "'";
		mr.updateData("stages", column, value, where);

		return getStageData("sys_stage_id", sysStageId);
	}

	public DataEntity updateGroupData(String column, String value, String sysGroupId) {
		if(sysGroupId == null) {
			DataEntity userData = (DataEntity)session.getAttribute("userSession");
			sysGroupId = userData.getUser_def_group();
		}

		where = " where sys_group_id = '" + sysGroupId + "'";
		mr.updateData("groupes", column, value, where);

		return getGroupData("sys_group_id", sysGroupId);
	}

	public DataEntity updateDateData(String sysDateId, String column, String value) {
		where = " where sys_date_id = '" + sysDateId + "'";
		mr.updateData("dates", column, value, where);

		return getDateData(sysDateId);
	}

	public DataEntity updateTicketData(String sysTicketId, String column, String value) {
		where = " where sys_ticket_id = '" + sysTicketId + "'";
		mr.updateData("tickets", column, value, where);

		return getTicketData(sysTicketId);
	}

	public DataEntity updateFormsetData(String column, String value, String sysStageId, String sysFormId, String sysTicketId, String sysDateId) {
		if(sysStageId == null) {
			DataEntity userData = (DataEntity)session.getAttribute("userSession");
			sysStageId = userData.getUser_def_stage();
		}

		where = " where sys_stage_id = '" + sysStageId + "'";
		if(sysFormId != null) {
			where = where + " and sys_form_id = '" + sysFormId + "'";
		}
		if(sysTicketId != null) {
			where = where + " and sys_ticket_id = '" + sysTicketId + "'";
		}
		if(sysDateId != null) {
			where = where + " and sys_date_id = '" + sysDateId + "'";
		}
		mr.updateData("formset", column, value, where);

		return getFormsetData(sysStageId, sysFormId, sysTicketId, sysDateId);
	}

	public void updateCastOrStaffData(String tableName, String sysAnyId, String column, String value) {
		if(tableName.equals("cast")) {
			where = " where sys_cast_id = '" + sysAnyId + "'";
		}
		if(tableName.equals("staff")) {
			where = " where sys_staff_id = '" + sysAnyId + "'";
		}
		mr.updateData(tableName, column, value, where);
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
			return reGetUserData("sys_user_id", null);
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
			return reGetUserData("sys_user_id", null);
		} else {
			return null;
			//model.addAttribute("message", "公演登録時にエラーが発生しました");
		}
	}

	public void updateAdvertisement(String sysStageId, String tableName, String column, String value) {
		DataEntity userData = (DataEntity)session.getAttribute("userSession");
		if(sysStageId == null) {
			sysStageId = userData.getUser_def_stage();
		}

		where = " where sys_stage_id = '" + sysStageId + "' ";
		mr.updateData(tableName, column, value, where);
	}

	public ArrayList<DataEntity> updateLoginList(String tableName, String column, String value, String sysUserId){
		DataEntity userData = (DataEntity)session.getAttribute("userSession");
		if(sysUserId != null) {
			userData = reGetUserData("sys_user_id", sysUserId);
		}

		where = " where sys_user_id = '" + userData.getSys_user_id() + "'";
		mr.updateData(tableName, column, value, where);

		return getLoginList(tableName);
	}
	
	public void updateTransaction(String sysTransactionId, String sysDateId, String sysTicketId, Integer traAmount, String traComment) {
		where = " where sys_tra_id = '" + sysTransactionId + "' ";
		mr.updateData("transactions", "sys_date_id", sysDateId, where);
		mr.updateData("transactions", "sys_ticket_id", sysTicketId, where);
		mr.updateData("transactions", "tra_amount", traAmount+"", where);
		mr.updateData("transactions", "tra_comment", traComment, where);
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
			where = where + " and ";
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
			where = where + " and sys_form_id = '" + sysFormId + "'";
		}
		if(column != null) {
			where = where + " and " + column + " is not null";
		}
		mr.deleteData("formset", where);
	}

	public void deleteDateOrTicket(String tableName, String sysAnyId) {
		where = " where ";
		if(tableName.equals("dates")) {
			where = where + " sys_date_id = '" + sysAnyId + "'";
		} else if(tableName.equals("tickets")) {
			where = where + " sys_ticket_id = '" + sysAnyId + "'";
		}
		mr.deleteData(tableName, where);
		mr.deleteData("formset", where);
	}

	public void deleteCastOrStaffData(String tableName, String sysAnyId) {
		where = " where ";
		if(tableName.equals("cast")) {
			where = where + " sys_cast_id = '" + sysAnyId + "'";
		} else if(tableName.equals("staff")) {
			where = where + " sys_staff_id = '" + sysAnyId + "'";
		}
		mr.deleteData(tableName, where);
	}
	
	public void deleteTransaction(String sysTransactionId) {
		where = " where sys_tra_id = '" + sysTransactionId + "' ";
		mr.deleteData("transactions", where);
	}
}