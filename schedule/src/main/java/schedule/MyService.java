package schedule;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MyService {

    @Autowired
    private ConnectionSettings connection; 
    
    @Autowired
    private NurseRosteringSettings nurseRosteringSettings;

    //...

    @PostConstruct
    public void openConnection() {
        System.out.println(connection);
    }
    
    @PostConstruct
    public void getNurseRosteringSettings() {
    	System.out.println(nurseRosteringSettings);
    }
}