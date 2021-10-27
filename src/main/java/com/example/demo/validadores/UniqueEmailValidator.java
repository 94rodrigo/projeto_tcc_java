package com.example.demo.validadores;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.demo.repository.UserRepository;

@Component
public class UniqueEmailValidator implements ConstraintValidator<UniqueEmail, String>{
	
	@Autowired
	UserRepository userRepository;

	@Override
	public boolean isValid(String email, ConstraintValidatorContext context) {
		return userRepository.findByEmail(email) == null;
	}

}
