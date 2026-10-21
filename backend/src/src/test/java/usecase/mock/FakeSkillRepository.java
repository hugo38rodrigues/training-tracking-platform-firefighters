package usecase.mock;

import java.util.HashMap;

import domaine.entity.Skill;
import  domaine.repository.SkillRepository;

public class FakeSkillRepository implements SkillRepository {
    HashMap<Integer, Skill> storage = new HashMap<>();

    @Override
    public Skill findSkillByName(String name) {
        return storage.values().stream()
                .filter(skill -> skill.getName().equals(name))
                .findFirst()
                .orElse(null);
        }

    }

