package com.upc.aventurape.platform.iam.infrastructure.persistence.jpa.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.upc.aventurape.platform.iam.domain.model.aggregates.User;

import java.util.Optional;

/**
 * This interface is responsible for providing the User entity related operations.
 * It extends the JpaRepository interface.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

  /**
   * This method is responsible for finding the user by username.
   * @param username The username.
   * @return The user object.
   */
  Optional<User> findByUsername(String username);

  /**
   * This method is responsible for checking if the user exists by username.
   * @param username The username.
   * @return True if the user exists, false otherwise.
   */
  boolean existsByUsername(String username);

  /**
   * This method is responsible for finding the user by email.
   * @param email The email.
   * @return The user object.
   */
  Optional<User> findByEmail(String email);

  /**
   * This method is responsible for checking if the user exists by email.
   * @param email The email.
   * @return True if the user exists, false otherwise.
   */
  boolean existsByEmail(String email);



}