export interface DepartmentEmployee {
  id: number;
  firstName: string;
  lastName: string;
}

export interface Department {
  id: number;
  name: string;
  description: string;
  employees: DepartmentEmployee[];
} 