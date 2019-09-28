package test.springbootrest.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import test.springbootrest.exception.IsAlreadyExistException;
import test.springbootrest.exception.NotFoundException;
import test.springbootrest.model.User;
import test.springbootrest.service.UserService;

import java.security.InvalidParameterException;

@RestController
@RequestMapping("/rest")
//@PreAuthorize("hasAnyAuthority('admin')")
public class RestCrudController {
    private final UserService userService;

    public RestCrudController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<?> addUser(@RequestBody User user) {
        try {
            userService.addUser(user);
            return ResponseEntity
                    .created(ServletUriComponentsBuilder.fromCurrentRequest()
                            .path("/{id}")
                            .buildAndExpand(user)
                            .toUri())
                    .build();
        } catch (IsAlreadyExistException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "user is already exist", e);
        } catch (InvalidParameterException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "parameters incorrect", e);
        }
    }

    @GetMapping("/id={id}")
    public ResponseEntity<?> getUniqueUser(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(userService.getUserById(id));
        } catch (NotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "user not found", e);
        }
    }

    @GetMapping("/login={login}")
    public ResponseEntity<?> getUniqueUser(@PathVariable String login) {
        try {
            return ResponseEntity.ok(userService.getUserByLogin(login));
        } catch (NotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "user not found", e);
        }
    }


    @GetMapping
    public ResponseEntity<?> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @PutMapping
    public ResponseEntity<?> updateUser(@RequestBody User user) {
        try {
            userService.updateUser(user);
            return ResponseEntity.ok().build();
        } catch (IsAlreadyExistException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "user with the login(" + user.getLogin() + ") is already exist", e);
        } catch (NotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "user not found", e);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
            userService.deleteUserById(id);
            return ResponseEntity.ok().build();
    }
}
