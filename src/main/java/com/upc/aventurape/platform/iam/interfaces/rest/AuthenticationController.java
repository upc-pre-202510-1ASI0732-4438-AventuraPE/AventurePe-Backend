package com.upc.aventurape.platform.iam.interfaces.rest;

import com.upc.aventurape.platform.iam.domain.services.RecaptchaService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.cors.CorsConfiguration;
import com.upc.aventurape.platform.iam.domain.services.UserCommandService;
import com.upc.aventurape.platform.iam.interfaces.rest.resources.AuthenticatedUserResource;
import com.upc.aventurape.platform.iam.interfaces.rest.resources.SignInResource;
import com.upc.aventurape.platform.iam.interfaces.rest.resources.SignUpResource;
import com.upc.aventurape.platform.iam.interfaces.rest.resources.UserResource;
import com.upc.aventurape.platform.iam.interfaces.rest.transform.AuthenticatedUserResourceFromEntityAssembler;
import com.upc.aventurape.platform.iam.interfaces.rest.transform.SignInCommandFromResourceAssembler;
import com.upc.aventurape.platform.iam.interfaces.rest.transform.SignUpCommandFromResourceAssembler;
import com.upc.aventurape.platform.iam.interfaces.rest.transform.UserResourceFromEntityAssembler;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping(value = "/api/v1/authentication", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Authentication", description = "Authentication Endpoints")
@CrossOrigin(origins = "*", maxAge = 3600, allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS})
public class AuthenticationController {

  private final UserCommandService userCommandService;
  private final RecaptchaService recaptchaService;

  public AuthenticationController(UserCommandService userCommandService, RecaptchaService recaptchaService) {
    this.recaptchaService = recaptchaService;
    this.userCommandService = userCommandService;
  }

  private boolean isWebRequest(HttpServletRequest request) {
    String userAgent = request.getHeader("User-Agent");
    return userAgent != null && (
        userAgent.contains("Mozilla") || 
        userAgent.contains("Chrome") || 
        userAgent.contains("Safari") || 
        userAgent.contains("Firefox") || 
        userAgent.contains("Edge") ||
        userAgent.contains("Opera GX")
    );
  }

  @PostMapping("/sign-in")
  public ResponseEntity<AuthenticatedUserResource> signIn(
          @RequestBody SignInResource signInResource,
          @RequestParam(value = "recaptchaToken", required = false) String recaptchaToken,
          HttpServletRequest request) {

    // Solo verificar captcha si es una petición web
    if (isWebRequest(request)) {
      if (recaptchaToken == null || !recaptchaService.verifyRecaptcha(recaptchaToken)) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
      }
    }

    var signInCommand = SignInCommandFromResourceAssembler
            .toCommandFromResource(signInResource);
    var authenticatedUser = userCommandService.handle(signInCommand);
    if (authenticatedUser.isEmpty()) {
      return ResponseEntity.notFound().build();
    }

    var authenticatedUserResource = AuthenticatedUserResourceFromEntityAssembler
            .toResourceFromEntity(
                    authenticatedUser.get().getLeft(), authenticatedUser.get().getRight());
    return ResponseEntity.ok(authenticatedUserResource);
  }

  @PostMapping("/sign-up")
  public ResponseEntity<UserResource> signUp(
          @RequestBody SignUpResource signUpResource,
          @RequestParam(value = "recaptchaToken", required = false) String recaptchaToken,
          HttpServletRequest request) {

    // Solo verificar captcha si es una petición web
    if (isWebRequest(request)) {
      if (recaptchaToken == null || !recaptchaService.verifyRecaptcha(recaptchaToken)) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
      }
    }

    var signUpCommand = SignUpCommandFromResourceAssembler
            .toCommandFromResource(signUpResource);
    var user = userCommandService.handle(signUpCommand);
    if (user.isEmpty()) {
      return ResponseEntity.badRequest().build();
    }
    var userResource = UserResourceFromEntityAssembler.toResourceFromEntity(user.get());
    return new ResponseEntity<>(userResource, HttpStatus.CREATED);
  }
}
