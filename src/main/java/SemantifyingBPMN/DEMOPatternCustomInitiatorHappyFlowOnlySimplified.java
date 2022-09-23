package SemantifyingBPMN;

import java.util.ArrayList;

import javax.xml.namespace.QName;

public class DEMOPatternCustomInitiatorHappyFlowOnlySimplified 
extends DEMOPattern{

	
	public Lane CreateElements_and_Sequence(Lane lane , TransactionKind tk, ArrayList<BPMNMessageFlow> MessageFlows , ArrayList<String> deps , PatternView view, boolean isFirst) {
		

		//define elements
		//  0		1			2	  	3			  4		 5  
		// strt , verify , request , check produt , accept , end ;
		QName[] bpmn_elements = {  null, null, null, null, null, null   };
		
		
		if (isFirst) bpmn_elements[0] = lane.addElement(new Event  ( EventType.Start, "INITIAL" , "INITIAL" , 1));
		else bpmn_elements[0] = lane.addElement(new Event  ( EventType.IntermediateCatchTimerEvent, "INITIAL" , "INITIAL" , 1));
		
		String RequestDecisionLabel = view.getTKStepValue("Request Decision");
	    if ( RequestDecisionLabel.compareTo("") != 0 )
	    	bpmn_elements[1] = lane.addElement(new Activity  ( ActivityType.UserTask, RequestDecisionLabel , "Request Decision" , 1));
	    
	    String RequestLabel = view.getTKStepValue("Request");
	    if ( RequestLabel.compareTo("") != 0 )
	    	bpmn_elements[2] = lane.addElement(new Activity( ActivityType.UserTask , RequestLabel , "Request"  , 1));

		String AcceptDecisionLabel = view.getTKStepValue("Decision Accept");
	    if ( AcceptDecisionLabel.compareTo("") != 0 )
	    	bpmn_elements[3] = lane.addElement(new Activity  ( ActivityType.UserTask, AcceptDecisionLabel , "Check" , 1));
	    
	    String AcceptLabel = view.getTKStepValue("Accept");
	    if ( AcceptLabel.compareTo("") != 0 )
	    	bpmn_elements[4] = lane.addElement(new Activity( ActivityType.UserTask , AcceptLabel , "Accept"  , 1));
	    
	    if (isFirst) bpmn_elements[5] = lane.addElement(new Event  ( EventType.End, "END" , "END" , 1));	
	    //else bpmn_elements[5] = lane.addElement(new Event  ( EventType.IntermediateCatchEvent, "END" , "END" , 1));
	    
	    
	    // Connect elements
		int source = -1;
		for (int idx = 0 ; idx < 3 ; idx++)
		{			
			if (source == -1 && bpmn_elements[idx] != null) source = idx;
			else if (source != -1 && bpmn_elements[idx] != null)
			{
				lane.addSequenceFlow(new BPMNSequenceFlow( bpmn_elements[source] , bpmn_elements[idx] ) );
				source = idx;
			}
		}
	    
		source = -1;
		for (int idx = 3 ; idx < bpmn_elements.length ; idx++)
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


}
