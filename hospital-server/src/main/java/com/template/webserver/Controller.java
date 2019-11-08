package com.template.webserver;

import com.insurance.data.InsuranceData;
import com.template.contracts.InsuranceContract;
import com.template.flows.IssuanceFlowInit;
import com.template.insurance.bo.InsuranceStateBO;
import com.template.insurance.controller.CommonController;
import com.template.insurance.helper.ControllerHelper;
import com.template.states.InsuranceState;
import net.corda.core.contracts.StateAndRef;
import net.corda.core.messaging.FlowHandle;
import net.corda.core.transactions.SignedTransaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.concurrent.ExecutionException;

/**
 * Define your API endpoints here.
 */

@CrossOrigin
@RestController
@RequestMapping("/api/v1") // The paths for HTTP requests are relative to this base path.
public class Controller extends CommonController {

    private final static Logger logger = LoggerFactory.getLogger(Controller.class);

   @GetMapping("/hello")
     public String hello(){
         logger.info("Hello from hospital-server");
         return "Hello from hospital-server";
     }


   @CrossOrigin
   @PostMapping("issueInsuranceRequest/{command}")
    public ResponseEntity raiseInsuranceRequest(@PathVariable("command") String command,
                                                @RequestBody InsuranceStateBO insuranceStateBO){

         InsuranceContract.Commands contractCommand;
         SignedTransaction signedTransaction = null;
         if(command.equalsIgnoreCase("issue")){
             contractCommand = new InsuranceContract.Commands.Issue();
             logger.info("Contract command " + contractCommand);
         }else{
             return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Unknown Command: "+command);
         }
         InsuranceState insuranceState;
         StateAndRef<InsuranceState> previousState = null;

         if(insuranceStateBO == null){
             throw new IllegalArgumentException("Invalid booking Info");
         }

         insuranceState = ControllerHelper.convertToInsuranceState(insuranceStateBO,
                 getPartyFromName(insuranceStateBO.getHospitalName()), getPartyFromName(insuranceStateBO.getInsuranceComp()));

         try{
             FlowHandle<SignedTransaction> flowHandle = proxy.getRPCOps().startFlowDynamic(IssuanceFlowInit.class,new InsuranceData(insuranceState,null,contractCommand));
             signedTransaction = flowHandle.getReturnValue().get();
             return ResponseEntity.ok("Insurance booking request initiated with Txn ID: "+ signedTransaction.getId().toString());
         }catch (InterruptedException | ExecutionException e ){
             e.printStackTrace();
             logger.error("here is error");
             logger.error(e.getMessage());
             return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
         }
     }
}