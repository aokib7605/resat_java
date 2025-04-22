package com.webApplication.repository;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.webApplication.entity.DataEntity;
import com.webApplication.functions.Pub;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class MainRepository {
	private final JdbcTemplate tmp;
	private String sql = "";

	// 基本のメソッド
	// ===================================================================== //

	private DataEntity setData(Map<String, Object> dbObj, List<String> columns) {
		DataEntity data = new DataEntity();
		try {
			for(String column : columns) {
				if(dbObj.get(column) != null) {
					try {
						String record = (Integer)dbObj.get(column) + "";
						data.setEntity(column, record);
					} catch (Exception e) {
						// TODO: handle exception
					}
					try {
						String record = new SimpleDateFormat("yyyy-MM-dd").format((Date)dbObj.get(column));
						data.setEntity(column, record);
					} catch (Exception e) {
						// TODO: handle exception
					}
					try {
						data.setEntity(column, (String)dbObj.get(column));
					} catch (Exception e) {
						// TODO: handle exception
					}
				}
			}
		} catch (Exception e) {
			System.out.println(e);
		}
		return data;
	}

	public DataEntity getData(String table, List<String> columns, String where) {
		try {
			sql = "select " + "*" + " from " + table + " " + where;
			System.out.println(sql);
			Map<String, Object> dbObj = tmp.queryForMap(sql);
			return setData(dbObj, columns);
		} catch (Exception e) {
			// TODO: handle exception
		}
		return null;
	}

	public ArrayList<DataEntity> getDataList(String table, List<String> columns, String where){
		try {
			sql = "select " + "*" + " from " + table + " " + where;
			System.out.println(sql);
			List<Map<String, Object>> dbObjList = tmp.queryForList(sql);
			ArrayList<DataEntity> resultList = new ArrayList<DataEntity>();
			for(Map<String, Object> dbObj : dbObjList) {
				resultList.add(setData(dbObj, columns));
			}
			return resultList;
		} catch (Exception e) {
			// TODO: handle exception
		}
		return null;
	}

	public ArrayList<DataEntity> getDataListBySelectColumn(String table, List<String> columns, String where){
		try {
			sql = "select " + Pub.convertListToStr(columns) + " from " + table + " " + where;
			System.out.println(sql);
			List<Map<String, Object>> dbObjList = tmp.queryForList(sql);
			ArrayList<DataEntity> resultList = new ArrayList<DataEntity>();
			for(Map<String, Object> dbObj : dbObjList) {
				resultList.add(setData(dbObj, columns));
			}
			return resultList;
		} catch (Exception e) {
			// TODO: handle exception
		}
		return null;
	}

	public void insertData(String table, List<String> columns, List<String> values) {
		try {
			sql = "insert into " + table + "(" + Pub.convertListToStr(columns) + ") values(" + Pub.convertListToQuotedStr(values) + ")";
			System.out.println(sql);
			tmp.update(sql);
		} catch (Exception e) {
			System.out.println(e);
		}
	}
	
	public void insertImage(String uuid, String fileName, String fileType, byte[] binaryData) {
		try {
			sql = "insert into " + "images" + " values( ?, ?, ?, ? )";
			System.out.println(sql);
			tmp.update(sql, uuid, fileName, fileType, binaryData);
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	public void updateData(String table, String column, String value, String where) {
		try {
			sql = "update " + table + " set " + column + " = '" + value + "' " + where;
			System.out.println(sql);
			tmp.update(sql);
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	public void createTable(String table, List<String> columns, String where) {
		try {
			sql = "create table " + table + " (" + Pub.convertListToStr(columns) + ") " + where;
			System.out.println(sql);
			tmp.update(sql);
		} catch (Exception e) {
			System.out.println(e);
		}
	}


	// カラム一覧をまとめたメソッド
	// ===================================================================== //

	public List<String> getUsersTableColumns(){
		List<String> columns = new ArrayList<String>(Arrays.asList(
				"sys_user_id",
				"sys_user_mode",
				"sys_user_ev",
				"user_id",
				"user_mail",
				"user_tell",
				"user_name",
				"user_kana_name",
				"user_stage_name",
				"user_stage_kana_name",
				"user_pass",
				"user_def_stage",
				"user_cre_date",
				"user_last_login",
				"user_birthday",
				"user_hide_age",
				"user_def_group"
				));
		return columns;
	}

	public List<String> getStagesTableColumns(){
		List<String> columns = new ArrayList<String>(Arrays.asList(
				"sys_stage_id",
				"sys_group_id",
				"stage_id",
				"stage_pass",
				"stage_name",
				"stage_attract_customers",
				"stage_url_title",
				"stage_place_name",
				"stage_open_minutes",
				"stage_runtime",
				"stage_story",
				"stage_cre_date",
				"stage_opener",
				"stage_flyer_1",
				"stage_flyer_2",
				"stage_place_address"
				));
		return columns;
	}

	public List<String> getGroupesTableColumns(){
		List<String> columns = new ArrayList<String>(Arrays.asList(
				"sys_group_id",
				"group_id",
				"group_name",
				"group_kana_name",
				"group_pass",
				"group_mail",
				"group_cre_date",
				"group_last_login"
				));
		return columns;
	}

	public List<String> getGroupeLoginListTableColumns(){
		List<String> columns = new ArrayList<String>(Arrays.asList(
				"sys_group_id",
				"sys_user_id",
				"group_authority",
				"user_spe_name",
				"user_spe_name_ev"
				));
		return columns;
	}

	public List<String> getImagesTableColumns(){
		List<String> columns = new ArrayList<String>(Arrays.asList(
				"sys_image_id",
				"file_name",
				"file_type",
				"binary_data"
				));
		return columns;
	}

	public List<String> getStageLoginListTableColumns(){
		List<String> columns = new ArrayList<String>(Arrays.asList(
				"sys_stage_id", 
				"sys_user_id", 
				"stage_authority"
				));
		return columns;
	}

	public List<String> getCast_sysStageIdTableColumns(){ 
		List<String> columns = new ArrayList<String>(Arrays.asList(
				"sys_user_id", 
				"cast_chara_name", 
				"cast_sort_num"
				));
		return columns;
	}

	public List<String> getStaff_sysStageIdTableColumns(){ 
		List<String> columns = new ArrayList<String>(Arrays.asList(
				"sys_user_id", 
				"staff_dep_name", 
				"staff_sort_num"
				));
		return columns;
	}
	
	public List<String> getGroupAuthorityTableColumns(){ 
		List<String> columns = new ArrayList<String>(Arrays.asList(
			"authority_id", 
			"authority_name"
		));
		return columns;
	}
	
	public List<String> getStageAuthorityTableColumns(){ 
		List<String> columns = new ArrayList<String>(Arrays.asList(
			"authority_id", 
			"authority_name"
		));
		return columns;
	}
}
