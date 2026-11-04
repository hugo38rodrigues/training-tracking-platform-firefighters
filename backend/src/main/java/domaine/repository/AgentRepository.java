package domaine.repository;
import java.util.List;

import domaine.entity.Agent;

public interface AgentRepository {
    
    public default void add(Agent agent){
        throw new Error("Function not implemented addAgent");
    }
    
    public default void update(Agent agent){
        throw new Error("Function not implemented update");
    }

    public default Agent findByMatricule(int matricule){
        throw new Error("Function not implemented findByMatricule");
    }

    public default boolean existsByMatricule(int matricule){
        throw new Error("Function not implemented existsByMatricule");
    }

    public default List<Agent> findAllAgent(){
            throw new Error("Function not implemented findAllAgent");
    }


    
}
