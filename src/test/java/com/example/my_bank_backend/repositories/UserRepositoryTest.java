package com.example.my_bank_backend.repositories;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import com.example.my_bank_backend.domain.user.User;
import com.example.my_bank_backend.dto.RegisterRequestDto;
import static org.assertj.core.api.Assertions.assertThat;

import jakarta.persistence.EntityManager;

@DataJpaTest
@ActiveProfiles("test")
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    @DisplayName("Should get successfully")
    void testFindByCpfCase1() {
        String cpf = "01234567899";
        RegisterRequestDto registerRequestDto = new RegisterRequestDto("Mateus", "teste@teste.com", "123456",
                "859999999", "01234567899", "25/10/2001");
        this.createUser(registerRequestDto);

        Optional<User> result = this.userRepository.findByCpf(cpf);

        assertThat(result).isPresent();
    }

    @Test
    @DisplayName("Should not get User from DB when user not exists")
    void testFindByCpfCase2() {
        String cpf = "01234567899";

        Optional<User> result = this.userRepository.findByCpf(cpf);

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Should get User By Email successfully")
    void testFindByEmail() {
        String email = "teste@teste.com";
        RegisterRequestDto registerRequestDto = new RegisterRequestDto("Mateus", email, "123456",
                "859999999", "01234567899", "25/10/2001");
        this.createUser(registerRequestDto);

        Optional<User> result = this.userRepository.findByEmail(email);

        assertThat(result).isPresent();
        assertThat(result.get().getEmail()).isEqualTo(email);
    }

    private User createUser(RegisterRequestDto registerRequestDto) {
        User newUser = new User(registerRequestDto);
        this.entityManager.persist(newUser);
        return newUser;
    }
}
