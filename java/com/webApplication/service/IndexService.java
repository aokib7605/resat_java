package com.webApplication.service;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class IndexService {
	private final HttpSession session;
	
	public void setPageInfo(Model model) {
		model.addAttribute("title2", "管理者メニュー");
		model.addAttribute("user", session.getAttribute("userSession"));
		model.addAttribute("stage", session.getAttribute("defStSession"));
	}
}
