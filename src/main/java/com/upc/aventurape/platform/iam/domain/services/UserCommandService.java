package com.upc.aventurape.platform.iam.domain.services;

import com.upc.aventurape.platform.iam.domain.model.commands.UpdateProofingEntrepreneureCommand;
import org.apache.commons.lang3.tuple.ImmutablePair;
import com.upc.aventurape.platform.iam.domain.model.aggregates.User;
import com.upc.aventurape.platform.iam.domain.model.commands.SignInCommand;
import com.upc.aventurape.platform.iam.domain.model.commands.SignUpCommand;

import java.util.Optional;

public interface UserCommandService {
  Optional<ImmutablePair<User, String>> handle(SignInCommand command);
  Optional<User> handle(SignUpCommand command);
  void updateProofingEntrepreneure(UpdateProofingEntrepreneureCommand command);
}