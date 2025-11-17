package SemantifyingBPMN;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

import javax.xml.namespace.QName;

public class DEMOPatternCustomInitiatorSimplified 
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
		else strt = lane.addElement(new Event  ( EventType.IntermediateCatchTimerEvent, "INITIAL" , "INITIAL" , 2) );	    
	    QName act1 = lane.addElement(new Activity  ( ActivityType.UserTask, "Request Decision" , "Request Decision" , 2));
	    QName gtw1 = lane.addElement(new Gateway( GatewayType.Exclusive , "converging gateway" , "converging gateway"  , 2));
		QName act2 = lane.addElement(new Activity( ActivityType.UserTask , "Request" , "Request"  , 2));
		QName act5 = lane.addElementWithShift(new Activity( ActivityType.UserTask , "Reject" , "Reject"  , 2) , 0.55);		
		QName end1 = lane.addElementWithShift(new Event  ( EventType.End, "END" , "END" , 1) , 0.1);	
	    QName gtw5 = lane.addElement(new Gateway( GatewayType.Exclusive , "Make adapted request?" , "Make adapted request?"  , 1));
	    QName act6 = lane.addElementWithShift(new Activity  ( ActivityType.UserTask, "After Decline Decision" , "After Decline Decision" , 1), 0.05);
	    
	    QName act3 = null;
	    if (view.getTKStepValue("After Decline Decision").compareTo("") != 0 )
	    	act3 = lane.addElementWithShift(new Activity  ( ActivityType.UserTask, "Decision Accept" , "Decision Accept" , 1) , 0.3);
	    else act3 = lane.addElementWithShift(new Activity  ( ActivityType.UserTask, "Decision Accept" , "Decision Accept" , 1) , 0.6);
	    
	    
	    QName gtw4 = lane.addElement(new Gateway( GatewayType.Exclusive , "Is product OK?" , "Is product OK?"  , 1));
	    
	    QName act4 = null;
	    if ( view.getTKStepValue("After Decline Decision").compareTo("") != 0 && 
	    	 view.getTKStepValue("Decision Accept").compareTo("") != 0	)
		    act4 = lane.addElement(new Activity( ActivityType.UserTask , "Accept" , "Accept"  , 1));
	    else act4 = lane.addElementWithShift(new Activity( ActivityType.UserTask , "Accept" , "Accept"  , 1 ) , 0.4 );
	    
		QName end3 = null;
	    if (isFirst) end3 = lane.addElement(new Event  ( EventType.End, "END" , "END" , 1));
		else end3 = lane.addElement(new Event  ( EventType.IntermediateCatchEvent, "Intermediate_Accept" , "Intermediate_Accept" , 1));

		semantified_elements.add( new SemantifiedElement( strt , "INITIAL" , true)  ); 
		semantified_elements.add( new SemantifiedElement( act1 , "Request Decision")  );
		semantified_elements.add( new SemantifiedElement( act2 , "Request")  ); 				
		semantified_elements.add( new SemantifiedElement( act3 , "Decision Accept")  ); 
		semantified_elements.add( new SemantifiedElement( act4 , "Accept")  );
		semantified_elements.add( new SemantifiedElement( act5 , "Reject")  );
		
		if ( view.getTKStepValue("Decline").compareTo("")  == 0 && 
			 view.getTKStepValue("After Decline Decision").compareTo("")  == 0)
		{
			semantified_elements.add( new SemantifiedElement( gtw1 , "converging gateway"  ) );
			semantified_elements.add( new SemantifiedElement( act6 , "After Decline Decision")  ); 	
			semantified_elements.add( new SemantifiedElement( gtw5 , "Make adapted request?"  ) );
			semantified_elements.add( new SemantifiedElement( end1 , "END")  );
		}
		else
		{
			semantified_elements.add( new SemantifiedElement( gtw1 , "converging gateway" , true)  );
			semantified_elements.add( new SemantifiedElement( act6 , "After Decline Decision")  ); 			
			semantified_elements.add( new SemantifiedElement( gtw5 , "Make adapted request?" , true)  );
			semantified_elements.add( new SemantifiedElement( end1 , "END" , true)  );
		}			
		
		
		if ( view.getTKStepValue("Reject").compareTo("") == 0 )
			semantified_elements.add( new SemantifiedElement( gtw4 , "Is product OK?"  )  );
		else semantified_elements.add( new SemantifiedElement( gtw4 , "Is product OK?" , true )  );

		//if (isFirst) 
		semantified_elements.add( new SemantifiedElement( end3 , "END" , true )  );
		
	     // add all the flows to semantified elements		
		AddFlow2SemantifiedElements(semantified_elements , strt , act1);		
		AddFlow2SemantifiedElements(semantified_elements , act1 , gtw1);
		AddFlow2SemantifiedElements(semantified_elements , gtw1 , act2);	    
		AddFlow2SemantifiedElements(semantified_elements , act6 , gtw5);
		AddFlow2SemantifiedElements(semantified_elements , gtw5 , end1);
		AddFlow2SemantifiedElements(semantified_elements , gtw5 , gtw1);	    
		AddFlow2SemantifiedElements(semantified_elements , act3 , gtw4);
		AddFlow2SemantifiedElements(semantified_elements , gtw4 , act4);
		AddFlow2SemantifiedElements(semantified_elements , gtw4 , act5);
		//if (isFirst) 
		AddFlow2SemantifiedElements(semantified_elements , act4 , end3);
		
		
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
							//System.out.println("Choosing target: " + semElemTarget.toString() + "secondtry = " + secondtry + " Semantified Elements: " + semantified_elements.toString());
							
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
									//System.out.println(semElemTarget.toString() + ": seecond try and > 1 on Initiator");
									targetIdx = semElemTarget.GetReferenced_semantified_element(1);	// trying second path
									secondtry = false;
								}
								else if ( semElemTarget.CheckReferenced_semantified_element(0) )
								{
									//System.out.println(" (seecond try and > 1) fail on Initiator");
									targetIdx = semElemTarget.GetReferenced_semantified_element(0);	// trying first path
								}
								else 
								{
									//targetIdx = semantified_elements.size();
									break; // give-up													
								}
							}
						}
					}					
				}				
			}			
		}

		 
		return(lane);
 		  
   	}


}
