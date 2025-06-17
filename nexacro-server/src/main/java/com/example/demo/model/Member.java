package com.example.demo.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class Member {

	@Id
    private String id;  // 사용자 ID (ex. user01)
    private String pw;  // 비밀번호
}