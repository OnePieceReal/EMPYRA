package net.study.springboot.helper;

import net.study.springboot.domain.Hobby;
import net.study.springboot.model.HobbyModel;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class HobbyModelConverter {
    @Autowired
    private ModelMapper modelMapper;
    //conversion for the hobby class
    public HobbyModel convertHobbytoHobbyModel(Hobby hobby){
        HobbyModel hobbyModel = modelMapper.map(hobby, HobbyModel.class);
        return hobbyModel;
    }
    public Hobby convertHobbyModeltoHobby(HobbyModel hobbyModel){
        Hobby hobby=modelMapper.map(hobbyModel,Hobby.class);
        return hobby;
    }


}
