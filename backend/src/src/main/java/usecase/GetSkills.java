package usecase;

import java.util.List;

import domaine.entity.Skill;
import domaine.repository.SkillRepository;

public class GetSkills {
    private SkillRepository skillRepository;

    
    public GetSkills(SkillRepository skillRepository) {
        this.skillRepository = skillRepository;
    }


    public List<Skill>execute(){
        return skillRepository.findAll();
    }
}
