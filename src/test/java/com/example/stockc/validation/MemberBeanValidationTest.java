package com.example.stockc.validation;

import com.example.stockc.domain.member.Member;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Set;

@Slf4j
@SpringBootTest
public class MemberBeanValidationTest {

    @Test
    public void beanValidation(){
        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        Validator validator = validatorFactory.getValidator();


        Member member = Member.builder()
                .loginId(" ")
                .password("1234")
                .name("권용호")
                .build();

        Set<ConstraintViolation<Member>> violations = validator.validate(member);

        for (ConstraintViolation<Member> violation : violations) {
            System.out.println("violation = " + violation);
            System.out.println("violation.message = " + violation.getMessage());
        }
    }
}
