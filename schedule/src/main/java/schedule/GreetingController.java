package schedule;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.io.FileUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.optaplanner.core.impl.solution.Solution;
import org.optaplanner.examples.nurserostering.app.MyNurseRosteringApp;
import org.optaplanner.examples.nurserostering.domain.Employee;
import org.optaplanner.examples.nurserostering.domain.contract.Contract;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class GreetingController {
	private static final String ENCODING = "UTF-8";
	protected static final Logger logger = LoggerFactory.getLogger(GreetingController.class);
	private static final String template = "Hello, %s!";
	private final AtomicLong counter = new AtomicLong();
	private static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

	@Autowired
	private NurseRosteringSettings nurseRosteringSettings;

	@RequestMapping("/greeting")
	public @ResponseBody
	Greeting greeting(
			@RequestParam(value = "name", required = false, defaultValue = "World") String name) {
		return new Greeting(counter.incrementAndGet(), String.format(template, name));
	}
	
	@RequestMapping("/index")
	public String index() {
		
		return "index";
	}
	
	@RequestMapping("/greeting3")
	public String greeting(
			@RequestParam(value = "name", required = false, defaultValue = "123") String name,
			Model model) {
		model.addAttribute("name", name);
		return "greeting";
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
        
        for (Employee employee : getEmployeeList()) {
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

	public static void main(String... args) throws Exception {
		GreetingController gc = new GreetingController();
		NurseRosteringSettings settings = new NurseRosteringSettings();
		settings.setDataDir("C:\\design_env\\github\\mustached-robot\\schedule\\data\\nurserostering");
        XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());
        StringWriter sw = new StringWriter();
        outputter.output(gc.generateEmployees(), sw);
        System.out.println(sw.toString());
        
        StringBuilder sb = new StringBuilder();
        String meta1 = FileUtils.readFileToString(new File(settings.getDataDir()+File.separator+"meta/meta1.xml"), ENCODING);
        String meta2 = FileUtils.readFileToString(new File(settings.getDataDir()+File.separator+"meta/meta2.xml"), ENCODING);
        sb.append(meta1);
        sb.append(sw.getBuffer());
        sb.append(meta2);
        FileUtils.writeStringToFile(new File(settings.getDataDir()+"/import/"+UUID.randomUUID()+".xml"), sb.toString(), ENCODING);
        
	}
	public NurseRosteringSettings getNurseRosteringSettings() {
		return nurseRosteringSettings;
	}

	public void setNurseRosteringSettings(NurseRosteringSettings nurseRosteringSettings) {
		this.nurseRosteringSettings = nurseRosteringSettings;
	}

	private List<Employee> getEmployeeList() {
		List<Employee> list = new ArrayList<Employee>();
		for (int i = 0; i < 10; i++) {
			Employee e = new Employee();
			e.setName("name"+i);
			Contract c = new Contract();
			c.setId(0l);
			e.setContract(c);
			e.setId(Long.valueOf(i));
			list.add(e);
		}
		return list;
	}
	
	@RequestMapping("/main")
	public String greeting(Model model) throws IOException, JDOMException {
		// 1. generate to be imported problem
		// 2. solve
		// 3. generate result
		String dataDir = nurseRosteringSettings.getDataDir();
		
		XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());
        StringWriter sw = new StringWriter();
        outputter.output(generateEmployees(), sw); // employees
        
        StringBuilder sb = new StringBuilder();
        String meta1 = FileUtils.readFileToString(new File(dataDir+"/meta/meta1.xml"), ENCODING);
        String meta2 = FileUtils.readFileToString(new File(dataDir+"/meta/meta2.xml"), ENCODING);
        sb.append(meta1);
        sb.append(sw.getBuffer()); 
        sb.append(meta2);
        
        String problemID = UUID.randomUUID().toString();
        String problemFilePath = dataDir+"/import/"+problemID+".xml";
        String solvedFilePath = dataDir + "/solved/"+problemID+".xml";
        File problemFile = new File(problemFilePath);
        File solvedFile = new File(solvedFilePath);
        
        FileUtils.writeStringToFile(problemFile, sb.toString(), ENCODING);
        
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
		System.out.println("get 1st best result");
		app.getSolutionBusiness().exportSolution(solvedFile);

//		try {
//			TimeUnit.SECONDS.sleep(10);
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
//		System.out.println("get 2nd best result");
//		app.getSolutionBusiness().exportSolution(solvedFile);
//
//		try {
//			TimeUnit.SECONDS.sleep(10);
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
//		System.out.println("get 3rd best result");
//		app.getSolutionBusiness().exportSolution(solvedFile);
		
		app.getSolutionBusiness().terminateSolvingEarly();
		
		String solved = FileUtils.readFileToString(solvedFile, ENCODING);
		System.out.println(solved);
		model.addAttribute("solved", solved);

		SAXBuilder builder = new SAXBuilder();
		Document document = builder.build(solvedFile);
		List<Element> assignmentElementList = (List<Element>) document.getRootElement().getChildren("Assignment");
		
		outputter.output(assignmentElementList.get(0), System.out);
		System.out.println(assignmentElementList.size());
		
		List<Assignment> assignmentList = getAssignmentList(assignmentElementList);
		
		// key is employee id
		Map<String, Map<String, Assignment>> assignmentMap = new TreeMap<String, Map<String,Assignment>>();
		
		Calendar startDate = Calendar.getInstance();
		startDate.set(2014, Calendar.JANUARY, 1);
		
		for (Assignment assignment : assignmentList) {
			String employeeID = String.valueOf(assignment.getEmployeeID());
			String date = simpleDateFormat.format(assignment.getDate());
			Map<String, Assignment> employeeAssignmentMap = assignmentMap.get(employeeID);
			if (employeeAssignmentMap == null) {
				employeeAssignmentMap = new HashMap<String, Assignment>();
			}
			employeeAssignmentMap.put(date, assignment);
			assignmentMap.put(employeeID, employeeAssignmentMap);
		}
		Set<Integer> employeeIDList = findEmployeeIDSet(assignmentList);
		for (Iterator<Integer> iterator = employeeIDList.iterator(); iterator.hasNext();) { // loop over employeeID list
			Integer employeeID = (Integer) iterator.next();
			Map<String, Assignment> employeeAssignmentMap = assignmentMap.get(String.valueOf(employeeID));
			for (int i = 1; i <= 7; i++) { // loop over dates
				Date date = startDate.getTime();
				String dateStr = simpleDateFormat.format(date);
				if (!employeeAssignmentMap.containsKey(dateStr))
					employeeAssignmentMap.put(dateStr, null);  	// fill with empty value just like placeholder
				
				startDate.add(Calendar.DAY_OF_MONTH, 1);
			}
			startDate.set(2014, Calendar.JANUARY, 1);
		}
		
		model.addAttribute("assignmentMap", assignmentMap);
		
//		Set<Integer> employeeIDList = findEmployeeIDSet(assignmentList);
//		for (Iterator iterator = employeeIDList.iterator(); iterator.hasNext();) { // loop over employeeID list
//			// key is date format: "2014-01-01"
//			Map<String, Assignment> employeeAssignmentMap = new HashMap<String, Assignment>();
//			Integer employeeID = (Integer) iterator.next();
//			
//			for (int i = 1; i <= 7; i++) { // loop over dates
//				Date date = startDate.getTime();
//				Assignment assignment = findAssignmentOfEmployeeAndDate(employeeID, date, assignmentList);
//				employeeAssignmentMap.put(simpleDateFormat.format(date), assignment);
//
//				startDate.add(Calendar.DAY_OF_MONTH, 1);
//			}
//			
//			assignmentMap.put(String.valueOf(employeeID), employeeAssignmentMap);
//		}
//		model.addAttribute("assignmentMap", assignmentMap);
		return "greeting_style";
	}
	
	private List<Assignment> getAssignmentList(List<Element> assignmentElementList) {
		List<Assignment> list = new ArrayList<GreetingController.Assignment>();
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

	/**
	 * @param employeeID
	 * @param date
	 * @param list
	 * @return null if not found
	 */
	public Assignment findAssignmentOfEmployeeAndDate(int employeeID, Date date, List<Assignment> list) {
		for (Assignment assignment : list) {
			if (assignment.getEmployeeID() == employeeID && date.equals(assignment.getDate()))
				return assignment;
		}
		return null;
	}
	class Assignment  {
		private Date date;
		private int employeeID;
		private String shiftType;
		
		public Assignment(Date date, int employeeID, String shiftType) {
			super();
			this.date = date;
			this.employeeID = employeeID;
			this.shiftType = shiftType;
		}
		public Date getDate() {
			return date;
		}
		public void setDate(Date date) {
			this.date = date;
		}
		public int getEmployeeID() {
			return employeeID;
		}
		public void setEmployeeID(int employeeID) {
			this.employeeID = employeeID;
		}
		public String getShiftType() {
			return shiftType;
		}
		public void setShiftType(String shiftType) {
			this.shiftType = shiftType;
		}
		
	}
	
	class AssignmentDateComparator implements Comparator<Assignment> {
		@Override
		public int compare(Assignment o1, Assignment o2) {
			return o1.getDate().compareTo(o2.getDate());
		}
	}
	
	class AssignmentEmployeeComparator implements Comparator<Assignment> {
		@Override
		public int compare(Assignment o1, Assignment o2) {
			return o1.getEmployeeID() - o2.getEmployeeID();
		}
	}
//	private void readEmployeeList(Element employeesElement) throws JDOMException {
//        List<Element> employeeElementList = (List<Element>) employeesElement.getChildren();
//        List<Employee> employeeList = new ArrayList<Employee>(employeeElementList.size());
//        employeeMap = new HashMap<String, Employee>(employeeElementList.size());
//        long id = 0L;
//        List<SkillProficiency> skillProficiencyList
//                = new ArrayList<SkillProficiency>(employeeElementList.size() * 2);
//        long skillProficiencyId = 0L;
//        for (Element element : employeeElementList) {
//            assertElementName(element, "Employee");
//            Employee employee = new Employee();
//            employee.setId(id);
//            employee.setCode(element.getAttribute("ID").getValue());
//            employee.setName(element.getChild("Name").getText());
//            Element contractElement = element.getChild("ContractID");
//            Contract contract = contractMap.get(contractElement.getText());
//            if (contract == null) {
//                throw new IllegalArgumentException("The contract (" + contractElement.getText()
//                        + ") of employee (" + employee.getCode() + ") does not exist.");
//            }
//            employee.setContract(contract);
//            int estimatedRequestSize = (shiftDateMap.size() / employeeElementList.size()) + 1;
//            employee.setDayOffRequestMap(new HashMap<ShiftDate, DayOffRequest>(estimatedRequestSize));
//            employee.setDayOnRequestMap(new HashMap<ShiftDate, DayOnRequest>(estimatedRequestSize));
//            employee.setShiftOffRequestMap(new HashMap<Shift, ShiftOffRequest>(estimatedRequestSize));
//            employee.setShiftOnRequestMap(new HashMap<Shift, ShiftOnRequest>(estimatedRequestSize));
//
//            Element skillsElement = element.getChild("Skills");
//            if (skillsElement != null) {
//                List<Element> skillElementList = (List<Element>) skillsElement.getChildren();
//                for (Element skillElement : skillElementList) {
//                    assertElementName(skillElement, "Skill");
//                    Skill skill = skillMap.get(skillElement.getText());
//                    if (skill == null) {
//                        throw new IllegalArgumentException("The skill (" + skillElement.getText()
//                                + ") of employee (" + employee.getCode() + ") does not exist.");
//                    }
//                    SkillProficiency skillProficiency = new SkillProficiency();
//                    skillProficiency.setId(skillProficiencyId);
//                    skillProficiency.setEmployee(employee);
//                    skillProficiency.setSkill(skill);
//                    skillProficiencyList.add(skillProficiency);
//                    skillProficiencyId++;
//                }
//            }
//
//            employeeList.add(employee);
//            if (employeeMap.containsKey(employee.getCode())) {
//                throw new IllegalArgumentException("There are 2 employees with the same code ("
//                        + employee.getCode() + ").");
//            }
//            employeeMap.put(employee.getCode(), employee);
//            id++;
//        }
// 
//    }
}