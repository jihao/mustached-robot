package schedule.controllers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import schedule.Application;
import schedule.domains.Employee;
import schedule.repository.EmployeeRepository;

@Controller
@RequestMapping("/employee")
public class CustomerController {
	protected static final Logger logger = LoggerFactory.getLogger(CustomerController.class);

	@Autowired
	private EmployeeRepository repository;

	@RequestMapping({"", "/", "list"}) // default
	public String index(final Employee employee) {
		return "employee";
	}
	
	@ModelAttribute("employeeList")
	public List<Employee> populateEmloyees()
	{
		Iterable<Employee> customers = repository.findAll();
		List<Employee> employeeList = new ArrayList<Employee>();
		for (Employee c : customers) {
			employeeList.add(c);
		}
		return employeeList;
	}
	@ModelAttribute("allOfficeTypes")
	public List<String> populateOfficeTypes() {
		return Arrays.asList("1","2","3","4","5");
	}
	
	@RequestMapping(value="/new", method=RequestMethod.POST)
	public String greeting(@Valid Employee employee, Model model) {
		logger.info("employee:{}",employee.toString());
		repository.save(employee);
		logger.info("employee:{}",employee.toString());
		model.asMap().clear();
		
		return "redirect:/employee";
	}

	@RequestMapping(value="/delete/{id}", method=RequestMethod.POST)
	public String delete(@PathVariable Long id, Model model) {
		logger.info("/employee/delete");
		repository.delete(id);
		model.asMap().clear();
		
		return "redirect:/employee";
	}
	
	@RequestMapping(value="/edit/{id}", method=RequestMethod.GET)
	public String edit(@PathVariable Long id, Model model) {
		logger.info("/employee/edit/{id}");
		Employee employee = repository.findOne(id);
		model.addAttribute("employee", employee);	
		return "employee";
	}

	public static void main(String... args) throws Exception {
		AbstractApplicationContext context = new AnnotationConfigApplicationContext(Application.class);
        EmployeeRepository repository = context.getBean(EmployeeRepository.class);

        // save a couple of customers
        repository.save(new Employee("Jack", "Bauer","position","office","..."));
        repository.save(new Employee("Chloe", "O'Brian","position","office","..."));
        repository.save(new Employee("Kim", "Bauer","position","office","..."));
        repository.save(new Employee("David", "Palmer","position","office","..."));
        repository.save(new Employee("Michelle", "Dessler","position","office","..."));

        // fetch all customers
        Iterable<Employee> customers = repository.findAll();
        System.out.println("Customers found with findAll():");
        System.out.println("-------------------------------");
        for (Employee customer : customers) {
            System.out.println(customer);
        }
        System.out.println();

        // fetch an individual customer by ID
        Employee customer = repository.findOne(1L);
        System.out.println("Customer found with findOne(1L):");
        System.out.println("--------------------------------");
        System.out.println(customer);
        System.out.println();

        // fetch customers by last name
        List<Employee> bauers = repository.findByName("Bauer");
        System.out.println("Customer found with findByLastName('Bauer'):");
        System.out.println("--------------------------------------------");
        for (Employee bauer : bauers) {
            System.out.println(bauer);
        }

        context.close();
	}
	
}