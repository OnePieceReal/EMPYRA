import { Component, OnInit } from '@angular/core';
import { EmployeeService } from '../../employee.service';
import { HobbyService } from '../../hobby.service';
import { DepartmentService } from '../../department.service';
import { LocationService, Country, State, City } from '../../services/location.service';
import { Employee } from '../../employee';
import { Hobby } from '../../hobby';
import { Department } from '../../department';
import { ActivatedRoute, Router } from '@angular/router';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { trigger, transition, style, animate } from '@angular/animations';
import { Observable, map, startWith, forkJoin, debounceTime, distinctUntilChanged, switchMap } from 'rxjs';

@Component({
  selector: 'app-update-employee',
  templateUrl: './update-employee.component.html',
  styleUrls: ['./update-employee.component.css'],
  animations: [
    trigger('fadeIn', [
      transition(':enter', [
        style({ opacity: 0, transform: 'translateY(20px)' }),
        animate('400ms ease-out', style({ opacity: 1, transform: 'translateY(0)' }))
      ])
    ])
  ]
})
export class UpdateEmployeeComponent implements OnInit {

  id: number = 0;
  employeeForm: FormGroup;
  loading: boolean = false;
  allHobbies: Hobby[] = [];
  selectedHobbies: Hobby[] = [];
  originalHobbies: Hobby[] = [];
  filteredHobbies: Observable<Hobby[]> = new Observable();
  allDepartments: Department[] = [];
  selectedDepartments: Department[] = [];
  originalDepartments: Department[] = [];
  filteredDepartments: Observable<Department[]> = new Observable();
  
  // Location autocomplete observables
  filteredCountries: Observable<Country[]> = new Observable();
  filteredStates: Observable<State[]> = new Observable();
  filteredCities: Observable<City[]> = new Observable();
  
  allEmployees: Employee[] = [];
  emailExistsError: boolean = false;
  
  constructor(
    private employeeService: EmployeeService,
    private hobbyService: HobbyService,
    private departmentService: DepartmentService,
    private locationService: LocationService,
    private route: ActivatedRoute,
    private router: Router,
    private fb: FormBuilder
  ) {
    this.employeeForm = this.fb.group({
      firstName: ['', [Validators.required, Validators.minLength(2)]],
      lastName: ['', [Validators.required, Validators.minLength(2)]],
      emailId: ['', [Validators.required, Validators.email]],
      city: ['', [Validators.required, Validators.minLength(2)]],
      state: ['', [Validators.required, Validators.minLength(2)]],
      country: ['', [Validators.required, Validators.minLength(2)]],
      phone: ['', [Validators.required, Validators.pattern(/^\d{10}$/)]],
      dob: ['', [Validators.required, this.ageValidator.bind(this)]],
      dateOfJoining: ['', [Validators.required, this.dateOfJoiningValidator.bind(this)]],
      salary: ['', [Validators.required, Validators.min(0)]],
      rating: [1, [Validators.required, Validators.min(1), Validators.max(10)]],
      hobbySearch: [''],
      employeeHobbies: [[]],
      departmentSearch: [''],
      employeeDepartments: [[]]
    });
  }

  ngOnInit(): void {
    this.id = this.route.snapshot.params['id'];
    this.loadHobbies();
    this.loadDepartments();
    this.loadEmployee();
    this.setupLocationFilters();
    // Fetch all employees for email uniqueness check
    this.employeeService.getEmployeesList().subscribe({
      next: (employees) => { this.allEmployees = employees || []; },
      error: (err) => { console.error('Failed to fetch employees:', err); }
    });
  }

  // Custom validator for age (must be at least 18)
  ageValidator(control: any): {[key: string]: any} | null {
    if (!control.value) return null;
    
    const dob = new Date(control.value);
    const today = new Date();
    let age = today.getFullYear() - dob.getFullYear();
    const monthDiff = today.getMonth() - dob.getMonth();
    
    if (monthDiff < 0 || (monthDiff === 0 && today.getDate() < dob.getDate())) {
      age--;
    }
    
    return age >= 18 ? null : { 'underage': { value: age } };
  }

  // Custom validator for date of joining (must be after DOB and not in future)
  dateOfJoiningValidator(control: any): {[key: string]: any} | null {
    if (!control.value) return null;
    
    const dateOfJoining = new Date(control.value);
    const today = new Date();
    const dob = this.employeeForm?.get('dob')?.value ? new Date(this.employeeForm.get('dob')?.value) : null;
    
    if (dateOfJoining > today) {
      return { 'futureDate': { value: control.value } };
    }
    
    if (dob && dateOfJoining < dob) {
      return { 'beforeDob': { value: control.value } };
    }
    
    return null;
  }

  loadHobbies() {
    this.hobbyService.getHobbiesList().subscribe({
      next: (hobbies) => {
        this.allHobbies = hobbies;
        this.setupHobbyFilter();
      },
      error: (error) => {
        console.error('Error loading hobbies:', error);
      }
    });
  }

  setupHobbyFilter() {
    this.filteredHobbies = this.employeeForm.get('hobbySearch')?.valueChanges.pipe(
      startWith(''),
      map(value => this._filterHobbies(value || ''))
    ) || new Observable();
  }

  private _filterHobbies(value: string): Hobby[] {
    const filterValue = value.toLowerCase();
    return this.allHobbies.filter(hobby => 
      hobby.name.toLowerCase().includes(filterValue) &&
      !this.selectedHobbies.some(selected => selected.id === hobby.id)
    );
  }

  addHobby(hobby: Hobby) {
    if (!this.selectedHobbies.some(h => h.id === hobby.id)) {
      this.selectedHobbies.push(hobby);
      this.employeeForm.get('hobbySearch')?.setValue('');
    }
  }

  removeHobby(hobby: Hobby) {
    this.selectedHobbies = this.selectedHobbies.filter(h => h.id !== hobby.id);
  }

  loadDepartments() {
    this.departmentService.getDepartments().subscribe({
      next: (departments: Department[]) => {
        console.log('Departments loaded:', departments);
        this.allDepartments = departments;
        this.setupDepartmentFilter();
        
        // If employee is already loaded, try to merge departments
        if (this.id > 0) {
          // Get the current employee data and merge departments
          this.employeeService.getEmployeeById(this.id).subscribe({
            next: (employee) => {
              if (!this.selectedDepartments || this.selectedDepartments.length === 0) {
                this.mergeEmployeeDepartments(employee);
              }
            }
          });
        }
      },
      error: (error: any) => {
        console.error('Error loading departments:', error);
      }
    });
  }

  setupDepartmentFilter() {
    this.filteredDepartments = this.employeeForm.get('departmentSearch')?.valueChanges.pipe(
      startWith(''),
      map(value => this._filterDepartments(value || ''))
    ) || new Observable();
  }

  setupLocationFilters() {
    // Setup country autocomplete
    this.filteredCountries = this.employeeForm.get('country')?.valueChanges.pipe(
      startWith(''),
      debounceTime(300),
      distinctUntilChanged(),
      switchMap(value => this.locationService.searchCountries(value || ''))
    ) || new Observable();

    // Setup state autocomplete
    this.filteredStates = this.employeeForm.get('state')?.valueChanges.pipe(
      startWith(''),
      debounceTime(300),
      distinctUntilChanged(),
      switchMap(value => this.locationService.searchUSStates(value || ''))
    ) || new Observable();

    // Setup city autocomplete
    this.filteredCities = this.employeeForm.get('city')?.valueChanges.pipe(
      startWith(''),
      debounceTime(300),
      distinctUntilChanged(),
      switchMap(value => {
        const stateValue = this.employeeForm.get('state')?.value || '';
        return this.locationService.searchUSCities(value || '', stateValue);
      })
    ) || new Observable();
  }

  private _filterDepartments(value: string): Department[] {
    const filterValue = value.toLowerCase();
    return this.allDepartments.filter(department => 
      department.name.toLowerCase().includes(filterValue) &&
      !this.selectedDepartments.some(selected => selected.id === department.id)
    );
  }

  addDepartment(department: Department) {
    if (!this.selectedDepartments.some(d => d.id === department.id)) {
      this.selectedDepartments.push(department);
      this.employeeForm.get('departmentSearch')?.setValue('');
      console.log('Department added to selection:', department);
    }
  }

  removeDepartment(department: Department) {
    this.selectedDepartments = this.selectedDepartments.filter(d => d.id !== department.id);
    console.log('Department removed from selection:', department);
  }

  // Location selection methods
  selectCountry(country: Country) {
    this.employeeForm.get('country')?.setValue(country.name.common);
  }

  selectState(state: State) {
    this.employeeForm.get('state')?.setValue(state.name);
    // Refresh city suggestions based on selected state
    this.setupLocationFilters();
  }

  selectCity(city: City) {
    this.employeeForm.get('city')?.setValue(city.name);
  }

  // Display functions for autocomplete
  displayCountry(country: Country): string {
    return country ? country.name.common : '';
  }

  displayState(state: State): string {
    return state ? state.name : '';
  }

  displayCity(city: City): string {
    return city ? city.name : '';
  }

  loadEmployee() {
    this.loading = true;
    this.employeeService.getEmployeeById(this.id).subscribe({
      next: (data) => {
        console.log('Employee data loaded:', data);
        console.log('Employee departments from backend:', data.employeeDepartments);
        
        this.employeeForm.patchValue({
          firstName: data.firstName,
          lastName: data.lastName,
          emailId: data.emailId,
          city: data.city,
          state: data.state,
          country: data.country,
          phone: data.phone,
          dob: data.dob,
          dateOfJoining: data.dateOfJoining,
          salary: data.salary,
          rating: data.rating,
          employeeHobbies: data.employeeHobbies || [],
          employeeDepartments: data.employeeDepartments || []
        });
        
        // Load existing hobbies
        if (data.employeeHobbies && data.employeeHobbies.length > 0) {
          this.selectedHobbies = data.employeeHobbies.map((map: any) => map.hobby);
          this.originalHobbies = [...this.selectedHobbies];
          console.log('Loaded hobbies:', this.selectedHobbies);
        }

        // Load existing departments - try both approaches
        if (data.employeeDepartments && data.employeeDepartments.length > 0) {
          this.selectedDepartments = data.employeeDepartments.map((map: any) => map.department);
          this.originalDepartments = [...this.selectedDepartments];
          console.log('Loaded departments from employee data:', this.selectedDepartments);
        } else {
          // If no departments in employee data, try to find them from department list
          console.log('No departments in employee data, checking department list...');
          // Wait a bit for departments to load if they haven't loaded yet
          if (this.allDepartments.length === 0) {
            console.log('Departments not loaded yet, will retry in 1 second...');
            setTimeout(() => {
              this.mergeEmployeeDepartments(data);
            }, 1000);
          } else {
            this.mergeEmployeeDepartments(data);
          }
        }
        
        this.loading = false;
      },
      error: (error) => {
        console.error('Error loading employee:', error);
        this.loading = false;
        // You could add a snackbar notification here
      }
    });
  }

  // Merge departments for an employee (similar to employee list logic)
  private mergeEmployeeDepartments(employee: Employee) {
    if (this.allDepartments.length > 0) {
      // Find departments that contain this employee
      const employeeDepartments = this.allDepartments.filter(dept => 
        dept.employees && dept.employees.some(emp => emp.id === employee.id)
      );
      
      if (employeeDepartments.length > 0) {
        this.selectedDepartments = employeeDepartments;
        this.originalDepartments = [...employeeDepartments];
        console.log('Merged departments from department list:', this.selectedDepartments);
      } else {
        console.log('No departments found for employee in department list');
      }
    } else {
      console.log('Department list not loaded yet, will merge later');
    }
  }

  onSubmit() {
    if (this.employeeForm.valid) {
      const email = (this.employeeForm.value.emailId || '').trim().toLowerCase();
      const myId = Number(this.id);
      const emailExists = this.allEmployees.some(emp => {
        const empEmail = (emp.emailId || '').trim().toLowerCase();
        const empId = Number(emp.id);
        return empEmail === email && empId !== myId;
      });
      if (emailExists) {
        this.emailExistsError = true;
        this.employeeForm.get('emailId')?.setErrors({ emailExists: true });
        // Debug log
        console.warn('Email collision detected:', email, 'Current ID:', myId, 'All employees:', this.allEmployees.map(e => ({id: e.id, email: e.emailId})));
        return;
      } else {
        this.emailExistsError = false;
      }
      this.loading = true;
      
      const formValue = this.employeeForm.value;
      
      // Make sure location values are strings, not objects
      const cityValue = typeof formValue.city === 'object' ? formValue.city.name : formValue.city;
      const stateValue = typeof formValue.state === 'object' ? formValue.state.name : formValue.state;
      const countryValue = typeof formValue.country === 'object' ? formValue.country.name.common : formValue.country;
      
      const employee: Employee = {
        id: this.id,
        firstName: this.employeeForm.value.firstName,
        lastName: this.employeeForm.value.lastName,
        emailId: this.employeeForm.value.emailId,
        city: cityValue,
        state: stateValue,
        country: countryValue,
        phone: this.employeeForm.value.phone,
        dob: this.employeeForm.value.dob,
        dateOfJoining: this.employeeForm.value.dateOfJoining,
        salary: Number(this.employeeForm.value.salary),
        rating: Number(this.employeeForm.value.rating),
        employeeHobbies: this.employeeForm.value.employeeHobbies || [],
        employeeDepartments: this.employeeForm.value.employeeDepartments || []
      };
      
      this.employeeService.updateEmployee(this.id, employee).subscribe({
        next: (data) => {
          console.log('Employee updated successfully:', data);
          // Update hobbies
          this.updateEmployeeHobbies();
          // Update departments
          this.updateEmployeeDepartments();
        },
        error: (error) => {
          console.error('Error updating employee:', error);
          this.loading = false;
          // You could add a snackbar notification here
        }
      });
    } else {
      // Mark all fields as touched to show validation errors
      Object.keys(this.employeeForm.controls).forEach(key => {
        const control = this.employeeForm.get(key);
        control?.markAsTouched();
      });
    }
  }

  updateEmployeeHobbies() {
    // Find hobbies to add (in selected but not in original)
    const hobbiesToAdd = this.selectedHobbies.filter(hobby => 
      !this.originalHobbies.some(original => original.id === hobby.id)
    );

    // Find hobbies to remove (in original but not in selected)
    const hobbiesToRemove = this.originalHobbies.filter(hobby => 
      !this.selectedHobbies.some(selected => selected.id === hobby.id)
    );

    const addObservables = hobbiesToAdd.map(hobby => 
      this.employeeService.addHobbyToEmployee(this.id, hobby.id)
    );

    const removeObservables = hobbiesToRemove.map(hobby => 
      this.employeeService.removeHobbyFromEmployee(this.id, hobby.id)
    );

    const allObservables = [...addObservables, ...removeObservables];

    if (allObservables.length === 0) {
      this.loading = false;
      this.goToEmployeeList();
      return;
    }

    forkJoin(allObservables).subscribe({
      next: () => {
        console.log('Hobbies updated successfully');
        this.loading = false;
        this.goToEmployeeList();
      },
      error: (error) => {
        console.error('Error updating hobbies:', error);
        this.loading = false;
        this.goToEmployeeList();
      }
    });
  }

  updateEmployeeDepartments() {
    // Find departments to add (in selected but not in original)
    const departmentsToAdd = this.selectedDepartments.filter(department => 
      !this.originalDepartments.some(original => original.id === department.id)
    );

    // Find departments to remove (in original but not in selected)
    const departmentsToRemove = this.originalDepartments.filter(department => 
      !this.selectedDepartments.some(selected => selected.id === department.id)
    );

    console.log('Departments to add:', departmentsToAdd);
    console.log('Departments to remove:', departmentsToRemove);

    const addObservables = departmentsToAdd.map(department => 
      this.employeeService.addDepartmentToEmployee(this.id, department.id)
    );

    const removeObservables = departmentsToRemove.map(department => 
      this.employeeService.removeDepartmentFromEmployee(this.id, department.id)
    );

    const allObservables = [...addObservables, ...removeObservables];

    if (allObservables.length === 0) {
      this.loading = false;
      this.goToEmployeeList();
      return;
    }

    forkJoin(allObservables).subscribe({
      next: () => {
        console.log('Departments updated successfully');
        this.loading = false;
        this.goToEmployeeList();
      },
      error: (error) => {
        console.error('Error updating departments:', error);
        console.error('Error details:', error.error || error.message || error);
        alert('Error updating departments: ' + (error.error?.message || error.message || 'Unknown error'));
        this.loading = false;
        this.goToEmployeeList();
      }
    });
  }

  goToEmployeeList() {
    this.router.navigate(['/employees']);
  }

  // Getter methods for easy template access
  get firstName() { return this.employeeForm.get('firstName'); }
  get lastName() { return this.employeeForm.get('lastName'); }
  get emailId() { return this.employeeForm.get('emailId'); }
  get city() { return this.employeeForm.get('city'); }
  get state() { return this.employeeForm.get('state'); }
  get country() { return this.employeeForm.get('country'); }
  get phone() { return this.employeeForm.get('phone'); }
  get dob() { return this.employeeForm.get('dob'); }
  get dateOfJoining() { return this.employeeForm.get('dateOfJoining'); }
  get salary() { return this.employeeForm.get('salary'); }
  get rating() { return this.employeeForm.get('rating'); }
  get hobbySearch() { return this.employeeForm.get('hobbySearch'); }
  get departmentSearch() { return this.employeeForm.get('departmentSearch'); }
}