package api.controllers;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import domaine.entity.Training;
import usecase.GetAllTraining;
import usecase.GetAvailableTrainings;
import usecase.DTO.TrainingOption;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@RequestMapping ("/api/trainings")
@RestController 
public class TrainingController {
    private final GetAllTraining getAllTraining;
    private final GetAvailableTrainings getAvailableTrainings;


    public TrainingController(GetAllTraining getAllTraining, GetAvailableTrainings getAvailableTrainings) {
        this.getAllTraining = getAllTraining;
        this.getAvailableTrainings = getAvailableTrainings;
    }
    
    @GetMapping("/available-trainings")
    public @ResponseBody List<TrainingOption> getAvailableTraining(@RequestParam int matricule){
        return getAvailableTrainings.execute(matricule);
    }
    
    @GetMapping
    public List<Training> findAll(){
        return getAllTraining.execute();
    }
}
