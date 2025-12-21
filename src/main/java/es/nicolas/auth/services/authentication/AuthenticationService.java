package es.nicolas.auth.services.authentication;

import es.nicolas.auth.dto.JwtAuthResponse;
import es.nicolas.auth.dto.UserSignInRequest;
import es.nicolas.auth.dto.UserSignUpRequest;

public interface AuthenticationService {
    JwtAuthResponse signUp(UserSignUpRequest request);

    JwtAuthResponse signIn(UserSignInRequest request);
}
