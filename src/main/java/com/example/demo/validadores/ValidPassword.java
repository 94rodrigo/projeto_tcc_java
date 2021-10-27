package com.example.demo.validadores;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

@Documented
@Constraint(validatedBy = PasswordConstraintValidator.class)
@Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidPassword {

	String message() default "Senha deve ter entre 8 e 30 caracteres, tendo pelo menos 1 dígito";
	Class<?>[] groups() default {};
	Class<? extends Payload>[] payload() default {};
}
