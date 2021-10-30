package com.example.demo.controller;

import java.io.UnsupportedEncodingException;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.demo.model.User;
import com.example.demo.service.CustomerNotFoundException;
import com.example.demo.service.UserService;

import net.bytebuddy.utility.RandomString;

@Controller
public class ForgotPasswordController {

	@Autowired
	private JavaMailSender mailSender;
	
	@Autowired
	private UserService userService;
	
	private Boolean emailEnviado = false;

	@GetMapping("/forgot_password")
	public String showForgotPasswordForm(Model model) {
		return "forgot_password_form";
	}
	
	
	@PostMapping("/forgot_password")
	public String processForgotPassword(HttpServletRequest request, Model model) throws CustomerNotFoundException, MessagingException, UnsupportedEncodingException {
		String email = request.getParameter("email");
		String token = RandomString.make(30);
		
		try {
			userService.updateResetPasswordToken(token, email);
			String resetPasswordLink = Utility.getSiteURL(request) + "/reset_password?token=" + token;
	        sendEmail(email, resetPasswordLink);
	        if(emailEnviado)
	        	model.addAttribute("mensagemEmail", emailEnviado);
	        emailEnviado = false;
			
		} catch (UsernameNotFoundException | CustomerNotFoundException e) {
			model.addAttribute("error", "Campo obrigatório!");
		}
		
		return "forgot_password_form";
	}
	
	public void sendEmail(String recipientEmail, String link) throws MessagingException, UnsupportedEncodingException{
		MimeMessage message = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message);
		
		helper.setFrom("contato@recuperacao.com", "Recuperação de senha");
	    helper.setTo(recipientEmail);
	     
	    String subject = "Aqui está o link para redefinir sua senha";
	     
	    String content = "<p>Olá,</p>"
	            + "<p>Você requisitou a redefinição de senha.</p>"
	            + "<p>Clique no link abaixo para alterar sua senha:</p>"
	            + "<p><a href=\"" + link + "\">Alterar minha senha</a></p>"
	            + "<br>"
	            + "<p>Ignore esta mensagem se você lembra de sua senha, "
	            + "ou não fez esta requisição.</p>";
	     
	    helper.setSubject(subject);
	     
	    helper.setText(content, true);
	     
	    mailSender.send(message);
	    this.emailEnviado = true;
	}
	
	@GetMapping("/reset_password")
	public String showResetPasswordForm(@Param(value = "token") String token, Model model) {
		User user = userService.getByResetPasswordToken(token);
	    model.addAttribute("token", token);
	    
	    if (user == null) {
	        model.addAttribute("message", "Token Inválido");
	        return "message";
	    }
	     
	    return "reset_password_form";
	}
	
	@PostMapping("/reset_password")
	public String processResetPassword(HttpServletRequest request, Model model) {
		String token = request.getParameter("token");
	    String password = request.getParameter("password");
	     
	    User user = userService.getByResetPasswordToken(token);
	    model.addAttribute("title", "Redefinir sua senha");
	     
	    if (user == null) {
	        model.addAttribute("message", "Token inválido");
	        return "message";
	    } else {           
	        userService.updatePassword(user, password);
	         
	        model.addAttribute("message", "Você alterou sua senha com sucesso.");
	    }
	     
	    return "login";
	}
}