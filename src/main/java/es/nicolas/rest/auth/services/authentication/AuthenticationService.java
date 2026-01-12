package es.nicolas.rest.auth.services.authentication;

import es.nicolas.rest.auth.dto.JwtAuthResponse;
import es.nicolas.rest.auth.dto.UserSignInRequest;
import es.nicolas.rest.auth.dto.UserSignUpRequest;

public interface AuthenticationService {
    JwtAuthResponse signUp(UserSignUpRequest request);

    JwtAuthResponse signIn(UserSignInRequest request);
}
