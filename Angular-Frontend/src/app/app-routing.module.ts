import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { CreateEmployeeComponent } from './employee/create-employee/create-employee.component';
import { EmployeeListComponent } from './employee/employee-list/employee-list.component';
import { UpdateEmployeeComponent } from './employee/update-employee/update-employee.component';
import { AnalyticsComponent } from './analytics/analytics.component';
import { SettingsComponent } from './settings/settings.component';
import { HobbyCrudComponent } from './hobby/hobby-crud/hobby-crud.component';
import { DepartmentCrudComponent } from './department/department-crud/department-crud.component';

const routes: Routes = [
    {path: 'employees', component: EmployeeListComponent},
    {path: 'create-employee', component: CreateEmployeeComponent},
    {path: 'analytics', component: AnalyticsComponent},
    {path: 'settings', component: SettingsComponent},
    {path: 'hobbies', component: HobbyCrudComponent},
    {path: 'departments', component: DepartmentCrudComponent},
    {path: '', redirectTo: 'employees', pathMatch: 'full'},
    {path: 'update-employee/:id', component: UpdateEmployeeComponent}
  ];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
