package com.hiepnt.moviebooking.config.jwt;

import com.hiepnt.moviebooking.entity.User;
import com.hiepnt.moviebooking.exception.AppException;
import com.hiepnt.moviebooking.exception.ErrorCode;
import com.hiepnt.moviebooking.repository.UserRepository;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Slf4j
@Component
public class JwtService {
    @NonFinal
    @Value("${jwt.signerKey}")
    private String SIGNER_KEY;

    @NonFinal
    @Value("${jwt.access-duration}")
    private int ACCESS_DURATION;

    @NonFinal
    @Value("${jwt.refresh-duration}")
    private int REFRESH_DURATION;

    @Autowired
    private UserRepository userRepository;


    public String generateAccessToken(User user){
        return buildToken(user,ACCESS_DURATION);
    }

    public String generateRefreshToken(User user){
        return buildToken(user,REFRESH_DURATION);
    }

    public String buildToken(User user, int duration) {
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);

        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(String.valueOf(user.getId()))
                .issuer("nthcinema.com")
                .issueTime(new Date())
                .expirationTime(new Date(
                        Instant.now().plus(duration, ChronoUnit.MINUTES).toEpochMilli()
                ))
                .claim("email", user.getEmail())
                .claim("scope", user.getRole().name())
                .build();

        Payload payload = new Payload(jwtClaimsSet.toJSONObject());

        JWSObject jwsObject = new JWSObject(header, payload);

        try {
            jwsObject.sign(new MACSigner(SIGNER_KEY.getBytes()));
            return jwsObject.serialize();
        } catch (JOSEException e) {
            log.error("Cannot create token", e);
            throw new RuntimeException(e);
        }
    }


    public boolean verifyToken(String token) {
        try {

            JWSVerifier verifier = new MACVerifier(SIGNER_KEY.getBytes());

            SignedJWT signedJWT = SignedJWT.parse(token);

            Date expiryTime = signedJWT.getJWTClaimsSet().getExpirationTime();
            int userId = Integer.parseInt(signedJWT.getJWTClaimsSet().getSubject());

            User user = userRepository.findById(userId).orElseThrow(
                    () -> new AppException(ErrorCode.NOT_EXISTED));

            return signedJWT.verify(verifier) && expiryTime.after(new Date());

        } catch (ParseException | JOSEException | AppException e) {
            return false;
        }
    }
}