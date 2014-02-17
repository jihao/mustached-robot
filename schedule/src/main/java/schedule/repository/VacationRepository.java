package schedule.repository;

import org.springframework.data.repository.CrudRepository;

import schedule.domains.Employee;
import schedule.domains.Vacation;

public interface  VacationRepository extends CrudRepository<Vacation, Long> {
	Vacation findOneByEmployee(Employee employee);
}
