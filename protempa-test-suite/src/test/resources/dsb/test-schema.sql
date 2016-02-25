
--create user  cvrg identified by cvrg
--default tablespace users
--QUOTA 10000M ON users
--temporary tablespace temp;
--
--grant create session to cvrg;
--grant create table to  cvrg;
--grant create view to  cvrg;
--grant create any index to  cvrg;
--GRANT CREATE PUBLIC SYNONYM TO  cvrg;
--GRANT DROP PUBLIC SYNONYM TO  cvrg;
--GRANT CREATE SEQUENCE TO  cvrg;
--GRANT CREATE PROCEDURE TO  cvrg;
--grant CREATE ANY TYPE TO cvrg;
--grant ALTER ANY TYPE TO cvrg;
--grant DROP ANY TYPE TO cvrg;
--grant EXECUTE ANY TYPE TO cvrg;
--grant UNDER ANY TYPE TO cvrg;
--grant CREATE ANY TRIGGER to  cvrg;
--grant ALTER ANY TRIGGER to  cvrg;
--grant DROP ANY TRIGGER to  cvrg;


CREATE SCHEMA "TEST";

SET SCHEMA "TEST";

CREATE TABLE "PATIENT" (

"PATIENT_KEY"    NUMBER(22,0) NOT NULL ,
"FIRST_NAME"     VARCHAR2(32) ,
"LAST_NAME"      VARCHAR2(32) ,
"DOB"            DATE         ,
"LANGUAGE"       VARCHAR2(32) ,
"MARITAL_STATUS" VARCHAR2(32) ,
"RACE"           VARCHAR2(32) ,
"GENDER"         VARCHAR2(16) ,
"CREATE_DATE"    DATE ,
"UPDATE_DATE"    DATE ,
"DELETE_DATE"    DATE ,

CONSTRAINT patient_pk PRIMARY KEY (patient_key)
);
--tablespace users
--nologging;



CREATE TABLE "PROVIDER" (

"PROVIDER_KEY"  NUMBER(22,0) NOT NULL ,
"FIRST_NAME"    VARCHAR2(32) ,
"LAST_NAME"     VARCHAR2(32) ,
"CREATE_DATE"    DATE ,
"UPDATE_DATE"    DATE ,
"DELETE_DATE"    DATE ,

CONSTRAINT provider_pk PRIMARY KEY (provider_key)
);
--tablespace users
--nologging;


CREATE TABLE "ENCOUNTER" (

"ENCOUNTER_KEY"   NUMBER(22,0) NOT NULL ,
"PATIENT_KEY"     NUMBER(22,0) NOT NULL ,
"PROVIDER_KEY"    NUMBER(22,0) NOT NULL ,
"TS_START"        TIMESTAMP(4) ,
"TS_END"          TIMESTAMP(4) ,
"ENCOUNTER_TYPE"  VARCHAR2(64) ,
"DISCHARGE_DISP"  VARCHAR2(64) ,
"CREATE_DATE"    DATE ,
"UPDATE_DATE"    DATE ,
"DELETE_DATE"    DATE ,
 
CONSTRAINT encounter_pk PRIMARY KEY (encounter_key)
);
--tablespace users
--nologging;
 
 
 
 
 
CREATE TABLE "CPT_EVENT" (
 
"EVENT_KEY"     VARCHAR2(32) NOT NULL ,
"ENCOUNTER_KEY" NUMBER(22,0) NOT NULL ,
"TS_OBX"        TIMESTAMP(4) ,
"ENTITY_ID"     VARCHAR2(128) NOT NULL ,
"CREATE_DATE"    DATE ,
"UPDATE_DATE"    DATE ,
"DELETE_DATE"    DATE ,
 
CONSTRAINT cpt_event_pk PRIMARY KEY (event_key)
);
--tablespace users
--nologging;
 
 
 
CREATE TABLE "ICD9D_EVENT" (
 
"EVENT_KEY"     VARCHAR2(32) NOT NULL ,
"ENCOUNTER_KEY" NUMBER(22,0) NOT NULL ,
"TS_OBX"        TIMESTAMP(4) ,
"ENTITY_ID"     VARCHAR2(128) NOT NULL ,
"CREATE_DATE"    DATE ,
"UPDATE_DATE"    DATE ,
"DELETE_DATE"    DATE ,
 
CONSTRAINT icd9d_event_pk PRIMARY KEY (event_key)
);
--tablespace users
--nologging;
 
 
 
CREATE TABLE "ICD9P_EVENT" (
 
"EVENT_KEY"     VARCHAR2(32) NOT NULL ,
"ENCOUNTER_KEY" NUMBER(22,0) NOT NULL ,
"TS_OBX"        TIMESTAMP(4) ,
"ENTITY_ID"     VARCHAR2(128) NOT NULL ,
"CREATE_DATE"    DATE ,
"UPDATE_DATE"    DATE ,
"DELETE_DATE"    DATE ,
 
CONSTRAINT icd9p_event_pk PRIMARY KEY (event_key)
);
--tablespace users
--nologging;
 
 
 
 
CREATE TABLE "MEDS_EVENT" (
 
"EVENT_KEY"     VARCHAR2(32) NOT NULL ,
"ENCOUNTER_KEY" NUMBER(22,0) NOT NULL ,
"TS_OBX"        TIMESTAMP(4) ,
"ENTITY_ID"     VARCHAR2(128) NOT NULL ,
"CREATE_DATE"    DATE ,
"UPDATE_DATE"    DATE ,
"DELETE_DATE"    DATE ,
 
CONSTRAINT meds_event_pk PRIMARY KEY (event_key)
);
--tablespace users
--nologging;
 
 
 
 
CREATE TABLE "LABS_EVENT" (
 
"EVENT_KEY"     VARCHAR2(32) NOT NULL ,
"ENCOUNTER_KEY" NUMBER(22,0) NOT NULL ,
"TS_OBX"        TIMESTAMP(4) ,
"ENTITY_ID"     VARCHAR2(128) NOT NULL ,
"RESULT_STR"    VARCHAR2(32) ,
"RESULT_NUM"    NUMBER(18,4) ,
"UNITS"         VARCHAR2(16) ,
"FLAG"          VARCHAR2(8) ,
"CREATE_DATE"    TIMESTAMP(4) ,
"UPDATE_DATE"    TIMESTAMP(4) ,
"DELETE_DATE"    TIMESTAMP(4) ,
 
CONSTRAINT labs_event_pk PRIMARY KEY (event_key)
);
--tablespace users
--nologging;
 
 
 
 
CREATE TABLE "VITALS_EVENT" (
 
"EVENT_KEY"     VARCHAR2(32) NOT NULL ,
"ENCOUNTER_KEY" NUMBER(22,0) NOT NULL ,
"TS_OBX"        TIMESTAMP(4) ,
"ENTITY_ID"     VARCHAR2(128) NOT NULL ,
"RESULT_STR"    VARCHAR2(32) ,
"RESULT_NUM"    NUMBER(18,4) ,
"UNITS"         VARCHAR2(16) ,
"FLAG"          VARCHAR2(8) ,
"CREATE_DATE"    TIMESTAMP(4) ,
"UPDATE_DATE"    TIMESTAMP(4) ,
"DELETE_DATE"    TIMESTAMP(4) ,
 
CONSTRAINT vitals_event_pk PRIMARY KEY (event_key)
);
--tablespace users
--nologging;
