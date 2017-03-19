/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.blazartech.products.qotdp.data.access.impl.spring;

import com.blazartech.products.qotdp.data.DataObject;
import com.blazartech.products.qotdp.data.Quote;
import com.blazartech.products.qotdp.data.QuoteOfTheDay;
import com.blazartech.products.qotdp.data.QuoteOfTheDayHistory;
import com.blazartech.products.qotdp.data.QuoteSourceCode;
import com.blazartech.products.qotdp.data.access.QuoteOfTheDayDAL;
import com.blazartech.products.qotdp.data.access.impl.QuoteOfTheDayDALBaseImpl;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;
import javax.sql.DataSource;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

/**
 *
 * @author scott
 */
@Service
public class QuoteOfTheDayDALSpringImpl extends QuoteOfTheDayDALBaseImpl implements QuoteOfTheDayDAL {
    
    private static final Logger logger = Logger.getLogger(QuoteOfTheDayDALSpringImpl.class);
    
    @Autowired
    private JdbcTemplate databaseTemplate;

    /**
     * Get the value of databaseTemplate
     *
     * @return the value of databaseTemplate
     */
    public JdbcTemplate getDatabaseTemplate() {
        return databaseTemplate;
    }

    /**
     * Set the value of databaseTemplate
     *
     * @param databaseTemplate new value of databaseTemplate
     */
    public void setDatabaseTemplate(JdbcTemplate databaseTemplate) {
        this.databaseTemplate = databaseTemplate;
    }

    public QuoteOfTheDayDALSpringImpl(DataSource dataSource) {
        logger.info("instantiating spring DAL implementation.  Data source is of type " + dataSource);
        databaseTemplate = new JdbcTemplate(dataSource);
    }

    private int getPriorQuoteNumber(int quoteNumber) {
        return 0;
    }

    private int getNextQuoteNumber(int quoteNumber) {
        return 0;
    }

    @Override
    public Quote getQuote(int quoteNumber) {
        logger.info("getting quote #" + quoteNumber);
        String querySQL = "select * from Quote where QuoteNum = ?";
        Quote q = (Quote) getDatabaseTemplate().queryForObject(querySQL, new Object[] { quoteNumber }, new QuoteRowMapper());
        q.setNextQuoteNumber(getNextQuoteNumber(quoteNumber));
        q.setPriorQuoteNumber(getPriorQuoteNumber(quoteNumber));
        return q;
    }

    @Override
    public void updateQuote(Quote q) {
        logger.info("updating quote #" + q.getNumber());
        String querySQL = "update Quote set QuoteTxt = ?, SrcCde = ?, CanUse = ? where QuoteNum = ?";
        String canUse = q.isUsable() ? "Y" :"N";
        getDatabaseTemplate().update(querySQL, new Object[] { q.getText(), q.getSourceCode(), canUse, q.getNumber() });
    }

    @Override
    public QuoteSourceCode getQuoteSourceCode(int sourceCode) {
        logger.info("getting quote source code #" + sourceCode);
        String querySQL = "select * from SrcVal where SrcCde = ?";
        QuoteSourceCode q = (QuoteSourceCode) getDatabaseTemplate().queryForObject(querySQL, new Object[] { sourceCode }, new QuoteSourceCodeRowMapper());
        return q;
    }

    @Override
    public void updateQuoteSourceCode(QuoteSourceCode sourceCode) {
        logger.info("updating quote source code #" + sourceCode.getNumber());
        String querySQL = "update SrcVal set SrcTxt = ? where SrcCde = ?";
        getDatabaseTemplate().update(querySQL, new Object[] { sourceCode.getText(), sourceCode.getNumber() });
    }

    private void setNextAndPriorQuoteNumbers(Collection<Quote> quoteList) {
        for (Quote q : quoteList) {
            q.setNextQuoteNumber(getNextQuoteNumber(q.getNumber()));
            q.setPriorQuoteNumber(getPriorQuoteNumber(q.getNumber()));
        }
    }

    private Collection<Quote> getQuoteCollection(String querySQL) {
        Collection<Quote> quoteList = (Collection<Quote>) getDatabaseTemplate().query(querySQL, new QuoteRowMapper());
        setNextAndPriorQuoteNumbers(quoteList);
        return quoteList;
    }

    @Override
    public Collection<Quote> getAllQuotes() {
        logger.info("getting all quotes.");
        String querySQL = "select * from Quote";
        return getQuoteCollection(querySQL);
    }

    @Override
    public Collection<Quote> getUsableQuotes() {
        logger.info("getting usable quotes.");
        String querySQL = "select * from Quote where CanUse = 'Y'";
        return getQuoteCollection(querySQL);
    }

    @Override
    public Collection<Quote> getQuotesForSourceCode(int sourceCode) {
        logger.info("getting quotes for source code " + sourceCode);
        String querySQL = "select * from Quote where SrcCde = ?";
        Collection<Quote> quoteList = (Collection<Quote>) getDatabaseTemplate().query(querySQL, new Object[] { sourceCode }, new QuoteRowMapper());
        setNextAndPriorQuoteNumbers(quoteList);
        return quoteList;
    }

    @Override
    public QuoteOfTheDay getQuoteOfTheDay(Date runDate) {
        logger.info("getting quote of the day for " + runDate);
        String querySQL = "select * from QuoteOfTheDay where QuoteDate = ?";

        try {
            QuoteOfTheDay qotd = (QuoteOfTheDay) getDatabaseTemplate().queryForObject(querySQL, new Object[] { runDate }, new QuoteOfTheDayRowMapper());
            return qotd;
        } catch (EmptyResultDataAccessException e) {
            logger.info("no data found.");
            return null;
        }
    }

    @Override
    public Collection<QuoteSourceCode> getQuoteSourceCodes() {
        logger.info("getting list of source code.");
        String querySQL = "select * from SrcVal order by SrcTxt";
        Collection<QuoteSourceCode> sourceCodeList = (Collection<QuoteSourceCode>) getDatabaseTemplate().query(querySQL, new QuoteSourceCodeRowMapper());
        return sourceCodeList;
    }

    @Value("${com.blazartech.products.qotdp.data.access.impl.spring.identitySQL}")
    private String identitySQL;

    /**
     * Get the value of identitySQL
     *
     * @return the value of identitySQL
     */
    public String getIdentitySQL() {
        return identitySQL;
    }

    /**
     * Set the value of identitySQL
     *
     * @param identitySQL new value of identitySQL
     */
    public void setIdentitySQL(String identitySQL) {
        this.identitySQL = identitySQL;
    }

    private int getIdentity() {
        logger.info("getting identity value");
        return getDatabaseTemplate().queryForObject(getIdentitySQL(), Integer.class);
    }

    private void addObject(DataObject object, String insertSQL, Object[] insertArguments) {
        logger.info("adding object of type " + object.getClass());
        getDatabaseTemplate().update(insertSQL, insertArguments);
        int idValue = getIdentity();
        object.setNumber(idValue);
    }

    @Override
    public void addQuote(Quote q) {
        String insertSQL = "insert into Quote (SrcCde, QuoteTxt, CanUse) values(?, ?, ?)";
        String canUse = q.isUsable() ? "Y" : "N";
        Object[] arguments = new Object[] { q.getSourceCode(), q.getText(), canUse };
        addObject(q, insertSQL, arguments);
    }

    @Override
    public void addQuoteOfTheDay(QuoteOfTheDay qotd) {
        String insertSQL = "insert into QuoteOfTheDay (QuoteNum, QuoteDate) values (?, ?)";
        Object[] arguments = new Object[] { qotd.getQuoteNumber(), qotd.getRunDate() };
        addObject(qotd, insertSQL, arguments);
    }

    @Override
    public void updateQuoteOfTheDay(QuoteOfTheDay qotd) {
        logger.info("updating qotd #" + qotd.getNumber());
        String updateSQL = "update QuoteOfTheDay set QuoteDate = ?, QuoteNum = ? where QotdNum = ?";
        getDatabaseTemplate().update(updateSQL, new Object[] { qotd.getRunDate(), qotd.getQuoteNumber(), qotd.getNumber() });
    }

    @Override
    public void addQuoteSourceCode(QuoteSourceCode sourceCode) {
        String insertSQL = "insert into SrcVal (SrcTxt) values(?)";
        Object[] arguments = new Object[] { sourceCode.getText() };
        addObject(sourceCode, insertSQL, arguments);
    }

    @Override
    public Collection<QuoteOfTheDay> getQuoteOfTheDayInDateRange(int quoteNumber, Date startDate, Date endDate) {
        logger.info("looking for instances of quote #" + quoteNumber + " in date range " + startDate + " to " + endDate);
        String querySQL = "select * from QuoteOfTheDay where QuoteNum = ? and QuoteDate between ? and ?";
        Collection<QuoteOfTheDay> qotdList = (Collection<QuoteOfTheDay>) getDatabaseTemplate().query(querySQL, new Object[] { quoteNumber, startDate, endDate }, new QuoteOfTheDayRowMapper());
        return qotdList;
    }

    @Override
    public QuoteOfTheDayHistory getQuoteOfTheDayHistoryForQuote(int quoteNumber) {
        logger.info("getting history for quote #" + quoteNumber);
        String querySQL = "select * from QuoteOfTheDay where QuoteNum = ?";
        Collection<QuoteOfTheDay> qotdList = (Collection<QuoteOfTheDay>) getDatabaseTemplate().query(querySQL, new Object[] { quoteNumber }, new QuoteOfTheDayRowMapper());
        
        return buildQuoteOfTheDayHistory(qotdList, quoteNumber);
    }

    
    


}
