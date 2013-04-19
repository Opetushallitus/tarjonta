package fi.vm.sade.tarjonta.service.search;

import java.io.File;

import javax.servlet.FilterConfig;
import javax.servlet.ServletException;

import org.apache.solr.servlet.SolrDispatchFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.google.common.base.Preconditions;

public class TarjontaSolrFilter extends SolrDispatchFilter {


    private SolrConfigBean solrConfig;
    
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void init(FilterConfig config) throws ServletException {
        WebApplicationContext springContext = WebApplicationContextUtils
                .getWebApplicationContext(config.getServletContext());
        solrConfig = springContext.getBean(SolrConfigBean.class);
        
        if (solrConfig.getStart()) {
            logger.info("Per configuration Solr is going to be started...");
            init();
            System.setProperty("solr.data.dir", solrConfig.getSolrDataDir());
            System.setProperty("solr.solr.home", solrConfig.getSolrHome());
            super.init(config);
            System.clearProperty("solr.data.dir");
            System.clearProperty("solr.solr.home");
        } else {
            logger.info("Per configuration Solr is not going to be started...");
        }
    }

    private void init() {
        Preconditions
                .checkNotNull(solrConfig.getSolrDataDir(),
                        "You must configure 'organisaatio.solr.dataDir' to point to a proper solr home");
        Preconditions
                .checkNotNull(
                        solrConfig.getSolrHome(),
                        "You must configure 'organisaatio.solr.home' to point to a proper solr data directory");
        checkDir(solrConfig.getSolrHome());
        checkDir(solrConfig.getSolrDataDir());
    }

    private void checkDir(String dir) {
        File f = new File(dir);
        Preconditions.checkArgument(f.exists(),
                "Directory %s (%s) does not exist, please create it first",
                dir, f.getAbsoluteFile().toString());
        Preconditions.checkArgument(f.canWrite(),
                "Directory %s (%s) is not writable", dir, f.getAbsoluteFile()
                        .toString());
        Preconditions.checkArgument(f.isAbsolute(),
                "Directory %s (%s) is not a directory", dir, f
                        .getAbsoluteFile().toString());
    }

}
