package com.dummy.myerp.business.impl.manager;

import com.dummy.myerp.business.impl.AbstractBusinessManager;
import com.dummy.myerp.business.impl.TransactionManager;
import com.dummy.myerp.business.contrat.BusinessProxy;
import com.dummy.myerp.consumer.dao.contrat.ComptabiliteDao;
import com.dummy.myerp.consumer.dao.contrat.DaoProxy;
import com.dummy.myerp.model.bean.comptabilite.*;
import com.dummy.myerp.technical.exception.FunctionalException;
import com.dummy.myerp.technical.exception.NotFoundException;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.*;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ComptabiliteManagerImplTest {



    // ****************************************************************************************** Déclaration des objets



    private static ComptabiliteManagerImpl manager;

    private static BusinessProxy businessProxy;

    private static DaoProxy daoProxy;

    private static TransactionManager transactionManager;

    private static ComptabiliteDao comptabiliteDao;



    // ****************************************************************************************** Configs



    @BeforeClass
    public static void setUpClass() throws Exception {

        // Initialisation
        manager = new ComptabiliteManagerImpl();
        businessProxy = mock(BusinessProxy.class);
        daoProxy = mock(DaoProxy.class);
        transactionManager = mock(TransactionManager.class);
        comptabiliteDao = mock(ComptabiliteDao.class);

        // Config general
        AbstractBusinessManager.configure(businessProxy, daoProxy, transactionManager);
        when(daoProxy.getComptabiliteDao()).thenReturn(comptabiliteDao);
    }



    // ****************************************************************************************** Methodes



    @Test
    public void checkEcritureComptableUnit() throws Exception {

        EcritureComptable vEcritureComptable = new EcritureComptable();

        vEcritureComptable.setJournal(new JournalComptable("AC", "Achat"));
        vEcritureComptable.setDate(new Date());
        vEcritureComptable.setLibelle("Libelle");

        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1),
                                                                                 null, new BigDecimal(123),
                                                                                 null));
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(2),
                                                                                 null, null,
                                                                                 new BigDecimal(123)));
        manager.checkEcritureComptableUnit(vEcritureComptable);
    }


    @Test(expected = FunctionalException.class)
    public void checkEcritureComptableUnitViolation() throws Exception {
        // 4 Violations à :
        // NotNull => vEcritureComptable.journal
        // NotNull => vEcritureComptable.libelle
        // NotNull => vEcritureComptable.date
        // [2; ?] => vEcritureComptable.listLigneEcriture

        EcritureComptable vEcritureComptable = new EcritureComptable();
        manager.checkEcritureComptableUnit(vEcritureComptable);
    }


    @Test(expected = FunctionalException.class)
    public void checkEcritureComptableUnitViolation2() throws Exception {
        // 3 Violations à :
        // [1; 200] => vEcritureComptable.libelle
        // Valid.[0; 200] => vEcritureComptable.listLigneEcriture.libelle
        // Valid.NotNull => vEcritureComptable.listLigneEcriture.compteComptable

        StringBuilder libelle = new StringBuilder();
        for(int i = 0; i < 250; i++) libelle.append("X");

        EcritureComptable vEcritureComptable = new EcritureComptable();
        vEcritureComptable.setJournal(new JournalComptable());
        vEcritureComptable.setDate(new Date());
        vEcritureComptable.setLibelle(libelle.toString());
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(null, libelle.toString(), null, null));
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(null, libelle.toString(), null, null));

        manager.checkEcritureComptableUnit(vEcritureComptable);
    }


    @Test(expected = FunctionalException.class)
    public void checkEcritureComptableUnitRG2() throws Exception {

        EcritureComptable vEcritureComptable;
        vEcritureComptable = new EcritureComptable();
        vEcritureComptable.setJournal(new JournalComptable("AC", "Achat"));
        vEcritureComptable.setDate(new Date());
        vEcritureComptable.setLibelle("Libelle");
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1),
                                                                                 null, new BigDecimal(123),
                                                                                 null));
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(2),
                                                                                 null, null,
                                                                                 new BigDecimal(1234)));
        manager.checkEcritureComptableUnit(vEcritureComptable);
    }


    @Test(expected = FunctionalException.class)
    public void checkEcritureComptableUnitRG3() throws Exception {

        EcritureComptable vEcritureComptable;
        vEcritureComptable = new EcritureComptable();
        vEcritureComptable.setJournal(new JournalComptable("AC", "Achat"));
        vEcritureComptable.setDate(new Date());
        vEcritureComptable.setLibelle("Libelle");
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1),
                                                                                 null, null, null));
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1),
                                                                                 null, null, null));
        manager.checkEcritureComptableUnit(vEcritureComptable);
    }


    @Test(expected = FunctionalException.class)
    public void checkEcritureComptableUnitRG5() throws Exception {

        EcritureComptable vEcritureComptable = new EcritureComptable();
        vEcritureComptable.setJournal(new JournalComptable("PO", "PO"));
        vEcritureComptable.setDate(new Date());
        vEcritureComptable.setLibelle("libelle");
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(), "", new BigDecimal("1"), null));
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(), "", null, new BigDecimal("1")));

        // 1 Violation à : Pattern => vEcritureComptable.reference
        vEcritureComptable.setReference("XXXXXXXXXXXXXXXXXXXXXXX");
        manager.checkEcritureComptableUnit(vEcritureComptable);
    }


    @Test(expected = FunctionalException.class)
    public void checkEcritureComptableUnit2RG5() throws Exception {

        EcritureComptable vEcritureComptable = new EcritureComptable();
        vEcritureComptable.setJournal(new JournalComptable("PO", "PO"));
        vEcritureComptable.setDate(new Date());
        vEcritureComptable.setLibelle("libelle");
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(), "", new BigDecimal("1"), null));
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(), "", null, new BigDecimal("1")));

        // 1 Violation à : Année dans la référence (2000) != vEcritureComptable.date (new date = année de l'éxécution du code = 2019)
        vEcritureComptable.setReference("PO-2000/00001");
        manager.checkEcritureComptableUnit(vEcritureComptable);
    }


    @Test(expected = FunctionalException.class)
    public void checkEcritureComptableUnit3RG5() throws Exception {

        EcritureComptable vEcritureComptable = new EcritureComptable();
        vEcritureComptable.setJournal(new JournalComptable("PO", "PO"));
        vEcritureComptable.setDate(new Date());
        vEcritureComptable.setLibelle("libelle");
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(), "", new BigDecimal("1"), null));
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(), "", null, new BigDecimal("1")));

        // 1 Violation à : Code dans la référence (MM) != vEcritureComptable.journal.code (PO)
        vEcritureComptable.setReference("MM-2019/00001");
        manager.checkEcritureComptableUnit(vEcritureComptable);
    }


    @Test(expected = FunctionalException.class)
    public void checkEcritureComptableContextRG6() throws Exception {

        when(comptabiliteDao.getEcritureComptableByRef("BOOM-2016/00005")).thenReturn(new EcritureComptable());

        EcritureComptable ecritureComptable = new EcritureComptable();
        ecritureComptable.setReference("BOOM-2016/00005");

        manager.checkEcritureComptableContext(ecritureComptable);


    }


    @Test
    public void checkEcritureComptableContext2RG6() throws Exception {

        doThrow(NotFoundException.class)
            .when(comptabiliteDao).getEcritureComptableByRef("KPI-2018/00067");

        EcritureComptable ecritureComptable = new EcritureComptable();
        ecritureComptable.setReference("KPI-2018/00067");

        manager.checkEcritureComptableContext(ecritureComptable);


    }


    @Test(expected = FunctionalException.class)
    public void checkEcritureComptableUnitRG7() throws Exception {
        // 2 Violations à : (RG7)
        // Valid.Digits(integer = 13, fraction = 2) (MontantComptable.java) => vEcritureComptable.listLigneEcriture.debit
        // Valid.Digits(integer = 13, fraction = 2) (MontantComptable.java) => vEcritureComptable.listLigneEcriture.credit

        EcritureComptable vEcritureComptable = new EcritureComptable();
        vEcritureComptable.setJournal(new JournalComptable());
        vEcritureComptable.setDate(new Date());
        vEcritureComptable.setReference("BQ-2019/00001");
        vEcritureComptable.setLibelle("libelle");
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(), null, new BigDecimal("10.111"), null));
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(), null, null, new BigDecimal("10.111")));
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(), null, new BigDecimal("123456789123456789"), null));
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(), null, null, new BigDecimal("123456789123456789")));

        manager.checkEcritureComptableUnit(vEcritureComptable);
    }


    @Test
    public void checkEcritureComptable() throws Exception{

        doThrow(NotFoundException.class)
                .when(comptabiliteDao).getEcritureComptableByRef("AC-2019/00001");

        EcritureComptable vEcritureComptable = new EcritureComptable();

        String codeJournal = "AC";
        String sequenceEcritureComptable = "00001";
        Date dateEcritureComptable = new Date();

        Calendar calendar = new GregorianCalendar();
        calendar.setTime(dateEcritureComptable);


        vEcritureComptable.setJournal(new JournalComptable("AC", "Achat"));
        vEcritureComptable.setDate(new Date());
        vEcritureComptable.setReference( codeJournal + "-" + calendar.get(Calendar.YEAR) + "/" + sequenceEcritureComptable );
        vEcritureComptable.setLibelle("Libelle");

        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1),
                                                                        null, new BigDecimal(123),
                                                                        null));

        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(2),
                                                null, null,
                                                         new BigDecimal(123)));

        manager.checkEcritureComptable(vEcritureComptable);
    }


    @Test(expected = FunctionalException.class)
    public void addReference() throws Exception {

        doThrow(NotFoundException.class)
                .doReturn(new SequenceEcritureComptable(2019, 9))
                .doReturn(new SequenceEcritureComptable(2019, 99999999))
                .when(comptabiliteDao).getSequenceEcritureComptable(2019, "BPO");

        EcritureComptable ecritureComptable = new EcritureComptable();

        ecritureComptable.setJournal(new JournalComptable("BPO", "BPO"));
        ecritureComptable.setDate(new Date());

        ecritureComptable.setLibelle("ref attendu = 'BPO-2019/00001'");
        manager.addReference(ecritureComptable);
        Assert.assertEquals(ecritureComptable.toString(), "BPO-2019/00001", ecritureComptable.getReference());

        ecritureComptable.setLibelle("ref attendu = 'BPO-2019/00010'");
        manager.addReference(ecritureComptable);
        Assert.assertEquals(ecritureComptable.toString(), "BPO-2019/00010", ecritureComptable.getReference());

        // FunctionnalException (cf => this.setup())
        manager.addReference(ecritureComptable);


    }



    // ****************************************************************************************** Insert / Update / Delete



    @Test
    public void insertEcritureComptable() throws Exception {

        doThrow(NotFoundException.class).when(comptabiliteDao).getEcritureComptableByRef(anyString());

        EcritureComptable vEcritureComptable = new EcritureComptable();

        String codeJournal = "AC";
        String sequenceEcritureComptable = "00001";
        Date dateEcritureComptable = new Date();

        Calendar calendar = new GregorianCalendar();
        calendar.setTime(dateEcritureComptable);


        vEcritureComptable.setJournal(new JournalComptable("AC", "Achat"));
        vEcritureComptable.setDate(new Date());
        vEcritureComptable.setReference( codeJournal + "-" + calendar.get(Calendar.YEAR) + "/" + sequenceEcritureComptable );
        vEcritureComptable.setLibelle("Libelle");

        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1),
                null, new BigDecimal(123),
                null));

        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(2),
                null, null,
                new BigDecimal(123)));

        manager.insertEcritureComptable(vEcritureComptable);
    }


    @Test
    public void updateEcritureComptable() throws Exception {

        EcritureComptable vEcritureComptable = new EcritureComptable();

        vEcritureComptable.setJournal(new JournalComptable("AC", "Achat"));
        vEcritureComptable.setDate(new Date());
        vEcritureComptable.setLibelle("Libelle");

        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1),
                null, new BigDecimal(123),
                null));
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(2),
                null, null,
                new BigDecimal(123)));

        manager.updateEcritureComptable(vEcritureComptable);
    }


    @Test
    public void deleteEcritureComptable() {

        manager.deleteEcritureComptable(null);
    }



    // ****************************************************************************************** Getters



    @Test
    public void getListCompteComptable() {

        List<CompteComptable> compteComptableList = new ArrayList<>();
        for (int i = 0; i < 10; i++) compteComptableList.add(new CompteComptable());
        when(comptabiliteDao.getListCompteComptable()).thenReturn(compteComptableList);

        Assert.assertEquals("getListCompteComptable() = 10 (defini par ComptabiliteManagerImplTest.setup())", 10, manager.getListCompteComptable().size());
    }


    @Test
    public void getListJournalComptable() {

        List<JournalComptable> journalComptableList = new ArrayList<>();
        for (int i = 0; i < 10; i++) journalComptableList.add(new JournalComptable());
        when(comptabiliteDao.getListJournalComptable()).thenReturn(journalComptableList);

        Assert.assertEquals("getListJournalComptable() = 10 (defini par ComptabiliteManagerImplTest.setup())", 10, manager.getListJournalComptable().size());
    }


    @Test
    public void getListEcritureComptable() {

        List<EcritureComptable> ecritureComptableList = new ArrayList<>();
        for (int i = 0; i < 10; i++) ecritureComptableList.add(new EcritureComptable());
        when(comptabiliteDao.getListEcritureComptable()).thenReturn(ecritureComptableList);

        Assert.assertEquals("getListEcritureComptable() = 10 (defini par ComptabiliteManagerImplTest.setup())", 10, manager.getListEcritureComptable().size());
    }

    
}
