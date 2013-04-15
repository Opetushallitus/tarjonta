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
package fi.vm.sade.tarjonta.publication;

import fi.vm.sade.organisaatio.api.model.types.EmailDTO;
import fi.vm.sade.organisaatio.api.model.types.HakutoimistoTyyppi;
import fi.vm.sade.organisaatio.api.model.types.KuvailevaTietoTyyppi;
import fi.vm.sade.organisaatio.api.model.types.KuvailevaTietoTyyppiTyyppi;
import fi.vm.sade.organisaatio.api.model.types.MonikielinenTekstiTyyppi;
import fi.vm.sade.organisaatio.api.model.types.MonikielinenTekstiTyyppi.Teksti;
import fi.vm.sade.organisaatio.api.model.types.OrganisaatioDTO;
import fi.vm.sade.organisaatio.api.model.types.OrganisaatioKuvaTyyppi;
import fi.vm.sade.organisaatio.api.model.types.OrganisaatioKuvailevatTiedotTyyppi;
import fi.vm.sade.organisaatio.api.model.types.OsoiteDTO;
import fi.vm.sade.organisaatio.api.model.types.PuhelinnumeroDTO;
import fi.vm.sade.organisaatio.api.model.types.SoMeLinkkiTyyppi;
import fi.vm.sade.organisaatio.api.model.types.WwwDTO;
import fi.vm.sade.organisaatio.api.model.types.YhteyshenkiloTyyppi;
import fi.vm.sade.organisaatio.api.model.types.YhteystietoDTO;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.namespace.QName;
import javax.xml.bind.*;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;

import fi.vm.sade.tarjonta.publication.types.*;
import fi.vm.sade.tarjonta.publication.types.CodeValueType.Code;
import fi.vm.sade.tarjonta.publication.types.LearningOpportunitySpecificationType.Classification;
import fi.vm.sade.tarjonta.publication.types.LearningOpportunitySpecificationType.Description;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fi.vm.sade.tarjonta.model.*;
import fi.vm.sade.tarjonta.publication.types.AttachmentCollectionType.Attachment;
import fi.vm.sade.tarjonta.publication.types.LearningOpportunityProviderType.InstitutionInfo;
import fi.vm.sade.tarjonta.publication.types.SelectionCriterionsType.EntranceExaminations.Examination;
import fi.vm.sade.tarjonta.publication.types.WebLinkCollectionType.Link;
import fi.vm.sade.tarjonta.publication.utils.VersionedUri;
import fi.vm.sade.tarjonta.service.types.KoulutusasteTyyppi;
import fi.vm.sade.tarjonta.service.types.ValinnanPisterajaTyyppi;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Implements {@link PublicationCollector.EventHandler} by writing all
 * encountered published entities as "Pubication XML". Data is written to stream
 * as the events occur.
 *
 * @author Jukka Raanamo
 */
public class LearningOpportunityJAXBWriter extends PublicationCollector.EventHandlerSuppport {
    
    private static final Logger log = LoggerFactory.getLogger(LearningOpportunityJAXBWriter.class);
    public static final String UTF_8 = "UTF-8";
    public static final String DEFAULT_ROOT_ELEMENT_NAME = "LearningOpportunityDownloadData";
    public static final String NAMESPACE = "http://publication.tarjonta.sade.vm.fi/types";
    private XMLStreamWriter xmlWriter;
    private Marshaller marshaller;
    private boolean partialDocument = false;
    private final ObjectFactory objectFactory;
    private static DatatypeFactory datatypeFactory;
    private ExportParams params;
    /**
     * Root element gets written manually.
     */
    private String rootElementName = DEFAULT_ROOT_ELEMENT_NAME;
    /**
     * When using ID and IDREF -types with JAXB, the actual object being
     * referenced needs to be put into the "ref" field. This is problematic
     * since we need to obtain instance to an element that we'd otherwise had
     * forgotten already. This makes this writer statefull which is no good.
     * Custom IDResolver was attempted to work around this issue but Marshaller
     * does not seem to accept one. One way is to stop using ID and IDREF types,
     * another is to use more low level writing.
     */
    private Map<String, Object> idRefMap = new HashMap<String, Object>();
    private Map<String, String> komotoParentMap = new HashMap<String, String>();

    /**
     * Constructs new writer and initializes JAXBContext. Cannot be reused.
     *
     * @throws JAXBException
     */
    public LearningOpportunityJAXBWriter(ExportParams params) throws JAXBException {
        this.params = params;
        final String packageName = LearningOpportunityDownloadDataType.class.getPackage().getName();
        final JAXBContext context = JAXBContext.newInstance(packageName, LearningOpportunityDownloadDataType.class.getClassLoader());
        
        objectFactory = new ObjectFactory();
        try {
            datatypeFactory = DatatypeFactory.newInstance();
        } catch (DatatypeConfigurationException e) {
            // there's nothing that can be done
            throw new IllegalStateException("configuring DatatypeFactory failed", e);
        }
        
        marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);

        // hmm... not supported
        //marshaller.setProperty("com.sun.xml.bind.IDResolver", new CustomIDResolver());

        
    }

    /**
     * By passing a true value, this writer will not write start and end of
     * document to stream.
     *
     * todo: it seems this does not work with XMLStreamWriter
     *
     * @param partial
     */
    public void setPartialDocument(boolean partial) {
        
        this.partialDocument = partial;
        
    }

    /**
     * XML output.
     *
     * @param out
     * @throws XMLStreamException
     */
    public void setOutput(Writer out) throws XMLStreamException {
        
        xmlWriter = XMLOutputFactory.newFactory().createXMLStreamWriter(out);
        
    }

    /**
     * XML output.
     *
     * @param out
     * @throws XMLStreamException
     */
    public void setOutput(OutputStream out) throws XMLStreamException {
        
        try {
            setOutput(new OutputStreamWriter(out, UTF_8));
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException(UTF_8 + " must be supported");
        }
        
    }
    
    @Override
    public void onCollectStart() throws Exception {
        
        if (!partialDocument) {
            xmlWriter.writeStartDocument(UTF_8, "1.0");
        }
        
        xmlWriter.writeStartElement("", rootElementName);
        xmlWriter.writeAttribute("xmlns", NAMESPACE);
        
    }
    
    @Override
    public void onCollectEnd() throws Exception {
        
        xmlWriter.writeEndElement();
        if (!partialDocument) {
            xmlWriter.writeEndDocument();
        }
        
    }
    
    @Override
    public void onCollect(Haku haku) throws Exception {
        
        final ApplicationSystemType applicationSystem = objectFactory.createApplicationSystemType();
        applicationSystem.setId(haku.getOid());

        //ApplicationSystemType#status
        applicationSystem.setStatus(status(haku.getTila()));

        // ApplicationSystem/Name
        copyTexts(haku.getNimi(), applicationSystem.getName());

        // ApplicationSystem/ApplicationSeason
        addHakukausi(haku, applicationSystem);

        // ApplicationSystem/ApplicationMethod
        addHakutapa(haku, applicationSystem);

        // ApplicationSystem/Type
        addHakutyyppi(haku, applicationSystem);

        // ApplicationSystem/ApplicationYear
        addHakuvuosi(haku, applicationSystem);

        // ApplicationSystem/Identifier
        addHaunTunniste(haku, applicationSystem);

        // ApplicationSystem/EducationStartSeason
        addKoulutuksenAlkamiskausi(haku, applicationSystem);

        // ApplicationSystem/EducationStartYear
        addKoulutuksenAlkamisvuosi(haku, applicationSystem);

        // ApplicationSystem/TargetGroup
        addKohdejoukko(haku, applicationSystem);
        
        marshal(ApplicationSystemType.class, applicationSystem);
    }
    
    @Override
    public void onCollect(Hakukohde hakukohde, List<MonikielinenMetadata> soraDesc, List<MonikielinenMetadata> valintaperusteDesc) throws Exception {
        
        ApplicationOptionType applicationOption = objectFactory.createApplicationOptionType();

        //ApplicationSystemType#status
        applicationOption.setStatus(status(hakukohde.getTila()));
        // ApplicationOption/Identifier
        ApplicationOptionType.Identifier identifier = new ApplicationOptionType.Identifier();

        //ApplicationOption/Description
        TypedDescriptionType description = new TypedDescriptionType();
        description.setType(DescriptionType.GENERAL.value());
        copyDescriptions(hakukohde.getLisatiedot(), description);
        applicationOption.setDescription(description);
        
        identifier.setValue(hakukohde.getOid());
        applicationOption.setIdentifier(identifier);

        // ApplicationOption/Title - insert koodisto uri only
        addHakukohdeNimi(hakukohde, applicationOption);


        // ApplicationOption/ApplicationSystemRef
        ApplicationSystemRefType applicationSystemRef = new ApplicationSystemRefType();
        applicationSystemRef.setOidRef(hakukohde.getHaku().getOid());
        applicationOption.setApplicationSystemRef(applicationSystemRef);

        // ApplicationOption/EligibilityRequirements
        addHakukelpoisuusvaatimus(soraDesc, applicationOption);

        // ApplicationOption/SelectionCriterions
        addValintaperusteet(hakukohde, valintaperusteDesc, applicationOption);

        // ApplicationOption/LearningOpportunities
        addKoulutukset(hakukohde, applicationOption);
        
        //ApplicationOption/WeightedSubjects
        addPainotettavatOppiaineet(hakukohde, applicationOption);
        
        marshal(ApplicationOptionType.class, applicationOption);
    }  

    /*
     * Adds painotettavat oppiaineet to hakukohde. Lukio hakukohde contains these. 
     */
	private void addPainotettavatOppiaineet(Hakukohde hakukohde,
			ApplicationOptionType applicationOption) {
		for (PainotettavaOppiaine curOp : hakukohde.getPainotettavatOppiaineet()) {
			WeightedSubjectType ws = new WeightedSubjectType();
			ws.setSubject(createExtendedCodeValue(curOp.getOppiaine()));
			ws.setWeight(BigDecimal.valueOf(curOp.getPainokerroin()));
			applicationOption.getWeightedSubjects().add(ws);
		}
	}

	@Override
    public void onCollect(Koulutusmoduuli moduuli) throws Exception {
        onCollect(moduuli, null);
    }
    
    @Override
    public void onCollect(Koulutusmoduuli moduuli, KoulutusmoduuliToteutus t) throws Exception {
        LearningOpportunitySpecificationType specification = objectFactory.createLearningOpportunitySpecificationType();
        
        if (moduuli.getModuuliTyyppi() != KoulutusmoduuliTyyppi.TUTKINTO_OHJELMA && moduuli.getModuuliTyyppi() != KoulutusmoduuliTyyppi.TUTKINTO) {
            throw new Exception("KoulutusmoduuliTyyppi not supported: " + moduuli.getModuuliTyyppi());
        }
        
        if (t != null) {
            handleChildren(moduuli, t, specification);
        }

        //LearningOpportunitySpecification#status
        specification.setStatus(status(moduuli.getTila()));

        // LearningOpportunitySpecification/Name
        addNimi(moduuli, specification);

        // LearningOpportunitySpecification/type
        specification.setType(LearningOpportunityTypeType.DEGREE_PROGRAMME);

        // LearningOpportunitySpecification/id
        if (t != null) {
            specification.setId(putID(t.getOid(), specification));
        } else {
            specification.setId(putID(moduuli.getOid(), specification));
        }

        // LearningOpportunitySpecification/Identifier
        addUlkoinenTunniste(moduuli, specification);

        // LearningOpportunitySpecification/OrganizationRef
        if (t == null) {
            addOrganisaatioRef(moduuli, specification);
        } else {
            addOrganisaatioRef(t, specification);
        }


        // LearningOpportunitySpecification/OfferedBy
        specification.setOfferedBy(null);

        // LearningOpportunitySpecification/Credits
        addLaajuus(moduuli, specification);

        // LearningOpportunitySpecification/Qualification
        addTutkintonimike(moduuli, specification);

        // LearningOpportunitySpecification/DegreeTitle
        specification.setDegreeTitle(createCodeValue(CodeSchemeType.KOODISTO, moduuli.getKoulutusohjelmaKoodi()));

        // LearningOpportunitySpecification/Classification
        Classification classification = new Classification();
        specification.setClassification(classification);
        
        addKoulutusluokitus(moduuli, classification);
        addKoulutusala(moduuli, classification);
        addKoulutusaste(moduuli, classification);
        addOpintoala(moduuli, classification);
        addEqf(moduuli, classification);
        addNqf(moduuli, classification);

        // LearningOpportunitySpecification/Description
        Description description = new Description();
        specification.setDescription(description);

        // LearningOpportunitySpecification/Description/StructureDiagram
        addKoulutuksenRakenne(moduuli, description);

        // LearningOpportunitySpecification/Description/AccessToFurtherStudies
        addJatkoOpintoMahdollisuudet(moduuli, description);
        
        if (t != null) {
            addKoulutusohjelmanValinta(t, description);
        }
        
        marshal(LearningOpportunitySpecificationType.class, specification);
    }
    
    private void addKoulutusohjelmanValinta(KoulutusmoduuliToteutus koulutus, Description description) {
        copyTexts(koulutus.getKoulutusohjelmanValinta(), description.getSelectionOfDegreeProgram());
    }
    
    private void handleChildren(Koulutusmoduuli moduuli,
            KoulutusmoduuliToteutus toteutus,
            LearningOpportunitySpecificationType specification) {
        for (Koulutusmoduuli curChild : moduuli.getAlamoduuliList()) {
            LearningOpportunitySpecificationRefType losRef = createLOSRef(curChild.getOid());
            if (losRef != null) {
                this.komotoParentMap.put(curChild.getOid() + toteutus.getTarjoaja(), toteutus.getOid());
                specification.getChildLOSRefs().add(losRef);
            }
        }
    }
    
    private LearningOpportunitySpecificationRefType createLOSRef(String moduuliOid) {
        try {
            LearningOpportunitySpecificationRefType losRef = new LearningOpportunitySpecificationRefType();
            losRef.setRef(getIDREF(moduuliOid));
            return losRef;
        } catch (Exception ex) {
            return null;
        }
    }
    
    @Override
    public void onCollect(KoulutusmoduuliToteutus toteutus) throws Exception {
        
        LearningOpportunityInstanceType instance = new LearningOpportunityInstanceType();

        //LearningOpportunityInstance#status
        instance.setStatus(status(toteutus.getTila()));

        // LearningOpportunityInstance#id
        instance.setId(putID(toteutus.getOid(), instance));

        // LearningOpportunityInstance/Identifier
        addUlkoinenTunniste(toteutus, instance);

        // LearningOpportunityInstance/SpecificationRef
        addSpecificationRef(toteutus, instance);

        // LearningOpportunityInstance/OrganizationRef
        addOrganisaatioRef(toteutus, instance);

        // LearningOpportunityInstance/Prerequisite
        addPohjakoulutusvaatimus(toteutus, instance);

        // LearningOpportunityInstance/ProfessionLabels
        addAmmattinimikkeet(toteutus, instance);

        // LearningOpportunityInstance/Keywords
        addAvainsanat(toteutus, instance);

        // LearningOpportunityInstance/LanguagesOfInsruction
        addOpetuskielet(toteutus, instance);

        // LearningOpportunityInstance/FormOfEducation
        addKoulutuslajit(toteutus, instance);

        // LearningOpportunityInstance/FormOfTeaching
        addOpetusmuodot(toteutus, instance);

        // LearningOpportunityInstance/StartDate
        instance.setStartDate(toteutus.getKoulutuksenAlkamisPvm());

        // LearningOpportunityInstance/AcademicYear
        instance.setAcademicYear(formatAcademicYear(toteutus.getKoulutuksenAlkamisPvm()));

        // LearningOpportunityInstance/Duration
        addKesto(toteutus, instance);

        // LearningOpportunityInstance/Assessments
        addArviointikriteerit(toteutus, instance);

        // LearningOpportunityInstance/FinalExamination
        addLoppukoeVaatimukset(toteutus, instance);

        // LearningOpportunityInstance/CostOfEducation
        addMaksullisuus(toteutus, instance);

        // LearningOpportunityInstance/WebLinks
        addLinkit(toteutus, instance);
        
        //If the LOI is lukio, adding high school diplomas, and language assortment
        if (toteutus.getKoulutusmoduuli().getKoulutustyyppi().equals(KoulutusasteTyyppi.LUKIOKOULUTUS.value())) {
        	//LearningOpportunityInstance/LanguageAssortment
        	addLukiodiplomit(toteutus, instance);
        	//LearningOpportunityInstance/HighSchoolDiplomas
        	addKielivalikoimat(toteutus, instance);
        }
        
        marshal(LearningOpportunityInstanceType.class, instance);
    }
    
    
    
    private void addKielivalikoimat(KoulutusmoduuliToteutus toteutus,
			LearningOpportunityInstanceType instance) {
		for (Kielivalikoima curValikoima : toteutus.getTarjotutKielet()) {
			LanguageSetType curLangSet = new LanguageSetType();
			curLangSet.setSubject(curValikoima.getKey());
			curLangSet.setLanguages(toCodeValueCollection(curValikoima.getKielet()));
			instance.getLanguageAssortment().add(curLangSet);			
		}
	}

	private void addLukiodiplomit(KoulutusmoduuliToteutus toteutus,
			LearningOpportunityInstanceType instance) {
        CodeValueCollectionType collection = toCodeValueCollection(toteutus.getLukiodiplomit());
        instance.setHighSchoolDiplomas(collection);
	}

	@Override
    public void onCollect(OrganisaatioDTO tarjoaja) throws Exception {
        LearningOpportunityProviderType provider = new LearningOpportunityProviderType();
        OrganisaatioKuvailevatTiedotTyyppi kuvailevatTiedot = tarjoaja.getKuvailevatTiedot();
        
        InstitutionInfo institutionInfo = new InstitutionInfo();
        provider.setInstitutionInfo(institutionInfo);
        
        LearningOpportunityProviderType.GeneralInformation generalInformation = new LearningOpportunityProviderType.GeneralInformation();
        provider.setGeneralInformation(generalInformation);
        
        if (kuvailevatTiedot != null && kuvailevatTiedot.getHakutoimisto() != null) {
            HakutoimistoTyyppi hakutoimisto = kuvailevatTiedot.getHakutoimisto();

            //ECTS coordinator
            YhteyshenkiloTyyppi henkilo = hakutoimisto.getEctsYhteyshenkilo();
            if (henkilo != null) {
                PersonType person = new PersonType();
                person.setEmail(henkilo.getEmail());
                person.setFullName(henkilo.getKokoNimi());
                person.setOidRef(henkilo.getOid());
                person.setPersonType(PersonSchemeType.ECTS_COORDINATOR);
                person.setPhoneNumber(henkilo.getPuhelin());
                person.setTitle(henkilo.getTitteli());
                institutionInfo.setEctsCoordinator(person);
            }
            copyTexts(hakutoimisto.getOpintotoimistoNimi(), generalInformation.getAdmissionOfficeName());
            
            kuvailevatTiedot.getHakutoimisto().getOpintotoimistoYhteystiedot();
            for (final YhteystietoDTO yhtDto : hakutoimisto.getOpintotoimistoYhteystiedot()) {
                ProviderAddressType pat = new ProviderAddressType();
                if (yhtDto instanceof EmailDTO) {
                    EmailDTO emailDto = (EmailDTO) yhtDto;
                    if (emailDto.getEmail() != null) {
                        pat.setEmailAddress(emailDto.getEmail());
                    }
                    
                } else if (yhtDto instanceof PuhelinnumeroDTO) {
                    PuhelinnumeroDTO p = (PuhelinnumeroDTO) yhtDto;
                    pat.setPhoneNumber(p.getPuhelinnumero());
                    switch (p.getTyyppi()) {
                        case FAKSI:
                            pat.setScheme(AddressInfoSchemeType.FAX);
                            break;
                        case PUHELIN:
                            pat.setScheme(AddressInfoSchemeType.PHONE);
                            break;
                    }
                } else if (yhtDto instanceof OsoiteDTO) {
                    OsoiteDTO osoiteDto = (OsoiteDTO) yhtDto;

                    //address information
                    pat.getAddressLine().add(osoiteDto.getOsoite());
                    pat.getAddressLine().add(osoiteDto.getExtraRivi());
                    pat.setCity(osoiteDto.getPostitoimipaikka());
                    pat.setPostalCode(osoiteDto.getPostinumero());
                    pat.setCountry(osoiteDto.getMaa());
                    
                    if (osoiteDto.getOsoiteTyyppi() != null) {
                        switch (osoiteDto.getOsoiteTyyppi()) {
                            case KAYNTI:
                                pat.setScheme(AddressInfoSchemeType.ADMISSION_OFFICE_ENTRANCE);
                                break;
                            case POSTI:
                                pat.setScheme(AddressInfoSchemeType.ADMISSION_OFFICE_POST_ADDRESS);
                                break;
                            case MUU:
                                pat.setScheme(AddressInfoSchemeType.OTHER);
                                break;
                        }
                    }
                    
                } else if (yhtDto instanceof WwwDTO) {
                    //skip, the DTO not needed
                    continue;
                }
                
                generalInformation.getAddress().add(pat);
            }
        }

        // office name
        OrganizationRefType organizationRef = new OrganizationRefType();
        organizationRef.setOidRef(tarjoaja.getOid());
        provider.setOrganizationRef(organizationRef);
        
        copyTexts(tarjoaja.getNimi(), institutionInfo.getName());
        
        if (kuvailevatTiedot != null) {
            for (KuvailevaTietoTyyppi kuvailevat : kuvailevatTiedot.getVapaatKuvaukset()) {
                if (kuvailevat != null) {
                    TypedDescriptionType tdt = null;
                    final DescriptionType dt = lopInstitutionInformation(kuvailevat);
                    if (dt != null) {
                        tdt = typedDescriptionType(kuvailevat, dt);
                        provider.getInstitutionInfo().getDescription().add(tdt);
                    } else if (!kuvailevat.getTyyppi().equals(KuvailevaTietoTyyppiTyyppi.TIETOA_ASUMISESTA)){
                        tdt = typedDescriptionType(kuvailevat, lopGeneralInformation(kuvailevat));
                        provider.getGeneralInformation().getDescription().add(tdt);
                    }
                }
            }
            
            final List<SoMeLinkkiTyyppi> soMeLinkit = kuvailevatTiedot.getSoMeLinkit();
            
            if (!soMeLinkit.isEmpty()) {
                provider.setWebLinks(new WebLinkCollectionType());
                
                for (SoMeLinkkiTyyppi sourceLink : soMeLinkit) {
                    provider.getWebLinks().getLink().add(link(sourceLink.getTyyppi().value(), sourceLink.getSisalto()));
                }
            }
            
            final OrganisaatioKuvaTyyppi kuva = kuvailevatTiedot.getKuva();
            if (params.showImages() && kuva != null) {
                provider.setImages(new LearningOpportunityProviderType.Images());
                ImageType img = new ImageType();
                img.setFileName(kuva.getFileName());
                img.setImage(kuva.getKuva());
                img.setMimeType(kuva.getMimeType());
                provider.getImages().getImage().add(img);
            }
        }
        
        marshal(LearningOpportunityProviderType.class, provider);
    }
    
    private void addHakutapa(Haku source, ApplicationSystemType target) {
        
        target.setApplicationMethod(createCodeValue(CodeSchemeType.KOODISTO, source.getHakutapaUri()));
        
    }
    
    private void addHakukausi(Haku source, ApplicationSystemType target) {
        
        target.setApplicationSeason(createCodeValue(CodeSchemeType.KOODISTO, source.getHakukausiUri()));
        
    }
    
    private void addHakutyyppi(Haku source, ApplicationSystemType target) {
        
        target.setApplicationType(createCodeValue(CodeSchemeType.KOODISTO, source.getHakutyyppiUri()));
        
    }
    
    private void addHakuvuosi(Haku source, ApplicationSystemType target) {
        
        target.setApplicationYear(formatYear(source.getHakukausiVuosi()));
        
    }
    
    private void addKoulutuksenAlkamiskausi(Haku source, ApplicationSystemType target) {
        
        target.setEducationStartSeason(createCodeValue(CodeSchemeType.KOODISTO, source.getKoulutuksenAlkamiskausiUri()));
        
    }
    
    private void addKoulutuksenAlkamisvuosi(Haku source, ApplicationSystemType target) {
        
        target.setEducationStartYear(formatYear(source.getKoulutuksenAlkamisVuosi()));
        
    }
    
    private void addHaunTunniste(Haku source, ApplicationSystemType target) {
        
        target.setIdentifier(source.getHaunTunniste());
        
    }
    
    private void addKohdejoukko(Haku source, ApplicationSystemType target) {
        
        target.setTargetGroup(createCodeValue(CodeSchemeType.KOODISTO, source.getKohdejoukkoUri()));
        
    }
    
    private void addNimi(Koulutusmoduuli from, LearningOpportunitySpecificationType to) {
        
        copyTexts(from.getNimi(), to.getName());
        
    }
    
    private void addUlkoinenTunniste(Koulutusmoduuli from, LearningOpportunitySpecificationType to) {
        
        if (from.getUlkoinenTunniste() != null) {
            to.setIdentifier(from.getUlkoinenTunniste());
        }
        
    }
    
    private void addUlkoinenTunniste(KoulutusmoduuliToteutus from, LearningOpportunityInstanceType to) {
        
        if (from.getUlkoinenTunniste() != null) {
            to.setIdentifier(from.getUlkoinenTunniste());
        }
        
    }
    
    private void addSpecificationRef(KoulutusmoduuliToteutus from, LearningOpportunityInstanceType to) {
        
        LearningOpportunitySpecificationRefType losRef = new LearningOpportunitySpecificationRefType();
        losRef.setRef(getIDREF(from.getKoulutusmoduuli().getOid()));
        to.setSpecificationRef(losRef);
        
    }
    
    private void addOrganisaatioRef(Koulutusmoduuli from, LearningOpportunitySpecificationType to) {
        
        OrganizationRefType organizationRef = new OrganizationRefType();
        organizationRef.setOidRef(from.getOmistajaOrganisaatioOid());
        to.setOrganizationRef(organizationRef);
        
    }
    
    private void addOrganisaatioRef(KoulutusmoduuliToteutus from, LearningOpportunitySpecificationType to) {
        OrganizationRefType organizationRef = new OrganizationRefType();
        organizationRef.setOidRef(from.getTarjoaja());
        to.setOrganizationRef(organizationRef);
    }
    
    private void addOrganisaatioRef(KoulutusmoduuliToteutus from, LearningOpportunityInstanceType to) {
        
        OrganizationRefType orgRef = new OrganizationRefType();
        orgRef.setOidRef(from.getTarjoaja());
        to.setOrganizationRef(orgRef);
        
    }
    
    private void addHakukohdeNimi(Hakukohde source, ApplicationOptionType target) {
        
        target.setTitle(createCodeValue(CodeSchemeType.KOODISTO, source.getHakukohdeNimi()));
        
    }
    
    private void addTutkintonimike(Koulutusmoduuli source, LearningOpportunitySpecificationType target) {
        
        final String nimike = source.getTutkintonimike();
        if (nimike != null) {
            target.setQualification(createCodeValue(CodeSchemeType.KOODISTO, nimike));
        }
        
    }
    
    private void addKesto(KoulutusmoduuliToteutus source, LearningOpportunityInstanceType target) {
        
        final String units = source.getSuunniteltuKestoYksikko();
        final String value = source.getSuunniteltuKestoArvo();
        
        if (units != null && value != null) {
            
            EducationDurationType duration = new EducationDurationType();
            
            duration.setUnits(createCodeValue(CodeSchemeType.KOODISTO, units));
            duration.setValue(value);
            
            target.setDuration(duration);
            
        }
        
    }
    
    private void addPohjakoulutusvaatimus(KoulutusmoduuliToteutus source, LearningOpportunityInstanceType target) {
        
        target.setPrerequisite(createCodeValue(CodeSchemeType.KOODISTO, source.getPohjakoulutusvaatimus()));
        
    }
    
    private void addKoulutukset(Hakukohde hakukohde, ApplicationOptionType target) {
        
        ApplicationOptionType.LearningOpportunities refContainer = new ApplicationOptionType.LearningOpportunities();
        List<LearningOpportunityInstanceRefType> refList = refContainer.getInstanceRef();
        
        Set<KoulutusmoduuliToteutus> koulutukset = hakukohde.getKoulutusmoduuliToteutuses();
        int laskuri = 0;
        for (KoulutusmoduuliToteutus koulutus : koulutukset) {
            
            if (laskuri == 0) {
                String parentOid = this.komotoParentMap.get(koulutus.getKoulutusmoduuli().getOid() + koulutus.getTarjoaja());
                if (parentOid != null) {
                    refContainer.setParentRef(createLOSRef(parentOid));
                }
            }
            
            LearningOpportunityInstanceRefType ref = new LearningOpportunityInstanceRefType();
            ref.setRef(getIDREF(koulutus.getOid()));
            refList.add(ref);
            ++laskuri;
            
        }
        
        target.setLearningOpportunities(refContainer);
        
    }
    
    private void addLaajuus(Koulutusmoduuli source, LearningOpportunitySpecificationType target) {
        
        if (source.getLaajuusArvo() != null) {
            CreditsType credits = new CreditsType();
            credits.setUnits(createCodeValue(CodeSchemeType.KOODISTO, source.getLaajuusYksikko()));
            credits.setValue(createCodeValue(CodeSchemeType.KOODISTO, source.getLaajuusArvo()));
            target.setCredits(credits);
        }
    }
    
    private void addValintaperusteet(Hakukohde source, List<MonikielinenMetadata> valintaperustekuvaus, ApplicationOptionType target) {
        SelectionCriterionsType criterions = new SelectionCriterionsType();
        target.setSelectionCriterions(criterions);
        
        if (source.getAloituspaikatLkm() != null) {
            criterions.setStartingQuota(BigInteger.valueOf(source.getAloituspaikatLkm()));
        }
        if (source.getAlinValintaPistemaara() != null) {
            criterions.setLastYearMinScore(BigInteger.valueOf(source.getAlinValintaPistemaara()));
        }
        if (source.getYlinValintaPistemaara() != null) {
            criterions.setLastYearMaxScore(BigInteger.valueOf(source.getYlinValintaPistemaara()));
        }
        if (source.getEdellisenVuodenHakijat() != null) {
            criterions.setLastYearTotalApplicants(BigInteger.valueOf(source.getEdellisenVuodenHakijat()));
        }
        
        criterions.setDescription(new TypedDescriptionType());
        copyTexts(valintaperustekuvaus, criterions.getDescription());
        addValintakokeet(source, criterions);
        
        addLiitteet(source, criterions);
        
      
        
    }
    
    private void addValintakokeet(Hakukohde source, SelectionCriterionsType target) {
        
        Set<Valintakoe> valintakoes = source.getValintakoes();
        if (valintakoes == null || valintakoes.isEmpty()) {
            return;
        }
        
        
        
        SelectionCriterionsType.EntranceExaminations exams = new SelectionCriterionsType.EntranceExaminations();
        for (Valintakoe sourceExamination : valintakoes) {
            Examination targetExamination = new Examination();
            addValintakoe(sourceExamination, targetExamination);
            exams.getExamination().add(targetExamination);

        }
        //If the source hakukohde relates to high school education the
        //high school specific data is added (score limits, and extra evidence description)
        if (KoulutusasteTyyppi.LUKIOKOULUTUS.value().equals(getKoulutustyyppi(source))) {
        	Valintakoe lukioValintakoe = valintakoes.iterator().next();
        	//EntranceExaminations/ScoreLimits
        	addPisterajat(lukioValintakoe, exams);
        	//EntranceExaminations/ExtraEvidenceDescription
        	addLisanaytto(lukioValintakoe, exams);
        }
        target.setEntranceExaminations(exams);
    }
    
    /*
     * Add extra evidence description. Used in high school application option.
     */
    private void addLisanaytto(Valintakoe source, SelectionCriterionsType.EntranceExaminations target) {
		target.setExtraEvidenceDescription(new TypedDescriptionType());
        copyDescriptions(source.getLisanaytot(), target.getExtraEvidenceDescription());
	}

    /*
     * Add score limits. Used in high school application option.
     */
	private void addPisterajat(Valintakoe source, SelectionCriterionsType.EntranceExaminations target) {
		ScoreLimitsType limits = new ScoreLimitsType();
		for(Pisteraja curPisteraja : source.getPisterajat()) {
			if (curPisteraja.getValinnanPisterajaTyyppi().equals(ValinnanPisterajaTyyppi.PAASYKOE.value())) {
				limits.setExaminationMaxScore(curPisteraja.getYlinPistemaara());
				limits.setExaminationMinScore(curPisteraja.getAlinPistemaara());
				limits.setExaminationMinApplicableScore(curPisteraja.getAlinHyvaksyttyPistemaara());
			} else if (curPisteraja.getValinnanPisterajaTyyppi().equals(ValinnanPisterajaTyyppi.LISAPISTEET.value())) {
				limits.setExtraPointsMaxScore(curPisteraja.getYlinPistemaara());
				limits.setExtraPointsMinScore(curPisteraja.getAlinPistemaara());
				limits.setExtraPointsMinApplicableScore(curPisteraja.getAlinHyvaksyttyPistemaara());
			} else if (curPisteraja.getValinnanPisterajaTyyppi().equals(ValinnanPisterajaTyyppi.KOKONAISPISTEET.value())) {
				limits.setOverallMinApplicableScore(curPisteraja.getAlinHyvaksyttyPistemaara());
			}
		}
		target.setScoreLimits(limits);
	}
    
    private String getKoulutustyyppi(Hakukohde hakukohde) {
    	String koulutustyyppi = null;
        if (!hakukohde.getKoulutusmoduuliToteutuses().isEmpty()) {
        	koulutustyyppi = hakukohde.getKoulutusmoduuliToteutuses().iterator().next().getKoulutusmoduuli().getKoulutustyyppi();
        }
		return koulutustyyppi;
	}

	private void addLiitteet(Hakukohde source, SelectionCriterionsType target) {
        
        Set<HakukohdeLiite> liitteet = source.getLiites();
        if (liitteet == null || liitteet.isEmpty()) {
            return;
        }
        
        AttachmentCollectionType attachmentContainer = new AttachmentCollectionType();
        List<Attachment> attachments = attachmentContainer.getAttachment();
        
        for (HakukohdeLiite liite : liitteet) {
            
            Attachment attachment = new Attachment();
            
            attachment.setType(createCodeValue(CodeSchemeType.KOODISTO, liite.getLiitetyyppi()));
            TypedDescriptionType description = new TypedDescriptionType();
            description.setType(DescriptionType.GENERAL.value());
            copyDescriptions(liite.getKuvaus(), description);
            attachment.setDescription(description);
            
            Attachment.Return returnSpec = new Attachment.Return();
            returnSpec.setDueDate(liite.getErapaiva());
            Attachment.Return.To returnTo = new Attachment.Return.To();
            
            if (liite.getSahkoinenToimitusosoite() != null) {
                // could be anyUri???
                returnTo.setEmailAddress(liite.getSahkoinenToimitusosoite());
            }
            Osoite toimitusosoite = liite.getToimitusosoite();
            
            if (toimitusosoite != null) {
                PostalAddress postTo = new PostalAddress();
                postTo.getAddressLine().add(toimitusosoite.getOsoiterivi1());
                postTo.getAddressLine().add(toimitusosoite.getOsoiterivi2());
                postTo.setCity(toimitusosoite.getPostitoimipaikka());
                postTo.setPostalCode(toimitusosoite.getPostinumero());
                returnTo.setPostalAddress(postTo);
            }
            
            returnSpec.setTo(returnTo);
            attachment.setReturn(returnSpec);
            
            attachments.add(attachment);
            
        }
        
        target.setAttachments(attachmentContainer);
        
    }
    
    private void addValintakoe(Valintakoe source, Examination target) {
        // ApplicationOption/.../Examination/ExaminationType
        target.setExaminationType(createCodeValue(CodeSchemeType.KOODISTO, source.getTyyppiUri()));

        // ApplicationOption/.../Examination/Description
        target.setDescription(new TypedDescriptionType());
        copyDescriptions(source.getKuvaus(), target.getDescription());
        
        Set<ValintakoeAjankohta> ajankohtas = source.getAjankohtas();
        if (ajankohtas != null && !ajankohtas.isEmpty()) {
            List<ExaminationEventType> events = target.getExaminationEvent();
            
            for (ValintakoeAjankohta sourceAjankohta : ajankohtas) {
                // ApplicationOption/.../Examination/ExaminationEvent
                ExaminationEventType event = new ExaminationEventType();
                event.setStart(sourceAjankohta.getAlkamisaika());
                event.setEnd(sourceAjankohta.getPaattymisaika());
                
                if (sourceAjankohta.getAjankohdanOsoite() != null) {
                    ExaminationEventType.Locations locations = new ExaminationEventType.Locations();
                    event.setLocations(locations);
                    
                    ExaminationLocationType location = new ExaminationLocationType();
                    location.setName(sourceAjankohta.getLisatietoja());
                    location.getAddressLine().add(sourceAjankohta.getAjankohdanOsoite().getOsoiterivi1());
                    location.getAddressLine().add(sourceAjankohta.getAjankohdanOsoite().getOsoiterivi2());
                    location.setCity(sourceAjankohta.getAjankohdanOsoite().getPostitoimipaikka());
                    location.setPostalCode(sourceAjankohta.getAjankohdanOsoite().getPostinumero());
                    locations.getLocation().add(location);
                }
                
                events.add(event);
            }
        }
    }
    
    private void addHakukelpoisuusvaatimus(List<MonikielinenMetadata> source, ApplicationOptionType target) {
        EligibilityRequirementsType eligibilityRequirements = new EligibilityRequirementsType();
        eligibilityRequirements.setDescription(new TypedDescriptionType());
        copyTexts(source, eligibilityRequirements.getDescription());
        target.setEligibilityRequirements(eligibilityRequirements);
    }

    /**
     * Marshals data fragment into underlying stream.
     *
     * @param <T> type of element to write
     * @param elementClass
     * @param data fragment to write
     * @throws JAXBException
     * @throws XMLStreamException
     */
    private <T> void marshal(Class<T> elementClass, T data) throws JAXBException, XMLStreamException {

        // class name with out the Type postfix
        String name = elementClass.getSimpleName();
        name = name.substring(0, name.length() - 4);
        
        marshal(name, elementClass, data);
        
    }
    
    private void marshal(String elementName, Class elementClass, Object data) throws JAXBException, XMLStreamException {
        
        QName qname = new QName("", elementName);
        JAXBElement e = new JAXBElement(qname, elementClass, data);
        marshaller.marshal(e, xmlWriter);
        
        xmlWriter.flush();
        
    }
    
    private static ExtendedStringType createExtendedString(String valueFI) {
        
        ExtendedStringType s = new ExtendedStringType();
        s.setLang("fi");
        s.setValue(valueFI);
        return s;
        
    }
    
    private static ExtendedStringType createExtendedString(String value, String langKey) {
        if (langKey == null) {
            log.warn("Data was missing language key. Value : {}, lang : {}", value, langKey);
            return new ExtendedStringType();
        }
        
        ExtendedStringType s = new ExtendedStringType();
        s.setLang(langKey.toLowerCase());
        s.setValue(value);
        return s;
        
    }
    
    private void addLinkit(KoulutusmoduuliToteutus toteutus, LearningOpportunityInstanceType target) {
        
        Set<WebLinkki> sourceLinkkis = toteutus.getLinkkis();
        if (sourceLinkkis != null && !sourceLinkkis.isEmpty()) {
            
            WebLinkCollectionType linkContainer = new WebLinkCollectionType();
            target.setWebLinks(linkContainer);
            
            List<Link> targetLinks = linkContainer.getLink();
            
            for (WebLinkki sourceLink : sourceLinkkis) {
                targetLinks.add(link(sourceLink.getTyyppi(), sourceLink.getUrl()));
            }
            
        }
        
    }
    
    private void addMaksullisuus(KoulutusmoduuliToteutus toteutus, LearningOpportunityInstanceType target) {
        // TODO maksullisuus on koodisto url ...
    }
    
    private void addLoppukoeVaatimukset(KoulutusmoduuliToteutus toteutus, LearningOpportunityInstanceType target) {
        
        MonikielinenTeksti teksti = toteutus.getLoppukoeVaatimukset();
        if (teksti != null && !teksti.getTekstis().isEmpty()) {
            FinalExaminationCollectionType examinations = new FinalExaminationCollectionType();
            target.setFinalExamination(examinations);
            
            copyDescriptions(teksti, examinations.getDescription());
        }
        
    }
    
    private void addArviointikriteerit(KoulutusmoduuliToteutus toteutus, LearningOpportunityInstanceType target) {
        
        MonikielinenTeksti tekstis = toteutus.getArviointikriteerit();
        
        if (tekstis != null && !tekstis.getTekstis().isEmpty()) {
            AssessmentCollectionType assessments = new AssessmentCollectionType();
            copyTexts(tekstis, assessments.getAssessment());
        }
        
    }
    
    private void addOpetusmuodot(KoulutusmoduuliToteutus toteutus, LearningOpportunityInstanceType target) {
        
        CodeValueCollectionType collection = toCodeValueCollection(toteutus.getOpetusmuotos());
        target.setFormsOfTeaching(collection);
        
    }
    
    private void addKoulutuslajit(KoulutusmoduuliToteutus toteutus, LearningOpportunityInstanceType target) {
        
        CodeValueCollectionType collection = toCodeValueCollection(toteutus.getKoulutuslajis());
        target.setFormOfEducation(collection);
        
    }
    
    private void addOpetuskielet(KoulutusmoduuliToteutus toteutus, LearningOpportunityInstanceType target) {
        
        CodeValueCollectionType collection = toCodeValueCollection(toteutus.getOpetuskielis());
        target.setLanguagesOfInstruction(collection);
        
    }
    
    private void addKoulutuksenRakenne(Koulutusmoduuli moduuli, Description target) {
        
        copyTexts(moduuli.getKoulutuksenRakenne(), target.getStructureDiagram());
        
    }
    
    private void addJatkoOpintoMahdollisuudet(Koulutusmoduuli moduuli, Description target) {
        
        copyTexts(moduuli.getJatkoOpintoMahdollisuudet(), target.getAccessToFurtherStudies());
        
    }

    /**
     * Helper method that copies all translated texts from the source format to
     * target format. Null source object is silently ignored.
     *
     * @param source
     * @param target
     */
    private void copyTexts(MonikielinenTekstiTyyppi source, List<ExtendedStringType> target) {
        
        if (source != null) {
            for (Teksti teksti : source.getTeksti()) {
                target.add(createExtendedString(teksti.getValue(), teksti.getKieliKoodi()));
            }
        }
    }
    
    private void copyTexts(Collection<MonikielinenMetadata> source, TypedDescriptionType target) {
        
        if (source != null && target != null) {
            target.setType(DescriptionType.GENERAL.value());
            for (MonikielinenMetadata teksti : source) {
                target.getText().add(createExtendedString(teksti.getArvo(), teksti.getKieli()));
            }
        }
    }
    
    private void copyTexts(MonikielinenTeksti source, List<ExtendedStringType> target) {
        
        if (source != null) {
            for (TekstiKaannos teksti : source.getTekstis()) {
                target.add(createExtendedString(teksti.getArvo(), teksti.getKieliKoodi()));
            }
        }
    }
    
    private void copyDescriptions(MonikielinenTeksti source, TypedDescriptionType target) {
        
        if (source != null && target != null) {
            target.setType(DescriptionType.GENERAL.value());
            for (TekstiKaannos teksti : source.getTekstis()) {
                target.getText().add(createExtendedString(teksti.getArvo(), teksti.getKieliKoodi()));
            }
        }
    }

    /**
     * Helper method that converts Koodisto uri's to codes. If input set is null
     * or empty, null is returned.
     *
     * @param uris
     * @return
     */
    private static CodeValueCollectionType toCodeValueCollection(Set<KoodistoUri> uris) {
        
        if (uris == null || uris.isEmpty()) {
            return null;
        }
        
        CodeValueCollectionType collection = new CodeValueCollectionType();
        collection.setCodes(new CodeValueCollectionType.Codes());
        collection.setScheme(CodeSchemeType.KOODISTO);
       
        for (KoodistoUri uri : uris) {  
            collection.getCodes().getCode().add(createExtendedCodeValue(uri.getKoodiUri()));
        }
        return collection;
        
    }
    
    private void addAmmattinimikkeet(KoulutusmoduuliToteutus toteutus, LearningOpportunityInstanceType target) {
        
        final Set<KoodistoUri> uris = toteutus.getAmmattinimikes();
        if (uris != null && !uris.isEmpty()) {
            
            ProfessionCollectionType professions = new ProfessionCollectionType();
            List<CodeValueType> list = professions.getProfession();
            
            for (KoodistoUri uri : uris) {
                list.add(createCodeValue(CodeSchemeType.KOODISTO, uri.getKoodiUri()));
            }
            
            target.setProfessionLabels(professions);
            
        }
        
    }
    
    private void addAvainsanat(KoulutusmoduuliToteutus toteutus, LearningOpportunityInstanceType target) {
        
        final Set<KoodistoUri> uris = toteutus.getAvainsanas();
        if (uris != null && !uris.isEmpty()) {
            
            LearningOpportunityInstanceType.Keywords keywords = new LearningOpportunityInstanceType.Keywords();
            List<CodeValueType> list = keywords.getKeyword();
            
            for (KoodistoUri uri : uris) {
                list.add(createCodeValue(CodeSchemeType.KOODISTO, uri.getKoodiUri()));
            }
            
            target.setKeywords(keywords);
            
        }
        
    }
    
    private void addKoulutusala(Koulutusmoduuli koulutusmoduuli, Classification target) {
        
        final String uri = koulutusmoduuli.getKoulutusala();
        if (uri != null) {
            target.setEducationDomain(createCodeValue(CodeSchemeType.KOODISTO, uri));
        }
        
    }
    
    private void addKoulutusluokitus(Koulutusmoduuli koulutusmoduuli, Classification target) {
        
        final String uri = koulutusmoduuli.getKoulutusKoodi();
        if (uri != null) {
            target.setEducationClassification(createCodeValue(CodeSchemeType.KOODISTO, uri));
        }
        
    }
    
    private void addKoulutusaste(Koulutusmoduuli koulutusmoduuli, Classification target) {
        
        final String uri = koulutusmoduuli.getKoulutusAste();
        if (uri != null) {
            target.setEducationDegree(createCodeValue(CodeSchemeType.KOODISTO, uri));
        }
        
    }
    
    private void addOpintoala(Koulutusmoduuli koulutusmoduuli, Classification target) {
        
        final String uri = koulutusmoduuli.getKoulutusala();
        if (uri != null) {
            target.setStudyDomain(createCodeValue(CodeSchemeType.KOODISTO, uri));
        }
        
    }
    
    private void addEqf(Koulutusmoduuli koulutusmoduuli, Classification target) {
        
        final String uri = koulutusmoduuli.getEqfLuokitus();
        if (uri != null) {
            target.setEqfClassification(createCodeValue(CodeSchemeType.KOODISTO, uri));
        }
        
    }
    
    private void addNqf(Koulutusmoduuli koulutusmoduuli, Classification target) {
        
        final String uri = koulutusmoduuli.getNqfLuokitus();
        if (uri != null) {
            target.setNqfClassification(createCodeValue(CodeSchemeType.KOODISTO, uri));
        }
        
    }
    
    private static String formatYear(Integer year) {
        
        if (year == null) {
            return null;
        }
        
        return year.toString();
    }

    /**
     * Formats year with four digits.
     *
     * @param date
     * @return
     */
    private static String formatAcademicYear(Date date) {
        
        return new SimpleDateFormat("yyyy").format(date);
        
    }

    /**
     * Creates CodeValueType by setting it's scheme and code's value. If value
     * is null, null is returned.
     *
     * @param scheme
     * @param codeUriWithVersion
     * @return
     */
    private static CodeValueType createCodeValue(CodeSchemeType scheme, String codeUriWithVersion) {
        
        if (codeUriWithVersion == null) {
            return null;
        }
        VersionedUri vuri = VersionedUri.parse(codeUriWithVersion);
        Code code = new Code();
        if (scheme != null) {
            code.setScheme(scheme);
        }
        code.setUri(vuri.getUri()); // always Koodisto service uri
        code.setVersion(vuri.getVersio() != null ? vuri.getVersio().toString() : "");
        CodeValueType codeValueType = new CodeValueType();
        codeValueType.setCode(code);
        
        return codeValueType;
        
    }

    private static ExtendedCodeLabelType createExtendedCodeValue(String codeUriWithVersion) {
        
        if (codeUriWithVersion == null) {
            return null;
        }
        VersionedUri vuri = VersionedUri.parse(codeUriWithVersion);  
        ExtendedCodeLabelType code = new ExtendedCodeLabelType();
             
        code.setUri(vuri.getUri()); // always Koodisto service uri
        code.setVersion(vuri.getVersio() != null ? vuri.getVersio().toString() : "");
        code.setValue(codeUriWithVersion);
      
        return code;
    }

    
    /**
     * Stores element that is used as a target of IDREF later. Returns the input
     * id for method chaining.
     *
     * @param idTarget
     * @param id
     * @return
     */
    private String putID(String id, Object idTarget) {
        
        idRefMap.put(id, idTarget);
        return id;
        
    }

    /**
     * Returns element that has been stored before using {@link #putID(java.lang.Object, java.lang.String)
     * }.
     *
     * @exception IllegalArgumentException if not such id is found
     * @param key
     * @return
     */
    private Object getIDREF(String key) {
        
        Object value = idRefMap.get(key);
        if (value == null) {
            throw new IllegalStateException("no such mapped ID " + key);
        }
        return value;
    }

    /**
     * Translates enumeration used in Organisation -service to constants know by
     * the Publication API. If there is no proper mapping, input parameters
     * value as string is returned instead.
     */
    private static DescriptionType lopGeneralInformation(KuvailevaTietoTyyppi metaKey) {
        switch (metaKey.getTyyppi()) {
            
            case KANSAINVALISET_KOULUTUSOHJELMAT:
                return DescriptionType.INTERNATIONAL_PROGRAMMES;
            case KIELIOPINNOT:
                return DescriptionType.LANGUAGE_COURSES;
            case KUSTANNUKSET:
                return DescriptionType.COST_OF_LIVING;
            case OPISKELIJALIIKKUVUUS:
                //general information for mobile students
                return DescriptionType.MOBILE_STUDENTS;
            case OPISKELIJALIIKUNTA:
                return DescriptionType.SPORTS_FACILITIES;
            case OPISKELIJARUOKAILU:
                return DescriptionType.MEALS;
            case OPISKELIJA_JARJESTOT:
                return DescriptionType.STUDENT_ASSOCIATIONS;
            case OPPIMISYMPARISTO:
                return DescriptionType.STUDY_FACILITIES;
            case RAHOITUS:
                return DescriptionType.FINANCIAL_SUPPORT;
            case TERVEYDENHUOLTOPALVELUT:
                return DescriptionType.MEDICAL_FACILITIES;
            case TYOHARJOITTELU:
                return DescriptionType.INTERNSHIPS;
            case VAKUUTUKSET:
                return DescriptionType.INSURANCE;
            case VALINTAMENETTELY:
                return DescriptionType.ADMISSION_PROCEDURES;
            case VAPAA_AIKA:
                return DescriptionType.EXTRA_MURAL_AND_LEISURE_FACILITIES;
            case YLEISKUVAUS:
                return DescriptionType.GENERAL;
            case ESTEETOMYYS:
                return DescriptionType.FACILITIES_FOR_STUDENTS_WITH_SPECIAL_NEEDS;
        }
        return null;
    }

    /**
     * Translates enumeration used in Organisation -service to constants know by
     * the Publication API. If there is no proper mapping, input parameters
     * value as string is returned instead.
     */
    private static DescriptionType lopInstitutionInformation(KuvailevaTietoTyyppi metaKey) {
        switch (metaKey.getTyyppi()) {
            case VALINTAMENETTELY:
                return DescriptionType.ADMISSION_PROCEDURES;
            case VASTUUHENKILOT:
                return DescriptionType.ACADEMIC_AUTHORITIES;
            case VUOSIKELLO:
                return DescriptionType.ACADEMIC_CALENDAR;
            case AIEMMIN_HANKITTU_OSAAMINEN:
                return DescriptionType.MAIN_UNIVERSITY_REQULATIONS;
            case YLEISKUVAUS:
                return DescriptionType.GENERAL;
        }
        return null;
    }
    
    private static TypedDescriptionType typedDescriptionType(KuvailevaTietoTyyppi tyyppi, DescriptionType metaType) {
        TypedDescriptionType tdt = new TypedDescriptionType();
        
        List<Teksti> tekstis = tyyppi.getSisalto().getTeksti();
        for (Teksti teksti : tekstis) {
            tdt.getText().add(createExtendedString(teksti.getValue(), teksti.getKieliKoodi()));
        }
        
        tdt.setType(metaType.value());
        return tdt;
    }
    
    private static Link link(String type, String url) {
        Link targetLink = new Link();
        targetLink.setType(type);
        targetLink.setUri(url);
        return targetLink;
    }
    
    private static StatusSchemeType status(final TarjontaTila tila) {
        switch (tila) {
            case JULKAISTU:
                return StatusSchemeType.PUBLISHED;
            case PERUTTU:
                return StatusSchemeType.CANCELLED;
            default:
                return StatusSchemeType.UNKNOWN;
        }
    }
}
