package com.dummy.myerp.testconsumer.consumer.test;

import com.dummy.myerp.consumer.dao.impl.db.dao.ComptabiliteDaoImpl;
import com.dummy.myerp.model.bean.comptabilite.*;
import com.dummy.myerp.technical.exception.NotFoundException;
import com.dummy.myerp.testconsumer.consumer.ConsumerTestCase;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.math.BigDecimal;
import java.util.Date;

import static org.junit.Assert.*;

public class ComptabiliteDaoImplTest extends ConsumerTestCase {

    private static final Logger LOGGER = LogManager.getLogger(ComptabiliteDaoImplTest.class);

    private static ComptabiliteDaoImpl comptabiliteDao;


    @BeforeClass
    public static void setUpClass() throws Exception {

        comptabiliteDao = ComptabiliteDaoImpl.getInstance();

        comptabiliteDao.setJdbcTemplate(new JdbcTemplate(getDataSource()));
        comptabiliteDao.setNamedParameterJdbcTemplate(new NamedParameterJdbcTemplate(getDataSource()));
    }



    // setSQLgetListJournalComptable
    @Test
    public void getListJournalComptable() {

        LOGGER.debug("Exécution de [comptabiliteDao.getListJournalComptable()] | Test : assertTrue( !isEmpty )");
        assertTrue( !comptabiliteDao.getListJournalComptable().isEmpty() );
    }


    //setSQLgetListEcritureComptable()
    @Test
    public void getListEcritureComptable() {

        LOGGER.debug("Exécution de [comptabiliteDao.getListEcritureComptable()] | Test : assertTrue( !isEmpty )");
        assertTrue( !comptabiliteDao.getListEcritureComptable().isEmpty() );
    }


    // setSQLgetEcritureComptable()
    @Test(expected = NotFoundException.class)
    public void getEcritureComptable() throws NotFoundException {

        LOGGER.debug("Test A => Exécution de [comptabiliteDao.getEcritureComptable(-1);] | Résultat attendu : Aucun");
        comptabiliteDao.getEcritureComptable(-1);

        LOGGER.debug("Test B => Exécution de [comptabiliteDao.getEcritureComptable(999);] | Résultat attendu : NotfoundException.class");
        comptabiliteDao.getEcritureComptable(999);

    }


    // setSQLgetEcritureComptableByRef()
    @Test(expected = NotFoundException.class)
    public void getEcritureComptableByRef() throws NotFoundException {

        LOGGER.debug("TEST A => Exécution de [comptabiliteDao.getEcritureComptableByRef('reference');] | Résultat attendu : Aucun");
        comptabiliteDao.getEcritureComptableByRef("BQ-2016/00005");

        LOGGER.debug("TEST B => Exécution de [comptabiliteDao.getEcritureComptableByRef('reference');] | Résultat attendu : NotfoundException.class");
        comptabiliteDao.getEcritureComptableByRef("reference");

    }


    // setSQLloadListLigneEcriture()
    @Test
    public void loadListLigneEcriture() throws NotFoundException {

        LOGGER.debug("Exécution de [comptabiliteDao.loadListLigneEcriture(comptabiliteDao.getEcritureComptableByRef(BQ-2016/00005));] | Résultat attendu : Aucun");
        comptabiliteDao.loadListLigneEcriture(comptabiliteDao.getEcritureComptableByRef("BQ-2016/00005"));
    }


    // setSQLinsertEcritureComptable()
    @Test
    public void insertEcritureComptable() {

        // Préparation des beans
        String libelle = "InsertEcritureComptableIntégrationDAO";
        JournalComptable journalComptable = comptabiliteDao.getListJournalComptable().get(0);
        CompteComptable compteComptable = comptabiliteDao.getListCompteComptable().get(0);

        EcritureComptable vEcritureComptable = new EcritureComptable();
        vEcritureComptable.setJournal(journalComptable);
        vEcritureComptable.setDate(new Date());
        vEcritureComptable.setLibelle(libelle);
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(compteComptable,
                null, new BigDecimal(123),
                null));
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(compteComptable,
                null, null,
                new BigDecimal(123)));


        // Insert
        LOGGER.debug("Exécution de [comptabiliteDao.insertEcritureComptable(ecritureComptable);] | Résultat attendu : Aucun");
        comptabiliteDao.insertEcritureComptable(vEcritureComptable);


        // Vérification de l'insert
        Boolean insert = false;
        for (EcritureComptable ecritureComptable : comptabiliteDao.getListEcritureComptable()) {
            if(ecritureComptable.getLibelle().equals(libelle)) {
                insert = true;
                break;
            }
        }
        assertTrue("insert => true => insertion réussi", insert);


    }


    // setSQLupdateEcritureComptable()
    @Test
    public void updateEcritureComptable() {

        // Préparation des beans
        String newLibelle = "UpdateEcritureComptableIntégrationDAO";
        EcritureComptable vEcritureComptable = comptabiliteDao.getListEcritureComptable().get(0);
        vEcritureComptable.setLibelle(newLibelle);
        
        // Update
        LOGGER.debug("Exécution de [comptabiliteDao.updateEcritureComptable(ecritureComptable);] | Résultat attendu : Aucun");
        comptabiliteDao.updateEcritureComptable(vEcritureComptable);

        // Vérification de l'update
        Boolean update = false;
        for (EcritureComptable ecritureComptable : comptabiliteDao.getListEcritureComptable()) {
            if(ecritureComptable.getLibelle().equals(newLibelle)) {
                update = true;
                break;
            }
        }
       assertTrue("update => true => update réussi", update);
    }


    // setSQLdeleteEcritureComptable()
    @Test
    public void deleteEcritureComptable() {

        Integer id = -3;

        LOGGER.debug("Exécution de [comptabiliteDao.deleteEcritureComptable(-2);] | Résultat attendu : assertTrue(delete)");
        comptabiliteDao.deleteEcritureComptable(id);

        Boolean delete = true;
        for (EcritureComptable ecritureComptable : comptabiliteDao.getListEcritureComptable()) {
            if(ecritureComptable.getId() == id) {
                delete = false;
                break;
            }
        }

        assertTrue("Delete => True => Ecriture supprimé", delete);
    }



    // setSQLgetSequenceEcritureComptableByAnnee()
    @Test(expected = NotFoundException.class)
    public void getSequenceEcritureComptableByAnneeAndCodeJournal() throws NotFoundException {


        LOGGER.debug("Exécution de [comptabiliteDao.getSequenceEcritureComptableByAnnee(2016);] | Résultat attendu : Aucun");
        comptabiliteDao.getSequenceEcritureComptable(2016, "AC");

        LOGGER.debug("Exécution de [comptabiliteDao.getSequenceEcritureComptableByAnnee(2222);] | Résultat attendu : NotfoundException.class");
        comptabiliteDao.getSequenceEcritureComptable(2016, "XXXXX");
    }


    // setSQLupdateSequenceEcritureComptable()
    @Test
    public void updateSequenceEcritureComptable() throws NotFoundException {

        // Préparation des beans
        Integer lastValue = 999;
        Integer annee = 2016;
        String codeJournal = "OD";
        SequenceEcritureComptable sequenceEcritureComptable = comptabiliteDao.getSequenceEcritureComptable(annee, codeJournal);
        sequenceEcritureComptable.setDerniereValeur(lastValue);

        // Update
        LOGGER.debug("Exécution de [comptabiliteDao.updateSequenceEcritureComptable(sequenceEcritureComptable, codeJournal);] | Résultat attendu : Aucun");
        comptabiliteDao.updateSequenceEcritureComptable(sequenceEcritureComptable, codeJournal);


        // Vérification de l'update
        SequenceEcritureComptable sequenceBDD = comptabiliteDao.getSequenceEcritureComptable(annee, codeJournal);
        assertEquals(sequenceEcritureComptable.getDerniereValeur().intValue(), sequenceBDD.getDerniereValeur().intValue());
    }


    // setSQLinsertSequenceEcritureComptable()
    @Test
    public void insertSequenceEcritureComptable() throws NotFoundException {

        // Préparation des beans
        Integer dernierValeur = 9;
        Integer annee = 2019;
        SequenceEcritureComptable sequenceEcritureComptable = new SequenceEcritureComptable(annee, dernierValeur);
        JournalComptable journalComptable = comptabiliteDao.getListJournalComptable().get(2);

        // Insert
        LOGGER.debug("Exécution de [comptabiliteDao.insertSequenceEcritureComptable(sequenceEcritureComptable, journalComptable.getCode());] | Résultat attendu : Aucun");
        comptabiliteDao.insertSequenceEcritureComptable(sequenceEcritureComptable, journalComptable.getCode());

        // Vérification de l'insert
        SequenceEcritureComptable sequenceBDD = comptabiliteDao.getSequenceEcritureComptable(annee, journalComptable.getCode());
        assertSame(sequenceEcritureComptable.getDerniereValeur(), sequenceBDD.getDerniereValeur());

    }
}
