package com.webApplication.service.cust;

import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import com.webApplication.entity.Env;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MyPageService {
//	private final MainRepository mr;
//	private final HttpSession session;
//	private final SQL sql;
	
	public void setPageInfo(Model model) {
		model.addAttribute("title2", Env.myPageView);
	}
	
	

}
