package com.webApplication.service.cust;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Service;

import com.webApplication.entity.DataEntity;
import com.webApplication.functions.SQL;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class ChangeModeService {
	private final HttpSession session;
	private final SQL sql;
	
	public void changeMode() {
		DataEntity custData = (DataEntity)session.getAttribute("custSession");
		if(custData.getUser_def_stage() != null) {
			DataEntity stageData = sql.getStageData("sys_stage_id", custData.getUser_def_stage());
			session.setAttribute("defStSession", stageData);
		}
		session.setAttribute("userSession", custData);
		session.setAttribute("custSession", null);
	}
}
