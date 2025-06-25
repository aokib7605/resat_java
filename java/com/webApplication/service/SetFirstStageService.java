package com.webApplication.service;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Service;

import com.webApplication.entity.DataEntity;
import com.webApplication.functions.SQL;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SetFirstStageService {
	private final HttpSession session;
	private final SQL sql;
	
	public void updateUserSession() {
		DataEntity userData = (DataEntity) session.getAttribute("userSession");
		session.setAttribute("userSession", sql.getUserData("sys_user_id", userData.getSys_user_id()));
	}
}
