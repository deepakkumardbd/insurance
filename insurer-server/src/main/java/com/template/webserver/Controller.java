package com.template.webserver;

import com.google.common.collect.ImmutableMap;
import com.insurance.data.InsuranceData;
import com.template.contracts.InsuranceContract;
import com.template.flows.initiator.IssuanceFlowResponInit;
import com.template.insurance.bo.InsuranceStateBO;
import com.template.insurance.controller.CommonController;
import com.template.insurance.exception.ServerException;
import com.template.insurance.helper.ControllerHelper;
import com.template.states.InsuranceState;
import net.corda.core.contracts.StateAndRef;
import net.corda.core.messaging.FlowHandle;
import net.corda.core.transactions.SignedTransaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
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
        logger.info("Hello from insurer-server");
        return "Hello from insurer-server";
    }

    @CrossOrigin
    @PostMapping("issueInsuranceResponse/{id}")
    public ResponseEntity raiseInsuranceRequest(@PathVariable("id") String id){

        InsuranceContract.Commands contractCommand;
        SignedTransaction signedTransaction = null;

        if (StringUtils.isEmpty(id)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No booking state with this bookingId!");
        }

        contractCommand = new InsuranceContract.Commands.Respond();

        List<StateAndRef<InsuranceState>> previousBooking = ControllerHelper.getBookingReqFromLinearId
                (id, proxy.getRPCOps());

        if (previousBooking == null || previousBooking.isEmpty()) {
            throw new ServerException("booking Id with id: " + id +
                    "doesn't exist please verify and try again!");
        }

        InsuranceState insuranceState = new InsuranceState(previousBooking.get(0).getState().getData(),"RECEIVED");

        try{
            FlowHandle<SignedTransaction> flowHandle = proxy.getRPCOps().startFlowDynamic(IssuanceFlowResponInit.class,new InsuranceData(insuranceState,previousBooking.get(0),contractCommand));
            signedTransaction = flowHandle.getReturnValue().get();
            return ResponseEntity.ok("Insurance booking request received with Txn ID: "+ signedTransaction.getId().toString());
        }catch (InterruptedException | ExecutionException e ){
            e.printStackTrace();
            logger.error("here is error");
            logger.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

}