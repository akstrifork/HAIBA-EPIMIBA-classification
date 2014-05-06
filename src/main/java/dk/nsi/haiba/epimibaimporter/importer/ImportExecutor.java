/**
 * The MIT License
 *
 * Original work sponsored and donated by National Board of e-Health (NSI), Denmark
 * (http://www.nsi.dk)
 *
 * Copyright (C) 2011 National Board of e-Health (NSI), Denmark (http://www.nsi.dk)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do
 * so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package dk.nsi.haiba.epimibaimporter.importer;

import java.util.Collection;
import java.util.Comparator;
import java.util.Date;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;

import dk.nsi.haiba.epimibaimporter.dao.ClassificationCheckDAO;
import dk.nsi.haiba.epimibaimporter.dao.DefaultClassificationCheckDAOColumnMapper;
import dk.nsi.haiba.epimibaimporter.dao.HAIBADAO;
import dk.nsi.haiba.epimibaimporter.email.EmailSender;
import dk.nsi.haiba.epimibaimporter.log.Log;
import dk.nsi.haiba.epimibaimporter.status.CurrentImportProgress;
import dk.nsi.haiba.epimibaimporter.status.ImportStatusRepository;
import dk.nsi.haiba.epimibaimporter.ws.EpimibaWebserviceClient;
import dk.nsi.stamdata.jaxws.generated.Answer;

/*
 * Scheduled job, responsible for fetching new data from LPR, then send it to the RulesEngine for further processing
 */
public class ImportExecutor {
    private static Log log = new Log(Logger.getLogger(ImportExecutor.class));

    private boolean manualOverride;

    @Autowired
    private HAIBADAO haibaDao;

    @Autowired
    private ImportStatusRepository statusRepo;

    @Autowired
    private EpimibaWebserviceClient epimibaWebserviceClient;

    @Autowired
    private EmailSender emailSender;

    @Autowired
    private ClassificationCheckDAO classificationCheckDAO;

    @Autowired
    private CurrentImportProgress currentImportProgress;

    @Value("${import.testpatients:false}")
    private boolean allowTestPt;

    @Scheduled(cron = "${cron.import.job}")
    public void run() {
        if (!isManualOverride()) {
            log.debug("Running Importer: " + new Date().toString());
            doProcess(false);
        } else {
            log.debug("Importer must be started manually");
        }
    }

    /*
     * Separated into its own method for testing purpose, because testing a scheduled method isn't good
     */
    public void doProcess(boolean manual) {
        // Fetch new records from LPR contact table
        try {
            statusRepo.importStartedAt(new DateTime());
            if (manual) {
                emailSender.sendHello();
            }
            currentImportProgress.reset();
            
            // import into tab tables
            // update tab tables first, in order to copy proper values into location/classification tables for used
            // values
            currentImportProgress.addStatusLine("importing and storing analysis data");
            haibaDao.clearAnalysisTable();
            haibaDao.saveAnalysis(epimibaWebserviceClient.getClassifications("Analysis"));

            currentImportProgress.addStatusLine("importing and storing investigation data");
            haibaDao.clearInvestigationTable();
            haibaDao.saveInvestigation(epimibaWebserviceClient.getClassifications("Investigation"));

            currentImportProgress.addStatusLine("importing and storing labsection data");
            haibaDao.clearLabSectionTable();
            haibaDao.saveLabSection(epimibaWebserviceClient.getClassifications("LabSection"));

            currentImportProgress.addStatusLine("importing and storing locations data");
            haibaDao.clearLocationTable();
            haibaDao.saveLocation(epimibaWebserviceClient.getClassifications("Locations"));

            currentImportProgress.addStatusLine("importing and storing organization data");
            haibaDao.clearOrganizationTable();
            haibaDao.saveOrganization(epimibaWebserviceClient.getClassifications("Organization"));

            currentImportProgress.addStatusLine("importing and storing microorganism data");
            haibaDao.clearMicroorganismTable();
            haibaDao.saveMicroorganism(epimibaWebserviceClient.getClassifications("Microorganism"));
            
            currentImportProgress.addStatusLine("checking for new alnr/banr");
            Collection<String> alnrInNewAnswers = haibaDao.getAllAlnr();
            Collection<String> banrInNewAnswers = haibaDao.getAllBanr();
            checkAndSendEmailOnNewImports(alnrInNewAnswers, banrInNewAnswers);
            currentImportProgress.addStatusLine("done");
            if (manual) {
                emailSender.sendDone(null);
            }
            statusRepo.importEndedWithSuccess(new DateTime());
        } catch (Exception e) {
            log.error("", e);
            statusRepo.importEndedWithFailure(new DateTime(), e.getMessage());
            if (manual) {
                emailSender.sendDone(ExceptionUtils.getStackTrace(e));
            }
        }
    }

    private void checkAndSendEmailOnNewImports(Collection<String> alnrInNewAnswers, Collection<String> banrInNewAnswers) {
        Collection<String> unknownBanrSet = classificationCheckDAO.checkClassifications(banrInNewAnswers, "Banr",
                new MyBanrClassificationCheckMapper());
        Collection<String> unknownAlnrSet = classificationCheckDAO.checkClassifications(alnrInNewAnswers, "Alnr",
                new MyAlnrClassificationCheckMapper());

        if (!unknownBanrSet.isEmpty() || !unknownAlnrSet.isEmpty()) {
            currentImportProgress.addStatusLine("sending email about " + unknownBanrSet.size() + " new banr and "
                    + unknownAlnrSet.size() + " new alnr to " + emailSender.getTo());
            log.debug("send email about new banr=" + unknownBanrSet + " or new alnr=" + unknownAlnrSet);
            emailSender.send(unknownBanrSet, unknownAlnrSet);
        }
    }

    public boolean isManualOverride() {
        return manualOverride;
    }

    public void setManualOverride(boolean manualOverride) {
        this.manualOverride = manualOverride;
    }

    private final class MyBanrClassificationCheckMapper extends DefaultClassificationCheckDAOColumnMapper {
        public MyBanrClassificationCheckMapper() {
            super("Class_dynamic_microorganism", "Class_TabMicroorganism", new String[] { "TabmicroorganismId", "Banr",
                    "Text" }, "Banr");
        }
    }

    private final class MyAlnrClassificationCheckMapper extends DefaultClassificationCheckDAOColumnMapper {
        public MyAlnrClassificationCheckMapper() {
            super("Class_dynamic_location", "Class_TabLocation", new String[] { "TabLocationId", "Alnr", "Text" }, "Alnr");
        }
    }

    public static class SortAnswersByTransactionIdComparator implements Comparator<Answer> {
        @Override
        public int compare(Answer a1, Answer a2) {
            return a1.getTransactionID().compareTo(a2.getTransactionID());
        }
    }
}
