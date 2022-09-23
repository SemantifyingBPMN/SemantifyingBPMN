package SemantifyingBPMN;

import java.util.ArrayList;

import javax.xml.namespace.QName;

public class DEMOPatternHappyFlowInitiatorSimplified 
extends DEMOPattern{

	
	public Lane CreateElements_and_Sequence(Lane lane , TransactionKind tk, ArrayList<BPMNMessageFlow> MessageFlows , ArrayList<String> deps , PatternView view , boolean isFirst) {
		
		
		QName strt;
		if (isFirst) strt = lane.addElement(new Event  ( EventType.Start, "INITIAL" , "INITIAL" , 1));
		else strt = lane.addElement(new Event  ( EventType.IntermediateCatchEvent, "INITIAL" , "INITIAL" , 1));
	    QName act1 = lane.addElement(new Activity  ( ActivityType.Task, "Decide the type of product to order" , "Decide the type of product to order" , 1));
		QName act2 = lane.addElement(new Activity( ActivityType.Task , "Request" , "Request"  , 1));
	    QName act3 = lane.addElement(new Activity  ( ActivityType.Task, "Check" , "Check" , 1));
		QName act4 = lane.addElement(new Activity( ActivityType.Task , "Accept" , "Accept"  , 1));

	    lane.addSequenceFlow(new BPMNSequenceFlow(strt , act1));
	    lane.addSequenceFlow(new BPMNSequenceFlow(act1 , act2));
	    lane.addSequenceFlow(new BPMNSequenceFlow(act3 , act4));
	    
	    if (isFirst) 
	    {
	    	QName end  = lane.addElement(new Event  ( EventType.End, "END" , "END" , 1));	
	    	lane.addSequenceFlow(new BPMNSequenceFlow(act4 , end));
	    }
	    
	    

  
		return(lane);
 		  
   	}


}
