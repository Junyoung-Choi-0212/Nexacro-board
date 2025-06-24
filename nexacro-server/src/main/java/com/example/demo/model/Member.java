package com.example.demo.model;

import java.util.Date;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class Member {
	@Id
    private String id;  // 사용자 ID (ex. user01)
	private String name; // 사용자 이름
    private String pw;  // 비밀번호
    private String email; // 사용자 이메일
    private boolean is_admin; // 관리자 여부
    private Date join_date; // 가입일
}