package com.dummy.myerp.model.bean.comptabilite;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Test;


public class EcritureComptableTest {


    private LigneEcritureComptable createLigne(Integer pCompteComptableNumero, String pDebit, String pCredit) {

        BigDecimal vDebit = pDebit == null ? null : new BigDecimal(pDebit);
        BigDecimal vCredit = pCredit == null ? null : new BigDecimal(pCredit);
        String vLibelle = ObjectUtils.defaultIfNull(vDebit, BigDecimal.ZERO)
                                     .subtract(ObjectUtils.defaultIfNull(vCredit, BigDecimal.ZERO)).toPlainString();
        LigneEcritureComptable vRetour = new LigneEcritureComptable(new CompteComptable(pCompteComptableNumero),
                                                                    vLibelle,
                                                                    vDebit, vCredit);
        return vRetour;
    }



    @Test
    public void getTotalDebit() {

        EcritureComptable ecritureComptable = new EcritureComptable();

        ecritureComptable.setLibelle("Total_debit : "+( 78.50 + 100 + 99.99 + 13 + 0 + 0 + -56 + 93 + -12 ));
        ecritureComptable.getListLigneEcriture().add(this.createLigne(1, "78.50", "19.50"));
        ecritureComptable.getListLigneEcriture().add(this.createLigne(1, "100", "799.99"));
        ecritureComptable.getListLigneEcriture().add(this.createLigne(1, "99.99", null));
        ecritureComptable.getListLigneEcriture().add(this.createLigne(8, "13", "13"));
        ecritureComptable.getListLigneEcriture().add(this.createLigne(8, null, null));
        ecritureComptable.getListLigneEcriture().add(this.createLigne(8, null, "63.5"));
        ecritureComptable.getListLigneEcriture().add(this.createLigne(19, "-56", "-32"));
        ecritureComptable.getListLigneEcriture().add(this.createLigne(15, "93", "-93"));
        ecritureComptable.getListLigneEcriture().add(this.createLigne(15, "-12", null));

        //Assert.assertTrue(ecritureComptable.toString(), ecritureComptable.getTotalDebit().compareTo(new BigDecimal(78.50 + 100 + 99.99 + 13 + 0 + 0 )) == 0);
        Assert.assertEquals(
                            ecritureComptable.toString(),
                            new BigDecimal((  78.50 + 100 + 99.99 + 13 + 0 + 0 + -56 + 93 + -12 ) ,
                                    new MathContext( ecritureComptable.getTotalDebit().precision() )),
                            ecritureComptable.getTotalDebit()
                            );


        ecritureComptable.getListLigneEcriture().clear();

        ecritureComptable.setLibelle("Total_debit : 0 => aucune ligne");
        Assert.assertNotEquals(
                ecritureComptable.toString(),
                new BigDecimal((  78.50 + 100 + 99.99 + 13 + 0 + 0 + -56 + 93 + -12 ) ,
                        new MathContext( ecritureComptable.getTotalDebit().precision() )),
                ecritureComptable.getTotalDebit()
        );
    }



    @Test
    public void getTotalCredit() {

        EcritureComptable ecritureComptable = new EcritureComptable();

        ecritureComptable.setLibelle("Total_credit : "+( 19.50 + 799.99 + 0 + 13 + 0 + 63.5 + -32 + -93 + 0 ));
        ecritureComptable.getListLigneEcriture().add(this.createLigne(1, "78.50", "19.50"));
        ecritureComptable.getListLigneEcriture().add(this.createLigne(1, "100", "799.99"));
        ecritureComptable.getListLigneEcriture().add(this.createLigne(1, "99.99", null));
        ecritureComptable.getListLigneEcriture().add(this.createLigne(8, "13", "13"));
        ecritureComptable.getListLigneEcriture().add(this.createLigne(8, null, null));
        ecritureComptable.getListLigneEcriture().add(this.createLigne(8, null, "63.5"));
        ecritureComptable.getListLigneEcriture().add(this.createLigne(19, "-56", "-32"));
        ecritureComptable.getListLigneEcriture().add(this.createLigne(15, "93", "-93"));
        ecritureComptable.getListLigneEcriture().add(this.createLigne(15, "-12", null));

        Assert.assertEquals(
                ecritureComptable.toString(),
                new BigDecimal((  19.50 + 799.99 + 0 + 13 + 0 + 63.5 + -32 + -93 + 0 ) ,
                        new MathContext( ecritureComptable.getTotalCredit().precision() )),
                ecritureComptable.getTotalCredit()
        );


        ecritureComptable.getListLigneEcriture().clear();

        ecritureComptable.setLibelle("Total_credit : 0 => aucune ligne");
        Assert.assertNotEquals(
                ecritureComptable.toString(),
                new BigDecimal((  19.50 + 799.99 + 0 + 13 + 0 + 63.5 + -32 + -93 + 0 ) ,
                        new MathContext( ecritureComptable.getTotalCredit().precision() )),
                ecritureComptable.getTotalCredit()
        );
    }



    @Test
    public void isEquilibree() {
        EcritureComptable vEcriture;
        vEcriture = new EcritureComptable();

        vEcriture.setLibelle("Equilibrée");
        vEcriture.getListLigneEcriture().add(this.createLigne(1, "200.50", null));
        vEcriture.getListLigneEcriture().add(this.createLigne(1, "100.50", "33"));
        vEcriture.getListLigneEcriture().add(this.createLigne(2, null, "301"));
        vEcriture.getListLigneEcriture().add(this.createLigne(2, "40", "7"));
        Assert.assertTrue(vEcriture.toString(), vEcriture.isEquilibree());

        vEcriture.getListLigneEcriture().clear();
        vEcriture.setLibelle("Non équilibrée");
        vEcriture.getListLigneEcriture().add(this.createLigne(1, "10", null));
        vEcriture.getListLigneEcriture().add(this.createLigne(1, "20", "1"));
        vEcriture.getListLigneEcriture().add(this.createLigne(2, null, "30"));
        vEcriture.getListLigneEcriture().add(this.createLigne(2, "1", "2"));
        Assert.assertFalse(vEcriture.toString(), vEcriture.isEquilibree());
    }




}
