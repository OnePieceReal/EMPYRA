# EMPYRA - Employee Management System

![EMPYRA Logo](https://via.placeholder.com/150x50?text=EMPYRA) 
*(Consider adding your actual logo here)*

## Table of Contents
- [Project Overview](#project-overview)
- [Demo](#demo)
- [Features](#features)
- [Preview](#preview)
- [Setup Guide](#setup-guide)
- [Technologies Used](#technologies-used)
- [API Documentation](#api-documentation)
- [License](#license)

## Project Overview

EMPYRA is a comprehensive Employee Management System built with Angular for the frontend and Spring Boot for the backend. The system leverages REST APIs to facilitate seamless communication between the frontend and backend components, with API documentation automatically generated using SwaggerUI.

To enhance user experience during employee data entry, EMPYRA integrates with the Restful Countries API, providing intelligent suggestions for country-related fields. The system also features advanced data visualization capabilities powered by Chart.js, presenting key metrics such as employee joining trends over time, department-wise average age and ratings, and geographical distribution of employees across cities, states, and countries.

The analytics module takes this a step further by feeding the visualized data to the OpenRouter API, which accesses DeepSeek V3 0324 to generate actionable insights. These AI-powered insights include key findings, strategic recommendations, emerging trends, and potential opportunities specific to your organizational data.

## Demo

[Live Demo](#) *(Add your live demo link here when available)*

## Features

- **Comprehensive Employee Management**: Create, read, update, and delete employee records
- **Intelligent Data Entry**: Country suggestions powered by Restful Countries API
- **Advanced Analytics**: 
  - Visualizations of employee demographics and performance metrics
  - AI-generated insights with DeepSeek V3 0324 via OpenRouter API
- **Responsive Dashboard**: Interactive charts and data visualizations
- **Secure API Integration**: Robust Spring Boot backend with documented REST endpoints

## Preview

*(Add screenshots of your application here)*
![Dashboard Preview](https://via.placeholder.com/600x400?text=Dashboard+Preview)
![Employee Management Preview](https://via.placeholder.com/600x400?text=Employee+Management)

## Setup Guide

To set up EMPYRA locally, follow these steps:

### Prerequisites
- Node.js (v16 or later)
- Angular CLI
- Java JDK (v11 or later)
- Maven
- MySQL or your preferred database

### Backend Setup
```bash
# Clone the repository
git clone https://github.com/yourusername/empyra.git

# Navigate to backend directory
cd empyra/backend

# Install dependencies
mvn install

# Configure your database settings in application.properties
# spring.datasource.url=jdbc:mysql://localhost:3306/empyra
# spring.datasource.username=yourusername
# spring.datasource.password=yourpassword

# Run the Spring Boot application
mvn spring-boot:run
