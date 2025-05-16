package edu.kh.project;

import java.util.List;

import edu.kh.project.board.model.dto.BoardImg;
import edu.kh.project.board.model.dto.Comment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@toString
public class Board {
    private int boardNo;
    private String boardTitle;
    private String boardContent;
    private String boardWriteDate;
    private String boardUpdateDate;
    private char boardDelFl;
    private int boardCode;         // FK: BOARD_TYPE
    private int memberNo;          // FK: MEMBER

    // 관계 필드
    private String boardTypeName;  // 조인용 (옵션)
    private String memberNickname; // 조인용 (옵션)
    
	// 목록 조회시 상관 쿼리 결과
	private int commentCount; // 댓글 수
	
	// 게시글 작성자 프로필 이미지
	private String profileImg;
	
	// 게시글 목록 썸네일 이미지
	private String thumbnail;
	
	//특정 게시글 작성된 목록 리스트
	private List<Comment> commentList;
	
}
