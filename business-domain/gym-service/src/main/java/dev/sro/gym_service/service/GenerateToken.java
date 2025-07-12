package dev.sro.gym_service.service;

import org.springframework.stereotype.Service;

import dev.sro.gym_service.repository.TrainerRepository;
import dev.sro.gym_service.security.CustomUserDetails;
import dev.sro.gym_service.util.jwt.JwtUtil;
import jakarta.annotation.PostConstruct;
import java.util.List;
import dev.sro.gym_service.entity.Trainer;
import java.util.HashMap;


@Service
public class GenerateToken {
    private final JwtUtil jwtUtil;
    private final TrainerRepository trainerRepository;

    public GenerateToken(JwtUtil jwtUtil, TrainerRepository trainerRepository) {
        this.jwtUtil = jwtUtil;
        this.trainerRepository = trainerRepository;
    }

    @PostConstruct
    public void generateToken() {
        List<Trainer> trainers = trainerRepository.findAll();
        for (Trainer trainer : trainers) {
            if (trainer.getUsername().equals("client.client")) {
                CustomUserDetails userDetails = new CustomUserDetails(trainer);
                // Token que dura 1 año (365 días)
                long oneYearInMillis = 1000L * 60 * 60 * 24 * 365;
                String token = jwtUtil.generateToken(userDetails, new HashMap<>(), oneYearInMillis);
                System.out.println("=== TOKEN DE SERVICIO INTERNO (1 AÑO) ===");
                System.out.println(token);
                System.out.println("=== COPIAR ESTE TOKEN A LA CONFIGURACIÓN ===");
            }
        }
    }


}
