package com.webApplication.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.webApplication.entity.Env;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class JavaMailSenderService {
	private final JavaMailSender mailSender;
	
	/**
	 * 
	 * @param toMailAddress
	 * @param title
	 * @param messageText
	 */
	public void sendMail(String toMailAddress, String title, String messageText) {
		//build.gradleに下記の記述が必要
		/*
    			plugins {
    				    id 'org.springframework.boot' version '3.2.4'
    				    id 'io.spring.dependency-management' version '1.1.4'
    				    id 'java'
    			}
    			group = 'com.example'
    			version = '0.0.1-SNAPSHOT'
    			sourceCompatibility = '17'  // Javaバージョンに応じて調整

    			repositories {
    			    mavenCentral()
    			}
    			dependencies {
    			    implementation 'org.springframework.boot:spring-boot-starter-mail'
    			    implementation 'org.springframework.boot:spring-boot-starter'
    			}
		 */
		//application.propertiesに下記の記述が必要
		/*
    			spring.mail.host=smtp.gmail.com
    			spring.mail.port=587
    			spring.mail.username=nukikugi@gmail.com
    			spring.mail.password=kizm hdxv lpyr fbqx
    			spring.mail.properties.mail.smtp.auth=true
    			spring.mail.properties.mail.smtp.starttls.enable=true
		 */
		SimpleMailMessage message = new SimpleMailMessage();
		message.setTo(toMailAddress);
		message.setFrom(Env.sendFromMail);
		message.setSubject(title);
		message.setText(messageText);

		// メール送信を実施する。
		mailSender.send(message);
	}
}
