package com.example.demo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.model.Member;

@Repository
public interface MemberRepository extends JpaRepository<Member, String> {
	// 로그인 시 사용할 메서드
    Optional<Member> findByIdAndPw(String id, String pw);
    List<Member> findAll();
}