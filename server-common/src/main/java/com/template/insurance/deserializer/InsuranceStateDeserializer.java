package com.template.insurance.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.template.insurance.bo.InsuranceStateBO;

import java.io.IOException;

public class InsuranceStateDeserializer extends JsonDeserializer<InsuranceStateBO> {

    public InsuranceStateBO deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        ObjectCodec oc = p.getCodec();
        JsonNode node = oc.readTree(p);

        InsuranceStateBO insuranceStateBO = new InsuranceStateBO();

        insuranceStateBO.setHospitalName(node.get("hospitalName") != null ? node.get("hospitalName").asText() : null);
        insuranceStateBO.setInsuranceComp(node.get("insuranceComp") != null ? node.get("insuranceComp").asText(): null);
        insuranceStateBO.setInsuranceId(node.get("insuranceId") != null ? node.get("insuranceId").asText() : null);
        insuranceStateBO.setInitialComments(node.get("initialComments") != null ? node.get("initialComments").asText() : null);
        insuranceStateBO.setRoomNo(node.get("roomNo") != null ? node.get("roomNo").asInt() : null);
        insuranceStateBO.setBedNo(node.get("bedNo") != null ? node.get("bedNo").asInt() : null);
        insuranceStateBO.setDoctorName(node.get("doctorName") != null ? node.get("doctorName").asText() : null);
        insuranceStateBO.setDept(node.get("dept") != null ? node.get("dept").asText() : null);
        insuranceStateBO.setState(node.get("state") != null ? node.get("state").asText() : null);
        return insuranceStateBO;
    }
}
