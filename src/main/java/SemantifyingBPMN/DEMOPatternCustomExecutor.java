package SemantifyingBPMN;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

import javax.xml.namespace.QName;

public class DEMOPatternCustomExecutor 
extends DEMOPattern{

	
	public Lane CreateElements_and_Sequence(Lane lane , TransactionKind tk, ArrayList<BPMNMessageFlow> MessageFlows , ArrayList<String> deps , PatternView view , boolean isFirst) {
		
		 boolean RaP = CheckPreviousFixed(deps, new String("RaP"));  
		 boolean RaE = CheckPreviousFixed(deps, new String("RaE"));  
		 boolean RaD = CheckPreviousFixed(deps, new String("RaD"));
		  
		  
		 QName DIVERGE_RaP = null , CONVERGE_RaP = null,
			   DIVERGE_RaE = null , CONVERGE_RaE = null,
			   DIVERGE_RaD = null , CONVERGE_RaD = null;
		
		 ArrayList<SemantifiedElement> semantified_elements = new ArrayList<SemantifiedElement>();

			// Provision the full pattern with declinations and rejections in semantified_elements - BPMN elements and all flow
		 QName strt = lane.addElement(new Event  ( EventType.Start, "INITIAL" , "INITIAL" , 3));
	     QName act1 = lane.addElement(new Activity  ( ActivityType.ManualTask, "Promise Decision" , "Promise Decision" , 3));	     
	     QName gtw1 = lane.addElement(new Gateway( GatewayType.Exclusive , "ok to produce?" , "ok to produce?"  , 3));
		 QName act2 = lane.addElement(new Activity( ActivityType.SendTask , "Promise" , "Promise"  , 3));
		 if (RaP)
		 {
//	 		 DIVERGE_RaP = lane.addElement(new Gateway( GatewayType.Parallel , "DIVERGE_RaP" , "DIVERGE_RaP"   , 3));
//			 CONVERGE_RaP = lane.addElement(new Gateway( GatewayType.Parallel , "CONVERGE_RaP" , "CONVERGE_RaP"   , 3));
	 		 DIVERGE_RaP = lane.addElement(new Gateway( GatewayType.Inclusive , "DIVERGE_RaP" , "DIVERGE_RaP"   , 3));
			 CONVERGE_RaP = lane.addElement(new Gateway( GatewayType.Inclusive , "CONVERGE_RaP" , "CONVERGE_RaP"   , 3));

		 } 		
	     QName act3 = lane.addElement(new Activity  ( ActivityType.ManualTask, "Execute" , "Execute" , 3));
	     if (RaE)
	     {
//			 DIVERGE_RaE = lane.addElement(new Gateway( GatewayType.Parallel , "DIVERGE_RaE" , "DIVERGE_RaE"   , 3));
//			 CONVERGE_RaE = lane.addElement(new Gateway( GatewayType.Parallel , "CONVERGE_RaE" , "CONVERGE_RaE"   , 3));
			 DIVERGE_RaE = lane.addElement(new Gateway( GatewayType.Inclusive , "DIVERGE_RaE" , "DIVERGE_RaE"   , 3));
			 CONVERGE_RaE = lane.addElement(new Gateway( GatewayType.Inclusive , "CONVERGE_RaE" , "CONVERGE_RaE"   , 3));

	     }	    
		 QName gtw2 = lane.addElement(new Gateway( GatewayType.Exclusive , "converging" , "converging"  , 3));
		 QName act4 = lane.addElement(new Activity( ActivityType.SendTask , "Declare" , "Declare"  , 3));
		 if (RaD) //DIVERGE_RaD = lane.addElement(new Gateway( GatewayType.Parallel , "DIVERGE_RaD" , "DIVERGE_RaD"   , 3));
			 DIVERGE_RaD = lane.addElement(new Gateway( GatewayType.Inclusive , "DIVERGE_RaD" , "DIVERGE_RaD"   , 3));
	 	 QName gtw3 = lane.addElement(new Gateway( GatewayType.Eventbased, "Wait for Declare result" , "Wait for Declare result" , 3));
		 QName evt1 = lane.addElement(new Event  ( EventType.IntermediateMessageCatchEvent, "Accept received" , "Accept received" , 3));
		 if (RaD) //CONVERGE_RaD = lane.addElement(new Gateway( GatewayType.Parallel , "CONVERGE_RaD" , "CONVERGE_RaD"   , 3));
			 CONVERGE_RaD = lane.addElement(new Gateway( GatewayType.Inclusive , "CONVERGE_RaD" , "CONVERGE_RaD"   , 3));
	     QName end = lane.addElement(new Event  ( EventType.End, "END" , "END" , 3));
	     QName act5 = lane.addElementWithShift(new Activity( ActivityType.SendTask , "Stop" , "Stop"  , 1) , 0.32);
	     QName end2 = lane.addElement(new Event  ( EventType.End, "END" , "END" , 1));
		 QName act6 = lane.addElementWithShift(new Activity( ActivityType.SendTask , "Decline" , "Decline"  , 2) , 0.1);
		 QName end3 = lane.addElement(new Event  ( EventType.End, "END" , "END" , 2));
	     QName gtw4 = lane.addElementWithShift(new Gateway( GatewayType.Exclusive , "Agree with arguments?" , "Agree with arguments?"  , 2) , 0.1);
	     QName act7 = lane.addElement(new Activity( ActivityType.ManualTask , "Evaluate Rejection" , "Evaluate Rejection"  , 2));
		 QName evt2 = lane.addElement(new Event  ( EventType.IntermediateMessageCatchEvent, "Reject received" , "Reject received" , 2));
	    

		
		 
		 semantified_elements.add( new SemantifiedElement(strt , "INITIAL" , true)); 
	     
	     semantified_elements.add( new SemantifiedElement(act1 , "Promise Decision"));
	     
	     if  ( (view.getTKStepValue("Decline").compareTo("") != 0) //&& //change to avoid declination path without positive
	    	 //  (view.getTKStepValue("Promise").compareTo("") != 0) 
	    		 )
	    	   semantified_elements.add( new SemantifiedElement(gtw1 , "ok to produce?" , true));
	     else  semantified_elements.add( new SemantifiedElement(gtw1 , "ok to produce?" ));
		 
		 semantified_elements.add( new SemantifiedElement(act2 , "Promise"));
		 
		 if (RaP)
		 {
	 		 semantified_elements.add( new SemantifiedElement(DIVERGE_RaP , "DIVERGE_RaP" , true));		
			 semantified_elements.add( new SemantifiedElement(CONVERGE_RaP , "CONVERGE_RaP" , true));
		 } 		
	     semantified_elements.add( new SemantifiedElement(act3 , "Execute"));
	     if (RaE)
	     {		
			 semantified_elements.add( new SemantifiedElement(DIVERGE_RaE , "DIVERGE_RaE" , true));
			 semantified_elements.add( new SemantifiedElement(CONVERGE_RaE , "CONVERGE_RaE" , true));
	     }
	    
	     if (  (view.getTKStepValue("Reject").compareTo("") != 0) ||
	    	   (view.getTKStepValue("Evaluate Rejection").compareTo("") != 0)  )	    	 
	    	 semantified_elements.add( new SemantifiedElement(gtw2 , "converging" , true));
	     else semantified_elements.add( new SemantifiedElement(gtw2 , "converging"));

		 semantified_elements.add( new SemantifiedElement(act4 , "Declare" ));
		 
		 if (RaD) semantified_elements.add( new SemantifiedElement(DIVERGE_RaD , "DIVERGE_RaD" , true));

		 if ( (view.getTKStepValue("Reject").compareTo("") != 0) // && //change to avoid declination path without positive
		      //(view.getTKStepValue("Accept").compareTo("") != 0) 
		      )
			 semantified_elements.add( new SemantifiedElement(gtw3 , "Wait for Declare result" ,true ));
		 else semantified_elements.add( new SemantifiedElement(gtw3 , "Wait for Declare result"  ) );
			 
			 

		 if (view.getTKStepValue("Accept").compareTo("") != 0) semantified_elements.add( new SemantifiedElement(evt1 , "Accept received" , true));
		 else semantified_elements.add( new SemantifiedElement(evt1 , "Accept received"));

		 if (RaD) semantified_elements.add( new SemantifiedElement(CONVERGE_RaD , "CONVERGE_RaD" , true));
		 
	     
	     semantified_elements.add( new SemantifiedElement(end , "END" , true));
	    

	    semantified_elements.add( new SemantifiedElement(act5 , "Stop" ));
	    if (view.getTKStepValue("Stop").compareTo("") == 0  )
	    {
	    	semantified_elements.add( new SemantifiedElement(end2 , "END"));
	    	semantified_elements.add( new SemantifiedElement(gtw4 , "Agree with arguments?"));
	    }
	    else 
	    {
	    	semantified_elements.add( new SemantifiedElement(end2 , "END" , true));
	    	semantified_elements.add( new SemantifiedElement(gtw4 , "Agree with arguments?" , true));
	    }
	    
	    semantified_elements.add( new SemantifiedElement(act6 , "Decline" ));
	    
	    if (view.getTKStepValue("Decline").compareTo("") == 0  ) semantified_elements.add( new SemantifiedElement(end3 , "END" ));
	    else semantified_elements.add( new SemantifiedElement(end3 , "END" ,true ));

	    semantified_elements.add( new SemantifiedElement(act7 , "Evaluate Rejection"));

		if (view.getTKStepValue("Reject").compareTo("") != 0) semantified_elements.add( new SemantifiedElement(evt2 , "Reject received" , true));
		else semantified_elements.add( new SemantifiedElement(evt2 , "Reject received" ));

		

		
	    // add all the flows to semantified elements
		AddFlow2SemantifiedElements(semantified_elements , strt , act1);
		AddFlow2SemantifiedElements(semantified_elements , act1 , gtw1);
		AddFlow2SemantifiedElements(semantified_elements , gtw1 , act6);
		AddFlow2SemantifiedElements(semantified_elements , act6 , end3);
		AddFlow2SemantifiedElements(semantified_elements , gtw1 , act2);
        if (RaP)
	    {
        	AddFlow2SemantifiedElements(semantified_elements , act2 , DIVERGE_RaP);
        	AddFlow2SemantifiedElements(semantified_elements , DIVERGE_RaP , CONVERGE_RaP);
        	AddFlow2SemantifiedElements(semantified_elements , CONVERGE_RaP , act3);
	    }
        else AddFlow2SemantifiedElements(semantified_elements , act2 , act3);
  	    if (RaE)
	    {
  	    	AddFlow2SemantifiedElements(semantified_elements , act3 , DIVERGE_RaE);
  	    	AddFlow2SemantifiedElements(semantified_elements , DIVERGE_RaE , CONVERGE_RaE);
  	    	AddFlow2SemantifiedElements(semantified_elements , CONVERGE_RaE , gtw2);
	    }
  	    else AddFlow2SemantifiedElements(semantified_elements , act3 , gtw2);
  	    AddFlow2SemantifiedElements(semantified_elements , gtw2 , act4);
	    if (RaD)
		{
	    	AddFlow2SemantifiedElements(semantified_elements , act4 , DIVERGE_RaD);
	    	AddFlow2SemantifiedElements(semantified_elements , DIVERGE_RaD , gtw3);
	    	AddFlow2SemantifiedElements(semantified_elements , gtw3 , evt1);
	    	AddFlow2SemantifiedElements(semantified_elements , evt1 , CONVERGE_RaD);
	    	AddFlow2SemantifiedElements(semantified_elements , CONVERGE_RaD,end);
		}
		else
		{
			AddFlow2SemantifiedElements(semantified_elements , act4 , gtw3);
			AddFlow2SemantifiedElements(semantified_elements , gtw3 , evt1);
			AddFlow2SemantifiedElements(semantified_elements , evt1 , end);
		}
	    AddFlow2SemantifiedElements(semantified_elements , gtw3 , evt2);
	    AddFlow2SemantifiedElements(semantified_elements , evt2 , act7);
	    AddFlow2SemantifiedElements(semantified_elements , act7 , gtw4);
	    AddFlow2SemantifiedElements(semantified_elements , gtw4 , gtw2);
	    AddFlow2SemantifiedElements(semantified_elements , gtw4 , act5);
	    AddFlow2SemantifiedElements(semantified_elements , act5 , end2);
		

	    
	    
	    
		// Provision the configured elements at CustomView in semantified_elements with boolean		
		Set<String> keys = view.getCustomViewDetail().keySet();
		Iterator<String> it = keys.iterator();
		String chave;
		for (int idx = 0 ; idx < keys.size() ;idx++)
		{
			chave =  (String) it.next();	
			// change boolean of chave in semantified elements
			for (int elem = 0 ; elem < semantified_elements.size() ; elem++)
			{
				if ( semantified_elements.get(elem).getTKElementName().compareTo(chave) == 0 &&	 
					  view.getCustomViewDetail().get(chave).compareTo("")!= 0 )
					semantified_elements.get(elem).setToConsider(true);				
			}
			
			// update name in lane
			for (int elem = 0 ; elem < lane.getBPMNElements().size() ; elem++)
			{
				if (lane.getBPMNElements().get(elem).getName().compareTo(chave) == 0)
					lane.getBPMNElements().get(elem).setName( view.getTKStepValue(chave)  );
			}
		}	
				
		
		// remove elements from lane that are not provisioned in the Custom view
		for (SemantifiedElement semElem:semantified_elements )
			if (semElem.isToConsider() == false) lane.removeElement(semElem.getSemantified_element());
		
		
		System.out.println("Semantified Elements: " + semantified_elements.toString());

		
		// Add flow to lane considering only the provisioned - Cycle
		for (SemantifiedElement semElemSource: semantified_elements )
		{
			if ( semElemSource.isToConsider() )
			{
				for (int NConnection = 0 ; NConnection < semElemSource.getReferenced_semantified_elements().size() ; NConnection++ )
				{
					int targetIdx = semElemSource.GetReferenced_semantified_element(NConnection).intValue();
					int lastconsidered = targetIdx;
					boolean secondtry = false;
					
					while ( targetIdx < semantified_elements.size())
					{						
						SemantifiedElement semElemTarget = semantified_elements.get(targetIdx); 
						if ( semElemTarget.isToConsider()  )
						{
							lane.addSequenceFlow(new BPMNSequenceFlow( semElemSource.getSemantified_element() , 
																	   semElemTarget.getSemantified_element() )	);
							lastconsidered = targetIdx;
							targetIdx = semantified_elements.size();
							System.out.println("Flow added from: " + semElemSource.toString() + " to: " + semElemTarget.toString());
						}					
						else
						{
							System.out.println("Choosing target: " + semElemTarget.toString());
							if ( secondtry == false && semElemTarget.getReferenced_semantified_elements().size() == 0 ) 
							{
								//dead end -> end of search using first path
								targetIdx = lastconsidered; // go back
								secondtry = true; // and force the second path solution
							}
							else 
							{
								if ( secondtry && ( semElemTarget.getReferenced_semantified_elements().size() > 1) )
								{
										targetIdx = semElemTarget.GetReferenced_semantified_element(1);	// trying second path
										secondtry = false;
								}
								else targetIdx = semElemTarget.GetReferenced_semantified_element(0);	// trying first path					
							}
						}
					}					
				}				
			}			
		}

	    if ( CheckMessageFlow(MessageFlows , tk) == true ) // message flow exists
	    {		
	    	if ( view.getTKStepValue("Request").compareTo("") != 0) updateMessageFlow(MessageFlows, tk, strt, "request (C-act)" );
	    	if ( view.getTKStepValue("Accept").compareTo("") != 0) updateMessageFlow(MessageFlows, tk, evt1, "accept (C-act)" );
	    	if ( view.getTKStepValue("Reject").compareTo("") != 0) updateMessageFlow(MessageFlows, tk, evt2, "reject (C-act)" );	
	    	if ( view.getTKStepValue("Promise").compareTo("") != 0) updateMessageFlow(MessageFlows,tk, act2 ,"promise (C-act)");
	    	if ( view.getTKStepValue("Decline").compareTo("") != 0) updateMessageFlow(MessageFlows,tk, act6 ,"decline (C-act)");
	    	if ( view.getTKStepValue("Declare").compareTo("") != 0) updateMessageFlow(MessageFlows,tk, act4 ,"declare (C-act)" );
	    	if ( view.getTKStepValue("Stop").compareTo("") != 0) updateMessageFlow(MessageFlows,tk, act5 ,"stop (C-act)" );
	    }
	    else
	    {
	    	if ( view.getTKStepValue("Request").compareTo("") != 0) MessageFlows.add(new BPMNMessageFlow(tk, strt, "request (C-act)", false) );
	    	if ( view.getTKStepValue("Accept").compareTo("") != 0) MessageFlows.add(new BPMNMessageFlow(tk, evt1, "accept (C-act)",  false) );
	    	if ( view.getTKStepValue("Reject").compareTo("") != 0) MessageFlows.add(new BPMNMessageFlow(tk, evt2, "reject (C-act)",  false) );		
	    	if ( view.getTKStepValue("Promise").compareTo("") != 0) MessageFlows.add(new BPMNMessageFlow(tk, act2 ,"promise (C-act)", true) );
	    	if ( view.getTKStepValue("Decline").compareTo("") != 0) MessageFlows.add(new BPMNMessageFlow(tk, act6 ,"decline (C-act)", true) );
	    	if ( view.getTKStepValue("Declare").compareTo("") != 0) MessageFlows.add(new BPMNMessageFlow(tk, act4 ,"declare (C-act)", true) );
	    	if ( view.getTKStepValue("Stop").compareTo("") != 0) MessageFlows.add(new BPMNMessageFlow(tk, act5 ,"stop (C-act)", true) );
	    }
		
		lane = SpecifyIncoming_Outgoing(lane);
		  
		return(lane);
   	}
}
