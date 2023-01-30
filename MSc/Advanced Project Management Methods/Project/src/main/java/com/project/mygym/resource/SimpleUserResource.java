package com.project.mygym.resource;

import com.project.mygym.domain.Exercise;
import com.project.mygym.domain.ExerciseProgress;
import com.project.mygym.domain.Program;
import com.project.mygym.domain.SimpleUser;
import com.project.mygym.persistence.ExerciseRepository;
import com.project.mygym.persistence.ProgramRepository;
import com.project.mygym.persistence.SimpleUserRepository;
import com.project.mygym.representation.*;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import java.net.URI;
import java.util.List;

import static com.project.mygym.resource.GymURI.SIMPLE_USERS;

@Path(SIMPLE_USERS)
@RequestScoped
public class SimpleUserResource {

    @Inject
    ProgramRepository programRepository;

    @Inject
    SimpleUserRepository simpleUserRepository;

    @Inject
    ExerciseRepository exerciseRepository;

    @Inject
    ProgramMapper programMapper;

    @Inject
    ExerciseProgressMapper exerciseProgressMapper;

    @GET
    @Path("/{userId:[0-9]*}/programs")
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public Response getTailoredPrograms(@PathParam("userId") Long userId) {
        List<Program> programs;
        SimpleUser user = simpleUserRepository.findById(userId);
        if (user == null) return Response.status(Response.Status.NOT_FOUND).build();

        user.calculateAge();
        programs = programRepository.findTailoredPrograms(user);
        return Response.ok().entity(programMapper.toRepresentationList(programs)).build();
    }

    @POST
    @Path("{simpleUserId:[0-9]*}/subscribe/{programId:[0-9]*}")
    @Transactional
    public Response subscribe(@PathParam("programId") Long programId, @PathParam("simpleUserId") Long simpleUserId) {
        //TODO: Integrity checks. Could be implemented with JWT authentication
        System.out.println("IN SUBSCRIBE");
        Program program = programRepository.findById(programId);
        if (program == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        SimpleUser simpleUser = simpleUserRepository.findById(simpleUserId);
        if (simpleUser == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        simpleUser.subscribe(program);

        return Response.noContent().build();
    }

    @POST
    @Path("{simpleUserId:[0-9]*}/unsubscribe/{programId:[0-9]*}")
    @Transactional
    public Response unsubscribe(@PathParam("programId") Long programId, @PathParam("simpleUserId") Long simpleUserId) {
        //TODO: Integrity checks. Could be implemented with JWT authentication

        Program program = programRepository.findById(programId);
        if (program == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        SimpleUser simpleUser = simpleUserRepository.findById(simpleUserId);
        if (simpleUser == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        simpleUser.unsubscribe(program);

        return Response.noContent().build();
    }

    @POST
    @Path("{simpleUserId:[0-9]*}/exercise/{exerciseId:[0-9]*}")
    @Transactional
    public Response startExercise(@PathParam("exerciseId") Long exerciseId, @PathParam("simpleUserId") Long simpleUserId) {
        //TODO: Integrity checks. Could be implemented with JWT authentication
        SimpleUser simpleUser = simpleUserRepository.findById(simpleUserId);
        if (simpleUser == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        Exercise exercise = exerciseRepository.findById(exerciseId);
        if (exercise == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        simpleUser.startExercise(exercise);

        return Response.noContent().build();
    }

    @PUT
    @Path("/exercise")
    @Transactional
    public Response updateExercise(ExerciseProgressRepresentation exerciseProgressRepresentation) {
        //TODO: Integrity checks. Could be implemented with JWT authentication

        ExerciseProgress exerciseProgress = exerciseProgressMapper.toModel(exerciseProgressRepresentation);

        Exercise exercise = exerciseRepository.findById(exerciseProgress.getId().getExerciseId());
        if (exercise == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        SimpleUser simpleUser = simpleUserRepository.findById(exerciseProgress.getId().getUserId());
        if (simpleUser == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        simpleUser.updateExercise(exercise, exerciseProgress.getRepetitionsLeft(), exerciseProgress.getWeightOnCompletion());

        return Response.ok().build();
    }
}
