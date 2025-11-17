// com.smartStudy.configs.EmailConfig.java
package com.smartStudy.configs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.core.env.Environment;
import java.util.Properties;

@Configuration
public class EmailConfig {
    @Autowired
    private Environment env;

    @Bean
    public JavaMailSender javaMailSender(
            @Value("${spring.mail.host:smtp.gmail.com}") String host,
            @Value("${spring.mail.port:587}") Integer port,
            @Value("${spring.mail.username:#{null}}") String user,
            @Value("${spring.mail.password:#{null}}") String pass,
            @Value("${spring.mail.properties.mail.smtp.auth:true}") boolean auth,
            @Value("${spring.mail.properties.mail.smtp.starttls.enable:true}") boolean starttls
    ) {
        System.out.println("All Environment Properties: " + env.getProperty("spring.mail.username") + ", " + env.getProperty("spring.mail.password"));
        System.out.println("Mail Config - Host: " + host);
        System.out.println("Mail Config - Port: " + port);
        System.out.println("Mail Config - Username: " + (user != null ? user : "NULL"));
        System.out.println("Mail Config - Password: " + (pass != null ? "SET" : "NULL"));
        System.out.println("Mail Config - SMTP Auth: " + auth);
        System.out.println("Mail Config - StartTLS: " + starttls);

        if (user == null || pass == null) {
            throw new IllegalStateException("Mail username hoặc password không được cung cấp. Vui lòng kiểm tra file .env hoặc cấu hình.");
        }

        var s = new JavaMailSenderImpl();
        s.setHost(host);
        s.setPort(port != null ? port : 587);
        s.setUsername(user);
        s.setPassword(pass);
        var p = s.getJavaMailProperties();
        p.put("mail.smtp.auth", String.valueOf(auth));
        p.put("mail.smtp.starttls.enable", String.valueOf(starttls));
        p.put("mail.smtp.ssl.trust", "smtp.gmail.com");
        p.put("mail.smtp.connectiontimeout", "10000");
        p.put("mail.smtp.timeout", "10000");
        p.put("mail.smtp.writetimeout", "10000");
        return s;
    }

}
