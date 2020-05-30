# Insurance use case:
--------------
This is a blockchain solution between Hospital and Insurance company wherein whenever a patient is admitted in Hospital, the Hospital raises a request for reimbursement of the amount with the insurance company of the patient. The Insurance company has to be registered with the Hospital. The Insurance company confirms the request by verifying the information provided by the Hospital. The use case helps insurance company to tackle the fraudulent claims activity. Therefore the settlement of the amount between patient and Hospital is automatically done by the Insurance company without any request raised by the patient and at the same time it doesn't have any discrepency.

### Prerequisites:
* For getting familiarity with R3-Corda, visit R3-Corda [here](https://docs.corda.net/).
### Running the Insurance use case locally

1. Clone the project
2. Deploy the CordaApps locally
  * Windows:
```
cd insurance
gradlew clean build
gradlew deployNodes
```
 * Linux:
```
 cd insurance
./gradlew clean build
./gradlew deployNodes
```
3. Running the nodes locally
```
call build\nodes\runnodes (Windows)
build/nodes/runnodes (Linux)
```
4. Starting the web-server for Hospital and Insurer
```
start java -jar hospital-server/build/libs/hospital-server-0.1.jar (Hospital-server)
start java -jar insurer-server/build/libs/insurer-server-0.1.jar(Insurer-server)
```
### API Collections :

* API collection for the use-case is specified in the postman_collections.

* The use case can be extended further based on the requirements. Here we have two flows. One flow initiated by the Hospital to raise the insurance booking and another flow is there from the Insurer side to confirm the issuance of the insurance after it verifies.
