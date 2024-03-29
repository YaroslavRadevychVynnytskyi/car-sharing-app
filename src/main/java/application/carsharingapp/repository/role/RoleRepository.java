package application.carsharingapp.repository.role;

import application.carsharingapp.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Role getByName(Role.RoleName name);
}
