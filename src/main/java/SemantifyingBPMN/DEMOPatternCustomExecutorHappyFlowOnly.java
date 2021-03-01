package SemantifyingBPMN;

import java.util.ArrayList;

import javax.xml.namespace.QName;

public class DEMOPatternCustomExecutorHappyFlowOnly 
extends DEMOPattern{

	
	public Lane CreateElements_and_Sequence(Lane lane , TransactionKind tk, ArrayList<BPMNMessageFlow> MessageFlows , ArrayList<String> deps , PatternView view) {
		
		//13 elements
		//  0       1        2           3           4             5           6              7            8             9          10        11           12
		// strt , verify , promise ,DIVERGE_RaP , CONVERGE_RaP , execute , DIVERGE_RaE  , CONVERGE_RaE , declare , DIVERGE_RaD  , evt1 ,  CONVERGE_RaD  , end;
		 QName[] bpmn_elements = {  null, null, null, null, null, null, null, null , null, null, null, null, null };
		
		 boolean RaP = CheckPreviousFixed(deps, new String("RaP"));  
		 boolean RaE = CheckPreviousFixed(deps, new String("RaE"));  
		 boolean RaD = CheckPreviousFixed(deps, new String("RaD"));
		  		 		
		bpmn_elements[0] = lane.addElement(new Event  ( EventType.Start, "INITIAL" , "INITIAL" , 1));
		
		String PromiseDecisionLabel = view.getTKStepValue("Promise Decision");
	    if ( PromiseDecisionLabel.compareTo("") != 0 )
	    	bpmn_elements[1] = lane.addElement(new Activity  ( ActivityType.ManualTask, PromiseDecisionLabel , PromiseDecisionLabel , 1));
	    
	    String PromiseLabel = view.getTKStepValue("Promise");
	    if ( PromiseLabel.compareTo("") != 0 )
	    	bpmn_elements[2] = lane.addElement(new Activity( ActivityType.SendTask , PromiseLabel , PromiseLabel  , 1));
	 	    
		if (RaP)
		{
			 bpmn_elements[3] = lane.addElement(new Gateway( GatewayType.Parallel , "DIVERGE_RaP" , "DIVERGE_RaP"   , 1));
			 bpmn_elements[4] = lane.addElement(new Gateway( GatewayType.Parallel , "CONVERGE_RaP" , "CONVERGE_RaP"   , 1));
		}
		
		String ExecuteLabel = view.getTKStepValue("Execute");
		if ( ExecuteLabel.compareTo("") != 0 )
			bpmn_elements[5] = lane.addElement(new Activity  ( ActivityType.ManualTask, ExecuteLabel , ExecuteLabel , 1));
	    if (RaE)
	    {
	    	 bpmn_elements[6] = lane.addElement(new Gateway( GatewayType.Parallel , "DIVERGE_RaE" , "DIVERGE_RaE"   , 1));
	    	 bpmn_elements[7] = lane.addElement(new Gateway( GatewayType.Parallel , "CONVERGE_RaE" , "CONVERGE_RaE"   , 1)); 
	    }
	    
		String DeclareLabel = view.getTKStepValue("Declare");
		if ( DeclareLabel.compareTo("") != 0 )
			bpmn_elements[8] = lane.addElement(new Activity( ActivityType.SendTask , DeclareLabel , DeclareLabel  , 1));
		if (RaD)
			bpmn_elements[9] = lane.addElement(new Gateway( GatewayType.Parallel , "DIVERGE_RaD" , "DIVERGE_RaD"   , 1));		
		
	    String AcceptLabel = view.getTKStepValue("Accept");
	    if ( AcceptLabel.compareTo("") != 0 )
	    {
	    	AcceptLabel += " received";
	    	bpmn_elements[10] = lane.addElement(new Event  ( EventType.IntermediateMessageCatchEvent, AcceptLabel , AcceptLabel , 1));
	    }
		if (RaD) 
			bpmn_elements[11] = lane.addElement(new Gateway( GatewayType.Parallel , "CONVERGE_RaD" , "CONVERGE_RaD"   , 1)); 			 

		bpmn_elements[12] = lane.addElement(new Event  ( EventType.End, "END" , "END" , 1));


		
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
			
			
	    // Communications
	    if ( CheckMessageFlow(MessageFlows , tk) == true ) // message flow exists
	    {
		    if ( view.getTKStepValue("Request").compareTo("") != 0) updateMessageFlow(MessageFlows, tk, bpmn_elements[0], "request (C-act)");
		    if ( view.getTKStepValue("Accept").compareTo("") != 0) updateMessageFlow(MessageFlows, tk, bpmn_elements[10], "accept (C-act)");
		    if ( view.getTKStepValue("Promise").compareTo("") != 0) updateMessageFlow(MessageFlows, tk, bpmn_elements[2] ,"promise (C-act)");
		    if ( view.getTKStepValue("Declare").compareTo("") != 0) updateMessageFlow(MessageFlows, tk, bpmn_elements[8] ,"declare (C-act)");
	    }
	    else 
	    {
	    	if ( view.getTKStepValue("Request").compareTo("") != 0) MessageFlows.add(new BPMNMessageFlow(tk, bpmn_elements[0], "request (C-act)", false) );
	    	if ( view.getTKStepValue("Accept").compareTo("") != 0) MessageFlows.add(new BPMNMessageFlow(tk, bpmn_elements[10], "accept (C-act)",  false) );
	    	if ( view.getTKStepValue("Promise").compareTo("") != 0) MessageFlows.add(new BPMNMessageFlow(tk, bpmn_elements[2] ,"promise (C-act)", true) );
	    	if ( view.getTKStepValue("Declare").compareTo("") != 0) MessageFlows.add(new BPMNMessageFlow(tk, bpmn_elements[8] ,"declare (C-act)", true) );	    	
	    }
	    
		lane = SpecifyIncoming_Outgoing(lane);
		  
		return(lane);
 		  
   	};

}
