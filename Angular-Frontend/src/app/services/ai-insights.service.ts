import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { map, catchError } from 'rxjs/operators';

export interface InsightRequest {
  employees: any[];
  departments: any[];
  summaryMetrics: {
    totalEmployees: number;
    averageSalary: number;
    averageRating: number;
    averageAge: number;
    mostPopularHobby: string;
    mostPopularDepartment: string;
    mostPopularState: string;
    mostPopularCity: string;
    mostPopularCountry: string;
  };
}

export interface InsightResponse {
  insights: string[];
  recommendations: string[];
  trends: string[];
  risks: string[];
  opportunities: string[];
}

@Injectable({
  providedIn: 'root'
})
export class AiInsightsService {
  private readonly openRouterApiUrl = 'https://openrouter.ai/api/v1/chat/completions';
  private readonly apiKey = 'use your own api key';

  constructor(private http: HttpClient) {}

  generateInsights(data: InsightRequest): Observable<InsightResponse> {
    const prompt = this.buildPrompt(data);
    
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${this.apiKey}`,
      'HTTP-Referer': 'http://localhost:4200', // Helps us track usage on OpenRouter
      'X-Title': 'Employee Management Analytics', // Shows up in OpenRouter dashboard
      'Content-Type': 'application/json'
    });

    const requestBody = {
      model: 'deepseek/deepseek-chat-v3-0324:free',
      messages: [
        {
          role: 'system',
          content: 'You are an expert HR analyst and business intelligence specialist. Analyze the provided employee data and generate actionable insights, recommendations, trends, risks, and opportunities. Be specific, data-driven, and provide practical advice.'
        },
        {
          role: 'user',
          content: prompt
        }
      ],
      max_tokens: 2000,
      temperature: 0.7
    };

    return this.http.post<any>(this.openRouterApiUrl, requestBody, { headers }).pipe(
      map(response => this.parseInsightResponse(response)),
      catchError(error => {
        console.error('Error generating AI insights:', error);
        return of(this.getFallbackInsights());
      })
    );
  }

  private buildPrompt(data: InsightRequest): string {
    const { employees, departments, summaryMetrics } = data;
    
    // Calculate additional metrics
    const salaryDistribution = this.calculateSalaryDistribution(employees);
    const ageDistribution = this.calculateAgeDistribution(employees);
    const departmentStats = this.calculateDepartmentStats(employees, departments);
    const locationStats = this.calculateLocationStats(employees);
    const ratingDistribution = this.calculateRatingDistribution(employees);

    return `
Please analyze the following employee data and provide comprehensive insights:

EMPLOYEE SUMMARY:
- Total Employees: ${summaryMetrics.totalEmployees}
- Average Salary: $${summaryMetrics.averageSalary.toLocaleString()}
- Average Rating: ${summaryMetrics.averageRating.toFixed(1)}/10
- Average Age: ${summaryMetrics.averageAge} years

POPULAR ITEMS:
- Most Popular Hobby: ${summaryMetrics.mostPopularHobby}
- Most Popular Department: ${summaryMetrics.mostPopularDepartment}
- Most Popular State: ${summaryMetrics.mostPopularState}
- Most Popular City: ${summaryMetrics.mostPopularCity}
- Most Popular Country: ${summaryMetrics.mostPopularCountry}

SALARY DISTRIBUTION:
${salaryDistribution.map(item => `- ${item.range}: ${item.count} employees (${item.percentage}%)`).join('\n')}

AGE DISTRIBUTION:
${ageDistribution.map(item => `- ${item.range}: ${item.count} employees (${item.percentage}%)`).join('\n')}

RATING DISTRIBUTION:
${ratingDistribution.map(item => `- ${item.range}: ${item.count} employees (${item.percentage}%)`).join('\n')}

DEPARTMENT STATISTICS:
${departmentStats.map(dept => `- ${dept.name}: ${dept.count} employees, Avg Salary: $${dept.avgSalary.toLocaleString()}, Avg Rating: ${dept.avgRating.toFixed(1)}`).join('\n')}

LOCATION STATISTICS:
${locationStats.map(loc => `- ${loc.location}: ${loc.count} employees`).join('\n')}

Please provide your response as a single valid JSON object with the following keys ONLY: insights, recommendations, trends, risks, opportunities. Each value should be an array of strings. Do not include any extra text, explanations, or markdown. Return only the JSON object, nothing else.
    `;
  }

  private calculateSalaryDistribution(employees: any[]): any[] {
    const ranges = [
      { min: 0, max: 50000, label: '$0-$50K' },
      { min: 50001, max: 75000, label: '$50K-$75K' },
      { min: 75001, max: 100000, label: '$75K-$100K' },
      { min: 100001, max: 150000, label: '$100K-$150K' },
      { min: 150001, max: Infinity, label: '$150K+' }
    ];

    return ranges.map(range => {
      const count = employees.filter(emp => emp.salary >= range.min && emp.salary <= range.max).length;
      const percentage = ((count / employees.length) * 100).toFixed(1);
      return { range: range.label, count, percentage };
    });
  }

  private calculateAgeDistribution(employees: any[]): any[] {
    const ranges = [
      { min: 18, max: 25, label: '18-25' },
      { min: 26, max: 35, label: '26-35' },
      { min: 36, max: 45, label: '36-45' },
      { min: 46, max: 55, label: '46-55' },
      { min: 56, max: 100, label: '56+' }
    ];

    return ranges.map(range => {
      const count = employees.filter(emp => {
        const age = this.calculateAge(emp.dob);
        return age >= range.min && age <= range.max;
      }).length;
      const percentage = ((count / employees.length) * 100).toFixed(1);
      return { range: range.label, count, percentage };
    });
  }

  private calculateRatingDistribution(employees: any[]): any[] {
    const ranges = [
      { min: 1, max: 3, label: '1-3' },
      { min: 4, max: 6, label: '4-6' },
      { min: 7, max: 8, label: '7-8' },
      { min: 9, max: 10, label: '9-10' }
    ];

    return ranges.map(range => {
      const count = employees.filter(emp => emp.rating >= range.min && emp.rating <= range.max).length;
      const percentage = ((count / employees.length) * 100).toFixed(1);
      return { range: range.label, count, percentage };
    });
  }

  private calculateDepartmentStats(employees: any[], departments: any[]): any[] {
    return departments.map(dept => {
      const deptEmployees = employees.filter(emp => 
        emp.employeeDepartments?.some((ed: any) => ed.department.id === dept.id)
      );
      
      const avgSalary = deptEmployees.length > 0 
        ? deptEmployees.reduce((sum, emp) => sum + emp.salary, 0) / deptEmployees.length 
        : 0;
      
      const avgRating = deptEmployees.length > 0 
        ? deptEmployees.reduce((sum, emp) => sum + emp.rating, 0) / deptEmployees.length 
        : 0;

      return {
        name: dept.name,
        count: deptEmployees.length,
        avgSalary,
        avgRating
      };
    });
  }

  private calculateLocationStats(employees: any[]): any[] {
    const locationCount: { [key: string]: number } = {};
    
    employees.forEach(emp => {
      const location = `${emp.city}, ${emp.state}`;
      locationCount[location] = (locationCount[location] || 0) + 1;
    });

    return Object.entries(locationCount)
      .map(([location, count]) => ({ location, count }))
      .sort((a, b) => b.count - a.count)
      .slice(0, 5);
  }

  private calculateAge(dob: string): number {
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

  private parseInsightResponse(response: any): InsightResponse {
    try {
      const content = response.choices[0]?.message?.content;
      if (!content) {
        throw new Error('No content in response');
      }

      // Try to parse as JSON first
      try {
        // Clean up any extra text around the JSON
        const jsonStart = content.indexOf('{');
        const jsonEnd = content.lastIndexOf('}');
        const jsonString = (jsonStart !== -1 && jsonEnd !== -1) ? content.substring(jsonStart, jsonEnd + 1) : content;
        const parsed = JSON.parse(jsonString);
        return {
          insights: Array.isArray(parsed.insights) ? parsed.insights.filter((x: any) => typeof x === 'string') : [],
          recommendations: Array.isArray(parsed.recommendations) ? parsed.recommendations.filter((x: any) => typeof x === 'string') : [],
          trends: Array.isArray(parsed.trends) ? parsed.trends.filter((x: any) => typeof x === 'string') : [],
          risks: Array.isArray(parsed.risks) ? parsed.risks.filter((x: any) => typeof x === 'string') : [],
          opportunities: Array.isArray(parsed.opportunities) ? parsed.opportunities.filter((x: any) => typeof x === 'string') : []
        };
      } catch {
        // If JSON parsing fails, try to extract insights from text
        return this.extractInsightsFromText(content);
      }
    } catch (error) {
      console.error('Error parsing AI response:', error);
      return this.getFallbackInsights();
    }
  }

  private extractInsightsFromText(text: string): InsightResponse {
    // Simple text parsing as fallback
    const lines = text.split('\n').filter(line => line.trim());
    
    return {
      insights: lines.filter(line => line.includes('insight') || line.includes('observation')).slice(0, 3),
      recommendations: lines.filter(line => line.includes('recommend') || line.includes('suggest')).slice(0, 3),
      trends: lines.filter(line => line.includes('trend') || line.includes('growing')).slice(0, 2),
      risks: lines.filter(line => line.includes('risk') || line.includes('concern')).slice(0, 2),
      opportunities: lines.filter(line => line.includes('opportunity') || line.includes('potential')).slice(0, 2)
    };
  }

  private getFallbackInsights(): InsightResponse {
    return {
      insights: [
        "Employee data analysis shows a diverse workforce with varying salary ranges and performance ratings.",
        "The team demonstrates good geographic distribution across multiple locations.",
        "Department performance varies significantly, indicating potential areas for improvement."
      ],
      recommendations: [
        "Consider implementing targeted training programs for departments with lower average ratings.",
        "Review salary structures to ensure competitive compensation across all departments.",
        "Develop retention strategies for high-performing employees in competitive markets."
      ],
      trends: [
        "Growing employee base indicates positive company expansion.",
        "Diverse location distribution suggests successful remote work adoption."
      ],
      risks: [
        "Salary disparities between departments could lead to retention issues.",
        "Concentration of employees in certain locations may create operational risks."
      ],
      opportunities: [
        "Leverage geographic diversity to expand market presence.",
        "Use performance data to optimize team structures and resource allocation."
      ]
    };
  }
} 