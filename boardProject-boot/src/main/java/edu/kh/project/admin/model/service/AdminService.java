package edu.kh.project.admin.model.service;

import java.util.List;

import edu.kh.project.board.model.dto.Board;
import edu.kh.project.member.model.dto.Member;

public interface AdminService {

	/** 관리자 로그인
	 * @param inputMember
	 * @return
	 */
	Member login(Member inputMember);

	/** 최대 조회수 게시글 조회
	 * @return
	 */
	Board maxReadCount();

	/** 최대 좋아요수 게시글 조회
	 * @return
	 */
	Board maxLikeCount();

	/** 최대 댓글수 게시글 조회
	 * @return
	 */
	Board maxCommentCount();

	/** 새 회원 조회 서비스
	 * @return
	 */
	List<Member> newMember();
	/** 새 회원 수 조회
	 * @return
	 */
	int newMemberCount();

	/** 탈퇴한 회원 목록 조회
	 * @return
	 */
	List<Member> withDrawnMemberList();

	/** 삭제한 게시글 목록 조회
	 * @return
	 */
	List<Board> selectDelteBoardList();

	/** 게시글 복구
	 * @param boardNo
	 * @return
	 */
	int restoreBoard(int boardNo);

	/** 탈퇴한 회원 복구
	 * @param memberNo
	 * @return
	 */
	int restoreMember(int memberNo);

	/** 관리자 이메일 중복확인 검사 서비스
	 * @param memberEmail
	 * @return
	 */
	int checkEmail(String memberEmail);

	/** 관리자 계정발급 서비스
	 * @param member
	 * @return
	 */
	String createAdminAccount(Member member);

	/** 관리자 계정 조회
	 * @return
	 */
	List<Member> AdminAccount();

}
