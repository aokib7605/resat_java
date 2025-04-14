package com.webApplication.service;

import java.util.Base64;
import java.util.List;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import com.webApplication.repository.MainRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CreateUserService {
	private final MainRepository mr;
	private final HttpSession session;
	
	public void setPageInfo(Model model) {
		model.addAttribute("title2", "新規ユーザー作成");
	}
	
	public String start(Model model) {
		model.addAttribute("mode", "checkMail");
		return "createUser";
	}
	
	public boolean checkMail(Model model, String userMail) {
		List<String> columns = mr.getUsersTableColumns();
		String where = " where user_mail = '" + userMail + "'";
		if(mr.getData("users", columns, where) == null) {
			String mailBase64 = Base64.getUrlEncoder().encodeToString(userMail.getBytes());
			System.out.println(mailBase64);
			model.addAttribute("message", userMail + " にメールを送信しました。<br>URLをクリックして以降のステップを進めてください。" + 
					"<p>メールが届かない場合、迷惑メールの設定にて<br> nukikugi@gmail.com からの受信設定を確認してください。</p>");
			model.addAttribute("mode", "confiMail");
			return true;
		} else {
			model.addAttribute("message", "そのメールアドレスは既に使用されています");
			model.addAttribute("mode", "start");
			return false;
		}
	}
	
	public String confiMail(Model model, String mode) {
		byte[] decodedBytes = Base64.getUrlDecoder().decode(mode);
		String userMail = new String(decodedBytes);
		mode = "inputStart";
		model.addAttribute("userMail", userMail);
		model.addAttribute("mode", "inputStart");
		return mode;
	}
}
