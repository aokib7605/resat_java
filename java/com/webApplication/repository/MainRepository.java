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

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class MainRepository {
	private final JdbcTemplate tmp;
	private String sql = "";
	
	private DataEntity setData(Map<String, Object> dbObj, List<String> columns) {
		DataEntity data = new DataEntity();
		try {
			for(String column : columns) {
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
		} catch (Exception e) {
			System.out.println(e);
		}
		return data;
	}
	
	public DataEntity getData(String table, List<String> columns, String where) {
		try {
			String columnsStr = String.join(", ", columns);
			sql = "select " + columnsStr + " from " + table + " " + where;
			Map<String, Object> dbObj = tmp.queryForMap(sql);
			return setData(dbObj, columns);
		} catch (Exception e) {
			// TODO: handle exception
		}
		return null;
	}
	
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
				"user_hide_age"
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
				"stage_flyer_2"
				));
		return columns;
	}
}
