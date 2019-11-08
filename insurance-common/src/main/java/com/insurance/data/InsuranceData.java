package com.insurance.data;

import com.template.contracts.InsuranceContract;
import com.template.states.InsuranceState;
import net.corda.core.contracts.StateAndRef;
import net.corda.core.serialization.CordaSerializable;

@CordaSerializable
public class InsuranceData {
    private InsuranceState insuranceState;
    private StateAndRef<InsuranceState> prevState;
    private InsuranceContract.Commands command;
    public InsuranceData(InsuranceState insuranceState, StateAndRef<InsuranceState> prevState, InsuranceContract.Commands command) {
        this.insuranceState = insuranceState;
        this.prevState = prevState;
        this.command = command;
    }

    public InsuranceState getInsuranceState() {
        return insuranceState;
    }

    public StateAndRef<InsuranceState> getPrevState() {
        return prevState;
    }

    public InsuranceContract.Commands getCommand() {
        return command;
    }
}
