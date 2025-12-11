package uim.fei.stuba.sk.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uim.fei.stuba.sk.model.UserEntity;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    UserEntity findByEmail(String email);
    UserEntity findByUsername(String username);

    UserEntity findFirstByUsername(String username);
}
