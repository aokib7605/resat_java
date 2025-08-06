package com.webApplication.controller;

import java.util.ArrayList;

import jakarta.servlet.http.HttpSession;

import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.webApplication.entity.DataEntity;
import com.webApplication.entity.Env;
import com.webApplication.service.EnvService;
import com.webApplication.service.cust.CustLoginService;
import com.webApplication.service.cust.InputFormService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Configuration
@Controller
public class OtherServiceController implements WebMvcConfigurer {
	private final HttpSession session;
	private final EnvService es;
	private final CustLoginService cls;
	private final InputFormService ifs;
	
//	@Override
//	public void addCorsMappings(CorsRegistry registry) {
//		registry.addMapping("/**")
//		.allowedOrigins("http://127.0.0.1:5500")
//		.allowedOrigins("http://lailai.main.jp")
//		.allowedMethods("GET", "POST", "PUT", "DELETE")
//		.allowedHeaders("*")
//		.allowCredentials(true);
//	}

	private void setEnvData(Model model, String mode) {
		model.addAttribute("title", Env.custApplicationTitle);
		setMenuList(model, mode);
	}

	private void setMenuList(Model model, String mode) {
		//mode には「manager」「customer」のいずれかを格納
		es.setMenuList(model, mode);
	}

	public String goLoginPage(Model model) {
		setEnvData(model, "login");
		cls.setPageInfo(model);
		return "reserve/custLogin";
	}

	@PostMapping("/loginGrobeAccountFromOtherService")
	public String loginGrobeAccountFromOtherService(Model model, @RequestParam("json") String json) {
		ObjectMapper mapper = new ObjectMapper();
		try {
			ArrayList<DataEntity> tempReceptionList = new ArrayList<DataEntity>();
			DataEntity data = mapper.readValue(json, DataEntity.class);
			
			// ユーザーの予約内容を仮受付データとしてセッションに登録
			tempReceptionList.add(data);
			session.setAttribute("tempReceptionList", tempReceptionList);
			
			// フォームIDを元に公演情報をセッションに登録
			ifs.setFormDataSession(data.getSys_form_id(), "");
			
			model.addAttribute("mode", "reserveLogin");
			return goLoginPage(model);
			
		} catch (JsonProcessingException e) {
			return goLoginPage(model);
		}
	}
}
