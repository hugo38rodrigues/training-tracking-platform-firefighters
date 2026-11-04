package domaine.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

public class RankCalculatorTest {
    
    @Test 
    void validChangeRank(){
        Skill skill = new Skill("CE");
        Agent agent = new Agent("test", "toto", 23659, Rank.SAPEUR_PREMIERE_CLASSE, LocalDate.parse("2022-03-20"), new ArrayList<>(), List.of(skill));
        RankCondition rankCondition = new RankCondition(Rank.CAPORAL, Rank.SAPEUR_PREMIERE_CLASSE, 3, List.of(skill));
        RankCalculator rankCalculator = new RankCalculator();
        Rank caporal =  rankCalculator.compileNewRank(agent, List.of(rankCondition),LocalDate.of(2026, 10, 06));
        assertEquals(Rank.CAPORAL, caporal);
    }
    @Test
    void invalidChangeRankWithYearLess() {
        Skill skill = new Skill("CE");
        Agent agent = new Agent("test", "toto", 23659, Rank.SAPEUR_PREMIERE_CLASSE, LocalDate.parse("2026-03-20"), new ArrayList<>(), List.of(skill));
        RankCondition rankCondition = new RankCondition(Rank.CAPORAL, Rank.SAPEUR_PREMIERE_CLASSE, 3, List.of(skill));
        RankCalculator rankCalculator = new RankCalculator();
        Rank caporal =  rankCalculator.compileNewRank(agent, List.of(rankCondition), LocalDate.of(2026, 10, 06));
        assertEquals(Rank.SAPEUR_PREMIERE_CLASSE, caporal);
    }

    @Test
    void invalidChangeRankWithNotGoodRank() {
        Skill skill = new Skill("CE");
        Agent agent = new Agent("test", "toto", 23659, Rank.SAPEUR_DEUXIEME_CLASSE, LocalDate.parse("2022-03-20"), new ArrayList<>(), List.of(skill));
        RankCondition rankCondition = new RankCondition(Rank.CAPORAL, Rank.SAPEUR_PREMIERE_CLASSE, 3, List.of(skill));
        RankCalculator rankCalculator = new RankCalculator();
        Rank caporal =  rankCalculator.compileNewRank(agent, List.of(rankCondition),LocalDate.of(2026, 10, 06));
        assertEquals(Rank.SAPEUR_DEUXIEME_CLASSE, caporal);
    }

    @Test
    void invalidChangeRankWithNotGoodSkill() {
        Skill skillRulesRank = new Skill("CE");
        Skill skillAgent = new Skill("EQ SSUAP");
        Agent agent = new Agent("test", "toto", 23659, Rank.SAPEUR_PREMIERE_CLASSE, LocalDate.parse("2022-03-20"), new ArrayList<>(), List.of(skillAgent));
        RankCondition rankCondition = new RankCondition(Rank.CAPORAL, Rank.SAPEUR_PREMIERE_CLASSE, 3, List.of(skillRulesRank));
        RankCalculator rankCalculator = new RankCalculator();
        Rank caporal =  rankCalculator.compileNewRank(agent, List.of(rankCondition), LocalDate.of(2026, 10, 06));
        assertEquals(Rank.SAPEUR_PREMIERE_CLASSE, caporal);
    }
}
