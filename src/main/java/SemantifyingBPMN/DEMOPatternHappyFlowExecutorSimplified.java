package SemantifyingBPMN;

import java.util.ArrayList;

import javax.xml.namespace.QName;

public class DEMOPatternHappyFlowExecutorSimplified 
extends DEMOPattern{

	
	public Lane CreateElements_and_Sequence(Lane lane , TransactionKind tk, ArrayList<BPMNMessageFlow> MessageFlows , ArrayList<String> deps , PatternView view , boolean isFirst) {
		
		 boolean RaP = CheckPreviousFixed(deps, new String("RaP"));  
		 boolean RaE = CheckPreviousFixed(deps, new String("RaE"));  
		 boolean RaD = CheckPreviousFixed(deps, new String("RaD"));
		  
		  
		 QName DIVERGE_RaP = null , CONVERGE_RaP = null,
				DIVERGE_RaE = null , CONVERGE_RaE = null,
				DIVERGE_RaD = null , CONVERGE_RaD = null;
	
	    QName act1 = lane.addElement(new Activity ( ActivityType.Task, "Verify if execute product is possible" , "Verify if execute product is possible" , 1));
		QName act2 = lane.addElement(new Activity ( ActivityType.Task , "Promise" , "Promise"  , 1));
		if (RaP)
		{
			 DIVERGE_RaP = lane.addElement(new Gateway( GatewayType.Inclusive , "DIVERGE_RaP" , "DIVERGE_RaP"   , 1));
			 CONVERGE_RaP = lane.addElement(new Gateway( GatewayType.Inclusive , "CONVERGE_RaP" , "CONVERGE_RaP"   , 1));
		}
		
	    QName act3 = lane.addElement(new Activity  ( ActivityType.Task, "Execute (P-act)" , "Execute (P-act)" , 1));
	    if (RaE)
	    {
			 DIVERGE_RaE = lane.addElement(new Gateway( GatewayType.Inclusive , "DIVERGE_RaE" , "DIVERGE_RaE"   , 1));
			 CONVERGE_RaE = lane.addElement(new Gateway( GatewayType.Inclusive , "CONVERGE_RaE" , "CONVERGE_RaE"   , 1)); 
	    }
		QName act4 = lane.addElement(new Activity( ActivityType.Task , "Declare" , "Declare"  , 1));
		if (RaD)
		{
 			 DIVERGE_RaD = lane.addElement(new Gateway( GatewayType.Inclusive , "DIVERGE_RaD" , "DIVERGE_RaD"   , 1));
    	     CONVERGE_RaD = lane.addElement(new Gateway( GatewayType.Inclusive , "CONVERGE_RaD" , "CONVERGE_RaD"   , 1));
		}

	    lane.addSequenceFlow(new BPMNSequenceFlow(act1 , act2));
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
			  lane.addSequenceFlow(new BPMNSequenceFlow(DIVERGE_RaD ,  CONVERGE_RaD));
		  }
		  else
		  {
			  
		  }
	  

		return(lane);
 		  
   	};
   	
   	
   	public void connectwithInitiator(Lane Initiator , Lane Executor)
   	{

		
		Initiator.addSequenceFlow( new BPMNSequenceFlow
   									( Initiator.SearchQNameByString("Request"),
   									  Executor.SearchQNameByString("Verify if execute product is possible") ) );
   
   		
   		// declare with check or converge_RaD with check		
   		if ( Executor.SearchQNameByString("CONVERGE_RaD") == null) 
	   		Executor.addSequenceFlow( new BPMNSequenceFlow
										( Executor.SearchQNameByString("Declare"),
										  Initiator.SearchQNameByString("Check") ) );
   		else Executor.addSequenceFlow( new BPMNSequenceFlow
   										( Executor.SearchQNameByString("CONVERGE_RaD"),
   										  Initiator.SearchQNameByString("Check") ) );

   		
		  Initiator = SpecifyIncoming_Outgoing_BetweenLanes(Initiator , Executor);
		  Executor  = SpecifyIncoming_Outgoing_BetweenLanes(Executor , Initiator);
   		
   	}

}
