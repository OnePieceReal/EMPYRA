import { Component, OnInit, OnDestroy } from '@angular/core';
import { Employee } from '../../employee'
import { EmployeeService } from '../../employee.service'
import { DepartmentService } from '../../department.service'
import { Department } from '../../department'
import { Router, NavigationEnd } from '@angular/router';
import { trigger, state, style, transition, animate, query, stagger, keyframes } from '@angular/animations';
import { filter, takeUntil } from 'rxjs/operators';
import { Subject } from 'rxjs';

@Component({
  selector: 'app-employee-list',
  templateUrl: './employee-list.component.html',
  styleUrls: ['./employee-list.component.css'],
  animations: [
    trigger('fadeIn', [
      transition(':enter', [
        style({ opacity: 0 }),
        animate('500ms ease-out', style({ opacity: 1 }))
      ])
    ]),
    trigger('slideInDown', [
      transition(':enter', [
        style({ opacity: 0, transform: 'translateY(-30px)' }),
        animate('400ms ease-out', style({ opacity: 1, transform: 'translateY(0)' }))
      ])
    ]),
    trigger('fadeInUp', [
      transition(':enter', [
        style({ opacity: 0, transform: 'translateY(30px)' }),
        animate('400ms ease-out', style({ opacity: 1, transform: 'translateY(0)' }))
      ])
    ]),
    trigger('staggerAnimation', [
      transition('* => *', [
        query(':enter', [
          style({ opacity: 0, transform: 'scale(0.8)' }),
          stagger(100, [
            animate('300ms ease-out', style({ opacity: 1, transform: 'scale(1)' }))
          ])
        ], { optional: true })
      ])
    ]),
    trigger('cardAnimation', [
      transition(':enter', [
        style({ opacity: 0, transform: 'translateY(20px)' }),
        animate('300ms ease-out', style({ opacity: 1, transform: 'translateY(0)' }))
      ]),
      transition(':leave', [
        animate('200ms ease-in', style({ opacity: 0, transform: 'translateY(-20px)' }))
      ])
    ]),
    trigger('hoverAnimation', [
      state('normal', style({
        transform: 'scale(1)',
        boxShadow: '0 2px 8px rgba(0,0,0,0.1)'
      })),
      state('hovered', style({
        transform: 'scale(1.02)',
        boxShadow: '0 8px 25px rgba(0,0,0,0.15)'
      })),
      transition('normal => hovered', animate('200ms ease-out')),
      transition('hovered => normal', animate('200ms ease-in'))
    ]),
    trigger('buttonHover', [
      state('normal', style({
        transform: 'scale(1)'
      })),
      state('hovered', style({
        transform: 'scale(1.05)'
      })),
      transition('normal => hovered', animate('150ms ease-out')),
      transition('hovered => normal', animate('150ms ease-in'))
    ]),
    trigger('fabAnimation', [
      transition(':enter', [
        style({ opacity: 0, transform: 'scale(0) rotate(180deg)' }),
        animate('400ms cubic-bezier(0.68, -0.55, 0.265, 1.55)', 
               style({ opacity: 1, transform: 'scale(1) rotate(0deg)' }))
      ])
    ])
  ]
})
export class EmployeeListComponent implements OnInit, OnDestroy {

  employees: Employee[] = [];
  filteredEmployees: Employee[] = [];
  departments: Department[] = [];
  searchTerm: string = '';
  loading: boolean = true;
  private destroy$ = new Subject<void>();
  
  constructor(private employeeService: EmployeeService,
    private departmentService: DepartmentService,
    private router: Router) { }

  ngOnInit(): void {
    this.getEmployees();
    this.getDepartments();
    
    // Subscribe to navigation events to refresh data when returning to this component
    this.router.events.pipe(
      filter(event => event instanceof NavigationEnd),
      takeUntil(this.destroy$)
    ).subscribe((event) => {
      // Type guard to ensure it's NavigationEnd
      if (event instanceof NavigationEnd) {
        // Refresh data when navigating back to employees list
        if (event.url === '/employees' || event.url === '/') {
          console.log('Navigation detected - refreshing employee list data...');
          // Add a small delay to ensure backend has processed the changes
          setTimeout(() => {
            this.getEmployees();
            this.getDepartments();
          }, 500);
        }
      }
    });
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  private getEmployees(){
    this.loading = true;
    this.employeeService.getEmployeesList().subscribe({
      next: (data) => {
        console.log('Fetched employees:', data);
        // Check if employees already have department data
        data.forEach(employee => {
          if (employee.employeeDepartments && employee.employeeDepartments.length > 0) {
            console.log(`Employee ${employee.id} has departments from backend:`, employee.employeeDepartments);
          } else {
            console.log(`Employee ${employee.id} has no departments from backend`);
          }
        });
        
        this.employees = data;
        this.mergeEmployeeDepartments();
        this.filteredEmployees = this.employees;
        this.loading = false;
      },
      error: (error) => {
        console.error('Error fetching employees:', error);
        this.loading = false;
      }
    });
  }

  // Public method to manually refresh data
  public refreshData(): void {
    console.log('Manually refreshing employee list data...');
    this.getEmployees();
    this.getDepartments();
  }

  // Debug method to check API responses
  public debugEmployeeData(): void {
    console.log('=== DEBUG: Employee Data ===');
    console.log('Current employees:', this.employees);
    console.log('Current departments:', this.departments);
    
    this.employees.forEach(employee => {
      console.log(`Employee ${employee.id} (${employee.firstName} ${employee.lastName}):`);
      console.log('  - employeeDepartments:', employee.employeeDepartments);
      console.log('  - employeeHobbies:', employee.employeeHobbies);
    });
  }

  private getDepartments() {
    this.departmentService.getDepartments().subscribe({
      next: (departments) => {
        this.departments = departments;
        this.mergeEmployeeDepartments();
      },
      error: (error) => {
        console.error('Error fetching departments:', error);
      }
    });
  }

  private mergeEmployeeDepartments() {
    if (this.employees.length > 0 && this.departments.length > 0) {
      this.employees.forEach(employee => {
        // If employee already has department data from backend, use that
        if (employee.employeeDepartments && employee.employeeDepartments.length > 0) {
          console.log(`Employee ${employee.id} already has departments:`, employee.employeeDepartments);
          return; // Skip merging if data already exists
        }
        
        // Otherwise, find departments that contain this employee from department data
        const employeeDepartments = this.departments.filter(dept => 
          dept.employees && dept.employees.some(emp => emp.id === employee.id)
        );
        
        // Map to EmployeeDepartmentMap format
        employee.employeeDepartments = employeeDepartments.map(dept => ({
          id: 0, // This would be the mapping ID from backend
          employeeId: employee.id,
          departmentId: dept.id,
          department: dept
        }));
        
        if (employeeDepartments.length > 0) {
          console.log(`Merged departments for employee ${employee.id}:`, employee.employeeDepartments);
        }
      });
    }
  }

  // Search functionality
  onSearchChange(): void {
    if (!this.searchTerm.trim()) {
      this.filteredEmployees = this.employees;
    } else {
      const searchLower = this.searchTerm.toLowerCase();
      this.filteredEmployees = this.employees.filter(employee =>
        employee.firstName.toLowerCase().includes(searchLower) ||
        employee.lastName.toLowerCase().includes(searchLower) ||
        employee.emailId.toLowerCase().includes(searchLower) ||
        employee.city.toLowerCase().includes(searchLower) ||
        employee.state.toLowerCase().includes(searchLower) ||
        employee.country.toLowerCase().includes(searchLower) ||
        employee.phone.toLowerCase().includes(searchLower) ||
        employee.id.toString().includes(searchLower) ||
        employee.salary.toString().includes(searchLower) ||
        employee.rating.toString().includes(searchLower) ||
        this.getHobbyNames(employee).some(hobby => 
          hobby.toLowerCase().includes(searchLower)
        ) ||
        this.getDepartmentNames(employee).some(department => 
          department.toLowerCase().includes(searchLower)
        )
      );
    }
  }

  // Get hobby names from employee
  getHobbyNames(employee: Employee): string[] {
    if (!employee.employeeHobbies || employee.employeeHobbies.length === 0) {
      return [];
    }
    return employee.employeeHobbies.map(hobbyMap => 
      hobbyMap.hobby.name
    );
  }

  // Get department names from employee
  getDepartmentNames(employee: Employee): string[] {
    if (!employee.employeeDepartments || employee.employeeDepartments.length === 0) {
      return [];
    }
    return employee.employeeDepartments.map(deptMap => 
      deptMap.department.name
    );
  }

  // Track by function for better performance
  trackByEmployeeId(index: number, employee: Employee): number {
    return employee.id;
  }

  updateEmployee(id: number){
    this.router.navigate(['update-employee', id]);
  }

  deleteEmployee(id: number){
    if (confirm('Are you sure you want to delete this employee?')) {
      this.employeeService.deleteEmployee(id).subscribe({
        next: (data) => {
          console.log(data);
          this.getEmployees();
        },
        error: (error) => {
          console.error('Error deleting employee:', error);
        }
      });
    }
  }

  // Animation event handlers
  onCardMouseEnter(card: any): void {
    card.state = 'hovered';
  }

  onCardMouseLeave(card: any): void {
    card.state = 'normal';
  }

  onButtonMouseEnter(button: any): void {
    button.state = 'hovered';
  }

  onButtonMouseLeave(button: any): void {
    button.state = 'normal';
  }

  // Format phone number for display
  formatPhone(phone: string): string {
    if (!phone) return 'N/A';
    if (phone.length === 10) {
      return `(${phone.slice(0, 3)}) ${phone.slice(3, 6)}-${phone.slice(6)}`;
    }
    return phone;
  }

  // Format date for display
  formatDate(dateString: string): string {
    if (!dateString) return 'N/A';
    const date = new Date(dateString);
    return date.toLocaleDateString();
  }

  // Format salary for display
  formatSalary(salary: number): string {
    if (!salary) return 'N/A';
    return new Intl.NumberFormat('en-US', {
      style: 'currency',
      currency: 'USD'
    }).format(salary);
  }

  // Get age from date of birth
  getAge(dob: string): number {
    if (!dob) return 0;
    const birthDate = new Date(dob);
    const today = new Date();
    let age = today.getFullYear() - birthDate.getFullYear();
    const monthDiff = today.getMonth() - birthDate.getMonth();
    
    if (monthDiff < 0 || (monthDiff === 0 && today.getDate() < birthDate.getDate())) {
      age--;
    }
    
    return age;
  }

  // Get years of service
  getYearsOfService(dateOfJoining: string): number {
    if (!dateOfJoining) return 0;
    const joiningDate = new Date(dateOfJoining);
    const today = new Date();
    let years = today.getFullYear() - joiningDate.getFullYear();
    const monthDiff = today.getMonth() - joiningDate.getMonth();
    
    if (monthDiff < 0 || (monthDiff === 0 && today.getDate() < joiningDate.getDate())) {
      years--;
    }
    
    return years;
  }
}