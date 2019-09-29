package com.dummy.myerp.testconsumer.consumer;


import com.dummy.myerp.consumer.dao.contrat.DaoProxy;

import javax.sql.DataSource;


/**
 * Classe mère des classes de test d'intégration de la couche Consumer
 */
public abstract class ConsumerTestCase {

    static {
        SpringRegistry.init();
    }


    /** {@link DaoProxy} */
    private static final DataSource DATA_SOURCE = SpringRegistry.getDatasource();



    // ==================== Constructeurs ====================
    /**
     * Constructeur.
     */
    public ConsumerTestCase() {
    }


    // ==================== Getters/Setters ====================

    public static DataSource getDataSource() {
        return DATA_SOURCE;
    }

}
