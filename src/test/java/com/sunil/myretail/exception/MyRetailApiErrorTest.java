package com.sunil.myretail.exception;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Date;

public class MyRetailApiErrorTest {

    MyRetailApiError classUnderTest;

    @BeforeEach
    public void setup(){

    }

    @Test
    public void testMyRetailApiError() throws Exception {

        classUnderTest = new MyRetailApiError();
        classUnderTest.setStatus(400);
        classUnderTest.setError("bad data");
        classUnderTest.setTimestamp(new Date());

        ObjectMapper mapper = new ObjectMapper();
        String myRetailApiErrorString  = mapper.writeValueAsString(classUnderTest);

        assertTrue(myRetailApiErrorString.contains("\"status\""));
        assertTrue(myRetailApiErrorString.contains("\"error\""));
        assertTrue(myRetailApiErrorString.contains("\"timestamp\""));

        assertTrue(myRetailApiErrorString.contains("\"bad data\""));
        assertTrue(myRetailApiErrorString.contains("400"));


    }

}