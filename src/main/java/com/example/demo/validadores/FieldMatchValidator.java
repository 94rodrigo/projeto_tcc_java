package com.example.demo.validadores;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

public class FieldMatchValidator implements ConstraintValidator<FieldMatch, Object>{

	private String firstFieldName;
	private String secondFieldName;
	private String message;
	
	@Override
	public void initialize(FieldMatch constraintAnnotation) {
		this.firstFieldName = constraintAnnotation.field();
		this.secondFieldName = constraintAnnotation.fieldMatch();
		this.message = constraintAnnotation.message();
	}
	
	@Override
	public boolean isValid(Object value, ConstraintValidatorContext context) {
		BeanWrapper beanWrapper = new BeanWrapperImpl(value);
		Object fieldValue = beanWrapper.getPropertyValue(this.firstFieldName);
		Object fieldMatchValue = beanWrapper.getPropertyValue(this.secondFieldName);
		
		boolean isValid = false;
		if (fieldValue != null) {
			isValid = fieldValue.equals(fieldMatchValue);
		}
		if(!isValid) {
			context.disableDefaultConstraintViolation();
			context
				.buildConstraintViolationWithTemplate(message)
				.addPropertyNode(firstFieldName)
				.addConstraintViolation();
			context
				.buildConstraintViolationWithTemplate(message)
				.addPropertyNode(secondFieldName)
				.addConstraintViolation();
		}
		return isValid;
	}
}
