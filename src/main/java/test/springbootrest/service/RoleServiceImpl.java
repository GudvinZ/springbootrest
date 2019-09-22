package test.springbootrest.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import test.springbootrest.model.Role;
import test.springbootrest.repository.RoleRepository;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class RoleServiceImpl implements RoleService {
    private final RoleRepository roleRepository;

    public RoleServiceImpl(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public boolean addRole(Role role) {
        if (getRoleByName(role.getRoleName()) == null) {
            roleRepository.save(role);
            return true;
        } else
            return false;
    }

    @Override
    public void deleteRoleById(Long id) {
        roleRepository.deleteById(id);
    }

//    @Override
//    public void deleteAllRoles() {
//        dao.deleteAll();
//    }

    @Override
    public boolean updateRole(Role role) {
        Optional<Role> old = roleRepository.findById(role.getId());
        if(old.isPresent()) {
            old.get().setRoleName(role.getRoleName());
            return true;
        } else
            return false;
    }

    @Override
    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    @Override
    public Role getRoleById(Long id) {
        return roleRepository.findById(id).orElse(null);
    }

    @Override
    public Role getRoleByName(String name) {
        return roleRepository.findByRoleName(name).orElse(null);
    }
}
