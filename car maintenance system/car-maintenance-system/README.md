# 🚗 Smart Car Maintenance Analytics System

نظام إدارة صيانة السيارات وتحليل الأعطال مع التنبؤ بالصيانة القادمة

## 📋 Overview

نظام متكامل لإدارة صيانة السيارات يتضمن:
- إدارة السيارات وسجلات الصيانة
- تسجيل الأعطال والإصلاحات
- تحليل البيانات والتنبؤ بالصيانة
- لوحة تحكم تفاعلية

## 🏗️ Architecture

```
car-maintenance-system/
├── backend/                 # Spring Boot Backend
│   ├── src/main/java/
│   │   └── com/carmaintenance/
│   │       ├── config/      # Configuration classes
│   │       ├── controller/  # REST Controllers
│   │       ├── dto/         # Data Transfer Objects
│   │       ├── entity/      # JPA Entities
│   │       ├── repository/  # JPA Repositories
│   │       ├── service/     # Business Logic
│   │       └── exception/   # Exception Handling
│   └── pom.xml
│
├── analytics/               # Python Analytics Service
│   ├── app.py              # Flask Application
│   └── requirements.txt
│
├── frontend/                # React Dashboard (Next.js)
│   └── (in main project)
│
└── database/
    └── schema.sql          # MySQL Database Schema
```

## 🛠️ Technology Stack

### Backend
- **Java 17**
- **Spring Boot 3.2.0**
- **Spring Data JPA**
- **Spring Security**
- **MySQL/MariaDB**
- **JWT Authentication**

### Analytics
- **Python 3.9+**
- **Flask**
- **Pandas**
- **Scikit-learn**
- **NumPy**

### Frontend
- **Next.js 16**
- **React 18**
- **TypeScript**
- **Tailwind CSS**
- **shadcn/ui**
- **Recharts**

## 🚀 Quick Start

### Prerequisites
- Java 17+
- Python 3.9+
- Node.js 18+
- MySQL 8.0+
- Maven 3.8+

### 1. Database Setup

```bash
# Create database and tables
mysql -u root -p < database/schema.sql
```

### 2. Backend Setup

```bash
cd backend

# Build the project
mvn clean install

# Run the application
mvn spring-boot:run
```

Backend will run on: `http://localhost:8080/api`

### 3. Analytics Service Setup

```bash
cd analytics

# Create virtual environment
python -m venv venv
source venv/bin/activate  # On Windows: venv\Scripts\activate

# Install dependencies
pip install -r requirements.txt

# Run the service
python app.py
```

Analytics service will run on: `http://localhost:5000`

### 4. Frontend Setup

```bash
# The frontend is already running in the main project
# Access it through the preview panel
```

Frontend runs on: `http://localhost:3000`

## 📚 API Documentation

### Cars API

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/cars` | Get all cars |
| GET | `/api/cars/{id}` | Get car by ID |
| POST | `/api/cars` | Create new car |
| PUT | `/api/cars/{id}` | Update car |
| DELETE | `/api/cars/{id}` | Delete car |
| PATCH | `/api/cars/{id}/mileage` | Update mileage |

### Maintenance API

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/maintenance/car/{carId}` | Get car maintenance records |
| POST | `/api/maintenance` | Create maintenance record |
| PUT | `/api/maintenance/{id}` | Update maintenance record |
| GET | `/api/maintenance/upcoming` | Get upcoming maintenance |

### Failures API

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/failures/car/{carId}` | Get car failures |
| POST | `/api/failures` | Create failure record |
| GET | `/api/failures/most-common` | Get most common failures |
| POST | `/api/failures/{id}/repairs` | Add repair to failure |

### Analytics API

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/analytics/dashboard` | Get dashboard statistics |
| GET | `/api/analytics/predictions/{carId}` | Get predictions for car |
| POST | `/api/analytics/predict/{carId}` | Generate new predictions |

## 📊 Database Schema

### Main Tables

- **cars** - معلومات السيارات
- **maintenance_types** - أنواع الصيانة
- **maintenance_records** - سجلات الصيانة
- **failure_types** - أنواع الأعطال
- **failures** - سجلات الأعطال
- **repairs** - سجلات الإصلاحات
- **predictions** - التوقعات
- **analytics_summary** - ملخص التحليلات

## 🔧 Configuration

### Backend (application.yml)

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/car_maintenance_db
    username: root
    password: your_password

analytics:
  service:
    url: http://localhost:5000
```

### Analytics Service

```python
# Default configuration
app.run(host='0.0.0.0', port=5000, debug=True)
```

## 📈 Features

### إدارة السيارات
- ✅ إضافة سيارة جديدة
- ✅ تعديل بيانات السيارة
- ✅ حذف سيارة
- ✅ البحث في السيارات
- ✅ عرض تفاصيل السيارة

### إدارة الصيانة
- ✅ تسجيل عملية صيانة
- ✅ عرض سجل الصيانة
- ✅ الصيانة القادمة
- ✅ تكاليف الصيانة

### إدارة الأعطال
- ✅ تسجيل عطل جديد
- ✅ تصنيف شدة العطل
- ✅ تسجيل الإصلاحات
- ✅ أكثر الأعطال تكراراً

### التحليلات والتوقعات
- ✅ إحصائيات لوحة التحكم
- ✅ توقعات الصيانة
- ✅ توقعات الأعطال
- ✅ تقدير التكاليف
- ✅ مؤشر صحة السيارة

## 🔒 Security

- JWT Authentication
- CORS Configuration
- Input Validation
- SQL Injection Protection (JPA)

## 📝 License

This project is licensed under the MIT License.

## 👥 Authors

- Auto-Intellix Team

---

**ملاحظة:** هذا المشروع يعمل بشكل مستقل ولكن يمكن دمجه مع نظام Auto-Intellix الموجود.
