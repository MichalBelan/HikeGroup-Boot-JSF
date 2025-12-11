package uim.fei.stuba.sk.service;

import uim.fei.stuba.sk.dto.RegistrationDto;
import uim.fei.stuba.sk.model.UserEntity;

public interface UserService {
    void saveUser(RegistrationDto registrationDto);
    UserEntity findByUsername(String username);
    UserEntity findByEmail(String email);
}
