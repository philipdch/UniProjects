package com.project.mygym.representation;

import com.project.mygym.domain.SimpleUser;
import com.project.mygym.values.Category;
import com.project.mygym.values.Muscles;
import com.project.mygym.values.PhysicalCondition;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "cdi",
        injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public abstract class SimpleUserMapper {

    @Mapping(source = "physicalCondition", target = "physicalCondition", qualifiedByName = "physicalConditionToString")
    @Mapping(target = "password", expression = "java(new String(simpleUser.getPassword()))")
    @Mapping(target = "isLoggedIn", source = "loggedIn")
    public abstract SimpleUserRepresentation toRepresentation(SimpleUser simpleUser);

    @Mapping(source = "physicalCondition", target = "physicalCondition", qualifiedByName = "stringToPhysicalCondition")
    @Mapping(target = "password", expression = "java(dto.password.getBytes())")
    @Mapping(target = "loggedIn", source = "isLoggedIn")
    public abstract SimpleUser toModel(SimpleUserRepresentation dto);

    @Named("physicalConditionToString")
    String fromPhysicalCondition(PhysicalCondition physicalCondition){
        return physicalCondition.label;
    }
    @Named("stringToPhysicalCondition")
    PhysicalCondition toPhysicalCondition(String physicalCondition) { return PhysicalCondition.valueOfLabel(physicalCondition); }
}
