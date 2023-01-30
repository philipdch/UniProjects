package com.project.mygym.persistence;

import com.project.mygym.domain.Program;
import com.project.mygym.domain.SimpleUser;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import io.quarkus.panache.common.Parameters;

import javax.enterprise.context.RequestScoped;
import java.util.ArrayList;
import java.util.List;

@RequestScoped
public class ProgramRepository implements PanacheRepositoryBase<Program, Long> {

//    @Inject
//    SearchSession searchSession;

    public List<Program> findTailoredPrograms(SimpleUser user){
        List<Program> allPrograms = find("select program from Program program").list();
        List<Program> tailoredPrograms = new ArrayList<>();
        for(Program program: allPrograms){
            if(program.canSubscribe(user)){
                tailoredPrograms.add(program);
            }
        }
        return tailoredPrograms;
    }

    public List<Program> findProgramsByName(String name) {
        if(name == null)
            return listAll();
        return find("select program from Program program where lower(program.name) like :programName",
                Parameters.with("programName", name.toLowerCase() + "%").map())
                .list();
    }

//    public List<Program> findProgramByGoals(String keyword) {
//        return searchSession.search(Program.class)
//                .where(f -> keyword == null || keyword.trim().isEmpty() ? f.matchAll() :
//                        f.simpleQueryString().field("goals").matching(keyword))
//                .fetchAllHits();
//    }

    public List<Program> findProgramsByAge(int age){
        return list("select program from Program program where :age >= ageMin and :age <= ageMax", Parameters.with("age", age));
    }
}
