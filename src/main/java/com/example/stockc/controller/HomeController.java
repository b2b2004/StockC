package com.example.stockc.controller;

import com.example.stockc.domain.member.Member;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home(HttpServletRequest request, Model model) {

        // 세션에 회원 데이터가 없으면 home
        HttpSession session = request.getSession(false);
        if (session == null) {
            return "/home";
        }

        Member loginMember = (Member) session.getAttribute("loginMember");
        //세션에 회원 데이터가 없으면 home
        if (loginMember == null) {
            return "home";
        }

        // 세션이 유지되면 로그인으로 이동
        model.addAttribute("member", loginMember);
        return "members/loginHome";

    }
}
