package it.gruppo27.Models;

import it.gruppo27.Exceptions.InvalidNumberException;
import it.gruppo27.Models.Contact.ContactEmail;
import it.gruppo27.Models.Contact.ContactNumero;
import it.gruppo27.Models.Contact.Contatto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class RubricaTest {
    Rubrica r;

    @BeforeEach
    void setUp() throws Exception {
        Contatto c = new Contatto("nome","cognome","descrizione",false);
        c.addEmail( new ContactEmail("mail@mail.com"));
        c.addNumero( new ContactNumero("1234567890"));
        r = new Rubrica();
        r.addContatto(c);
        r.getListaOsservabile().setAll(r.getContatti());

    }

    // ID = 2.1.1
    @Test
    void addContattoNonDuplicatoTest() {
        Contatto contattoNonDuplicato= new Contatto("nome2","cognome2","descrizione",false);
        r.addContatto(contattoNonDuplicato);
        assertEquals(2, r.getContatti().size());
        System.out.println("Test ID = 2.1.1 SUPERATO");
    }

    // ID = 2.1.2
    @Test
    void addContattoDuplicatoTest() throws Exception{
        //Aggiungo contatto duplicato.Un contatto è duplicato se , ha stesso cognome stesso nome e stessi numeri di telefono
        Contatto contattoDuplicato = new Contatto("nome","cognome","descrizione",false);
        contattoDuplicato.addEmail( new ContactEmail("mail@mail.com"));
        contattoDuplicato.addNumero(new ContactNumero("1234567890"));
        r.addContatto(contattoDuplicato);
        assertEquals(1, r.getContatti().size());
        System.out.println("Test ID = 2.1.2 SUPERATO");

    }

    // ID = 2.2.1
    @Test
    void removeContattoInesistenteTest() {
        //rimuovo contatto inesistente
        r.removeContatto( new Contatto("nome2","cognome2","descizione",false));
        assertEquals(1, r.getContatti().size());
        System.out.println("Test ID = 2.2.1 SUPERATO");
    }

    // ID = 2.2.2
    @Test
    void removeContattoEsistente(){
        Contatto c2 = new Contatto("nome2","cognome2","descizione",false);
        //Aggiungo il contatto
        r.addContatto(c2);
        r.removeContatto(c2);
        assertEquals(1 , r.getContatti().size());
        System.out.println("Test ID = 2.2.2 SUPERATO");
    }

    // ID = 2.3
    @Test
    void getListaOsservabileTest() throws Exception {
        assertNotNull(r.getListaOsservabile());
        System.out.println("Test ID = 2.3 SUPERATO");
    }

    // ID = 2.4
    @Test
    void getContattiTest() throws Exception {
        assertNotNull(r.getContatti());
        System.out.println("Test ID = 2.4 SUPERATO");
    }

    // ID = 2.9.1
    @Test
    void ricercaContattiTest() {
        assertEquals(1,r.ricercaContatti("nome").getContatti().size()); //Cerco per nome
        assertEquals(1,r.ricercaContatti("cognome").getContatti().size());//cerco per cognome
        assertEquals(1,r.ricercaContatti("cognome"+" "+"nome").getContatti().size());//Cerco per cognome e nome
        assertEquals(1,r.ricercaContatti("nome"+" "+"cognome").getContatti().size());//cerco per nome e cognome
        System.out.println("Test ID = 2.9.1 SUPERATO");
    }

    // ID = 2.9.2
    @Test
    void ricercaContattiParzialeTest(){
        Contatto mario = new Contatto("Mario","Giordano","",false);
        Contatto marinella = new Contatto("Marinella","Rocca","",false);
        r.addContatto(mario);
        r.addContatto(marinella);
        assertEquals(1,r.ricercaContatti("Marin").getContatti().size()); //Solo marinella
        assertEquals(2,r.ricercaContatti("Mar").getContatti().size()); //Mar deve cercarmi entrambi
        System.out.println("Test ID = 2.9.2  SUPERATO");


    }

    // ID = 2.5.1
    @Test
    void isPresentContattoAssenteTest() {
        //check per contatto non presente in lista
        Contatto contattoAssente= new Contatto("nome2","cognome2","descizione",false);
        assertFalse(r.isPresent(contattoAssente));
        System.out.println("Test ID = 2.5.1 SUPERATO");
    }

    // ID = 2.5.2
    @Test
    void isPresentContattoPresenteTest(){
        //check per contatto presente in lista
        Contatto contattoPresente = new Contatto("nomePresente","cognomePresente","description",false);
        r.addContatto(contattoPresente);
        assertTrue(r.isPresent(contattoPresente));
        System.out.println("Test ID = 2.5.2 SUPERATO");
    }

    // ID = 2.6
    @Test
    void deleteAllTest() {
        r.deleteAll();
        assertEquals(0, r.getContatti().size());
        System.out.println("Test ID = 2.6 SUPERATO");
    }

    // ID = 2.7
    @Test
    void salvaVCFTest() {
        try {
            r.salvaVCF("LaMiaRubrica.vcf");
        }catch(IOException ex){}

        File vcfFile = new File("LaMiaRubrica.vcf");
        //Verifico che il file sia stato creato
        assertTrue(vcfFile.exists());
        String fileContent="";
        try {
             fileContent = new String(Files.readAllBytes(vcfFile.toPath()), StandardCharsets.UTF_8);
        }catch(IOException ex){}
         String expectedContent =
                 "BEGIN:VCARD" +"\n" +
                 "VERSION:3.0" + "\n" +
                 "PRODID:ez-vcard 0.12.1" + "\n" +
                 "FN:nome cognome" +"\n" +
                 "TEL;TYPE=cell:1234567890" + "\n" +
                 "EMAIL:mail@mail.com" + "\n" +
                 "X-DESCRIPTION:descrizione" + "\n"+
                 "END:VCARD" +"\n";
        // Normalizzo i separatori di riga per entrambi i contenuti, per evitare che i file siano diversi solo per i separatori di riga diversi
        String normalizedFileContent = fileContent.replace("\r\n", "\n");
        String normalizedExpectedContent = expectedContent.replace("\r\n", "\n");

        assertEquals(normalizedExpectedContent, normalizedFileContent);
        //Pulisco dopo averlo creato
        vcfFile.delete();
        System.out.println("Test ID = 2.7 SUPERATO");
    }

    // ID = 2.8
    @Test
    void leggiVCFTest() {
        //Salvo la rubrica su file
        try{
            r.salvaVCF("LaMiaRubrica.vcf");
        }catch(Exception ex){}

        //Leggo la rubrica appena salvata
        Rubrica rubricaLettaDaFile =null ;
        try {
             rubricaLettaDaFile = r.leggiVCF("LaMiaRubrica.vcf");
        }catch(Exception ex){}

        assertNotNull(rubricaLettaDaFile); //Verifico innanzitutto che sia stata popolata

        List<Contatto> contattiRubricaVecchia= new ArrayList<>();
        List<Contatto> contattiLettiDaFile= new ArrayList<>();
        //Salvo tutti i contatti della rubrica che è stata salvata
        for(Contatto c : r.getContatti()){
            contattiRubricaVecchia.add(c);
        }

        //Salvo tutti i contatti della rubrica che è stata letta
        for(Contatto c : rubricaLettaDaFile.getContatti()){
            contattiLettiDaFile.add(c);
        }

        int risultatoCompareTo = 20;
        //verifico che TUTTI i contatti della rubrica che è stata salvata e di quella che è stata letta siano uguali
        for(int i=0;i<contattiLettiDaFile.size();i++){
            risultatoCompareTo = contattiLettiDaFile.get(i).compareTo(contattiRubricaVecchia.get(i));
            assertTrue(risultatoCompareTo==0);
        }

        System.out.println("Test ID = 2.8 SUPERATO");
    }
}