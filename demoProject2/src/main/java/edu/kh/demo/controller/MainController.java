package edu.kh.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller // 요청/응답 제어 역할 명시 + Bean 등록
public class MainController {

	// "/" 주소 요청시 main.html 파일로 forward
	
	@RequestMapping("/")
	public String mainPage() {
		
		//forward : 요청 위임
		// thymleaf : Spring Boot 에서 사용하는 템플릿 엔진(html 파일 사용)
		
		// thymleaf 를 이용한 html 파일로 forward시
		// 사용되는 접두사, 접미사 존재
		
		// 접두사 : classpath:/templates/
		// 접미사 : .html
		
		// -> clsspath:/templates/commom/main.html
		return "common/main";
	}
}
