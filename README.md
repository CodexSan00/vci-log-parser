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
3. **Execute the parser:** Run the **VciParserApplication.java** class from your preferred IDE to start the Spring Boot 
server on port 8080
4. **Test the API**: Send a raw report text via curl or any API client:
```bash
   curl -X POST http://localhost:8080/api/parser/upload \
  -H "Content-Type: text/plain" \
  -H "Accept: application/json" \
  -d "VIN: WDB9340311K1234567 UCE: 10 DTC: P0118 Description: Sensor circuit high" 
```

## Database Schema
The database runs fully normalized using a 1:N (One-to-many) relationship:
* vehicles: Stores unique Vehicle Identification Numbers (VIN).
* vehicles_dtcs: Stores individual Diagnostic Trouble Codes (DTC) linked to their respective
vehicle Foreign Keys.