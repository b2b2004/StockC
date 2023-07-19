package com.example.stockc.domain.member;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table
@Entity
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    @NotBlank(message = "아이디를 입력해주세요.")
    private String loginId;

    @Column
    @NotBlank(message = "이름을 입력해주세요.")
    private String name;

    @Column
    @Pattern(regexp = "(?=.*[0-9])(?=.*[a-zA-Z])(?=.*\\W)(?=\\S+$).{8,20}"
            , message = "패스워드는 대문자, 소문자, 특수문자가 적어도 하나씩은 있어야 하며 최소 8자리여야 하며 최대 20자리까지 가능합니다.")
    private String password;
}
