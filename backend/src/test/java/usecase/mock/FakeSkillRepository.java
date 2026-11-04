package usecase.mock;

import java.util.HashMap;

import domaine.entity.Skill;
import domaine.repository.SkillRepository;

public class FakeSkillRepository implements SkillRepository {
    private HashMap<Integer, Skill> storage = new HashMap<>();

    @Override
    public Skill findByName(String name) {
        return storage.get(name);
    }

}
