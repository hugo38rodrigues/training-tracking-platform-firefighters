package usecase;

import java.util.ArrayList;
import java.util.List;

import domaine.entity.Skill;
import domaine.entity.Training;
import domaine.repository.SkillRepository;
import domaine.repository.TrainingRepository;
import usecase.exceptions.InvalidTrainingNumberExecption;

public class AddTraining {
    private TrainingRepository trainingRepo;
    private SkillRepository skillRepo;
    
    public AddTraining(TrainingRepository trainingRepo, SkillRepository skillRepo) {
        this.trainingRepo = trainingRepo;
        this.skillRepo = skillRepo;
    }

    public void execute(Training training) {
        
        if (trainingRepo.findTrainingByFormationNumber(training.getFormationNumber())){
            throw new InvalidTrainingNumberExecption("Cette formation existe déjà: " + training.getFormationNumber());
        }

        List<Skill> skillNeededComplete = new ArrayList<>();
        List<Skill> skillDelivredComplete = new ArrayList<>();
        
        for (Skill skill : training.getSkillsNeeded()){
            skillNeededComplete.add(skillRepo.findSkillByName(skill.getName()));
        }

        for (Skill skill : training.getSkillDelivered()){
            skillDelivredComplete.add(skillRepo.findSkillByName(skill.getName()));
        }

        training.setSkillDelivered(skillDelivredComplete);
        training.setSkillsNeeded(skillNeededComplete);
        trainingRepo.addTraining(training);
    }
    
}
