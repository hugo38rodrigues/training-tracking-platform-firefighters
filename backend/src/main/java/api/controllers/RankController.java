package api.controllers;

import org.springframework.web.bind.annotation.RestController;

import domaine.entity.Rank;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController 
@RequestMapping("/api/ranks")
public class RankController {

    @GetMapping
    public List<Rank> findAll(){
        return List.of(Rank.values());
    }
}
