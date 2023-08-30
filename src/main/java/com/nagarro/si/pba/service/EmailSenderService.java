package com.nagarro.si.pba.service;

import com.nagarro.si.pba.dto.UserDTO;
import org.springframework.stereotype.Service;

@Service
public interface EmailSenderService {
	void sendRegistrationEmail(UserDTO userDTO, String token);

	void sendResetPasswordEmail(UserDTO userDTO, String token);
}
