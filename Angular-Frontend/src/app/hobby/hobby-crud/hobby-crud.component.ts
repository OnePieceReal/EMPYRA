import { Component, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { trigger, transition, style, animate } from '@angular/animations';
import { Hobby } from '../../hobby';
import { HobbyService } from '../../hobby.service';
import { HobbyFormComponent } from '../hobby-form/hobby-form.component';

@Component({
  selector: 'app-hobby-crud',
  templateUrl: './hobby-crud.component.html',
  styleUrls: ['./hobby-crud.component.css'],
  animations: [
    trigger('fabAnimation', [
      transition('normal => hover', [
        animate('200ms ease-out', style({ transform: 'scale(1.1)' }))
      ]),
      transition('hover => normal', [
        animate('200ms ease-out', style({ transform: 'scale(1)' }))
      ])
    ])
  ]
})
export class HobbyCrudComponent implements OnInit {
  hobbies: Hobby[] = [];
  loading: boolean = false;
  searchTerm: string = '';
  filteredHobbies: Hobby[] = [];
  fabState: string = 'normal';
  
  constructor(
    private hobbyService: HobbyService,
    private dialog: MatDialog
  ) {}
  
  ngOnInit(): void {
    this.loadHobbies();
  }
  
  loadHobbies(): void {
    this.loading = true;
    this.hobbyService.getHobbiesList().subscribe({
      next: (data) => {
        this.hobbies = data;
        this.filteredHobbies = data;
        this.loading = false;
      },
      error: (error) => {
        console.error('Error fetching hobbies:', error);
        this.loading = false;
      }
    });
  }
  
  // Search functionality
  onSearchChange(): void {
    if (!this.searchTerm.trim()) {
      this.filteredHobbies = this.hobbies;
    } else {
      const searchLower = this.searchTerm.toLowerCase();
      this.filteredHobbies = this.hobbies.filter(hobby =>
        hobby.name.toLowerCase().includes(searchLower) ||
        hobby.description.toLowerCase().includes(searchLower) ||
        hobby.id.toString().includes(searchLower)
      );
    }
  }
  
  // Add new hobby
  addHobby(): void {
    const dialogRef = this.dialog.open(HobbyFormComponent, {
      width: '700px',
      maxWidth: '90vw',
      maxHeight: '90vh',
      data: { hobby: null, isEditMode: false }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.loadHobbies();
      }
    });
  }
  
  // Update hobby
  updateHobby(hobby: Hobby): void {
    const dialogRef = this.dialog.open(HobbyFormComponent, {
      width: '700px',
      maxWidth: '90vw',
      maxHeight: '90vh',
      data: { hobby: hobby, isEditMode: true }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.loadHobbies();
      }
    });
  }
  
  // Delete hobby
  deleteHobby(id: number): void {
    if (confirm('Are you sure you want to delete this hobby?')) {
      this.hobbyService.deleteHobby(id).subscribe({
        next: () => {
          console.log('Hobby deleted successfully');
          this.loadHobbies();
        },
        error: (error) => {
          console.error('Error deleting hobby:', error);
        }
      });
    }
  }
  
  // Track by function for better performance
  trackByHobbyId(index: number, hobby: Hobby): number {
    return hobby.id;
  }
} 