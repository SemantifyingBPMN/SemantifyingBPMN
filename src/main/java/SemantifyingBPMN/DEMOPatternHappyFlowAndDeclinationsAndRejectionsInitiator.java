package SemantifyingBPMN;

import java.util.ArrayList;

import javax.xml.namespace.QName;

public class DEMOPatternHappyFlowAndDeclinationsAndRejectionsInitiator 
extends DEMOPattern{

	
	public Lane CreateElements_and_Sequence(Lane lane , TransactionKind tk, ArrayList<BPMNMessageFlow> MessageFlows , ArrayList<String> deps , PatternView view ) {
		
	    QName strt = lane.addElement(new Event  ( EventType.Start, "INITIAL" , "INITIAL" , 2));
	    QName act1 = lane.addElement(new Activity  ( ActivityType.ManualTask, "Decide the type of product to order" , "Decide the type of product to order" , 2));
	    QName gtw1 = lane.addElement(new Gateway( GatewayType.Exclusive , "converging gateway" , "converging gateway"  , 2));
		QName act2 = lane.addElement(new Activity( ActivityType.SendTask , "Request product" , "Request product"  , 2));
		QName gtw2 = lane.addElement(new Gateway( GatewayType.Eventbased, "Wait for Request result" , "Wait for Request result" , 2));
		QName evt1 = lane.addElement(new Event  ( EventType.IntermediateMessageCatchEvent, "Promise received" , "Promise received" , 2));
	    QName gtw3 = lane.addElement(new Gateway( GatewayType.Exclusive , "converging gateway" , "converging gateway"  , 2));
	    QName act3 = lane.addElement(new Activity  ( ActivityType.ManualTask, "Check product" , "Check product" , 2));
	    QName gtw4 = lane.addElement(new Gateway( GatewayType.Exclusive , "Is product ok?" , "Is product ok?"  , 2));
		QName act4 = lane.addElement(new Activity( ActivityType.SendTask , "Accept Product" , "Accept Product"  , 2));
	    QName end1 = lane.addElement(new Event  ( EventType.End, "END" , "END" , 2));
		QName act5 = lane.addElementWithShift(new Activity( ActivityType.SendTask , "Reject product" , "Reject product"  , 1) , 0.45);
	    QName end2 = lane.addElementWithShift(new Event  ( EventType.End, "END" , "END" , 5),0.05);
	    QName gtw5 = lane.addElement(new Gateway( GatewayType.Exclusive , "Make new request?" , "Make new request?"  , 5));
	    QName act6 = lane.addElement(new Activity  ( ActivityType.ManualTask, "Decide what to do next" , "Decide what to do next" , 5));
		QName evt2 = lane.addElement(new Event  ( EventType.IntermediateMessageCatchEvent, "Decline received" , "Decline received" , 5));
		QName gtw6 = lane.addElementWithShift(new Gateway( GatewayType.Eventbased, "Wait for Reject result" , "Wait for Reject result" , 3) , 0.4);
		QName evt3 = lane.addElement(new Event  ( EventType.IntermediateMessageCatchEvent, "Declare received" , "Declare received" , 3));
		QName evt4 = lane.addElementWithShift(new Event  ( EventType.IntermediateMessageCatchEvent, "Stop received" , "Stop received" , 4),0.4);
	    QName end3 = lane.addElement(new Event  ( EventType.End, "END" , "END" , 4));
	    
	    lane.addSequenceFlow(new BPMNSequenceFlow(strt , act1));
	    lane.addSequenceFlow(new BPMNSequenceFlow(act1 , gtw1));
	    lane.addSequenceFlow(new BPMNSequenceFlow(gtw1 , act2));
	    lane.addSequenceFlow(new BPMNSequenceFlow(act2 , gtw2));
	    lane.addSequenceFlow(new BPMNSequenceFlow(gtw2 , evt1));
	    lane.addSequenceFlow(new BPMNSequenceFlow(evt1 , gtw3));
	    lane.addSequenceFlow(new BPMNSequenceFlow(gtw3 , gtw6));
	    lane.addSequenceFlow(new BPMNSequenceFlow(gtw6 , evt3));
	    lane.addSequenceFlow(new BPMNSequenceFlow(gtw6 , evt4));
	    lane.addSequenceFlow(new BPMNSequenceFlow(evt4 , end3));
	    lane.addSequenceFlow(new BPMNSequenceFlow(evt3 , act3));
	    lane.addSequenceFlow(new BPMNSequenceFlow(act3 , gtw4));
	    lane.addSequenceFlow(new BPMNSequenceFlow(gtw4 , act4));
	    lane.addSequenceFlow(new BPMNSequenceFlow(gtw4 , act5));
	    lane.addSequenceFlow(new BPMNSequenceFlow(act4 , end1));
	    lane.addSequenceFlow(new BPMNSequenceFlow(act5 , gtw3));
	    lane.addSequenceFlow(new BPMNSequenceFlow(gtw2 , evt2));	    
	    lane.addSequenceFlow(new BPMNSequenceFlow(evt2 , act6));
	    lane.addSequenceFlow(new BPMNSequenceFlow(act6 , gtw5));
	    lane.addSequenceFlow(new BPMNSequenceFlow(gtw5 , gtw1));
	    lane.addSequenceFlow(new BPMNSequenceFlow(gtw5 , end2));


	    if ( CheckMessageFlow(MessageFlows , tk) == false ) //no message flow exists
	    {
			MessageFlows.add(new BPMNMessageFlow(tk, act2, "request (C-act)", true) );
			MessageFlows.add(new BPMNMessageFlow(tk, act4, "accept (C-act)",  true) );
			MessageFlows.add(new BPMNMessageFlow(tk, act5, "reject (C-act)",  true) );
			
			MessageFlows.add(new BPMNMessageFlow(tk, evt1 ,"promise (C-act)", false) );
			MessageFlows.add(new BPMNMessageFlow(tk, evt2 ,"decline (C-act)", false) );
			MessageFlows.add(new BPMNMessageFlow(tk, evt3 ,"declare (C-act)", false) );
			MessageFlows.add(new BPMNMessageFlow(tk, evt4 ,"stop (C-act)", false) );
	    }
	    else
	    {
		    updateMessageFlow(MessageFlows, tk, act2, "request (C-act)" );
		    updateMessageFlow(MessageFlows, tk, act4, "accept (C-act)" );
		    updateMessageFlow(MessageFlows, tk, act5, "reject (C-act)" );
			
		    updateMessageFlow(MessageFlows,tk, evt1 ,"promise (C-act)");
		    updateMessageFlow(MessageFlows,tk, evt2 ,"decline (C-act)");
		    updateMessageFlow(MessageFlows,tk, evt3 ,"declare (C-act)" );
		    updateMessageFlow(MessageFlows,tk, evt4 ,"stop (C-act)" );



	    }
		
		lane = SpecifyIncoming_Outgoing(lane);
		  
		return(lane);
 		  
   	};

}
