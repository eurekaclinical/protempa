package org.protempa.bp.commons.dsb;

import org.protempa.proposition.value.Granularity;
import org.protempa.proposition.value.ValueFactory;

public class TableSpec {

    private String id;
    private String name;
    private String key;
    private String code;
    private String position;
    private String val;
    private ValueFactory valType;
    private Granularity gran;
    private PositionParser positionParser;

    public TableSpec() {
    }

    public TableSpec(String id, String name, String key, String code,
            String position, String val, ValueFactory valType, Granularity gran,
            PositionParser positionParser) {
        this.id = id;
        this.name = name;
        this.key = key;
        this.code = code;
        this.position = position;
        this.val = val;
        this.valType = valType;
        this.gran = gran;
        this.positionParser = positionParser;
    }



    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id
     *            the id to set
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name
     *            the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the key
     */
    public String getKey() {
        return key;
    }

    /**
     * @param key
     *            the key to set
     */
    public void setKey(String key) {
        this.key = key;
    }

    /**
     * @return the tstamp
     */
    public String getPosition() {
        return position;
    }

    /**
     * @param tstamp
     *            the tstamp to set
     */
    public void setPosition(String position) {
        this.position = position;
    }

    /**
     * @return the val
     */
    public String getVal() {
        return val;
    }

    /**
     * @param val
     *            the val to set
     */
    public void setVal(String val) {
        this.val = val;
    }

    /**
     * @return the valType
     */
    public ValueFactory getValType() {
        return valType;
    }

    /**
     * @param valType
     *            the valType to set
     */
    public void setValType(ValueFactory valType) {
        this.valType = valType;
    }

    /**
     * @return the gran
     */
    public Granularity getGran() {
        return gran;
    }

    /**
     * @param gran
     *            the gran to set
     */
    public void setGran(Granularity gran) {
        this.gran = gran;
    }

    /**
     * @return the code
     */
    public String getCode() {
        return code;
    }

    /**
     * @param code
     *            the code to set
     */
    public void setCode(String code) {
        this.code = code;
    }

    public PositionParser getPositionParser() {
        return positionParser;
    }


}
