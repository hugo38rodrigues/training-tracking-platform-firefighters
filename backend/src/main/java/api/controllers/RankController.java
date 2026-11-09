package api.controllers;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import domaine.entity.Rank;

@RestController 
@RequestMapping ("/api/ranks")
public class RankController  {


    @GetMapping("/")
    public List<Rank> findAll() {
        return List.of(Rank.values());
    } 
}
