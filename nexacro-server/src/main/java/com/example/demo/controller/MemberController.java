package com.example.demo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.*;
import org.xml.sax.InputSource;

import com.example.demo.model.Member;
import com.example.demo.repository.MemberRepository;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class MemberController {

    private final MemberRepository repo;

    public MemberController(MemberRepository repo) {
        this.repo = repo;
    }

    @PostMapping("/login")
    public void login(HttpServletRequest request, HttpServletResponse response) throws Exception {
    	// 1. XML 읽기
        String xml = new BufferedReader(new InputStreamReader(request.getInputStream(), StandardCharsets.UTF_8))
                .lines().collect(Collectors.joining("\n"));

        // 2. DOM 파서 초기화
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true); // 네임스페이스 허용
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(new InputSource(new StringReader(xml)));

        // 3. <Row> 찾기
        NodeList rowList = document.getElementsByTagNameNS("*", "Row");
        if (rowList.getLength() == 0) {
            throw new RuntimeException("No <Row> found in XML.");
        }
        Element row = (Element) rowList.item(0);

        // 4. <Col>들 순회하며 id/pw 추출
        String id = null;
        String pw = null;

        NodeList cols = row.getElementsByTagNameNS("*", "Col");
        for (int i = 0; i < cols.getLength(); i++) {
            Element col = (Element) cols.item(i);
            String colId = col.getAttribute("id");
            String value = col.getTextContent();

            if ("id".equals(colId)) {
                id = value;
            } else if ("pw".equals(colId)) {
                pw = value;
            }
        }

        Optional<Member> result = repo.findByIdAndPw(id, pw);

        // 5. 응답 헤더
        response.setContentType("text/xml; charset=UTF-8");

        // 6. 응답 본문 작성
        PrintWriter out = response.getWriter();
        out.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        out.println("<Root xmlns=\"http://www.nexacroplatform.com/platform/dataset\">");
        out.println("<Parameters>");
        if (result.isPresent()) {
            out.println("<Parameter id=\"ErrorCode\" type=\"int\">0</Parameter>");
            out.println("<Parameter id=\"ErrorMsg\" type=\"string\">SUCC</Parameter>");
        } else {
            out.println("<Parameter id=\"ErrorCode\" type=\"int\">-1</Parameter>");
            out.println("<Parameter id=\"ErrorMsg\" type=\"string\">로그인 실패</Parameter>");
        }
        out.println("</Parameters>");
        out.println("</Root>");
    }
}