package net.study.springboot.service;

import net.study.springboot.domain.Employee;
import net.study.springboot.domain.Hobby;
import net.study.springboot.exception.DataFormatException;
import net.study.springboot.exception.ResourceAlreadyExistException;
import net.study.springboot.exception.ResourceNotFoundException;
import net.study.springboot.helper.EmployeeModelConverter;
import net.study.springboot.helper.HobbyModelConverter;
import net.study.springboot.model.EmployeeModelWithOutList;
import net.study.springboot.model.HobbyModel;
import net.study.springboot.repository.HobbyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class HobbyService {
    @Autowired
    private HobbyRepository hobbyRepository;
    @Autowired
    private HobbyModelConverter hobbyModelConverter;
    @Autowired
    private EmployeeModelConverter employeeModelConverter;
    public HobbyModel getHobbyById(int id){
        return hobbyModelConverter.convertHobbytoHobbyModel(hobbyRepository.findById(id).orElseThrow(()-> new ResourceNotFoundException("hobby does not exist with id:" + id)));
    }

    //check the validity of the provided paramters
    public boolean validateAllParameters(HobbyModel hobbyModel){
        if(hobbyModel.getName() == null || hobbyModel.getDescription() == null
    || hobbyModel.getName().length() == 0 || hobbyModel.getDescription().length() == 0
       ){
            return false;
        }
        return true;
    }



    //Add hobby service
    public HobbyModel addHobbyService(HobbyModel hobbyModel) {
        //validate the provided parameters
        if(!validateAllParameters(hobbyModel)){
            throw new DataFormatException("Invalid data format - add - hobby");
        }
        //check if hobby already exist by name or id
        if(hobbyRepository.existsById(hobbyModel.getId()) || hobbyRepository.existsByName(hobbyModel.getName())){
            throw new ResourceAlreadyExistException("hobby - add - resource exist");
        }
        //add data
        Hobby hobby = hobbyModelConverter.convertHobbyModeltoHobby(hobbyModel);
        hobbyRepository.save(hobby);
        hobbyModel.setId(hobbyRepository.findIdByName(hobbyModel.getName()));
       return hobbyModel;
    }

    //Update hobby
    public HobbyModel hobbyUpdateService(HobbyModel hobbyModel,int id){
        //check the format of the data
        if(!validateAllParameters(hobbyModel) || hobbyModel.getId() != id){
            throw new DataFormatException("hobby -update -invalid format");
        }
        //check if the id even exists
        if(!hobbyRepository.existsById(hobbyModel.getId())){
            throw new ResourceNotFoundException("hobby -update -data not found");
        }
        //check if another hobby exists by name
        if(hobbyRepository.existsByNameExcludingId(hobbyModel.getName(),hobbyModel.getId())){
            throw new ResourceAlreadyExistException("hobby -update -name already exist");
        }
        Hobby hobby = hobbyModelConverter.convertHobbyModeltoHobby(hobbyModel);
        hobbyRepository.save(hobby);
        return hobbyModel;
    }

    //get all hobbies
    public List<HobbyModel> getAllHobbyService() {
        List<Hobby> hobbies = hobbyRepository.findAll();
        List<HobbyModel> hobbyModels = hobbies.stream()
                .map(hobby -> HobbyModel.builder()
                        .Id(hobby.getId())
                        .name(hobby.getName())
                        .description(hobby.getDescription())
                        .build())
                .collect(Collectors.toList());

        return hobbyModels;
    }

    //delete hobby
    public void deleteHobbyService(int id) {
        if (!hobbyRepository.existsById(id)) {
            throw new ResourceNotFoundException("Hobby with ID " + id + " does not exist.");
        }

        hobbyRepository.deleteById(id);
    }


}
