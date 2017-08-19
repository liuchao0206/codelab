package com.coderpage.codelab.model;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * @author lc. 2017-08-19
 * @since 0.1.0
 */

@Entity
public class QuerySource {

    @Id(autoincrement = true)
    private Long id;
    private String text;

    @Generated(hash = 1391832582)
    public QuerySource(Long id, String text) {
        this.id = id;
        this.text = text;
    }

    @Generated(hash = 785455395)
    public QuerySource() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
