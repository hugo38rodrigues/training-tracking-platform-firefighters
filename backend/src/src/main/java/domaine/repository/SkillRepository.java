package domaine.repository;

import java.util.List;

import domaine.entity.Skill;

public interface SkillRepository {
    public default Skill findByName(String name) {
        throw new Error("Function not implemented findByName");
    }

    public default List<Skill> findAll() {
        throw new Error("Function not implemented findAll");
    }

}
