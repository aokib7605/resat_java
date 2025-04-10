package com.webApplication.entity;

import java.lang.reflect.Method;
import java.sql.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DataEntity {
	
    private String sys_user_id;
    private String sys_user_mode;
    private int sys_user_ev;
    private String user_id;
    private String user_mail;
    private String user_tell;
    private String user_name;
    private String user_kana_name;
    private String user_stage_name;
    private String user_stage_kana_name;
    private String user_pass;
    private String user_def_stage;
    private Date user_cre_date;
    private Date user_last_login;
    private Date user_birthday;
    private int user_hide_age;
	
	public void setEntity(String column, String value) {
		try {
			String methodName = "set" + column.substring(0, 1).toUpperCase() + column.substring(1);
			
			Method getter = this.getClass().getMethod("get" + column.substring(0, 1).toUpperCase() + column.substring(1));
			Class<?> fieldType = getter.getReturnType();
			
			Object convertedValue = convertValue(value, fieldType);
			
            // セッターメソッドを取得して実行
            Method method = this.getClass().getMethod(methodName, fieldType);
            method.invoke(this, convertedValue);
		} catch (Exception e) { 
			System.out.println("execute error");
		}
	}
	

    // 値を型に合わせて変換するヘルパーメソッド
    private Object convertValue(String value, Class<?> targetType) {
        if (targetType == int.class || targetType == Integer.class) {
            return Integer.parseInt(value);
        } else if (targetType == double.class || targetType == Double.class) {
            return Double.parseDouble(value);
        } else if (targetType == boolean.class || targetType == Boolean.class) {
            return Boolean.parseBoolean(value);
        }
        return value; // デフォルトは文字列
    }
}
