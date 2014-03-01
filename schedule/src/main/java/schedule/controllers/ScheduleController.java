package schedule.controllers;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpSession;

import org.apache.commons.io.FileUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.optaplanner.core.impl.solution.Solution;
import org.optaplanner.examples.nurserostering.app.MyNurseRosteringApp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import schedule.NurseRosteringSettings;
import schedule.domains.Assignment;
import schedule.domains.Contract;
import schedule.domains.Employee;
import schedule.domains.Vacation;
import schedule.repository.EmployeeRepository;
import schedule.repository.VacationRepository;
import schedule.utils.CalendarUtils;

@Controller
@RequestMapping("/schedule")
public class ScheduleController {
	protected static final Logger logger = LoggerFactory.getLogger(ScheduleController.class);
	private static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
	private static final String ENCODING = "UTF-8";

	@Autowired
	private NurseRosteringSettings nurseRosteringSettings;
	@Autowired
	private EmployeeRepository employeeRepository;
	@Autowired
	private VacationRepository vacationRepository;
		
	@RequestMapping({"", "/", "list"}) // default
	public String index(final Vacation vacation, Model model) {
		
		Map<String, Map<String, Assignment>> assignmentMap = new TreeMap<String, Map<String,Assignment>>();
		String startDate = "2014-02-27";
		model.addAttribute("assignmentMap", assignmentMap);
		model.addAttribute("weekOfYear", CalendarUtils.weekOfYear(startDate));
		model.addAttribute("mondayDate", CalendarUtils.mondayOfTheWeek(startDate));
		
		return "schedule_a";
	}
	@RequestMapping(value="/a", method=RequestMethod.POST)
	public String automaticSchedule(String startDate, String endDate, Model model, HttpSession session) throws IOException, JDOMException, ParseException {
		String dataDir = nurseRosteringSettings.getDataDir();
		String problemID = startDate+"-"+endDate; // key
		
		File problemFile = generateProblemFile(startDate, endDate, dataDir, problemID);
        File solvedFile = solveProblem(dataDir, problemID, problemFile);
        session.setAttribute("solvedFile", solvedFile.getPath());
        
        String mondayDate = CalendarUtils.formatDate(CalendarUtils.mondayOfTheWeek(startDate));
        return "redirect:/schedule/current?mondayDate="+mondayDate;
        
//		Map<String, Map<String, Assignment>> assignmentMap = parseSolvedResult(startDate, solvedFile);
//		
//		model.asMap().clear();
//		model.addAttribute("assignmentMap", assignmentMap);
//		model.addAttribute("weekOfYear", CalendarUtils.weekOfYear(startDate));
//		model.addAttribute("mondayDate", CalendarUtils.mondayOfTheWeek(startDate));
//		
//		return "schedule_a";
	}
	@RequestMapping({"/prev"})
	public String prev(String mondayDate, Model model, HttpSession session) throws IOException, JDOMException, ParseException {
		String startDate = CalendarUtils.getPrevMondayDate(mondayDate);
		
		File solvedFile = new File( (String) session.getAttribute("solvedFile") );
		Map<String, Map<String, Assignment>> assignmentMap = parseSolvedResult(startDate, solvedFile);
		
		model.asMap().clear();
		model.addAttribute("assignmentMap", assignmentMap);
		model.addAttribute("weekOfYear", CalendarUtils.weekOfYear(startDate));
		model.addAttribute("mondayDate", CalendarUtils.mondayOfTheWeek(startDate));
		
		return "schedule_a";
	}
	@RequestMapping({"/next"})
	public String next(String mondayDate, Model model, HttpSession session) throws IOException, JDOMException, ParseException {
		String startDate = CalendarUtils.getNextMondayDate(mondayDate);
		
		File solvedFile = new File( (String) session.getAttribute("solvedFile") );
		Map<String, Map<String, Assignment>> assignmentMap = parseSolvedResult(startDate, solvedFile);
		
		model.asMap().clear();
		model.addAttribute("assignmentMap", assignmentMap);
		model.addAttribute("weekOfYear", CalendarUtils.weekOfYear(startDate));
		model.addAttribute("mondayDate", CalendarUtils.mondayOfTheWeek(startDate));
		
		return "schedule_a";
	}
	@RequestMapping({"/current"})
	public String current(String mondayDate, Model model, HttpSession session) throws IOException, JDOMException, ParseException {
		
		String startDate = mondayDate;
		if(startDate==null || startDate.isEmpty())
		{
			startDate = CalendarUtils.mondayOfCurrentDate();
		}
		
		File solvedFile = new File( (String) session.getAttribute("solvedFile") );
		Map<String, Map<String, Assignment>> assignmentMap = parseSolvedResult(startDate, solvedFile);
		
		model.asMap().clear();
		model.addAttribute("assignmentMap", assignmentMap);
		model.addAttribute("weekOfYear", CalendarUtils.weekOfYear(startDate));
		model.addAttribute("mondayDate", CalendarUtils.mondayOfTheWeek(startDate));
		
		return "schedule_a";
	}
	
	
	private Map<String, Map<String, Assignment>> parseSolvedResult(String startDate, File solvedFile)
			throws JDOMException, IOException, ParseException {
		SAXBuilder builder = new SAXBuilder();
		Document document = builder.build(solvedFile);
		List<Element> assignmentElementList = (List<Element>) document.getRootElement().getChildren("Assignment");
		
		//outputter.output(assignmentElementList.get(0), System.out);
		logger.debug("assignmentElementList.size()={}",assignmentElementList.size());
		
		List<Assignment> assignmentList = getAssignmentList(assignmentElementList);
		
		// key is employee id
		Map<String, Map<String, Assignment>> assignmentMap = new TreeMap<String, Map<String,Assignment>>();
		
		Calendar calendarDate = Calendar.getInstance();
		//calendarDate.setTime(simpleDateFormat.parse(startDate));
		
		Date monday = CalendarUtils.mondayOfTheWeek(startDate);
		calendarDate.setTime(monday);
		Date sunday = CalendarUtils.sundayOfTheWeek(startDate);;
		
		for (Assignment assignment : assignmentList) {
			String employeeID = String.valueOf(assignment.getEmployeeID());
			String date = simpleDateFormat.format(assignment.getDate());
			Map<String, Assignment> employeeAssignmentMap = assignmentMap.get(employeeID);
			if (employeeAssignmentMap == null) {
				employeeAssignmentMap = new TreeMap<String, Assignment>();
			}
			
			// assignment within this week 
			if (assignment.getDate().compareTo(monday) >= 0 && assignment.getDate().compareTo(sunday) <= 0) {
				employeeAssignmentMap.put(date, assignment);
			}
			assignmentMap.put(employeeID, employeeAssignmentMap);
		}
		Set<Integer> employeeIDList = findEmployeeIDSet(assignmentList);
		for (Iterator<Integer> iterator = employeeIDList.iterator(); iterator.hasNext();) { // loop over employeeID list
			Integer employeeID = (Integer) iterator.next();
			Map<String, Assignment> employeeAssignmentMap = assignmentMap.get(String.valueOf(employeeID));
			for (int i = 1; i <= 7; i++) { // loop over dates in this week
				Date date = calendarDate.getTime();
				String dateStr = simpleDateFormat.format(date);
				if (!employeeAssignmentMap.containsKey(dateStr))
					employeeAssignmentMap.put(dateStr, null);  	// fill with empty value just like placeholder
				
				calendarDate.add(Calendar.DAY_OF_MONTH, 1);
			}
			calendarDate.setTime(monday);
		}
		return assignmentMap;
	}
	private File solveProblem(String dataDir, String problemID, File problemFile) throws IOException {
		final MyNurseRosteringApp app = new MyNurseRosteringApp();
		app.getSolutionBusiness().importSolution(problemFile);

		ExecutorService executor = Executors.newFixedThreadPool(1);
		final Solution planningProblem = app.getSolutionBusiness().getSolution(); 
		executor.submit(new Runnable() {
			@Override
			public void run() {
				Solution bestSolution = null;
				try {
					bestSolution = app.getSolutionBusiness().solve(planningProblem); 
				} catch (final Throwable e) {
					// Otherwise the newFixedThreadPool will eat the exception...
					logger.error("Solving failed.", e);
					bestSolution = null;
				}
				app.getSolutionBusiness().setSolution(bestSolution); 
			}

		});

		// main thead wait for 1 minute then terminate the solving result
		try {
			TimeUnit.SECONDS.sleep(10);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		logger.debug("get 1st best result");
		
		String solvedFilePath = dataDir + "/solved/"+problemID+".xml";
		File solvedFile = new File(solvedFilePath);
		
		app.getSolutionBusiness().exportSolution(solvedFile);
		
		app.getSolutionBusiness().terminateSolvingEarly();
		
		logger.debug(FileUtils.readFileToString(solvedFile, ENCODING));
		return solvedFile;
	}
	private File generateProblemFile(String startDate, String endDate, String dataDir,
			String problemID) throws IOException {
		XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());
		StringWriter sw = new StringWriter();
		outputter.output(generateEmployees(), sw); // employees
		
		StringBuilder sb = new StringBuilder();
		String meta0 = FileUtils.readFileToString(new File(dataDir+"/meta/meta0.xml"), ENCODING);
		String meta1 = FileUtils.readFileToString(new File(dataDir+"/meta/meta1.xml"), ENCODING);
		String meta2 = FileUtils.readFileToString(new File(dataDir+"/meta/meta2.xml"), ENCODING);
		
		sb.append(meta0);
		sb.append("<StartDate>"+startDate+"</StartDate>");
		sb.append("<EndDate>"+endDate+"</EndDate>");
		sb.append(meta1);
		sb.append(sw.getBuffer()); 
		sb.append(meta2);
		
		
		String problemFilePath = dataDir+"/import/"+problemID+".xml";
		
		File problemFile = new File(problemFilePath);
		
		FileUtils.writeStringToFile(problemFile, sb.toString(), ENCODING);
		return problemFile;
	}
	
	/**
	 * <pre>
  &lt;Employees>
    &lt;Employee ID="0">
      &lt;ContractID>0&lt;/ContractID>
      &lt;Name>0&lt;/Name>
      &lt;Skills>
        &lt;Skill>Nurse&lt;/Skill>
      &lt;/Skills>
    &lt;/Employee>
    ...
    	</pre>
	 * @return
	 * @throws IOException
	 */
	public Element generateEmployees() throws IOException {
        Element employeesElement = new Element("Employees");
        
        for (Employee employee : populateEmloyees()) {
            Element employeeElement = new Element("Employee");
            employeeElement.setAttribute("ID", String.valueOf(employee.getId()));

            Element contractIDElement = new Element("ContractID");
            contractIDElement.setText(String.valueOf(employee.getContract().getId()));
            employeeElement.addContent(contractIDElement);
            
            Element nameElement = new Element("Name");
            nameElement.setText(employee.getName());
            employeeElement.addContent(nameElement);

            Element skillsElement = new Element("Skills");
            employeeElement.addContent(skillsElement);
            
            Element skillElement = new Element("Skill");
            skillElement.setText("Nurse");
            skillsElement.addContent(skillElement);
            
            employeesElement.addContent(employeeElement);
        }
        
        return employeesElement;
    }
		
	private List<Employee> populateEmloyees() {
		Iterable<Employee> customers = employeeRepository.findAll();
		List<Employee> employeeList = new ArrayList<Employee>();
		for (Employee e : customers) {
			// TODO: fix me
			Contract c = new Contract();
			c.setId(0l);
			e.setContract(c);
			
			employeeList.add(e);
		}
		return employeeList;
	}
	
	private List<Assignment> getAssignmentList(List<Element> assignmentElementList) {
		List<Assignment> list = new ArrayList<Assignment>();
		for (Element elem : assignmentElementList) {
			String dateStr = elem.getChildText("Date");
			String employeeID = elem.getChildText("Employee");
			String shiftType = elem.getChildText("ShiftType");
			Date date = null;
			try {
				date = simpleDateFormat.parse(dateStr);
			} catch (ParseException e) {
				// eat it, since we can guarantee the format is correct
				logger.error("should never happen, the date string is {}", dateStr);
			}
			list.add(new Assignment(date, Integer.valueOf(employeeID), shiftType));
		}
		return list;
	}

	private Set<Integer> findEmployeeIDSet(List<Assignment> list) {
		Set<Integer> result = new TreeSet<Integer>();
		for (Assignment assignment : list) {
			result.add(assignment.getEmployeeID());
		}
		return result;
	}
	
	
}