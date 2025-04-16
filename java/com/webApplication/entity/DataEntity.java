package com.webApplication.entity;

import java.lang.reflect.Method;
import java.sql.Date;
import java.text.SimpleDateFormat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DataEntity {
	
	//users
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
    
    //stages
    private String sys_stage_id;
    private String sys_group_id;
    private String stage_id;
    private String stage_pass;
    private String stage_name;
    private Integer stage_attract_customers;
    private String stage_url_title;
    private String stage_place_name;
    private String stage_open_minutes;
    private String stage_runtime;
    private String stage_story;
    private Date stage_cre_date;
    private Integer stage_opener;
    private String stage_flyer_1;
    private String stage_flyer_2;
    
    //groupes
//    private String sys_group_id;	//(= stages, group_login_list)
    private String group_id;
    private String group_name;
    private String group_kana_name;
    private String group_pass;
    private String group_mail;
    private Date group_cre_date;
    private Date group_last_login;
    
    //group_login_list
//    private String sys_group_id;	//(= stages, groupes)
//    private String sys_user_id;	//(= users)
    private Integer group_authority;
    private String user_spe_name;
    private Integer user_spe_name_ev;

	
	public void setEntity(String column, String value) {
		System.out.println(column + " : " +value);
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
    	if(value == null) {
    		return null;
    	}
        if (targetType == int.class || targetType == Integer.class) {
            return Integer.parseInt(value);
        } else if (targetType == double.class || targetType == Double.class) {
            return Double.parseDouble(value);
        } else if (targetType == boolean.class || targetType == Boolean.class) {
            return Boolean.parseBoolean(value);
        } else if (targetType == Date.class) { 
        	// java.sql.Date に対応
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            try {
                java.util.Date parsed = formatter.parse(value);
                return new Date(parsed.getTime()); // java.sql.Date に変換
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
        return value; // デフォルトは文字列
    }
}
