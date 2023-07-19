package com.example.stockc.controller;

import com.example.stockc.domain.member.Member;
import com.example.stockc.model.KakaoProfile;
import com.example.stockc.model.OAuthToken;
import com.example.stockc.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

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

    @GetMapping("/auth/kakao/callback")
    public String kakaoCallback(String code) {

        RestTemplate rt = new RestTemplate();
        // HttpHeader 오브젝트 생성
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
        // HttpBody 오브젝트 생성
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", "8b64315e95340c7ad710269f872d1f50");
        params.add("redirect_uri", "http://localhost:8080/auth/kakao/callback");
        params.add("code", code);

        // HttpHeader와 HttpBody를 하나의 오브젝트에 담기
        HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest = new HttpEntity<>(params, headers);

        // Http 요청하기 - Post방식으로 - 그리고 response 변수의 응답 받음.
        ResponseEntity<String> response = rt.exchange(
                "https://kauth.kakao.com/oauth/token",
                HttpMethod.POST,
                kakaoTokenRequest,
                String.class
        );

        ObjectMapper objectMapper = new ObjectMapper();
        OAuthToken oauthToken = null;

        try {
            oauthToken = objectMapper.readValue(response.getBody(), OAuthToken.class);
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        System.out.println("카카오 엑세스 토큰 : "+oauthToken.getAccess_token());

        RestTemplate rt2 = new RestTemplate();

        // HttpHeader 오브젝트 생성
        HttpHeaders headers2 = new HttpHeaders();
        headers2.add("Authorization", "Bearer "+oauthToken.getAccess_token());
        headers2.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        // HttpHeader와 HttpBody를 하나의 오브젝트에 담기
        HttpEntity<MultiValueMap<String, String>> kakaoProfileRequest2 = new HttpEntity<>(headers2);

        // Http 요청하기 - Post방식으로 - 그리고 response변수의 응답 받음.
        ResponseEntity<String> response2 = rt2.exchange(
                "https://kapi.kakao.com/v2/user/me",
                HttpMethod.POST,
                kakaoProfileRequest2,
                String.class
        );
        System.out.println(response2.getBody());

        ObjectMapper objectMapper2 = new ObjectMapper();
        KakaoProfile kakaoProfile = null;
        try {
            kakaoProfile = objectMapper2.readValue(response2.getBody(), KakaoProfile.class);
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        System.out.println("kakaoProfile = " + kakaoProfile);
        // Member 오브젝트 : username, password, email

        // 비밀번호 난수 설정
        String pw = UUID.randomUUID().toString();
        String pw1 = pw.split("-")[0];

        System.out.println(pw1);

        Member kakaoUser = Member.builder()
                .name(kakaoProfile.getProperties().getNickname())
                .loginId("kakao_" + kakaoProfile.getId())
                .password("!A" + pw1)
                .build();

        // 가입자 혹은 비가입자 체크 해서 처리
        Member originUser = userService.findById("kakao_" + kakaoUser.getId());

        if(originUser.getLoginId() == null) {
            System.out.println("기존 회원이 아니기에 자동 회원가입을 진행합니다");
            userService.addUser(kakaoUser);
        }


        return "redirect:/";
    }

}
