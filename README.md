# Shopify Multi-Tenant Data Ingestion & Insights Platform

## Overview

A multi-tenant Shopify analytics platform built using Spring Boot and PostgreSQL that ingests customer, order, and product data from Shopify stores into a centralized analytics dashboard.

The platform simulates how modern SaaS systems onboard enterprise retailers, synchronize store data, and generate actionable business insights through analytics and visualization workflows.

This project focuses on:

* Shopify API integration
* Multi-tenant backend architecture
* Scheduled data synchronization
* Analytics dashboard engineering
* Relational database design
* SaaS-oriented system design

---

# Features

## Shopify Data Ingestion

* Customer data synchronization
* Orders ingestion pipeline
* Product synchronization
* Shopify Admin REST API integration
* Scheduled background sync service

## Multi-Tenant Architecture

* Tenant-based data isolation
* Tenant-specific configuration
* Shared database with tenant separation
* Scalable onboarding architecture

## Analytics Dashboard

* Total revenue analytics
* Total orders overview
* Customer insights
* Sales trend visualization
* Top customers by spend
* Date-range filtering
* KPI metrics dashboard

## Backend Engineering

* RESTful API architecture
* Controller-Service-Repository pattern
* Hibernate/JPA ORM integration
* PostgreSQL relational schema design
* Background scheduler implementation

---

# Tech Stack

## Backend

* Java
* Spring Boot
* Spring Data JPA
* Hibernate
* REST APIs

## Frontend

* HTML
* CSS
* JavaScript
* Chart.js

## Database

* PostgreSQL

## Integrations

* Shopify Admin REST APIs

---

# System Architecture

```text
┌─────────────────────────────┐
│      Shopify Stores         │
└─────────────┬───────────────┘
              │
              ▼
┌─────────────────────────────┐
│ Shopify Admin REST APIs     │
└─────────────┬───────────────┘
              │
              ▼
┌─────────────────────────────┐
│ Spring Boot Backend         │
│                             │
│ • Controllers               │
│ • Sync Services             │
│ • Analytics Services        │
│ • Scheduler Services        │
│ • JPA/Hibernate             │
└─────────────┬───────────────┘
              │
              ▼
┌─────────────────────────────┐
│ PostgreSQL Database         │
│ • Tenants                   │
│ • Customers                 │
│ • Orders                    │
│ • Products                  │
└─────────────┬───────────────┘
              │
              ▼
┌─────────────────────────────┐
│ Analytics Dashboard         │
│ • Charts                    │
│ • KPIs                      │
│ • Insights                  │
│ • Trend Analysis            │
└─────────────────────────────┘
```

---

# Data Flow

1. Shopify store data is fetched using Shopify Admin REST APIs.
2. Customer, order, and product data are mapped into Java entities.
3. Data is stored in PostgreSQL with tenant-level isolation.
4. Background scheduler periodically syncs new store data.
5. Analytics services aggregate metrics from the database.
6. Dashboard visualizes insights through charts and KPI cards.

---

# Database Design

## Core Tables

### Tenant

Stores tenant-specific store configuration and onboarding information.

### Customer

Stores Shopify customer details including spend analytics.

### Order

Stores transaction and sales information.

### Product

Stores Shopify product catalog data.

---

# API Endpoints

## Analytics APIs

### Get Dashboard Overview

```http
GET /api/analytics/{tenantId}/overview
```

### Get Top Customers

```http
GET /api/analytics/{tenantId}/customers/top
```

---

## Synchronization APIs

### Trigger Manual Sync

```http
POST /api/sync/{tenantId}/all
```

---

# Dashboard Metrics

The analytics dashboard provides:

* Total Revenue
* Total Orders
* Total Customers
* Average Order Value
* Top Customers by Spend
* Sales Trend Visualization
* Orders by Date
* Tenant-Specific KPIs

---

# Engineering Highlights

* Built scalable multi-tenant architecture
* Implemented Shopify API integration workflows
* Designed tenant-level database isolation
* Developed scheduled synchronization services
* Built analytics aggregation pipelines
* Structured modular Spring Boot backend
* Implemented ORM-based database management
* Developed responsive analytics dashboard

---

# Assumptions & Trade-offs

## Scheduler-Based Sync

Implemented scheduled polling using Spring Scheduler instead of real-time webhooks for faster MVP development.

## Authentication

Authentication layer was intentionally skipped in this iteration to prioritize ingestion architecture and analytics workflows.

## Pagination

Current synchronization flow supports limited API records per sync cycle and does not yet implement cursor-based pagination.

---

# Future Improvements

* Shopify webhook integration
* JWT authentication with Spring Security
* Cursor-based pagination
* Real-time dashboard updates
* Advanced analytics engine
* AI-based sales prediction

---

# Setup Instructions

## Clone Repository

```bash
git clone <your-repository-link>
```

---

## Backend Setup

```bash
cd backend
```

Configure:

* PostgreSQL database
* Shopify API credentials

Run the application:

```bash
./mvnw spring-boot:run
```

---

## Frontend Setup

Open:

```text
src/main/resources/static/Frontend.html
```

in browser after backend starts.

---

# Environment Variables

```env
SHOPIFY_STORE_URL=
SHOPIFY_ACCESS_TOKEN=
SPRING_DATASOURCE_URL=
SPRING_DATASOURCE_USERNAME=
SPRING_DATASOURCE_PASSWORD=
```

---

# Author

## Akash Kumar

Java Backend Engineer | AI-Integrated Systems Developer | Cloud & DevOps Enthusiast

### Connect With Me

* GitHub: https://github.com/akashkumar-05
* LinkedIn: https://www.linkedin.com/in/akashsight30/

---

# License

This project is developed for educational, portfolio, and engineering demonstration purposes.
