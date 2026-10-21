package domaine.repository;

import java.util.List;

import domaine.entity.RegistrationAgentInTraining;

public interface RegistrationAgentInTrainingRepository {
    public default void save(RegistrationAgentInTraining registration){
        throw new Error("Function not implemented save");
    }
    
    public default void update(RegistrationAgentInTraining registration){
        throw new Error("Function not implemented update");
    }

    public default List<RegistrationAgentInTraining> findByAgent(int agentId){
        throw new Error("Function not implemented findByAgent");
    }  
    
    public default List<RegistrationAgentInTraining> findByTraining(int trainingId){
        throw new Error("Function not implemented findByAgent");
    }  
    
    public default RegistrationAgentInTraining findById(int id){
        throw new Error("Function not implemented findById");
    }

    
    
}
