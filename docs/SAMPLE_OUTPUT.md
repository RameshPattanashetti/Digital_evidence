# Sample Output

```text
Digital Evidence & Chain of Custody Management System
1. Create case
2. Add evidence
3. Transfer custody
4. View case report
5. List cases
0. Exit
Enter choice: 5
Cases:
- CASE-001 | Unauthorized Cloud Access | Investigator: Inspector Sharma

Enter choice: 4
Case ID: CASE-001

Case Report
===========
Case ID: CASE-001
Title: Unauthorized Cloud Access
Investigator: Inspector Sharma
Created At: 2026-04-16 10:15:00
Evidence Count: 2

Evidence ID: EV-001
Description: Laptop image containing login traces
Type: IMAGE
Collected By: Inspector Sharma
Current Custodian: Inspector Sharma
Status: IN_CUSTODY
Custody History:
  - 2026-04-16 10:15:00 | SYSTEM -> Inspector Sharma | Initial collection

Evidence ID: EV-002
Description: Server log export from the affected account
Type: DOCUMENT
Collected By: Inspector Sharma
Current Custodian: Forensic Lab
Status: TRANSFERRED
Custody History:
  - 2026-04-16 10:15:00 | SYSTEM -> Inspector Sharma | Initial collection
  - 2026-04-16 10:16:00 | Inspector Sharma -> Forensic Lab | Digital forensic analysis
```
