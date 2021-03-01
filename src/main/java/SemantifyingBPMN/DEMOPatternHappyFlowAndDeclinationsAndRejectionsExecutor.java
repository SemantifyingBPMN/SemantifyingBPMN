package SemantifyingBPMN;

import java.util.ArrayList;

import javax.xml.namespace.QName;

public class DEMOPatternHappyFlowAndDeclinationsAndRejectionsExecutor 
extends DEMOPattern{

	
	public Lane CreateElements_and_Sequence(Lane lane , TransactionKind tk, ArrayList<BPMNMessageFlow> MessageFlows , ArrayList<String> deps , PatternView view) {
		
		 boolean RaP = CheckPreviousFixed(deps, new String("RaP"));  
		 boolean RaE = CheckPreviousFixed(deps, new String("RaE"));  
		 boolean RaD = CheckPreviousFixed(deps, new String("RaD"));
		  
		  
		 QName DIVERGE_RaP = null , CONVERGE_RaP = null,
			   DIVERGE_RaE = null , CONVERGE_RaE = null,
			   DIVERGE_RaD = null , CONVERGE_RaD = null;
		 
		
		 QName strt = lane.addElement(new Event  ( EventType.Start, "INITIAL" , "INITIAL" , 3));
	     QName act1 = lane.addElement(new Activity  ( ActivityType.ManualTask, "Verify if execute product is possible" , "Verify if execute product is possible" , 3));
		 QName gtw1 = lane.addElement(new Gateway( GatewayType.Exclusive , "ok to produce?" , "ok to produce?"  , 3));

		 QName act2 = lane.addElement(new Activity( ActivityType.SendTask , "Promise product" , "Promise product"  , 3));
		 if (RaP)
		 {
	 		 DIVERGE_RaP = lane.addElement(new Gateway( GatewayType.Parallel , "DIVERGE_RaP" , "DIVERGE_RaP"   , 3));
 			 CONVERGE_RaP = lane.addElement(new Gateway( GatewayType.Parallel , "CONVERGE_RaP" , "CONVERGE_RaP"   , 3));
		 } 
		
	     QName act3 = lane.addElement(new Activity  ( ActivityType.ManualTask, "Execute product (P-act)" , "Execute product (P-act)" , 3));
	     if (RaE)
	     {
 			 DIVERGE_RaE = lane.addElement(new Gateway( GatewayType.Parallel , "DIVERGE_RaE" , "DIVERGE_RaE"   , 3));
 			 CONVERGE_RaE = lane.addElement(new Gateway( GatewayType.Parallel , "CONVERGE_RaE" , "CONVERGE_RaE"   , 3)); 
	     }
	    
		 QName gtw2 = lane.addElement(new Gateway( GatewayType.Exclusive , "converging" , "converging"  , 3));
		 QName act4 = lane.addElement(new Activity( ActivityType.SendTask , "Declare product" , "Declare product"  , 3));
		 if (RaD)
		 {
 			 DIVERGE_RaD = lane.addElement(new Gateway( GatewayType.Parallel , "DIVERGE_RaD" , "DIVERGE_RaD"   , 3));
		 }
	 	 QName gtw3 = lane.addElement(new Gateway( GatewayType.Eventbased, "Wait for Declare result" , "Wait for Declare result" , 3));
		 QName evt1 = lane.addElement(new Event  ( EventType.IntermediateMessageCatchEvent, "Accept received" , "Accept received" , 3));
		 if (RaD) 
		 {
	  	      CONVERGE_RaD = lane.addElement(new Gateway( GatewayType.Parallel , "CONVERGE_RaD" , "CONVERGE_RaD"   , 3)); 			 
		 }
	    QName end = lane.addElement(new Event  ( EventType.End, "END" , "END" , 3));
	    
	    QName act5 = lane.addElementWithShift(new Activity( ActivityType.SendTask , "Agree with arguments for rejection" , "Agree with arguments for rejection"  , 1) , 0.32);	    
	    QName end2 = lane.addElement(new Event  ( EventType.End, "END" , "END" , 1));
	    
		QName act6 = lane.addElementWithShift(new Activity( ActivityType.SendTask , "Decline product" , "Decline product"  , 2) , 0.1);
		QName end3 = lane.addElement(new Event  ( EventType.End, "END" , "END" , 2));
	    QName gtw4 = lane.addElementWithShift(new Gateway( GatewayType.Exclusive , "Agree with arguments?" , "Agree with arguments?"  , 2) , 0.1);	    

	    QName act7 = lane.addElement(new Activity( ActivityType.ManualTask , "Evaluate arguments for rejection" , "Evaluate arguments for rejection"  , 2));
		QName evt2 = lane.addElement(new Event  ( EventType.IntermediateMessageCatchEvent, "Reject received" , "Reject received" , 2));
	    
	    lane.addSequenceFlow(new BPMNSequenceFlow(strt , act1));
	    lane.addSequenceFlow(new BPMNSequenceFlow(act1 , gtw1));
	    lane.addSequenceFlow(new BPMNSequenceFlow(gtw1 , act6));
	    lane.addSequenceFlow(new BPMNSequenceFlow(act6 , end3));
	    lane.addSequenceFlow(new BPMNSequenceFlow(gtw1 , act2));
        if (RaP)
	    {
		    lane.addSequenceFlow(new BPMNSequenceFlow(act2 , DIVERGE_RaP));
		    lane.addSequenceFlow(new BPMNSequenceFlow(DIVERGE_RaP , CONVERGE_RaP));
  		   lane.addSequenceFlow(new BPMNSequenceFlow(CONVERGE_RaP , act3));
	    }
        else lane.addSequenceFlow(new BPMNSequenceFlow(act2 , act3));
  	    if (RaE)
	    {
		    lane.addSequenceFlow(new BPMNSequenceFlow(act3 , DIVERGE_RaE));
		    lane.addSequenceFlow(new BPMNSequenceFlow(DIVERGE_RaE , CONVERGE_RaE));
  		    lane.addSequenceFlow(new BPMNSequenceFlow(CONVERGE_RaE , gtw2));
	    }
  	    else lane.addSequenceFlow(new BPMNSequenceFlow(act3 , gtw2));
	    lane.addSequenceFlow(new BPMNSequenceFlow(gtw2 , act4));
	    if (RaD)
		{
		    lane.addSequenceFlow(new BPMNSequenceFlow(act4 , DIVERGE_RaD));
		    lane.addSequenceFlow(new BPMNSequenceFlow(DIVERGE_RaD , gtw3));
		    lane.addSequenceFlow(new BPMNSequenceFlow(gtw3 , evt1));
			lane.addSequenceFlow(new BPMNSequenceFlow(evt1 , CONVERGE_RaD));
			lane.addSequenceFlow(new BPMNSequenceFlow(CONVERGE_RaD,end));
		}
		else
		{
		    lane.addSequenceFlow(new BPMNSequenceFlow(act4 , gtw3));
		    lane.addSequenceFlow(new BPMNSequenceFlow(gtw3 , evt1));
			lane.addSequenceFlow(new BPMNSequenceFlow(evt1 , end));
		}
	    lane.addSequenceFlow(new BPMNSequenceFlow(gtw3 , evt2));
	    lane.addSequenceFlow(new BPMNSequenceFlow(evt2 , act7));
	    lane.addSequenceFlow(new BPMNSequenceFlow(act7 , gtw4));
	    lane.addSequenceFlow(new BPMNSequenceFlow(gtw4 , gtw2));
	    lane.addSequenceFlow(new BPMNSequenceFlow(gtw4 , act5));
	    lane.addSequenceFlow(new BPMNSequenceFlow(act5 , end2));
	    
		
	    
	    if ( CheckMessageFlow(MessageFlows , tk) == true ) // message flow exists
	    {		
		    updateMessageFlow(MessageFlows, tk, strt, "request (C-act)" );
		    updateMessageFlow(MessageFlows, tk, evt1, "accept (C-act)" );
		    updateMessageFlow(MessageFlows, tk, evt2, "reject (C-act)" );
			
		    updateMessageFlow(MessageFlows,tk, act2 ,"promise (C-act)");
		    updateMessageFlow(MessageFlows,tk, act6 ,"decline (C-act)");
		    updateMessageFlow(MessageFlows,tk, act4 ,"declare (C-act)" );
		    updateMessageFlow(MessageFlows,tk, act5 ,"stop (C-act)" );
	    }
	    else
	    {
			MessageFlows.add(new BPMNMessageFlow(tk, strt, "request (C-act)", false) );
			MessageFlows.add(new BPMNMessageFlow(tk, evt1, "accept (C-act)",  false) );
			MessageFlows.add(new BPMNMessageFlow(tk, evt2, "reject (C-act)",  false) );
			
			MessageFlows.add(new BPMNMessageFlow(tk, act2 ,"promise (C-act)", true) );
			MessageFlows.add(new BPMNMessageFlow(tk, act6 ,"decline (C-act)", true) );
			MessageFlows.add(new BPMNMessageFlow(tk, act4 ,"declare (C-act)", true) );
			MessageFlows.add(new BPMNMessageFlow(tk, act5 ,"stop (C-act)", true) );
	
	    	
	    }

		
		
		
		
		lane = SpecifyIncoming_Outgoing(lane);
		  
		return(lane);
 		  
   	};

}
