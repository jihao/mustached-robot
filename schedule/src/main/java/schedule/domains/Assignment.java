package schedule.domains;

import java.util.Date;

public class Assignment  {
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