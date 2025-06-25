package com.example.demo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
        factory.setNamespaceAware(true);
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(new InputSource(new StringReader(xml)));

        // 3. <Row> 찾기
        NodeList rowList = document.getElementsByTagNameNS("*", "Row");
        if (rowList.getLength() == 0) {
            throw new RuntimeException("No <Row> found in XML.");
        }

        Element row = (Element) rowList.item(0);
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

        // 4. 응답 설정
        response.setContentType("text/xml; charset=UTF-8");
        PrintWriter out = response.getWriter();

        out.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        out.println("<Root xmlns=\"http://www.nexacroplatform.com/platform/dataset\">");

        if (result.isPresent()) {
            Member member = result.get();

            // ✅ Dataset 형식으로 응답
            out.println("<Dataset id=\"output\">");
            out.println("  <ColumnInfo>");
            out.println("    <Column id=\"name\" type=\"STRING\" size=\"256\"/>");
            out.println("    <Column id=\"isAdmin\" type=\"STRING\" size=\"10\"/>");
            out.println("  </ColumnInfo>");
            out.println("  <Rows>");
            out.println("    <Row>");
            out.println("      <Col id=\"name\">" + member.getName() + "</Col>");
            out.println("      <Col id=\"isAdmin\">" + member.isAdmin() + "</Col>");
            out.println("    </Row>");
            out.println("  </Rows>");
            out.println("</Dataset>");

            out.println("<Parameters>");
            out.println("  <Parameter id=\"ErrorCode\" type=\"int\">0</Parameter>");
            out.println("  <Parameter id=\"ErrorMsg\" type=\"string\">SUCC</Parameter>");
            out.println("</Parameters>");
        } else {
            // 실패 응답
            out.println("<Parameters>");
            out.println("  <Parameter id=\"ErrorCode\" type=\"int\">-1</Parameter>");
            out.println("  <Parameter id=\"ErrorMsg\" type=\"string\">해당하는 유저가 없습니다.</Parameter>");
            out.println("</Parameters>");
        }

        out.println("</Root>");
    }
    
    @GetMapping("/members")
    public void getMembers(HttpServletResponse response) throws IOException {
        List<Member> members = repo.findAll();

        response.setContentType("text/xml; charset=UTF-8");
        PrintWriter out = response.getWriter();

        out.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        out.println("<Root xmlns=\"http://www.nexacroplatform.com/platform/dataset\">");

        out.println("<Dataset id=\"output\">");
        out.println("  <ColumnInfo>");
        out.println("    <Column id=\"id\" type=\"STRING\" size=\"256\"/>");
        out.println("    <Column id=\"name\" type=\"STRING\" size=\"256\"/>");
        out.println("    <Column id=\"email\" type=\"STRING\" size=\"256\"/>");
        out.println("    <Column id=\"isAdmin\" type=\"STRING\" size=\"10\"/>"); // ✅ 관리자 여부 포함
        out.println("  </ColumnInfo>");
        out.println("  <Rows>");
        for (Member m : members) {
            out.println("    <Row>");
            out.println("      <Col id=\"id\">" + m.getId() + "</Col>");
            out.println("      <Col id=\"name\">" + m.getName() + "</Col>");
            out.println("      <Col id=\"email\">" + m.getEmail() + "</Col>");
            out.println("      <Col id=\"isAdmin\">" + m.isAdmin() + "</Col>"); // ✅ 관리자 여부 포함
            out.println("    </Row>");
        }
        out.println("  </Rows>");
        out.println("</Dataset>");

        out.println("<Parameters>");
        out.println("  <Parameter id=\"ErrorCode\" type=\"int\">0</Parameter>");
        out.println("  <Parameter id=\"ErrorMsg\" type=\"string\">SUCCESS</Parameter>");
        out.println("</Parameters>");

        out.println("</Root>");
    }
    
    @Transactional
    @PostMapping("/update")
    public void updateMembers(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String xml = new BufferedReader(new InputStreamReader(request.getInputStream(), StandardCharsets.UTF_8))
            .lines().collect(Collectors.joining("\n"));

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new InputSource(new StringReader(xml)));

        NodeList rows = doc.getElementsByTagNameNS("*", "Row");
        for (int i = 0; i < rows.getLength(); i++) {
            Element row = (Element) rows.item(i);
            String id = null;
            String name = null;
            String email = null;
            boolean isAdmin = false;

            NodeList cols = row.getElementsByTagNameNS("*", "Col");
            for (int j = 0; j < cols.getLength(); j++) {
                Element col = (Element) cols.item(j);
                switch (col.getAttribute("id")) {
                    case "id": id = col.getTextContent(); break;
                    case "name": name = col.getTextContent(); break;
                    case "email": email = col.getTextContent(); break;
                    case "isAdmin":
                        String val = col.getTextContent();
                        isAdmin = "1".equals(val) || "true".equalsIgnoreCase(val);
                        break;
                }
            }

            if (id != null) {
                Member member = repo.findById(id).orElse(null);
                if (member != null) {
                    member.setName(name);
                    member.setEmail(email);
                    member.setAdmin(isAdmin);
                    repo.save(member);
                }
            }
        }

        response.setContentType("text/xml; charset=UTF-8");
        PrintWriter out = response.getWriter();
        out.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        out.println("<Root xmlns=\"http://www.nexacroplatform.com/platform/dataset\">");
        out.println("<Parameters>");
        out.println("<Parameter id=\"ErrorCode\" type=\"int\">0</Parameter>");
        out.println("<Parameter id=\"ErrorMsg\" type=\"string\">SUCCESS</Parameter>");
        out.println("</Parameters>");
        out.println("</Root>");
    }
}