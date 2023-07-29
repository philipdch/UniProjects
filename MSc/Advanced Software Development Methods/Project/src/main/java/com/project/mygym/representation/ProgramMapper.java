package com.project.mygym.representation;

import com.project.mygym.domain.Exercise;
import com.project.mygym.domain.Program;
import com.project.mygym.domain.Trainer;
import com.project.mygym.persistence.ExerciseRepository;
import com.project.mygym.persistence.TrainerRepository;
import com.project.mygym.values.Difficulty;
import com.project.mygym.values.PhysicalCondition;
import org.mapstruct.*;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "cdi",
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        uses = {TrainerMapper.class})
public abstract class ProgramMapper {

    @Inject
    TrainerRepository trainerRepository;
    @Inject
    ExerciseRepository exerciseRepository;

    @Mapping(source = "trainer", target = "trainerId" ,qualifiedByName = "trainerToId")
    @Mapping(source = "exercises", target = "exerciseIds", qualifiedByName = "exerciseToId")
    public abstract ProgramRepresentation toRepresentation(Program program);

    @Mapping(source = "difficulty", target = "difficulty", qualifiedByName = "difficultyToString")
    @Mapping(source = "aimedAt", target = "aimedAt", qualifiedByName = "conditionToString")
    @Mapping(target = "trainer", ignore = true)
    public abstract Program toModel(ProgramRepresentation dto);

    public abstract List<ProgramRepresentation> toRepresentationList(List<Program> programs);

    @AfterMapping
    public void connectDependencies(ProgramRepresentation dto, @MappingTarget Program program){
        Trainer trainer = trainerRepository.findById(dto.trainerId);
        program.setTrainer(trainer);
        List<Exercise> all = exerciseRepository.findAll().list();
        for (Exercise e: all) {
            if (e.getProgram().equals(program))
                program.addExercise(e);
        }
    }

    @Named("conditionToString")
    PhysicalCondition toPhysicalCondition(String value){
        return PhysicalCondition.valueOfLabel(value);
    }

    @Named("difficultyToString")
    Difficulty toDifficulty(String value){
        return Difficulty.valueOfLabel(value);
    }

    @Named("exerciseToId")
    public static Long exerciseToId(Exercise exercise) {
        return exercise.getId();
    }

    @Named("trainerToId")
    public static Long trainerToId(Trainer trainer){
        return trainer.getId();
    }
}
