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
package fi.vm.sade.tarjonta.servlet;

import fi.vm.sade.tarjonta.publication.enricher.XMLStreamEnricher;
import fi.vm.sade.tarjonta.publication.enricher.factory.LearningOpportunityDataEnricherFactory;
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.xml.sax.SAXException;

/**
 * Tarjonta XML export file needs enriching before it can be used to
 * display any human-readable values. This process should take place in
 * ESB. Since that environment was not available, this filter was created
 * to simulate such process. Do not use in production environment.
 *
 * @author Jukka Raanamo
 */
public class PublicationEnrichingFilter implements Filter {

    private FilterConfig filterConfig;

    @Autowired
    private LearningOpportunityDataEnricherFactory enricherFactory;

    private static final Logger log = LoggerFactory.getLogger(PublicationEnrichingFilter.class);

    @Override
    public void init(FilterConfig config) throws ServletException {
        filterConfig = config;
    }

    @Override
    public void destroy() {
        filterConfig = null;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
        throws IOException, ServletException {

        if ((request instanceof HttpServletRequest) == false) {
            throw new ServletException("only http requests accepted");
        }

        HttpServletResponse httpResponse = (HttpServletResponse) response;

        BufferedHttpResponseWrapper responseWrapper = new BufferedHttpResponseWrapper(httpResponse);
        chain.doFilter(request, responseWrapper);

        byte[] responseData = responseWrapper.getBuffer();
        if (responseData == null || responseData.length == 0) {
            // browser refresh - request recycled?
            chain.doFilter(request, response);
            return;
        }

        // add Base64 encoded MD5 checksum of the content for additional cache control support
        //TODO: fix it does not build says that method is not found, maybe some depency problem
        //httpResponse.setHeader("Content-MD5", Base64.encodeBase64String(DigestUtils.md5(responseData)));

        OutputStream out = response.getOutputStream();
        ByteArrayInputStream in = new ByteArrayInputStream(responseData);

        try {

            response.setContentType("application/xml");
            enrichData(in, out);
            response.flushBuffer();

        } catch (SAXException e) {
            throw new ServletException(e);
        }

    }

    private void enrichData(InputStream in, OutputStream out) throws SAXException, IOException {

        // using factory to create
        XMLStreamEnricher enricher = enricherFactory.getObject();
        enricher.setInput(in);
        enricher.setOutput(out);

        enricher.process();

    }

    private static class BufferedHttpResponseWrapper extends HttpServletResponseWrapper {

        private BufferedServletOutputStream bufferedOut = new BufferedServletOutputStream();

        private PrintWriter writer;

        private ServletOutputStream outputStream;

        public BufferedHttpResponseWrapper(HttpServletResponse response) {
            super(response);
        }

        public byte[] getBuffer() {
            return bufferedOut.getBytes();
        }

        @Override
        public PrintWriter getWriter() throws IOException {

            if (outputStream != null) {
                throw new IllegalStateException("getOutputStream has already been called");
            }

            if (writer == null) {
                writer = new PrintWriter(bufferedOut);
            }

            return writer;
        }

        @Override
        public ServletOutputStream getOutputStream() throws IOException {

            if (writer != null) {
                throw new IllegalStateException("getWriter has already been called");
            }

            if (outputStream == null) {
                outputStream = bufferedOut;
            }

            return outputStream;

        }

        @Override
        public void flushBuffer() throws IOException {

            if (writer != null) {
                writer.flush();
            } else if (outputStream != null) {
                outputStream.flush();
            }

        }

        @Override
        public int getBufferSize() {
            return bufferedOut.getBytes().length;
        }

        @Override
        public void reset() {
            bufferedOut.reset();
        }

        @Override
        public void resetBuffer() {
            reset();
        }

        @Override
        public void setBufferSize(int size) {
            bufferedOut.setBufferSize(size);
        }

    }


    private static class BufferedServletOutputStream extends ServletOutputStream {

        private ByteArrayOutputStream bos = new ByteArrayOutputStream();

        public byte[] getBytes() {
            return bos.toByteArray();
        }

        @Override
        public void write(int i) throws IOException {
            bos.write(i);
        }

        public void reset() {
            bos.reset();
        }

        public void setBufferSize(int size) {
            bos = new ByteArrayOutputStream(size);
        }

    }


}

