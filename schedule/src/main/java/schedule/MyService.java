package schedule;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MyService {

    @Autowired
    private ConnectionSettings connection; 

    //...

    @PostConstruct
    public void openConnection() {
        System.out.println(connection);
    }
}