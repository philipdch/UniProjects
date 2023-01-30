package com.project.mygym.resource;

import com.project.mygym.domain.Exercise;
import com.project.mygym.domain.Program;
import com.project.mygym.domain.SimpleUser;
import com.project.mygym.persistence.*;
import com.project.mygym.representation.ExerciseMapper;
import com.project.mygym.representation.ExerciseRepresentation;
import com.project.mygym.representation.ProgramMapper;
import com.project.mygym.representation.ProgramRepresentation;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.*;

import java.net.URI;
import java.util.List;

import static com.project.mygym.resource.GymURI.PROGRAMS;

@Path(PROGRAMS)
@RequestScoped
public class ProgramResource {

    @Context
    UriInfo uriInfo;

    @Inject
    ProgramRepository programRepository;

    @Inject
    SimpleUserRepository simpleUserRepository;

    @Inject
    TrainerRepository trainerRepository;

    @Inject
    ExerciseRepository exerciseRepository;

    @Inject
    ProgramMapper programMapper;

    @Inject
    ExerciseMapper exerciseMapper;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public Response getAllPrograms(@DefaultValue("") @QueryParam("name") String name, @DefaultValue("0") @QueryParam("age") int age, @QueryParam("goals") String goals) {
        List<Program> programs = programRepository.findAll().list();
        if (name != null && !name.isEmpty()) {
            programs.retainAll(programRepository.findProgramsByName(name));
        }
        if (age > 0){
            programs.retainAll(programRepository.findProgramsByAge(age));
        }
        if(goals != null && !goals.isEmpty()){
//            programs.retainAll(programRepository.findProgramByGoals(goals));
        }
        return Response.ok().entity(programMapper.toRepresentationList(programs)).build();
    }

    @GET
    @Path("/{programId:[0-9]*}")
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public Response find(@PathParam("programId") Long programId) {

        Program program = programRepository.findById(programId);
        if (program == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        return Response.ok().entity(programMapper.toRepresentation(program)).build();
    }

    @POST
    @Path("/{programId:[0-9]*}/exercises")
    @Transactional
    public Response addExercise(ExerciseRepresentation exerciseRepresentation, @PathParam("programId") Long programId) {
        Program program = programRepository.findById(programId);
        if (program == null)
            return Response.status(Response.Status.NOT_FOUND).build();

        Exercise exercise = exerciseMapper.toModel(exerciseRepresentation);
        program.addExercise(exercise);
        exerciseRepository.persist(exercise);
        programRepository.persist(program);
        URI uri = UriBuilder.fromResource(ProgramResource.class).path(String.valueOf(programId)).build();
        return Response.created(uri).entity(exerciseMapper.toRepresentation(exercise)).build();
    }
}
