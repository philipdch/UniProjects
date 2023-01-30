package com.project.mygym.resource;

import com.project.mygym.domain.SimpleUser;
import com.project.mygym.domain.Trainer;
import com.project.mygym.persistence.SimpleUserRepository;
import com.project.mygym.persistence.TrainerRepository;
import com.project.mygym.representation.SimpleUserMapper;
import com.project.mygym.representation.SimpleUserRepresentation;
import com.project.mygym.representation.TrainerMapper;
import com.project.mygym.representation.TrainerRepresentation;
import com.project.mygym.security.Authentication;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static com.project.mygym.resource.GymURI.AUTHENTICATION;

@RequestScoped
@Path(AUTHENTICATION)
public class AuthenticationResource {

    @Inject
    SimpleUserRepository simpleUserRepository;

    @Inject
    TrainerRepository trainerRepository;

    @Inject
    SimpleUserMapper simpleUserMapper;

    @Inject
    TrainerMapper trainerMapper;

    @Inject
    Authentication auth;

    @POST
    @Path("/users/register")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public Response register(SimpleUserRepresentation userDto) {
        List<SimpleUser> results = simpleUserRepository.list("email", userDto.email);
        if (!results.isEmpty()) {
            return Response.status(Response.Status.CONFLICT).build();
        }

        SimpleUser simpleUser = simpleUserMapper.toModel(userDto);
        boolean success = auth.register(simpleUser);
        if (success) {
            simpleUser = simpleUserRepository.list("email", simpleUser.getEmail()).get(0);
            SimpleUserRepresentation simpleUserRepresentation = simpleUserMapper.toRepresentation(simpleUser);
            URI uri = UriBuilder.fromResource(SimpleUserResource.class).path(String.valueOf(simpleUser.getId())).build();
            return Response.created(uri).entity(simpleUserRepresentation).build();
        } else {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }

    @POST
    @Path("/users/login")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public Response login(SimpleUserRepresentation userDto) {
        SimpleUser simpleUser;
        boolean success = auth.login(userDto.email, userDto.password);
        if (success) {
            simpleUser = simpleUserRepository.list("email", userDto.email.toLowerCase()).get(0);
            SimpleUserRepresentation simpleUserRepresentation = simpleUserMapper.toRepresentation(simpleUser);
            return Response.ok().entity(simpleUserRepresentation).build();
        } else {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }

    @POST
    @Path("/trainers/register")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public Response register(TrainerRepresentation trainerDto) {
        List<Trainer> results = trainerRepository.list("email", trainerDto.email);
        if (!results.isEmpty()) {
            return Response.status(Response.Status.CONFLICT).build();
        }

        Trainer trainer = trainerMapper.toModel(trainerDto);
        boolean success = auth.register(trainer);
        if (success) {
            trainer = trainerRepository.list("email", trainer.getEmail()).get(0);
            TrainerRepresentation trainerRepresentation = trainerMapper.toRepresentation(trainer);
            URI uri = UriBuilder.fromResource(TrainerResource.class).path(String.valueOf(trainer.getId())).build();
            return Response.created(uri).entity(trainerRepresentation).build();
        } else {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }

    @POST
    @Path("/trainers/login")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public Response login(TrainerRepresentation trainerDto) {
        Trainer trainer;
        boolean success = auth.login(trainerDto.email, trainerDto.password);
        if (success) {
            trainer = trainerRepository.list("email", trainerDto.email.toLowerCase()).get(0);
            TrainerRepresentation trainerRepresentation = trainerMapper.toRepresentation(trainer);
            return Response.ok().entity(trainerRepresentation).build();
        } else {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }


}
