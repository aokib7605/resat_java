package com.webApplication.controller;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;

import com.webApplication.service.EnvService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class CustController {
	private final HttpSession session;
	private final EnvService es;
	private String pageName = "";	//遷移先のページ名
}
