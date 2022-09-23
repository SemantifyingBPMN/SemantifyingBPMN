package SemantifyingBPMN;

import java.util.ArrayList;

import javax.xml.namespace.QName;

public class DEMOPatternCustomExecutorHappyFlowOnlySimplified 
extends DEMOPattern{

	
	public Lane CreateElements_and_Sequence(Lane lane , TransactionKind tk, ArrayList<BPMNMessageFlow> MessageFlows , ArrayList<String> deps , PatternView view , boolean isFirst) {
		
		
		//13 elements
		//       1        2           3           4             5           6              7            8             9               11      12  
		//  verify , promise ,DIVERGE_RaP , CONVERGE_RaP , execute , DIVERGE_RaE  , CONVERGE_RaE , declare , DIVERGE_RaD  ,   CONVERGE_RaD , end ;
		 QName[] bpmn_elements = {  null, null, null, null, null, null, null, null , null, null, null, null, null };
		
		 boolean RaP = CheckPreviousFixed(deps, new String("RaP"));  
		 boolean RaE = CheckPreviousFixed(deps, new String("RaE"));  
		 boolean RaD = CheckPreviousFixed(deps, new String("RaD"));
		  		 		
		
		String PromiseDecisionLabel = view.getTKStepValue("Promise Decision");
	    if ( PromiseDecisionLabel.compareTo("") != 0 )
	    	bpmn_elements[1] = lane.addElement(new Activity  ( ActivityType.UserTask, PromiseDecisionLabel , "Verify if execute product is possible" , 1));
	    
	    String PromiseLabel = view.getTKStepValue("Promise");
	    if ( PromiseLabel.compareTo("") != 0 )
	    	bpmn_elements[2] = lane.addElement(new Activity( ActivityType.UserTask , PromiseLabel , "Promise"  , 1));
	 	    
		if (RaP)
		{
			 bpmn_elements[3] = lane.addElement(new Gateway( GatewayType.Inclusive , "DIVERGE_RaP" , "DIVERGE_RaP"   , 1));
			 bpmn_elements[4] = lane.addElement(new Gateway( GatewayType.Inclusive , "CONVERGE_RaP" , "CONVERGE_RaP"   , 1));
		}
		
		String ExecuteLabel = view.getTKStepValue("Execute");
		if ( ExecuteLabel.compareTo("") != 0 )
			bpmn_elements[5] = lane.addElement(new Activity  ( ActivityType.UserTask, ExecuteLabel , "Execute" , 1));
		
	    if (RaE)
	    {
	    	 bpmn_elements[6] = lane.addElement(new Gateway( GatewayType.Inclusive , "DIVERGE_RaE" , "DIVERGE_RaE"   , 1));
	    	 bpmn_elements[7] = lane.addElement(new Gateway( GatewayType.Inclusive , "CONVERGE_RaE" , "CONVERGE_RaE"   , 1)); 
	    }
	    
		String DeclareLabel = view.getTKStepValue("Declare");
		if ( DeclareLabel.compareTo("") != 0 )
			bpmn_elements[8] = lane.addElement(new Activity( ActivityType.UserTask , DeclareLabel , "Declare"  , 1));
		
		if (RaD) bpmn_elements[9] = lane.addElement(new Gateway( GatewayType.Inclusive , "DIVERGE_RaD" , "DIVERGE_RaD"   , 1));		
		

		if (RaD) bpmn_elements[11] = lane.addElement(new Gateway( GatewayType.Inclusive , "CONVERGE_RaD" , "CONVERGE_RaD"   , 1)); 		
		
		
	//	bpmn_elements[12] = lane.addElement(new Event  ( EventType.IntermediateCatchEvent, "END" , "END" , 1));
		

	    // Connect elements
			int source = -1;
			for (int idx = 0 ; idx < bpmn_elements.length ; idx++)
			{			
				if (source == -1 && bpmn_elements[idx] != null) source = idx;
				else if (source != -1 && bpmn_elements[idx] != null)
				{
					lane.addSequenceFlow(new BPMNSequenceFlow( bpmn_elements[source] , bpmn_elements[idx] ) );
					source = idx;
				}
			}
			
		  
		return(lane);
 		  
   	}

	
	
	
	public void connectwithInitiator(Lane Initiator, Lane Executor) {
   
		
   		ArrayList<String> PossibilidadesInitiatorCluster1 = new ArrayList<String>();
   		ArrayList<String> PossibilidadesInitiatorCluster2 = new ArrayList<String>();
   		
   		PossibilidadesInitiatorCluster1.add("INITIAL");
   		PossibilidadesInitiatorCluster1.add("Request Decision");
   		PossibilidadesInitiatorCluster1.add("Request");
		
   		PossibilidadesInitiatorCluster2.add("Check");
   		PossibilidadesInitiatorCluster2.add("Accept");
   		PossibilidadesInitiatorCluster2.add("END");
   		
   		
   		ArrayList<String> PossibilidadesExecutorCluster = new ArrayList<String>();
   		PossibilidadesExecutorCluster.add("Verify if execute product is possible");
   		PossibilidadesExecutorCluster.add("Promise");
   		PossibilidadesExecutorCluster.add("DIVERGE_RaP");
   		PossibilidadesExecutorCluster.add("CONVERGE_RaP");
   		PossibilidadesExecutorCluster.add("Execute");
   		PossibilidadesExecutorCluster.add("DIVERGE_RaE");
   		PossibilidadesExecutorCluster.add("CONVERGE_RaE"); 
   		PossibilidadesExecutorCluster.add("Declare");
   		PossibilidadesExecutorCluster.add("DIVERGE_RaD");
   		PossibilidadesExecutorCluster.add("CONVERGE_RaD");  
   		
   		
   		// From initiator to Executor
   		int Elem_I = -1;
   		for (int Elem_I_1 = 0 ; Elem_I_1 < PossibilidadesInitiatorCluster1.size() ; Elem_I_1++)
   			if (Initiator.SearchQNameByString( PossibilidadesInitiatorCluster1.get(Elem_I_1)) != null) Elem_I = Elem_I_1;   			
   
   		int Elem_E = - 1;
   		for (int Elem_E_1 = 0 ; Elem_E_1 < PossibilidadesExecutorCluster.size() ; Elem_E_1++)
   			if (Executor.SearchQNameByString( PossibilidadesExecutorCluster.get(Elem_E_1)) != null )
   			{
   					Elem_E = Elem_E_1;
   					Elem_E_1 = PossibilidadesExecutorCluster.size();
   			}
    	
   		if (Elem_I != -1 && Elem_E != -1)
   			Initiator.addSequenceFlow( new BPMNSequenceFlow
					( Initiator.SearchQNameByString(PossibilidadesInitiatorCluster1.get(Elem_I)),
					  Executor.SearchQNameByString( PossibilidadesExecutorCluster.get(Elem_E) ) ) );
   		else System.out.println("Warning: There is no way to connect by flowElement: " + Initiator.getName() + " -> " + Executor.getName());
   		
   		
   		
   		
   		
   		
   		// From Executor to initiator
   		Elem_I = -1;
   		for (int Elem_I_2 = 0 ; Elem_I_2 < PossibilidadesInitiatorCluster2.size() ; Elem_I_2++)
   			if (Initiator.SearchQNameByString( PossibilidadesInitiatorCluster2.get(Elem_I_2)) != null) 
   			{
   				Elem_I = Elem_I_2;
   				Elem_I_2 = PossibilidadesInitiatorCluster2.size();
   			}
   
   		Elem_E = - 1;
   		for (int Elem_E_2 = 0 ; Elem_E_2 < PossibilidadesExecutorCluster.size() ; Elem_E_2++)
   			if (Executor.SearchQNameByString( PossibilidadesExecutorCluster.get(Elem_E_2)) != null )
   			{
   					Elem_E = Elem_E_2;
   			}
    	
   		if (Elem_I != -1 && Elem_E != -1)
   			Executor.addSequenceFlow( new BPMNSequenceFlow
   					( Executor.SearchQNameByString(PossibilidadesExecutorCluster.get(Elem_E)),
   					  Initiator.SearchQNameByString(PossibilidadesInitiatorCluster2.get(Elem_I) ) ) );
   		else System.out.println("Warning: There is no way to connect by flowElement: " + Executor.getName() + " -> " + Initiator.getName()); 

   		
   		
   		
		Initiator = SpecifyIncoming_Outgoing_BetweenLanes(Initiator , Executor);
		Executor  = SpecifyIncoming_Outgoing_BetweenLanes(Executor , Initiator);
	
	};

}
