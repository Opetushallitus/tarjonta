package fi.vm.sade.tarjonta.service.search;

import java.io.File;
import java.util.Enumeration;

import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.apache.solr.servlet.SolrDispatchFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.base.Preconditions;

@Component("solrFilter")
public class TarjontaSolrFilter extends SolrDispatchFilter {


    @Autowired
    private SolrConfigBean solrConfig;
    
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void init(final FilterConfig config) throws ServletException {
        
        System.out.println(config.getInitParameter("path-prefix") + "\n\n\n\n");
        if (solrConfig.getStart()) {
            logger.info("Per configuration Solr is going to be started...");
            init();
            System.setProperty("solr.data.dir", solrConfig.getSolrDataDir());
            System.setProperty("solr.solr.home", solrConfig.getSolrHome());
            super.init(new FilterConfig(){@Override
            public String getFilterName() {
                return null;
            }

            @Override
            public String getInitParameter(String arg0) {
                return "/solr";
            }

            @Override
            public Enumeration<String> getInitParameterNames() {
                return null;
            }

            @Override
            public ServletContext getServletContext() {
                return config.getServletContext();
            }});
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
//        Preconditions.checkArgument(f.isAbsolute(),
//                "Directory %s (%s) is not a directory", dir, f
//                        .getAbsoluteFile().toString());
    }

}
