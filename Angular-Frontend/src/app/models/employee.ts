import { Hobby } from './hobby';
import { Department } from './department';

export interface EmployeeHobbyMap {
  id: number;
  employeeId: number;
  hobbyId: number;
  hobby: Hobby;
}

export interface EmployeeDepartmentMap {
  id: number;
  employeeId: number;
  departmentId: number;
  department: Department;
}

export class Employee { 
    id: number = 0;
    firstName: string = "";
    lastName: string = "";
    emailId: string = "";
    city: string = "";
    state: string = "";
    country: string = "";
    phone: string = ""; // Changed from number to string
    dob: string = ""; // Date of birth in YYYY-MM-DD format
    dateOfJoining: string = ""; // Date of joining in YYYY-MM-DD format
    salary: number = 0;
    rating: number = 1; // Rating from 1-10
    employeeHobbies: EmployeeHobbyMap[] = [];
    employeeDepartments: EmployeeDepartmentMap[] = [];
}
