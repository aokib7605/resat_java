package com.webApplication.repository;

import java.sql.Date;
import java.text.SimpleDateFormat;
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
}
