import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Department } from './department';

@Injectable({
  providedIn: 'root'
})
export class DepartmentService {
  private baseURL = 'http://localhost:8080/api/v1/department';

  constructor(private httpClient: HttpClient) { }

  // Get all departments
  getDepartments(): Observable<Department[]> {
    return this.httpClient.get<Department[]>(`${this.baseURL}/allDepartment`);
  }

  // Create new department
  createDepartment(department: Department): Observable<Department> {
    return this.httpClient.post<Department>(`${this.baseURL}`, department);
  }

  // Update department
  updateDepartment(id: number, department: Department): Observable<Department> {
    return this.httpClient.put<Department>(`${this.baseURL}/${id}`, department);
  }

  // Delete department
  deleteDepartment(id: number): Observable<void> {
    return this.httpClient.delete<void>(`${this.baseURL}/${id}`);
  }

  // Add an employee to a department
  addEmployeeToDepartment(departmentId: number, employeeId: number): Observable<any> {
    return this.httpClient.post(`${this.baseURL}/${departmentId}/employees/${employeeId}`, {});
  }

  // Remove an employee from a department
  removeEmployeeFromDepartment(departmentId: number, employeeId: number): Observable<any> {
    return this.httpClient.delete(`${this.baseURL}/${departmentId}/employees/${employeeId}`);
  }
}
