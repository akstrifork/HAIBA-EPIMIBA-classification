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
package dk.nsi.haiba.epimibaimporter.dao.impl;

import java.util.Collection;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import dk.nsi.haiba.epimibaimporter.dao.CommonDAO;
import dk.nsi.haiba.epimibaimporter.dao.HAIBADAO;
import dk.nsi.haiba.epimibaimporter.log.Log;

@Transactional("haibaTransactionManager")
public class HAIBADAOImpl extends CommonDAO implements HAIBADAO {
    private static Log log = new Log(Logger.getLogger(HAIBADAOImpl.class));

    @Autowired
    @Qualifier("haibaJdbcTemplate")
    JdbcTemplate jdbc;

    @Value("${jdbc.haibatableprefix:}")
    private String haibaTablePrefix;

    @Override
    public Collection<String> getAllAlnr() {
        Collection<String> returnValue = null;
        log.debug("** querying for Alnr");
        String sql = "SELECT DISTINCT Alnr FROM " + haibaTablePrefix + "Header";
        returnValue = jdbc.queryForList(sql, String.class);
        log.trace("getAllAlnr returns " + returnValue);
        return returnValue;
    }

    @Override
    public Collection<String> getAllBanr() {
        Collection<String> returnValue = null;
        log.debug("** querying for Banr");
        String sql = "SELECT DISTINCT Banr FROM " + haibaTablePrefix + "Isolate";
        returnValue = jdbc.queryForList(sql, String.class);
        log.trace("getAllBanr returns " + returnValue);
        return returnValue;
    }
}
