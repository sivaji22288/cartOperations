package com.store.cartOperations.repository;

import com.store.cartOperations.domain.RetailUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RetailUserRepository extends JpaRepository<RetailUser, Integer> {

    RetailUser findByName(String name);
}
