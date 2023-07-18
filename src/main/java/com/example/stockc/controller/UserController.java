package com.example.stockc.controller;

import com.example.stockc.domain.member.Member;
import com.example.stockc.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Slf4j
@Controller
@RequiredArgsConstructor
public class UserController {

    @Autowired
    UserService userService;

    @GetMapping("/members/add")
    public String addForm(@ModelAttribute("member") Member member) {
        return "members/addMemberForm";
    }

    @PostMapping("/members/add")
    public String save(@Valid @ModelAttribute Member member, BindingResult result) {
        if (result.hasErrors()) {
            return "members/addMemberForm";
        }

        log.info("member = {}", member);
        userService.addUser(member);
        return "redirect:/";
    }

    @GetMapping("members/login")
    public String loginForm(@ModelAttribute("member") Member member) {
        return "members/loginForm";
    }

    @PostMapping("members/login")
    public String login(@ModelAttribute Member member, HttpServletRequest request, BindingResult bindingResult) {
        log.info("loginMember = {}", member);

        if (bindingResult.hasErrors()) {
            return "members/loginForm";
        }
        if (member.getLoginId().isEmpty() || member.getPassword().isEmpty()) {
            bindingResult.reject("loginFail", "아이디 또는 비밀번호를 입력해주세요.");
            return "members/loginForm";
        }

        Member loginMember = userService.login(member);

        if (loginMember == null) {
            bindingResult.reject("loginFail", "아이디 또는 비밀번호가 맞지 않습니다.");
            return "members/loginForm";
        }

        // 로그인 성공 시
        HttpSession session = request.getSession();
        log.info("member = {}", loginMember);

        // 세션에 로그인 회원 정보 보관
        session.setAttribute("loginMember", loginMember);
        return "redirect:/";
    }

    @PostMapping("/logout")
    public String logout(HttpServletRequest request){
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        return "redirect:/";
    }

}
