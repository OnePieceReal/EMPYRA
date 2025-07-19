# EMPYRA - Employee Management System

![EMPYRA Logo](Preview/empyra_logo.jpg)


## Table of Contents
- [Project Overview](#project-overview)
- [Demo](#demo)
- [Features](#features)
- [Preview](#preview)
- [Setup Guide](#setup-guide)

## Project Overview

EMPYRA is a comprehensive Employee Management System built with Angular (using Material UI) for the frontend and Spring Boot for the backend, with MySQL as the database. The system leverages REST APIs to facilitate seamless communication between the frontend and backend components, with API documentation automatically generated using SwaggerUI.

To enhance user experience during employee data entry, EMPYRA integrates with the Restful Countries API, providing intelligent suggestions for country-related fields. The system also features advanced data visualization capabilities powered by Chart.js, presenting key metrics such as employee joining trends over time, department-wise average age and ratings, and geographical distribution of employees across cities, states, and countries.

The analytics module takes this a step further by feeding the visualized data to the OpenRouter API, which accesses DeepSeek V3 0324 to generate actionable insights. These AI-powered insights include key findings, strategic recommendations, emerging trends, and potential opportunities specific to your organizational data.

## Demo



https://github.com/user-attachments/assets/07a2af0f-962c-49a8-91d5-20abc10bd39e


## Features

- **Intelligent Data Entry**: Country suggestions powered by Restful Countries API
- **Advanced Analytics**: 
  - Visualizations of employee demographics and performance metrics
  - AI-generated insights with DeepSeek V3 0324 via OpenRouter API
- **Responsive Dashboard**: Interactive charts and data visualizations
- **Secure API Integration**: Robust Spring Boot backend with documented REST endpoints


### Employee Management
![Employee List](./Preview/empyra_employeelist.jpg)
*Figure 1: Employee List View*

![Add Employee](./Preview/empyra_addemployee.jpg)
*Figure 2: Employee Creation Form*

### Department Management
![Department View](./Preview/empyra_department.jpg)
*Figure 3: Department Management*

### Analytics Dashboard
![Analytics View 1](./Preview/empyra_analytics1.jpg)
*Figure 4: Analytics Dashboard - Overview*

![Analytics View 2](./Preview/empyra_analytics2.jpg)
*Figure 5: Analytics Dashboard - Department Metrics*

![Analytics View 3](./Preview/empyra_analytics3.jpg)
*Figure 6: Analytics Dashboard - Geographical Distribution*

### Additional Features
![Hobbies Management](./Preview/empyra_hobbies.jpg)
*Figure 7: Employee Hobbies Management*

## Setup Guide

To set up EMPYRA locally, follow these steps:

### Prerequisites
- Node.js (v16 or later)
- Angular CLI
- Java JDK (v11 or later)
- Maven
- MySQL

### Backend Setup
```bash
# Clone the repository
git clone https://github.com/yourusername/empyra.git

# Navigate to backend directory
cd empyra/backend

# Install dependencies
mvn install

# Configure MySQL in application.properties
# spring.datasource.url=jdbc:mysql://localhost:3306/empyra
# spring.datasource.username=yourusername
# spring.datasource.password=yourpassword

# Run the Spring Boot application
mvn spring-boot:run
```
### Frontend Setup
# Navigate to frontend directory
cd ../frontend

# Install dependencies
npm install

# Configure API base URL in environment.ts
# export const environment = {
#   production: false,
#   apiUrl: 'http://localhost:8080/api'
# };

# Run the Angular development server
ng serve
