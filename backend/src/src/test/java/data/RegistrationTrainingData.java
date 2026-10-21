package data;
import java.time.LocalDate;

import domaine.entity.RegistrationAgentInTraining;
import domaine.entity.RegistrationStatus;

public class RegistrationTrainingData {


    
    public static RegistrationAgentInTraining registration(RegistrationStatus value){  
        return new RegistrationAgentInTraining(LocalDate.now(),TrainingData.trainingCE().getEndDate(), value,1, 1);
    }

}
