package schedule.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import schedule.domains.Employee;

public interface  CustomerRepository extends CrudRepository<Employee, Long> {
	List<Employee> findByName(String name);
}
