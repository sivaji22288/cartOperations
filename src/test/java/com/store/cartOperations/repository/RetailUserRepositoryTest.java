package com.store.cartOperations.repository;

import com.store.cartOperations.domain.RetailUser;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class RetailUserRepositoryTest {

    @Autowired
    RetailUserRepository retailUserRepository;

    @Autowired
    TestEntityManager testEntityManager;

    @Test
    void testFindByName() {
        // Given
        RetailUser retailUser = RetailUser.builder().id(null).name("John").email("test@tmail.com")
                .isAffiliated(false).isAffiliated(false).registeredOn(new Date()).build();
        retailUser = testEntityManager.persistFlushFind(retailUser);

        // When
        RetailUser found = retailUserRepository.findByName("John");

        // Then
        assertThat(found.getId()).isNotNull();
        assertThat(found.getName()).isEqualTo(retailUser.getName());
    }

}
