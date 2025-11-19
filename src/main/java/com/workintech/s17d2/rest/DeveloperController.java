package com.workintech.s17d2.rest;

import com.workintech.s17d2.model.*;
import com.workintech.s17d2.tax.Taxable;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/developers")
public class DeveloperController {

    public Map<Integer, Developer> developers;
    private final Taxable developerTax;

    @PostConstruct
    public void init() {
        developers = new HashMap<>();
        developers.put(1, new Developer(1, "Tayfun", 50000.0, Experience.JUNIOR));
        developers.put(2, new Developer(2, "Ayşe", 60000.0,Experience.MID));
        developers.put(3, new Developer(3, "Mehmet", 70000.0,Experience.SENIOR));
    }
    @Autowired
    public DeveloperController(Taxable developerTax) {
        this.developerTax = developerTax;
    }

    @GetMapping
    public List<Developer> getDevelopersList(){
        return developers.values().stream().toList();
    }

    @GetMapping("/{id}")
    public Developer getDeveloperById(@PathVariable int id ){
        if(developers.containsKey(id)){
            return developers.get(id);
        } else {
            return null;
        }
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Developer addDeveloper(@RequestBody Developer developer) {

        Developer dev = null;

        switch (developer.getExperience()) {
            case JUNIOR : {
                dev = new JuniorDeveloper(
                        developer.getId(),
                        developer.getName(),
                        developer.getSalary()
                );
                dev.setSalary(dev.getSalary() * (1 - developerTax.getSimpleTaxRate()));
            }
            case MID : {
                dev = new MidDeveloper(
                        developer.getId(),
                        developer.getName(),
                        developer.getSalary()
                );
                dev.setSalary(dev.getSalary() * (1 - developerTax.getMiddleTaxRate()));
            }
            case SENIOR :
            {
                dev = new SeniorDeveloper(
                        developer.getId(),
                        developer.getName(),
                        developer.getSalary()
                );
                dev.setSalary(dev.getSalary() * (1 - developerTax.getUpperTaxRate()));
            }
            default :
                System.out.println("Geçersiz deneyim seviyesi: " + developer.getExperience());
        }

        developers.put(dev.getId(), dev);
        return dev;
    }

    @PutMapping("/{id}")
    public Developer putDeveloper(@PathVariable int id , @RequestBody Developer developer ){
        if(developers.containsKey(id)){
            developers.put(developer.getId(),developer);
        }
        return developer;
    }

    @DeleteMapping("/{id}")
    public void deleteDeveloper(@PathVariable int id){
        developers.remove(id);
    }

}
