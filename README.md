# MediCare Oman Billing System

## Overview

A Java-based client-server billing system for healthcare that calculates patient bills based on service type, insurance coverage, and patient category. The system uses multithreading, networking, JDBC database connectivity, and Java Collections Framework.

## Features

- **Multi-Client Support**: Concurrent client connections using multithreading
- **Real-Time Bill Calculation**: Instant processing with detailed breakdowns
- **Database Integration**: MySQL for persistent storage
- **Input Validation**: Comprehensive validation for all user inputs
- **Error Handling**: Proper exception management and resource cleanup

## System Architecture

```
CLIENT (Socket) ←→ SERVER (Port 5000) ←→ DATABASE (MySQL)
  ↓                     ↓
BillingClient      BillingServer      medicareBilling
ClientThread       BillingServerThread
```

## Project Structure

```
MediCareBillingSystem/
├── src/
│   ├── database/
│   │   └── DatabaseSetup.java
│   ├── server/
│   │   ├── BillingServer.java
│   │   └── BillingServerThread.java
│   ├── client/
│   │   ├── BillingClient.java
│   │   └── ClientThread.java
│   ├── utility/
│   │   └── PatientBillCollection.java
│   └── Main.java
├── lib/
│   └── mysql-connector-java-8.0.33.jar
└── README.md
```

## Installation

### Prerequisites
- Java 11 or higher
- MySQL 8.0 or higher
- MySQL Connector/J 8.0.33 JAR file

### Steps

1. **Setup Database**
   ```bash
   mysql -u root -p
   # Run DatabaseSetup.java to create tables
   ```

2. **Add JDBC Driver**
   - Right-click Project → Properties → Libraries
   - Add JAR: mysql-connector-java-8.0.33.jar

3. **Compile & Run**
   ```bash
   # Terminal 1 - Start Server
   java -cp lib/mysql-connector-java-8.0.33.jar:src server.BillingServer
   
   # Terminal 2 - Start Client
   java -cp lib/mysql-connector-java-8.0.33.jar:src client.BillingClient
   ```

## Database Schema

### Patient Table
| Column | Type | Description |
|--------|------|-------------|
| patient_id | INT | Primary Key, Auto Increment |
| name | VARCHAR(50) | Patient name |
| age | INT | Patient age |
| insurance_plan | VARCHAR(20) | Premium/Standard/Basic |

### PatientBill Table
| Column | Type | Description |
|--------|------|-------------|
| bill_id | INT | Primary Key, Auto Increment |
| patient_id | INT | Foreign Key (Patient) |
| visit_date | DATE | Service date |
| bill_amount | DECIMAL(10,2) | Final bill amount |

### Sample Data
```
ID 1: Ahmed Al-Balushi, Age 45, Premium
ID 2: Fatima Al-Hinai, Age 38, Standard
ID 3: Mohammed Al-Kalbani, Age 52, Basic
ID 4: Layla Al-Ismaili, Age 29, Premium
ID 5: Salem Al-Harthi, Age 61, Standard
```

## Billing Calculation

### Formula
```
serviceAmount = Service Price
insuranceDiscount = insuranceDiscount% × serviceAmount
discountedAmount = serviceAmount - insuranceDiscount
perVisitFee = Insurance Plan Fee
subtotal = discountedAmount + perVisitFee
extraCharge = subtotal × patientType%
finalBillAmount = subtotal + extraCharge
```

### Service Codes & Prices
- CONS100: OMR 12.00 (Consultation)
- LAB210: OMR 8.50 (Lab Tests)
- IMG330: OMR 25.00 (Imaging)
- US400: OMR 35.00 (Ultrasound)
- MRI700: OMR 180.00 (MRI Scan)

### Insurance Plans
- Premium: 15% discount, OMR 5.00 per-visit fee
- Standard: 10% discount, OMR 8.00 per-visit fee
- Basic: 0% discount, OMR 10.00 per-visit fee

### Patient Type Surcharges
- Outpatient: 0%
- Inpatient: 5%
- Emergency: 15%

### Example
```
Patient 1 (Ahmed - Premium, Outpatient), Service CONS100:
Step 1: serviceAmount = 12.00
Step 2: insuranceDiscount = 15% × 12.00 = 1.80
Step 3: discountedAmount = 12.00 - 1.80 = 10.20
Step 4: perVisitFee = 5.00
Step 5: subtotal = 10.20 + 5.00 = 15.20
Step 6: extraCharge = 15.20 × 0% = 0.00
Step 7: finalBillAmount = OMR 15.20
```

## Usage

### Running the Server
```bash
java -cp lib/mysql-connector-java-8.0.33.jar:src server.BillingServer
```
Output:
```
=== MediCare Billing Server ===
Server started on port 5000
Waiting for client connections...
```

### Running the Client
```bash
java -cp lib/mysql-connector-java-8.0.33.jar:src client.BillingClient
```
Follow prompts:
```
Enter Patient ID: 1
Enter Visit Date (YYYY-MM-DD): 2025-01-04
Enter Patient Type (Outpatient/Inpatient/Emergency): Outpatient
Enter Service Code (CONS100/LAB210/IMG330/US400/MRI700): CONS100
```

### Running Utility Class
```bash
java -cp lib/mysql-connector-java-8.0.33.jar:src Main
```
Menu:
```
1. Add Bill
2. Display Bill (by Patient ID)
3. Remove Bill (by Patient ID)
4. Display All Bills
5. Exit
```

## Sample Output

```
=====================================
         PATIENT BILL DETAILS        
=====================================
Patient ID: 1
Visit Date: 2025-01-04
Service Code: CONS100
Patient Type: Outpatient
Insurance Plan: Premium
-------------------------------------
Service Amount: OMR 12.00
Insurance Discount (15%): -OMR 1.80
Discounted Amount: OMR 10.20
Per-Visit Fee: OMR 5.00
Subtotal: OMR 15.20
Extra Charge (0%): OMR 0.00
=====================================
FINAL BILL AMOUNT: OMR 15.20
=====================================
```

## Key Classes

### DatabaseSetup.java
- Creates database and tables
- Inserts 5 sample patient records

### BillingClient.java & ClientThread.java
- Socket connection to server
- User input with validation
- Display bill details

### BillingServer.java & BillingServerThread.java
- Listens on port 5000
- Creates thread per client
- Retrieves patient data from database
- Calculates bills
- Inserts bill records

### PatientBillCollection.java
- ArrayList for ordered storage
- HashMap for quick lookup by patient ID
- Menu-driven operations (add, display, remove, iterate)

## Input Validation

- **Patient ID**: Positive integer only
- **Visit Date**: YYYY-MM-DD format
- **Patient Type**: Outpatient/Inpatient/Emergency only
- **Service Code**: CONS100/LAB210/IMG330/US400/MRI700 only

## Error Handling

- Invalid patient ID: "Patient ID not found in database"
- Invalid input format: Re-prompt user with error message
- Database errors: Catch SQLException and display message
- Connection errors: Proper socket closure and resource cleanup

## Multithreading

- Server uses multithreading to handle multiple clients simultaneously
- Each client connection spawns a new BillingServerThread
- Main server thread continues listening for new connections

## Security

- **SQL Injection Prevention**: PreparedStatement with parameterized queries
- **Input Validation**: Regex patterns and type checking
- **Resource Management**: Try-catch-finally blocks for cleanup

## Troubleshooting

### Server won't start
- Port 5000 might be in use → Change PORT constant
- JDBC driver not found → Add JAR to classpath

### Client can't connect
- Server not running → Start BillingServer first
- Check localhost and port 5000 are accessible

### Database errors
- MySQL not running → Start MySQL service
- Patient ID not found → Check ID is between 1-5

## Testing Scenarios

1. Run server, then client with valid patient ID
2. Test with invalid patient ID (should show error)
3. Test input validation (wrong date format, negative ID)
4. Run utility class and test all menu operations
5. Run multiple clients simultaneously

## Author
[Your Name]
Student ID: [Your ID]
Module: Advanced Programming (COMP 20014.1)
Submission Date: January 7, 2026

## References
- Oracle Java Documentation: https://docs.oracle.com/javase/
- MySQL Documentation: https://dev.mysql.com/doc/
- Java Networking Tutorial: https://www.baeldung.com/java-networking
