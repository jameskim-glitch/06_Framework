package edu.kh.project.email.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import edu.kh.project.email.model.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("email")
@RequiredArgsConstructor
@Slf4j
public class EmailController {

	private final EmailService service;
	
	@ResponseBody
	@PostMapping("signup")
	public int signUp(@RequestBody String email) {
		
		log.debug("email : " + email);
		String authKey = service.sendEmail("signup", email);
		
		if(authKey != null) { // 인증번호 발급 성공 & 이메일 보내기 성공
			return 1;
		}
		// 이메일 보내기 실패
		return 0;
	}
	
	
	
	
}
