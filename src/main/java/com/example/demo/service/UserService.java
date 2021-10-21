package com.example.demo.service;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;

@Service("userService")
@Transactional
public class UserService {

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	public UserService(UserRepository userRepository) {
		this.userRepository = userRepository;
	}
	
	public User findByEmail(String email) {
		return userRepository.findByEmail(email);
	}
	
	public void saveUser(User user) {
		userRepository.save(user);
	}
	
	public void updateResetPasswordToken(String token, String email) throws CustomerNotFoundException {
        User user = userRepository.findByEmail(email);
        if (user != null) {
        	user.setResetPasswordToken(token);
            userRepository.save(user);
        } else {
            throw new CustomerNotFoundException("Could not find any customer with the email " + email);
        }
    }
     
    public User getByResetPasswordToken(String token) {
        return userRepository.findByResetPasswordToken(token);
    }
     
    public void updatePassword(User user, String newPassword) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encodedPassword = passwordEncoder.encode(newPassword);
        user.setSenha(encodedPassword);
        user.setConfirmacaoSenha(encodedPassword);
        user.setResetPasswordToken(null);
        userRepository.save(user);
    }
    
    public void updateUsuario(User user, Principal principal) {
    	user = userRepository.findByEmail(principal.getName());
    	user.setAtividades(user.getAtividades());
    	user.setAtividadesQueUsuariosParticipa(user.getAtividadesQueUsuariosParticipa());
    	user.setSenha(user.getSenha());
    	user.setConfirmacaoSenha(user.getSenha());
    	user.setEmail(user.getEmail());
    	user.setEnabled(true);
    	user.setMunicipio(user.getMunicipio());
    	user.setPrimeiroNome(user.getPrimeiroNome());
    	user.setRoles(user.getRoles());
    	user.setUf(user.getUf());
    	user.setUltimoNome(user.getUltimoNome());
    }
}