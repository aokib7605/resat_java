package com.webApplication.entity;

import java.lang.reflect.Method;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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
    private String user_def_group;
    
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
    private String stage_place_address;
    
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
    
    //images
    private String sys_image_id;
    private String file_name;
    private String file_type;
    private byte[] binary_data;
    private String sys_any_id;
    private String content_type;
    
    //cast
    private String sys_cast_id;
//    private String sys_stage_id;
    private String cast_chara_name;
//    private String sys_user_id;
    private int cast_sort_num;
    private int user_sort_num;
    
    //staff
    private String sys_staff_id;
//    private String sys_stage_id;
    private String staff_dep_name;
//    private String sys_user_id;
    private int staff_sort_num;
//    private int user_sort_num;
    
    //stage_login_list
//    private String sys_stage_id;
//    private String sys_user_id;
    private Integer stage_authority;
    
    //group_authority
    private Integer authority_id;
    private String authority_name;
    
  //stage_authority
//    private Integer authority_id;
//    private String authority_name;
    
    //forms
    private String sys_form_id;
//    private String sys_stage_id;
    private String form_name;
    private LocalDateTime date_st;
    private LocalDateTime date_ed;
    
    //tickets
    private String sys_ticket_id;
//    private String sys_stage_id;
    private String ticket_name;
    private int ticket_price;
    
    //dates
    private String sys_date_id;
//    private String sys_stage_id;
    private LocalDateTime st_date;
    private int st_seat;
    private String st_info;
    
    //formset
//    private String sys_stage_id;
//    private String sys_form_id;
//    private String sys_ticket_id;
//    private String sys_date_id;
    
    // transactions
    private String sys_tra_id;
//    private String sys_user_id;
//    private String sys_stage_id;
//    private String sys_date_id;
//    private String sys_ticket_id;
    private int tra_amount;
    private String tra_manager_id;
    private String tra_memo;
    private Date tra_cre_date;
    private String tra_comment;
    private int tra_discount;
    private String no_login;
    
    // none_users
    private String sys_none_user_id;
    private String none_user_name;
    private String none_user_kana_name;
    private String none_user_mail;
    private String none_user_tell;
    
    //sqlで生成するカラム名
    private String user_names;
    private String cust_id;
    private String cust_name;
    private String cust_kana_name;
    private String manager_id;
    private String manager_name;
    private String manager_kana_name;
    private Integer amount_by_date;
    private Integer total_amount_by_date;
    private Integer amount_by_ticket;
    private Integer amount_by_manager;
    private Integer total_amount_by_manager;
    private Integer amount_by_group;
    private Integer total_amount_by_group;
    private Integer total_price_by_ticket;
    private Integer total_amount;
    private Integer total_price;
	
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
			System.out.println(column + " : " + value);
			System.out.println("execute error");
		}
	}
	
    // 値を型に合わせて変換するヘルパーメソッド
    private Object convertValue(String value, Class<?> targetType) {
        if (value == null) {
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
        } else if (targetType == LocalDateTime.class) {
            // LocalDateTime に対応（フォーマットに応じて調整可能）
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            try {
                return LocalDateTime.parse(value, formatter);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        return value; // デフォルトは文字列
    }
}
