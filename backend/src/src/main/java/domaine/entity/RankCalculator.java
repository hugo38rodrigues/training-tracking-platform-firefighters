package domaine.entity;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class RankCalculator {

    public Rank compileNewRank(Agent agent, List<RankCondition> rankRules, LocalDate today){
        Rank rank = agent.getRank();
        for(RankCondition rankRule : rankRules){
            if (validation(agent, rankRule, today) && rankRule.getRank().isAtLeast(rank)){
                rank = rankRule.getRank();
            }
        }
        return rank;
    }

    public boolean validation(Agent agent, RankCondition rules, LocalDate today){
        return hasRequireSkills(rules, agent) 
            && hasRequireYears(rules, agent, today)
            && hasRequiredPreviousRank(rules, agent);  
    }

    boolean hasRequiredPreviousRank(RankCondition rule, Agent agent){
        if (rule.getPreviousRank() == null) return true;
        return agent.getRank() == rule.getPreviousRank();
    }

    boolean hasRequireYears(RankCondition rule, Agent agent, LocalDate today){
        long years = ChronoUnit.YEARS.between(agent.getRankStartDate(), today);
        return years >= rule.getRequiredYears();
    }

    boolean hasRequireSkills(RankCondition rule, Agent agent){
        if (agent.isDifferentiated()) return true;
        return agent.getSkills().containsAll(rule.getRequireSkills());   // ← sens corrigé
    }
}