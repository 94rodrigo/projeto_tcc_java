package com.example.demo.validadores;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.passay.CharacterRule;
import org.passay.EnglishCharacterData;
import org.passay.LengthRule;
import org.passay.MessageResolver;
import org.passay.PasswordData;
import org.passay.PasswordValidator;
import org.passay.PropertiesMessageResolver;
import org.passay.RuleResult;

public class PasswordConstraintValidator implements ConstraintValidator<ValidPassword, String>{

	@Override
	public void initialize(ValidPassword arg0) {
	}
	
	@Override
	public boolean isValid(String password, ConstraintValidatorContext context) {
		LengthRule lengthRule = new LengthRule();
		lengthRule.setMinimumLength(8);
		URL resource = this.getClass().getClassLoader().getResource("messages.properties");
		Properties props = new Properties();
		try {
			props.load(new FileInputStream(resource.getPath()));
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		MessageResolver resolver = new PropertiesMessageResolver(props);
		PasswordValidator validator = new PasswordValidator(resolver, Arrays.asList(
			lengthRule,
			new CharacterRule(EnglishCharacterData.LowerCase, 1),
			new CharacterRule(EnglishCharacterData.Digit, 1)
		));
		RuleResult result = validator.validate(new PasswordData(password));
		
		if (result.isValid())
			return true;
		
		List<String> messages = validator.getMessages(result);
		String messageTemplate = messages.stream()
			.collect(Collectors.joining(", "));
		context.buildConstraintViolationWithTemplate(messageTemplate)
			.addConstraintViolation()
			.disableDefaultConstraintViolation();
		return false;
	}

}
