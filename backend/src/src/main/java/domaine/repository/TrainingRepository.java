package domaine.repository;
import java.util.List;

import domaine.entity.Training;

public interface TrainingRepository {

    public default boolean findTrainingByFormationNumber(int formationNumber){
        throw new Error("Function not implemented findTraining");
    }

    public default void addTraining(Training training){
        throw new Error("Function not implemented addTraining");
    }
   
    public default Training findTrainingById(int id){
        throw new Error("Function not implemented findTrainingById");
    }
    
    public default List<Training> findTrainings(){
        throw new Error("Function not implemented findTrainings");
    }

}
