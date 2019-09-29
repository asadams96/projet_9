package com.dummy.myerp.testbusiness.business.test;

import com.dummy.myerp.testbusiness.business.BusinessTestCase;
import com.dummy.myerp.testbusiness.business.SpringRegistry;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;


/**
 * Classe de test de l'initialisation du contexte Spring
 */
public class TestInitSpring extends BusinessTestCase {

    /**
     * Constructeur.
     */
    public TestInitSpring() {
        super();
    }


    /**
     * Teste l'initialisation du contexte Spring
     */
    @Test
    public void testInit() {
        SpringRegistry.init();
        assertNotNull(SpringRegistry.getBusinessProxy());
        assertNotNull(SpringRegistry.getTransactionManager());
        assertNotNull(SpringRegistry.getDaoProxy());
    }
}
