import { Component, OnInit, OnDestroy } from '@angular/core';
import { Employee } from '../employee';
import { EmployeeService } from '../employee.service';
import { DepartmentService } from '../department.service';
import { Department } from '../department';
import { AiInsightsService, InsightResponse } from '../services/ai-insights.service';
import { Chart, registerables } from 'chart.js';
import { trigger, transition, style, animate } from '@angular/animations';

// Register Chart.js components
Chart.register(...registerables);

@Component({
  selector: 'app-analytics',
  templateUrl: './analytics.component.html',
  styleUrls: ['./analytics.component.css'],
  animations: [
    trigger('fadeIn', [
      transition(':enter', [
        style({ opacity: 0, transform: 'translateY(20px)' }),
        animate('400ms ease-out', style({ opacity: 1, transform: 'translateY(0)' }))
      ])
    ]),
    trigger('slideInUp', [
      transition(':enter', [
        style({ opacity: 0, transform: 'translateY(30px)' }),
        animate('500ms ease-out', style({ opacity: 1, transform: 'translateY(0)' }))
      ])
    ])
  ]
})
export class AnalyticsComponent implements OnInit, OnDestroy {
  employees: Employee[] = [];
  departments: Department[] = [];
  loading: boolean = true;
  
  // Chart instances
  joinDateChart: any = null;
  ageByDepartmentChart: any = null;
  ratingByDepartmentChart: any = null;
  salaryByDepartmentChart: any = null;
  cityByDepartmentChart: any = null;
  countryByDepartmentChart: any = null;
  stateByDepartmentChart: any = null;

  // Summary metrics
  averageSalary: number = 0;
  averageRating: number = 0;
  averageAge: number = 0;
  mostPopularHobby: string = '';
  mostPopularDepartment: string = '';
  mostPopularState: string = '';
  mostPopularCity: string = '';
  mostPopularCountry: string = '';

  // AI Insights
  aiInsights: InsightResponse | null = null;
  generatingInsights: boolean = false;

  constructor(
    private employeeService: EmployeeService,
    private departmentService: DepartmentService,
    private aiInsightsService: AiInsightsService
  ) {}

  ngOnInit(): void {
    this.loadData();
    // Remove auto-generation of AI insights
    // Do not call this.generateAiInsights() here
  }

  ngOnDestroy(): void {
    // Clean up charts to prevent memory leaks
    this.destroyCharts();
  }

  destroyCharts(): void {
    const charts = [
      this.joinDateChart, this.ageByDepartmentChart, this.ratingByDepartmentChart,
      this.salaryByDepartmentChart, this.cityByDepartmentChart, this.countryByDepartmentChart,
      this.stateByDepartmentChart
    ];
    
    charts.forEach(chart => {
      if (chart) chart.destroy();
    });
  }

  loadData(): void {
    this.loading = true;
    
    // Load employees and departments in parallel
    Promise.all([
      this.employeeService.getEmployeesList().toPromise(),
      this.departmentService.getDepartments().toPromise()
    ]).then(([employees, departments]) => {
      
      this.employees = employees || [];
      this.departments = departments || [];
      
      // Merge department data with employees
      this.mergeEmployeeDepartments();
      
      // Calculate summary metrics
      this.calculateSummaryMetrics();
      
      // Do not auto-generate AI insights here
      this.loading = false;
      
      // Make sure DOM is ready before creating charts
      setTimeout(() => {
        this.createCharts();
      }, 100);
    }).catch(error => {
      console.error('Error loading data:', error);
      this.loading = false;
    });
  }

  mergeEmployeeDepartments(): void {
    if (this.employees.length > 0 && this.departments.length > 0) {
      this.employees.forEach(employee => {
        const employeeDepartments = this.departments.filter(dept => 
          dept.employees && dept.employees.some(emp => emp.id === employee.id)
        );
        
        employee.employeeDepartments = employeeDepartments.map(dept => ({
          id: 0,
          employeeId: employee.id,
          departmentId: dept.id,
          department: dept
        }));
      });
    }
  }

  calculateSummaryMetrics(): void {
    if (this.employees.length === 0) return;

    // Calculate averages
    this.averageSalary = Math.round(this.employees.reduce((sum, emp) => sum + emp.salary, 0) / this.employees.length);
    this.averageRating = this.employees.reduce((sum, emp) => sum + emp.rating, 0) / this.employees.length;
    this.averageAge = Math.round(this.employees.reduce((sum, emp) => sum + this.getAge(emp.dob), 0) / this.employees.length);

    // Find most popular items
    this.mostPopularHobby = this.getMostPopularHobby();
    this.mostPopularDepartment = this.getMostPopularDepartment();
    this.mostPopularState = this.getMostPopularState();
    this.mostPopularCity = this.getMostPopularCity();
    this.mostPopularCountry = this.getMostPopularCountry();
  }

  createCharts(): void {
    
    this.createJoinDateChart();
    
    this.createAgeByDepartmentChart();
    
    this.createRatingByDepartmentChart();
    
    this.createSalaryByDepartmentChart();
    
    this.createCityByDepartmentChart();
    
    this.createCountryByDepartmentChart();
    
    this.createStateByDepartmentChart();
    
  }

  createJoinDateChart(): void {
    const data = this.getJoinDateData();
    
    const ctx = document.getElementById('joinDateChart') as HTMLCanvasElement;
    if (ctx) {
      this.joinDateChart = new Chart(ctx, {
        type: 'line',
        data: {
          labels: data.labels,
          datasets: [{
            label: 'Total Employees',
            data: data.data,
            borderColor: 'rgba(102, 126, 234, 1)',
            backgroundColor: 'rgba(102, 126, 234, 0.1)',
            borderWidth: 3,
            fill: true,
            tension: 0.4,
            pointBackgroundColor: 'rgba(102, 126, 234, 1)',
            pointBorderColor: '#fff',
            pointBorderWidth: 2,
            pointRadius: 6
          }]
        },
        options: {
          responsive: true,
          maintainAspectRatio: false,
          plugins: {
            legend: {
              display: false
            },
            title: {
              display: true,
              text: 'Employee Growth Over Time',
              font: { size: 16, weight: 'bold' }
              }
          },
          scales: {
            y: {
              beginAtZero: true,
              ticks: { stepSize: 1 }
            }
          }
        }
      });
    } else {
      console.error('Join date chart canvas not found!');
    }
  }

  createAgeByDepartmentChart(): void {
    const data = this.getAgeByDepartmentData();
    
    const ctx = document.getElementById('ageByDepartmentChart') as HTMLCanvasElement;
    if (ctx) {
      this.ageByDepartmentChart = new Chart(ctx, {
        type: 'bar',
        data: {
          labels: data.labels,
          datasets: [{
            label: 'Average Age',
            data: data.data,
            backgroundColor: 'rgba(255, 99, 132, 0.8)',
            borderColor: 'rgba(255, 99, 132, 1)',
            borderWidth: 2,
            borderRadius: 8
          }]
        },
        options: {
          responsive: true,
          maintainAspectRatio: false,
          plugins: {
            legend: { display: false },
            title: {
              display: true,
              text: 'Average Age by Department',
              font: { size: 16, weight: 'bold' }
              }
          },
          scales: {
            y: {
              beginAtZero: true,
              title: { display: true, text: 'Age' }
            }
          }
        }
      });
    }
  }

  createRatingByDepartmentChart(): void {
    const data = this.getRatingByDepartmentData();
    
    const ctx = document.getElementById('ratingByDepartmentChart') as HTMLCanvasElement;
    if (ctx) {
      this.ratingByDepartmentChart = new Chart(ctx, {
        type: 'bar',
        data: {
          labels: data.labels,
          datasets: [{
            label: 'Average Rating',
            data: data.data,
            backgroundColor: 'rgba(54, 162, 235, 0.8)',
            borderColor: 'rgba(54, 162, 235, 1)',
            borderWidth: 2,
            borderRadius: 8
          }]
        },
        options: {
          responsive: true,
          maintainAspectRatio: false,
          plugins: {
            legend: { display: false },
            title: {
              display: true,
              text: 'Average Rating by Department',
              font: { size: 16, weight: 'bold' }
            }
          },
          scales: {
            y: {
              beginAtZero: true,
              max: 10,
              title: { display: true, text: 'Rating (1-10)' }
            }
          }
        }
      });
    }
  }

  createSalaryByDepartmentChart(): void {
    const data = this.getSalaryByDepartmentData();
    
    const ctx = document.getElementById('salaryByDepartmentChart') as HTMLCanvasElement;
    if (ctx) {
      this.salaryByDepartmentChart = new Chart(ctx, {
        type: 'bar',
        data: {
          labels: data.labels,
          datasets: [{
            label: 'Average Salary',
            data: data.data,
            backgroundColor: 'rgba(75, 192, 192, 0.8)',
            borderColor: 'rgba(75, 192, 192, 1)',
            borderWidth: 2,
            borderRadius: 8
          }]
        },
        options: {
          responsive: true,
          maintainAspectRatio: false,
          plugins: {
            legend: { display: false },
            title: {
              display: true,
              text: 'Average Salary by Department',
              font: { size: 16, weight: 'bold' }
            }
          },
          scales: {
            y: {
              beginAtZero: true,
              title: { display: true, text: 'Salary ($)' }
            }
          }
        }
      });
    }
  }

  createCityByDepartmentChart(): void {
    const data = this.getCityByDepartmentData();
    
    const ctx = document.getElementById('cityByDepartmentChart') as HTMLCanvasElement;
    if (ctx) {
      this.cityByDepartmentChart = new Chart(ctx, {
        type: 'pie',
        data: {
          labels: data.labels,
          datasets: [{
            data: data.data,
            backgroundColor: [
              '#667eea', '#764ba2', '#f093fb', '#f5576c',
              '#4facfe', '#00f2fe', '#43e97b', '#38f9d7',
              '#fa709a', '#fee140', '#a8edea', '#fed6e3'
            ],
            borderWidth: 2,
            borderColor: '#fff'
          }]
        },
        options: {
          responsive: true,
          maintainAspectRatio: false,
          plugins: {
            legend: {
              position: 'bottom',
              labels: { padding: 20, usePointStyle: true }
            },
            title: {
              display: true,
              text: 'Employee Distribution by City',
              font: { size: 16, weight: 'bold' }
            }
          }
        }
      });
    }
  }

  createCountryByDepartmentChart(): void {
    const data = this.getCountryByDepartmentData();
    
    const ctx = document.getElementById('countryByDepartmentChart') as HTMLCanvasElement;
    if (ctx) {
      this.countryByDepartmentChart = new Chart(ctx, {
        type: 'pie',
        data: {
          labels: data.labels,
          datasets: [{
            data: data.data,
            backgroundColor: [
              '#FF6384', '#36A2EB', '#FFCE56', '#4BC0C0',
              '#9966FF', '#FF9F40', '#FF6384', '#C9CBCF'
            ],
            borderWidth: 2,
            borderColor: '#fff'
          }]
        },
        options: {
          responsive: true,
          maintainAspectRatio: false,
          plugins: {
            legend: {
              position: 'bottom',
              labels: { padding: 20, usePointStyle: true }
            },
            title: {
              display: true,
              text: 'Employee Distribution by Country',
              font: { size: 16, weight: 'bold' }
            }
          }
        }
      });
    }
  }

  createStateByDepartmentChart(): void {
    const data = this.getStateByDepartmentData();
    
    const ctx = document.getElementById('stateByDepartmentChart') as HTMLCanvasElement;
    if (ctx) {
      this.stateByDepartmentChart = new Chart(ctx, {
        type: 'pie',
        data: {
          labels: data.labels,
          datasets: [{
            data: data.data,
            backgroundColor: [
              '#FF6384', '#36A2EB', '#FFCE56', '#4BC0C0',
              '#9966FF', '#FF9F40', '#FF6384', '#C9CBCF',
              '#FF6384', '#36A2EB', '#FFCE56', '#4BC0C0'
            ],
            borderWidth: 2,
            borderColor: '#fff'
          }]
        },
        options: {
          responsive: true,
          maintainAspectRatio: false,
          plugins: {
            legend: {
              position: 'bottom',
              labels: { padding: 20, usePointStyle: true }
            },
            title: {
              display: true,
              text: 'Employee Distribution by State',
              font: { size: 16, weight: 'bold' }
            }
          }
        }
      });
    }
  }

  // Data preparation methods
  getJoinDateData(): { labels: string[], data: number[] } {
    const joinDates = this.employees
      .map(emp => new Date(emp.dateOfJoining))
      .sort((a, b) => a.getTime() - b.getTime());

    const monthlyData: { [key: string]: number } = {};
    let cumulative = 0;

    joinDates.forEach(date => {
      const monthYear = `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}`;
      cumulative++;
      monthlyData[monthYear] = cumulative;
    });

    // Always return new arrays
    return {
      labels: [...Object.keys(monthlyData)],
      data: [...Object.values(monthlyData)]
    };
  }

  getAgeByDepartmentData(): { labels: string[], data: number[] } {
    const deptData: { [key: string]: number[] } = {};
    
    this.employees.forEach(emp => {
      const deptNames = emp.employeeDepartments?.map(d => d.department.name) || ['No Department'];
      deptNames.forEach(deptName => {
        if (!deptData[deptName]) deptData[deptName] = [];
        deptData[deptName].push(this.getAge(emp.dob));
      });
    });

    return {
      labels: [...Object.keys(deptData)],
      data: Object.values(deptData).map(ages => 
        ages.reduce((sum, age) => sum + age, 0) / ages.length
      )
    };
  }

  getRatingByDepartmentData(): { labels: string[], data: number[] } {
    const deptData: { [key: string]: number[] } = {};

    this.employees.forEach(emp => {
      const deptNames = emp.employeeDepartments?.map(d => d.department.name) || ['No Department'];
      deptNames.forEach(deptName => {
        if (!deptData[deptName]) deptData[deptName] = [];
        deptData[deptName].push(emp.rating);
      });
    });

    return {
      labels: [...Object.keys(deptData)],
      data: Object.values(deptData).map(ratings => 
        ratings.reduce((sum, rating) => sum + rating, 0) / ratings.length
      )
    };
  }

  getSalaryByDepartmentData(): { labels: string[], data: number[] } {
    const deptData: { [key: string]: number[] } = {};
    
    this.employees.forEach(emp => {
      const deptNames = emp.employeeDepartments?.map(d => d.department.name) || ['No Department'];
      deptNames.forEach(deptName => {
        if (!deptData[deptName]) deptData[deptName] = [];
        deptData[deptName].push(emp.salary);
      });
    });

    return {
      labels: [...Object.keys(deptData)],
      data: Object.values(deptData).map(salaries => 
        salaries.reduce((sum, salary) => sum + salary, 0) / salaries.length
      )
    };
  }

  getCityByDepartmentData(): { labels: string[], data: number[] } {
    const cityCount: { [key: string]: number } = {};
    
    this.employees.forEach(emp => {
      cityCount[emp.city] = (cityCount[emp.city] || 0) + 1;
    });

    const sorted = Object.entries(cityCount)
      .sort(([,a], [,b]) => b - a)
      .slice(0, 8);

    return {
      labels: sorted.map(([city]) => city).slice(),
      data: sorted.map(([, count]) => count).slice()
    };
  }

  getCountryByDepartmentData(): { labels: string[], data: number[] } {
    const countryCount: { [key: string]: number } = {};
    
    this.employees.forEach(emp => {
      countryCount[emp.country] = (countryCount[emp.country] || 0) + 1;
    });

    const sorted = Object.entries(countryCount)
      .sort(([,a], [,b]) => b - a);

    return {
      labels: sorted.map(([country]) => country).slice(),
      data: sorted.map(([, count]) => count).slice()
    };
  }

  getStateByDepartmentData(): { labels: string[], data: number[] } {
    const stateCount: { [key: string]: number } = {};

    this.employees.forEach(emp => {
      stateCount[emp.state] = (stateCount[emp.state] || 0) + 1;
    });

    const sorted = Object.entries(stateCount)
      .sort(([,a], [,b]) => b - a)
      .slice(0, 8);

    return {
      labels: sorted.map(([state]) => state).slice(),
      data: sorted.map(([, count]) => count).slice()
    };
  }

  // Most popular calculations
  getMostPopularHobby(): string {
    const hobbyCount: { [key: string]: number } = {};
    
    this.employees.forEach(emp => {
      emp.employeeHobbies?.forEach(hobbyMap => {
        hobbyCount[hobbyMap.hobby.name] = (hobbyCount[hobbyMap.hobby.name] || 0) + 1;
      });
    });

    return Object.entries(hobbyCount)
      .sort(([,a], [,b]) => b - a)[0]?.[0] || 'None';
  }

  getMostPopularDepartment(): string {
    const deptCount: { [key: string]: number } = {};
    
    this.employees.forEach(emp => {
      emp.employeeDepartments?.forEach(deptMap => {
        deptCount[deptMap.department.name] = (deptCount[deptMap.department.name] || 0) + 1;
      });
    });

    return Object.entries(deptCount)
      .sort(([,a], [,b]) => b - a)[0]?.[0] || 'None';
  }

  getMostPopularState(): string {
    const stateCount: { [key: string]: number } = {};
    
    this.employees.forEach(emp => {
      stateCount[emp.state] = (stateCount[emp.state] || 0) + 1;
    });

    return Object.entries(stateCount)
      .sort(([,a], [,b]) => b - a)[0]?.[0] || 'None';
  }

  getMostPopularCity(): string {
    const cityCount: { [key: string]: number } = {};
    
    this.employees.forEach(emp => {
      cityCount[emp.city] = (cityCount[emp.city] || 0) + 1;
    });

    return Object.entries(cityCount)
      .sort(([,a], [,b]) => b - a)[0]?.[0] || 'None';
  }

  getMostPopularCountry(): string {
    const countryCount: { [key: string]: number } = {};
    
    this.employees.forEach(emp => {
      countryCount[emp.country] = (countryCount[emp.country] || 0) + 1;
    });

    return Object.entries(countryCount)
      .sort(([,a], [,b]) => b - a)[0]?.[0] || 'None';
  }

  // Utility methods
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

  formatSalary(salary: number): string {
    return new Intl.NumberFormat('en-US', {
      style: 'currency',
      currency: 'USD'
    }).format(salary);
  }

  formatAverageSalary(salary: number): string {
    return new Intl.NumberFormat('en-US', {
      style: 'currency',
      currency: 'USD',
      minimumFractionDigits: 0,
      maximumFractionDigits: 0
    }).format(salary);
  }

  getTotalEmployees(): number {
    return this.employees.length;
  }

  generateAiInsights(): void {
    if (this.employees.length === 0) return;
    this.generatingInsights = true;
    this.aiInsights = null; // Clear previous insights
    const insightData = {
      employees: this.employees,
      departments: this.departments,
      summaryMetrics: {
        totalEmployees: this.getTotalEmployees(),
        averageSalary: this.averageSalary,
        averageRating: this.averageRating,
        averageAge: this.averageAge,
        mostPopularHobby: this.mostPopularHobby,
        mostPopularDepartment: this.mostPopularDepartment,
        mostPopularState: this.mostPopularState,
        mostPopularCity: this.mostPopularCity,
        mostPopularCountry: this.mostPopularCountry
      }
    };
    this.aiInsightsService.generateInsights(insightData).subscribe({
      next: (insights) => {
        this.aiInsights = insights;
        this.generatingInsights = false;
        console.log('AI Insights generated:', insights);
      },
      error: (error) => {
        console.error('Error generating AI insights:', error);
        this.generatingInsights = false;
        // Do not show fallback insights
        this.aiInsights = null;
      }
    });
  }
} 