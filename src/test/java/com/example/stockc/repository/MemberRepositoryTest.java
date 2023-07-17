package com.example.stockc.repository;

import com.example.stockc.domain.member.Member;
import com.example.stockc.repository.jpa.MemberRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@SpringBootTest
public class MemberRepositoryTest {

    @Autowired
    MemberRepository memberRepository;

    @Test
    public void InsertMember(){
        Member member = Member.builder()
                .loginId("user1")
                .name("권용호")
                .password("123123")
                .build();

        memberRepository.save(member);
    }

    @Test
    public void SelectMember(){
        Long id = 2L;
        Member member = memberRepository.findById(id).orElseThrow();
        log.info("member={}", member);
        assertThat(member.getLoginId()).isEqualTo("user1");
    }

    @Test
    public void UpdateMember(){
        Member member = Member.builder()
                .id(2L)
                .loginId("user2")
                .name("권용호")
                .password("123123")
                .build();
        memberRepository.save(member);
    }

    @Test
    public void DeleteMember(){
        Long id = 1L;
        memberRepository.deleteById(id);
    }

}
