/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.blazartech.products.qotdp.data.access.impl.spring;

import com.blazartech.products.qotdp.data.Quote;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

/**
 * row mapper for a quote.
 */
class QuoteRowMapper implements RowMapper<Quote> {

    @Override
    public Quote mapRow(ResultSet rs, int rowNumber) throws SQLException {
        Quote q = new Quote();
        q.setNumber(rs.getInt("QuoteNum"));
        q.setText(rs.getString("QuoteTxt"));
        q.setSourceCode(rs.getInt("SrcCde"));
        String canUse = rs.getString("CanUse");
        q.setUsable(canUse.equals("Y"));
        return q;
    }
    
}
