package com.shengchuang.member.core.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;

@Entity(name = "name_value_pair")
public class KeyValuePair {

    @Id
    @Column(name = "k")
    private String key;

    @Lob
    @Column(name = "v")
    private String value;

    public KeyValuePair() {
    }

    public KeyValuePair(String key, Object value) {
        this.key = key;
        this.value = value == null ? null : value.toString();
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String stingValue() {
        return value;
    }

    public Integer intValue() {
        return value == null ? null : Integer.valueOf(value);
    }

    public Double doubleValue() {
        return value == null ? null : Double.valueOf(value);
    }

    public Long LongValue() {
        return value == null ? null : Long.valueOf(value);
    }

    public KeyValuePair value(Object value) {
        this.value = value == null ? null : value.toString();
        return this;
    }

    public boolean equalsKV(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof KeyValuePair))
            return false;
        KeyValuePair other = (KeyValuePair) obj;
        if (key == null) {
            if (other.getKey() != null)
                return false;
        } else if (!key.equals(other.getKey()))
            return false;
        if (value == null) {
            if (other.getValue() != null)
                return false;
        } else if (!value.equals(other.getValue()))
            return false;
        return true;
    }
}
