package com.dummy.myerp.testbusiness.business.test;

import com.dummy.myerp.business.impl.AbstractBusinessManager;
import com.dummy.myerp.business.impl.TransactionManager;
import com.dummy.myerp.consumer.dao.contrat.DaoProxy;
import com.dummy.myerp.model.bean.comptabilite.CompteComptable;
import com.dummy.myerp.model.bean.comptabilite.EcritureComptable;
import com.dummy.myerp.model.bean.comptabilite.JournalComptable;
import com.dummy.myerp.model.bean.comptabilite.LigneEcritureComptable;
import com.dummy.myerp.business.contrat.BusinessProxy;
import com.dummy.myerp.business.contrat.manager.ComptabiliteManager;
import com.dummy.myerp.testbusiness.business.BusinessTestCase;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Date;

import static org.junit.Assert.assertTrue;

public class ComptabiliteManagerImplTest extends BusinessTestCase {

    private static final Logger LOGGER = LogManager.getLogger(ComptabiliteManagerImplTest.class);

    private static ComptabiliteManager manager;

    private static DaoProxy daoProxy = getDaoProxy();

    private static BusinessProxy businessProxy = getBusinessProxy();;

    private static TransactionManager transactionManager = getTransactionManager();


    @BeforeClass
    public static void setUpClass(){

        AbstractBusinessManager.configure(businessProxy, daoProxy, transactionManager);
        manager = businessProxy.getComptabiliteManager();
    }


    @Test
    public void insertEcritureComptable() throws Exception {

        // Préparation des beans
        String libelle = "InsertEcritureComptableIntégrationManager";
        JournalComptable journalComptable = manager.getListJournalComptable().get(0);
        CompteComptable compteComptable = manager.getListCompteComptable().get(0);

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

        manager.addReference(vEcritureComptable);

        // Insert
        LOGGER.debug("Exécution de [manager.insertEcritureComptable(ecritureComptable);] | Résultat attendu : Aucun");
        manager.insertEcritureComptable(vEcritureComptable);


        // Vérification de l'insert
        Boolean insert = false;
        for (EcritureComptable ecritureComptable : manager.getListEcritureComptable()) {
            if(ecritureComptable.getLibelle().equals(libelle)) {
                insert = true;
                break;
            }
        }
        assertTrue("insert => true => insertion réussi", insert);
    }

    @Test
    public void updateEcritureComptable() throws Exception {

        // Préparation des beans
        String libelle = "UpdateEcritureComptableIntégrationManager";
        JournalComptable journalComptable = manager.getListJournalComptable().get(1);
        CompteComptable compteComptable = manager.getListCompteComptable().get(1);

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


        // Insertion d'une écriture - 1ere étape avant l'update
        manager.insertEcritureComptable(vEcritureComptable);

        // Récupération de l'écriture inséré
        vEcritureComptable = null;
        for (EcritureComptable ecritureComptable : manager.getListEcritureComptable()) {
            if(ecritureComptable.getLibelle().equals(libelle)) {
                vEcritureComptable = ecritureComptable;
                break;
            }
        }
        if(vEcritureComptable == null)
            throw new Exception("Echec de l'insertion de l'écriture comptable (1er partie de la méthode)");


        // Ajout de la référence à l'écriture récupéré - Préparation du paramètre à update
        manager.addReference(vEcritureComptable);
        String reference = vEcritureComptable.getReference();

        // Update
        LOGGER.debug("Exécution de [manager.updateEcritureComptable(ecritureComptable);] | Résultat attendu : Aucun");
        manager.updateEcritureComptable(vEcritureComptable);

        // Vérification de l'update
        Boolean update = false;
        for (EcritureComptable ecritureComptable : manager.getListEcritureComptable()) {
            if(ecritureComptable.getReference() != null && ecritureComptable.getReference().equals(reference)) {
                update = true;
                break;
            }
        }
        assertTrue("update => true => update réussi", update);
    }

    @Test
    public void deleteEcritureComptable() {

        Integer id = -2;
        manager.deleteEcritureComptable(id);

        Boolean delete = true;
        for (EcritureComptable ecritureComptable : manager.getListEcritureComptable()) {
            if(ecritureComptable.getId() == id) {
                delete = false;
                break;
            }
        }
        Assert.assertTrue("Delete => True => Ecriture supprimé", delete);

    }

}
