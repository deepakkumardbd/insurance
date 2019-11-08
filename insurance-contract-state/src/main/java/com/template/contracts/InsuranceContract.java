package com.template.contracts;

import com.template.states.InsuranceState;
import net.corda.core.contracts.CommandData;
import net.corda.core.contracts.CommandWithParties;
import net.corda.core.contracts.Contract;
import net.corda.core.identity.AbstractParty;
import net.corda.core.transactions.LedgerTransaction;

import java.util.stream.Collectors;

import static net.corda.core.contracts.ContractsDSL.requireSingleCommand;
import static net.corda.core.contracts.ContractsDSL.requireThat;

// ************
// * Contract *
// ************
public class InsuranceContract implements Contract {
    // This is used to identify our contract when building a transaction.
    public static final String ID = "com.template.contracts.InsuranceContract";

    // A transaction is valid if the verify() function of the contract of all the transaction's input and output states
    // does not throw an exception.
    @Override
    public void verify(LedgerTransaction tx) {
        final CommandWithParties<Commands> command = requireSingleCommand(tx.getCommands(),Commands.class);
        Commands value = command.getValue();

        if(value instanceof Commands.Issue){
            requireThat(require ->{
                require.using("No input should be consumed when issuing the insurance state.",tx.getInputs().isEmpty());
                require.using("Only one output should be consumed", tx.getOutputs().size() == 1);
                final InsuranceState out = tx.outputsOfType(InsuranceState.class).get(0);
                require.using("all participants must sign",command.getSigners().containsAll(out.getParticipants().stream().map(AbstractParty::getOwningKey).collect(Collectors.toList())));
                require.using(" output state should be in NEW", out.getState().equalsIgnoreCase("NEW"));
                return null;
            });
        }
        if(value instanceof Commands.Respond){
            requireThat(require ->{
                final InsuranceState out = tx.outputsOfType(InsuranceState.class).get(0);
                final InsuranceState input = tx.inputsOfType(InsuranceState.class).get(0);
                require.using("one input should be consumed when issuing the insurance state.",tx.getInputs().size() == 1);
                require.using("Only one output should be consumed", tx.getOutputs().size() == 1);
                require.using("all participants must sign",command.getSigners().containsAll(out.getParticipants().stream().map(AbstractParty::getOwningKey).collect(Collectors.toList())));
                require.using(" output state should be in Received", out.getState().equalsIgnoreCase("RECEIVED"));
                require.using(" Input state should be in NEW", input.getState().equalsIgnoreCase("NEW"));
                return null;
            });
        }
    }

    // Used to indicate the transaction's intent.
    public interface Commands extends CommandData {
        class Issue implements Commands {}
        class Respond implements Commands {}
    }
}