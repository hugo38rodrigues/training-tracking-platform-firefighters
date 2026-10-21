package domaine.repository;

import domaine.entity.Skill;

public interface SkillRepository {
    public default Skill findSkillByName(String name){
        throw new Error("Function not implemented addAgent");
    }
    

}
