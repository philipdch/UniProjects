package com.project.mygym.representation;

import com.project.mygym.domain.Exercise;
import com.project.mygym.domain.Program;
import com.project.mygym.domain.Trainer;
import com.project.mygym.persistence.ExerciseRepository;
import com.project.mygym.persistence.ProgramRepository;
import com.project.mygym.values.Category;
import com.project.mygym.values.Difficulty;
import com.project.mygym.values.Muscles;
import org.mapstruct.*;

import javax.inject.Inject;
import java.util.List;

@Mapper(componentModel = "cdi",
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        uses = {ProgramMapper.class})
public abstract class ExerciseMapper {

    @Inject
    ProgramRepository programRepository;

    @Mapping(source = "program", target = "programId", qualifiedByName = "programToId")
    @Mapping(source = "category", target = "category", qualifiedByName = "categoryToString")
    @Mapping(source = "musclesTrained", target = "musclesTrained", qualifiedByName = "musclesToString")
    @Mapping(source = "difficulty", target = "difficulty", qualifiedByName = "difficultyToString")
    public abstract ExerciseRepresentation toRepresentation(Exercise exercise);

    @Mapping(source = "category", target = "category", qualifiedByName = "stringToCategory")
    @Mapping(source = "musclesTrained", target = "musclesTrained", qualifiedByName = "stringToMuscles")
    @Mapping(source = "difficulty", target = "difficulty", qualifiedByName = "stringToDifficulty")
    public abstract Exercise toModel(ExerciseRepresentation dto);

    public abstract List<ExerciseRepresentation> toRepresentationList(List<Exercise> exercises);

    @AfterMapping
    public void connectDependencies(ExerciseRepresentation dto, @MappingTarget Exercise exercise) {
        Program program = programRepository.findById(dto.programId);
        exercise.setProgram(program);
    }

    @Named("programToId")
    public static Long programToId(Program program){
        return program.getId();
    }

    @Named("categoryToString")
    String fromCategory(Category category){
        return category.label;
    }
    @Named("stringToCategory")
    Category toCategory(String value){
        return Category.valueOfLabel(value);
    }

    @Named("musclesToString")
    String fromMuscles(Muscles muscles){
        return muscles.label;
    }
    @Named("stringToMuscles")
    Muscles toMuscles(String value){
        return Muscles.valueOfLabel(value);
    }

    @Named("difficultyToString")
    String fromDifficulty(Difficulty difficulty){
        return difficulty.label;
    }
    @Named("stringToDifficulty")
    Difficulty toDifficulty(String value){
        return Difficulty.valueOfLabel(value);
    }
}
