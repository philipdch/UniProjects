package com.project.mygym.representation;

import com.project.mygym.domain.Exercise;
import com.project.mygym.domain.Program;
import com.project.mygym.domain.Trainer;
import com.project.mygym.persistence.ProgramRepository;
import org.mapstruct.*;

import javax.inject.Inject;
import java.util.List;

@Mapper(componentModel = "cdi",
        injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public abstract class TrainerMapper {

    @Inject
    ProgramRepository programRepository;

    @Mapping(source = "programs", target = "programIds", qualifiedByName = "programToId")
    @Mapping(target = "password", expression = "java(new String(trainer.getPassword()))")
    @Mapping(target = "isLoggedIn", source = "loggedIn")
    public abstract TrainerRepresentation toRepresentation(Trainer trainer);

    @Mapping(target = "password", expression = "java(dto.password.getBytes())")
    @Mapping(target = "loggedIn", source = "isLoggedIn")
    public abstract Trainer toModel(TrainerRepresentation dto);

    @Named("programToId")
    public static Long programToId(Program program) {
        return program.getId();
    }

    @AfterMapping
    public void connectDependencies(TrainerRepresentation dto, @MappingTarget Trainer trainer) {
        List<Program> all = programRepository.findAll().list();
        for (Program p: all) {
            if (p.getTrainer().equals(trainer))
                p.setTrainer(trainer);
        }
    }
}
