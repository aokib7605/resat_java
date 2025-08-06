package com.webApplication.controller;

import java.util.Base64;

import org.springframework.context.annotation.Configuration;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.webApplication.entity.DataEntity;
import com.webApplication.entity.TestEntity;
import com.webApplication.service.SetUserService;
import com.webApplication.service.cust.InputFormService;
import com.webApplication.service.cust.SetCustService;

import lombok.RequiredArgsConstructor;

@org.springframework.web.bind.annotation.RestController
@RequiredArgsConstructor
@Configuration
public class RestController implements WebMvcConfigurer {
	private final SetUserService sus;
	private final SetCustService scs;
	private final InputFormService ifs;

	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/**")
		.allowedOrigins("http://127.0.0.1:5500")
		.allowedOrigins("http://lailai.main.jp")
		.allowedMethods("GET", "POST", "PUT", "DELETE")
		.allowedHeaders("*")
		.allowCredentials(true);
	}

	@PostMapping("/test/multiply")
	public Integer getResult(@RequestBody TestEntity obj) {
		return obj.getA() * obj.getB();
	}

	@GetMapping("/userMailUpdate")
	public String userMailUpdate(Model model, String sysDiResu, String liam) {
		String sysUserId = sysDiResu;
		byte[] decodedBytes = Base64.getUrlDecoder().decode(liam);
		String userMail = new String(decodedBytes);

		sus.setUserMail(model, "confiMail", sysUserId, userMail);

		return "<p>メールアドレスを更新しました<p>";
	}

	@GetMapping("/mailUpdate")
	public String custMailUpdate(Model model, String sysDiResu, String liam) {
		String sysUserId = sysDiResu;
		byte[] decodedBytes = Base64.getUrlDecoder().decode(liam);
		String userMail = new String(decodedBytes);

		scs.setUserMail(model, "confiMail", sysUserId, userMail);

		return "<p>メールアドレスを更新しました<p>";
	}

	@PostMapping("/otherServiceExeReserve")
	public void otherServiceExeReserve(Model model, @RequestBody DataEntity formData) {
		boolean result = ifs.registOtherServiceTransaction(model, formData);
		if(result == false) {
			
		}
	}
}
