package SemantifyingBPMN;

import java.util.ArrayList;

import javax.xml.namespace.QName;

public class DEMOPatternHappyFlowInitiator 
extends DEMOPattern{

	
	public Lane CreateElements_and_Sequence(Lane lane , TransactionKind tk, ArrayList<BPMNMessageFlow> MessageFlows , ArrayList<String> deps , PatternView view ) {
		

		


	    QName strt = lane.addElement(new Event  ( EventType.Start, "INITIAL" , "INITIAL" , 1));
	    QName act1 = lane.addElement(new Activity  ( ActivityType.ManualTask, "Decide the type of product to order" , "Decide the type of product to order" , 1));
		QName act2 = lane.addElement(new Activity( ActivityType.SendTask , "Request product" , "Request product"  , 1));
		QName evt1 = lane.addElement(new Event  ( EventType.IntermediateMessageCatchEvent, "Promise received" , "Promise received" , 1));
		QName evt2 = lane.addElement(new Event  ( EventType.IntermediateMessageCatchEvent, "Declare received" , "Declare received" , 1));
	    QName act3 = lane.addElement(new Activity  ( ActivityType.ManualTask, "Check product" , "Check product" , 1));
		QName act4 = lane.addElement(new Activity( ActivityType.SendTask , "Accept Product" , "Accept Product"  , 1));
	    QName end = lane.addElement(new Event  ( EventType.End, "END" , "END" , 1));

	    lane.addSequenceFlow(new BPMNSequenceFlow(strt , act1));
	    lane.addSequenceFlow(new BPMNSequenceFlow(act1 , act2));
	    lane.addSequenceFlow(new BPMNSequenceFlow(act2 , evt1));
	    lane.addSequenceFlow(new BPMNSequenceFlow(evt1 , evt2));
	    lane.addSequenceFlow(new BPMNSequenceFlow(evt2 , act3));
	    lane.addSequenceFlow(new BPMNSequenceFlow(act3 , act4));
	    lane.addSequenceFlow(new BPMNSequenceFlow(act4 , end));

	    
	    if ( CheckMessageFlow(MessageFlows , tk) == false ) //no message flow exists
	    {
			MessageFlows.add(new BPMNMessageFlow(tk, act2, "request (C-act)", true) );
			MessageFlows.add(new BPMNMessageFlow(tk, act4, "accept (C-act)",  true) );
			MessageFlows.add(new BPMNMessageFlow(tk, evt1 ,"promise (C-act)", false) );
			MessageFlows.add(new BPMNMessageFlow(tk, evt2 ,"declare (C-act)", false) );
	    }
	    else
	    {
		    updateMessageFlow(MessageFlows, tk, act2, "request (C-act)");
		    updateMessageFlow(MessageFlows, tk, act4, "accept (C-act)");
		    updateMessageFlow(MessageFlows, tk, evt1 ,"promise (C-act)");
		    updateMessageFlow(MessageFlows, tk, evt2 ,"declare (C-act)");

	    	
	    }
		
		lane = SpecifyIncoming_Outgoing(lane);
		  
		return(lane);
 		  
   	}


}
