package org.protempa.query.handler;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.io.Writer;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.logging.Level;

import javax.swing.tree.DefaultMutableTreeNode;

import org.apache.commons.lang.StringUtils;
import org.arp.javautil.string.StringUtil;
import org.protempa.AbstractionDefinition;
import org.protempa.ConstantDefinition;
import org.protempa.DataSourceReadException;
import org.protempa.EventDefinition;
import org.protempa.FinderException;
import org.protempa.KnowledgeSource;
import org.protempa.KnowledgeSourceReadException;
import org.protempa.ProtempaUtil;
import org.protempa.QuerySession;
import org.protempa.dsb.filter.Filter;
import org.protempa.proposition.Proposition;
import org.protempa.proposition.UniqueIdentifier;
import org.protempa.query.Query;
import org.protempa.query.handler.table.TableColumnSpec;

/**
 * 
 * @author Andrew Post
 */
//TODO: implement QueryResultsHandler
//TODO: move to registry repository
public final class I2B2QueryResultsHandler extends WriterQueryResultsHandler
        implements Serializable {

    private static final long serialVersionUID = -1503401944818776787L;
    private final char columnDelimiter;
    private final String[] rowPropositionIds;
    private final TableColumnSpec[] columnSpecs;
    private final boolean headerWritten;
    private KnowledgeSource knowledgeSource;
    private Query query;

    public I2B2QueryResultsHandler(Writer out, char columnDelimiter,
            String[] rowPropositionIds, TableColumnSpec[] columnSpecs,
            boolean headerWritten, Query query) {
        super(out);
        checkConstructorArgs(rowPropositionIds, columnSpecs);
        this.columnDelimiter = columnDelimiter;
        this.rowPropositionIds = rowPropositionIds.clone();
        this.columnSpecs = columnSpecs.clone();
        this.headerWritten = headerWritten;
        this.query = query;
    }

    public I2B2QueryResultsHandler(OutputStream out, char columnDelimiter,
            String[] rowPropositionIds, TableColumnSpec[] columnSpecs,
            boolean headerWritten, Query query) {
        super(out);
        checkConstructorArgs(rowPropositionIds, columnSpecs);
        this.columnDelimiter = columnDelimiter;
        this.rowPropositionIds = rowPropositionIds.clone();
        this.columnSpecs = columnSpecs.clone();
        this.headerWritten = headerWritten;
        this.query = query;
    }

    public I2B2QueryResultsHandler(String fileName, char columnDelimiter,
            String[] rowPropositionIds, TableColumnSpec[] columnSpecs,
            boolean headerWritten, Query query) throws IOException {
        super(fileName);
        checkConstructorArgs(rowPropositionIds, columnSpecs);
        this.columnDelimiter = columnDelimiter;
        this.rowPropositionIds = rowPropositionIds.clone();
        this.columnSpecs = columnSpecs.clone();
        this.headerWritten = headerWritten;
        this.query = query;
    }

    public I2B2QueryResultsHandler(File file, char columnDelimiter,
            String[] rowPropositionIds, TableColumnSpec[] columnSpecs,
            boolean headerWritten, Query query) throws IOException {
        super(file);
        checkConstructorArgs(rowPropositionIds, columnSpecs);
        this.columnDelimiter = columnDelimiter;
        this.rowPropositionIds = rowPropositionIds.clone();
        this.columnSpecs = columnSpecs.clone();
        this.headerWritten = headerWritten;
        this.query = query;
    }

    private void checkConstructorArgs(String[] rowPropositionIds,
            TableColumnSpec[] columnSpecs) {
        ProtempaUtil.checkArray(rowPropositionIds, "rowPropositionIds");
        ProtempaUtil.checkArray(columnSpecs, "columnSpecs");
    }

    public String[] getRowPropositionIds() {
        return this.rowPropositionIds.clone();
    }

    public char getColumnDelimiter() {
        return this.columnDelimiter;
    }

    public TableColumnSpec[] getColumnSpecs() {
        return this.columnSpecs.clone();
    }

    public boolean isHeaderWritten() {
        return this.headerWritten;
    }

    @Override
    public void init(KnowledgeSource knowledgeSource) throws FinderException {
        this.knowledgeSource = knowledgeSource;
        if (this.headerWritten) {
            try {
                List<String> columnNames = new ArrayList<String>();
                columnNames.add("KeyId");
                for (TableColumnSpec columnSpec : this.columnSpecs) {
                    Util.logger().log(
                            Level.FINE,
                            "Processing columnSpec type "
                                    + columnSpec.getClass().getName());
                    String[] colNames =
                            columnSpec.columnNames(knowledgeSource);

                    for (int index = 0; index < colNames.length; index++) {
                        if (colNames[index] == null) {
                            colNames[index] = "(null)";
                        } else if (colNames[index].length() == 0) {
                            colNames[index] = "(empty)";
                        }
                    }

                    String[] escapedColNames = StringUtil
                            .escapeDelimitedColumns(colNames,
                                    this.columnDelimiter);
                    Util.logger().log(
                            Level.FINE,
                            "Got the following columns for proposition "
                                    + Arrays.toString(this.rowPropositionIds)
                                    + ": "
                                    + StringUtils.join(escapedColNames, ","));
                    for (String colName : escapedColNames) {
                        columnNames.add(colName);
                    }
                }

                write(StringUtils.join(columnNames, this.columnDelimiter));
                newLine();
            } catch (KnowledgeSourceReadException ex1) {
                throw new FinderException("Error reading knowledge source", ex1);
            } catch (IOException ex) {
                throw new FinderException("Could not write header", ex);
            }
        }

        // Create the ontology tree
        try {
			createOntologyTree(org.arp.javautil.arrays.Arrays.asSet(query.getKeyIds()), 
					org.arp.javautil.arrays.Arrays.asSet(query.getPropIds()),
					query.getFilters() ,null , false);
		} catch (DataSourceReadException e) {
			e.printStackTrace();
		} catch (KnowledgeSourceReadException e) {
			e.printStackTrace();
		}
    }
    
    public void finish() throws FinderException {
    	try {	
			Class.forName ("oracle.jdbc.driver.OracleDriver");
			try {
				//TODO: give as argument
				String[] CS = {"jdbc:oracle:thin:@localhost:1521/XE" , "i2b2metadata" , "demouser"};
				java.sql.Connection cn = java.sql.DriverManager.getConnection (CS[0] , CS[1] , CS[2]);
				System.out.println("/n/n/n/n/n/n/n/n META /n/n/n/n/n/n/n/n/n");
				doMetaData();
		        persistMetaData(cn);
		        cn.close();
			}
			catch (Exception sqle) {

				System.err.println ("Bad connection parameters for META");
				sqle.printStackTrace();
			}
        }
        catch (Exception e) {

        	e.printStackTrace();
        }
        try {

			Class.forName ("oracle.jdbc.driver.OracleDriver");
			try {
				String[] CS = {"jdbc:oracle:thin:@localhost:1521/XE" , "i2b2demodata" , "demouser"};
				java.sql.Connection cn = java.sql.DriverManager.getConnection (CS[0] , CS[1] , CS[2]);
		        doData();
		        persistData(cn);
		        cn.close();
			}
			catch (Exception sqle) {

				System.err.println ("Bad connection parameters for DATA");
				sqle.printStackTrace();
			}
        }
        catch (Exception e) {

        	e.printStackTrace();
        }
    }

    @Override
	public void handleQueryResult(String key, List<Proposition> propositions,
			Map<Proposition, List<Proposition>> derivations,
			Map<UniqueIdentifier, Proposition> references)
			throws FinderException {

		int n = this.columnSpecs.length;
		List<Proposition> filtered = new ArrayList<Proposition>();

		for (Proposition prop : propositions) {

			if (org.arp.javautil.arrays.Arrays.contains(this.rowPropositionIds,
					prop.getId())) {

				filtered.add(prop);
			}
		}
		for (Proposition prop : filtered) {
			ArrayList<String> accumulator = new ArrayList<String>(1 << 8);
			for (int i = 0; i < n; i++) {

				TableColumnSpec columnSpec = this.columnSpecs[i];
				try {

					List<String> columnValues = new ArrayList<String>();
					String[] colValues = columnSpec.columnValues(key, prop,
							derivations, references, this.knowledgeSource);
					if (i == 0) {

						columnValues.add(key);
					}
					for (String colVal : colValues) {

						columnValues.add(colVal);
					}
					for (int index = 0; index < columnValues.size(); index++) {

						if (columnValues.get(index) == null) {

							columnValues.set(index, "(null)");
						} else if (columnValues.get(index).length() == 0) {

							columnValues.set(index, "(empty)");
						}
					}
					List<String> escapedColumnValues = StringUtil
							.escapeDelimitedColumns(columnValues,
									this.columnDelimiter);

					accumulator.addAll(escapedColumnValues);
				} catch (KnowledgeSourceReadException ex1) {

					throw new FinderException(
							"Could not read knowledge source", ex1);
				}
			}
			handleRecord(accumulator);
		}
	}
    
    public void treeLeafEventIds (String abstractionOrEventId , DefaultMutableTreeNode root) throws KnowledgeSourceReadException {
    	if (abstractionOrEventId == null) {
    		throw new IllegalArgumentException ("abstractionAndEventIds cannot be null");
    	}
		treeLeafEventIdsHelper (abstractionOrEventId , root);
    }
    
    private void treeLeafEventIdsHelper (String abstractionOrEventId , DefaultMutableTreeNode n) throws KnowledgeSourceReadException {
        if (abstractionOrEventId == null) {

        	return;
        }
		EventDefinition eventDef = knowledgeSource.readEventDefinition (abstractionOrEventId);
		if (eventDef != null) {

			String[] inverseIsA = eventDef.getInverseIsA();
            if (inverseIsA.length == 0) {
            	String ss = eventDef.getDisplayName();
            	if ((ss != null) && (ss.length() > 0)) {
                	try {

                		Double.parseDouble (eventDef.getId());
                    	DefaultMutableTreeNode c = new DefaultMutableTreeNode (eventDef.getId() + "  " + eventDef.getDisplayName());
                    	n.add (c);
                	}
                	catch (Exception e) {

                    	DefaultMutableTreeNode c = new DefaultMutableTreeNode (eventDef.getDisplayName());
                    	n.add (c);
                	}
//                	System.out.println (eventDef.getDisplayName());
            	}
            	else {

                	DefaultMutableTreeNode c = new DefaultMutableTreeNode (eventDef.getId());
//                	System.out.println (eventDef.getDisplayName());
                	n.add (c);
            	}
            }
            else {

            	DefaultMutableTreeNode c = new DefaultMutableTreeNode (eventDef.getId());
            	n.add (c);
            	for (String s : inverseIsA) {
//                	System.out.println ("inverseIsA_" + s);
            		treeLeafEventIdsHelper (s , c);
            	}
            }
        }
		else {

            AbstractionDefinition apDef = knowledgeSource.readAbstractionDefinition (abstractionOrEventId);
            if (apDef != null) {

                Set<String> af = apDef.getAbstractedFrom();
                for (String s : af) {

                	DefaultMutableTreeNode c = new DefaultMutableTreeNode (s);
                	n.add (c);
                	System.out.println ("abstractedFrom__" + s);
            		treeLeafEventIdsHelper (s , c);
                }
            }
            else {

                ConstantDefinition constantDef = knowledgeSource.readConstantDefinition (abstractionOrEventId);
                if (constantDef == null) {

                	throw new KnowledgeSourceReadException ("The proposition definition '" + abstractionOrEventId + "' is unknown");
                }
            }
        }
    }
    
	private Map<String, List<Object>> createOntologyTree(Set<String> keyIds,
			Set<String> propositionIds, Filter filters, QuerySession qs,
			boolean stateful) throws DataSourceReadException,
			KnowledgeSourceReadException {

		Map<String, List<Object>> objects = new HashMap<String, List<Object>>();

		// Add events
		System.out.println("     keyIds:");
		for (String s : keyIds) {

			System.out.println(s);
		}
		System.out.println("     propositionIds:");
		for (String s : propositionIds) {

			System.out.println(s);
		}
		System.out.println("     leafEventIds:");

		DefaultMutableTreeNode ROOT = new DefaultMutableTreeNode("ROOT");
		for (String s : propositionIds) {

			try {

				treeLeafEventIds(s, ROOT);
			} catch (Exception e) {

				e.printStackTrace();
			}
		}

		TreeMap<String, DefaultMutableTreeNode> meds = new TreeMap<String, DefaultMutableTreeNode>();
		TreeMap<String, DefaultMutableTreeNode> cond = new TreeMap<String, DefaultMutableTreeNode>();
		TreeMap<String, DefaultMutableTreeNode> drug = new TreeMap<String, DefaultMutableTreeNode>();
		// TreeMap<String,DefaultMutableTreeNode> enct = new
		// TreeMap<String,DefaultMutableTreeNode>();
		@SuppressWarnings("unchecked")
		Enumeration<DefaultMutableTreeNode> children = ROOT.children();
		while (children.hasMoreElements()) {

			DefaultMutableTreeNode x = children.nextElement();
			if (((String) x.getUserObject()).equals("Encounter")) {

				// enct.put((String)x.getUserObject() , x);
			} else if (((String) x.getUserObject()).endsWith("Medication")) {

				meds.put((String) x.getUserObject(), x);
			} else if (((String) x.getUserObject()).startsWith("ERAT")) {

				cond.put((String) x.getUserObject(), x);
			} else {

				drug.put((String) x.getUserObject(), x);
			}
		}
		DefaultMutableTreeNode a = new DefaultMutableTreeNode("Condition");
		DefaultMutableTreeNode b = new DefaultMutableTreeNode("Drug");
		DefaultMutableTreeNode c = new DefaultMutableTreeNode("Treatment");
		// DefaultMutableTreeNode d = new DefaultMutableTreeNode ("Encounter");

		for (DefaultMutableTreeNode f : cond.values()) {

			a.add(f);
		}
		for (DefaultMutableTreeNode f : drug.values()) {

			b.add(f);
		}
		for (DefaultMutableTreeNode f : meds.values()) {

			c.add(f);
		}
		// for (DefaultMutableTreeNode f : enct.values()) {
		//
		// d.add(f);
		// }

		grove.add(a);
		grove.add(b);
		grove.add(c);
		// org.protempa.query.handler.grove.add(d);

		// Enumeration<DefaultMutableTreeNode> enm =
		// TOOR.breadthFirstEnumeration();
		// while (enm.hasMoreElements()) {
		//
		// DefaultMutableTreeNode x = (DefaultMutableTreeNode)enm.nextElement();
		// if (x.isLeaf()) {
		//
		// continue;
		// }
		// this.alphabetizeChildren(x);
		// }

		return objects;
	}
	
	public static DefaultMutableTreeNode grove = new DefaultMutableTreeNode ("PROTEMPA");		//	reference to the collection of trees... embodying the ontology
	public static List<String> columnDescriptor = null;

	static TreeMap<String,Provider_Dimension> providers = new TreeMap<String,Provider_Dimension>();
	static TreeMap<String,Discharge> discharge = new TreeMap<String,Discharge>();
	static ArrayList<Observation_Fact> obx_cache = new ArrayList<Observation_Fact> ();
	static ArrayList<List<String>> raw_data = new ArrayList<List<String>> (1<<16);
	static TreeMap<String,DefaultMutableTreeNode> cache = new TreeMap<String,DefaultMutableTreeNode>();

	String[] cols = {	"KeyId",
						"null.lastName",
						"null.firstName",
						"null.middleName",
						"null.nameSuffix",
/*5*/					"null.zipCode",
						"null_startOrTimeStamp",
						"null_finish",
						"null_length",
						"null.type",
/*10*/					"null.healthcareEntity",
						"null.age",
						"null.organization",
						"null.hospitalPavilion",
						"null.dischargeDisposition",
/*15*/					"null.dischargeUnit",					//	<<-----------
						"null_displayName",
						"null.code",
						"atleast1(.diagnosisCodes(id=V58.11,V58.0))",
						"atleast1(.diagnosisCodes(id=V15.81))",
/*20*/					"atleast1(.diagnosisCodes.derived(id=ERATCancer,ERATCKD,ERATCOPD,ERATDiabetes,ERATHF,ERATHxTransplant,ERATMI,ERATPulmHyp,ERATStroke))",
						"count(.diagnosisCodes.derived(id=ERATCancer,ERATCKD,ERATCOPD,ERATDiabetes,ERATHF,ERATHxTransplant,ERATMI,ERATPulmHyp,ERATStroke))",
						"null.firstName",
						"null.middleName",
						"null.lastName",

/*25*/					"atleast1(.diagnosisCodes.derived(id=ERATCancer))",
						"atleast1(.diagnosisCodes.derived(id=ERATCKD))",
						"atleast1(.diagnosisCodes.derived(id=ERATCOPD))",
						"atleast1(.diagnosisCodes.derived(id=ERATDiabetes))",
						"atleast1(.diagnosisCodes.derived(id=ERATHF))",
/*30*/					"atleast1(.diagnosisCodes.derived(id=ERATHxTransplant))",
						"atleast1(.diagnosisCodes.derived(id=ERATMI))",
						"atleast1(.diagnosisCodes.derived(id=ERATPulmHyp))",
						"atleast1(.diagnosisCodes.derived(id=ERATStroke))",

						"atleast1(.medicationHistory(id=Zofran))",
/*35*/					"atleast1(.medicationHistory(id=Lovenox))",
						"atleast1(.medicationHistory(id=Neulasta))",
						"atleast1(.medicationHistory(id=Tussionex))",
						"atleast1(.medicationHistory(id=Neupogen))",
						"atleast1(.medicationHistory(id=Levaquin))",
/*40*/					"atleast1(.medicationHistory(id=Cipro))",
						"atleast1(.medicationHistory(id=OxyContin))",
						"count(.medicationHistory.derived(id=ERATOncologyMedication))",
						"atleast1(.medicationHistory(id=Altace))",
						"atleast1(.medicationHistory(id=Diovan))",
/*45*/					"atleast1(.medicationHistory(id=Imdur))",
						"count(.medicationHistory.derived(id=ERATHeartFailureMedication))",
						"atleast1(.medicationHistory(id=NovoLOG))",
						"atleast1(.medicationHistory(id=Lantus))",
						"atleast1(.medicationHistory(id=metformin))",
/*50*/					"count(.medicationHistory.derived(id=ERATDiabetesMedication))"
	};
	
	
	
	public static void handleRecord (List<String> record) {

		raw_data.add(record);
		for (String s : record) {
			System.err.print(s + "\t");
		}
		System.err.print("\n");

//		Patient_Dimension pat = new Patient_Dimension (record);
//		if ( ! patients.containsKey(pat.getMD5())) {
//
//			patients.put (pat.getMD5() , pat);
//		}
//
//		Provider_Dimension pd = new Provider_Dimension (record);
//		if ( ! providers.containsKey(pd.getMD5())) {
//
//			providers.put (pd.getMD5() , pd);
//		}
//
//		Discharge d = new Discharge (record);
//		if ( ! discharge.containsKey(d.getMD5())) {
//
//			discharge.put (d.getMD5() , d);
//		}
//
//		Observation_Fact obx = new Observation_Fact (record);



		//	columnDescriptor has all the column names in it.
		//	the record argument will line up with the columnDescriptor.
		//	inspection of the record will cause triggers to fire.
		//
		//		like for:
		//			patient:	_id_,zip,age
		//			provider:	_dr_path_,_id_,dr name,
		//			visit:		
		//			fact:		_encounter_id_,_concept_cd_,_provider_id_,_start_date_,_modifier_cd_,patient_id,<datum>
		//
		//			concept:	_concept_path_,concept_cd,
		//
		//	collect these and put in database.
	}

	@SuppressWarnings({ "unchecked" })
	public static void doMetaData() {

		//
		//	sort & condition the accumulated data from both the Protege ontology
		//	and the protempa result set
		//

		for (List<String> record : raw_data) {
			Provider_Dimension pd = new Provider_Dimension (record);
			if ( ! providers.containsKey(pd.getMD5())) {

				providers.put (pd.getMD5(), pd);
			}
			Discharge d = new Discharge (record);
			if ( ! discharge.containsKey(d.getMD5())) {

				discharge.put (d.getMD5(), d);
			}
		}

		ArrayList<String[]> sa = new ArrayList<String[]> (1<<10);
		for (Discharge dd : discharge.values()) {
			sa.add (new String[] {dd.healthcareEntity , dd.dischargeUnit});
		}
		DefaultMutableTreeNode discharges = new DefaultMutableTreeNode("Discharges");
		MD5.translate (discharges , 0 , 2 , sa);
		grove.add(discharges);

		DefaultMutableTreeNode provs = new DefaultMutableTreeNode("Providers");
		for (Provider_Dimension dd : providers.values()) {
			provs.add (new DefaultMutableTreeNode (dd.last + ", " + dd.first));
		}
		grove.add(provs);
		
		Enumeration<DefaultMutableTreeNode> emu = grove.breadthFirstEnumeration();
		while (emu.hasMoreElements()) {
			DefaultMutableTreeNode x = (DefaultMutableTreeNode)emu.nextElement();
			if (x.isLeaf()) {
				continue;
			}
			MD5.alphabetizeChildren(x);
		}
		emu = grove.children();
		while (emu.hasMoreElements()) {		//	temporary kludge
			DefaultMutableTreeNode x = (DefaultMutableTreeNode)emu.nextElement();
			if (x.getUserObject().equals("Drug")) {

				Enumeration<DefaultMutableTreeNode> ume = x.children();
				while (ume.hasMoreElements()) {

					DefaultMutableTreeNode xx = (DefaultMutableTreeNode)ume.nextElement();
					xx.setUserObject(xx.getUserObject() + "_");
				}
				break;
			}
		}

		Enumeration<DefaultMutableTreeNode> enm = grove.breadthFirstEnumeration();
		while (enm.hasMoreElements()) {
			DefaultMutableTreeNode x = enm.nextElement();
			cache.put ((String)x.getUserObject() , x);
			System.out.println ("put ========>>>>> " + x.getUserObject());
		}


		emu = grove.breadthFirstEnumeration();
		while (emu.hasMoreElements()) {
			DefaultMutableTreeNode x = (DefaultMutableTreeNode)emu.nextElement();
			String[] rest = new String[14];
			Object[] oa = x.getUserObjectPath();
			System.out.print(x.getLevel() + 1 + "\t");		//	c_hlevel			!null	NUMBER(22,0)
			rest[0] = Integer.toString (x.getLevel() + 1);
			String path = "";
			for (Object o : oa) {
				path += "\\";
				path += o;
			}
			path += "\\";
			System.out.print(path + "\t");					//	c_fullname			!null	VARCHAR2(700)
			rest[1] = path;
			System.out.print(x.getUserObject() + "\t");		//	c_name				!null	VARCHAR2(2000)
			System.out.print("N\t");						//	c_synonym_cd		!null	CHAR(1)
			if (x.isLeaf()) {
				System.out.print("LAE\t");
			}
			else {
				System.out.print("FAE\t");					//	c_visualattributes	!null	CHAR(1)
			}
			System.out.print("\t");							//	c_totalnum					NUMBER(22,0)
			System.out.print(MD5.calcMD5 (path) + "\t");	//	c_basecode					VARCHAR2(50)
			System.out.print("\t");							//	c_metadataxml				CLOB
			System.out.print("concept_cd\t");				//	c_facttablecolumn	!null	VARCHAR2(50)
			System.out.print("concept_dimension\t");		//	c_tablename			!null	VARCHAR2(50)
			System.out.print("concept_path\t");				//	c_columnname		!null	VARCHAR2(50)
			System.out.print("T\t");						//	c_columndatatype	!null	VARCHAR2(50)
			System.out.print("LIKE\t");						//	c_operator			!null	VARCHAR2(10)
			System.out.print(path + "\t");					//	c_dimcode			!null	VARCHAR2(700)
			System.out.print("\t");							//	c_comment					CLOB
			System.out.print("\t");							//	c_tooltip					VARCHAR2(900)
			System.out.print("\t");							//	update_date			!null	DATE
			System.out.print("\t");							//	download_date				DATE
			System.out.print("\t");							//	import_date					DATE
			System.out.print("\t");							//	sourcesystem_cd				VARCHAR2(50)
			System.out.print("\t");							//	valuetype_cd				VARCHAR2(50)
			System.out.print("\n");
		}
		/*
		JFrame jf = new JFrame ("PROTEMPA");
	    jf.setSize (1000 , 621);
	    jf.getContentPane().add (new JScrollPane (new JTree (grove)));
	    jf.setVisible (true);
	    jf.show();
	    */
	}

	public static void persistMetaData (Connection con) {

		PreparedStatement p = null;
		try {
			@SuppressWarnings("unchecked")
			Enumeration<DefaultMutableTreeNode> emu = grove.depthFirstEnumeration();
			while (emu.hasMoreElements()) {

				DefaultMutableTreeNode x = emu.nextElement();
				try {

					p = con.prepareStatement ("insert into COMORBIDITIES values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
					p.setLong   ( 1 , x.getLevel());
					String path = "";
					Object[] oa = x.getUserObjectPath();
					for (Object o : oa) {
	
						path += "\\";
						path += o;
					}
					path += "\\";
					p.setString ( 2 , path);
					p.setString ( 3 , (String)x.getUserObject());
					p.setString ( 4 , "N");
					if (x.isLeaf()) {
	
						p.setString ( 5 , "LAE");
					}
					else {
	
						p.setString ( 5 , "FAE");
					}
					p.setObject ( 6 , null);
					p.setString ( 7 , MD5.calcMD5 (path));
					p.setObject ( 8 , null);
					p.setString ( 9 , "concept_cd");
					p.setString (10 , "concept_dimension");
					p.setString (11 , "concept_path");
					p.setString (12 , "T");
					p.setString (13 , "LIKE");
					p.setString (14 , path);
					p.setObject (15 , null);
					p.setString (16 , null);
					p.setDate   (17 , new java.sql.Date (System.currentTimeMillis()));
					p.setDate   (18 , null);
					p.setDate   (19 , null);
					p.setString (20 , null);
					p.setString (21 , null);
	
	//				p.addBatch();
	//				ctr++;
	
	//				if (ctr == 1<<8) {
	//
	//					p.executeBatch();
	//					con.commit();
	//					p.clearBatch();
	//					ctr = 0;
	//				}
					p.execute();
					con.commit();
				}
				catch (Exception e) {

					e.printStackTrace();
				}
				finally {

					try {

						p.close();
					}
					catch (Exception e) {

						e.printStackTrace();
					}
				}
			}
		}
		catch (Exception e) {

			e.printStackTrace();
		}
		finally {
			
			try{if(p!=null)p.close();}catch(Exception e){}
		}
	}

	public static void doData() {

		for (List<String> record : raw_data) {

			try {

				Observation_Fact primary_obx = new Observation_Fact (record);
				obx_cache.add (primary_obx);
//				System.out.println (primary_obx.toString());
			}
			catch (Exception e) {

				e.printStackTrace();
			}
		}
	}

	public static void persistData (Connection con) {

		try {

			Patient_Dimension.insertAll (con);
		}
		catch (Exception e) {

			e.printStackTrace();
		}
		try {

			Provider_Dimension.insertAll (con);
		}
		catch (Exception e) {

			e.printStackTrace();
		}
		try {

			Visit_Dimension.insertAll (con);
		}
		catch (Exception e) {

			e.printStackTrace();
		}
		try {

			Concept_Dimension.insertAll (con);
		}
		catch (Exception e) {

			e.printStackTrace();
		}
		for (Observation_Fact obx : obx_cache) {

			try {

				obx.insert (con);
			}
			catch (Exception e) {

				e.printStackTrace();
			}
		}
		System.out.println(Observation_Fact.good);
		System.out.println(Observation_Fact.bad);
		System.out.flush();
	}
	
	
	
	static class Patient_Dimension implements Comparable<Patient_Dimension> {

		long patient_num = 0L;	//	pK

		boolean persisted = false;

		String first = "";
		String middle = "";
		String last = "";
		int age = 0;
		String zip = "";
		static TreeMap<Long,Patient_Dimension> cache = new TreeMap<Long,Patient_Dimension>();


		Patient_Dimension (List<String> record) {

			this.first = record.get(2);
			this.middle = record.get(3);
			this.last = record.get(1);
			try {

				this.age = Integer.parseInt(record.get(11));
			}
			catch (Exception e) {

				this.age = -1;
			}
			this.zip = record.get(5);
			try {

				this.patient_num = Long.parseLong(record.get(0));
			}
			catch (Exception e) {

				this.patient_num = -1L;
			}
		}

		static Patient_Dimension getOrCreateInstance (List<String> record) {

			Patient_Dimension x = new Patient_Dimension (record);
			if ( ! cache.containsKey(x.patient_num)) {

				cache.put (x.patient_num , x);
			}
			return cache.get(x.patient_num);
		}

		public static void insertAll (java.sql.Connection con) {   // This one

			PreparedStatement p = null;
			try {

				for (Patient_Dimension pat : cache.values()) {

					try {

						p = con.prepareStatement ("insert into PATIENT_DIMENSION values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
						p.setLong   ( 1 , pat.patient_num);
						p.setString ( 2 , "vital");
						p.setDate   ( 3 , null);
						p.setDate   ( 4 , null);
						p.setString ( 5 , "F");
						p.setLong   ( 6 , pat.age);
						p.setString ( 7 , "java");
						p.setString ( 8 , "calico");
						p.setString ( 9 , "single");
						p.setString (10 , "zoroastrian");
						p.setString (11 , pat.zip);
						p.setString (12 , "");
						p.setObject (13 , null);
						p.setDate   (14 , null);
						p.setDate   (15 , null);
						p.setDate   (16 , null);
						p.setString (17 , "protempa");
						p.setObject (18 , null);
	
	//					p.addBatch();
	//					ctr++;
	//
	//					if (ctr == 1<<8) {
	//
	//						p.executeBatch();
	//						con.commit();
	//						p.clearBatch();
	//						ctr = 0;
	//					}
						p.execute();
						con.commit();
					}
					catch (Exception e) {

						e.printStackTrace();
						System.out.println (pat);
					}
					finally {

						try {

							p.close();
						}
						catch (Exception e) {

							e.printStackTrace();
						}
					}
				}
			}
			catch (Exception e) {

				e.printStackTrace();
			}
			finally {
				
				try{if(p!=null)p.close();}catch(Exception e){}
			}
		}

		public void insert (java.sql.Connection con) {

			PreparedStatement p = null;
			try {

				p = con.prepareStatement ("insert into PATIENT_DIMENSION values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");

				p.setLong   ( 1 , this.patient_num);
				p.setString ( 2 , null);
				p.setDate   ( 3 , null);
				p.setDate   ( 4 , null);
				p.setString ( 5 , null);
				p.setLong   ( 6 , this.age);
				p.setString ( 7 , "protempa");
				p.setString ( 8 , null);
				p.setString ( 9 , null);
				p.setString (10 , null);
				p.setString (11 , this.zip);
				p.setString (12 , null);
				p.setObject (13 , null);
				p.setDate   (14 , null);
				p.setDate   (15 , null);
				p.setDate   (16 , null);
				p.setString (17 , null);
				p.setObject (18 , null);

				p.execute();
				con.commit();
			}
			catch (Exception e) {

				e.printStackTrace();
				System.out.println (this);
			}
			finally {
				
				try{if(p!=null)p.close();}catch(Exception e){}
			}
		}

		public int compareTo (Patient_Dimension other) {

			return (other.patient_num == this.patient_num) ? 0 : ((other.patient_num > this.patient_num) ? 1 : -1);
		}

		public String getMD5() {

			return MD5.calcMD5(this.toString());
		}

		public String toRecord() {

			StringBuffer sb = new StringBuffer();
			sb.append(patient_num).append("\t").append(first).append("\t").append(middle).append("\t").append(last).append("\t");
			sb.append(age).append("\t").append(zip);
			return sb.toString();
		}

		public String toString() {

			StringBuffer sb = new StringBuffer("\n\n    == patient ==\n");
			sb.append("patient_num = ").append(patient_num).append("\n");
			sb.append("      first = ").append(first).append("\n");
			sb.append("     middle = ").append(middle).append("\n");
			sb.append("       last = ").append(last).append("\n");
			sb.append("        age = ").append(age).append("\n");
			sb.append("        zip = ").append(zip).append("\n\n");
			return sb.toString();
		}
	}

	static class Provider_Dimension implements Comparable<Provider_Dimension> {

		String provider_path = "";		//	pK
		String provider_id = "";		//

		boolean persisted = false;

		String first = "";
		String middle = "";
		String last = "";
		static TreeMap<String,Provider_Dimension> cache = new TreeMap<String,Provider_Dimension>();


		Provider_Dimension (List<String> record) {

			this.first = record.get(22);
			this.middle = "";
			this.last = record.get(24);
		}

		Provider_Dimension (DefaultMutableTreeNode n) {

			String[] sa = ((String)n.getUserObject()).split(",");
			this.first = sa[0].trim();
			this.middle = "";
			this.last = sa[1].trim();

			Object[] oa = n.getUserObjectPath();
			String path = "";
			for (Object o : oa) {

				path += "\\";
				path += o;
			}
			path += "\\";
			this.provider_path = path;
			this.provider_id = MD5.calcMD5(path);
		}

		static Provider_Dimension getOrCreateInstance (DefaultMutableTreeNode n) {

			Provider_Dimension x = new Provider_Dimension (n);
			if ( ! cache.containsKey(x.provider_id)) {

				cache.put (x.provider_id , x);
			}
			return cache.get(x.provider_id);
		}

		public static void insertAll (java.sql.Connection con) {

			PreparedStatement p = null;
			try {

				for (Provider_Dimension prov : cache.values()) {

					try {

						p = con.prepareStatement ("insert into PROVIDER_DIMENSION values (?,?,?,?,?,?,?,?,?)");
						p.setString (1 , prov.provider_path);
						p.setString (2 , prov.provider_id);
						p.setString (3 , prov.first + " " + prov.last);
						p.setObject (4 , null);
						p.setDate   (5 , null);
						p.setDate   (6 , null);
						p.setDate   (7 , null);
						p.setString (8 , "protempa");
						p.setObject (9 , null);

//						p.addBatch();
//						ctr++;
//						if (ctr == 1<<8) {
//
//							p.executeBatch();
//							con.commit();
//							p.clearBatch();
//							ctr = 0;
//						}
						p.execute();
						con.commit();
					}
					catch (Exception e) {

						e.printStackTrace();
						System.out.println (prov.toString());
					}
					finally {

						try {

							p.close();
						}
						catch (Exception e) {

							e.printStackTrace();
						}
					}
				}
			}
			catch (Exception e) {

				e.printStackTrace();
			}
			finally {

				try{if(p!=null)p.close();}catch(Exception e){}
			}
		}

		public int compareTo (Provider_Dimension other) {

			int x = other.last.compareTo(this.last);
			return (x == 0) ? other.first.compareTo(this.first) : x;
		}

		public String getMD5() {

			return MD5.calcMD5(this.toString());
		}

		public String toRecord() {

			StringBuffer sb = new StringBuffer();
			sb.append(first).append("\t").append(middle).append("\t").append(last).append("\t");
			sb.append(provider_path).append("\t").append(provider_id);
			return sb.toString();
		}

		public String toString() {

			StringBuffer sb = new StringBuffer("\n\n    == provider ==\n");
			sb.append("        first = ").append(first).append("\n");
			sb.append("       middle = ").append(middle).append("\n");
			sb.append("         last = ").append(last).append("\n");
			sb.append("provider_path = ").append(provider_path).append("\n");
			sb.append("  provider_id = ").append(provider_id).append("\n\n");
			return sb.toString();
		}
	}

	static class Visit_Dimension {

		long encounter_num = 0L;	//	pK
		long patient_num = 0L;		//
		static TreeMap<String,Visit_Dimension> cache = new TreeMap<String,Visit_Dimension>();


		Visit_Dimension (long encounter_num , long patient_num) {

			this.encounter_num = encounter_num;
			this.patient_num = patient_num;
		}

		static Visit_Dimension getOrCreateInstance (long encounter_num , long patient_num) {

			Visit_Dimension x = new Visit_Dimension (encounter_num , patient_num);
			if ( ! cache.containsKey (encounter_num + "_" +  patient_num)) {

				cache.put (encounter_num + "_" +  patient_num , x);
			}
			return cache.get (encounter_num + "_" +  patient_num);
		}
		// This one
		public static void insertAll (java.sql.Connection con) {

			PreparedStatement p = null;
			try {

				for (Visit_Dimension visit : cache.values()) {

					try {

						p = con.prepareStatement ("insert into VISIT_DIMENSION values (?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
						p.setLong   ( 1 , visit.encounter_num);
						p.setLong   ( 2 , visit.patient_num);
						p.setString ( 3 , null);
						p.setDate   ( 4 , null);
						p.setDate   ( 5 , null);
						p.setString ( 6 , null);
						p.setString ( 7 , null);
						p.setString ( 8 , null);
						p.setObject ( 9 , null);
						p.setDate   (10 , null);
						p.setDate   (11 , null);
						p.setDate   (12 , null);
						p.setString	(13 , "protempa");
						p.setObject (14 , null);
	
//						p.addBatch();
//						ctr++;
//	
//						if (ctr == 1<<8) {
//	
//							p.executeBatch();
//							con.commit();
//							p.clearBatch();
//							ctr = 0;
//						}
						p.execute();
						con.commit();
					}
					catch (Exception e) {

						e.printStackTrace();
						System.out.println(visit);
					}
					finally {

						try {

							p.close();
						}
						catch (Exception e) {

							e.printStackTrace();
						}
					}
				}
			}
			catch (Exception e) {

				e.printStackTrace();
			}
			finally {

				try{if(p!=null)p.close();}catch(Exception e){}
			}
		}

		public void insert (java.sql.Connection con) {

			PreparedStatement p = null;
			try {

				p = con.prepareStatement ("insert into VISIT_DIMENSION values (?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
				p.setLong   ( 1 , this.encounter_num);
				p.setLong   ( 2 , this.patient_num);
				p.setString ( 3 , null);
				p.setDate   ( 4 , null);
				p.setDate   ( 5 , null);
				p.setString ( 6 , null);
				p.setString ( 7 , null);
				p.setString ( 8 , null);
				p.setObject ( 9 , null);
				p.setDate   (10 , null);
				p.setDate   (11 , null);
				p.setDate   (12 , null);
				p.setString	(13 , "protempa");
				p.setObject (14 , null);

				p.addBatch();
				p.executeBatch();
				con.commit();
				p.clearBatch();
			}
			catch (Exception e) {

				e.printStackTrace();
				System.out.println (this);
			}
			finally {

				try{if(p!=null)p.close();}catch(Exception e){}
			}
		}

		public String toRecord() {

			return encounter_num + "\t" + patient_num;
		}

		public String toString() {

			StringBuffer sb = new StringBuffer("\n\n    == visit ==\n");
			sb.append("encounter_num = ").append(encounter_num).append("\n");
			sb.append(  "patient_num = ").append(patient_num).append("\n\n");
			return sb.toString();
		}
	}

	static class Concept_Dimension {

		String concept_path = "";	//	pK
		String concept_cd = "";		//

		boolean persisted = false;

		String name_char = "";
		static TreeMap<String,Concept_Dimension> cache = new TreeMap<String,Concept_Dimension>();


		Concept_Dimension (DefaultMutableTreeNode n) {

			Object[] oa = n.getUserObjectPath();
			String path = "";
			for (Object o : oa) {

				path += "\\";
				path += o;
			}
			path += "\\";
			this.concept_path = path;
			this.concept_cd = MD5.calcMD5 (path);
			this.name_char = (String)n.getUserObject();
		}

		static Concept_Dimension getOrCreateInstance (DefaultMutableTreeNode n) {

			Concept_Dimension x = new Concept_Dimension(n);
			if ( ! cache.containsKey(x.concept_cd)) {

				cache.put (x.concept_cd , x);
			}
			return cache.get(x.concept_cd);
		}
		// This one
		public static void insertAll (java.sql.Connection con) {

			PreparedStatement p = null;
			try {

				for (Concept_Dimension concept : cache.values()) {

					try {

						p = con.prepareStatement ("insert into CONCEPT_DIMENSION values (?,?,?,?,?,?,?,?,?)");
						p.setString (1 , concept.concept_cd);
						p.setString (2 , concept.concept_path);
						p.setString (3 , concept.name_char);
						p.setObject (4 , null);
						p.setDate   (5 , null);
						p.setDate   (6 , null);
						p.setDate   (7 , null);
						p.setString (8 , "protempa");
						p.setObject (9 , null);

//						p.addBatch();
//						ctr++;
//
//						if (ctr == 1<<8) {
//
//							p.executeBatch();
//							con.commit();
//							p.clearBatch();
//							ctr = 0;
//						}
						p.execute();
						con.commit();
					}
					catch (Exception e) {

						e.printStackTrace();
						System.out.println (concept);
					}
					finally {

						try {

							p.close();
						}
						catch (Exception e) {

							e.printStackTrace();
						}
					}
				}
			}
			catch (Exception e) {

				e.printStackTrace();
			}
			finally {
				
				try{if(p!=null)p.close();}catch(Exception e){}
			}
		}

		public String toString() {

			StringBuffer sb = new StringBuffer("\n\n    == concept ==\n");
			sb.append("concept_path = ").append(concept_path).append("\n");
			sb.append("  concept_cd = ").append(concept_cd).append("\n");
			sb.append("   name_char = ").append(name_char).append("\n\n");
			return sb.toString();
		}
	}

	static class Observation_Fact {

		private static long encounter_num_serial = 0L;

		long encounter_num = 0L;													//	pK
		String concept_cd = "";														//	pK
		String provider_id = "@";													//	pK
		java.util.Date start_date = java.util.Calendar.getInstance().getTime();		//	pK
		String modifier_cd = "@";													//	pK

		Patient_Dimension patient = null;
		Provider_Dimension provider = null;
		Visit_Dimension visit = null;

		ArrayList<Concept_Dimension> concepts = new ArrayList<Concept_Dimension> (1<<5);

		Observation_Fact (List<String> record) {

			try {

				String[] dt = record.get(6).split("/");
				@SuppressWarnings("deprecation")
				java.util.Date ddd = new java.util.Date (2009 , Integer.parseInt(dt[1]) , Integer.parseInt(dt[0]));
				start_date = ddd;
			}
			catch (Exception e) {

				start_date = java.util.Calendar.getInstance().getTime();
			}
			this.patient = Patient_Dimension.getOrCreateInstance (record);

			Provider_Dimension provider = new Provider_Dimension (record);
			DefaultMutableTreeNode prov = cache.get(provider.last + ", " + provider.first);
			this.provider = Provider_Dimension.getOrCreateInstance (prov);

			this.encounter_num = ++Observation_Fact.encounter_num_serial;

			this.visit = Visit_Dimension.getOrCreateInstance (this.encounter_num , this.patient.patient_num);

			this.concepts.add (Concept_Dimension.getOrCreateInstance (cache.get (record.get(15))));		//	dischargeUnit

			//	[25,33]
			if (record.get(25).equals("true")) {
				concepts.add(Concept_Dimension.getOrCreateInstance (cache.get("ERATCancer")));
			}
			if (record.get(26).equals("true")) {
				concepts.add(Concept_Dimension.getOrCreateInstance (cache.get("ERATCKD")));
			}
			if (record.get(27).equals("true")) {
				concepts.add(Concept_Dimension.getOrCreateInstance (cache.get("ERATCOPD")));
			}
			if (record.get(28).equals("true")) {
				concepts.add(Concept_Dimension.getOrCreateInstance (cache.get("ERATDiabetes")));
			}
			if (record.get(29).equals("true")) {
				concepts.add(Concept_Dimension.getOrCreateInstance (cache.get("ERATHF")));
			}
			if (record.get(30).equals("true")) {
				concepts.add(Concept_Dimension.getOrCreateInstance (cache.get("ERATHxTransplant")));
			}
			if (record.get(31).equals("true")) {
				concepts.add(Concept_Dimension.getOrCreateInstance (cache.get("ERATMI")));
			}
			if (record.get(32).equals("true")) {
				concepts.add(Concept_Dimension.getOrCreateInstance (cache.get("ERATPulmHyp")));
			}
			if (record.get(33).equals("true")) {
				concepts.add(Concept_Dimension.getOrCreateInstance (cache.get("ERATStroke")));
			}
			//	[34,41]
			if (record.get(34).equals("true")) {
				concepts.add(Concept_Dimension.getOrCreateInstance (cache.get("Zofran")));
				concepts.add(Concept_Dimension.getOrCreateInstance (cache.get("Zofran_")));
				concepts.add(Concept_Dimension.getOrCreateInstance (cache.get("ERATOncologyMedication")));
			}
			if (record.get(35).equals("true")) {
				concepts.add(Concept_Dimension.getOrCreateInstance (cache.get("Lovenox")));
				concepts.add(Concept_Dimension.getOrCreateInstance (cache.get("Lovenox_")));
				concepts.add(Concept_Dimension.getOrCreateInstance (cache.get("ERATOncologyMedication")));
			}
			if (record.get(36).equals("true")) {
				concepts.add(Concept_Dimension.getOrCreateInstance (cache.get("Neulasta")));
				concepts.add(Concept_Dimension.getOrCreateInstance (cache.get("Neulasta_")));
				concepts.add(Concept_Dimension.getOrCreateInstance (cache.get("ERATOncologyMedication")));
			}
			if (record.get(37).equals("true")) {
				concepts.add(Concept_Dimension.getOrCreateInstance (cache.get("Tussionex")));
				concepts.add(Concept_Dimension.getOrCreateInstance (cache.get("Tussionex_")));
				concepts.add(Concept_Dimension.getOrCreateInstance (cache.get("ERATOncologyMedication")));
			}
			if (record.get(38).equals("true")) {
				concepts.add(Concept_Dimension.getOrCreateInstance (cache.get("Neupogen")));
				concepts.add(Concept_Dimension.getOrCreateInstance (cache.get("Neupogen_")));
				concepts.add(Concept_Dimension.getOrCreateInstance (cache.get("ERATOncologyMedication")));
			}
			if (record.get(39).equals("true")) {
				concepts.add(Concept_Dimension.getOrCreateInstance (cache.get("Levaquin")));
				concepts.add(Concept_Dimension.getOrCreateInstance (cache.get("Levaquin_")));
				concepts.add(Concept_Dimension.getOrCreateInstance (cache.get("ERATOncologyMedication")));
			}
			if (record.get(40).equals("true")) {
				concepts.add(Concept_Dimension.getOrCreateInstance (cache.get("Cipro")));
				concepts.add(Concept_Dimension.getOrCreateInstance (cache.get("Cipro_")));
				concepts.add(Concept_Dimension.getOrCreateInstance (cache.get("ERATOncologyMedication")));
			}
			if (record.get(41).equals("true")) {
				concepts.add(Concept_Dimension.getOrCreateInstance (cache.get("OxyContin")));
				concepts.add(Concept_Dimension.getOrCreateInstance (cache.get("OxyContin_")));
				concepts.add(Concept_Dimension.getOrCreateInstance (cache.get("ERATOncologyMedication")));
			}
			//	[43,45]
			if (record.get(43).equals("true")) {
				concepts.add(Concept_Dimension.getOrCreateInstance (cache.get("Altace")));
				concepts.add(Concept_Dimension.getOrCreateInstance (cache.get("Altace_")));
				concepts.add(Concept_Dimension.getOrCreateInstance (cache.get("ERATHeartFailureMedication")));
			}
			if (record.get(44).equals("true")) {
				concepts.add(Concept_Dimension.getOrCreateInstance (cache.get("Diovan")));
				concepts.add(Concept_Dimension.getOrCreateInstance (cache.get("Diovan_")));
				concepts.add(Concept_Dimension.getOrCreateInstance (cache.get("ERATHeartFailureMedication")));
			}
			if (record.get(45).equals("true")) {
				concepts.add(Concept_Dimension.getOrCreateInstance (cache.get("Imdur")));
				concepts.add(Concept_Dimension.getOrCreateInstance (cache.get("Imdur_")));
				concepts.add(Concept_Dimension.getOrCreateInstance (cache.get("ERATHeartFailureMedication")));
			}
			//	[47,49]
			if (record.get(47).equals("true")) {
				concepts.add(Concept_Dimension.getOrCreateInstance (cache.get("NovoLOG")));
				concepts.add(Concept_Dimension.getOrCreateInstance (cache.get("NovoLOG_")));
				concepts.add(Concept_Dimension.getOrCreateInstance (cache.get("ERATDiabetesMedication")));
			}
			if (record.get(48).equals("true")) {
				concepts.add(Concept_Dimension.getOrCreateInstance (cache.get("Lantus")));
				concepts.add(Concept_Dimension.getOrCreateInstance (cache.get("Lantus_")));
				concepts.add(Concept_Dimension.getOrCreateInstance (cache.get("ERATDiabetesMedication")));
			}
			if (record.get(49).equals("true")) {
				concepts.add(Concept_Dimension.getOrCreateInstance (cache.get("metformin")));
				concepts.add(Concept_Dimension.getOrCreateInstance (cache.get("metformin_")));
				concepts.add(Concept_Dimension.getOrCreateInstance (cache.get("ERATDiabetesMedication")));
			}
		}

		public static void insert (java.sql.Connection con , Collection<Observation_Fact> obxs) {

			PreparedStatement p = null;
			try {

				int ctr = 0;
				p = con.prepareStatement ("insert into OBSERVATION_FACT values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
				for (Observation_Fact obx : obxs) {

					for (Concept_Dimension concept : obx.concepts) {

						p.setLong   ( 1 , obx.visit.encounter_num);
						p.setString ( 2 , (String)concept.name_char);
						p.setString ( 3 , obx.provider_id);
						p.setDate   ( 4 , new java.sql.Date (System.currentTimeMillis()));
						p.setString ( 5 , obx.modifier_cd);

						p.setLong   ( 6 , obx.patient.patient_num);

						p.setString ( 7 , null);
						p.setString ( 8 , null);
						p.setObject ( 9 , null);
						p.setString (10 , null);
						p.setObject (11 , null);
						p.setObject (12 , null);
						p.setString	(13 , null);
						p.setDate   (14 , null);
						p.setString (15 , null);
						p.setObject (16 , null);
						p.setObject (17 , null);
						p.setDate   (18 , null);
						p.setDate   (19 , null);
						p.setDate   (20 , null);
						p.setString (21 , "protempa");
						p.setObject (22 , null);

						p.addBatch();
						ctr++;
						if (ctr == 1<<8) {

							p.executeBatch();
							con.commit();
							p.clearBatch();
							ctr = 0;
						}
					}
				}
				p.executeBatch();
				con.commit();
				p.clearBatch();
			}
			catch (Exception e) {

				e.printStackTrace();
			}
			finally {
				
				try{if(p!=null)p.close();}catch(Exception e){}
			}
		}
		// This one
		static int good;
		static int bad;
		public void insert (java.sql.Connection con) {

			PreparedStatement p = null;
			try {

				for (Concept_Dimension concept : this.concepts) {

					try {

						p = con.prepareStatement ("insert into OBSERVATION_FACT values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
						p.setLong   ( 1 , this.visit.encounter_num);
						p.setLong   ( 2 , this.patient.patient_num);
						p.setString ( 3 , (String)concept.concept_cd);
						p.setString ( 4 , this.provider_id);
						p.setDate   ( 5 , new java.sql.Date (System.currentTimeMillis()));
						p.setString ( 6 , this.modifier_cd);

						p.setString ( 7 , null);
						p.setString ( 8 , null);
						p.setObject ( 9 , null);
						p.setString (10 , null);
						p.setObject (11 , null);
						p.setObject (12 , null);
						p.setString	(13 , null);
						p.setDate   (14 , null);
						p.setString (15 , null);
						p.setObject (16 , null);
						p.setObject (17 , null);
						p.setDate   (18 , null);
						p.setDate   (19 , null);
						p.setDate   (20 , null);
						p.setString (21 , "protempa");
						p.setObject (22 , null);

//						p.addBatch();
//						ctr++;
//						if (ctr == 1<<8) {
//
//							p.executeBatch();
//							con.commit();
//							p.clearBatch();
//							ctr = 0;
//						}
						p.execute();
						con.commit();
						good++;
					}
					catch (Exception e) {
						//TODO: find out why are there duplicate records
						bad++;
						//e.printStackTrace();
						//System.out.println (this.visit.encounter_num + " " + this.patient.patient_num + " " + (String)concept.concept_cd + " " + this.provider_id + " " + this.start_date + " " + this.modifier_cd);
					}
					finally {

						try {

							p.close();
						}
						catch (Exception e) {

							e.printStackTrace();
						}
						System.out.flush();
						System.err.flush();
					}
				}
			}
			catch (Exception e) {

				e.printStackTrace();
			}
			finally {
				
				try{if(p!=null)p.close();}catch(Exception e){}
			}
		}

		public String toString() {

			StringBuffer sb = new StringBuffer("\n\n    == Observation_Fact ==\n");
			sb.append("encounter_num = ").append(encounter_num).append("\n");
			sb.append("   concept_cd = ").append(concept_cd).append("\n");
			sb.append("  provider_id = ").append(provider_id).append("\n");
			sb.append("   start_date = ").append(start_date).append("\n");
			sb.append("  modifier_cd = ").append(modifier_cd).append("\n");
			sb.append("  patient_num = ").append(patient.patient_num).append("\n");

			sb.append("  patient_obj = ").append(patient.toString()).append("\n");
			sb.append(" provider_obj = ").append(provider.toString()).append("\n");
			sb.append("    visit_obj = ").append(visit.toString()).append("\n");
			for (Concept_Dimension cd : concepts) {

				sb.append("  concept_obj = ").append(cd.toString()).append("\n");
			}
			return sb.toString();
		}
	}

	//////////////////////////////////////////////////////////////////////////////////////////////////////////

	static class Discharge implements Comparable<Discharge> {

		String healthcareEntity = "";
		String dischargeUnit = "";


		Discharge (List<String> record) {

			this.healthcareEntity = record.get(10);
			this.dischargeUnit = record.get(15);
		}

		public int compareTo (Discharge other) {

			int x = other.healthcareEntity.compareTo(this.healthcareEntity);
			return (x == 0) ? other.dischargeUnit.compareTo(this.dischargeUnit) : x;
		}

		public String getMD5() {

			return MD5.calcMD5 (this.toString());
		}

		public String toString() {

			StringBuffer sb = new StringBuffer();
			sb.append(healthcareEntity).append("\t").append(dischargeUnit).append("\n");
			return sb.toString();
		}
	}
}
