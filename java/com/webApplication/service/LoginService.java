package com.webApplication.service;

import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

@Service
public class LoginService {
	
	public void setPageInfo(Model model) {
		model.addAttribute("title2", "システムログイン");
	}
}
