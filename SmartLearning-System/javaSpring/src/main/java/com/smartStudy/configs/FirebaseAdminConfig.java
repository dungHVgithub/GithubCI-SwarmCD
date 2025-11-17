// src/main/java/com/smartStudy/configs/FirebaseAdminConfig.java
package com.smartStudy.configs;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Configuration
public class FirebaseAdminConfig {
    @Bean
    public FirebaseApp firebaseApp() {
        try {
            // 1) Ưu tiên biến chuẩn của Google / hoặc biến đường dẫn file tùy chọn
            String fileFromGoogle = System.getenv("GOOGLE_APPLICATION_CREDENTIALS");
            String fileFromCustom  = System.getenv("FIREBASE_SERVICE_ACCOUNT_FILE");

            if (notBlank(fileFromGoogle)) {
                try (InputStream in = new FileInputStream(fileFromGoogle)) {
                    return initFromStream(in, "GOOGLE_APPLICATION_CREDENTIALS");
                }
            }
            if (notBlank(fileFromCustom)) {
                try (InputStream in = new FileInputStream(fileFromCustom)) {
                    return initFromStream(in, "FIREBASE_SERVICE_ACCOUNT_FILE");
                }
            }

            // 2) Fallback: BASE64 một dòng
            String b64 = System.getenv("FIREBASE_SERVICE_ACCOUNT_BASE64");
            if (notBlank(b64)) {
                byte[] decoded = Base64.getDecoder().decode(b64);
                try (InputStream in = new ByteArrayInputStream(decoded)) {
                    return initFromStream(in, "FIREBASE_SERVICE_ACCOUNT_BASE64");
                }
            }

            // 3) Fallback: JSON thô (ít khuyến nghị trên Windows)
            String json = System.getenv("FIREBASE_SERVICE_ACCOUNT_JSON");
            if (notBlank(json)) {
                byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
                try (InputStream in = new ByteArrayInputStream(bytes)) {
                    return initFromStream(in, "FIREBASE_SERVICE_ACCOUNT_JSON");
                }
            }

            throw new IllegalStateException(
                    "Missing Firebase credentials. Set GOOGLE_APPLICATION_CREDENTIALS (recommended) " +
                            "or FIREBASE_SERVICE_ACCOUNT_FILE / FIREBASE_SERVICE_ACCOUNT_BASE64 / FIREBASE_SERVICE_ACCOUNT_JSON."
            );

        } catch (Exception e) {
            throw new IllegalStateException("Init FirebaseApp failed: " + e.getMessage(), e);
        }
    }

    private FirebaseApp initFromStream(InputStream in, String source) throws IOException {
        GoogleCredentials creds = GoogleCredentials.fromStream(in);
        FirebaseOptions options = FirebaseOptions.builder().setCredentials(creds).build();
        System.out.println("[Firebase] Initialized using " + source);
        if (FirebaseApp.getApps().isEmpty()) {
            return FirebaseApp.initializeApp(options);
        }
        return FirebaseApp.getInstance();
    }

    private boolean notBlank(String s) {
        return s != null && !s.isBlank();
    }
}
