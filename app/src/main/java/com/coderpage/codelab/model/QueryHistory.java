package com.coderpage.codelab.model;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Unique;

import java.util.Date;
import org.greenrobot.greendao.annotation.Generated;

/**
 * @author lc. 2017-08-19
 * @since 0.1.0
 */

@Entity
public class QueryHistory {

    @Id(autoincrement = true)
    private Long id;
    @Unique
    private String query;
    private Date date;

    @Generated(hash = 2106919989)
    public QueryHistory(Long id, String query, Date date) {
        this.id = id;
        this.query = query;
        this.date = date;
    }

    @Generated(hash = 847348890)
    public QueryHistory() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
