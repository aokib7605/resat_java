package com.webApplication.repository;

import java.math.BigDecimal;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
			for (String column : columns) {
//				String type = column.getClass().getSimpleName();
//				System.out.println(column + "の変数の型は" + type + "（" + dbObj.get(column) + "）");
				if (column.contains(".")) {
					column = column.substring(column.indexOf(".") + 1);
				}
				if (dbObj.get(column) != null) {
					// Integer 型処理
					try {
						String record = (Integer) dbObj.get(column) + "";
						data.setEntity(column, record);
					} catch (Exception e) {
//						System.out.println(e);
					}

					// BigDecimal 型処理
					try {
						String record = ((BigDecimal) dbObj.get(column)).toPlainString();
						data.setEntity(column, record);
					} catch (Exception e) {
//						System.out.println(e);
					}

					// Date 型処理（java.sql.Date）
					try {
						String record = new SimpleDateFormat("yyyy-MM-dd").format((Date) dbObj.get(column));
						data.setEntity(column, record);
					} catch (Exception e) {
//						System.out.println(e);
					}

					// LocalDateTime 型処理（java.time.LocalDateTime）
					try {
						String record = ((LocalDateTime) dbObj.get(column)).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
						data.setEntity(column, record);
					} catch (Exception e) {
//						System.out.println(e);
					}

					// String 型処理
					try {
						data.setEntity(column, (String) dbObj.get(column));
					} catch (Exception e) {
//						System.out.println(e);
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
	
	public DataEntity getData(String table, List<String> columns, List<String> getColumns, String where) {
		try {
			sql = "select " + Pub.convertListToStr(getColumns) + " from " + table + " " + where;
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
	
	public ArrayList<DataEntity> getDataList(String table, List<String> columns, List<String> getColumns, String where){
		try {
			sql = "select " + Pub.convertListToStr(getColumns) + " from " + table + " " + where;
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

	public ArrayList<DataEntity> getDataListBySelectColumn(String table, List<String> columns, List<String> getColumns, String where){
		try {
			sql = "select " + Pub.convertListToStr(columns) + " from " + table + " " + where;
			System.out.println(sql);
			List<Map<String, Object>> dbObjList = tmp.queryForList(sql);
			ArrayList<DataEntity> resultList = new ArrayList<DataEntity>();
			
			List<String> targetColumns = (getColumns != null) ? getColumns : columns;
			for(Map<String, Object> dbObj : dbObjList) {
				resultList.add(setData(dbObj, targetColumns));
			}
			return resultList;
		} catch (Exception e) {
			System.out.println(e);
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
	
	public void insertImage(String uuid, String fileName, String fileType, byte[] binaryData, String sysAnyId, String contentType) {
		try {
			sql = "insert into " + "images" + " values( ?, ?, ?, ?, ?, ? )";
			System.out.println(sql);
			tmp.update(sql, uuid, fileName, fileType, binaryData, sysAnyId, contentType);
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	public void updateData(String table, String column, String value, String where) {
		try {
			sql = "update " + table + " set " + column + " = '" + value + "' " + where;
			if(value == null) {
				sql = sql.replace("'" + value + "' ", "" + value + " ");
			}
			System.out.println(sql);
			tmp.update(sql);
		} catch (Exception e) {
			System.out.println(e);
		}
	}
	
	public void updateData(String table, String column, byte[] value, String where) {
		try {
			sql = "update " + table + " set " + column + " = '" + value + "' " + where;
			if(value == null) {
				sql = sql.replace("'" + value + "' ", "" + value + " ");
			}
			System.out.println(sql);
			tmp.update(sql);
		} catch (Exception e) {
			System.out.println(e);
		}
	}
	
	public void deleteData(String table, String where) {
		try {
			sql = "delete from " + table + where;
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
				"binary_data",
				"sys_any_id",
				"content_type"
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

	public List<String> getCastTableColumns(){ 
		List<String> columns = new ArrayList<String>(Arrays.asList(
				"sys_cast_id", 
				"sys_stage_id",
				"cast_chara_name",
				"sys_user_id",
				"cast_sort_num",
				"user_sort_num"
				));
		return columns; 
	}

	public List<String> getStaffTableColumns(){ 
	    List<String> columns = new ArrayList<String>(Arrays.asList(
	    		"sys_staff_id", 
	            "sys_stage_id", 
	            "staff_dep_name", 
	            "sys_user_id", 
	            "staff_sort_num", 
	            "user_sort_num" 
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
	
	public List<String> getFormsTableColumns(){ 
	    List<String> columns = new ArrayList<String>(Arrays.asList(
	            "sys_form_id",
	            "sys_stage_id",
	            "form_name",
	            "date_st",
	            "date_ed"
	    ));
	    return columns; 
	}
	
	public List<String> getTicketsTableColumns(){ 
	    List<String> columns = new ArrayList<String>(Arrays.asList(
	            "sys_ticket_id",
	            "sys_stage_id",
	            "ticket_name",
	            "ticket_price"
	            ));
	    return columns; 
	}
	
	public List<String> getDatesTableColumns(){ 
	    List<String> columns = new ArrayList<String>(Arrays.asList(
	            "sys_date_id",
	            "sys_stage_id",
	            "st_date",
	            "st_seat",
	            "st_info"
	            ));
	    return columns; 
	}
	
	public List<String> getFormsetTableColumns(){ 
	    List<String> columns = new ArrayList<String>(Arrays.asList(
	        "sys_stage_id",
	        "sys_form_id",
	        "sys_ticket_id",
	        "sys_date_id"
	    ));
	    return columns; 
	}
	
	public List<String> getTransactionsTableColumns(){ 
		List<String> columns = new ArrayList<String>(Arrays.asList(
				"sys_tra_id",
				"sys_user_id",
				"sys_stage_id",
				"sys_date_id",
				"sys_ticket_id",
				"tra_amount",
				"tra_manager_id",
				"tra_memo",
				"tra_cre_date",
				"tra_comment",
				"tra_discount"));
		return columns; 
	}
}
