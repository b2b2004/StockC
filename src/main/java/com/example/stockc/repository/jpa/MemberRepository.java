package com.example.stockc.repository.jpa;


import com.example.stockc.domain.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Member findByLoginId(String loginUser);
}
