package com.upc.aventurape.platform.iam.application.internal.commandservices;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.upc.aventurape.platform.iam.application.internal.outboundservices.hashing.HashingService;
import com.upc.aventurape.platform.iam.application.internal.outboundservices.tokens.TokenService;
import com.upc.aventurape.platform.iam.domain.model.aggregates.User;
import com.upc.aventurape.platform.iam.domain.model.commands.SignInCommand;
import com.upc.aventurape.platform.iam.domain.model.commands.SignUpCommand;
import com.upc.aventurape.platform.iam.domain.model.commands.UpdateProofingEntrepreneureCommand;
import com.upc.aventurape.platform.iam.domain.services.UserCommandService;
import com.upc.aventurape.platform.iam.infrastructure.persistence.jpa.repositories.RoleRepository;
import com.upc.aventurape.platform.iam.infrastructure.persistence.jpa.repositories.UserRepository;

import java.util.Optional;

@Service
public class UserCommandServiceImpl implements UserCommandService {

  private final UserRepository userRepository;
  private final HashingService hashingService;
  private final TokenService tokenService;
  private final RoleRepository roleRepository;

  public UserCommandServiceImpl(UserRepository userRepository, HashingService hashingService,
                                TokenService tokenService, RoleRepository roleRepository) {

    this.userRepository = userRepository;
    this.hashingService = hashingService;
    this.tokenService = tokenService;
    this.roleRepository = roleRepository;
  }

  @Override
  public Optional<ImmutablePair<User, String>> handle(SignInCommand command) {
    var user = userRepository.findByUsername(command.username());
    if (user.isEmpty())
      throw new RuntimeException("User not found");
    if (!hashingService.matches(command.password(), user.get().getPassword()))
      throw new RuntimeException("Invalid password");

    var token = tokenService.generateToken(user.get().getUsername());
    return Optional.of(ImmutablePair.of(user.get(), token));
  }

  @Override
  public Optional<User> handle(SignUpCommand command) {
    if (userRepository.existsByUsername(command.username()))
      throw new RuntimeException("Username already exists");
    if (userRepository.existsByEmail(command.email()))
      throw new RuntimeException("Email already exists");
    var roles = command.roles().stream()
            .map(role ->
                    roleRepository.findByName(role.getName())
                            .orElseThrow(() -> new RuntimeException("Role name not found")))
            .toList();
    var user = new User(command.username(), hashingService.encode(command.password()), command.email(), roles);
    userRepository.save(user);
    return userRepository.findByUsername(command.username());
  }

  @Override
  @Transactional
  public void updateProofingEntrepreneure(UpdateProofingEntrepreneureCommand command) {
    // Validar si el usuario tiene el rol ROLE_ENTREPRENEUR
    if (!userRepository.hasEntrepreneurRole(command.userId())) {
      throw new IllegalArgumentException("Access denied: The user does not have the entrepreneur role.");
    }

    var userOptional = userRepository.findById(command.userId());
    if (userOptional.isEmpty()) {
      throw new IllegalArgumentException("User not found with ID: " + command.userId());
    }

    var user = userOptional.get();
    user.setProofingEntrepreneure(command.proofingEntrepreneure());
    userRepository.save(user);
  }
}