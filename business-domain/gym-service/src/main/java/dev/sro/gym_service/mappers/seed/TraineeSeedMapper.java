package dev.sro.gym_service.mappers.seed;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import dev.sro.gym_service.dtos.v1.request.seed.TraineeSeedRequest;
import dev.sro.gym_service.entity.Trainee;

@Mapper(componentModel = "spring")
public interface TraineeSeedMapper {
    
    @Mapping(target = "trainings", ignore = true)
    @Mapping(target = "trainers", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(source = "role", target = "role")
    Trainee toEntity(TraineeSeedRequest traineeSeedRequest);
    
}
