package com.template.flows;

import co.paralleluniverse.fibers.Suspendable;
import com.insurance.common.InsuranceException;
import com.insurance.data.InsuranceData;
import com.insurance.flows.IssuanceFlow;
import com.insurance.helper.FlowHelper;
import com.template.contracts.InsuranceContract;
import com.template.states.InsuranceState;
import net.corda.core.contracts.Command;
import net.corda.core.contracts.StateAndContract;
import net.corda.core.contracts.StateAndRef;
import net.corda.core.flows.*;
import net.corda.core.identity.AbstractParty;
import net.corda.core.identity.Party;
import net.corda.core.transactions.SignedTransaction;
import net.corda.core.transactions.TransactionBuilder;
import net.corda.core.utilities.ProgressTracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

// ******************
// * IssuanceFlowInit flow *
// ******************
@StartableByService
@StartableByRPC
public class IssuanceFlowInit extends IssuanceFlow {
    private static final Logger logger = LoggerFactory.getLogger(IssuanceFlowInit.class);
    private final ProgressTracker progressTracker = new ProgressTracker();
    private InsuranceState newInsuranceState;
    private StateAndRef<InsuranceState> prevInsuranceState;
    private InsuranceContract.Commands command;

    public IssuanceFlowInit(InsuranceState newInsuranceState, StateAndRef<InsuranceState> prevInsuranceState, InsuranceContract.Commands command)throws FlowException {
        this.newInsuranceState = newInsuranceState;
        this.prevInsuranceState = prevInsuranceState;
        this.command = command;
    }

    public IssuanceFlowInit(InsuranceData data){
        this.newInsuranceState = data.getInsuranceState();
        this.prevInsuranceState = data.getPrevState();
        this.command = data.getCommand();
    }

    @Override
    public ProgressTracker getProgressTracker() {
        return progressTracker;
    }

    @Suspendable
    @Override
    public SignedTransaction call() throws FlowException {
        // IssuanceFlowInit flow logic goes here.

        final Party notary = getServiceHub().getNetworkMapCache().getNotaryIdentities().get(0);
        TransactionBuilder txBuilder = null;
        validateTxCommand(command);
        Command<InsuranceContract.Commands> txCommand = new Command<>(command,
                newInsuranceState.getParticipants().stream().map(AbstractParty::getOwningKey).collect(Collectors.toList()));

        txBuilder = new TransactionBuilder(notary)
                .withItems(new StateAndContract(newInsuranceState,InsuranceContract.ID),txCommand);

        try{
            final SignedTransaction signedTransaction = getServiceHub().signInitialTransaction(txBuilder);

            Set<FlowSession> allCounterPartySessions = new HashSet<>();
            Party currentParty = getOurIdentity();
            List<Party> allCounterParties;
            allCounterParties = FlowHelper.getAllCOunterParties(newInsuranceState.getParticipants(),currentParty,getServiceHub());

            for(Party counterParty : allCounterParties){
                allCounterPartySessions.add(initiateFlow(counterParty));
            }

            final SignedTransaction fullySignedTx = subFlow(new CollectSignaturesFlow(signedTransaction,allCounterPartySessions));
            return subFlow(new FinalityFlow(fullySignedTx,allCounterPartySessions));
        }catch (Exception e) {
            throw new FlowException(e.getMessage());
        }
    }

    private void validateTxCommand(InsuranceContract.Commands command){
        if(command instanceof InsuranceContract.Commands.Issue){
            return;
        }else{
            throw new InsuranceException("Not identified command");
        }
    }
}

