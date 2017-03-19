/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.blazartech.products.qotdp.data.access.impl.spring;

import com.blazartech.products.qotdp.data.QuoteOfTheDay;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

/**
 *
 * @author scott
 */
class QuoteOfTheDayRowMapper implements RowMapper<QuoteOfTheDay> {
    
    @Override
    public QuoteOfTheDay mapRow(ResultSet rs, int rowNumber) throws SQLException {
        QuoteOfTheDay qotd = new QuoteOfTheDay();
        qotd.setNumber(rs.getInt("QotdNum"));
        qotd.setQuoteNumber(rs.getInt("QuoteNum"));
        qotd.setRunDate(rs.getDate("QuoteDate"));
        return qotd;
    }
    
}
