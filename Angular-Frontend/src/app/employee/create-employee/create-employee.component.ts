import { Component, OnInit } from '@angular/core';
import { Employee } from '../../employee';
import { Hobby } from '../../hobby';
import { Department } from '../../department';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { EmployeeService } from '../../employee.service';
import { HobbyService } from '../../hobby.service';
import { DepartmentService } from '../../department.service';
import { LocationService, Country, State, City } from '../../services/location.service';
import { Router } from '@angular/router';
import { trigger, transition, style, animate } from '@angular/animations';
import { Observable, map, startWith, forkJoin, debounceTime, distinctUntilChanged, switchMap } from 'rxjs';

@Component({
  selector: 'app-create-employee',
  templateUrl: './create-employee.component.html',
  styleUrls: ['./create-employee.component.css'],
  animations: [
    trigger('fadeIn', [
      transition(':enter', [
        style({ opacity: 0, transform: 'translateY(20px)' }),
        animate('400ms ease-out', style({ opacity: 1, transform: 'translateY(0)' }))
      ])
    ])
  ]
})
export class CreateEmployeeComponent implements OnInit {
  employeeForm: FormGroup;
  loading: boolean = false;
  allHobbies: Hobby[] = [];
  selectedHobbies: Hobby[] = [];
  filteredHobbies: Observable<Hobby[]> = new Observable();
  allDepartments: Department[] = [];
  selectedDepartments: Department[] = [];
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
    this.loadDepartments();
    this.loadHobbies();
    this.setupDepartmentFilter();
    this.setupHobbyFilter();
    this.locationService.loadAllCountries().subscribe(); // Preload all countries
    this.setupLocationFilters();
    // Fetch all employees for email uniqueness check
    this.employeeService.getEmployeesList().subscribe({
      next: (employees) => { this.allEmployees = employees || []; },
      error: (err) => { console.error('Failed to fetch employees:', err); }
    });
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

  loadDepartments() {
    this.departmentService.getDepartments().subscribe({
      next: (departments) => {
        console.log('Create employee - departments loaded:', departments);
        this.allDepartments = departments;
        this.setupDepartmentFilter();
      },
      error: (error) => {
        console.error('Error loading departments:', error);
      }
    });
  }

  setupHobbyFilter() {
    this.filteredHobbies = this.employeeForm.get('hobbySearch')?.valueChanges.pipe(
      startWith(''),
      map(value => this._filterHobbies(value || ''))
    ) || new Observable();
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
      switchMap(value => this.locationService.searchCountriesLocal(value || ''))
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

  private _filterHobbies(value: string): Hobby[] {
    const filterValue = value.toLowerCase();
    return this.allHobbies.filter(hobby => 
      hobby.name.toLowerCase().includes(filterValue) &&
      !this.selectedHobbies.some(selected => selected.id === hobby.id)
    );
  }

  private _filterDepartments(value: string): Department[] {
    const filterValue = value.toLowerCase();
    return this.allDepartments.filter(department => 
      department.name.toLowerCase().includes(filterValue) &&
      !this.selectedDepartments.some(selected => selected.id === department.id)
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
  
  saveEmployee() {
    if (this.employeeForm.valid) {
      const email = this.employeeForm.value.emailId.trim().toLowerCase();
      const emailExists = this.allEmployees.some(emp => emp.emailId.trim().toLowerCase() === email);
      if (emailExists) {
        this.emailExistsError = true;
        this.employeeForm.get('emailId')?.setErrors({ emailExists: true });
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
        ...this.employeeForm.value,
        id: 0, // Backend will set this
        city: cityValue,
        state: stateValue,
        country: countryValue,
        phone: this.employeeForm.value.phone, // Keep as string
        salary: Number(this.employeeForm.value.salary), // Convert to number
        rating: Number(this.employeeForm.value.rating) // Convert to number
      };
      
      this.employeeService.createEmployee(employee).subscribe({
        next: (data: any) => {
          console.log('Employee created successfully:', data);
          // Add hobbies and departments to the employee
          this.addHobbiesToEmployee(data.id);
        },
        error: (error) => {
          console.error('Error creating employee:', error);
          this.loading = false;
          // You could add a snackbar notification here
        }
      });
    }
  }

  addHobbiesToEmployee(employeeId: number) {
    if (this.selectedHobbies.length === 0 && this.selectedDepartments.length === 0) {
      this.loading = false;
      this.goToEmployeeList();
      return;
    }

    const observables: Observable<any>[] = [];

    // Add hobbies
    if (this.selectedHobbies.length > 0) {
      const hobbyObservables = this.selectedHobbies.map(hobby => 
        this.employeeService.addHobbyToEmployee(employeeId, hobby.id)
      );
      observables.push(...hobbyObservables);
    }

    // Add departments
    if (this.selectedDepartments.length > 0) {
      console.log('Adding departments to employee:', this.selectedDepartments);
      const departmentObservables = this.selectedDepartments.map(department => 
        this.employeeService.addDepartmentToEmployee(employeeId, department.id)
      );
      observables.push(...departmentObservables);
    }

    // Use forkJoin for modern RxJS approach
    forkJoin(observables).subscribe({
      next: () => {
        console.log('All hobbies and departments added successfully');
        this.loading = false;
        this.goToEmployeeList();
      },
      error: (error) => {
        console.error('Error adding hobbies/departments:', error);
        console.error('Error details:', error.error || error.message || error);
        alert('Error adding hobbies/departments: ' + (error.error?.message || error.message || 'Unknown error'));
        this.loading = false;
        this.goToEmployeeList();
      }
    });
  }

  goToEmployeeList() {
    this.router.navigate(['/employees']);
  }
  
  onSubmit() {
    if (this.employeeForm.valid) {
      this.saveEmployee();
    } else {
      // Mark all fields as touched to show validation errors
      Object.keys(this.employeeForm.controls).forEach(key => {
        const control = this.employeeForm.get(key);
        control?.markAsTouched();
      });
    }
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
