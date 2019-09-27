package test.springbootrest.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import test.springbootrest.exception.NotFoundException;
import test.springbootrest.model.Role;
import test.springbootrest.repository.RoleRepository;

import java.security.InvalidParameterException;
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
        if (role.getRoleName() == null || role.getRoleName().isEmpty())
            throw new InvalidParameterException();

        if (roleRepository.findByRoleName(role.getRoleName()).isPresent())
            return false;

        roleRepository.save(role);
        return true;
    }

    @Override
    public void deleteRoleById(Long id) {
        roleRepository.deleteById(id);
    }

    @Override
    public boolean updateRole(Role role) {
        if (role.getRoleName() == null || role.getRoleName().isEmpty())
            return false;

        return roleRepository.findById(role.getId())
                .map(r -> {
                    r.setRoleName(role.getRoleName());
                    return true;
                }).orElse(false);
    }

    @Override
    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    @Override
    public Role getRoleById(Long id) {
        return roleRepository.findById(id).orElseThrow(NotFoundException::new);
    }

    @Override
    public Role getRoleByName(String name) {
        return roleRepository.findByRoleName(name).orElseThrow(NotFoundException::new);
    }
}
