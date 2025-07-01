package com.example.demo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.demo.model.Member;

import jakarta.transaction.Transactional;

@Repository
public interface MemberRepository extends JpaRepository<Member, String> {
    Optional<Member> findByIdAndPw(String id, String pw); // 로그인
    List<Member> findAll(); // 회원 리스트
}