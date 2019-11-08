package com.insurance.flows;

import net.corda.core.flows.FlowLogic;
import net.corda.core.flows.InitiatingFlow;
import net.corda.core.transactions.SignedTransaction;

@InitiatingFlow
public abstract class IssuanceFlow extends FlowLogic<SignedTransaction> {

}
