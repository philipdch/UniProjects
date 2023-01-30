package com.project.mygym.representation;

import com.project.mygym.domain.*;
import com.project.mygym.persistence.ExerciseRepository;
import com.project.mygym.persistence.ProgramRepository;
import com.project.mygym.persistence.SimpleUserRepository;
import com.project.mygym.values.Category;
import com.project.mygym.values.Difficulty;
import com.project.mygym.values.Muscles;
import net.bytebuddy.build.Plugin;
import org.mapstruct.*;

import javax.inject.Inject;
import java.util.List;
import java.util.Set;

@Mapper(componentModel = "cdi",
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        uses = {ExerciseMapper.class})
public abstract class ExerciseProgressMapper {

    @Inject
    SimpleUserRepository simpleUserRepository;

    @Inject
    ExerciseRepository exerciseRepository;

    @Mapping(source = "id.userId", target = "userId")
    @Mapping(source = "id.exerciseId", target = "exerciseId")
    @Mapping(source = "id.createdOn", target = "createdOn")
    public abstract ExerciseProgressRepresentation toRepresentation(ExerciseProgress exerciseProgress);

    public abstract List<ExerciseProgressRepresentation> toRepresentationList(List<ExerciseProgress> progress);

    @Mapping(source = "userId", target = "id.userId")
    @Mapping(source = "exerciseId", target = "id.exerciseId")
    @Mapping(source = "createdOn", target = "id.createdOn")
    public abstract ExerciseProgress toModel(ExerciseProgressRepresentation dto);

    @AfterMapping
    public void connectDependencies(ExerciseProgressRepresentation dto, @MappingTarget ExerciseProgress exerciseProgress){
        Exercise exercise = exerciseRepository.findById(dto.exerciseId);
        exerciseProgress.setExercise(exercise);

        SimpleUser user = simpleUserRepository.findById(dto.userId);
        exerciseProgress.setSimpleUser(user);
    }

}
