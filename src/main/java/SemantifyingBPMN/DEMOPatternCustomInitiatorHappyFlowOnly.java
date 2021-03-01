package SemantifyingBPMN;

import java.util.ArrayList;

import javax.xml.namespace.QName;

public class DEMOPatternCustomInitiatorHappyFlowOnly 
extends DEMOPattern{

	
	public Lane CreateElements_and_Sequence(Lane lane , TransactionKind tk, ArrayList<BPMNMessageFlow> MessageFlows , ArrayList<String> deps , PatternView view) {
		

		//define elements
		//  0		1			2	  	3			  4				 5  			6		7
		// strt , verify , request , evt promise , event declare , check produt , accept , end ;
		QName[] bpmn_elements = {  null, null, null, null, null, null, null, null  };
		
		
		bpmn_elements[0] = lane.addElement(new Event  ( EventType.Start, "INITIAL" , "INITIAL" , 1));
		
		String RequestDecisionLabel = view.getTKStepValue("Request Decision");
	    if ( RequestDecisionLabel.compareTo("") != 0 )
	    	bpmn_elements[1] = lane.addElement(new Activity  ( ActivityType.ManualTask, RequestDecisionLabel , RequestDecisionLabel , 1));
	    
	    String RequestLabel = view.getTKStepValue("Request");
	    if ( RequestLabel.compareTo("") != 0 )
	    	bpmn_elements[2] = lane.addElement(new Activity( ActivityType.SendTask , RequestLabel , RequestLabel  , 1));
	    
	    
	    String PromiseLabel = view.getTKStepValue("Promise");
	    if ( PromiseLabel.compareTo("") != 0 )
	    {
	    	PromiseLabel += " received";
	    	bpmn_elements[3] = lane.addElement(new Event  ( EventType.IntermediateMessageCatchEvent, PromiseLabel , PromiseLabel , 1));
	    }
		
		String DeclareLabel = view.getTKStepValue("Declare");
		if ( DeclareLabel.compareTo("") != 0 )
		{
			DeclareLabel += " received";
			bpmn_elements[4] = lane.addElement(new Event  ( EventType.IntermediateMessageCatchEvent, DeclareLabel , DeclareLabel , 1));
		}
		
		String AcceptDecisionLabel = view.getTKStepValue("Decision Accept");
	    if ( AcceptDecisionLabel.compareTo("") != 0 )
	    	bpmn_elements[5] = lane.addElement(new Activity  ( ActivityType.ManualTask, AcceptDecisionLabel , AcceptDecisionLabel , 1));
	    
	    String AcceptLabel = view.getTKStepValue("Accept");
	    if ( AcceptLabel.compareTo("") != 0 )
	    	bpmn_elements[6] = lane.addElement(new Activity( ActivityType.SendTask , AcceptLabel , AcceptLabel  , 1));
	    
	    bpmn_elements[7] = lane.addElement(new Event  ( EventType.End, "END" , "END" , 1));

	    
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
	    
	    if ( CheckMessageFlow(MessageFlows , tk) == false ) //no message flow exists
	    {
	    	if ( view.getTKStepValue("Request").compareTo("") != 0) MessageFlows.add(new BPMNMessageFlow(tk, bpmn_elements[2], "request (C-act)", true) );
	    	if ( view.getTKStepValue("Accept").compareTo("") != 0) MessageFlows.add(new BPMNMessageFlow(tk, bpmn_elements[6], "accept (C-act)",  true) );
	    	if ( view.getTKStepValue("Promise").compareTo("") != 0) MessageFlows.add(new BPMNMessageFlow(tk, bpmn_elements[3] ,"promise (C-act)", false) );
	    	if ( view.getTKStepValue("Declare").compareTo("") != 0) MessageFlows.add(new BPMNMessageFlow(tk, bpmn_elements[4] ,"declare (C-act)", false) );
	    }
	    else
	    {
	    	if ( view.getTKStepValue("Request").compareTo("") != 0) updateMessageFlow(MessageFlows, tk, bpmn_elements[2], "request (C-act)");
	    	if ( view.getTKStepValue("Accept").compareTo("") != 0) updateMessageFlow(MessageFlows, tk, bpmn_elements[6], "accept (C-act)");
	    	if ( view.getTKStepValue("Promise").compareTo("") != 0) updateMessageFlow(MessageFlows, tk, bpmn_elements[3] ,"promise (C-act)");
	    	if ( view.getTKStepValue("Declare").compareTo("") != 0) updateMessageFlow(MessageFlows, tk, bpmn_elements[4] ,"declare (C-act)");
	    }
		
		lane = SpecifyIncoming_Outgoing(lane);
		  
		return(lane);
 		  
   	}


}
