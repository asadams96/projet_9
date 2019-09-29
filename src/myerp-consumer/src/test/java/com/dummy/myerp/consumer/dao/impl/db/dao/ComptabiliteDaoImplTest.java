package com.dummy.myerp.consumer.dao.impl.db.dao;

import com.dummy.myerp.consumer.dao.impl.db.rowmapper.comptabilite.EcritureComptableRM;
import com.dummy.myerp.consumer.dao.impl.db.rowmapper.comptabilite.SequenceEcritureComptableRM;
import com.dummy.myerp.consumer.db.DataSourcesEnum;
import com.dummy.myerp.model.bean.comptabilite.*;
import com.dummy.myerp.technical.exception.NotFoundException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ComptabiliteDaoImplTest {

    private static final Logger LOGGER = LogManager.getLogger(ComptabiliteDaoImplTest.class);



    // ********************************************************************************************** Déclaration des beans
    private static DataSource dataSource;

    private static JdbcTemplate jdbcTemplate;

    private static NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private static ComptabiliteDaoImpl comptabiliteDaoImpl;




    @BeforeClass
    public static void setUpClass() throws Exception {

        // ********************************************************************************************** Initialisation des beans
        dataSource = mock(DataSource.class);
        jdbcTemplate = mock(JdbcTemplate.class);
        namedParameterJdbcTemplate = mock(NamedParameterJdbcTemplate.class);
        comptabiliteDaoImpl = ComptabiliteDaoImpl.getInstance();



        // ********************************************************************************************** AbstractDbConsumer Configs (classe parent)
        Map<DataSourcesEnum, DataSource> dataSourceMap = new HashMap<>();
        dataSourceMap.put(DataSourcesEnum.MYERP, dataSource);
        ComptabiliteDaoImpl.configure(dataSourceMap);



        // ********************************************************************************************** Configs
        comptabiliteDaoImpl.setJdbcTemplate(jdbcTemplate);
        comptabiliteDaoImpl.setNamedParameterJdbcTemplate(namedParameterJdbcTemplate);



        // ********************************************************************************************** Requêtes SQL
        comptabiliteDaoImpl.setSQLgetListCompteComptable("SELECT * FROM myerp.compte_comptable");
        comptabiliteDaoImpl.setSQLgetListJournalComptable("SELECT * FROM myerp.journal_comptable");
        comptabiliteDaoImpl.setSQLgetListEcritureComptable("SELECT * FROM myerp.ecriture_comptable");
        comptabiliteDaoImpl.setSQLgetEcritureComptable("SELECT * FROM myerp.ecriture_comptable WHERE id = :id");
        comptabiliteDaoImpl.setSQLgetEcritureComptableByRef("SELECT * FROM myerp.ecriture_comptable WHERE reference = :reference");
        comptabiliteDaoImpl.setSQLloadListLigneEcriture("SELECT * FROM myerp.ligne_ecriture_comptable WHERE ecriture_id = :ecriture_id ORDER BY ligne_id");
        comptabiliteDaoImpl.setSQLinsertEcritureComptable("INSERT INTO myerp.ecriture_comptable ( id, journal_code, reference, date, libelle) " +
                                                            "VALUES ( nextval('myerp.ecriture_comptable_id_seq'), :journal_code, :reference, :date, :libelle)");
        comptabiliteDaoImpl.setSQLinsertListLigneEcritureComptable("INSERT INTO myerp.ligne_ecriture_comptable ( ecriture_id, ligne_id, compte_comptable_numero, libelle, debit, credit) " +
                                                                    "VALUES ( :ecriture_id, :ligne_id, :compte_comptable_numero, :libelle, :debit, :credit )");
        comptabiliteDaoImpl.setSQLupdateEcritureComptable("UPDATE myerp.ecriture_comptable " +
                                                            "SET journal_code = :journal_code, reference = :reference, date = :date, libelle = :libelle " +
                                                            "WHERE id = :id");
        comptabiliteDaoImpl.setSQLdeleteEcritureComptable("DELETE FROM myerp.ecriture_comptable WHERE id = :id");
        comptabiliteDaoImpl.setSQLdeleteListLigneEcritureComptable("DELETE FROM myerp.ligne_ecriture_comptable WHERE ecriture_id = :ecriture_id");
        comptabiliteDaoImpl.setSQLgetSequenceEcritureComptable("SELECT * FROM myerp.sequence_ecriture_comptable WHERE annee = :annee AND journal_code = :journal_code");
        comptabiliteDaoImpl.setSQLupdateSequenceEcritureComptable("INSERT INTO myerp.sequence_ecriture_comptable " +
                                                                    "( annee, derniere_valeur) " +
                                                                    "VALUES ( :annee, :derniere_valeur)");
        comptabiliteDaoImpl.setSQLinsertSequenceEcritureComptable("UPDATE myerp.sequence_ecriture_comptable SET derniere_valeur = :derniere_valeur WHERE annee = :annee");

    }

    @Test
    public void getInstance() {

        LOGGER.debug("Exécution de [ComptabiliteDaoImpl.getInstance()] | Test : assertEquals");
        Assert.assertEquals("getInstance", ComptabiliteDaoImpl.class, ComptabiliteDaoImpl.getInstance().getClass());
    }

    
    // setSQLgetListCompteComptable()
    @Test
    public void getListCompteComptable() {

        LOGGER.debug("Exécution de [comptabiliteDaoImpl.getListCompteComptable()] | Test : assertNotNull");
        assertNotNull("comptabiliteDaoImpl.getListCompteComptable() != null", comptabiliteDaoImpl.getListCompteComptable());
    }

    
    // setSQLgetListJournalComptable
    @Test
    public void getListJournalComptable() {

        LOGGER.debug("Exécution de [comptabiliteDaoImpl.getListJournalComptable()] | Test : assertNotNull");
        assertNotNull("comptabiliteDaoImpl.getListJournalComptable() != null", comptabiliteDaoImpl.getListJournalComptable());
    }
    

    //setSQLgetListEcritureComptable()
    @Test
    public void getListEcritureComptable() {

        LOGGER.debug("Exécution de [comptabiliteDaoImpl.getListEcritureComptable()] | Test : assertNotNull");
        assertNotNull("comptabiliteDaoImpl.getListEcritureComptable() != null", comptabiliteDaoImpl.getListEcritureComptable());
    }

    
    // setSQLgetEcritureComptable()
    @Test(expected = NotFoundException.class)
    public void getEcritureComptable() throws NotFoundException {

        doReturn(new EcritureComptable()).doThrow(EmptyResultDataAccessException.class)
                .when(namedParameterJdbcTemplate)
                .queryForObject(anyString(), any(MapSqlParameterSource.class), any(EcritureComptableRM.class));


        LOGGER.debug("Test A => Exécution de [comptabiliteDaoImpl.getEcritureComptable(1);] | Résultat attendu : Aucun");
        comptabiliteDaoImpl.getEcritureComptable(1);

        LOGGER.debug("Test B => Exécution de [comptabiliteDaoImpl.getEcritureComptable(1);] | Résultat attendu : NotfoundException.class");
        comptabiliteDaoImpl.getEcritureComptable(1);

    }
    

    // setSQLgetEcritureComptableByRef()
    @Test(expected = NotFoundException.class)
    public void getEcritureComptableByRef() throws NotFoundException {

        doReturn(new EcritureComptable()).doThrow(EmptyResultDataAccessException.class)
                .when(namedParameterJdbcTemplate)
                .queryForObject(anyString(), any(MapSqlParameterSource.class), any(EcritureComptableRM.class));


        LOGGER.debug("TEST A => Exécution de [comptabiliteDaoImpl.getEcritureComptableByRef('reference');] | Résultat attendu : Aucun");
        comptabiliteDaoImpl.getEcritureComptableByRef("reference");

        LOGGER.debug("TEST B => Exécution de [comptabiliteDaoImpl.getEcritureComptableByRef('reference');] | Résultat attendu : NotfoundException.class");
        comptabiliteDaoImpl.getEcritureComptableByRef("reference");

    }
    

    // setSQLloadListLigneEcriture()
    @Test
    public void loadListLigneEcriture() {

        LOGGER.debug("Exécution de [comptabiliteDaoImpl.loadListLigneEcriture(mock(EcritureComptable.class));] | Résultat attendu : Aucun");
        comptabiliteDaoImpl.loadListLigneEcriture(mock(EcritureComptable.class));
    }

    
    // setSQLinsertEcritureComptable()
    @Test
    public void insertEcritureComptable() {

        LOGGER.debug("Exécution de [comptabiliteDaoImpl.insertEcritureComptable(ecritureComptable);] | Résultat attendu : Aucun");

        EcritureComptable ecritureComptable = new EcritureComptable();
        ecritureComptable.setReference("DIIP-2019/00001");
        ecritureComptable.setJournal(new JournalComptable("DI", "Dépôt interne"));
        ecritureComptable.setDate(new Date());
        ecritureComptable.setLibelle("libelle");
        ecritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1, "libelle"), "libelle", new BigDecimal("33"), new BigDecimal("16")));
        ecritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1, "libelle"), "libelle", new BigDecimal("16"), new BigDecimal("33")));


        comptabiliteDaoImpl.insertEcritureComptable(ecritureComptable);
    }

    
    // setSQLinsertListLigneEcritureComptable()
    @Test
    public void insertListLigneEcritureComptable() {

        LOGGER.debug("TEST A => Exécution de [comptabiliteDaoImpl.insertListLigneEcritureComptable(ecritureComptable);] | Résultat attendu : Aucun");

        EcritureComptable ecritureComptable = new EcritureComptable();
        ecritureComptable.setReference("DIIP-2019/00001");
        ecritureComptable.setJournal(new JournalComptable("DI", "Dépôt interne"));
        ecritureComptable.setDate(new Date());
        ecritureComptable.setLibelle("libelle");
        ecritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1, "libelle"), "libelle", new BigDecimal("33"), new BigDecimal("16")));
        ecritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1, "libelle"), "libelle", new BigDecimal("16"), new BigDecimal("33")));


        comptabiliteDaoImpl.insertListLigneEcritureComptable(ecritureComptable);
    }

    
    // setSQLupdateEcritureComptable()
    @Test
    public void updateEcritureComptable() {

        LOGGER.debug("Exécution de [comptabiliteDaoImpl.updateEcritureComptable(ecritureComptable);] | Résultat attendu : Aucun");

        EcritureComptable ecritureComptable = new EcritureComptable();
        ecritureComptable.setReference("DIIP-2019/00001");
        ecritureComptable.setJournal(new JournalComptable("DI", "Dépôt interne"));
        ecritureComptable.setDate(new Date());
        ecritureComptable.setLibelle("libelle");
        ecritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1, "libelle"), "libelle", new BigDecimal("33"), new BigDecimal("16")));
        ecritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1, "libelle"), "libelle", new BigDecimal("16"), new BigDecimal("33")));

        comptabiliteDaoImpl.updateEcritureComptable(ecritureComptable);

    }

    
    // setSQLdeleteEcritureComptable()
    @Test
    public void deleteEcritureComptable() {

        LOGGER.debug("Exécution de [comptabiliteDaoImpl.deleteEcritureComptable(1);] | Résultat attendu : Aucun");

        comptabiliteDaoImpl.deleteEcritureComptable(1);
    }

    
    // setSQLdeleteListLigneEcritureComptable()
    @Test
    public void deleteListLigneEcritureComptable() {

        LOGGER.debug("Exécution de [comptabiliteDaoImpl.deleteListLigneEcritureComptable(1);] | Résultat attendu : Aucun");

        comptabiliteDaoImpl.deleteListLigneEcritureComptable(1);
    }

    // HERE Essayer doReturn / doThrow
    // setSQLgetSequenceEcritureComptable()
    @Test(expected = NotFoundException.class)
    public void getSequenceEcritureComptable() throws NotFoundException {

        doReturn(new SequenceEcritureComptable()).doThrow(EmptyResultDataAccessException.class)
        .when(namedParameterJdbcTemplate).queryForObject(anyString(), any(MapSqlParameterSource.class), any(SequenceEcritureComptableRM.class));

        LOGGER.debug("Exécution de [comptabiliteDaoImpl.getSequenceEcritureComptable(2019, 'XXX');] | Résultat attendu : Aucun");
        comptabiliteDaoImpl.getSequenceEcritureComptable(2019, "XXX");

        LOGGER.debug("Exécution de [comptabiliteDaoImpl.getSequenceEcritureComptable(2019, 'XXX');] | Résultat attendu : NotfoundException.class");
        comptabiliteDaoImpl.getSequenceEcritureComptable(2019, "XXX");
    }

    
    // setSQLupdateSequenceEcritureComptable()
    @Test
    public void updateSequenceEcritureComptable() {

        LOGGER.debug("Exécution de [comptabiliteDaoImpl.updateSequenceEcritureComptable(new SequenceEcritureComptable(2016, 9), 'AC');] | Résultat attendu : Aucun");

        comptabiliteDaoImpl.updateSequenceEcritureComptable(new SequenceEcritureComptable(2016, 9), "AC");

    }
    
    
    // setSQLinsertSequenceEcritureComptable()
    @Test
    public void insertSequenceEcritureComptable() {

        LOGGER.debug("Exécution de [comptabiliteDaoImpl.insertSequenceEcritureComptable(new SequenceEcritureComptable(2019, 9), 'XXX');] | Résultat attendu : Aucun");

        comptabiliteDaoImpl.insertSequenceEcritureComptable(new SequenceEcritureComptable(2019, 9), "XXX");
    }

}