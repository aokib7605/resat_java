package com.webApplication.service;

import jakarta.servlet.http.HttpSession;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import com.webApplication.entity.DataEntity;
import com.webApplication.functions.SQL;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SetGroupService {
	private final HttpSession session;
	private final JavaMailSender mailSender;
	//	private final MainRepository mr;
	private final SQL sql;

	public void setPageInfo(Model model) {
		model.addAttribute("title2", "団体基本情報");
		model.addAttribute("groupData", getGroupData());
	}
	
	public void setGroupId(Model model, String mode, String sysGroupId, String groupId) {
		switch (mode) {
		case "setGroupId": {
			model.addAttribute("mode", "setGroupId");
			break;
		}
		case "inputValue": {
			if(sql.getGroupData("group_id", groupId) != null) {
				model.addAttribute("mode", "setGroupId");
				model.addAttribute("groupId", groupId);
				model.addAttribute("message", "そのIDは既に使用されています");
			} else {
				DataEntity userData = (DataEntity)session.getAttribute("userSession");
				sql.updateGroupData("group_id", groupId, null);
				userData = sql.getUserData("sys_user_id", userData.getSys_user_id());
				model.addAttribute("message", "団体IDを変更しました");
				session.setAttribute("userSession", userData);
				model.addAttribute("userData", userData);
			}
			break;
		}
		default:
			break;
		}
	}
	
    public void setGroupMail(Model model, String mode, String sysGroupId, String groupMail) {
        switch (mode) {
        case "setMail": {
            model.addAttribute("mode", "setMail");
            break;
        }
        case "inputValue": {
                DataEntity userData = (DataEntity)session.getAttribute("userSession");
                if(sysGroupId == null) {
                	sysGroupId = userData.getUser_def_group();
                }
                sql.updateGroupData("group_mail", groupMail, sysGroupId);
                DataEntity groupData = sql.getGroupData("sys_group_id", sysGroupId);
                SimpleMailMessage message = new SimpleMailMessage();
                message.setTo(groupMail);
                message.setFrom("nukikugi@gmail.com");
                message.setSubject("りざっとメールアドレス変更手続き");
                message.setText(
                        "こんにちは！ \n"
                                + "りざっと運営・開発団体の劇団抜きにくい釘です。\n"
                                + "こちらのメールアドレスを" + groupData.getGroup_name() + "に設定しました"
                        );
                model.addAttribute("message", "メールアドレスを変更しました");
                userData = sql.getUserData("sys_user_id", userData.getSys_user_id());
                session.setAttribute("userSession", userData);
                model.addAttribute("userData", userData);
                model.addAttribute("mode", "setMail");
                
                // メール送信を実施する。
                mailSender.send(message);
            break;
        }
        default:
            break;
        }
    }
    
    public void setGroupPass(Model model, String mode, String sysGroupId, String groupPass, String rePass) {
        switch (mode) {
        case "setGroupPass": {
            model.addAttribute("mode", "setGroupPass");
            break;
        }
        case "inputValue": {
            if(!groupPass.equals(rePass)) {
                model.addAttribute("mode", "setGroupPass");
                model.addAttribute("message", "パスワードが一致していません");
            } else {
            	DataEntity userData = (DataEntity)session.getAttribute("userSession");
                sql.updateGroupData("group_pass", groupPass, null);
                model.addAttribute("message", "パスワードを変更しました");
                
                userData = sql.getUserData("sys_user_id", userData.getSys_user_id());
                session.setAttribute("userSession", userData);
                model.addAttribute("userData", userData);
            }
            break;
        }
        default:
            break;
        }
    }
    
    public void setGroupName(Model model, String mode, String sysGroupId, String groupName, String groupKanaName) {
        switch (mode) {
        case "setGroupName": {
            model.addAttribute("mode", "setGroupName");
            break;
        }
        case "inputValue": {
        	DataEntity userData = (DataEntity)session.getAttribute("userSession");
            sql.updateGroupData("group_name", groupName, null);
            sql.updateGroupData("group_kana_name", groupKanaName, null);
            model.addAttribute("message", "団体名を変更しました");
            
            userData = sql.getUserData("sys_user_id", userData.getSys_user_id());
            session.setAttribute("userSession", userData);
            model.addAttribute("userData", userData);
            break;
        }
        default:
            break;
        }
    }
	
	public DataEntity getGroupData() {
		DataEntity userData = (DataEntity)session.getAttribute("userSession");
		return sql.getGroupData("sys_group_id", userData.getUser_def_group());
	}
}
