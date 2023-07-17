package com.example.stockc.service;

import com.example.stockc.domain.member.Member;
import com.example.stockc.repository.jpa.MemberRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class UserService {

    @Autowired
    private MemberRepository memberRepository;

    // 맴버 추가
    public void addUser(Member member){
        log.info("UserService member = {}", member);
        memberRepository.save(member);
    }

    public Member login(Member member){

        Member member1 = memberRepository.findByLoginId(member.getLoginId());
        if(member1.getPassword().equals(member.getPassword())){
            return member1;
        }else
        {
            return null;
        }

    }

}
