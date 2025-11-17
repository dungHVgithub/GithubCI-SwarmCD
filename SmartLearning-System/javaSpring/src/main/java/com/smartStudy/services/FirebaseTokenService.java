// src/main/java/com/yourapp/firebase/FirebaseTokenService.java
package com.smartStudy.services;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class FirebaseTokenService {
    public String createCustomToken(String uid, Map<String, Object> claims) throws FirebaseAuthException {
        return (claims == null || claims.isEmpty())
                ? FirebaseAuth.getInstance().createCustomToken(uid)
                : FirebaseAuth.getInstance().createCustomToken(uid, claims);
    }
}

