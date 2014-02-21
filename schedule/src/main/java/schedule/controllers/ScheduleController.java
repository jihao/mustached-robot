package schedule.controllers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import schedule.domains.Employee;
import schedule.domains.Vacation;
import schedule.repository.EmployeeRepository;
import schedule.repository.VacationRepository;

@Controller
@RequestMapping("/schedule")
public class ScheduleController {
	protected static final Logger logger = LoggerFactory.getLogger(ScheduleController.class);

	@Autowired
	private EmployeeRepository employeeRepository;
	@Autowired
	private VacationRepository vacationRepository;

		
	@RequestMapping({"", "/", "list"}) // default
	public String index(final Vacation vacation) {
		return "schedule_a";
	}
	
	
}