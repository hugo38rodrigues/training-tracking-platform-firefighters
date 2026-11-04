package usecase;

import java.util.ArrayList;
import java.util.List;

import domaine.entity.Agent;
import domaine.entity.Skill;
import domaine.repository.AgentRepository;
import domaine.repository.SkillRepository;
import usecase.exceptions.InvalidMatriculesException;

public class AddAgent {
    private AgentRepository agentRepo;
    private SkillRepository skillRepo;

    public AddAgent(AgentRepository agentRepo, SkillRepository skillRepo) {
        this.agentRepo = agentRepo;
        this.skillRepo = skillRepo;
    }

    public void execute(Agent agent) {
        if (agentRepo.existsByMatricule(agent.getMatricule())) {
            throw new InvalidMatriculesException("This matricule is existe: " + agent.getMatricule());
        }
        if (String.valueOf(agent.getMatricule()).length() != 5) {
            throw new InvalidMatriculesException("Ce matricule est beaucoup trop long");
        }

        List<Skill> skillsDb = new ArrayList<>();
        for (Skill skill : agent.getSkills()) {
            skillsDb.add(skillRepo.findByName(skill.getName()));
        }
        agent.setSkills(skillsDb);
        agentRepo.add(agent);

    }

}
