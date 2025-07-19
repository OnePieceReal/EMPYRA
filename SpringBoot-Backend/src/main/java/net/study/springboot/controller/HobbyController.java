package net.study.springboot.controller;

import lombok.NoArgsConstructor;
import net.study.springboot.model.EmployeeModel;
import net.study.springboot.model.HobbyModel;
import net.study.springboot.service.HobbyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.zip.DataFormatException;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/v1/hobbies")
@NoArgsConstructor
//        the @CrossOrigin annotation only allows cross-origin HTTP requests
//        from a single origin. We can take a less restrictive approach and
//        specify multiple origins, on a per-use-case need.
public class HobbyController {
    @Autowired
    private HobbyService hobbyService;

    //Add hobby
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<HobbyModel> createHobby(@RequestBody HobbyModel hobbyModel) throws DataFormatException {
        HobbyModel createdHobby = hobbyService.addHobbyService(hobbyModel);
        return new ResponseEntity<>(createdHobby, HttpStatus.CREATED);
    }

    //Update Hobby
    @PutMapping("{id}")
    public ResponseEntity<HobbyModel> updateHobby(@PathVariable int id, @RequestBody HobbyModel hobbyModel) throws DataFormatException {
        return new ResponseEntity<HobbyModel>(
                hobbyService.hobbyUpdateService(hobbyModel, id),
                HttpStatus.ACCEPTED
        );
    }

    // Get all hobbies
    @GetMapping("/allHobbies")
    public ResponseEntity<List<HobbyModel>> getAllHobbies() {
        List<HobbyModel> hobbies = hobbyService.getAllHobbyService();
        return new ResponseEntity<>(hobbies, HttpStatus.OK);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteHobby(@PathVariable int id) {
        hobbyService.deleteHobbyService(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
