package com.project.mygym.resource;

import com.project.mygym.domain.ExerciseProgress;
import com.project.mygym.domain.Program;
import com.project.mygym.domain.SimpleUser;
import com.project.mygym.domain.Trainer;
import com.project.mygym.persistence.ProgramRepository;
import com.project.mygym.persistence.SimpleUserRepository;
import com.project.mygym.persistence.TrainerRepository;
import com.project.mygym.representation.ExerciseProgressMapper;
import com.project.mygym.representation.ExerciseProgressRepresentation;
import com.project.mygym.representation.ProgramMapper;
import com.project.mygym.representation.ProgramRepresentation;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.print.attribute.standard.Media;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.*;

import java.math.BigDecimal;
import java.net.URI;
import java.time.DateTimeException;
import java.time.Month;
import java.util.List;
import java.util.Set;

import static com.project.mygym.resource.GymURI.TRAINERS;

@Path(TRAINERS)
@RequestScoped
public class TrainerResource {

    @Context
    UriInfo uriInfo;

    @Inject
    ProgramMapper programMapper;

    @Inject
    ExerciseProgressMapper exerciseProgressMapper;

    @Inject
    TrainerRepository trainerRepository;

    @Inject
    ProgramRepository programRepository;

    @Inject
    SimpleUserRepository simpleUserRepository;

    @POST
    @Path("/{trainerId:[0-9]*}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces({MediaType.APPLICATION_JSON})
    @Transactional
    public Response createProgram(@PathParam("trainerId") Long trainerId, ProgramRepresentation programRepresentation){
        Trainer trainer = trainerRepository.findById(trainerId);
        if(trainer == null){
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        Program program = programMapper.toModel(programRepresentation);
        trainer.createProgram(program);
        programRepository.persist(program);

        URI uri = UriBuilder.fromResource(TrainerResource.class).path(String.valueOf(program.getId())).build();
        return Response.created(uri).entity(programMapper.toRepresentation(program)).build();
    }

    @GET
    @Path("/{trainerId:[0-9]*}/income")
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public Response calculateIncome(@PathParam("trainerId") Long trainerId, @QueryParam("month") int month){
        Trainer trainer = trainerRepository.findById(trainerId);
        if(trainer == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        BigDecimal income = new BigDecimal(0);
        if(month == 0){
            for(Month m: Month.values()){
                income = income.add(trainer.calculateIncome(m));
            }
        }else{
            try{
                Month m = Month.of(month);
                income = trainer.calculateIncome(m);
            }catch (DateTimeException e){
                e.printStackTrace();
            }
        }
        return Response.ok().entity(income.toString()).build();
    }

    @GET
    @Path("/{trainerId:[0-9]*}/customerProgress/{userId:[0-9]*}")
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public Response getProgress(@PathParam("trainerId") Long trainerId, @PathParam("userId") Long userId){
        Trainer trainer = trainerRepository.findById(trainerId);
        if(trainer == null)
            return Response.status(Response.Status.NOT_FOUND).build();

        SimpleUser customer = simpleUserRepository.findById(userId);
        if(customer == null)
            return Response.status(Response.Status.NOT_FOUND).build();

        Set<ExerciseProgress> progress = trainer.getCustomerProgress(customer);
        List<ExerciseProgressRepresentation> results = exerciseProgressMapper.toRepresentationList(List.copyOf(progress));
        return Response.ok().entity(results).build();
    }
}
