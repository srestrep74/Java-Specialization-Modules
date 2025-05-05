package com.sro.SpringCoreTask1.mappers.seed;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.sro.SpringCoreTask1.dtos.v1.request.seed.TraineeSeedRequest;
import com.sro.SpringCoreTask1.entity.Trainee;

@Mapper(componentModel = "spring")
public interface TraineeSeedMapper {
    
    @Mapping(target = "trainings", ignore = true)
    @Mapping(target = "trainers", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(source = "role", target = "role")
    Trainee toEntity(TraineeSeedRequest traineeSeedRequest);
    
}
