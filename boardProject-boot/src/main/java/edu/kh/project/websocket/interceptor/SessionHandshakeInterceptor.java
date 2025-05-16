package edu.kh.project.websocket.interceptor;

import java.util.Map;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import jakarta.servlet.http.HttpSession;

/** SessionHandshakeInterceptor 클래스
 * 
 * WebSocketHandlet 가 동작하기 전/후에
 * 연결된 클라이언트 세션을 가로채는 동작을 작성할 클래스
 * 
 */

@Component // Bean 등록
public class SessionHandshakeInterceptor implements HandshakeInterceptor {

	// beforeHandshake : 핸들러 동작 전에 수행되는 메서드
	@Override
	public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
			Map<String, Object> attributes) throws Exception {
		// ServerHttpRequest : HttpServletRequest 의 부모 인터페이스
		// ServerHttpResponse : HttpServletResponse 의 부모 인터페이스
		
		// request 가 참조하는 객체가 ServletServerHttpRequest 다운캐스팅이 가능한가?
		// ServerHttpRequest -> ServletServerHttpRequest ->HttpServletRequest
		if(request instanceof ServletServerHttpRequest) {
			// 다운 캐스팅 수행
			ServletServerHttpRequest servletRequest = (ServletServerHttpRequest)request;
			
			// 웹 소켓 동작을 요청한 클라이언트의 세션을 얻어옴
			HttpSession session = servletRequest.getServletRequest().getSession();
			
			// 가로챈 세션을 Handler 에 전달할 수 있게 세팅
			attributes.put("session", session);
		}
		// 가로채기 진행 여부 : true 로 작성해야 세션을 가로채서 Handler 에게 전달 가능
		return true;
	}

	// afterHandshake : 핸들러 동작 후에 수행되는 메서드
	@Override
	public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
			Exception exception) {
		// TODO Auto-generated method stub
		
	}

	// HandShake : 클라이언트와 서버가 WebSocket 연결을 수립하기위해 Http 프로토콜을 통해 수행하는 초기단계
	// 				-> 기존 HTTP 연결을 WebSocket 연결로 변경.
}
