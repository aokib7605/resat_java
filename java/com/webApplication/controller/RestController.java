package com.webApplication.controller;

import java.util.Base64;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.webApplication.entity.TestEntity;
import com.webApplication.service.SetUserService;

import lombok.RequiredArgsConstructor;

@org.springframework.web.bind.annotation.RestController
@RequiredArgsConstructor
public class RestController {
	private final SetUserService sus;

	@PostMapping("/test/multiply")
	public Integer getResult(@RequestBody TestEntity obj) {
		return obj.getA() * obj.getB();
	}
	
	@GetMapping("/resat/userMailUpdate")
	public String userMailUpdate(Model model, String sysDiResu, String liam) {
		String sysUserId = sysDiResu;
		byte[] decodedBytes = Base64.getUrlDecoder().decode(liam);
		String userMail = new String(decodedBytes);
		
		sus.setUserMail(model, "confiMail", sysUserId, userMail);
		
		return "<p>メールアドレスを更新しました<p>";
	}
}
