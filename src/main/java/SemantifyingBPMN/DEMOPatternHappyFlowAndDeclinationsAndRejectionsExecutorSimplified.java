package SemantifyingBPMN;

import java.util.ArrayList;

import javax.xml.namespace.QName;

public class DEMOPatternHappyFlowAndDeclinationsAndRejectionsExecutorSimplified 
extends DEMOPattern{

	
	public Lane CreateElements_and_Sequence(Lane lane , TransactionKind tk, ArrayList<BPMNMessageFlow> MessageFlows , ArrayList<String> deps , PatternView view, boolean isFirst) {
		
		 boolean RaP = CheckPreviousFixed(deps, new String("RaP"));  
		 boolean RaE = CheckPreviousFixed(deps, new String("RaE"));  
		 boolean RaD = CheckPreviousFixed(deps, new String("RaD"));		  
		  
		 QName DIVERGE_RaP = null , CONVERGE_RaP = null,
			   DIVERGE_RaE = null , CONVERGE_RaE = null,
			   DIVERGE_RaD = null , CONVERGE_RaD = null;		 
		
	     QName act1 = lane.addElementWithShift(new Activity ( ActivityType.ManualTask, "Verify if execute product is possible" , "Verify if execute product is possible" , 2) , 0.2);
		 QName gtw1 = lane.addElement(new Gateway  ( GatewayType.Exclusive , "OK to produce?" , "OK to produce?"  , 2));
		 QName act2 = lane.addElement(new Activity( ActivityType.Task , "Promise" , "Promise"  , 2));
		 if (RaP)
		 {
	 		 DIVERGE_RaP = lane.addElement(new Gateway( GatewayType.Inclusive , "DIVERGE_RaP" , "DIVERGE_RaP"   , 2));
 			 CONVERGE_RaP = lane.addElement(new Gateway( GatewayType.Inclusive , "CONVERGE_RaP" , "CONVERGE_RaP"   , 2));
		 } 	
	     QName act3 = lane.addElement(new Activity  ( ActivityType.ManualTask, "Execute (P-act)" , "Execute (P-act)" , 2));
	     if (RaE)
	     {
 			 DIVERGE_RaE = lane.addElement(new Gateway( GatewayType.Inclusive , "DIVERGE_RaE" , "DIVERGE_RaE"   , 2));
 			 CONVERGE_RaE = lane.addElement(new Gateway( GatewayType.Inclusive , "CONVERGE_RaE" , "CONVERGE_RaE"   , 2)); 
	     }	    
		 QName gtw2 = lane.addElement(new Gateway( GatewayType.Exclusive , "converging" , "converging"  , 2));	 
		 QName act4 = null;
		 QName act5 = null;

		 
		 if (RaD) 
		 {
			 act4 = lane.addElement(new Activity( ActivityType.Task , "Declare" , "Declare"  , 2));
			 DIVERGE_RaD = lane.addElement(new Gateway( GatewayType.Inclusive , "DIVERGE_RaD" , "DIVERGE_RaD"   , 2));
			 CONVERGE_RaD = lane.addElement(new Gateway( GatewayType.Inclusive , "CONVERGE_RaD" , "CONVERGE_RaD"   , 2));
		 }
		 QName gtw4 = lane.addElement(new Gateway( GatewayType.Exclusive , "Make declare adapted?" , "Make declare adapted?"  , 2));
		 QName act7 = lane.addElementWithShift(new Activity( ActivityType.ManualTask , "Decide what to do" , "Decide what to do"  , 2),0.05); 
		 
		 
		 QName act6 = lane.addElementWithShift(new Activity( ActivityType.Task , "Decline" , "Decline"  , 1) , 0.275);
		 
		 if (!RaD) 
		 {
			 act4 = lane.addElementWithShift(new Activity( ActivityType.Task , "Declare" , "Declare"  , 1) , 0.15);
			 act5 = lane.addElementWithShift(new Activity( ActivityType.Task , "Agree with arguments for rejection" , "Agree with arguments for rejection"  , 1),0.05);
		 }
		 else act5 = lane.addElementWithShift(new Activity( ActivityType.Task , "Agree with arguments for rejection" , "Agree with arguments for rejection"  , 1),0.25);
		 
		  	    
	     QName end2 = lane.addElement(new Event  ( EventType.End, "END" , "END" , 1));

	     
	    lane.addSequenceFlow(new BPMNSequenceFlow(act1 , gtw1));
	    lane.addSequenceFlow(new BPMNSequenceFlow(gtw1 , act2));	    
	    lane.addSequenceFlow(new BPMNSequenceFlow(gtw1 , act6));
	    
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
	    if (RaD)
		{
		    lane.addSequenceFlow(new BPMNSequenceFlow(act4 , DIVERGE_RaD));
		    lane.addSequenceFlow(new BPMNSequenceFlow(DIVERGE_RaD , CONVERGE_RaD));			
		}
	    
	    lane.addSequenceFlow(new BPMNSequenceFlow(gtw2 , act4));

	    lane.addSequenceFlow(new BPMNSequenceFlow(act7 , gtw4));
	    lane.addSequenceFlow(new BPMNSequenceFlow(gtw4 , gtw2));
	    lane.addSequenceFlow(new BPMNSequenceFlow(gtw4 , act5));
	    lane.addSequenceFlow(new BPMNSequenceFlow(act5 , end2));
		  
		return(lane);
 		  
   	}

	public void connectwithInitiator(Lane Initiator, Lane Executor) 
	{

		
   		// Request with verify
   		Initiator.addSequenceFlow( new BPMNSequenceFlow
   									( Initiator.SearchQNameByString("Request"),
   									  Executor.SearchQNameByString("Verify if execute product is possible") ) );
   		Initiator.addSequenceFlow( new BPMNSequenceFlow
   									( Initiator.SearchQNameByString("Reject"),
   									  Executor.SearchQNameByString("Decide what to do") ) );

   		
   		
   		// declare with check or converge_RaD with check		
   		Executor.addSequenceFlow( new BPMNSequenceFlow
				( Executor.SearchQNameByString("Decline"),
				  Initiator.SearchQNameByString("Decide what to do next") ) );
   		   		
   		
   		
   		if ( Executor.SearchQNameByString("CONVERGE_RaD") == null) 
	   		Executor.addSequenceFlow( new BPMNSequenceFlow
										( Executor.SearchQNameByString("Declare"),
										  Initiator.SearchQNameByString("Check") ) );
   		else Executor.addSequenceFlow( new BPMNSequenceFlow
   										( Executor.SearchQNameByString("CONVERGE_RaD"),
   										  Initiator.SearchQNameByString("Check") ) );

		  Initiator = SpecifyIncoming_Outgoing_BetweenLanes(Initiator , Executor);
		  Executor  = SpecifyIncoming_Outgoing_BetweenLanes(Executor , Initiator);
		
	};

}
