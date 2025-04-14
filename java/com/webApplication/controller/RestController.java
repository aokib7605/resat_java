package com.webApplication.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.webApplication.entity.TestEntity;

@org.springframework.web.bind.annotation.RestController
public class RestController {

	@PostMapping("/test/multiply")
	public Integer getResult(@RequestBody TestEntity obj) {
		return obj.getA() * obj.getB();
	}
}
