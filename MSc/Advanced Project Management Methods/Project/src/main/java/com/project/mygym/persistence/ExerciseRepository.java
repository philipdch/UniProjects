package com.project.mygym.persistence;

import com.project.mygym.domain.Exercise;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;

import javax.enterprise.context.RequestScoped;
import javax.persistence.EntityManager;
import javax.persistence.Query;

@RequestScoped
public class ExerciseRepository implements PanacheRepositoryBase<Exercise, Long> {
}
