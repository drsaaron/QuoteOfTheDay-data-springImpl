/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.blazartech.products.qotdp.data.access.impl.spring;

import com.blazartech.products.qotdp.data.QuoteSourceCode;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

/**
 *
 * @author scott
 */
class QuoteSourceCodeRowMapper implements RowMapper<QuoteSourceCode> {
    
    private String trimString(String s) {
        String trimmedString = s.trim().replaceAll("\r", "");
        return trimmedString;
    }
    
    @Override
    public QuoteSourceCode mapRow(ResultSet rs, int rowNumber) throws SQLException {
        QuoteSourceCode sourceCode = new QuoteSourceCode();
        sourceCode.setNumber(rs.getInt("SrcCde"));
        sourceCode.setText(trimString(rs.getString("SrcTxt")));
        return sourceCode;
    }
    
}
