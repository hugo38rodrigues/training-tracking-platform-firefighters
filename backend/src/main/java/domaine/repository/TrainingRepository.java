package domaine.repository;

import java.util.List;

import domaine.entity.Training;

public interface TrainingRepository {

    public default boolean existsByTrainingNumber(int trainingNumber) {
        throw new Error("Function not implemented existsByFormationNumber");
    }

    public default void addTraining(Training training) {
        throw new Error("Function not implemented addTraining");
    }

    public default Training findByTrainingNumber(int id) {
        throw new Error("Function not implemented findByFormationNumber");
    }

    public default List<Training> findAll() {
        throw new Error("Function not implemented findAll");
    }

}
