package SemantifyingBPMN;


import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.UUID;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.util.JAXBResult;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.omg.spec.bpmn._20100524.di.BPMNDiagram;
import org.omg.spec.bpmn._20100524.di.BPMNPlane;
import org.omg.spec.bpmn._20100524.di.BPMNShape;
import org.omg.spec.bpmn._20100524.model.ObjectFactory;
import org.omg.spec.bpmn._20100524.model.TAssociation;
import org.omg.spec.bpmn._20100524.model.TAssociationDirection;
import org.omg.spec.bpmn._20100524.model.TBoundaryEvent;
import org.omg.spec.bpmn._20100524.model.TCollaboration;
import org.omg.spec.bpmn._20100524.model.TCompensateEventDefinition;
import org.omg.spec.bpmn._20100524.model.TDefinitions;
import org.omg.spec.bpmn._20100524.model.TEndEvent;
import org.omg.spec.bpmn._20100524.model.TEvent;
import org.omg.spec.bpmn._20100524.model.TEventBasedGateway;
import org.omg.spec.bpmn._20100524.model.TExclusiveGateway;
import org.omg.spec.bpmn._20100524.model.TExpression;
import org.omg.spec.bpmn._20100524.model.TExtension;
import org.omg.spec.bpmn._20100524.model.TExtensionElements;
import org.omg.spec.bpmn._20100524.model.TFlowElement;
import org.omg.spec.bpmn._20100524.model.TFlowNode;
import org.omg.spec.bpmn._20100524.model.TGateway;
import org.omg.spec.bpmn._20100524.model.TInclusiveGateway;
import org.omg.spec.bpmn._20100524.model.TIntermediateCatchEvent;
import org.omg.spec.bpmn._20100524.model.TIntermediateThrowEvent;
import org.omg.spec.bpmn._20100524.model.TLane;
import org.omg.spec.bpmn._20100524.model.TLaneSet;
import org.omg.spec.bpmn._20100524.model.TManualTask;
import org.omg.spec.bpmn._20100524.model.TMessage;
import org.omg.spec.bpmn._20100524.model.TMessageEventDefinition;
import org.omg.spec.bpmn._20100524.model.TMessageFlow;
import org.omg.spec.bpmn._20100524.model.TParallelGateway;
import org.omg.spec.bpmn._20100524.model.TParticipant;
import org.omg.spec.bpmn._20100524.model.TProcess;
import org.omg.spec.bpmn._20100524.model.TReceiveTask;
import org.omg.spec.bpmn._20100524.model.TSendTask;
import org.omg.spec.bpmn._20100524.model.TSequenceFlow;
import org.omg.spec.bpmn._20100524.model.TStartEvent;
import org.omg.spec.bpmn._20100524.model.TTask;
import org.omg.spec.bpmn._20100524.model.TTerminateEventDefinition;
import org.omg.spec.bpmn._20100524.model.TTimerEventDefinition;
import org.omg.spec.bpmn._20100524.model.TUserTask;
import org.omg.spec.dd._20100524.dc.Bounds;
import org.omg.spec.dd._20100524.dc.Point;
import org.omg.spec.dd._20100524.di.DiagramElement;
import org.omg.spec.bpmn._20100524.di.BPMNEdge;

import camunda.spec.bpmn.*;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.math.BigDecimal;


public class SemantifyingBPMN {

	

	// Execution Configuration variable
	static String actors_filename = null;
	static String tpt_filename = null;
	static String tkdepend_filename = null;
	static String tkview_filename = null;
	static String output_file_txt = null;
	static String output_file_bpmn = null;
	
	static boolean simplify = false;
	static boolean camundaEngine = false;
	static String  businessObjects_filename=null;


	
	private static String[][] listTKdepend = null;
	
	// Actors List
	private static ArrayList<ActorRole> Actors = new ArrayList<ActorRole>();
	
	// Transactor Product Table
	private static ArrayList<TransactionKind> TPT = new ArrayList<TransactionKind>();
	
	// TK Dependencies
	private static HashMap<String,ArrayList<String>> TKDependencies = new HashMap<String,ArrayList<String>>();
	private static HashMap<String,ArrayList<String>> TKDependenciesT = new HashMap<String,ArrayList<String>>();
	private static ArrayList<String> keys = new ArrayList<String>();
	
	// Transaction pattern view
	private static ArrayList<PatternView> TKPatternViews = new ArrayList<PatternView>();
	
	// Business objects
	private static ArrayList<BusinessObject> BusinessObjects = new ArrayList<BusinessObject>();
	
	// BPMN Pools
	private static ArrayList<Pool> Pools = new ArrayList<Pool>(); 
	
	// Collaboration Message Flows
	private static ArrayList<BPMNMessageFlow> MessageFlows = new ArrayList<BPMNMessageFlow>(); 

	private static void timestamp() {
		// current time
		Instant now = Instant.now();
		System.out.println("timestamps now: " + now);		
	};
	
	private static String timeStampForFileName()
	{
		// current time
		Instant now = Instant.now();
		String tempo = new String();	
		LocalDateTime ldt = LocalDateTime.ofInstant(now, ZoneId.systemDefault());
		tempo = "-" + ldt.getYear() + ldt.getMonthValue() + ldt.getDayOfMonth() + "-" + ldt.getHour() + ldt.getMinute() + ldt.getSecond(); 
		return (tempo);		
	}
	
	private static String DayForJSON()
	{
		// current time
		Instant now = Instant.now();
		String tempo = new String();	
		LocalDateTime ldt = LocalDateTime.ofInstant(now, ZoneId.systemDefault());
		
		int dia = ldt.getDayOfMonth();
		int mes= ldt.getMonthValue();
		
		tempo = String.format("%02d" , dia ) + "-" + String.format("%02d" , mes ) + "-" + ldt.getYear();
						
		return (tempo);		
	}
	
	

	private static void Init()
	{
		
		
	}
	

	
	private static void ReadDataFromFiles()
	{	
		
		try
		{	
			Scanner ficheiro = new Scanner(new File(actors_filename),"UTF-8");
			String read_tmp = new String(""); 
			int count_line = 0;	
			ActorRole ar;

			while (ficheiro.hasNextLine())
			{
				count_line++;		
				read_tmp = 	ficheiro.nextLine();
				if (read_tmp.compareTo("") != 0)
				{
					String[] tokens = read_tmp.split(";");	
					ar = new ActorRole(tokens[0].trim() , tokens[1].trim());
					Actors.add(ar);
				}
				else System.out.println("Empty line at " + count_line);		
			}
			//System.out.println ("Reading input file ended. " + count_line + " lines read. With " + Actors.size() + " actor roles.");
			ficheiro.close();
		}
		catch (Exception e) 	{ 	e.printStackTrace(); 	}
		/*System.out.println(	"-------------------*-----------------------------" +
				" Actors \n" + Actors.toString() +
				"\n-------------------*-----------------------------");
		*/
		
		
		

		try
		{	
			Scanner ficheiro = new Scanner(new File(tpt_filename),"UTF-8");
			Scanner ficheiroView = null;
			if (tkview_filename != null) ficheiroView = new Scanner(new File(tkview_filename),"UTF-8");
			String read_tmp = new String(""); 
			String read_tmpView = new String("");
			int count_line = 0;	
			TransactionKind tknew;
			ArrayList<String> TKStepsDefinedInTkView = new ArrayList<String>();

			//Read header from ficheiroview
			if (ficheiroView != null) 
			{
				if  (ficheiroView.hasNextLine()) 
				{					
					read_tmp = ficheiroView.nextLine();
					String[] tokens = read_tmp.split(";");
					for (int Tokenid = 2 ; Tokenid < tokens.length ; Tokenid++)										
						TKStepsDefinedInTkView.add(tokens[Tokenid].trim());
				}
				else System.out.println("WARNING: tkview file without header.");
			}
			while (ficheiro.hasNextLine())
			{
				count_line++;		
				read_tmp = 	ficheiro.nextLine();
				if (ficheiroView != null) read_tmpView = ficheiroView.nextLine();
				
				if (read_tmp.compareTo("") != 0)
				{
					String[] tokens = read_tmp.split(";");
					String[] tokensView;
					if (ficheiroView != null)
					{
						tokensView = read_tmpView.split(";");
						
						if (tokensView[1].trim().substring(0, 6).compareTo("Custom") != 0)												
							TKPatternViews.add( new PatternView( tokensView[0].trim(), tokensView[1].trim() ) );
						else
						{
							PatternView CustomView = new PatternView(tokensView[0].trim(), tokensView[1].trim());
							for (int Tokenid = 2 ; Tokenid < tokensView.length ; Tokenid++)
							{
								CustomView.addTKStep( TKStepsDefinedInTkView.get(Tokenid-2) , tokensView[Tokenid].trim() );
							}							
							TKPatternViews.add(CustomView);
						}
					}
					
					tknew = new TransactionKind(
								tokens[0].trim() , 
								tokens[1].trim() ,
								SearchActors(tokens[2].trim()) ,
								SearchActors(tokens[3].trim()) ,
								new FactType(tokens[4].trim(),tokens[5].trim())
							);			
					
					TPT.add(tknew);
				}
				else System.out.println("Empty line at " + count_line);		
			}
			//System.out.println ("Reading input file ended. " + count_line + " lines read. With " + TPT.size() + " transaction kinds.");
			ficheiro.close();
			ficheiroView.close();
		}
		catch (Exception e) 	{ 	e.printStackTrace(); 	}
		/*System.out.println(	"-------------------*-----------------------------" +
				" TPT \n" + TPT.toString() +
				"\n-------------------*-----------------------------");
	
		System.out.println(	"-------------------*-----------------------------" +
				" TKVIEW \n" + TKPatternViews.toString() +
				"\n-------------------*-----------------------------");
		
		*/
		
		try
		{	
			Scanner ficheiro = new Scanner(new File(tkdepend_filename),"UTF-8");
			String read_tmp = new String(""); 
			int count_line = 1;	
			
			if (ficheiro != null)  
			{
				if  (ficheiro.hasNextLine()) // read keys
				{
					read_tmp = ficheiro.nextLine();
					String[] tokens = read_tmp.split(";");	
					if (read_tmp.compareTo("") != 0)
						for (int idx=1 ; idx < tokens.length ; idx++) keys.add(tokens[idx].trim());
				}
				
				if (keys.size() > 0)
					for (String key:keys) TKDependenciesT.put(key, new ArrayList<String>());
				
				while (ficheiro.hasNextLine() && keys.size() > 0)
				{
					count_line++;		
					read_tmp = 	ficheiro.nextLine();
	
					if (read_tmp.compareTo("") != 0)
					{	
						String[] tokens = read_tmp.split(";");	
						String tk_name = new String(tokens[0].trim());
						ArrayList<String> tk_dep = new ArrayList<String>(); 
						ArrayList<String> tmp = null;
	
						for (int idx=1 ; idx < tokens.length ; idx++)
						{
							if (tokens[idx].trim().isEmpty() == false )
							{
								tk_dep.add( new String(tokens[idx].trim()) );
								tmp = TKDependenciesT.get(keys.get(idx-1));
								tmp.add(new String(tokens[idx].trim()));
							}
							else 
							{	
								tk_dep.add( new String("") );
								tmp = TKDependenciesT.get(keys.get(idx-1));
								tmp.add(new String(""));
							}
						}
						
						TKDependencies.put(tk_name, tk_dep);						
					}				
					else System.out.println("Empty line at " + count_line);	
				}
				
				/*System.out.println("ReadDataFromFiles: keys=" + keys +
						" TKDependencies=" + TKDependencies.toString() +
						" TKDependenciesT=" + TKDependenciesT.toString());
					*/
			}
			//System.out.println ("Reading input file ended. " + count_line + " lines read. With " + TKDependencies.size() + " Dependencies.");
			ficheiro.close();
		}
		catch (Exception e) 	{ 	e.printStackTrace(); 	}
		/*System.out.println(	"-------------------*-----------------------------" +
				" TKDependencies \n" + TKDependencies.toString() +
				"\n-------------------*-----------------------------");
			*/
		
		
		
		try
		{	
			Scanner ficheirobo = new Scanner(new File(businessObjects_filename),"UTF-8");
			String read_tmp = new String(""); 
			int count_line = 0;	
			BusinessObject bo;

			
			//Read header from ficheiroview
			if (ficheirobo != null) 
				if  (ficheirobo.hasNextLine()) read_tmp = ficheirobo.nextLine();
					else System.out.println("WARNING: business objects file without header.");
			
			while (ficheirobo.hasNextLine())
			{
				count_line++;		
				read_tmp = 	ficheirobo.nextLine();
				if (read_tmp.compareTo("") != 0)
				{
					String[] tokens = read_tmp.split(";");	
					bo = new BusinessObject(tokens[0].trim() , tokens[1].trim() , tokens[2].trim());
					BusinessObjects.add(bo);
				}
				else System.out.println("Empty line at " + count_line);		
			}
			//System.out.println ("Reading input file ended. " + count_line + " lines read. With " + BusinessObjects.size() + " business objects.");
			ficheirobo.close();
		}
		catch (Exception e) 	{ 	e.printStackTrace(); 	}
		/*System.out.println(	"-------------------*-----------------------------" +
				" BusinessObjects \n" + BusinessObjects.toString() +
				"\n-------------------*-----------------------------");
		*/
		
	}

	
	private static ActorRole SearchActors(String trim) {

		for (ActorRole ar:Actors)
		{
			if (ar.getName().compareTo(trim)==0) return (ar);
			
		}		
		
		return null;
	}

	private static ArrayList<Pool> SpecifyPools(ArrayList<ActorRole> actorsToProducePools)
	{
		ArrayList<Pool> poolsProduced = new ArrayList<Pool>();  
				
		for(ActorRole actor:actorsToProducePools) poolsProduced.add( new Pool(actor) );
		
		return(poolsProduced);
	}
	
	private static ArrayList<Pool> SpecifyPoolsSimplified()
	{
		ArrayList<Pool> poolsProduced = new ArrayList<Pool>();  
				
		poolsProduced.add( new Pool() );
		
		return(poolsProduced);
	}
		
	private static ArrayList<String> TransposeV(TransactionKind tk2StoreLane) 
	{
		ArrayList<String> returnV = new ArrayList<String>();
		
		String Tkname = tk2StoreLane.getName();
		
		
		
		
		return (returnV);		
	}
	
	private static ArrayList<Pool> SpecifyLanesAndOrganize(ArrayList<TransactionKind> TPTReceived , ArrayList<Pool> poolsReceived)
	{
		ArrayList<Pool> poolsProduced = poolsReceived;
		ActorRole arI , arE;
		
		for (TransactionKind tk2StoreLane:TPTReceived)
		{
			arI = tk2StoreLane.getInitiatorRole();
			arE = tk2StoreLane.getExecutorRole();
			
			for (Pool pool2Update: poolsProduced)
			{
				
				if (pool2Update.getName().getName().compareTo(arI.getName()) == 0)
				{
					Lane newLane = new Lane("Initiator " + tk2StoreLane.getName() ,"Initiator lane for TK " + tk2StoreLane.getName());
					
					for (PatternView TKpatternName: TKPatternViews)
					{	
						if (TKpatternName.getName().compareTo(tk2StoreLane.getName()) == 0)
						{
							boolean isFirst;	
							if ( pool2Update.getLanes().size() == 0 ) // if no executor added before
									isFirst = true;
							else	isFirst = false;
							
							switch (TKpatternName.getPattern())
							{
							case "HappyFlow":
								 	newLane = (new DEMOPatternHappyFlowInitiator()).CreateElements_and_Sequence(newLane , tk2StoreLane , MessageFlows , TKDependencies.get(tk2StoreLane.getName()) , TKpatternName , isFirst);
									break;
							case "HappyFlowAndDeclinationsAndRejections":
									newLane = (new DEMOPatternHappyFlowAndDeclinationsAndRejectionsInitiator()).CreateElements_and_Sequence(newLane , tk2StoreLane , MessageFlows , TKDependencies.get(tk2StoreLane.getName()) , TKpatternName , isFirst);
									break;
							case "Custom": 
									newLane = (new DEMOPatternCustomInitiator()).CreateElements_and_Sequence(newLane , tk2StoreLane , MessageFlows , TKDependencies.get(tk2StoreLane.getName()) , TKpatternName , isFirst);
									break;
							case "CustomHappyFlowOnly":
									newLane = (new DEMOPatternCustomInitiatorHappyFlowOnly()).CreateElements_and_Sequence(newLane , tk2StoreLane , MessageFlows , TKDependencies.get(tk2StoreLane.getName()) , TKpatternName , isFirst);
									break;								
							case "Complete":		
									newLane = (new DEMOPatternInitiator()).CreateElements_and_Sequence(newLane , tk2StoreLane , MessageFlows , TKDependencies.get(tk2StoreLane.getName()) , TKpatternName , isFirst);
									break;
							default:
							 		newLane = (new DEMOPatternHappyFlowInitiator()).CreateElements_and_Sequence(newLane , tk2StoreLane , MessageFlows , TKDependencies.get(tk2StoreLane.getName()) , TKpatternName , isFirst);
							}
						}
					}					
					pool2Update.AddLane(newLane);
				}
				if (pool2Update.getName().getName().compareTo(arE.getName()) == 0)
				{
					Lane newLane = new Lane("Executor " + tk2StoreLane.getName() ,"Executor lane for TK " + tk2StoreLane.getName());
					
					for (PatternView TKpatternName: TKPatternViews)
					{	
						if (TKpatternName.getName().compareTo(tk2StoreLane.getName()) == 0)
						{
							switch (TKpatternName.getPattern())
							{
							case "HappyFlow":
								 	newLane = (new DEMOPatternHappyFlowExecutor()).CreateElements_and_Sequence(newLane , tk2StoreLane , MessageFlows , TKDependenciesT.get(tk2StoreLane.getName()) , TKpatternName , true);
									break;
							case "HappyFlowAndDeclinationsAndRejections":
									newLane = (new DEMOPatternHappyFlowAndDeclinationsAndRejectionsExecutor()).CreateElements_and_Sequence(newLane , tk2StoreLane , MessageFlows , TKDependenciesT.get(tk2StoreLane.getName()) , TKpatternName , true);
									break;
							case "Custom": 
									newLane = (new DEMOPatternCustomExecutor()).CreateElements_and_Sequence(newLane , tk2StoreLane , MessageFlows , TKDependenciesT.get(tk2StoreLane.getName()) , TKpatternName, true);
									break;
							case "CustomHappyFlowOnly":
									newLane = (new DEMOPatternCustomExecutorHappyFlowOnly()).CreateElements_and_Sequence(newLane , tk2StoreLane , MessageFlows , TKDependenciesT.get(tk2StoreLane.getName()) , TKpatternName , true);
									break;																	
							case "Complete":									
									newLane = (new DEMOPatternExecutor()).CreateElements_and_Sequence(newLane , tk2StoreLane , MessageFlows , TKDependenciesT.get(tk2StoreLane.getName()) , TKpatternName, true);
									break;
							default:
									newLane = (new DEMOPatternHappyFlowExecutor()).CreateElements_and_Sequence(newLane , tk2StoreLane , MessageFlows , TKDependenciesT.get(tk2StoreLane.getName()) , TKpatternName, true);
							}
						}
					}		
					pool2Update.AddLane(newLane);
				}				
			}
		}
		
		for (Pool eachpool: poolsProduced)	eachpool.SpecifyDimensions();
	
		// Organize all lanes by level 
		for (Pool eachpool: poolsProduced)
			for (Lane lane: eachpool.getLanes())
				lane.OrganizeBPMNElementsByLevel();

		
		// Specify Sequence Flow between dependencies
		for (HashMap.Entry<String,ArrayList<String>> entry : TKDependenciesT.entrySet() )
		{
			String name_TKE = entry.getKey();
			ArrayList<String> deps = entry.getValue();
			
			for(int idx=0 ; idx < deps.size() ; idx++)
			{
				if (deps.get(idx).compareTo("") != 0)
				{
					Lane laneE_tk = SearchLane("Executor " + name_TKE , poolsProduced);
					Lane laneI_tn = SearchLane("Initiator " + keys.get(idx) , poolsProduced);		
					String type_dep = deps.get(idx);	
					connectDependencies(laneE_tk , laneI_tn , type_dep);
				}
			}
		}		
		
		return(poolsProduced);
	}
	

	
	private static ArrayList<Pool> SpecifyLanesAndOrganizeSimplified(ArrayList<TransactionKind> TPTReceived , ArrayList<Pool> poolsReceived)
	{
		ArrayList<Pool> poolsProduced = poolsReceived;
		ActorRole arI , arE;
		
		Pool pool2Update = poolsProduced.get(0);
		
		boolean initialTK = true;
		
		for (TransactionKind tk2StoreLane:TPTReceived)
		{
			arI = tk2StoreLane.getInitiatorRole();
			arE = tk2StoreLane.getExecutorRole();
			
			// Initiator
			Lane newLaneI = new Lane(arI.getName() + " - Initiator " + tk2StoreLane.getName() ,"Initiator lane for TK " + tk2StoreLane.getName());
			
			for (PatternView TKpatternName: TKPatternViews)
			{	
				if (TKpatternName.getName().compareTo(tk2StoreLane.getName()) == 0)
				{
					switch (TKpatternName.getPattern())
					{
					case "HappyFlow":
						 	newLaneI = (new DEMOPatternHappyFlowInitiatorSimplified()).CreateElements_and_Sequence(newLaneI , tk2StoreLane , MessageFlows , TKDependencies.get(tk2StoreLane.getName()) , TKpatternName , initialTK);
							break;
					case "HappyFlowAndDeclinationsAndRejections":
							newLaneI = (new DEMOPatternHappyFlowAndDeclinationsAndRejectionsInitiatorSimplified()).CreateElements_and_Sequence(newLaneI , tk2StoreLane , MessageFlows , TKDependencies.get(tk2StoreLane.getName()) , TKpatternName , initialTK);
							break;
					case "Custom": 
							newLaneI = (new DEMOPatternCustomInitiatorSimplified()).CreateElements_and_Sequence(newLaneI , tk2StoreLane , MessageFlows , TKDependencies.get(tk2StoreLane.getName()) , TKpatternName , initialTK);
							break;
					case "CustomHappyFlowOnly":
							newLaneI = (new DEMOPatternCustomInitiatorHappyFlowOnlySimplified()).CreateElements_and_Sequence(newLaneI , tk2StoreLane , MessageFlows , TKDependencies.get(tk2StoreLane.getName()) , TKpatternName , initialTK);
							break;								
					case "Complete":		 //not implemented yet
							// newLaneI = (new DEMOPatternInitiatorSimplified()).CreateElements_and_Sequence(newLaneI , tk2StoreLane , MessageFlows , TKDependencies.get(tk2StoreLane.getName()) , TKpatternName , initialTK);
							newLaneI = (new DEMOPatternHappyFlowAndDeclinationsAndRejectionsInitiatorSimplified()).CreateElements_and_Sequence(newLaneI , tk2StoreLane , MessageFlows , TKDependencies.get(tk2StoreLane.getName()) , TKpatternName , initialTK);

							break;
					default:
					 		newLaneI = (new DEMOPatternHappyFlowInitiatorSimplified()).CreateElements_and_Sequence(newLaneI , tk2StoreLane  , MessageFlows , TKDependencies.get(tk2StoreLane.getName()) , TKpatternName , initialTK);
					}
				}						
			}
			pool2Update.AddLane(newLaneI);


			// Executor	
			Lane newLaneE = new Lane(arE.getName() + " - Executor " + tk2StoreLane.getName() ,"Executor lane for TK " + tk2StoreLane.getName());
			
			for (PatternView TKpatternName: TKPatternViews)
			{	
				if (TKpatternName.getName().compareTo(tk2StoreLane.getName()) == 0)
				{
					switch (TKpatternName.getPattern())
					{
					case "HappyFlow":
						 	newLaneE = (new DEMOPatternHappyFlowExecutorSimplified()).CreateElements_and_Sequence(newLaneE , tk2StoreLane , MessageFlows , TKDependenciesT.get(tk2StoreLane.getName()) , TKpatternName , initialTK);
							new DEMOPatternHappyFlowExecutorSimplified().connectwithInitiator(newLaneI , newLaneE);
							break;
					case "HappyFlowAndDeclinationsAndRejections":
							newLaneE = (new DEMOPatternHappyFlowAndDeclinationsAndRejectionsExecutorSimplified()).CreateElements_and_Sequence(newLaneE , tk2StoreLane , MessageFlows , TKDependenciesT.get(tk2StoreLane.getName()) , TKpatternName , initialTK);
							new DEMOPatternHappyFlowAndDeclinationsAndRejectionsExecutorSimplified().connectwithInitiator(newLaneI , newLaneE);
							break;
					case "Custom": 
							newLaneE = (new DEMOPatternCustomExecutorSimplified()).CreateElements_and_Sequence(newLaneE , tk2StoreLane , MessageFlows , TKDependenciesT.get(tk2StoreLane.getName()) , TKpatternName , initialTK);
							new DEMOPatternCustomExecutorSimplified().connectwithInitiator(newLaneI , newLaneE);
							break;
					case "CustomHappyFlowOnly":
							newLaneE = (new DEMOPatternCustomExecutorHappyFlowOnlySimplified()).CreateElements_and_Sequence(newLaneE , tk2StoreLane , MessageFlows ,  TKDependenciesT.get(tk2StoreLane.getName()) , TKpatternName , initialTK);
							new DEMOPatternCustomExecutorHappyFlowOnlySimplified().connectwithInitiator(newLaneI , newLaneE);
							break;																	
					case "Complete": //not implemented yet									
							//newLaneE = (new DEMOPatternExecutorSimplified()).CreateElements_and_Sequence(newLaneE , tk2StoreLane , MessageFlows , TKDependenciesT.get(tk2StoreLane.getName()) , TKpatternName , initialTK);
							//new DEMOPatternExecutorSimplified().connectwithInitiator(newLaneI , newLaneE);
							newLaneE = (new DEMOPatternHappyFlowAndDeclinationsAndRejectionsExecutorSimplified()).CreateElements_and_Sequence(newLaneE , tk2StoreLane , MessageFlows , TKDependenciesT.get(tk2StoreLane.getName()) , TKpatternName , initialTK);
							new DEMOPatternHappyFlowAndDeclinationsAndRejectionsExecutorSimplified().connectwithInitiator(newLaneI , newLaneE);						
							break;
					default:
							newLaneE = (new DEMOPatternHappyFlowExecutorSimplified()).CreateElements_and_Sequence(newLaneE , tk2StoreLane , MessageFlows , TKDependenciesT.get(tk2StoreLane.getName()) , TKpatternName, initialTK);
							new DEMOPatternHappyFlowExecutorSimplified().connectwithInitiator(newLaneI , newLaneE);
					}
				}
			}		
			pool2Update.AddLane(newLaneE);
			
			initialTK = false;
			
		}
		
		for (Pool eachpool: poolsProduced)	eachpool.SpecifyDimensions();
	
		// Organize all lanes by level 
		for (Pool eachpool: poolsProduced)
			for (Lane lane: eachpool.getLanes())
				lane.OrganizeBPMNElementsByLevel();

		
		
		
		
		// Specify Sequence Flow between dependencies
		for (HashMap.Entry<String,ArrayList<String>> entry : TKDependenciesT.entrySet() )
		{
			String name_TKE = entry.getKey();
			ArrayList<String> deps = entry.getValue();
			
			for(int idx=0 ; idx < deps.size() ; idx++)
			{
				if (deps.get(idx).compareTo("") != 0)
				{
					Lane laneE_tk = SearchLane("Executor " + name_TKE , poolsProduced);
					Lane laneI_tn = SearchLane("Initiator " + keys.get(idx) , poolsProduced);		
					String type_dep = deps.get(idx);	
					connectDependencies(laneE_tk , laneI_tn , type_dep);
				}
			}
		}		
		
		return(poolsProduced);
	}

	
	private static Lane SearchLane(String name_l , ArrayList<Pool> Pools_t)
	{
		Lane lane_R = null;
		for (Pool pool_t:Pools_t)
			for (Lane lane_t:pool_t.getLanes())
				if (lane_t.getName().endsWith(name_l) == true ) return (lane_t);
			
		return(lane_R);
	}
	
	 public static QName SearchQName(Lane lane , String text)
	 {
		 QName QName_response = null;
		 for (BPMNElement elem:lane.getBPMNElements())
		 {
			 if (elem.getName().compareTo(text) == 0) QName_response = elem.getQname_BPMNElement();
		 }
		 return(QName_response);
	 }
	 
	 public static QName SearchQNamebyDescription(Lane lane , String text)
	 {
		 QName QName_response = null;
		 for (BPMNElement elem:lane.getBPMNElements())
		 {
			 if (elem.getDescription().compareTo(text) == 0) QName_response = elem.getQname_BPMNElement();
		 }
		 return(QName_response);
	 }
	 
	 public static void connectDependencies(Lane laneE_tk , Lane laneI_tn , String dependency)
	 {
		 QName out_e = SearchQName( laneE_tk , "DIVERGE_" + dependency );
		 QName in_i  = SearchQName( laneI_tn , "INITIAL" );
		 QName out_i = SearchQNamebyDescription( laneI_tn , "Intermediate_Accept");
		 //QName out_i = SearchQNamebyDescription( laneI_tn , "Accept");
		 QName in_e  = SearchQName( laneE_tk , "CONVERGE_" + dependency );
		 		 
		 laneE_tk.addSequenceFlow(new BPMNSequenceFlow( out_e , in_i ));
		 laneI_tn.addSequenceFlow(new BPMNSequenceFlow( out_i , in_e ));
		 
		 // SpecifyIncoming-Outgoing
		 DEMOPatternExecutor tmp = new DEMOPatternExecutor(); 
		 laneE_tk = tmp.SpecifyIncoming_Outgoing_BetweenLanes(laneE_tk , laneI_tn);
		 laneI_tn = tmp.SpecifyIncoming_Outgoing_BetweenLanes(laneI_tn , laneE_tk);
		 
		// System.out.println("connectDependencies: " + laneE_tk.getName() + " with " + laneI_tn.getName() + " on " + dependency);
		// System.out.println("connectDependencies: elements =" + out_e + " -> " +  in_i);
		// System.out.println("connectDependencies: and elements =" + out_i + " -> " + in_e);
	 }	
		
	
	/**
	 * @param poolsReceived
	 * @param event 
	 */
	private static String ProduceBPMN2XML(ArrayList<Pool> poolsReceived)
	{
		
		
		
		 ObjectFactory factory = new ObjectFactory();
		 org.omg.spec.bpmn._20100524.di.ObjectFactory factoryBPMN = new org.omg.spec.bpmn._20100524.di.ObjectFactory();
		 org.omg.spec.dd._20100524.dc.ObjectFactory factoryDD = new org.omg.spec.dd._20100524.dc.ObjectFactory(); 
		 org.omg.spec.dd._20100524.di.ObjectFactory factoryDI = new org.omg.spec.dd._20100524.di.ObjectFactory();
		 camunda.spec.bpmn.ObjectFactory factoryCamundaEngine = new camunda.spec.bpmn.ObjectFactory(); 

		 
		 double X = 100;
		 double Y = 50;
		 
		 try {
			 QName qname_Collaboration = new QName("coll-DEMO2BPMN");
 			 QName qname_Diagram = new QName("diagram-DEMO2BPMN");	
			 QName qname_Plane = new QName("plane-DEMO2BPMN");
			 
			 JAXBElement<TCollaboration> element_coll;
			 JAXBElement<TProcess> element_process;
			 JAXBElement<TSendTask> element_sendtask;
			 JAXBElement<TManualTask> element_manualtask;
			 JAXBElement<TReceiveTask> element_receivetask;
			 JAXBElement<TUserTask> element_usertask;
			 JAXBElement<TTask> element_task;			 
			 JAXBElement<TStartEvent> element_startevt;
			 JAXBElement<TEndEvent> element_endevt;
			 JAXBElement<TTimerEventDefinition> element_TimerCatchevt;
			 JAXBElement<TIntermediateCatchEvent> element_IntermediateCatchevt;
			 JAXBElement<TIntermediateThrowEvent> element_IntermediateThrowevt;
			 JAXBElement<TMessageEventDefinition> element_MessageEventDefinition;
			 JAXBElement<TTerminateEventDefinition> element_TerminateEventDefinition;
			 JAXBElement<TCompensateEventDefinition> element_CompensateEventDefinition; 
			 JAXBElement<TExclusiveGateway> element_ExclusiveGateway;
			 JAXBElement<TEventBasedGateway> element_EventBasedGateway;
			 JAXBElement<TParallelGateway> element_ParallelGateway;
			 JAXBElement<TInclusiveGateway> element_InclusiveGateway;
			 JAXBElement<TFlowNode> flow;
			 JAXBElement<TBoundaryEvent> element_boundaryevent;			 

			 ArrayList<QName> GatewaysWithExpressions = new ArrayList<QName>(); 
					 
					 

			 
			 BPMNDiagram bpmndiagram = factoryBPMN.createBPMNDiagram();
			 BPMNPlane bpmnplane = factoryBPMN.createBPMNPlane();
			 bpmndiagram.setId(qname_Diagram.toString());
			 bpmndiagram.setBPMNPlane(bpmnplane);		 			 
				
			 /* LOAD MODEL */
			 TDefinitions definitions = factory.createTDefinitions();			 
			 TCollaboration collaboration = factory.createTCollaboration();
			 collaboration.setId(qname_Collaboration.toString());

			 element_coll = factory.createCollaboration(collaboration);
 			 definitions.getRootElement().add(element_coll);
 			 
 			 bpmnplane.setBpmnElement(qname_Collaboration);
			 bpmnplane.setId(qname_Plane.toString());			 
			 definitions.setExporter("DEMO2BPMN-2021");
			 if (camundaEngine) definitions.setTargetNamespace("http://bpmn.io/schema/bpmn");
			// definitions.getOtherAttributes().put(new QName("xmlns:camunda") , "http://camunda.org/schema/1.0/bpmn" );
			 definitions.getBPMNDiagram().add(bpmndiagram);
			 
			 int indexPool = 1;
			 int indexLane = 0;

			 for (Pool pool_toAdd:poolsReceived)
			 {
				 X =  indexPool * pool_toAdd.getOffSet_X_Position_of_a_Pool();

				 TParticipant participant = factory.createTParticipant();
				 participant.setName(pool_toAdd.getShortName());
				 participant.setProcessRef(pool_toAdd.getQname_Process());
				 participant.setId(pool_toAdd.getQname_Participant().toString());
				 collaboration.getParticipant().add(participant);

				 BPMNShape bpmnShape = factoryBPMN.createBPMNShape();
				 Bounds bounds = factoryDD.createBounds();				 
				 bounds.setX(X);
				 bounds.setY(Y);
				 bounds.setWidth(pool_toAdd.getWidth());
				 bounds.setHeight(pool_toAdd.getHeight());				 
				 
				 bpmnShape.setBpmnElement(pool_toAdd.getQname_Participant());		 
				 bpmnShape.setIsHorizontal(true);
				 bpmnShape.setId(pool_toAdd.getQname_Shape().toString());		 	 
				 bpmnShape.setBounds(bounds); 				
				 bpmnplane.getDiagramElement().add( factoryBPMN.createBPMNShape(bpmnShape));

				 TProcess process = factory.createTProcess();
				 process.setIsExecutable(Boolean.TRUE);
				 process.setId(pool_toAdd.getQname_Process().toString());
 				 
 				 TLaneSet laneset = factory.createTLaneSet();	
 				 laneset.setId(pool_toAdd.getQname_LaneSet().toString());
 				 process.getLaneSet().add(laneset);
 				 
 				 
 		
 				 double relative_lane_position = 0;

					 
 				 for (Lane lane_toAdd:pool_toAdd.getLanes())
 				 { 				
 					 X = indexPool * pool_toAdd.getOffSet_X_Position_of_a_Pool();

 					 
 					 TLane lane = factory.createTLane();
 					 lane.setId(lane_toAdd.getQname_Lane().toString());
 					 lane.setName(lane_toAdd.getName());
 					 laneset.getLane().add(lane);
 					 
 					 BPMNShape bpmnShape_l = factoryBPMN.createBPMNShape();
 					 Bounds bounds_l = factoryDD.createBounds(); 					 
 					 bounds_l.setX(X+30); 					
 					 bounds_l.setY(Y + relative_lane_position);
 					 //bounds_l.setWidth(lane_toAdd.getWidth() - 30); // 30 to guarantee space for lane name
 					 bounds_l.setWidth(pool_toAdd.getWidth() - 30); // 30 to guarantee space for lane name
 					 bounds_l.setHeight(lane_toAdd.getHeight());		 				
 					 bpmnShape_l.setBpmnElement(lane_toAdd.getQname_Lane());		 
 					 bpmnShape_l.setIsHorizontal(true);
 					 bpmnShape_l.setId(lane_toAdd.getQname_Shape().toString());		 	 
 					 bpmnShape_l.setBounds(bounds_l); 				
 					 bpmnplane.getDiagramElement().add( factoryBPMN.createBPMNShape(bpmnShape_l));
 					 
 					 for (HashMap.Entry<Integer, ArrayList<BPMNElement>> entry : lane_toAdd.getOrganizedBPMNElementByLevel().entrySet()) 
 					 {
 	 					 X = (indexPool * pool_toAdd.getOffSet_X_Position_of_a_Pool()) + 
 	 						 (indexLane * lane_toAdd.getOffSet_X_Position_of_a_Lane()) ;
 			
 	 					 
	 					 // Add all activities
	 					 int n_elements = 0;
	 					 for (BPMNElement element_toAdd:entry.getValue())
	 					 {
	 						 double X_element = 0;
	 						 double Y_element = 0;
	 						 
 	 			 			 BPMNShape bpmnShape_element = factoryBPMN.createBPMNShape();
 	 	 					 Bounds bounds_element = factoryDD.createBounds();		
	 						 	 
 	 	 					 X += element_toAdd.getOffset() * lane_toAdd.getWidth();
 	 	 					 
 	 	 					 
	 						 if (element_toAdd instanceof Activity)
	 						 {
	 							Activity act_toAdd = (Activity) element_toAdd;	 							
	 							TTask task = null;
	 							
	 							if ( act_toAdd.getType() == ActivityType.SendTask.ordinal() ) 
	 							{
	 								task = factory.createTSendTask();
	 	 	 	 					element_sendtask = factory.createSendTask((TSendTask) task);
	 	 							process.getFlowElement().add(element_sendtask); 							 
	 							}
	 							else if ( act_toAdd.getType() == ActivityType.ManualTask.ordinal() )
	 							{ 
	 								task = 	factory.createTManualTask(); 								 								
		 	 	 					element_manualtask = factory.createManualTask((TManualTask) task);
	 	 							process.getFlowElement().add(element_manualtask);  	 							
	 							}
	 							else if ( act_toAdd.getType() == ActivityType.Task.ordinal() )
	 							{	 								
	 								task = 	factory.createTTask(); 							
		 	 	 					element_task = factory.createTask(task);
	 	 							process.getFlowElement().add(element_task);  	 							
	 							}
	 							else if ( act_toAdd.getType() == ActivityType.Compensation.ordinal() )
	 							{ 
	 								task = 	factory.createTTask(); 			
	 	 	 						task.setIsForCompensation(true); 
		 	 	 					element_task = factory.createTask(task);
	 	 							process.getFlowElement().add(element_task);
	 	 							
	 	 	 	 					// store objects in Association for future rendering with association
	 	 	 	 					act_toAdd.setItself_forRendering(task);
	 							}
	 							else if ( act_toAdd.getType() == ActivityType.UserTask.ordinal())
	 							{
	 								
	 								task = 	factory.createTUserTask();
	 
	 								
	 								if (camundaEngine)
	 								{
	 						
	 									TExtensionElements extElements = factory.createTExtensionElements();
		 								formData fm = factoryCamundaEngine.createformData();
		 								
	 									boolean anyform2add = false;
	 									for (BusinessObject bo2check:BusinessObjects)
	 									{
	 										
	 									/*	System.out.println("act = " + act_toAdd.toString());
	 										System.out.println("bo = " + bo2check.toString());
	 										System.out.println("lane_toadd = " + lane_toAdd.toString());
	 										System.out.println("pool_toAdd = " + pool_toAdd.toString());  
	 									*/	
	 										if ( 
	 												(lane_toAdd.getName().contains(bo2check.getTransactionKind()) == true ) &&
	 												(act_toAdd.getName().compareTo(bo2check.getTransactionStep()) == 0)
	 											)
	 											   {
	 												   formField field = factoryCamundaEngine.createformField();
	 												   	
	 												 /*  field.setId( ConvertString2Camunda(bo2check.getTransactionKind()) +
	 														        ConvertString2Camunda(bo2check.getTransactionStep()) + 
	 														        ConvertString2Camunda(bo2check.getName())   );*/
                                                       field.setId( bo2check.getName().replaceAll("[^\\p{ASCII}]", "") );

	 												   field.setLabel(bo2check.getName());
	 												   field.setType("string");	 	
	 				 								   fm.getFormField().add(field);
	 				 								   
	 				 								   anyform2add = true;
	 											   }
	 									}
	 									
	 									if (anyform2add)
	 									{
		 									extElements.getformData().add(fm);
		 									task.setExtensionElements(extElements);
	 									}

	 								}
	 								
	 									
		 	 	 					element_usertask = factory.createUserTask((TUserTask) task);		 	 	 					
		 	 	 					
		 	 	 							 	 	 					
	 	 							process.getFlowElement().add(element_usertask);
	 	 							
	 							}	 							
	 							else if ( act_toAdd.getType() == ActivityType.SendTaskWithBoundaryRollback.ordinal() )  
	 							{ 
	 								// This a specific type of activity that has other element attached to it
 									task = 	factory.createTSendTask();
 									
 									if (act_toAdd.hasAttachedEvent())
 									{
	 									TBoundaryEvent tbound_evt = factory.createTBoundaryEvent();	 	
	 									QName id_boundaryevent = act_toAdd.getEvent_boundary().getQname_BPMNElement(); 
	 									tbound_evt.setId(id_boundaryevent.toString());
	 									tbound_evt.setAttachedToRef(element_toAdd.getQname_BPMNElement());
	 				 				    TCompensateEventDefinition tmessage = factory.createTCompensateEventDefinition();	 				 				    
	 				 				    tmessage.setId((new QName("Attached_compensation_event_" +  UUID.randomUUID().toString())).toString());	 				 				 
	 				 				    element_CompensateEventDefinition = factory.createCompensateEventDefinition(tmessage); 
	 				 				    ((TBoundaryEvent) tbound_evt).getEventDefinition().add(element_CompensateEventDefinition);	 	
	 									element_boundaryevent = factory.createBoundaryEvent(tbound_evt); 
	 									process.getFlowElement().add(element_boundaryevent); 	
	 									
	 									BPMNShape bpmnShape_element_aux = factoryBPMN.createBPMNShape();
		 	 	 	 					Bounds bounds_element_aux = factoryDD.createBounds();

		 	 							// draw boundary event straightaway
		 		 	 					X_element = (X + Activity.offsetInitial) + (Activity.distance_between_activities * n_elements) + (Activity.Width / 2);
		 		 	 					Y_element = Y + relative_lane_position + 
		 	 							 		     ( (lane_toAdd.getHeight() / lane_toAdd.getN_levels()) * (entry.getKey() - 1) )  +		 	 							 		
		 	 							 		     ( (lane_toAdd.getHeight() / lane_toAdd.getN_levels()) / 2 )
		 	 							 		     + (Activity.Height / 4) ;
		 		 	 					bounds_element_aux.setX(X_element);
		 		 	 					bounds_element_aux.setY(Y_element);	
			 	 						bounds_element_aux.setWidth(Event.Width); 
			 	 						bounds_element_aux.setHeight(Event.Height);
			 	 						
			 	 						BoundaryEvent update_elem = act_toAdd.getEvent_boundary();
			 	 						update_elem.setX(X_element);
			 	 						update_elem.setY(Y_element);
			 	 						
			 	 						bpmnShape_element_aux.setBpmnElement(id_boundaryevent);		  	 					 
		 		 	 					bpmnShape_element_aux.setId( new QName("Attached_event_di_" +  UUID.randomUUID().toString()).toString() );		 	 
		 	 	 	 					bpmnShape_element_aux.setBounds(bounds_element_aux); 				
		 	 	 	 					bpmnplane.getDiagramElement().add( factoryBPMN.createBPMNShape(bpmnShape_element_aux));
		 	 	 	 							 	 	 	 					
 									}
 									
	 	 	 	 					element_sendtask = factory.createSendTask((TSendTask) task);	 								
	 	 	 	 					process.getFlowElement().add(element_sendtask);  	 		
	 							}
	 							else if ( act_toAdd.getType() == ActivityType.ManualTaskWithBoundaryRollback.ordinal() )
	 							{ 
	 								task = 	factory.createTManualTask();
 									if (act_toAdd.hasAttachedEvent())
 									{
	 									TBoundaryEvent tbound_evt = factory.createTBoundaryEvent();	 	
	 									QName id_boundaryevent = act_toAdd.getEvent_boundary().getQname_BPMNElement(); 
	 									tbound_evt.setId(id_boundaryevent.toString());
	 									tbound_evt.setAttachedToRef(element_toAdd.getQname_BPMNElement());
	 				 				    TCompensateEventDefinition tmessage = factory.createTCompensateEventDefinition();
	 				 				    QName id_compensation = new QName("Attached_compensation_event_" +  UUID.randomUUID().toString()); 
	 				 				    tmessage.setId(id_compensation.toString());	 				 				 
	 				 				    element_CompensateEventDefinition = factory.createCompensateEventDefinition(tmessage); 
	 				 				    ((TBoundaryEvent) tbound_evt).getEventDefinition().add(element_CompensateEventDefinition);	 		 								
	 									element_boundaryevent = factory.createBoundaryEvent(tbound_evt); 
	 									process.getFlowElement().add(element_boundaryevent); 
		 	 							
		 		 	 					BPMNShape bpmnShape_element_aux = factoryBPMN.createBPMNShape();
		 	 	 	 					Bounds bounds_element_aux = factoryDD.createBounds();

		 	 							// draw boundary event straightaway
		 		 	 					X_element = (X + Activity.offsetInitial) + (Activity.distance_between_activities * n_elements) + (Activity.Width / 2);
		 		 	 					Y_element = Y + relative_lane_position + 
		 	 							 		     ( (lane_toAdd.getHeight() / lane_toAdd.getN_levels()) * (entry.getKey() - 1) )  +		 	 							 		
		 	 							 		     ( (lane_toAdd.getHeight() / lane_toAdd.getN_levels()) / 2 )
		 	 							 		     + (Activity.Height / 4) ;		 		 	 							 	 	 	 					 
		 		 	 					bounds_element_aux.setX(X_element);
		 		 	 					bounds_element_aux.setY(Y_element);	
			 	 						bounds_element_aux.setWidth(Event.Width); 
			 	 						bounds_element_aux.setHeight(Event.Height);

			 	 						BoundaryEvent update_elem = act_toAdd.getEvent_boundary();
			 	 						update_elem.setX(X_element);
			 	 						update_elem.setY(Y_element);

		 		 	 					bpmnShape_element_aux.setBpmnElement(id_boundaryevent);		  	 					 
		 		 	 					bpmnShape_element_aux.setId( new QName("Attached_event_di_" +  UUID.randomUUID().toString()).toString() );		 	 
		 	 	 	 					bpmnShape_element_aux.setBounds(bounds_element_aux); 				
		 	 	 	 					bpmnplane.getDiagramElement().add( factoryBPMN.createBPMNShape(bpmnShape_element_aux));
		 	 	 	 					
 									}
	 								element_manualtask = factory.createManualTask((TManualTask) task);
	 	 							process.getFlowElement().add(element_manualtask); 

 									
	 							}

	 							// if other type of Activity add another if clause
	 							// else if (...) { ...}
	 							
	 							
	 							if (task != null) // add to flow
	 							{
	 	 							task.setId(act_toAdd.getQname_BPMNElement().toString());
	 	 	 						task.setName(act_toAdd.getName());
	 	 	 						
	 	 	 						// For prototyping assigning a specific user to a task
	 	 	 						// for production assign to a group (roles) and then assign users to the group
	 	 	 						if (camundaEngine)	task.getOtherAttributes().put(new QName("camunda:assignee"), ConvertString2Camunda(lane_toAdd.getName() ) );
	 	 	 							 	 	 					
	 	 	 						
	 	 	 						// Specify incoming flows of task
	 	 	 						for (QName in_flow: element_toAdd.getQname_flow_Incoming())	task.getIncoming().add(in_flow);
	 	 	 						// Specify incoming flows of task
	 	 	 						for (QName out_flow: element_toAdd.getQname_flow_Outgoing()) task.getOutgoing().add(out_flow);
	 	 	 						
	 	 	 						lane_toAdd.StoreObject4FlowRenderingInPool((Object) task , element_toAdd , pool_toAdd);

	 	 	 						lane.getFlowNodeRef().add((JAXBElement<Object>)  factory.createTLaneFlowNodeRef((TFlowNode)  task ));
	 							}	 					
	 							
	 	 						 bounds_element.setWidth(Activity.Width); 
	 	 						 bounds_element.setHeight(Activity.Height);
		 						 // draw the gateway
		 						 X_element = (X + Activity.offsetInitial) + (Activity.distance_between_activities * n_elements);
		 	 					 Y_element = Y + relative_lane_position + 
	 							 		     ( (lane_toAdd.getHeight() / lane_toAdd.getN_levels()) * (entry.getKey() - 1) )  +		 	 							 		
	 							 		     ( (lane_toAdd.getHeight() / lane_toAdd.getN_levels()) / 2 )
	 							 		     - (Activity.Height / 2) ;
		 	 					 bounds_element.setX(X_element);
		 	 					 bounds_element.setY(Y_element);	
		 	 					 bpmnShape_element.setBpmnElement(element_toAdd.getQname_BPMNElement());		  	 					 
		 	 					 bpmnShape_element.setId(element_toAdd.getQname_Shape().toString());		 	 
	 	 	 					 bpmnShape_element.setBounds(bounds_element); 				
	 	 	 					 bpmnplane.getDiagramElement().add( factoryBPMN.createBPMNShape(bpmnShape_element)); 	

	 						 }
	 						 else if (element_toAdd instanceof Event)
	 						 {
	 							 Event event_toAdd = (Event) element_toAdd;
	 							 TEvent event = null;
	 							 if( event_toAdd.getType() == EventType.Start.ordinal())
	 							 { 		 								 
	 								 event = factory.createTStartEvent();	 				 				 
		 	 	 					 TMessageEventDefinition tmessage = factory.createTMessageEventDefinition();	 				 				 
	 				 				 tmessage.setId(event_toAdd.getQname_BPMNElement_EventDefinition().toString());

	 								 if (camundaEngine)
	 								 {	 									 
	 									  JAXBElement<TMessage>  element_message2Execute;				 
	 									  TMessage message2Execute = factory.createTMessage();
		 								  String IDMessage = "Message_" +  UUID.randomUUID().toString();
		 								  message2Execute.setId(IDMessage);
		 								  message2Execute.setName(IDMessage + "_" + event_toAdd.getName());
			 				 			  tmessage.setMessageRef(new QName(IDMessage));
			 				 			  element_message2Execute = factory.createMessage(message2Execute);
			 				 			  definitions.getRootElement().add(element_message2Execute);
	 								 }	
	 				 				 
	 				 				 element_MessageEventDefinition = factory.createMessageEventDefinition(tmessage); 
	 				 				 ((TStartEvent) event).getEventDefinition().add(element_MessageEventDefinition);	 				 		 	 	 					 
		 	 	 					 element_startevt = factory.createStartEvent((TStartEvent) event);
	 								 process.getFlowElement().add(element_startevt);
	 								 
	 							 }
	 							 else if ( event_toAdd.getType() == EventType.IntermediateCatchEvent.ordinal())
	 							 {
	 							     event = factory.createTIntermediateCatchEvent();
	 				 				 element_IntermediateCatchevt = factory.createIntermediateCatchEvent((TIntermediateCatchEvent) event);
	 								 process.getFlowElement().add(element_IntermediateCatchevt);
	 							 }
	 							 else if ( event_toAdd.getType() == EventType.IntermediateCatchTimerEvent.ordinal())
	 							 {
	 							     event = factory.createTIntermediateCatchEvent();
	 							     TExpression expression = factory.createTExpression();
	 							     expression.getContent().add("PT0S");
	 							     TTimerEventDefinition ttimer = factory.createTTimerEventDefinition();
	 							     ttimer.setTimeDuration(expression);	 	
	 							     
	 							     element_TimerCatchevt = factory.createTimerEventDefinition( ttimer);	 							     
	 							    ((TIntermediateCatchEvent) event).getEventDefinition().add(element_TimerCatchevt);	 							    
	 				 				 element_IntermediateCatchevt = factory.createIntermediateCatchEvent((TIntermediateCatchEvent) event);	 							     
	 								 process.getFlowElement().add(element_IntermediateCatchevt);
	 							 }	 								 							
	 							 else if ( event_toAdd.getType() == EventType.IntermediateThrowEvent.ordinal())
	 							 { 
	 							     event = factory.createTIntermediateThrowEvent();
	 				 				 element_IntermediateThrowevt = factory.createIntermediateThrowEvent((TIntermediateThrowEvent) event);
	 								 process.getFlowElement().add(element_IntermediateThrowevt);
	 							 }	 	
	 							 else if ( event_toAdd.getType() == EventType.End.ordinal())
	 							 { 
	 							     event = factory.createTEndEvent();
	 				 				 element_endevt = factory.createEndEvent((TEndEvent) event);
	 								 process.getFlowElement().add(element_endevt);
	 							 }
	 							 else if ( event_toAdd.getType() == EventType.IntermediateMessageCatchEvent.ordinal())
	 							 { 	 								 
	 								 event = factory.createTIntermediateCatchEvent();
	 				 				 TMessageEventDefinition tmessage = factory.createTMessageEventDefinition();	 				 				 
	 				 				 tmessage.setId(event_toAdd.getQname_BPMNElement_EventDefinition().toString());
	 				 				 
	 								 if (camundaEngine)
	 								 {	 									 
	 									  JAXBElement<TMessage>  element_message2Execute;				 
	 									  TMessage message2Execute = factory.createTMessage();
		 								  String IDMessage = "Message_" +  UUID.randomUUID().toString();
		 								  message2Execute.setId(IDMessage);
		 								  message2Execute.setName(IDMessage + "_" + event_toAdd.getName());
			 				 			  tmessage.setMessageRef(new QName(IDMessage));
			 				 			  element_message2Execute = factory.createMessage(message2Execute);
			 				 			  definitions.getRootElement().add(element_message2Execute);
	 								 }	 
	 				 				 
	 				 				 element_MessageEventDefinition = factory.createMessageEventDefinition(tmessage); 
	 				 				 ((TIntermediateCatchEvent) event).getEventDefinition().add(element_MessageEventDefinition);	 				 				 	 				 				 
	 				 				 element_IntermediateCatchevt = factory.createIntermediateCatchEvent((TIntermediateCatchEvent) event);
	 								 process.getFlowElement().add(element_IntermediateCatchevt);
	 							 }
	 							 else if ( event_toAdd.getType() == EventType.TerminateAll.ordinal())
	 							 {
	 							     event = factory.createTIntermediateCatchEvent();
	 				 				 TTerminateEventDefinition tmessage = factory.createTTerminateEventDefinition();	 				 				 
	 				 				 tmessage.setId(event_toAdd.getQname_BPMNElement_EventDefinition().toString());	 				 				 
	 				 				 element_TerminateEventDefinition = factory.createTerminateEventDefinition(tmessage); 
	 				 				 ((TIntermediateCatchEvent) event).getEventDefinition().add(element_TerminateEventDefinition);	 				 				 
	 				 				 element_IntermediateCatchevt = factory.createIntermediateCatchEvent((TIntermediateCatchEvent) event);
	 								 process.getFlowElement().add(element_IntermediateCatchevt);
	 								 
	 							 }
	 							 else if ( event_toAdd.getType() == EventType.IntermediateThrowCompensationEvent.ordinal())
	 							 {
	 								 event = factory.createTIntermediateThrowEvent();
	 				 				 TCompensateEventDefinition tmessage = factory.createTCompensateEventDefinition();	 				 				 
	 				 				 tmessage.setId(event_toAdd.getQname_BPMNElement_EventDefinition().toString());	 				 				 
	 				 				 element_CompensateEventDefinition = factory.createCompensateEventDefinition(tmessage); 
	 				 				 ((TIntermediateThrowEvent) event).getEventDefinition().add(element_CompensateEventDefinition);	 				 				 
	 				 				 element_IntermediateThrowevt = factory.createIntermediateThrowEvent((TIntermediateThrowEvent) event);
	 								 process.getFlowElement().add(element_IntermediateThrowevt);
	 							 }
	  							 // if other type of Event add another if clause
	 							// else if (...) { ...}
		 									 							
	 							if (event != null) // add to flow
	 							{
	 				 				 event.setId(event_toAdd.getQname_BPMNElement().toString());
	 				 				 event.setName(event_toAdd.getName());
	 				 				 
	 	 	 						// Specify incoming flows of event
	 	 	 						for (QName in_flow: element_toAdd.getQname_flow_Incoming())	event.getIncoming().add(in_flow);
	 	 	 						// Specify incoming flows of event
	 	 	 						for (QName out_flow: element_toAdd.getQname_flow_Outgoing()) event.getOutgoing().add(out_flow);
	 	 	 						
	 	 	 						lane_toAdd.StoreObject4FlowRenderingInPool((Object) event , element_toAdd , pool_toAdd);

	 	 	 					    lane.getFlowNodeRef().add((JAXBElement<Object>)  factory.createTLaneFlowNodeRef((TFlowNode)  event ));
	 							}	
	 	 						bounds_element.setWidth(Event.Width); 
	 	 						bounds_element.setHeight(Event.Height);	 	
		 						 // draw the event
		 						 X_element = (X + Activity.offsetInitial) + (Activity.distance_between_activities * n_elements);
		 	 					 Y_element = Y + relative_lane_position + 
	 							 		     ( (lane_toAdd.getHeight() / lane_toAdd.getN_levels()) * (entry.getKey() - 1) )  +		 	 							 		
	 							 		     ( (lane_toAdd.getHeight() / lane_toAdd.getN_levels()) / 2 )
	 							 		     - (Event.Height / 2) ;
		 	 					 bounds_element.setX(X_element);
		 	 					 bounds_element.setY(Y_element);	
		 	 					 bpmnShape_element.setBpmnElement(element_toAdd.getQname_BPMNElement());		  	 					 
		 	 					 bpmnShape_element.setId(element_toAdd.getQname_Shape().toString());		 	 
	 	 	 					 bpmnShape_element.setBounds(bounds_element); 				
	 	 	 					 bpmnplane.getDiagramElement().add( factoryBPMN.createBPMNShape(bpmnShape_element)); 	

	 						 }
	 						 else if (element_toAdd instanceof Gateway)
	 						 {
	 							 Gateway gateway_toAdd = (Gateway) element_toAdd;
	 							 TGateway gtw = null;
	 							 if( gateway_toAdd.getType() == GatewayType.Exclusive.ordinal())
	 							 {
	 								 gtw = factory.createTExclusiveGateway();
	 								 element_ExclusiveGateway = factory.createExclusiveGateway((TExclusiveGateway) gtw);
									 process.getFlowElement().add(element_ExclusiveGateway);
	 							 }
	 							 else if( gateway_toAdd.getType() == GatewayType.Eventbased.ordinal())
	 							 {
	 								 gtw = factory.createTEventBasedGateway();	 								
	 								 element_EventBasedGateway = factory.createEventBasedGateway((TEventBasedGateway) gtw);
									 process.getFlowElement().add(element_EventBasedGateway); 								 
	 							 }
	 							 else if( gateway_toAdd.getType() == GatewayType.Parallel.ordinal())
	 							 {
	 								 gtw = factory.createTParallelGateway();
									 element_ParallelGateway = factory.createParallelGateway((TParallelGateway) gtw);
									 process.getFlowElement().add(element_ParallelGateway);
	 							 }
	 							 else if( gateway_toAdd.getType() == GatewayType.Inclusive.ordinal())
	 							 {
	 								 gtw = factory.createTInclusiveGateway();
									 element_InclusiveGateway = factory.createInclusiveGateway((TInclusiveGateway) gtw);
									 process.getFlowElement().add(element_InclusiveGateway);
	 							 }
	 							 
	  							 // if other type of Gateway add another if clause
	 							// else if (...) { ...}
		 							
	 							 if (gtw != null) // add to flow
	 							{
									 gtw.setId(gateway_toAdd.getQname_BPMNElement().toString());
									 gtw.setName(gateway_toAdd.getName());
									 
	 	 	 						// Specify incoming flows of gateway
	 	 	 						for (QName in_flow: element_toAdd.getQname_flow_Incoming())	gtw.getIncoming().add(in_flow);
	 	 	 						// Specify outgoing flows of gateway
	 	 	 						for (QName out_flow: element_toAdd.getQname_flow_Outgoing()) gtw.getOutgoing().add(out_flow);
	 	 	 						
	 	 	 						
	 	 	 						if ( 
	 	 	 							 gateway_toAdd.getType() == GatewayType.Exclusive.ordinal() && 
	 	 	 							 element_toAdd.getQname_flow_Outgoing().size() > 1	&& // and converging
	 	 	 							 camundaEngine	 	 	 								
	 	 	 							)
	 	 	 						{
	 	 	 							// store QName of gateway to add expression later
	 	 	 							GatewaysWithExpressions.add(gateway_toAdd.getQname_BPMNElement());	 	 	 							
	 	 	 						}

	 	 	 						lane_toAdd.StoreObject4FlowRenderingInPool((Object) gtw , element_toAdd , pool_toAdd);

									lane.getFlowNodeRef().add((JAXBElement<Object>)  factory.createTLaneFlowNodeRef((TFlowNode)  gtw ));
	 							}		
	 	 						bounds_element.setWidth(Gateway.Width); 
	 	 						bounds_element.setHeight(Gateway.Height);
	 	 						
		 						 // draw the gateway
		 						 X_element = (X + Activity.offsetInitial) + (Activity.distance_between_activities * n_elements);
		 	 					 Y_element = Y + relative_lane_position + 
	 							 		     ( (lane_toAdd.getHeight() / lane_toAdd.getN_levels()) * (entry.getKey() - 1) )  +		 	 							 		
	 							 		     ( (lane_toAdd.getHeight() / lane_toAdd.getN_levels()) / 2 )
	 							 		     - (Gateway.Height / 2) ;
		 	 					 bounds_element.setX(X_element);
		 	 					 bounds_element.setY(Y_element);	
		 	 					 bpmnShape_element.setBpmnElement(element_toAdd.getQname_BPMNElement());		  	 					 
		 	 					 bpmnShape_element.setId(element_toAdd.getQname_Shape().toString());		 	 
	 	 	 					 bpmnShape_element.setBounds(bounds_element); 				
	 	 	 					 bpmnplane.getDiagramElement().add( factoryBPMN.createBPMNShape(bpmnShape_element)); 	
	 						 }
	 						 
 
 	 	 					 
	 						 n_elements++;
	 						 element_toAdd.setX(X_element);
	 						 element_toAdd.setY(Y_element);
	 					 } 
 					 }
 					  					 
	 				 relative_lane_position += lane_toAdd.getHeight(); 
	 				 indexLane++;
 				 }
 				 
 				 int option  = 1;
 				 
 				 // Add BPMNFlowElement visually for each lane
 				 for (Lane lane_toAdd:pool_toAdd.getLanes())
 				 {
 					 lane_toAdd.UpdateBPMNSequenceFlowsPositions(pool_toAdd);
 					 
 					 for (BPMNSequenceFlow seq_toAdd:lane_toAdd.getBPMNSequenceFlows())
 					 {
	 	 				 TSequenceFlow seq = factory.createTSequenceFlow();
	 	 				 seq.setId(seq_toAdd.getQName_Flowid().toString()); 	 	 						
	 	 				 seq.setSourceRef(seq_toAdd.getSourceRef()); 
	 	 				 seq.setTargetRef(seq_toAdd.getTargetRef()); 
	 	 				 
	 	 				 if (camundaEngine)
	 	 				 {
	 	 					for (QName gtw2Check:GatewaysWithExpressions)
	 	 					{
	 	 						if ( gtw2Check == seq_toAdd.getSourceQName() )
	 	 						{
	 	 							TExpression expression = factory.createTExpression();
	 	 								
	 	 							//expression.setId("${option}");
	 	 							expression.getContent().add("${option" + option + "}");
	 	 							option++;
	 	 							
	 	 							
	 	 							
	 	 							seq.setConditionExpression(expression);
	 	 							
	 	 						}
	 	 					}	 	 					
	 	 				 };
	 	 				 
 	 				 
	 	 				 process.getFlowElement().add(factory.createSequenceFlow(seq));
	 	 				 	 	 				 
	 	 				 BPMNEdge edge = factoryBPMN.createBPMNEdge();
	 	 		 		 edge.setId("Edge_" +  UUID.randomUUID().toString());
	 	 			 	 edge.setBpmnElement(new QName(seq.getId()));
	 	 			 	 	 	 			 	 
	 	 			 	 edge = Routing( edge , seq_toAdd , factoryDD);
	 	 			 	 
	 	 			 	 /*
 	 	 			 	 Point point = factoryDD.createPoint();
						 point.setX(seq_toAdd.getPos_X_Start()); 
	 	 			 	 point.setY(seq_toAdd.getPos_Y_Start());		 
	 	 				 edge.getWaypoint().add(point);
	 	 				 Point point2 = factoryDD.createPoint();
	 	 			 	 point2.setX(seq_toAdd.getPos_X_End()); 
	 	 			 	 point2.setY(seq_toAdd.getPos_Y_End());		 
	 	 				 edge.getWaypoint().add(point2);
	 	 				 */
	 	 			 	 
	 	 				 bpmnplane.getDiagramElement().add( factoryBPMN.createBPMNEdge(edge));	 
 					 }
 				 }
 				 
 				 //  Add all association of compensation visually for each lane 
 				 for (Lane lane_toAdd:pool_toAdd.getLanes())
 				 { 					 
 					 for (BPMNAssociation assoc_toAdd:lane_toAdd.getBPMNAssociations())
 					 {
 						 TAssociation assoc = factory.createTAssociation();
 						 assoc.setId(assoc_toAdd.getQName_associationid().toString());
 						 assoc.setSourceRef(assoc_toAdd.getSourceQName());
 						 assoc.setTargetRef(assoc_toAdd.getTargetQName());
 						 assoc.setAssociationDirection(TAssociationDirection.fromValue("One"));		
	 	 				 process.getArtifact().add(factory.createAssociation(assoc));

	 	 				 BPMNEdge edge = factoryBPMN.createBPMNEdge();
	 	 		 		 edge.setId(assoc_toAdd.getQName_Shape().toString());
	 	 			 	 edge.setBpmnElement(assoc_toAdd.getQName_associationid());
	 	 			 	 
	 	 			 	 Point point = factoryDD.createPoint();
	 	 			 	 
	 	 			 	 
	 	 			 	 double x_coor1 = 0 , y_coor1 = 0, x_coor2 = 0, y_coor2 = 0;
	 	 			 	 
	 	 			 	 for (BPMNElement elem:lane_toAdd.getBPMNElements())
	 	 			 	 {
	 	 			 		if ( elem instanceof Activity)
	 	 			 		{
		 	 			 		if (elem.getQname_BPMNElement() == assoc_toAdd.getTargetQName())
		 	 			 		 {
		 	 			 			x_coor2 = elem.getX();
		 	 			 			y_coor2 = elem.getY();
		 	 			 		 }	 
		 	 			 		else if ( ((Activity) elem).getEvent_boundary() != null )
		 	 			 		{
		 	 			 		    if (((Activity) elem).getEvent_boundary().getQname_BPMNElement() == assoc_toAdd.getSourceQName())
		 	 			 		    {
		 	 			 		    	x_coor1 = ((Activity) elem).getEvent_boundary().getX();
		 	 			 		    	y_coor1 = ((Activity) elem).getEvent_boundary().getY();	 	 		
		 	 			 		    }	 	 	 			 			
		 	 			 		}
	 	 			 		}
	 	 			 	 }
	 	 			 	 
	 	 			 	 point.setX(x_coor1 + Event.Width/2); 
	 	 			 	 point.setY(y_coor1 + Event.Height);		 
	 	 				 edge.getWaypoint().add(point);
	 	 				 Point point2 = factoryDD.createPoint();
	 	 			 	 point2.setX(x_coor2); 
	 	 			 	 point2.setY(y_coor2 + Activity.Height / 2);		 
	 	 				 edge.getWaypoint().add(point2);
	 	 				 bpmnplane.getDiagramElement().add( factoryBPMN.createBPMNEdge(edge));	 
 					 }
 				 }

				 Y += 50 + pool_toAdd.getHeight();				 			 
				 element_process = factory.createProcess(process);
 				 definitions.getRootElement().add(element_process);
 				 
 			 	indexPool++;
 			 	
 

			 }			 

			 
			 // Draw all the BPMN Message flow
			 for (BPMNMessageFlow mf: MessageFlows)
			 {
				 TMessageFlow tmessage = factory.createTMessageFlow();
				 tmessage.setId(mf.getQName_messageflowid().toString());
				 if (mf.isDirection())
				 {
					 tmessage.setSourceRef(mf.getSourceQName());
					 tmessage.setTargetRef(mf.getTargetQName());
				 }
				 else
				 {
					 tmessage.setSourceRef(mf.getTargetQName());
					 tmessage.setTargetRef(mf.getSourceQName());
				 }	 
				 collaboration.getMessageFlow().add(tmessage);	
			 
				 BPMNEdge edge = factoryBPMN.createBPMNEdge();
		 		 edge.setId(mf.getQName_Shape().toString());
			 	 edge.setBpmnElement(mf.getQName_messageflowid());
			 	 
			 	 double x_coor1 = 0 , y_coor1 = 0, x_coor2 = 0, y_coor2 = 0;
			 	 
			 	 BPMNElement source = null, target = null;
			 	 for (Pool pool:Pools)
			 	 {
			 		 for (Lane lane:pool.getLanes())
			 		 {
			 			 for (BPMNElement elem:lane.getBPMNElements())
			 			 {
			 				 if (elem.getQname_BPMNElement() == mf.getSourceQName())
			 				 {
			 					 x_coor1 = elem.getX();
			 					 y_coor1 = elem.getY();
			 					 source = elem;
			 				 }
			 				 else if (elem.getQname_BPMNElement() == mf.getTargetQName())
			 				 {
			 					 x_coor2 = elem.getX();
			 					 y_coor2 = elem.getY();
			 					 target = elem;
			 				 }
			 			 }
			 		 }
			 	 }
			 	 			 	 
 			 	 edge = RoutingMessageFlow( edge , mf , source , target , factoryDD , x_coor1 , y_coor1 , x_coor2 , y_coor2);
 			 	 
				 bpmnplane.getDiagramElement().add( factoryBPMN.createBPMNEdge(edge));	 
					 
			}
			 
			 /* CREATE CONTEXT AND XML MARSHALLER */
 			JAXBElement<TDefinitions> element = factory.createDefinitions(definitions);
			JAXBContext context = JAXBContext.newInstance("org.omg.spec.bpmn._20100524.model");
			Marshaller marshaller = context.createMarshaller();
			marshaller.setProperty("jaxb.formatted.output",Boolean.TRUE);
			if (output_file_bpmn!= null)
			{
				File file = new File( output_file_bpmn );
				marshaller.marshal(element,file);
				//System.out.println("BPMN model SENT to: " + output_file_bpmn + " file!");
			}
			//marshaller.marshal(element,System.out);
			
			StringWriter sw = new StringWriter();
			marshaller.marshal(element, sw);
		    String resultTobeReturned = sw.toString();			
			return(resultTobeReturned);
			
		} catch (JAXBException e) 
		{ 
			e.printStackTrace();
			return(e.toString());

		}		 		 
	}

	
private static String ConvertString2Camunda(String name) 
{
	String nameconverted = new String();
	
	if (name != null) 
	{
		nameconverted = name.replaceAll(" ", "");
		name = nameconverted.replaceAll("-", "");
	}
	
	return name;
}

private static BPMNEdge RoutingMessageFlow(BPMNEdge edge, BPMNMessageFlow mf, BPMNElement source, BPMNElement target, org.omg.spec.dd._20100524.dc.ObjectFactory factoryDD, double Xi, double Yi, double Xf, double Yf) {

	
			/*System.out.print("RoutingMessageFlow: ");
			if (source != null) System.out.print( source.getName() + " -> " );
			if (target != null) System.out.print( target.getName() );
			System.out.println();*/
	
			if (source != null && target != null )
			{
				double  w_s = source.getWidth(), 
						h_s = source.getHeight(), 
						w_t = target.getWidth(),
						h_t = target.getHeight();
				double DeltaX = Xf - (Xi - w_s);
				double DeltaY = Yf - Yi;
				double AcceptedDelta = 1;
				double Offset = 60;
				double X1 = 0, Y1 = 0, X2 = 0, Y2 = 0; 
			
				Point point = factoryDD.createPoint();
				Point point2 = factoryDD.createPoint();
				Point point3 = factoryDD.createPoint();
				Point point4 = factoryDD.createPoint();
				
				if (mf.isDirection()) 
				{
					 Xi += w_s / 2;
					 Yi += h_s;
					 Xf += w_t / 2;
					 point.setX(Xi);
					 point.setY(Yi);
					 point2.setX(Xi);
					 point2.setY(Yi + 25);
					 point3.setX(Xf);
					 point3.setY(Yi + 25);
					 point4.setX(Xf);
					 point4.setY(Yf);
				}
				else
				{
					Xf += w_t/2;
					Xi += w_s/2;
					Yi += h_s;		
					point.setX(Xf);
					point.setY(Yf);
					point2.setX(Xf);
					point2.setY(Yf - 25);
					point3.setX(Xi);
					point3.setY(Yf - 25);
					point4.setX(Xi);
					point4.setY(Yi);
					
					
				}
	
				 edge.getWaypoint().add(point);
				 edge.getWaypoint().add(point2);
				 edge.getWaypoint().add(point3);
				 edge.getWaypoint().add(point4);
			}
			
			return(edge);
	}


	private static BPMNEdge Routing(BPMNEdge edge, BPMNSequenceFlow seq_toAdd , org.omg.spec.dd._20100524.dc.ObjectFactory factoryDD ) 
	{
		BPMNEdge edge_return = edge;
		
		double Xi = seq_toAdd.getPos_X_Start();
		double Yi = seq_toAdd.getPos_Y_Start();
		double Xf = seq_toAdd.getPos_X_End();
		double Yf = seq_toAdd.getPos_Y_End();
		double X1 = 0, Y1 = 0, X2 = 0, Y2 = 0; 
		double WidthElement_i = WidthElement(seq_toAdd.getSourceQName());
		double HeightElement_i = HeightElement(seq_toAdd.getSourceQName());
		double WidthElement_f = WidthElement(seq_toAdd.getTargetQName());
		double HeightElement_f = HeightElement(seq_toAdd.getTargetQName());	 
		double DeltaX = Xf - (Xi - WidthElement_i);
		double DeltaY = Yf - Yi;
		double AcceptedDelta = 1;
		double Offset = 60;
		
		
		/*System.out.println(			
				"<Routing_B> "
				+ "Xi = " + Xi +
				" Yi = " + Yi +
				" Xf = " + Xf +
				" Yf = " + Yf + 
				" WidthElement_i = " + WidthElement_i + 
				" HeightElement_i = " + HeightElement_i +
				" WidthElement_f = " + WidthElement_f +
				" HeightElement_f = " + HeightElement_f +	 
				" DeltaX = " + DeltaX+ 
				" DeltaY = " + DeltaY 
				+ "S = " + NameElement(seq_toAdd.getSourceQName()) 
				+ " T = " + NameElement(seq_toAdd.getTargetQName()) 
				);
		*/
		
		
		
		
	 	 
	 	 if ( DeltaX > 0 && Math.abs(DeltaY) <= AcceptedDelta) // Quadrant 1
	 	 {
	 		 Xi =  Xi; 	 Yi = Yi;
	 		 X1 =  Xi;	 Y1 = Yi;
	 		 X2 =  Xf; 	 Y2 = Yf;
	 		 Xf =  Xf ;  Yf = Yf ;
	 	//	 System.out.println("Routing: Quadrant 1");
	 	 }
	 	 else if ( DeltaX > 0 && DeltaY < AcceptedDelta) // Quadrant 2
	 	 {
	 		Xi = Xi - (WidthElement_i / 4);  Yi = Yi - (HeightElement_i / 2) +10 ; //other option
	 		//Xi = Xi - (WidthElement_i / 2);  Yi = Yi - (HeightElement_i / 2) ; 
	 		X1 = Xi ;  Y1 = Yi - (Offset-15);
	 		X2 = Xf + (WidthElement_f / 2) ;  Y2 = Y1 ;
	 		Xf = Xf + (WidthElement_f / 2) ;  Yf = Yf + (HeightElement_f / 2);
	 	//	 System.out.println("Routing: Quadrant 2");

	 	 }
	 	 else if ( Math.abs(DeltaX) <= AcceptedDelta && DeltaY < 0 ) // Quadrant 3
	 	 {
		 	Xi = Xi - (WidthElement_i / 2);  Yi = Yi - (HeightElement_i / 2);
	 		X1 = Xi;  Y1 = Yi;
	 		X2 = Xi;  Y2 = Yi;
	 		Xf = Xf + (WidthElement_f / 2) ;  Yf = Yf + (HeightElement_f / 2);
	 	//	 System.out.println("Routing: Quadrant 3");

	 	 }
	 	 else if ( DeltaX < AcceptedDelta && DeltaY < 0 ) // Quadrant 4
	 	 {
		    Xi = Xi - ((3 * WidthElement_i) / 4) ;  Yi = Yi - (HeightElement_i / 2) +10; //other option
	 		//Xi = Xi - ( WidthElement_i / 2) ;  Yi = Yi - (HeightElement_i / 2); 
	 		X1 = Xi ;  Y1 = Yi - (Offset+5);
	 		X2 = Xf + (WidthElement_f / 2) ;  Y2 = Y1;
	 		Xf = X2 ;  Yf = Yf + (HeightElement_f / 2);
	 	//	System.out.println("Routing: Quadrant 4");

	 	 }
	 /*	 else if ( DeltaY <= AcceptedDelta && DeltaX < 0 ) // Quadrant 5
	 	 {
		 	Xi = Xi - ( (3.5 * WidthElement_i) / 4);  Yi = Yi + (HeightElement_i / 2);
	 		X1 = Xi;  Y1 = Yi + Offset/3;
	 		X2 = Xf + (WidthElement_f / 2) ;  Y2 = Y1;
	 		Xf = X2;  Yf = Yf + (HeightElement_f / 2);
	 		 System.out.println("Routing: Quadrant 5");

	 	 }*/ // other option
	 	 else if ( DeltaY <= AcceptedDelta && DeltaX < 0 ) // Quadrant 5
	 	 {
	 		 Xi =  Xi - WidthElement_i;  Yi = Yi;
	 		 X1 =  Xi;	 Y1 = Yi;
	 		 X2 =  Xi; 	 Y2 = Yf;
	 		 Xf =  Xf + WidthElement_f;  Yf = Yf;
	 	//	 System.out.println("Routing: Quadrant 5");

	 	 }
	 	 else if ( DeltaX < 0 &&  Math.abs(DeltaX) > AcceptedDelta && DeltaY > 0 ) // Quadrant 6
	 	 {
		 	Xi = Xi - ( (3 * WidthElement_i) / 4);  Yi = Yi + (HeightElement_i / 2) -10; //other option
	 	//	Xi = Xi - ( WidthElement_i / 2);  Yi = Yi + (HeightElement_i / 2);
	 		X1 = Xi;  Y1 = Yi + (Offset+10);
	 		X2 = Xf + (WidthElement_f / 2) ;  Y2 = Y1;
	 		Xf = X2 ;  Yf = Yf - (HeightElement_f / 2);
	 	//	 System.out.println("Routing: Quadrant 6");

	 	 }
	 	 else if ( Math.abs(DeltaX) <= AcceptedDelta && DeltaY > 0 ) // Quadrant 7
	 	 {
		 	Xi = Xi - (WidthElement_i / 2) ;  Yi = Yi + (HeightElement_i / 2);
	 		X1 = Xi;  Y1 = Yi;
	 		X2 = Xi;  Y2 = Yi;
	 		Xf = Xf + (WidthElement_f / 2) ;  Yf = Yf - (HeightElement_f / 2);
	 	//	 System.out.println("Routing: Quadrant 7");

	 	 }
	 	 else if ( DeltaX > 0 && DeltaY > AcceptedDelta ) // Quadrant 8
	 	 {
		 	Xi = Xi - (WidthElement_i / 4);  Yi = Yi + (HeightElement_i / 2) -10; //other option
	 	//	Xi = Xi - (WidthElement_i / 2);  Yi = Yi + (HeightElement_i / 2);
		 	X1 = Xi ;  Y1 = Yi + (Offset+15);
	 		X2 = Xf + (WidthElement_f / 2);  Y2 = Y1;
	 		Xf = X2 ;  Yf = Yf - (HeightElement_f / 2);
	 	//	 System.out.println("Routing: Quadrant 8");

	 	 }
	 	 
	 	 Point point_i = factoryDD.createPoint();
	     point_i.setX(Xi); point_i.setY(Yi);		 
	 	 edge_return.getWaypoint().add(point_i);
	 	 Point point_1 = factoryDD.createPoint();
	 	 point_1.setX(X1); point_1.setY(Y1);		 
	 	 edge_return.getWaypoint().add(point_1);
	 	 Point point_2 = factoryDD.createPoint();
	 	 point_2.setX(X2); point_2.setY(Y2);		 
	 	 edge_return.getWaypoint().add(point_2);
		 Point point_f = factoryDD.createPoint();
	 	 point_f.setX(Xf); point_f.setY(Yf);		 
	 	 edge_return.getWaypoint().add(point_f);
	
		/*	System.out.println(			
					"<Routing_A> Xi = " + Xi +
					" Yi = " + Yi +
					" Xf = " + Xf +
					" Yf = " + Yf + 
					" WidthElement_i = " + WidthElement_i + 
					" HeightElement_i = " + HeightElement_i +
					" WidthElement_f = " + WidthElement_f +
					" HeightElement_f = " + HeightElement_f +	 
					" DeltaX = " + DeltaX+ 
					" DeltaY = " + DeltaY
					);
	 	 */
		return(edge_return);
	}
	
	private static String  NameElement(QName QName_T) {		
		for (Pool pool:Pools)
			for (Lane lane:pool.getLanes())
				for (BPMNElement elem:lane.getBPMNElements())
					if (elem.getQname_BPMNElement() == QName_T) return(elem.getName());
		return("");
	}

	private static double HeightElement(QName QName_T) {
		double height = 0;
		
		for (Pool pool:Pools)
			for (Lane lane:pool.getLanes())
				for (BPMNElement elem:lane.getBPMNElements())
					if (elem.getQname_BPMNElement() == QName_T) return(elem.getHeight());
		return(height);
	}

	private static double WidthElement(QName QName_T) {
		double width = 0;
		
		for (Pool pool:Pools)
			for (Lane lane:pool.getLanes())
				for (BPMNElement elem:lane.getBPMNElements())
					if (elem.getQname_BPMNElement() == QName_T) return(elem.getWidth());
		return(width);	
	}

	private static void ProduceBPMN2TXT(ArrayList<Pool> poolsReceived)
	{
		
		String time_to_write = new String (timeStampForFileName());
		// output file
		PrintWriter escrever;
		try {
			escrever = new PrintWriter(new File("ProducedBPMN-TXT-" + time_to_write + ".txt"));
			
			for (Pool pool2print:poolsReceived)
			{
				escrever.println("Pool Name=" + pool2print.getShortName().trim());
				for (Lane lane2print:pool2print.getLanes())
				{
					escrever.println("Lane Name=" + lane2print.getName().trim());
					for (BPMNElement elem:lane2print.getBPMNElements())
						escrever.println("Element type=" + elem.getClass().toString() + ", Name = " + elem.getName().trim() + " - (" + elem.getDescription().trim() + ")" + " - type=(" + elem.getTypeS() + ")");
				}
				escrever.println ("--------*--------");
			}
			escrever.close();
		} 
		catch (FileNotFoundException e) { e.printStackTrace(); }
	}

	
	
	

	private static void CheckArguments()
	{
		/*System.out.println("--actors=" + actors_filename + "\n" +
						   "--tpt=" + tpt_filename + "\n" +
						   "--tkdepend=" + tkdepend_filename + "\n" +
						   "--tkview=" + tkview_filename + "\n" +
						   "--output-file-txt=" + output_file_txt + "\n" +
						   "--output-file-bpmn=" +  output_file_bpmn + "\n" +
						   "--simplify=" + simplify +
						   "--engine camunda=" + camundaEngine +
						   "--businessObjects" + businessObjects_filename);*/
	}
	
	private static boolean VerifyArgs(String[] cabecalho)
	{
		boolean result = true;
		boolean mandatoryactors = false;
		boolean mandatorytpt = false;
		boolean mandatoryptkdepend = false;
		
		for (int i=0 ; i < cabecalho.length ; i=i+2)
		{
			
			if (cabecalho[i].compareTo("--actors") == 0)
			{
				actors_filename = cabecalho[i+1].trim();
				mandatoryactors = true;
			}
			else if (cabecalho[i].compareTo("--tpt") == 0) 
			{
				tpt_filename = cabecalho[i+1].trim();
				mandatorytpt = true;
			}
			else if (cabecalho[i].compareTo("--tkdepend") == 0) 
			{
				tkdepend_filename = cabecalho[i+1].trim();
				mandatoryptkdepend = true;
			}
			else if (cabecalho[i].compareTo("--tkview") ==0) tkview_filename = cabecalho[i+1].trim();
			else if (cabecalho[i].compareTo("--output-file-txt") == 0) output_file_txt = cabecalho[i+1].trim();
			else if (cabecalho[i].compareTo("--output-file-bpmn") == 0) output_file_bpmn = cabecalho[i+1].trim();
			else if (cabecalho[i].compareTo("--businessObjects") == 0) businessObjects_filename = cabecalho[i+1].trim();
			else if (cabecalho[i].compareTo("--simplify") == 0) 
			{
				if (cabecalho[i+1].trim().compareTo("on") == 0) simplify = true;
			}
			else if (cabecalho[i].compareTo("--engine") == 0) 
			{
				if (cabecalho[i+1].trim().compareTo("camunda") == 0) camundaEngine = true;
			}			
			
			else 
			{
				System.out.println("Bad argument name: " + cabecalho[i].trim());
				return(false);
			}
		}
		
		
		if (mandatoryactors && mandatorytpt && mandatoryptkdepend)	return(result);
		else if (mandatoryactors == false) System.out.println ("Actor roles argument is mandatory!");
		else if (mandatorytpt == false) System.out.println ("TPT argument is mandatory!");		
		else System.out.println ("Transaction dependencies argument is mandatory!");
			
		return (false);
	}		
	
	private static String LoadBPMNXMLWithDiagramData( String toAdd , String filenameInput , String filenameOutput) 
	{	
		//System.out.println("Writing BPMN XML inside Diagram Data Starting...");
		String ProcessName_return = null;
		try
		{
			if ( toAdd != null ) 
			{
				//System.out.println("This is the BPMN XML produced: " + toAdd);
				// Read DiagramDataXML from INPUT_FOLDER > listagem.get(3)
	            File file = new File( filenameInput ); 
	            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance(); 
	            DocumentBuilder db = dbf.newDocumentBuilder(); 
	            Document doc = db.parse(file); 
	            doc.getDocumentElement().normalize();  
	
				// Change Contents of BPMN_Data	
	            NodeList nodeList = doc.getElementsByTagName("PropsAndValues");	            
	            Node node = nodeList.item(1); 
	            if (node.getNodeType() == Node.ELEMENT_NODE) 
	            { 
	                Element tElement = (Element)node;
	                
	                JSONObject internal = new JSONObject();
	                internal.put("xml" , toAdd);
	                internal.put("objects" , new JSONArray());
	                internal.put("relatedObjects" , new JSONObject());
	                
	                JSONObject newBPMN_Data = new JSONObject();	                
	                newBPMN_Data.put( DayForJSON() , internal);
	                 	                
	                tElement.getElementsByTagName("BPMN_Data").item(0).setTextContent( newBPMN_Data.toString() );
	                tElement.getElementsByTagName("BPMN_Is_Old_Structure").item(0).setTextContent("false");
	                ProcessName_return = tElement.getElementsByTagName("Name").item(0).getTextContent();
	                
					// Write new DiagramDataXML in OUTPUT_FOLDER
	                TransformerFactory transformerFactory = TransformerFactory.newInstance();
	                Transformer transformer = transformerFactory.newTransformer();
	                DOMSource source = new DOMSource(doc);
	                StreamResult result = new StreamResult(new File(filenameOutput));
	                transformer.transform(source, result);
	            } 
	    		//System.out.println("Writing BPMN XML inside Diagram Data Finished.");
			}
			else System.out.println("XML not produced correctly.");
			
			return(ProcessName_return);
		}
    	catch (ParserConfigurationException pce) { pce.printStackTrace(); } 
		catch (TransformerException tfe) 		 { tfe.printStackTrace(); } 
		catch (IOException ioe) 				 { ioe.printStackTrace(); } 
		catch (SAXException sae) 				 { sae.printStackTrace(); }
		
		return(ProcessName_return);
	}
	
	
	private static void Write2File(String resultBPMN, String filename_path) 
	{
		//System.out.println("Writing plain BPMN XML to output file Starting...");
		File BPMNfile = new File(filename_path);
    	FileWriter writer;

		try 
		{	writer = new FileWriter(BPMNfile);
			writer.write( resultBPMN );
			writer.close();
			//System.out.println("Writing plain BPMN XML to output file Finished.");
		} catch (IOException e) 
		{
			//System.out.println("Error Writing .BPMN file to local storage" + e.toString());
		}
	}
	
    private static Node NewElement(Document doc, String name, String value) {
        Element node = doc.createElement(name);
        node.appendChild(doc.createTextNode(value));
        return node;
    }
	
	private static void LoadTransactionsWithTransactionData( String ProcessName , String TK_XML_Out, String TK_Act_XML_Out)
	{
		//System.out.println("Writing XML inside Transaction and Transaction_Act Starting...");
		
		 try {
				if (ProcessName != null)
				{
				    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			        DocumentBuilder db = dbf.newDocumentBuilder();
			        Document document = db.newDocument();	        
		            Element rootElement =  document.createElementNS("", "DocumentElement");
		            document.appendChild(rootElement);
		            Element PropsAndValues = document.createElement("PropsAndValues");
		    		
		            //create elements
		            PropsAndValues.appendChild(NewElement(document , "Name", "# CONFIGURATION_NODE # DO NOT DELETE THIS ROW #"));
		            PropsAndValues.appendChild(NewElement(document , "Allocated_Transaction_Acts", "#"));
		            PropsAndValues.appendChild(NewElement(document , "Business_x0020_Process","#"));
		            PropsAndValues.appendChild(NewElement(document , "Description", "#"));
		            PropsAndValues.appendChild(NewElement(document , "InvokedTransactions", "#"));
		            PropsAndValues.appendChild(NewElement(document , "Product_x0020_Kind", "#"));
		            PropsAndValues.appendChild(NewElement(document , "Transaction_x0020_Executor", "#"));
		            PropsAndValues.appendChild(NewElement(document , "Transaction_x0020_Initiator", "#"));            
		            rootElement.appendChild(PropsAndValues);

					for (TransactionKind tk:TPT)
					{					
						Element PropsAndValues2 = document.createElement("PropsAndValues");
						
						String TKName_to_appear = new String();
						
						// TKName (A1)->(A2)
						TKName_to_appear = tk.getName() + ": From (" + tk.getInitiatorRole().getName() + ")" +
														  " to (" + tk.getExecutorRole().getName() + ")"; 
						
						//System.out.println("Name: " + ProcessName + "»" + TKName_to_appear);
			            PropsAndValues2.appendChild(NewElement(document , "Name", new String(ProcessName + "»" + TKName_to_appear )  ));
			            PropsAndValues2.appendChild(NewElement(document , "Business_x0020_Process" , ProcessName));

			            

						
						System.out.print("InvokedTransactions: ");
						for (int idxcl = 1 ; idxcl <= TPT.size()  ; idxcl++)
						{
							if ( listTKdepend[0][idxcl].compareTo(tk.getName()) == 0)
							{
								String InvokedTransactions = new String("");
								
								for (int idxrw = 1 ; idxrw <= TPT.size()  ; idxrw++)
								{
									if ( listTKdepend[idxrw][idxcl] != null )
									{									
										if (InvokedTransactions.compareTo("") == 0) InvokedTransactions =  ProcessName + "»" + listTKdepend[idxrw][0];
										//else InvokedTransactions +=  System.lineSeparator() + ProcessName + "»" + listTKdepend[idxrw][0];
										else InvokedTransactions +=  "\n" + ProcessName + "»" + listTKdepend[idxrw][0];
									}
								}
								
					            PropsAndValues2.appendChild(NewElement(document , "InvokedTransactions", InvokedTransactions));
							}
						}
						//System.out.println();
						
						//System.out.println("ActorExecutor: " + tk.getExecutorRole().getName());
						//.out.println("ActorInitiator: " + tk.getInitiatorRole().getName());
						PropsAndValues2.appendChild(NewElement(document , "Transaction_x0020_Executor", tk.getExecutorRole().getName()));
			            PropsAndValues2.appendChild(NewElement(document , "Transaction_x0020_Initiator", tk.getInitiatorRole().getName()));
						
						
						//System.out.print("Allocated_Transaction_Acts:");
						
						for ( PatternView  tkview:TKPatternViews)
						{
							if (tkview.getName().compareTo(tk.getName()) == 0)
							{
								Set<String> keys = tkview.getCustomViewDetail().keySet();
								Iterator<String> it = keys.iterator();
								
								String allocatedTransactionActs = new String("");
								
								for (int idx = 0 ; idx < keys.size() ;idx++) 
								{
									String key = (String) it.next();
									String tkview_stepname = tkview.getCustomViewDetail().get(key);								
									if ( tkview_stepname.compareTo("") != 0 ) 
									{ 
										if (allocatedTransactionActs.compareTo("") == 0 ) allocatedTransactionActs = tkview_stepname;
									//	else allocatedTransactionActs += System.lineSeparator() + tkview_stepname;							           
										else allocatedTransactionActs += "\n" + tkview_stepname;
									}
								}		
					            PropsAndValues2.appendChild(NewElement(document , "Allocated_Transaction_Acts", allocatedTransactionActs));
								
							}
						}
						
						//System.out.println("");
						
			            //create elements
			            PropsAndValues2.appendChild(NewElement(document , "Description", ""));
			            PropsAndValues2.appendChild(NewElement(document , "Product_x0020_Kind", ""));
			                        
			            rootElement.appendChild(PropsAndValues2);
					}
					
		            TransformerFactory transformerFactory = TransformerFactory.newInstance();
		            Transformer transformer = transformerFactory.newTransformer();
		            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		            transformer.setOutputProperty(OutputKeys.METHOD, "xml");
		            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
		            DOMSource source = new DOMSource(document);

		            StreamResult file = new StreamResult(new File(TK_XML_Out));
		            transformer.transform(source, file);
		            
				}
				else System.out.println("Process Name not defined in Diagram Data.xml, therefore not possible to generate Transaction.xml and TransactionAct.xml");
		    } 
		    catch (Exception exc) 
		    {
	            exc.printStackTrace();
	        }		
			
		 
		 
		 
			
			
			
			//System.out.println("Now for the Transaction_act.....");
			
			try
			{
				    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			        DocumentBuilder db = dbf.newDocumentBuilder();
			        Document document = db.newDocument();	        
		            Element rootElement =  document.createElementNS("", "DocumentElement");
		            document.appendChild(rootElement);
		            Element PropsAndValues = document.createElement("PropsAndValues");
		    		
		            //create elements
		            PropsAndValues.appendChild(NewElement(document , "Name", "# CONFIGURATION_NODE # DO NOT DELETE THIS ROW #"));
		            PropsAndValues.appendChild(NewElement(document , "Classification_Transaction_Act", "#"));
		            PropsAndValues.appendChild(NewElement(document , "Equilavent_Task_Name", "#"));
		            rootElement.appendChild(PropsAndValues);
			
					for ( PatternView  tkview:TKPatternViews)
					{

						Set<String> keys = tkview.getCustomViewDetail().keySet();
						Iterator<String> it = keys.iterator();
						
						for (int idx = 0 ; idx < keys.size() ;idx++) 
						{
							String key = (String) it.next();
							String tkview_stepname = tkview.getCustomViewDetail().get(key);
							
							if ( tkview_stepname.compareTo("") != 0 )
							{
								Element PropsAndValues2 = document.createElement("PropsAndValues");
								//System.out.println("Name : " + tkview_stepname);
								//System.out.println("Classification_Transaction_Act : " + tkview.Translate2Atlas(key));
								//System.out.println("Equilavent_Task_Name: " + tkview_stepname);
								PropsAndValues2.appendChild(NewElement(document , "Name", tkview_stepname));
					            PropsAndValues2.appendChild(NewElement(document , "Classification_Transaction_Act", tkview.Translate2Atlas(key)));								
					            PropsAndValues2.appendChild(NewElement(document , "Equilavent_Task_Name",tkview_stepname));
					            rootElement.appendChild(PropsAndValues2);
							}							
						}					                        
					}
					
		            TransformerFactory transformerFactory = TransformerFactory.newInstance();
		            Transformer transformer = transformerFactory.newTransformer();
		            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		            transformer.setOutputProperty(OutputKeys.METHOD, "xml");
		            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
		            DOMSource source = new DOMSource(document);

		            StreamResult file = new StreamResult(new File(TK_Act_XML_Out));
		            transformer.transform(source, file);

			}
			catch (Exception exc) 
		    {
	            exc.printStackTrace();
	        }	
			
			
		
		//System.out.println("Writing XML inside Transaction and Transaction_Act Finished.");		
	}


	private static ArrayList<PatternView> CreateEnhancedTKView(String TK_Act_XML_In, String TK_XML_In , String view_target) 
	{
		//System.out.println("Create Enhanced TKView Starting....");
		HashMap<String,String> tx_acts = new HashMap<String,String>();
		ArrayList<PatternView> pattern_to_return = new ArrayList<PatternView>(); 
		
		// Read TK_Act_XML_In and store in tx_acts
		try
		{
	        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance(); 
	        DocumentBuilder db = dbf.newDocumentBuilder();
	        FileInputStream in = new FileInputStream(new File(TK_Act_XML_In));	        
	        Document doc = db.parse(in, "utf-8");	        
	        doc.getDocumentElement().normalize();
	        
	        NodeList nodeList = doc.getElementsByTagName("PropsAndValues");
	        
	        PatternView tk_tmpPatternView = new PatternView();
	        
	        for (int i = 1; i < nodeList.getLength(); ++i) 
	        { 
	            Node node = nodeList.item(i);
		        if (node.getNodeType() == Node.ELEMENT_NODE) 
		        { 
		            Element tElement = (Element)node;
		            String Name = tElement.getElementsByTagName("Name").item(0).getTextContent();
		            
		            String classification = tElement.getElementsByTagName("Classification_Transaction_Act").item(0).getTextContent();
		            //System.out.println("Read from XML, Name = " + Name + ", Classification = " + classification);
		            classification = tk_tmpPatternView.Translate2SemantifyingBPMN(classification);
		            tx_acts.put(Name, classification);
		        } 
	        }	     
		}
		catch (Exception exc)	{  System.out.println(exc.toString()); 	}
        
        //System.out.println("TX_ACTS = " + tx_acts);
        
		// Read TK_XML_In and store in pattern_to_return
		try
		{
 
	        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance(); 
	        DocumentBuilder db = dbf.newDocumentBuilder();
	        
	      
	        FileInputStream in = new FileInputStream(new File(TK_XML_In));
	        //InputStream in = new ByteArrayInputStream(hardcoded_TK_XML_IN.getBytes());
	        Document doc = db.parse(in, "utf-8");
	        
	        
	        //Document doc = db.parse(file); 
	        doc.getDocumentElement().normalize();  
	        NodeList nodeList = doc.getElementsByTagName("PropsAndValues");
	        
	        PatternView tk_tmpPatternView = new PatternView();
	        
	        for (int i = 1; i < nodeList.getLength(); ++i) 
	        { 
	            Node node = nodeList.item(i); 
		        if (node.getNodeType() == Node.ELEMENT_NODE) 
		        { 
		            Element tElement = (Element)node;
		            String Name = tElement.getElementsByTagName("Name").item(0).getTextContent();
		           // System.out.println("Name = " + Name);
		            //Extract TK
					String[] tokens = Name.split("»");	
		           // String[] tokens = Name.split("_");
		            //System.out.println("Tokens = " + tokens);
		            String tk_Name_full = tokens[1].trim();
		            
		            //System.out.println("tk_name_full before extracting = " + tk_Name_full);
		            
		            // To extract the single name of the transaction without the details of the actors
					String[] tokenstk = tk_Name_full.split(":");	
					String tk_Name = tokenstk[0].trim();
		            
					//System.out.println("tk_name after extracting = " + tk_Name);
		            
		        	PatternView new_Patterview = new PatternView(tk_Name , view_target);
		        	
		        	//System.out.println("PatternView =" + new_Patterview.toString());
		        	
		            String Allocated_Transaction_Acts = tElement.getElementsByTagName("Allocated_Transaction_Acts").item(0).getTextContent();
		           // System.out.println("Allocated_Transaction_Acts = " + Allocated_Transaction_Acts);
		            
					String[] tokens2 = Allocated_Transaction_Acts.split("\n");
					//System.out.println("Tokens2 = " + tokens2);
					for (int indice = 0 ; indice < tokens2.length ; indice++)
					{
						//System.out.println("For TK = " + tk_Name + ", with view = " + view_target + ", allocated_transaction_act = " + tokens2[indice]);
						new_Patterview.addTKStep( tx_acts.get(tokens2[indice]), tokens2[indice]);
					}
										
		            pattern_to_return.add(new_Patterview);
		        } 
	        }
		}
		catch (Exception exc)	{  System.out.println(exc.toString()); 	}

       // System.out.println("List of new pattern view created = " + pattern_to_return);
		
		//System.out.println("Create Enhanced TKView Finished.");
		return pattern_to_return;
	}


	private static ArrayList<PatternView> AddTKPatternViews( ArrayList<PatternView> tKPatternViews_Enhanced) 
	{
		
		HashMap <String,String> list_views = (new PatternView()).getMapTKviewClassificationATLAS();
		
		for (PatternView tkview_TK:	tKPatternViews_Enhanced)
		{
		
			Set<String> keys = list_views.keySet();
			Iterator<String> it = keys.iterator();
					
			for (int idx = 0 ; idx < keys.size() ;idx++) 
			{	
				String key = (String) it.next();				
				if (tkview_TK.getCustomViewDetail().containsKey(key) == false)	tkview_TK.getCustomViewDetail().put(key, "");
			}		
		}		
		return tKPatternViews_Enhanced;
	}

	
	public static void main(String[] args) {

			
			String usage = "The usage of SemantifyingBPMN is the following.\n" +  
					"SemantifyingBPMN-4.2.0 --actors <filename> --tpt <filename> --tkdepend <filename> --output-file-txt <filename> --output-file-bpmn <filename>\n"
					+ "Credits: Sérgio Guerreiro (2025) (github: https://github.com/SemantifyingBPMN/SemantifyingBPMN)\n"
					+ "\n"	
					+"where the parameters are,\n"
					+"--actors: is a csv file with the list of actor roles and is mandatory. Composed of 2 fields, in each line, with actor role name and description:\n"
					+" (e.g.: A01 - Customer ; The role that initiates the business process).\n"
					+"--tpt: is a csv file with the Transactor Product Table and is mandatory. Composed of 6 fields, in each line, with TK name, TK description, Actor role initiator, Actor role executor, Product kind , Product kind description:\n"
					+" (e.g.: TK01; Sale completing ; A01 - Customer  ; A02 - Dispatcher ; PK01 ; [Product] is sold).\n"
					+"--tkdepend: is a csv file with the dependencies matrix N*N transactions and is mandatory. Composed of Strings with dependencies: RaP = Request after Promise pattern, RaE = Request after Execution, RaD = Request after Declare pattern.\n"
					+" (e.g.:\n" 
					+"             ; TK01  ; TK02  ; TK03 ; TK04\n"
					+"        TK01 ;       ;       ;      ;\n"
					+"        TK02 ; RaP   ;       ;      ;\n"
					+"        TK03 ;       ; RaE   ;      ;\n"
					+"        TK04 ;       ;       ; RaE  ;\n"  
					+" )\n"
					+"--tkview: is a mandatory csv file with view definition for each transaction per line, acceptable values are: HappyFlow | HappyFlowAndDeclinationsAndRejections | Complete | Custom | CustomHappyFlowOnly. Default value is HappyFlow.\n"
					+"          The Custom value accepts extra detail for each transaction step, even empty ones.\n"
					+" (e.g.\n"
					+"      TransactionKind  ; View   ; Request Decision ; Request ; Promise Decision ; Promise ; Decline ; After Decline Decision ; Execute ; Declare        ; Decision Accept ; Accept ; Reject ; Evaluate Rejection ; Stop\n"					
					+"                  TK01 ; HappyFlow\n"
					+"                  TK02 ; HappyFlowAndDeclinationsAndRejections\n"
					+"                  TK03 ; Custom ;                  ; Pedido  ;                  ;         ; Executa ; how to decide          ; Produce ; Here it is     ; Valida resultado;        ; not ok ; decide reject      ; ok  \n"
					+"                  TK04 ; Complete \n"
					+"                  TK05 ;\n"
					+" )\n"
					+"--businessObjects: is a csv file with the list of business object for each transaxction step.\n"
					+" (e.g.\n"
					+"  TransactionKind ; TransactionStep ; BusinessObject\n"
					+"  TK01 			; Request  		  ; Order\n	"
					+"  TK02			; Promise		  ; Receipt\n" 
					+")\n"
					+"--simplify on|off: create a BPMN model without communications for simplification purposes only. Optional, default is off.\n"
					+"--engine camunda|... : create a BPMN model ready for CAMUNDA execution. Optional.\n" 
					+"--output-file-txt: is a file to store the model in txt format. Optional.\n"
					+"--output-file-bpmn: is a file to store the BPMN model. Optional.\n";
					
			
		
			
			
			if (args.length == 0) System.out.println(usage);
			else 
			{
				if (VerifyArgs(args))
				{		
					//System.out.println ("The following arguments are accepted:");
					CheckArguments();
					//System.out.println ("------- Processing starting -------");
		
					Init();
					
					ReadDataFromFiles();
		
					if (!simplify)
					{
						Pools = SpecifyPools(Actors);
						Pools = SpecifyLanesAndOrganize(TPT , Pools);
					}
					else
					{
						Pools = SpecifyPoolsSimplified();
						Pools = SpecifyLanesAndOrganizeSimplified(TPT , Pools);					
					}
					
				
					if (output_file_bpmn != null) ProduceBPMN2XML(Pools);
					if (output_file_txt != null)  ProduceBPMN2TXT(Pools);
					
				
					//System.out.println ("------- Processing stopping -------");
				}
				else System.out.println("Application Arguments bad usage.\n\nPlease check syntax.\n\n" + usage);
			}

		
		
		

	}



}












