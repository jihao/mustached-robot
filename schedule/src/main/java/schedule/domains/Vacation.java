/*
 * Copyright 2010 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package schedule.domains;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;


@Entity
public class Vacation {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private long id;
    
    @OneToOne
    private Employee employee;

	@Override
	public String toString() {
		return "Vacation [id=" + id + ", employee=" + employee + ", dates=" + dates + ", office="
				+ office + ", notes=" + notes + "]";
	}
	private String dates;
	private String office;
    private String notes;
	
    public Vacation() {
		super();
	}
	public Vacation(Employee employee, String dates, String office, String notes) {
		super();
		this.employee = employee;
		this.dates = dates;
		this.office = office;
		this.notes = notes;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public Employee getEmployee() {
		return employee;
	}
	public void setEmployee(Employee employee) {
		this.employee = employee;
	}
	public String getDates() {
		return dates;
	}
	public void setDates(String dates) {
		this.dates = dates;
	}
	public String getOffice() {
		return office;
	}
	public void setOffice(String office) {
		this.office = office;
	}
	public String getNotes() {
		return notes;
	}
	public void setNotes(String notes) {
		this.notes = notes;
	}

    

}
