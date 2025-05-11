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

  /**
   * This method checks if a user has the ROLE_ENTREPRENEUR role based on role_id = 3.
   * @param userId The user ID.
   * @return True if the user has the entrepreneur role, false otherwise.
   */
  @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM User u JOIN u.roles r WHERE u.id = :userId AND r.name = 'ROLE_ENTREPRENEUR'")
  boolean hasEntrepreneurRole(Long userId);

}