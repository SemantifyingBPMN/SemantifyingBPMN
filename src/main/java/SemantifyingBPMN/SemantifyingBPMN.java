package SemantifyingBPMN;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;

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
import org.omg.spec.bpmn._20100524.model.TFlowElement;
import org.omg.spec.bpmn._20100524.model.TFlowNode;
import org.omg.spec.bpmn._20100524.model.TGateway;
import org.omg.spec.bpmn._20100524.model.TIntermediateCatchEvent;
import org.omg.spec.bpmn._20100524.model.TIntermediateThrowEvent;
import org.omg.spec.bpmn._20100524.model.TLane;
import org.omg.spec.bpmn._20100524.model.TLaneSet;
import org.omg.spec.bpmn._20100524.model.TManualTask;
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
import org.omg.spec.dd._20100524.dc.Bounds;
import org.omg.spec.dd._20100524.dc.Point;
import org.omg.spec.dd._20100524.di.DiagramElement;
import org.omg.spec.bpmn._20100524.di.BPMNEdge;

import java.math.BigDecimal;

public class SemantifyingBPMN {

	
	// Execution Configuration variable
	static String actors_filename = null;
	static String tpt_filename = null;
	static String tkdepend_filename = null;
	static String tkview_filename = null;
	static String output_file_txt = null;
	static String output_file_bpmn = null;
	

	
	// Actors List
	private static ArrayList<ActorRole> Actors = new ArrayList<ActorRole>();
	
	// Transactor Product Table
	// TBD: how to store same TK initiator by different actor roles?
	private static ArrayList<TransactionKind> TPT = new ArrayList<TransactionKind>();
	
	// TK Dependencies
	private static HashMap<String,ArrayList<String>> TKDependencies = new HashMap<String,ArrayList<String>>();
	private static HashMap<String,ArrayList<String>> TKDependenciesT = new HashMap<String,ArrayList<String>>();
	private static ArrayList<String> keys = new ArrayList<String>();
	
	// Transaction pattern view
	private static ArrayList<PatternView> TKPatternViews = new ArrayList<PatternView>();
	
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

	private static void Init()
	{
		
		
	}
	

	
	private static void ReadDataFromFiles()
	{	
		
		try
		{	
			Scanner ficheiro = new Scanner(new File(actors_filename));
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
			System.out.println ("Reading input file ended. " + count_line + " lines read. With " + Actors.size() + " actor roles.");
			ficheiro.close();
		}
		catch (Exception e) 	{ 	e.printStackTrace(); 	}
		System.out.println(	"-------------------*-----------------------------" +
				" Actors \n" + Actors.toString() +
				"\n-------------------*-----------------------------");
		
		
		
		

		try
		{	
			Scanner ficheiro = new Scanner(new File(tpt_filename));
			Scanner ficheiroView = null;
			if (tkview_filename != null) ficheiroView = new Scanner(new File(tkview_filename));
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
			System.out.println ("Reading input file ended. " + count_line + " lines read. With " + TPT.size() + " transaction kinds.");
			ficheiro.close();
			ficheiroView.close();
		}
		catch (Exception e) 	{ 	e.printStackTrace(); 	}
		System.out.println(	"-------------------*-----------------------------" +
				" TPT \n" + TPT.toString() +
				"\n-------------------*-----------------------------");
	
		System.out.println(	"-------------------*-----------------------------" +
				" TKVIEW \n" + TKPatternViews.toString() +
				"\n-------------------*-----------------------------");
		
		
		
		try
		{	
			Scanner ficheiro = new Scanner(new File(tkdepend_filename));
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
				
				System.out.println("ReadDataFromFiles: keys=" + keys +
						" TKDependencies=" + TKDependencies.toString() +
						" TKDependenciesT=" + TKDependenciesT.toString());

			}
			System.out.println ("Reading input file ended. " + count_line + " lines read. With " + TKDependencies.size() + " Dependencies.");
			ficheiro.close();
		}
		catch (Exception e) 	{ 	e.printStackTrace(); 	}
		System.out.println(	"-------------------*-----------------------------" +
				" TKDependencies \n" + TKDependencies.toString() +
				"\n-------------------*-----------------------------");

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

							switch (TKpatternName.getPattern())
							{
							case "HappyFlow":
								 	newLane = (new DEMOPatternHappyFlowInitiator()).CreateElements_and_Sequence(newLane , tk2StoreLane , MessageFlows , TKDependencies.get(tk2StoreLane.getName()) , TKpatternName );
									break;
							case "HappyFlowAndDeclinationsAndRejections":
									newLane = (new DEMOPatternHappyFlowAndDeclinationsAndRejectionsInitiator()).CreateElements_and_Sequence(newLane , tk2StoreLane , MessageFlows , TKDependencies.get(tk2StoreLane.getName()) , TKpatternName );
									break;
							case "Custom": 
									newLane = (new DEMOPatternCustomInitiator()).CreateElements_and_Sequence(newLane , tk2StoreLane , MessageFlows , TKDependencies.get(tk2StoreLane.getName()) , TKpatternName );
									break;
							case "CustomHappyFlowOnly":
									newLane = (new DEMOPatternCustomInitiatorHappyFlowOnly()).CreateElements_and_Sequence(newLane , tk2StoreLane , MessageFlows , TKDependencies.get(tk2StoreLane.getName()) , TKpatternName );
									break;								
							case "Complete":		
									newLane = (new DEMOPatternInitiator()).CreateElements_and_Sequence(newLane , tk2StoreLane , MessageFlows , TKDependencies.get(tk2StoreLane.getName()) , TKpatternName );
									break;
							default:
							 		newLane = (new DEMOPatternHappyFlowInitiator()).CreateElements_and_Sequence(newLane , tk2StoreLane , MessageFlows , TKDependencies.get(tk2StoreLane.getName()) , TKpatternName );
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
								 	newLane = (new DEMOPatternHappyFlowExecutor()).CreateElements_and_Sequence(newLane , tk2StoreLane , MessageFlows , TKDependenciesT.get(tk2StoreLane.getName()) , TKpatternName );
									break;
							case "HappyFlowAndDeclinationsAndRejections":
									newLane = (new DEMOPatternHappyFlowAndDeclinationsAndRejectionsExecutor()).CreateElements_and_Sequence(newLane , tk2StoreLane , MessageFlows , TKDependenciesT.get(tk2StoreLane.getName()) , TKpatternName );
									break;
							case "Custom": 
									newLane = (new DEMOPatternCustomExecutor()).CreateElements_and_Sequence(newLane , tk2StoreLane , MessageFlows , TKDependenciesT.get(tk2StoreLane.getName()) , TKpatternName);
									break;
							case "CustomHappyFlowOnly":
									newLane = (new DEMOPatternCustomExecutorHappyFlowOnly()).CreateElements_and_Sequence(newLane , tk2StoreLane , MessageFlows , TKDependenciesT.get(tk2StoreLane.getName()) , TKpatternName );
									break;																	
							case "Complete":									
									newLane = (new DEMOPatternExecutor()).CreateElements_and_Sequence(newLane , tk2StoreLane , MessageFlows , TKDependenciesT.get(tk2StoreLane.getName()) , TKpatternName);
									break;
							default:
									newLane = (new DEMOPatternHappyFlowExecutor()).CreateElements_and_Sequence(newLane , tk2StoreLane , MessageFlows , TKDependenciesT.get(tk2StoreLane.getName()) , TKpatternName);
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
	
	
	private static Lane SearchLane(String name_l , ArrayList<Pool> Pools_t)
	{
		Lane lane_R = null;
		for (Pool pool_t:Pools_t)
			for (Lane lane_t:pool_t.getLanes())
				if (lane_t.getName().compareTo(name_l) == 0 ) return (lane_t);
	
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
	 
	 public static void connectDependencies(Lane laneE_tk , Lane laneI_tn , String dependency)
	 {
		 QName out_e = SearchQName( laneE_tk , "DIVERGE_" + dependency );
		 QName in_i  = SearchQName( laneI_tn , "INITIAL" );
		 QName out_i = SearchQName( laneI_tn , "Accept Product" );
		 QName in_e  = SearchQName( laneE_tk , "CONVERGE_" + dependency );
		 		 
		 laneE_tk.addSequenceFlow(new BPMNSequenceFlow( out_e , in_i ));
		 laneI_tn.addSequenceFlow(new BPMNSequenceFlow( out_i , in_e ));
		 
		 // SpecifyIncoming-Outgoing
		 DEMOPatternExecutor tmp = new DEMOPatternExecutor(); 
		 laneE_tk = tmp.SpecifyIncoming_Outgoing_BetweenLanes(laneE_tk , laneI_tn);
		 laneI_tn = tmp.SpecifyIncoming_Outgoing_BetweenLanes(laneI_tn , laneE_tk);
		 
		 System.out.println("connectDependencies: " + laneE_tk.getName() + " with " + laneI_tn.getName() + " on " + dependency);
		 System.out.println("connectDependencies: elements =" + out_e + " -> " +  in_i);
		 System.out.println("connectDependencies: and elements =" + out_i + " -> " + in_e);
	 }	
		
	
	/**
	 * @param poolsReceived
	 * @param event 
	 */
	private static void ProduceBPMN2XML(ArrayList<Pool> poolsReceived)
	{
		 ObjectFactory factory = new ObjectFactory();
		 org.omg.spec.bpmn._20100524.di.ObjectFactory factoryBPMN = new org.omg.spec.bpmn._20100524.di.ObjectFactory();
		 org.omg.spec.dd._20100524.dc.ObjectFactory factoryDD = new org.omg.spec.dd._20100524.dc.ObjectFactory(); 
		 org.omg.spec.dd._20100524.di.ObjectFactory factoryDI = new org.omg.spec.dd._20100524.di.ObjectFactory();

		 
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
			 JAXBElement<TTask> element_task;			 
			 JAXBElement<TStartEvent> element_startevt;
			 JAXBElement<TEndEvent> element_endevt;
			 JAXBElement<TIntermediateCatchEvent> element_IntermediateCatchevt;
			 JAXBElement<TIntermediateThrowEvent> element_IntermediateThrowevt;
			 JAXBElement<TMessageEventDefinition> element_MessageEventDefinition;
			 JAXBElement<TTerminateEventDefinition> element_TerminateEventDefinition;
			 JAXBElement<TCompensateEventDefinition> element_CompensateEventDefinition; 
			 JAXBElement<TExclusiveGateway> element_ExclusiveGateway;
			 JAXBElement<TEventBasedGateway> element_EventBasedGateway;
			 JAXBElement<TParallelGateway> element_ParallelGateway;
			 JAXBElement<TFlowNode> flow;
			 JAXBElement<TBoundaryEvent> element_boundaryevent;
			 
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
			 definitions.setExporter("DEMO2BPMN-2020");
			 definitions.getBPMNDiagram().add(bpmndiagram);
			 
	

			 for (Pool pool_toAdd:poolsReceived)
			 {
				 X = 100;

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
 					 X = 100;

 					 TLane lane = factory.createTLane();
 					 lane.setId(lane_toAdd.getQname_Lane().toString());
 					 lane.setName(lane_toAdd.getName());
 					 laneset.getLane().add(lane);
 					 
 					 BPMNShape bpmnShape_l = factoryBPMN.createBPMNShape();
 					 Bounds bounds_l = factoryDD.createBounds(); 					 
 					 bounds_l.setX(X+30); 					
 					 bounds_l.setY(Y + relative_lane_position);
 					 bounds_l.setWidth(lane_toAdd.getWidth() - 30); // 30 to guarantee space for lane name  					
 					 bounds_l.setHeight(lane_toAdd.getHeight());		 				
 					 bpmnShape_l.setBpmnElement(lane_toAdd.getQname_Lane());		 
 					 bpmnShape_l.setIsHorizontal(true);
 					 bpmnShape_l.setId(lane_toAdd.getQname_Shape().toString());		 	 
 					 bpmnShape_l.setBounds(bounds_l); 				
 					 bpmnplane.getDiagramElement().add( factoryBPMN.createBPMNShape(bpmnShape_l));
 					 
 					 for (HashMap.Entry<Integer, ArrayList<BPMNElement>> entry : lane_toAdd.getOrganizedBPMNElementByLevel().entrySet()) 
 					 {
 	 					 X = 100;
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
	  							 // if other type of Gateway add another if clause
	 							// else if (...) { ...}
		 							
	 							 if (gtw != null) // add to flow
	 							{
									 gtw.setId(gateway_toAdd.getQname_BPMNElement().toString());
									 gtw.setName(gateway_toAdd.getName());
									 
	 	 	 						// Specify incoming flows of gateway
	 	 	 						for (QName in_flow: element_toAdd.getQname_flow_Incoming())	gtw.getIncoming().add(in_flow);
	 	 	 						// Specify incoming flows of gateway
	 	 	 						for (QName out_flow: element_toAdd.getQname_flow_Outgoing()) gtw.getOutgoing().add(out_flow);

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
 				 }
 				 
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
				System.out.println("BPMN model SENT to: " + output_file_bpmn + " file!");
			}
			marshaller.marshal(element,System.out);
			
		} catch (JAXBException e) { e.printStackTrace(); }		 		 
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
		
		
		System.out.println(			
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
		
		
		
		
		
	 	 
	 	 if ( DeltaX > 0 && Math.abs(DeltaY) <= AcceptedDelta) // Quadrant 1
	 	 {
	 		 Xi =  Xi; 	 Yi = Yi;
	 		 X1 =  Xi;	 Y1 = Yi;
	 		 X2 =  Xf; 	 Y2 = Yf;
	 		 Xf =  Xf ;  Yf = Yf ;
	 		 System.out.println("Routing: Quadrant 1");
	 	 }
	 	 else if ( DeltaX > 0 && DeltaY < AcceptedDelta) // Quadrant 2
	 	 {
	 		Xi = Xi - (WidthElement_i / 4);  Yi = Yi - (HeightElement_i / 2) +10 ; //other option
	 		//Xi = Xi - (WidthElement_i / 2);  Yi = Yi - (HeightElement_i / 2) ; 
	 		X1 = Xi ;  Y1 = Yi - (Offset-15);
	 		X2 = Xf + (WidthElement_f / 2) ;  Y2 = Y1 ;
	 		Xf = Xf + (WidthElement_f / 2) ;  Yf = Yf + (HeightElement_f / 2);
	 		 System.out.println("Routing: Quadrant 2");

	 	 }
	 	 else if ( Math.abs(DeltaX) <= AcceptedDelta && DeltaY < 0 ) // Quadrant 3
	 	 {
		 	Xi = Xi - (WidthElement_i / 2);  Yi = Yi - (HeightElement_i / 2);
	 		X1 = Xi;  Y1 = Yi;
	 		X2 = Xi;  Y2 = Yi;
	 		Xf = Xf + (WidthElement_f / 2) ;  Yf = Yf + (HeightElement_f / 2);
	 		 System.out.println("Routing: Quadrant 3");

	 	 }
	 	 else if ( DeltaX < AcceptedDelta && DeltaY < 0 ) // Quadrant 4
	 	 {
		    Xi = Xi - ((3 * WidthElement_i) / 4) ;  Yi = Yi - (HeightElement_i / 2) +10; //other option
	 		//Xi = Xi - ( WidthElement_i / 2) ;  Yi = Yi - (HeightElement_i / 2); 
	 		X1 = Xi ;  Y1 = Yi - (Offset+5);
	 		X2 = Xf + (WidthElement_f / 2) ;  Y2 = Y1;
	 		Xf = X2 ;  Yf = Yf + (HeightElement_f / 2);
	 		System.out.println("Routing: Quadrant 4");

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
	 		 System.out.println("Routing: Quadrant 5");

	 	 }
	 	 else if ( DeltaX < 0 &&  Math.abs(DeltaX) > AcceptedDelta && DeltaY > 0 ) // Quadrant 6
	 	 {
		 	Xi = Xi - ( (3 * WidthElement_i) / 4);  Yi = Yi + (HeightElement_i / 2) -10; //other option
	 	//	Xi = Xi - ( WidthElement_i / 2);  Yi = Yi + (HeightElement_i / 2);
	 		X1 = Xi;  Y1 = Yi + (Offset+10);
	 		X2 = Xf + (WidthElement_f / 2) ;  Y2 = Y1;
	 		Xf = X2 ;  Yf = Yf - (HeightElement_f / 2);
	 		 System.out.println("Routing: Quadrant 6");

	 	 }
	 	 else if ( Math.abs(DeltaX) <= AcceptedDelta && DeltaY > 0 ) // Quadrant 7
	 	 {
		 	Xi = Xi - (WidthElement_i / 2) ;  Yi = Yi + (HeightElement_i / 2);
	 		X1 = Xi;  Y1 = Yi;
	 		X2 = Xi;  Y2 = Yi;
	 		Xf = Xf + (WidthElement_f / 2) ;  Yf = Yf - (HeightElement_f / 2);
	 		 System.out.println("Routing: Quadrant 7");

	 	 }
	 	 else if ( DeltaX > 0 && DeltaY > AcceptedDelta ) // Quadrant 8
	 	 {
		 	Xi = Xi - (WidthElement_i / 4);  Yi = Yi + (HeightElement_i / 2) -10; //other option
	 	//	Xi = Xi - (WidthElement_i / 2);  Yi = Yi + (HeightElement_i / 2);
		 	X1 = Xi ;  Y1 = Yi + (Offset+15);
	 		X2 = Xf + (WidthElement_f / 2);  Y2 = Y1;
	 		Xf = X2 ;  Yf = Yf - (HeightElement_f / 2);
	 		 System.out.println("Routing: Quadrant 8");

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
	
			System.out.println(			
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
		System.out.println("--actors=" + actors_filename + "\n" +
						   "--tpt=" + tpt_filename + "\n" +
						   "--tkdepend=" + tkdepend_filename + "\n" +
						   "--tkview=" + tkview_filename + "\n" +
						   "--output-file-txt=" + output_file_txt + "\n" +
						   "--output-file-bpmn=" +  output_file_bpmn);
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
	
	
	public static void main(String[] args) {
		
		String usage = "The usage of SemantifyingBPMN is the following.\n" +  
				"SemantifyingBPMN-0.0.2 --actors <filename> --tpt <filename> --tkdepend <filename> --output-file-txt <filename> --output-file-bpmn <filename>\n"
				+ "Credits: S�rgio Guerreiro (2021) (github: https://github.com/SemantifyingBPMN/SemantifyingBPMN)\n"
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
				+"--output-file-txt: is a file to store the model in txt format. Optional.\n"
				+"--output-file-bpmn: is a file to store the BPMN model. Optional.\n";
				
		if (args.length == 0) System.out.println(usage);
		else 
		{
			if (VerifyArgs(args))
			{		
				System.out.println ("The following arguments are accepted:");
				CheckArguments();
				System.out.println ("------- Processing starting -------");
	
				Init();
				
				ReadDataFromFiles();
	
				// (Pools) = SpecifyPools(Actors);
				Pools = SpecifyPools(Actors);
				
				// (Pools) = SpecifyLanes(TPT,Pools);
				Pools = SpecifyLanesAndOrganize(TPT , Pools);
				
			
				if (output_file_bpmn != null) ProduceBPMN2XML(Pools);
				if (output_file_txt != null)  ProduceBPMN2TXT(Pools);
				
			
				System.out.println ("------- Processing stopping -------");
			}
			else System.out.println("Application Arguments bad usage.\n\nPlease check syntax.\n\n" + usage);
		}

	}
}
