package SemantifyingBPMN;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

import javax.xml.namespace.QName;

public class DEMOPatternCustomInitiator 
extends DEMOPattern{

	
	public Lane CreateElements_and_Sequence(Lane lane , TransactionKind tk, ArrayList<BPMNMessageFlow> MessageFlows , ArrayList<String> deps , PatternView view, boolean isFirst) {

//define elements
//		0   INITIAL
//		1   Request Decision
//		2   converging gateway
//		3   Request 
//		4   Wait for Request result
//		5   Promise received
//		6   converging gateway
//		7    Check product
//		8	 Is product ok?
//		9	 Accept
//		10	 END
//		11	 Reject 
//		12	 END
//		13	 Make new request?
//		14	 Decide what to do next
//		15	 Decline received
//		16	 Wait for Reject result
//		17	 Declare received
//		18	 Stop received
//		19	 END
//
//    from command line
// 		 Request Decision ; Request ; Promise Decision ; Promise ; Decline ; After Decline Decision ; Execute ; Declare  ; Decision Accept ; Accept ; Reject ; Evaluate Rejection ; Agree Rejection					

		
		ArrayList<SemantifiedElement> semantified_elements = new ArrayList<SemantifiedElement>();

		// Provision the full pattern with declinations and rejections in semantified_elements - BPMN elements and all flow
		QName strt;
		if (isFirst) strt = lane.addElement(new Event  ( EventType.Start, "INITIAL" , "INITIAL" , 2));
		else strt = lane.addElement(new Event  ( EventType.IntermediateCatchEvent, "INITIAL" , "INITIAL" , 2));
		
	    //QName strt = lane.addElement(new Event  ( EventType.Start, "INITIAL" , "INITIAL" , 2));
		
	    QName act1 = lane.addElement(new Activity  ( ActivityType.ManualTask, "Request Decision" , "Request Decision" , 2));
	    QName gtw1 = lane.addElement(new Gateway( GatewayType.Exclusive , "converging gateway" , "converging gateway"  , 2));
		QName act2 = lane.addElement(new Activity( ActivityType.SendTask , "Request" , "Request"  , 2));
		QName gtw2 = lane.addElement(new Gateway( GatewayType.Eventbased, "Wait for Request result" , "Wait for Request result" , 2));
		QName evt1 = lane.addElement(new Event  ( EventType.IntermediateMessageCatchEvent, "Promise received" , "Promise received" , 2));
	    QName gtw3 = lane.addElement(new Gateway( GatewayType.Exclusive , "converging gateway" , "converging gateway"  , 2));
	    QName act3 = lane.addElement(new Activity  ( ActivityType.ManualTask, "Decision Accept" , "Decision Accept" , 2));
	    QName gtw4 = lane.addElement(new Gateway( GatewayType.Exclusive , "Is product ok?" , "Is product ok?"  , 2));
		QName act4 = lane.addElement(new Activity( ActivityType.SendTask , "Accept" , "Accept"  , 2));
		QName act5 = lane.addElementWithShift(new Activity( ActivityType.SendTask , "Reject" , "Reject"  , 1) , 0.45);
	    QName end2 = lane.addElementWithShift(new Event  ( EventType.End, "END" , "END" , 5),0.05);
	    QName gtw5 = lane.addElement(new Gateway( GatewayType.Exclusive , "Make new request?" , "Make new request?"  , 5));
	    QName act6 = lane.addElement(new Activity  ( ActivityType.ManualTask, "After Decline Decision" , "After Decline Decision" , 5));
		QName evt2 = lane.addElement(new Event  ( EventType.IntermediateMessageCatchEvent, "Decline received" , "Decline received" , 5));
		QName gtw6 = lane.addElementWithShift(new Gateway( GatewayType.Eventbased, "Wait for Reject result" , "Wait for Reject result" , 3) , 0.4);
		QName evt3 = lane.addElement(new Event  ( EventType.IntermediateMessageCatchEvent, "Declare received" , "Declare received" , 3));
		QName evt4 = lane.addElementWithShift(new Event  ( EventType.IntermediateMessageCatchEvent, "Stop received" , "Stop received" , 4),0.4);
		QName end3;
	    //if (isFirst) 
		end3 = lane.addElement(new Event  ( EventType.End, "END" , "END" , 4));
	    
		semantified_elements.add( new SemantifiedElement( strt , "INITIAL" , true)  ); 
		semantified_elements.add( new SemantifiedElement( act1 , "Request Decision")  );
		
		if (	 view.getTKStepValue("Decline").compareTo("")  == 0 ) 	
			semantified_elements.add( new SemantifiedElement( gtw1 , "converging gateway")  );
		else semantified_elements.add( new SemantifiedElement( gtw1 , "converging gateway", true)  );			
			
		semantified_elements.add( new SemantifiedElement( act2 , "Request")  ); 
		
		if ( (view.getTKStepValue("Decline").compareTo("")  != 0)  )
			semantified_elements.add( new SemantifiedElement( gtw2 , "Wait for Request result" , true)  ); 
		else semantified_elements.add( new SemantifiedElement( gtw2 , "Wait for Request result")  ); 
		
		if (view.getTKStepValue("Promise").compareTo("") != 0) semantified_elements.add( new SemantifiedElement( evt1 , "Promise received" , true)  ); //5
		else semantified_elements.add( new SemantifiedElement( evt1 , "Promise received")  ); 
		
		if ( view.getTKStepValue("Reject").compareTo("") == 0 )
		semantified_elements.add( new SemantifiedElement( gtw3 , "converging gateway")  );
		else semantified_elements.add( new SemantifiedElement( gtw3 , "converging gateway" , true)  );
		
		semantified_elements.add( new SemantifiedElement( act3 , "Decision Accept")  ); 
		
		if ( (view.getTKStepValue("Reject").compareTo("") != 0) )
			semantified_elements.add( new SemantifiedElement( gtw4 , "Is product ok?"  , true)  );
		else semantified_elements.add( new SemantifiedElement( gtw4 , "Is product ok?"  )  );
		 
		QName end1 = null;
		if (isFirst)	
		    end1 = lane.addElement(new Event  ( EventType.End, "END" , "END" , 2));			
		else end1 = lane.addElement(new Event  ( EventType.IntermediateCatchEvent, "Intermediate_Accept" , "Intermediate_Accept" , 2));
		
		semantified_elements.add( new SemantifiedElement( end1 , "END" , true)  );
		semantified_elements.add( new SemantifiedElement( act4 , "Accept")  );
		semantified_elements.add( new SemantifiedElement( act5 , "Reject")  );

		if (view.getTKStepValue("Decline").compareTo("") != 0) 
		{
			semantified_elements.add( new SemantifiedElement( act6 , "After Decline Decision")  ); 	
			semantified_elements.add( new SemantifiedElement( evt2 , "Decline received" , true)  );
			semantified_elements.add( new SemantifiedElement( gtw5 , "Make new request?" , true)  );
			semantified_elements.add( new SemantifiedElement( end2 , "END" , true)  );
		}
		else 
		{	
			semantified_elements.add( new SemantifiedElement( evt2 , "Decline received")  );
			semantified_elements.add( new SemantifiedElement( act6 , "After Decline Decision")  ); 	
			semantified_elements.add( new SemantifiedElement( gtw5 , "Make new request?")  );
			semantified_elements.add( new SemantifiedElement( end2 , "END" )  );
		}
		
		if ( (view.getTKStepValue("Stop").compareTo("") != 0) //&& check comment in DEMOPAtternCustomExecutor  
			 //(view.getTKStepValue("Declare").compareTo("") != 0)  	)
				)
			semantified_elements.add( new SemantifiedElement( gtw6 , "Wait for Reject result" , true)  );
		else semantified_elements.add( new SemantifiedElement( gtw6 , "Wait for Reject result" )  ); 
		
		if (view.getTKStepValue("Declare").compareTo("") != 0) 
		{
			semantified_elements.add( new SemantifiedElement( evt3 , "Declare received" , true)  ); 
		}
		else 
		{
			semantified_elements.add( new SemantifiedElement( evt3 , "Declare received" )  ); 
		}
		
		if (view.getTKStepValue("Stop").compareTo("") != 0) 
		{
			semantified_elements.add( new SemantifiedElement( evt4 , "Stop received", true)  );
			semantified_elements.add( new SemantifiedElement( end3 , "END" , true)  );
		}
		else 
		{
			semantified_elements.add( new SemantifiedElement( evt4 , "Stop received")  );
			semantified_elements.add( new SemantifiedElement( end3 , "END")  );
		}
		
		     // add all the flows to semantified elements
		AddFlow2SemantifiedElements(semantified_elements ,strt , act1);
		AddFlow2SemantifiedElements(semantified_elements ,act1 , gtw1);
		AddFlow2SemantifiedElements(semantified_elements ,gtw1 , act2);
		AddFlow2SemantifiedElements(semantified_elements ,act2 , gtw2);
		AddFlow2SemantifiedElements(semantified_elements ,gtw2 , evt1);
		AddFlow2SemantifiedElements(semantified_elements ,evt1 , gtw3);
		AddFlow2SemantifiedElements(semantified_elements ,gtw3 , gtw6);
		AddFlow2SemantifiedElements(semantified_elements ,gtw6 , evt3);
		AddFlow2SemantifiedElements(semantified_elements ,gtw6 , evt4);
		AddFlow2SemantifiedElements(semantified_elements ,evt4 , end3);
		AddFlow2SemantifiedElements(semantified_elements ,evt3 , act3);
		AddFlow2SemantifiedElements(semantified_elements ,act3 , gtw4);
		AddFlow2SemantifiedElements(semantified_elements ,gtw4 , act4);
		AddFlow2SemantifiedElements(semantified_elements ,gtw4 , act5);
		//if (isFirst) 
		AddFlow2SemantifiedElements(semantified_elements ,act4 , end1);
		AddFlow2SemantifiedElements(semantified_elements ,act5 , gtw3);
		AddFlow2SemantifiedElements(semantified_elements ,gtw2 , evt2);	    
		AddFlow2SemantifiedElements(semantified_elements ,evt2 , act6);
		AddFlow2SemantifiedElements(semantified_elements ,act6 , gtw5);
		AddFlow2SemantifiedElements(semantified_elements ,gtw5 , gtw1);
		AddFlow2SemantifiedElements(semantified_elements ,gtw5 , end2);
		
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
		
		
	//	System.out.println("Semantified Elements: " + semantified_elements.toString());

		
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
							//System.out.println("Flow added from: " + semElemSource.toString() + " to: " + semElemTarget.toString());
						}					
						else
						{
							//System.out.println("Choosing target: " + semElemTarget.toString());
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
								else if ( semElemTarget.getReferenced_semantified_elements().size() > 0   )
									targetIdx = semElemTarget.GetReferenced_semantified_element(0);	// trying first path					
							}
						}
					}					
				}				
			}			
		}

		
	    if ( CheckMessageFlow(MessageFlows , tk) == false ) //no message flow exists
	    {	    	
	    	if ( view.getTKStepValue("Request").compareTo("") != 0) MessageFlows.add(new BPMNMessageFlow(tk, act2, "request (C-act)", true) );
	    	if ( view.getTKStepValue("Accept").compareTo("") != 0) MessageFlows.add(new BPMNMessageFlow(tk, act4, "accept (C-act)",  true) );
	    	if ( view.getTKStepValue("Reject").compareTo("") != 0) MessageFlows.add(new BPMNMessageFlow(tk, act5, "reject (C-act)",  true) );
	    	if ( view.getTKStepValue("Promise").compareTo("") != 0) MessageFlows.add(new BPMNMessageFlow(tk, evt1 ,"promise (C-act)", false) );
	    	if ( view.getTKStepValue("Decline").compareTo("") != 0) MessageFlows.add(new BPMNMessageFlow(tk, evt2 ,"decline (C-act)", false) );
	    	if ( view.getTKStepValue("Declare").compareTo("") != 0) MessageFlows.add(new BPMNMessageFlow(tk, evt3 ,"declare (C-act)", false) );
	    	if ( view.getTKStepValue("Stop").compareTo("") != 0) MessageFlows.add(new BPMNMessageFlow(tk, evt4 ,"stop (C-act)", false) );
	    }
	    else
	    {
	    	if ( view.getTKStepValue("Request").compareTo("") != 0) updateMessageFlow(MessageFlows, tk, act2, "request (C-act)" );
	    	if ( view.getTKStepValue("Accept").compareTo("") != 0) updateMessageFlow(MessageFlows, tk, act4, "accept (C-act)" );
	    	if ( view.getTKStepValue("Reject").compareTo("") != 0) updateMessageFlow(MessageFlows, tk, act5, "reject (C-act)" );			
	    	if ( view.getTKStepValue("Promise").compareTo("") != 0) updateMessageFlow(MessageFlows,tk, evt1 ,"promise (C-act)");
	    	if ( view.getTKStepValue("Decline").compareTo("") != 0) updateMessageFlow(MessageFlows,tk, evt2 ,"decline (C-act)");
	    	if ( view.getTKStepValue("Declare").compareTo("") != 0) updateMessageFlow(MessageFlows,tk, evt3 ,"declare (C-act)" );
	    	if ( view.getTKStepValue("Stop").compareTo("") != 0) updateMessageFlow(MessageFlows,tk, evt4 ,"stop (C-act)" );
	    }	    
	    
		lane = SpecifyIncoming_Outgoing(lane);
		
		return(lane);
 		  
   	}


}
