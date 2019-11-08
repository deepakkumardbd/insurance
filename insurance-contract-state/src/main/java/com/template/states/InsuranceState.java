package com.template.states;

import com.template.contracts.InsuranceContract;
import net.corda.core.contracts.BelongsToContract;
import net.corda.core.contracts.ContractState;
import net.corda.core.contracts.LinearState;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.identity.AbstractParty;
import net.corda.core.identity.Party;
import net.corda.core.serialization.ConstructorForDeserialization;
import net.corda.core.serialization.CordaSerializable;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

// *********
// * State *
// *********

@CordaSerializable
@BelongsToContract(InsuranceContract.class)
public class InsuranceState implements LinearState {

    private final Party hospital;
    private final Party insuranceComp;
    private final String insuranceId;
    private final String initialComments;
    private final Integer roomNo;
    private final Integer bedNo;
    private final String doctorName;
    private final String dept;
    private final UniqueIdentifier linearId;
    private String stateHash;
    private final String state;


    @ConstructorForDeserialization
    public InsuranceState(String insuranceId, String initialComments, int roomNo, int bedNo, String doctorName, String dept, UniqueIdentifier linearId, Party hospital, Party insuranceComp, String state ) {
        this.insuranceId = insuranceId;
        this.initialComments = initialComments;
        this.roomNo = roomNo;
        this.bedNo = bedNo;
        this.doctorName = doctorName;
        this.dept = dept;
        this.linearId = linearId;
        this.hospital = hospital;
        this.insuranceComp = insuranceComp;
        this.state = state;
    }

    public InsuranceState(InsuranceState other, String state){
        this.insuranceId = other.insuranceId;
        this.initialComments = other.initialComments;
        this.roomNo = other.roomNo;
        this.bedNo = other.bedNo;
        this.doctorName = other.doctorName;
        this.dept = other.dept;
        this.linearId = other.linearId;
        this.hospital = other.hospital;
        this.insuranceComp = other.insuranceComp;
        this.state = state;
    }


    public String getState() {
        return state;
    }

    public String getInsuranceId() {
        return insuranceId;
    }

    public String getInitialComments() {
        return initialComments;
    }

    public Integer getRoomNo() {
        return roomNo;
    }

    public Integer getBedNo() {
        return bedNo;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public String getDept() {
        return dept;
    }

    @Override
    public UniqueIdentifier getLinearId() {
        return linearId;
    }

    public Party getHospital() {
        return hospital;
    }

    public Party getInsuranceComp() {
        return insuranceComp;
    }

    public String getStateHash() {
        return stateHash;
    }

    public void setStateHash(String stateHash) {
        this.stateHash = stateHash;
    }

    @NotNull
    @Override
    public List<AbstractParty> getParticipants() {
        return Arrays.asList(hospital,insuranceComp);
    }
}