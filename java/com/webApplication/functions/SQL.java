package com.webApplication.functions;

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

	public DataEntity updateUserDefGroup(String column, String id, boolean newGroup) {
		DataEntity userData = (DataEntity)session.getAttribute("userSession");
		if(getGroupData(column, id) != null) {
			DataEntity groupData = getGroupData(column, id);

			where = " where sys_user_id = '" + userData.getSys_user_id() + "'";
			mr.updateData("users", "user_def_group", groupData.getSys_group_id(), where);

			if(newGroup == true) {
				addGroupLoginList(groupData.getSys_group_id(), userData.getSys_user_id());
			}
			return getUserData();
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
			return getUserData();
		} else {
			return null;
			//model.addAttribute("message", "公演登録時にエラーが発生しました");
		}
	}

	public DataEntity getUserData() {
		DataEntity userData = (DataEntity)session.getAttribute("userSession");
		where = "left outer join groupes on sys_group_id = user_def_group where sys_user_id =  '" + userData.getSys_user_id() + "'";
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
		List<String> columns = mr.getStagesTableColumns();
		where = " where <ID> = '" + anyId + "' limit 1";

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
			where = where.replace(" = '" + userData.getSys_user_id() + "'", " like '%" + keyword + "%'");
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
			where = where.replace(" = '" + userData.getSys_user_id() + "'", " like '%" + keyword + "%'");
		}
		return mr.getDataList("stages s", columns, where);
	}
	
	public ArrayList<DataEntity> getUserDataList(String column, String keyword, Integer limit, Integer offset){
		List<String> columns = Stream.concat(mr.getUsersTableColumns().stream(), mr.getGroupeLoginListTableColumns().stream()).collect(Collectors.toList());

		String joinTable = " left outer join group_login_list gll on gll.sys_group_id = u.user_def_group ";
		String where = "";
		if(offset != null) {
			where = joinTable + " where " + column + " = '" + keyword + "' limit " + limit + " offset " + offset * limit;
		} else {
			where = joinTable + " where " + column + " = '" + keyword + "'";
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
			joinTable = " left outer join stages s on sll.sys_stage_id = s.sys_stage_id left outer join stage_authorities sa on sll.group_authority = sa.authority_id left outer join users u on u.sys_user_id = gll.sys_user_id ";
			column = "sll.sys_stage_id";
			break;
		}
		default:
			break;
		}
		where = joinTable + " where " + column + " = '" + sysId + "'";
		return mr.getDataList(tableName, columns, where);
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
		mr.insertData("group_login_list", columns, values);
	}

	private void addStageLoginList(String sysStageId, String sysUserId) {
		List<String> columns = mr.getStageLoginListTableColumns();
		List<String> values = new ArrayList<String>(Arrays.asList(
				sysStageId,		//sys_stage_id
				sysUserId,		//sys_user_id
				1 + ""			//stage_authority
				));
		mr.insertData("stage_login_list", columns, values);
	}
}