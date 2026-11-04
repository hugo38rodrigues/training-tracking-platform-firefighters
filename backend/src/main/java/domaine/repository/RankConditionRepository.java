package domaine.repository;

import java.util.List;

import domaine.entity.RankCondition;


public interface RankConditionRepository {
    public default List<RankCondition> getRankCondition(){
        throw new Error("Function not implemented getRankCondition");
    }

}
