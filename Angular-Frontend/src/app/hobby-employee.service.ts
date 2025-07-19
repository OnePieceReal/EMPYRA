import { Injectable } from '@angular/core';
import { Observable, forkJoin, of } from 'rxjs';
import { map, switchMap } from 'rxjs/operators';
import { Hobby } from './hobby';
import { Employee } from './employee';
import { HobbyService } from './hobby.service';
import { EmployeeService } from './employee.service';

@Injectable({
  providedIn: 'root'
})
export class HobbyEmployeeService {

  constructor(
    private hobbyService: HobbyService,
    private employeeService: EmployeeService
  ) { }

  // Get all hobbies with employee count
  getAllHobbiesWithEmployeeCount(): Observable<Array<Hobby & { employeeCount: number }>> {
    return forkJoin({
      hobbies: this.hobbyService.getHobbiesList(),
      employees: this.employeeService.getEmployeesList()
    }).pipe(
      map(({ hobbies, employees }) => {
        return hobbies.map((hobby: Hobby) => ({
          ...hobby,
          employeeCount: this.getEmployeeCountForHobby(hobby.id, employees)
        }));
      })
    );
  }

  // Create hobby
  createHobby(hobby: Hobby): Observable<Hobby> {
    return this.hobbyService.createHobby(hobby);
  }

  // Update hobby and sync with employees
  updateHobby(hobby: Hobby): Observable<Hobby> {
    return this.hobbyService.updateHobby(hobby.id, hobby).pipe(
      switchMap((updatedHobby: Hobby) => {
        return this.syncHobbyWithEmployees(hobby.id, updatedHobby).pipe(
          map(() => updatedHobby)
        );
      })
    );
  }

  // Delete hobby and remove from all employees
  deleteHobby(hobbyId: number): Observable<void> {
    return this.removeHobbyFromAllEmployees(hobbyId).pipe(
      switchMap(() => this.hobbyService.deleteHobby(hobbyId))
    );
  }

  // Get employees who have a specific hobby
  getEmployeesWithHobby(hobbyId: number): Observable<Employee[]> {
    return this.employeeService.getEmployeesList().pipe(
      map(employees => employees.filter(employee => 
        employee.employeeHobbies?.some((hobby: any) => hobby.id === hobbyId)
      ))
    );
  }

  // Helper: Get employee count for a hobby
  private getEmployeeCountForHobby(hobbyId: number, employees: Employee[]): number {
    return employees.filter(employee =>
      employee.employeeHobbies?.some((hobby: any) => hobby.id === hobbyId)
    ).length;
  }

  // Sync hobby updates with all employees who have this hobby
  private syncHobbyWithEmployees(hobbyId: number, updatedHobby: Hobby): Observable<void> {
    return this.employeeService.getEmployeesList().pipe(
      switchMap(employees => {
        const employeesWithHobby = employees.filter(employee => 
          employee.employeeHobbies?.some((hobby: any) => hobby.id === hobbyId)
        );

        if (employeesWithHobby.length === 0) {
          return of(void 0);
        }

        const updateObservables = employeesWithHobby.map(employee => {
          const hobbyObj = employee.employeeHobbies?.find((hobby: any) => hobby.id === hobbyId);
          if (hobbyObj) {
            Object.assign(hobbyObj, updatedHobby);
            return this.employeeService.updateEmployee(employee.id, employee);
          }
          return of(void 0);
        });

        return forkJoin(updateObservables).pipe(
          map(() => void 0)
        );
      })
    );
  }

  // Remove hobby from all employees
  private removeHobbyFromAllEmployees(hobbyId: number): Observable<void> {
    return this.employeeService.getEmployeesList().pipe(
      switchMap(employees => {
        const employeesWithHobby = employees.filter(employee => 
          employee.employeeHobbies?.some((hobby: any) => hobby.id === hobbyId)
        );

        if (employeesWithHobby.length === 0) {
          return of(void 0);
        }

        const updateObservables = employeesWithHobby.map(employee => {
          employee.employeeHobbies = employee.employeeHobbies?.filter((hobby: any) => hobby.id !== hobbyId) || [];
          return this.employeeService.updateEmployee(employee.id, employee);
        });

        return forkJoin(updateObservables).pipe(
          map(() => void 0)
        );
      })
    );
  }

  // Optionally, export removeHobbyFromAllEmployees if needed elsewhere
  public removeHobbyFromAllEmployeesPublic(hobbyId: number): Observable<void> {
    return this.removeHobbyFromAllEmployees(hobbyId);
  }

  // Add hobby to employee
  addHobbyToEmployee(employeeId: number, hobbyId: number): Observable<void> {
    return forkJoin({
      employee: this.employeeService.getEmployeeById(employeeId),
      hobby: this.hobbyService.getHobbyById(hobbyId)
    }).pipe(
      switchMap(({ employee, hobby }) => {
        const existingHobby = employee.employeeHobbies?.find((h: any) => h.hobbyId === hobbyId);
        if (existingHobby) {
          return of(void 0); // Hobby already exists for this employee
        }
        const newHobbyMap = {
          id: 0, // Backend will set this
          employeeId: employeeId,
          hobbyId: hobbyId,
          hobby: hobby
        };
        employee.employeeHobbies = [...(employee.employeeHobbies || []), newHobbyMap];
        return this.employeeService.updateEmployee(employeeId, employee).pipe(
          map(() => void 0)
        );
      })
    );
  }

  // Remove hobby from employee
  removeHobbyFromEmployee(employeeId: number, hobbyId: number): Observable<void> {
    return this.employeeService.getEmployeeById(employeeId).pipe(
      switchMap(employee => {
        employee.employeeHobbies = employee.employeeHobbies?.filter((hobby: any) => hobby.id !== hobbyId) || [];
        return this.employeeService.updateEmployee(employeeId, employee).pipe(
          map(() => void 0)
        );
      })
    );
  }
} 