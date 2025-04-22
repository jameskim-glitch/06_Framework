package edu.kh.project.member.model.sevice;

import edu.kh.project.member.model.dto.Member;

public interface MemberService {

	/** 로그인 서비스
	 * @param inputMember
	 * @return loginMember(member)
	 */
	Member login(Member inputMember);

	/** 이메일 중복 검사 서비스
	 * @param memberEmail
	 * @return
	 */
	int checkEmail(String memberEmail);

}
