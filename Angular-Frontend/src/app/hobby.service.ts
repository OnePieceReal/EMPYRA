import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Hobby } from './hobby';

@Injectable({
  providedIn: 'root'
})
export class HobbyService {
  private baseURL = "http://localhost:8080/api/v1/hobbies";

  constructor(private httpClient: HttpClient) { }

  // Get all hobbies
  getHobbiesList(): Observable<Hobby[]> {
    return this.httpClient.get<Hobby[]>(`${this.baseURL}/allHobbies`);
  }

  // Create new hobby
  createHobby(hobby: Hobby): Observable<Hobby> {
    return this.httpClient.post<Hobby>(`${this.baseURL}`, hobby);
  }

  // Update hobby
  updateHobby(id: number, hobby: Hobby): Observable<Hobby> {
    return this.httpClient.put<Hobby>(`${this.baseURL}/${id}`, hobby);
  }

  // Delete hobby
  deleteHobby(id: number): Observable<void> {
    return this.httpClient.delete<void>(`${this.baseURL}/${id}`);
  }

  // Get hobby by ID
  getHobbyById(id: number): Observable<Hobby> {
    return this.httpClient.get<Hobby>(`${this.baseURL}/${id}`);
  }
}
