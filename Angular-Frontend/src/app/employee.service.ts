import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http'
import { Observable } from 'rxjs';
import { Employee } from './employee';

@Injectable({
  providedIn: 'root'
})
export class EmployeeService {

  private baseURL = "http://localhost:8080/api/v1/employees";

  constructor(private httpClient: HttpClient) { }
  
  getEmployeesList(): Observable<Employee[]>{
    return this.httpClient.get<Employee[]>(`${this.baseURL}`);
  }
  createEmployee(employee: Employee): Observable<Object>{
    return this.httpClient.post(`${this.baseURL}`, employee);
  }
  getEmployeeById(id: number): Observable<Employee>{
    return this.httpClient.get<Employee>(`${this.baseURL}/${id}`);
  }
  updateEmployee(id: number, employee: Employee): Observable<Object>{
    return this.httpClient.put(`${this.baseURL}/${id}`, employee);
  }
  deleteEmployee(id: number): Observable<Object>{
    return this.httpClient.delete(`${this.baseURL}/${id}`);
  }

  // Add a hobby to an employee
  addHobbyToEmployee(employeeId: number, hobbyId: number): Observable<Object> {
    return this.httpClient.post(`${this.baseURL}/${employeeId}/hobbies/${hobbyId}`, {});
  }

  // Remove a hobby from an employee
  removeHobbyFromEmployee(employeeId: number, hobbyId: number): Observable<Object> {
    return this.httpClient.delete(`${this.baseURL}/${employeeId}/hobbies/${hobbyId}`);
  }

  // Add a department to an employee
  addDepartmentToEmployee(employeeId: number, departmentId: number): Observable<Object> {
    // Using department-centric API as requested
    return this.httpClient.post(`http://localhost:8080/api/v1/department/${departmentId}/employees/${employeeId}`, {});
  }

  // Remove a department from an employee
  removeDepartmentFromEmployee(employeeId: number, departmentId: number): Observable<Object> {
    // Using department-centric API as requested
    return this.httpClient.delete(`http://localhost:8080/api/v1/department/${departmentId}/employees/${employeeId}`);
  }
}