/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smartStudy.untils;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import java.util.Date;

/**
 *
 * @author AN515-57
 */
public class JwtUtils {

    private static final String SECRET = "12345678901234567890123456789012"; // 32 ký tự (256-bit key)
    private static final long EXPIRATION_MS = 86400000; // 1 ngày

    public static String generateToken(String email) throws Exception {
        JWSSigner signer = new MACSigner(SECRET);

        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .subject(email)
                .issueTime(new Date())
                .expirationTime(new Date(System.currentTimeMillis() + EXPIRATION_MS))
                .build();

        SignedJWT signedJWT = new SignedJWT(
                new JWSHeader(JWSAlgorithm.HS256),
                claimsSet
        );
        signedJWT.sign(signer);

        return signedJWT.serialize();
    }

    public static String validateTokenAndGetUsername(String token) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);
            JWSVerifier verifier = new MACVerifier(SECRET);

            if (signedJWT.verify(verifier)) {
                Date exp = signedJWT.getJWTClaimsSet().getExpirationTime();
                if (exp != null && exp.after(new Date())) {
                    return signedJWT.getJWTClaimsSet().getSubject();
                }
            }
        } catch (Exception e) {
            // log ở mức DEBUG nếu cần, không throw
        }
        return null;
    }

}