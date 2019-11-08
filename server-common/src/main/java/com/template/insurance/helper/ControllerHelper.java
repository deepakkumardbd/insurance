package com.template.insurance.helper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.template.insurance.bo.InsuranceStateBO;
import com.template.states.InsuranceState;
import net.corda.core.contracts.StateAndRef;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.identity.Party;
import net.corda.core.messaging.CordaRPCOps;
import net.corda.core.node.services.Vault;
import net.corda.core.node.services.vault.QueryCriteria;

import java.util.*;

public class ControllerHelper {

    public static List<StateAndRef<InsuranceState>> getBookingReqFromLinearId(String id, CordaRPCOps rpcOps) {
        UniqueIdentifier uniqueIdentifier = UniqueIdentifier.Companion.fromString(id);

        Set<Class<InsuranceState>> contractStateTypes
                = new HashSet(Collections.singletonList(InsuranceState.class));
        List<StateAndRef<InsuranceState>> updatedStateList;

        QueryCriteria linearCriteria = new QueryCriteria.LinearStateQueryCriteria(null, Arrays.asList(uniqueIdentifier),
                Vault.StateStatus.UNCONSUMED, contractStateTypes);

        Vault.Page<InsuranceState> results = rpcOps.vaultQueryByCriteria(linearCriteria, InsuranceState.class);

        if (results.getStates().size() > 0) {
            return results.getStates();
        }
        else {
            return new ArrayList<>();
        }
    }

    public static List<StateAndRef<InsuranceState>> createOutputWithHash(List<StateAndRef<InsuranceState>> requestedStates)
            throws JsonProcessingException {

        for (StateAndRef<InsuranceState> bookingStatePage : requestedStates) {
            String stateHash = bookingStatePage.getRef().getTxhash().toString();
            bookingStatePage.getState().getData().setStateHash(stateHash);
        }

        return requestedStates;
    }

    public static InsuranceState convertToInsuranceState(InsuranceStateBO insuranceStateBO, Party partyA, Party partyB){
        return new InsuranceState(insuranceStateBO.getInsuranceId(),insuranceStateBO.getInitialComments(),insuranceStateBO.getRoomNo(),
                insuranceStateBO.getBedNo(),insuranceStateBO.getDoctorName(),insuranceStateBO.getDept(),new UniqueIdentifier(),
                partyA, partyB, insuranceStateBO.getState());
    }
}
