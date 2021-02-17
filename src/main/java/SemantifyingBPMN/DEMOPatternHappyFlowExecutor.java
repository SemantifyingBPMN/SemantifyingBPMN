package SemantifyingBPMN;

import java.util.ArrayList;

import javax.xml.namespace.QName;

public class DEMOPatternHappyFlowExecutor 
extends DEMOPattern{

	
	public Lane CreateElements_and_Sequence(Lane lane , TransactionKind tk, ArrayList<BPMNMessageFlow> MessageFlows , ArrayList<String> deps , PatternView view ) {
		
		 boolean RaP = CheckPreviousFixed(deps, new String("RaP"));  
		 boolean RaE = CheckPreviousFixed(deps, new String("RaE"));  
		 boolean RaD = CheckPreviousFixed(deps, new String("RaD"));
		  
		  
		 QName DIVERGE_RaP = null , CONVERGE_RaP = null,
				DIVERGE_RaE = null , CONVERGE_RaE = null,
				DIVERGE_RaD = null , CONVERGE_RaD = null;
	
	    QName strt = lane.addElement(new Event  ( EventType.Start, "INITIAL" , "INITIAL" , 1));
	    QName act1 = lane.addElement(new Activity  ( ActivityType.ManualTask, "Verify if execute product is possible" , "Verify if execute product is possible" , 1));
		QName act2 = lane.addElement(new Activity( ActivityType.SendTask , "Promise product" , "Promise product"  , 1));
		if (RaP)
		{
			 DIVERGE_RaP = lane.addElement(new Gateway( GatewayType.Parallel , "DIVERGE_RaP" , "DIVERGE_RaP"   , 1));
			 CONVERGE_RaP = lane.addElement(new Gateway( GatewayType.Parallel , "CONVERGE_RaP" , "CONVERGE_RaP"   , 1));
		}
		
	    QName act3 = lane.addElement(new Activity  ( ActivityType.ManualTask, "Execute product (P-act)" , "Execute product (P-act)" , 1));
	    if (RaE)
	    {
 			 DIVERGE_RaE = lane.addElement(new Gateway( GatewayType.Parallel , "DIVERGE_RaE" , "DIVERGE_RaE"   , 1));
 			 CONVERGE_RaE = lane.addElement(new Gateway( GatewayType.Parallel , "CONVERGE_RaE" , "CONVERGE_RaE"   , 1)); 
	    }
		QName act4 = lane.addElement(new Activity( ActivityType.SendTask , "Declare product" , "Declare product"  , 1));
		if (RaD)
		{
 			 DIVERGE_RaD = lane.addElement(new Gateway( GatewayType.Parallel , "DIVERGE_RaD" , "DIVERGE_RaD"   , 1));
		}
		QName evt1 = lane.addElement(new Event  ( EventType.IntermediateMessageCatchEvent, "Accept received" , "Accept received" , 1));
		if (RaD) 
		{
	  	      CONVERGE_RaD = lane.addElement(new Gateway( GatewayType.Parallel , "CONVERGE_RaD" , "CONVERGE_RaD"   , 1)); 			 
		}
	    QName end = lane.addElement(new Event  ( EventType.End, "END" , "END" , 1));

	    lane.addSequenceFlow(new BPMNSequenceFlow(strt , act1));
	    lane.addSequenceFlow(new BPMNSequenceFlow(act1 , act2));
	    lane.addSequenceFlow(new BPMNSequenceFlow(act4 , evt1));


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
			  lane.addSequenceFlow(new BPMNSequenceFlow(CONVERGE_RaE , act4));
		  }
		  else 	    lane.addSequenceFlow(new BPMNSequenceFlow(act3 , act4));
		  
		  if (RaD)
		  {
			  lane.addSequenceFlow(new BPMNSequenceFlow(act4 , DIVERGE_RaD));
			  lane.addSequenceFlow(new BPMNSequenceFlow(DIVERGE_RaD , evt1));
			  lane.addSequenceFlow(new BPMNSequenceFlow(evt1 , CONVERGE_RaD));
			  lane.addSequenceFlow(new BPMNSequenceFlow(CONVERGE_RaD,end));
		  }
		  else
		  {
			  lane.addSequenceFlow(new BPMNSequenceFlow(act4 , evt1));
			  lane.addSequenceFlow(new BPMNSequenceFlow(evt1 , end));
		  }
	    
	    
	    if ( CheckMessageFlow(MessageFlows , tk) == true ) // message flow exists
	    {
		    updateMessageFlow(MessageFlows, tk, strt, "request (C-act)");
		    updateMessageFlow(MessageFlows, tk, evt1, "accept (C-act)");
		    updateMessageFlow(MessageFlows, tk, act2 ,"promise (C-act)");
		    updateMessageFlow(MessageFlows, tk, act4 ,"declare (C-act)");
	    }
	    else 
	    {
			MessageFlows.add(new BPMNMessageFlow(tk, strt, "request (C-act)", false) );
			MessageFlows.add(new BPMNMessageFlow(tk, evt1, "accept (C-act)",  false) );
			MessageFlows.add(new BPMNMessageFlow(tk, act2 ,"promise (C-act)", true) );
			MessageFlows.add(new BPMNMessageFlow(tk, act4 ,"declare (C-act)", true) );	    	
	    }
	
		lane = SpecifyIncoming_Outgoing(lane);
		  
		return(lane);
 		  
   	};

}
