import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Department } from '../../department';
import { DepartmentService } from '../../department.service';
import { MatCardModule } from '@angular/material/card';
import { MatChipsModule } from '@angular/material/chips';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatExpansionModule } from '@angular/material/expansion';
import { MatTableModule } from '@angular/material/table';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { FormsModule } from '@angular/forms';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { DepartmentFormComponent } from '../department-form/department-form.component';
import { Employee } from '../../employee';
import { EmployeeService } from '../../employee.service';

@Component({
  selector: 'app-department-crud',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    MatCardModule,
    MatChipsModule,
    MatIconModule,
    MatButtonModule,
    MatProgressSpinnerModule,
    MatTooltipModule,
    MatExpansionModule,
    MatTableModule,
    MatFormFieldModule,
    MatInputModule,
    MatDialogModule,
    DepartmentFormComponent
  ],
  templateUrl: './department-crud.component.html',
  styleUrls: ['./department-crud.component.css']
})
export class DepartmentCrudComponent implements OnInit {
  departments: Department[] = [];
  filteredDepartments: Department[] = [];
  loading: boolean = true;
  searchTerm: string = '';
  displayedColumns: string[] = ['id', 'firstName', 'lastName', 'actions'];

  // For employee search/add
  allEmployees: Employee[] = [];
  employeeSearch: { [depId: number]: string } = {};
  employeeSearchResults: { [depId: number]: Employee[] } = {};
  addingEmployee: { [depId: number]: boolean } = {};
  removingEmployee: { [depId: number]: number | null } = {};

  constructor(
    private departmentService: DepartmentService,
    private dialog: MatDialog,
    private employeeService: EmployeeService
  ) {}

  ngOnInit(): void {
    this.loadDepartments();
    this.employeeService.getEmployeesList().subscribe({
      next: (employees) => {
        this.allEmployees = employees;
      },
      error: (err) => {
        console.error('Failed to fetch employees:', err);
      }
    });
  }

  loadDepartments() {
    this.loading = true;
    this.departmentService.getDepartments().subscribe({
      next: (departments: Department[]) => {
        this.departments = departments;
        this.filteredDepartments = departments;
        this.loading = false;
      },
      error: (error: any) => {
        console.error('Error loading departments:', error);
        this.loading = false;
      }
    });
  }

  onSearchChange() {
    if (!this.searchTerm.trim()) {
      this.filteredDepartments = this.departments;
    } else {
      const search = this.searchTerm.toLowerCase();
      this.filteredDepartments = this.departments.filter(dept => 
        dept.name.toLowerCase().includes(search) ||
        dept.id.toString().includes(search) ||
        dept.description.toLowerCase().includes(search)
      );
    }
  }

  addDepartment() {
    const dialogRef = this.dialog.open(DepartmentFormComponent, {
      width: '420px',
      data: { department: null, isEditMode: false }
    });
    dialogRef.afterClosed().subscribe((result: Department | undefined) => {
      if (result) {
        this.departmentService.createDepartment(result).subscribe({
          next: () => this.loadDepartments(),
          error: err => alert('Failed to create department: ' + err?.error?.message || err)
        });
      }
    });
  }

  editDepartment(dept: Department) {
    const dialogRef = this.dialog.open(DepartmentFormComponent, {
      width: '420px',
      data: { department: dept, isEditMode: true }
    });
    dialogRef.afterClosed().subscribe((result: Department | undefined) => {
      if (result) {
        this.departmentService.updateDepartment(dept.id, result).subscribe({
          next: () => this.loadDepartments(),
          error: err => alert('Failed to update department: ' + err?.error?.message || err)
        });
      }
    });
  }

  deleteDepartment(dept: Department) {
    if (confirm(`Are you sure you want to delete the department "${dept.name}"? This cannot be undone.`)) {
      this.departmentService.deleteDepartment(dept.id).subscribe({
        next: () => this.loadDepartments(),
        error: err => alert('Failed to delete department: ' + err?.error?.message || err)
      });
    }
  }

  // Employee search logic
  onEmployeeSearchChange(depId: number) {
    const search = (this.employeeSearch[depId] || '').toLowerCase();
    if (!search) {
      this.employeeSearchResults[depId] = [];
      return;
    }
    this.employeeSearchResults[depId] = this.allEmployees.filter(emp =>
      emp.firstName.toLowerCase().includes(search) ||
      emp.lastName.toLowerCase().includes(search) ||
      emp.id.toString().includes(search)
    ).slice(0, 5); // Limit results for UI
  }

  addEmployeeToDepartment(depId: number, empId: number) {
    this.addingEmployee[depId] = true;
    this.departmentService.addEmployeeToDepartment(depId, empId).subscribe({
      next: () => {
        this.loadDepartments();
        this.employeeSearch[depId] = '';
        this.employeeSearchResults[depId] = [];
        this.addingEmployee[depId] = false;
      },
      error: err => {
        alert('Failed to add employee: ' + (err?.error?.message || err));
        this.addingEmployee[depId] = false;
      }
    });
  }

  removeEmployeeFromDepartment(employeeId: number, departmentId: number) {
    this.removingEmployee[departmentId] = employeeId;
    this.departmentService.removeEmployeeFromDepartment(departmentId, employeeId).subscribe({
      next: () => {
        this.loadDepartments();
        this.removingEmployee[departmentId] = null;
      },
      error: err => {
        alert('Failed to remove employee: ' + (err?.error?.message || err));
        this.removingEmployee[departmentId] = null;
      }
    });
  }

  isEmployeeInDepartment(dept: Department, empId: number): boolean {
    return dept.employees.some(e => e.id === empId);
  }
}
 