package test.springbootrest.service;

import test.springbootrest.model.Role;

import java.util.List;
import java.util.Optional;

public interface RoleService {

    boolean addRole(Role role);

    void deleteRoleById(Long id);

    boolean updateRole(Role role);

    List<Role> getAllRoles();

    Role getRoleById(Long id);

    Role getRoleByName(String name);
}
