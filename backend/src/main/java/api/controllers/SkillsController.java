package api.controllers;
import java.util.List;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import domaine.entity.Skill;
import usecase.GetSkills;
import org.springframework.web.bind.annotation.GetMapping;



@RestController 
@RequestMapping ("/api/skills")
public class SkillsController {
    
    private final GetSkills getSkills;

    public SkillsController(GetSkills getSkills) {
        this.getSkills = getSkills;
    }

    @GetMapping("/")    
    public List<Skill> findAll(){
        return getSkills.execute();
    }
}
