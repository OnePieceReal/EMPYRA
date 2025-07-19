import { Component, OnInit, Inject } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { Hobby } from '../../hobby';
import { HobbyService } from '../../hobby.service';

@Component({
  selector: 'app-hobby-form',
  templateUrl: './hobby-form.component.html',
  styleUrls: ['./hobby-form.component.css']
})
export class HobbyFormComponent implements OnInit {
  hobbyForm: FormGroup;
  loading: boolean = false;
  isEditMode: boolean = false;
  dialogTitle: string = '';
  allHobbies: Hobby[] = [];
  nameExistsError: boolean = false;
  
  constructor(
    private hobbyService: HobbyService,
    private fb: FormBuilder,
    private dialogRef: MatDialogRef<HobbyFormComponent>,
    @Inject(MAT_DIALOG_DATA) public data: { hobby: Hobby | null, isEditMode: boolean }
  ) {
    this.isEditMode = data.isEditMode;
    this.dialogTitle = this.isEditMode ? 'Update Hobby' : 'Add New Hobby';
    
    this.hobbyForm = this.fb.group({
      name: ['', [Validators.required, Validators.minLength(2), Validators.maxLength(50)]],
      description: ['', [Validators.required, Validators.minLength(10), Validators.maxLength(500)]]
    });
  }
  
  ngOnInit(): void {
    if (this.data.hobby && this.isEditMode) {
      this.hobbyForm.patchValue({
        name: this.data.hobby.name,
        description: this.data.hobby.description
      });
    }
    // Fetch all hobbies for name uniqueness check
    this.hobbyService.getHobbiesList().subscribe({
      next: (hobbies) => { this.allHobbies = hobbies || []; },
      error: (err) => { console.error('Failed to fetch hobbies:', err); }
    });
  }
  
  onSubmit(): void {
    if (this.hobbyForm.valid) {
      const name = (this.hobbyForm.value.name || '').trim().toLowerCase();
      const myId = this.data.hobby?.id || 0;
      const nameExists = this.allHobbies.some(hobby => hobby.name.trim().toLowerCase() === name && hobby.id !== myId);
      if (nameExists) {
        this.nameExistsError = true;
        this.hobbyForm.get('name')?.setErrors({ nameExists: true });
        return;
      } else {
        this.nameExistsError = false;
      }
      this.loading = true;
      const hobbyData: Hobby = {
        id: this.data.hobby?.id || 0,
        name: this.hobbyForm.value.name,
        description: this.hobbyForm.value.description
      };
      
      if (this.isEditMode && this.data.hobby) {
        this.hobbyService.updateHobby(this.data.hobby.id, hobbyData).subscribe({
          next: (updatedHobby) => {
            console.log('Hobby updated successfully:', updatedHobby);
            this.loading = false;
            this.dialogRef.close(true);
          },
          error: (error) => {
            console.error('Error updating hobby:', error);
            this.loading = false;
          }
        });
      } else {
        this.hobbyService.createHobby(hobbyData).subscribe({
          next: (newHobby) => {
            console.log('Hobby created successfully:', newHobby);
            this.loading = false;
            this.dialogRef.close(true);
          },
          error: (error) => {
            console.error('Error creating hobby:', error);
            this.loading = false;
          }
        });
      }
    } else {
      // Mark all fields as touched to show validation errors
      Object.keys(this.hobbyForm.controls).forEach(key => {
        const control = this.hobbyForm.get(key);
        control?.markAsTouched();
      });
    }
  }
  
  onCancel(): void {
    this.dialogRef.close(false);
  }
  
  // Getter methods for easy template access
  get name() { return this.hobbyForm.get('name'); }
  get description() { return this.hobbyForm.get('description'); }
} 