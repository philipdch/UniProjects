package com.project.mygym.resource;

import com.project.mygym.domain.Exercise;
import com.project.mygym.domain.Program;
import com.project.mygym.persistence.ExerciseRepository;
import com.project.mygym.persistence.ProgramRepository;
import com.project.mygym.representation.ExerciseMapper;
import com.project.mygym.representation.ProgramMapper;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import static com.project.mygym.resource.GymURI.EXERCISES;

@Path(EXERCISES)
@RequestScoped
public class ExerciseResource {

    @Context
    UriInfo uriInfo;

    @Inject
    ExerciseRepository exerciseRepository;

    @Inject
    ExerciseMapper exerciseMapper;

    @GET
    @Path("{exerciseId:[0-9]*}")
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public Response find(@PathParam("exerciseId") Long exerciseId) {

        Exercise exercise = exerciseRepository.findById(exerciseId);
        if (exercise == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        return Response.ok().entity(exerciseMapper.toRepresentation(exercise)).type(MediaType.APPLICATION_JSON_TYPE).build();
    }

}
