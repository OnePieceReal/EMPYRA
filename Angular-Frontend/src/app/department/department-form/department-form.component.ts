import { Component, Inject } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { Department } from '../../department';
import { CommonModule } from '@angular/common';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { ReactiveFormsModule } from '@angular/forms';
import { DepartmentService } from '../../department.service';
import { OnInit } from '@angular/core';

@Component({
  selector: 'app-department-form',
  standalone: true,
  imports: [
    CommonModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    ReactiveFormsModule
  ],
  templateUrl: './department-form.component.html',
  styleUrls: ['./department-form.component.css']
})
export class DepartmentFormComponent implements OnInit {
  departmentForm: FormGroup;
  isEditMode: boolean = false;
  dialogTitle: string = '';
  allDepartments: Department[] = [];
  nameExistsError: boolean = false;

  constructor(
    private fb: FormBuilder,
    private dialogRef: MatDialogRef<DepartmentFormComponent>,
    @Inject(MAT_DIALOG_DATA) public data: { department: Department | null, isEditMode: boolean },
    private departmentService: DepartmentService
  ) {
    this.isEditMode = data.isEditMode;
    this.dialogTitle = this.isEditMode ? 'Edit Department' : 'Add Department';
    this.departmentForm = this.fb.group({
      name: [data.department?.name || '', [Validators.required, Validators.minLength(2), Validators.maxLength(50)]],
      description: [data.department?.description || '', [Validators.required, Validators.minLength(5), Validators.maxLength(200)]]
    });
  }

  ngOnInit(): void {
    // Fetch all departments for name uniqueness check
    this.departmentService.getDepartments().subscribe({
      next: (departments) => { this.allDepartments = departments || []; },
      error: (err) => { console.error('Failed to fetch departments:', err); }
    });
  }

  onCancel() {
    this.dialogRef.close();
  }

  onSubmit() {
    if (this.departmentForm.valid) {
      const name = (this.departmentForm.value.name || '').trim().toLowerCase();
      const myId = this.data.department?.id || 0;
      const nameExists = this.allDepartments.some(dept => dept.name.trim().toLowerCase() === name && dept.id !== myId);
      if (nameExists) {
        this.nameExistsError = true;
        this.departmentForm.get('name')?.setErrors({ nameExists: true });
        return;
      } else {
        this.nameExistsError = false;
      }
      const department: Department = {
        id: this.data.department?.id || 0,
        name: this.departmentForm.value.name,
        description: this.departmentForm.value.description,
        employees: this.data.department?.employees || []
      };
      this.dialogRef.close(department);
    }
  }
} 