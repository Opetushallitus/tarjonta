/*
 * Copyright (c) 2012 The Finnish Board of Education - Opetushallitus
 * 
 * This program is free software:  Licensed under the EUPL, Version 1.1 or - as
 * soon as they will be approved by the European Commission - subsequent versions
 * of the EUPL (the "Licence");
 * 
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at: http://www.osor.eu/eupl/
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * European Union Public Licence for more details.
 */
package fi.vm.sade.tarjonta;

import fi.vm.sade.tarjonta.dao.KoulutusDAO;
import fi.vm.sade.tarjonta.dao.KoulutusSisaltyvyysDAO;
import fi.vm.sade.tarjonta.model.LearningOpportunityObject;
import fi.vm.sade.tarjonta.model.KoulutusSisaltyvyys;
import java.io.PrintWriter;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author Jukka Raanamo 
 */
@Component
public class KoulutusDatabasePrinter {

    @Autowired
    private KoulutusDAO koulutusDAO;

    @Autowired
    private KoulutusSisaltyvyysDAO sisaltyvyysDAO;

    public void printAll() {
        PrintWriter out = new PrintWriter(System.out);
        printAll(out);
        out.close();
    }

    public void printAll(PrintWriter out) {
        out.println("---- Tarjonta database dump:");
        printKoulutus(out);
        printSisaltyvyys(out);
    }

    public void printKoulutus(PrintWriter out) {

        out.println("-- List of koulutus objects:");

        List<LearningOpportunityObject> list = koulutusDAO.findAll();
        for (int i = 0; i < list.size(); i++) {
            LearningOpportunityObject k = list.get(i);
            out.println((i + 1)
                + "\n\t type: " + k.getClass().getSimpleName()
                + "\n\t id: " + k.getId()
                + "\n\t oid: " + k.getOid()
                + "\n\t nimi: " + k.getNimi()
                + "\n\t parents: " + k.getParents()
                + "\n\t children: " + k.getChildren());

        }

    }

    public void printSisaltyvyys(PrintWriter out) {

        out.println("-- List of KoulutusSisaltyvyys objects:");

        List<KoulutusSisaltyvyys> list = sisaltyvyysDAO.findAll();
        for (int i = 0; i < list.size(); i++) {
            KoulutusSisaltyvyys s = list.get(i);
            out.println((i + 1)
                + "\t" + "parent: " + s.getParent().getId()
                + ", child: " + s.getChild().getId()
                + ", optional: " + s.isOptional());

        }

    }

}

