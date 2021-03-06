package com.template.flows;


import co.paralleluniverse.fibers.Suspendable;
import com.insurance.flows.ResponderFlow;
import com.template.states.InsuranceState;
import net.corda.core.contracts.ContractState;
import net.corda.core.flows.*;
import net.corda.core.transactions.SignedTransaction;
import org.jetbrains.annotations.NotNull;
import static net.corda.core.contracts.ContractsDSL.requireThat;

@InitiatedBy(ResponderFlow.class)
public class ResponderFlowRes extends FlowLogic<SignedTransaction> {
    private final FlowSession otherPartySessions;
    public ResponderFlowRes(FlowSession otherPartySessions){
        this.otherPartySessions = otherPartySessions;
    }

    @Suspendable
    @Override
    public SignedTransaction call() throws FlowException {
        class SignTxFlow extends SignTransactionFlow {
            private SignTxFlow(FlowSession otherPartySessions){
                super(otherPartySessions);
            }

            @Override
            protected void checkTransaction(@NotNull SignedTransaction stx) throws FlowException {
                requireThat(require -> {
                    ContractState out = stx.getTx().getOutputs().get(0).getData();
                    require.using("This must be Insurance state", out instanceof InsuranceState);
                    return null;
                });
            }
        }
        SignedTransaction fullyTx = subFlow(new SignTxFlow(otherPartySessions));
        return subFlow(new ReceiveFinalityFlow(otherPartySessions,fullyTx.getId()));
    }
}
