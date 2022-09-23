package SemantifyingBPMN;

import java.util.ArrayList;

import javax.xml.namespace.QName;

public class DEMOPatternHappyFlowAndDeclinationsAndRejectionsInitiatorSimplified 
extends DEMOPattern{

	
	public Lane CreateElements_and_Sequence(Lane lane , TransactionKind tk, ArrayList<BPMNMessageFlow> MessageFlows , ArrayList<String> deps , PatternView view , boolean isFirst) {
		
		
		QName strt;
		if (isFirst) strt = lane.addElement(new Event  ( EventType.Start, "INITIAL" , "INITIAL" , 2));
		else strt = lane.addElement(new Event  ( EventType.IntermediateCatchEvent, "INITIAL" , "INITIAL" , 2) );
	    
	    QName act1 = lane.addElement(new Activity  ( ActivityType.ManualTask, "Decide the type of product to order" , "Decide the type of product to order" , 2));
	    QName gtw1 = lane.addElement(new Gateway( GatewayType.Exclusive , "converging gateway" , "converging gateway"  , 2));
		QName act2 = lane.addElement(new Activity( ActivityType.Task , "Request" , "Request"  , 2));
		QName act5 = lane.addElementWithShift(new Activity( ActivityType.Task , "Reject" , "Reject"  , 2) , 0.45);
		
		QName end1 = lane.addElementWithShift(new Event  ( EventType.End, "END" , "END" , 1) , 0.1);	
	    QName gtw5 = lane.addElement(new Gateway( GatewayType.Exclusive , "Make adapted request?" , "Make adapted request?"  , 1));
	    QName act6 = lane.addElementWithShift(new Activity  ( ActivityType.ManualTask, "Decide what to do next" , "Decide what to do next" , 1), 0.05);
	    QName act3 = lane.addElementWithShift(new Activity  ( ActivityType.ManualTask, "Check" , "Check" , 1) , 0.2);
	    QName gtw4 = lane.addElement(new Gateway( GatewayType.Exclusive , "Is product ok?" , "Is product ok?"  , 1));
	    QName act4 = lane.addElement(new Activity( ActivityType.Task , "Accept" , "Accept"  , 1));

		
	    
	    lane.addSequenceFlow(new BPMNSequenceFlow(strt , act1));
	    lane.addSequenceFlow(new BPMNSequenceFlow(act1 , gtw1));
	    lane.addSequenceFlow(new BPMNSequenceFlow(gtw1 , act2));
	    
	    lane.addSequenceFlow(new BPMNSequenceFlow(act6 , gtw5));
	    lane.addSequenceFlow(new BPMNSequenceFlow(gtw5 , end1));
	    lane.addSequenceFlow(new BPMNSequenceFlow(gtw5 , gtw1));
	    
	    lane.addSequenceFlow(new BPMNSequenceFlow(act3 , gtw4));
	    lane.addSequenceFlow(new BPMNSequenceFlow(gtw4 , act4));
	    lane.addSequenceFlow(new BPMNSequenceFlow(gtw4 , act5));
	    	    	    	    
	    
	    if (isFirst) 
	    {
	    	QName end3 = lane.addElement(new Event  ( EventType.End, "END" , "END" , 1));

	    	lane.addSequenceFlow(new BPMNSequenceFlow(act4 , end3));	    		
	    	
	    }
	    
		  
		return(lane);
 		  
   	};

}
