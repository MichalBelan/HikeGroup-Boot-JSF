package uim.fei.stuba.sk.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uim.fei.stuba.sk.model.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Role findByName(String name);

}
