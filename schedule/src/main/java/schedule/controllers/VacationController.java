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
@RequestMapping("/vacation")
public class VacationController {
	protected static final Logger logger = LoggerFactory.getLogger(VacationController.class);

	@Autowired
	private EmployeeRepository employeeRepository;
	@Autowired
	private VacationRepository vacationRepository;

		
	@RequestMapping({"", "/", "list"}) // default
	public String index(final Vacation vacation) {
		return "vacation";
	}
	
	@ModelAttribute("allEmployees")
	public List<Employee> populateEmloyees()
	{
		Iterable<Employee> results = employeeRepository.findAll();
		List<Employee> list = new ArrayList<Employee>();
		for (Employee c : results) {
			list.add(c);
		}
		return list;
	}
	@ModelAttribute("allOfficeTypes")
	public List<String> populateOfficeTypes() {
		return Arrays.asList("1","2","3","4","5");
	}
	@ModelAttribute("allVacations")
	public List<Vacation> populateVacations()
	{
		Iterable<Vacation> results = vacationRepository.findAll();
		List<Vacation> list = new ArrayList<Vacation>();
		for (Vacation c : results) {
			list.add(c);
		}
		return list;
	}
	
	
	@RequestMapping(value="/new", method=RequestMethod.POST)
	public String greeting(@Valid Vacation vacation, Model model) {
		logger.info("vacation:{}",vacation.toString());
		vacationRepository.save(vacation);
		logger.info("vacation:{}",vacation.toString());
		model.asMap().clear();
		
		return "redirect:/vacation";
	}

	@RequestMapping(value="/delete/{id}", method=RequestMethod.POST)
	public String delete(@PathVariable Long id, Model model) {
		logger.info("/vacation/delete");
		vacationRepository.delete(id);
		model.asMap().clear();
		
		return "redirect:/vacation";
	}
	
	@RequestMapping(value="/edit/{id}", method=RequestMethod.GET)
	public String edit(@PathVariable Long id, Model model) {
		logger.info("/vacation/edit/{id}");
		Vacation vaction = vacationRepository.findOne(id);
		model.addAttribute("vacation", vaction);	
		return "vacation";
	}
	
}