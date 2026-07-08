# VCI Log Parser Engine
An automated diagnostic log parsing system designed to process raw telemetric data from 
automative scanners (such as Autocom/Delphi) and structure them into a relational database.

## Tech Stack & Architecture
- **Language:** Java 17
- **Database:** PostgresSQL (Hosted inside Docker)
- **Data Extraction:** Regex Pattern Matching Engine ('Pattern' & 'Matcher')
- **Persistence:** JDBC (Batch Processing & ACID Transactions)

## How to run Locally
### Prerequisites 
- Java Development Kit (JDK) 17 or higher.
- Docker Desktop installed and running.

### Step-by-Step Setup
1. **Clone the repository:**
```bash
   git clone [https://github.com/YOUR_USERNAME/vci-log-parser.git](https://github.com/YOUR_USERNAME/vci-log-parser.git)
   cd vci-log-parser
```
2. **Spin up database infrastructure:**
```bash
    docker compose up -d
```
3. **Provide a sample log:** Place your raw diagnostic report file named "diagnostic_report.txt"
in the root folder of the project.
4. **Execute the parser:** Run the vciparser.ReportParser.js class from your preferred IDE.

## Database Schema
The database runs fully normalized using a 1:N (One-to-many) relationship:
* vehicles: Stores unique Vehicle Identification Numbers (VIN).
* vehicles_dtcs: Stores individual Diagnostic Trouble Codes (DTC) linked to their respective
vehicle Foreign Keys.