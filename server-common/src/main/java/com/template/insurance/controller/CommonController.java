package com.template.insurance.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import com.template.insurance.config.NodeRPCConnection;
import com.template.insurance.helper.ControllerHelper;
import com.template.states.InsuranceState;
import net.corda.core.contracts.StateAndRef;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.identity.CordaX500Name;
import net.corda.core.identity.Party;
import net.corda.core.node.NodeInfo;
import net.corda.core.node.services.Vault;
import net.corda.core.node.services.vault.PageSpecification;
import net.corda.core.node.services.vault.QueryCriteria;
import net.corda.core.node.services.vault.Sort;
import net.corda.core.node.services.vault.SortAttribute;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;
import static net.corda.core.node.services.vault.QueryCriteriaUtils.DEFAULT_PAGE_SIZE;

@CrossOrigin
public class CommonController {

    @Autowired
    protected NodeRPCConnection proxy;

    @Autowired
    protected ObjectMapper mapper;

    private final static Logger logger = LoggerFactory.getLogger(CommonController.class);

    @GetMapping("/me")
    public Map<String, CordaX500Name> me() {
        logger.info("from me");
        return ImmutableMap.of("Me",proxy.getRPCOps().nodeInfo().getLegalIdentities().get(0).getName());
    }

    @GetMapping("/allNodes")
    public Map<String, List<CordaX500Name>> getAllNodes() {
        List<NodeInfo> nodeInfoSnapshot = proxy.getRPCOps().networkMapSnapshot();
        return ImmutableMap.of("allnodes",
                nodeInfoSnapshot.stream().map(node -> node.getLegalIdentities().get(0).getName()).filter(
                        name -> !name.equals(proxy.getRPCOps().nodeInfo().getLegalIdentities().get(0).getName()))
                        .collect(toList()));
    }


    @GetMapping("/getAllRequest/{pageNumber}")
    public ResponseEntity getAllBookingRequest(@PathVariable("pageNumber") Integer pageNumber, @RequestParam(value = "stateStatus",
            required = false, defaultValue = "UNCONSUMED") String stateStatus) throws JsonProcessingException {
        logger.info("Querying the vault");

        List<StateAndRef<InsuranceState>> outPutStateList = new ArrayList<>();
        Vault.StateStatus status = Vault.StateStatus.valueOf(stateStatus);
        QueryCriteria linearCriteria = new QueryCriteria.LinearStateQueryCriteria(null, null, null, status);
        List<StateAndRef<InsuranceState>> updatedStateList = new ArrayList<>();

        logger.info("2nd stage");
        PageSpecification pageSpecification = new PageSpecification(pageNumber, DEFAULT_PAGE_SIZE);
        Vault.Page<InsuranceState> requestStates = proxy.getRPCOps().vaultQueryByWithPagingSpec(InsuranceState.class, linearCriteria, pageSpecification);
        if (logger.isDebugEnabled()) {
            requestStates.getStates().forEach(e -> logger.debug(e.getState().getData().toString()));
        }
        if (!requestStates.getStates().isEmpty()) {
            outPutStateList = ControllerHelper.createOutputWithHash(requestStates.getStates());

            return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).body
                    (mapper.writeValueAsString(outPutStateList.stream()
                            .map(e -> e.getState().getData())
                            .collect(Collectors.toList())));

        } else
            return ResponseEntity.noContent().build();
    }

    @GetMapping("/getAllRequestHistory/{id}")
    public ResponseEntity getAllBookingRequestHistory(@PathVariable("id") String id, @RequestParam(value = "pageNumber",
            required = false, defaultValue = "1") Integer pageNumber) throws JsonProcessingException {

        logger.info("Querying the vault");
        List<StateAndRef<InsuranceState>> outPutStateList = new ArrayList<>();
        Set<Class<InsuranceState>> contractStateTypes
                = new HashSet(Collections.singletonList(InsuranceState.class));

        UniqueIdentifier uniqueIdentifier = UniqueIdentifier.Companion.fromString(id);

        QueryCriteria linearCriteria = new QueryCriteria.LinearStateQueryCriteria(null,
                Arrays.asList(uniqueIdentifier), Vault.StateStatus.ALL, contractStateTypes);

        PageSpecification pageSpecification = new PageSpecification(pageNumber, DEFAULT_PAGE_SIZE);

        SortAttribute.Standard sortAttribute = new SortAttribute.Standard(Sort.VaultStateAttribute.RECORDED_TIME);

        Sort sort = new Sort(Sets.newHashSet(new Sort.SortColumn(sortAttribute, Sort.Direction.DESC)));

        Vault.Page<InsuranceState> requestStates = proxy.getRPCOps().vaultQueryBy(linearCriteria,
                pageSpecification, sort, InsuranceState.class);

        if (logger.isDebugEnabled()) {
            requestStates.getStates().forEach(e -> logger.debug(e.getState().getData().toString()));
        }

        if (!requestStates.getStates().isEmpty()) {
            logger.info(requestStates.getStates().get(0).getState().getData().getLinearId().toString());
            outPutStateList = ControllerHelper.createOutputWithHash(requestStates.getStates());

            return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).body
                    (mapper.writeValueAsString(outPutStateList.stream()
                            .map(e -> e.getState().getData())
                            .collect(Collectors.toList())));

        } else{
            return ResponseEntity.noContent().build();
        }
    }

    @NotNull
    protected Party getPartyFromName(String partyName) {
        Set<Party> party = proxy.getRPCOps().partiesFromName(partyName, true);
        if (party.isEmpty()) {
            throw new IllegalArgumentException(String.format("unable to find specified party! %s", partyName));
        }
        return party.iterator().next();
    }
}