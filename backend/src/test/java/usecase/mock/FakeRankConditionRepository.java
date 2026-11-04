package usecase.mock;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import data.SkillData;
import domaine.entity.Rank;
import domaine.entity.RankCondition;
import domaine.repository.RankConditionRepository;

public class FakeRankConditionRepository implements  RankConditionRepository{
    Map<Integer,RankCondition> repo = Map.of(
        1, new RankCondition(Rank.CAPORAL, Rank.SAPEUR_PREMIERE_CLASSE, 3, SkillData.skillsforCE())
    );
    

    @Override
    public List<RankCondition> getRankCondition() {
       return new ArrayList<>(repo.values());
    }



}
