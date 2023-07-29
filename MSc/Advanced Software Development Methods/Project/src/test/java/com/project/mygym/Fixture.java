package com.project.mygym;

import com.project.mygym.representation.*;
import com.project.mygym.utils.SecurityImpl;
import com.project.mygym.values.*;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Fixture {
    public static String API_ROOT = "http://localhost";

    public static ExerciseRepresentation getExerciseRepresentation1(){
        List<String> musclesTrained = new ArrayList<>();
        musclesTrained.add(Muscles.UPPER_BACK.label);
        musclesTrained.add(Muscles.BICEPS.label);
        ExerciseRepresentation exerciseRepresentation = new ExerciseRepresentation();
        exerciseRepresentation.id = 4000L;
        exerciseRepresentation.difficulty = Difficulty.ADVANCED.label;
        exerciseRepresentation.name = "Back rows";
        exerciseRepresentation.category = Category.STRENGTH.label;
        exerciseRepresentation.description = "Advanced back rows for effective muscle growth";
        exerciseRepresentation.programId = 3000L;
        exerciseRepresentation.repetitions = 15;
        exerciseRepresentation.timeRequired = 25;
        exerciseRepresentation.musclesTrained = musclesTrained;
        return exerciseRepresentation;
    }

    public static ExerciseRepresentation getExerciseRepresentation2(){
        List<String> musclesTrained = new ArrayList<>();
        musclesTrained.add(Muscles.CALVES.label);
        musclesTrained.add(Muscles.THIGHS.label);
        ExerciseRepresentation exerciseRepresentation = new ExerciseRepresentation();
        exerciseRepresentation.id = 2001L;
        exerciseRepresentation.difficulty = Difficulty.BEGINNER.label;
        exerciseRepresentation.name = "Leg raises";
        exerciseRepresentation.category = Category.ENDURANCE.label;
        exerciseRepresentation.description = "Simple leg training";
        exerciseRepresentation.repetitions = 20;
        exerciseRepresentation.timeRequired = 10;
        exerciseRepresentation.musclesTrained = musclesTrained;
        return exerciseRepresentation;
    }

    public static ProgramRepresentation getProgramRepresentation(){
        List<String> aimedAt = new ArrayList<>();
        aimedAt.add(PhysicalCondition.NORMAL.label);
        ProgramRepresentation programRepresentation = new ProgramRepresentation();
        programRepresentation.id = 3000L;
        programRepresentation.name = "First Program";
        programRepresentation.goals = "Strengthens core and arms";
        programRepresentation.frequency = 5;
        programRepresentation.difficulty = Difficulty.ADVANCED.label;
        programRepresentation.aimedAt = aimedAt;
        programRepresentation.ageMin = 15;
        programRepresentation.ageMax = 90;
        programRepresentation.cost = 5.0f;
        programRepresentation.trainerId = 2000L;
        programRepresentation.exerciseIds.add(getExerciseRepresentation1().id);
        return programRepresentation;
    }

    public static ProgramRepresentation getNewProgramRepresentation(){
        List<String> aimedAt = new ArrayList<>();
        aimedAt.add(PhysicalCondition.NORMAL.label);
        aimedAt.add(PhysicalCondition.OVERWEIGHT.label);
        aimedAt.add(PhysicalCondition.FIT.label);
        ProgramRepresentation programRepresentation = new ProgramRepresentation();
        programRepresentation.name = "Third Program";
        programRepresentation.goals = "Effective cardio to increase lung capacity and stamina";
        programRepresentation.frequency = 3;
        programRepresentation.difficulty = Difficulty.INTERMEDIATE.label;
        programRepresentation.aimedAt = aimedAt;
        programRepresentation.ageMin = 16;
        programRepresentation.ageMax = 65;
        programRepresentation.cost = 15.0f;
        programRepresentation.trainerId = 2000L;
        programRepresentation.exerciseIds.add(getExerciseRepresentation2().id);
        return programRepresentation;
    }

    public static TrainerRepresentation getTrainerRepresentation(){
        TrainerRepresentation trainerRepresentation = new TrainerRepresentation();
        trainerRepresentation.username = "'philip_15'";
        trainerRepresentation.phoneNumber = "6969696969";
        trainerRepresentation.email = "'philip@gmail.com'";
        trainerRepresentation.password = "Asd98^dj2";
        trainerRepresentation.firstName = "Guido";
        trainerRepresentation.lastname = "Mista";
        trainerRepresentation.profileDescription = "'I am a trainer";
        trainerRepresentation.id = 2000L;
        trainerRepresentation.programIds.add(getProgramRepresentation().id);
        return trainerRepresentation;
    }

    public static TrainerRepresentation getNewTrainerRepresentation(){
        TrainerRepresentation trainerRepresentation = new TrainerRepresentation();
        trainerRepresentation.username = "Polnareff44";
        trainerRepresentation.phoneNumber = "6972487343";
        trainerRepresentation.email = "jeanp3@luckyland.com";
        trainerRepresentation.password = "12345%098765";
        trainerRepresentation.firstName = "Jean-Pierre";
        trainerRepresentation.lastname = "Polnareff";
        trainerRepresentation.profileDescription = "I'm an experienced personal trainer, always ready to take up new challenges and train aspiring athletes";
        return trainerRepresentation;
    }

    public static SimpleUserRepresentation getSimpleUserRepresentation1() {
        SimpleUserRepresentation simpleUserRepresentation = new SimpleUserRepresentation();
        simpleUserRepresentation.id = 1000L;
        simpleUserRepresentation.username = "arrakis";
        simpleUserRepresentation.phoneNumber = "6985648293";
        simpleUserRepresentation.email = "araki@gmail.com";
        simpleUserRepresentation.gender = Gender.M;
        simpleUserRepresentation.height = 172;
        simpleUserRepresentation.weight = 62.5;
        simpleUserRepresentation.dateOfBirth = "1999-05-14";
        simpleUserRepresentation.physicalCondition = PhysicalCondition.NORMAL.label;
        return simpleUserRepresentation;
    }

    public static SimpleUserRepresentation getSimpleUserRepresentation2() {
        SimpleUserRepresentation simpleUserRepresentation = new SimpleUserRepresentation();
        simpleUserRepresentation.username = "Bonnie32";
        simpleUserRepresentation.phoneNumber = "6987435743";
        simpleUserRepresentation.email = "BnM1209@gmail.com";
        simpleUserRepresentation.gender = Gender.F;
        simpleUserRepresentation.height = 180;
        simpleUserRepresentation.weight = 70.2;
        simpleUserRepresentation.password = "12345%098765";
        simpleUserRepresentation.dateOfBirth = "1990-04-13";
        simpleUserRepresentation.physicalCondition = PhysicalCondition.NORMAL.label;
        return simpleUserRepresentation;
    }

    public static ExerciseProgressRepresentation getExerciseProgressRepresentation() {
        ExerciseProgressRepresentation exerciseProgressRepresentation = new ExerciseProgressRepresentation();
        exerciseProgressRepresentation.exerciseId = 4000L;
        exerciseProgressRepresentation.userId = 1000L;
        exerciseProgressRepresentation.createdOn = "2023-01-15T15:45:06.69";
        exerciseProgressRepresentation.repetitionsLeft = 10;
        exerciseProgressRepresentation.weightOnCompletion = 70D;
        return exerciseProgressRepresentation;
    }
}
