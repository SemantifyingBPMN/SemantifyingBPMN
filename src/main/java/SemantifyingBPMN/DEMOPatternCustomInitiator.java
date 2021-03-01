package SemantifyingBPMN;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

import javax.xml.namespace.QName;

public class DEMOPatternCustomInitiator 
extends DEMOPattern{

	
	public Lane CreateElements_and_Sequence(Lane lane , TransactionKind tk, ArrayList<BPMNMessageFlow> MessageFlows , ArrayList<String> deps , PatternView view) {

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
	    QName strt = lane.addElement(new Event  ( EventType.Start, "INITIAL" , "INITIAL" , 2));
	    QName act1 = lane.addElement(new Activity  ( ActivityType.ManualTask, "Request Decision" , "Request Decision" , 2));
	    QName gtw1 = lane.addElement(new Gateway( GatewayType.Exclusive , "converging gateway" , "converging gateway"  , 2));
		QName act2 = lane.addElement(new Activity( ActivityType.SendTask , "Request" , "Request"  , 2));
		QName gtw2 = lane.addElement(new Gateway( GatewayType.Eventbased, "Wait for Request result" , "Wait for Request result" , 2));
		QName evt1 = lane.addElement(new Event  ( EventType.IntermediateMessageCatchEvent, "Promise received" , "Promise received" , 2));
	    QName gtw3 = lane.addElement(new Gateway( GatewayType.Exclusive , "converging gateway" , "converging gateway"  , 2));
	    QName act3 = lane.addElement(new Activity  ( ActivityType.ManualTask, "Decision Accept" , "Decision Accept" , 2));
	    QName gtw4 = lane.addElement(new Gateway( GatewayType.Exclusive , "Is product ok?" , "Is product ok?"  , 2));
		QName act4 = lane.addElement(new Activity( ActivityType.SendTask , "Accept" , "Accept"  , 2));
	    QName end1 = lane.addElement(new Event  ( EventType.End, "END" , "END" , 2));
		QName act5 = lane.addElementWithShift(new Activity( ActivityType.SendTask , "Reject" , "Reject"  , 1) , 0.45);
	    QName end2 = lane.addElementWithShift(new Event  ( EventType.End, "END" , "END" , 5),0.05);
	    QName gtw5 = lane.addElement(new Gateway( GatewayType.Exclusive , "Make new request?" , "Make new request?"  , 5));
	    QName act6 = lane.addElement(new Activity  ( ActivityType.ManualTask, "After Decline Decision" , "After Decline Decision" , 5));
		QName evt2 = lane.addElement(new Event  ( EventType.IntermediateMessageCatchEvent, "Decline received" , "Decline received" , 5));
		QName gtw6 = lane.addElementWithShift(new Gateway( GatewayType.Eventbased, "Wait for Reject result" , "Wait for Reject result" , 3) , 0.4);
		QName evt3 = lane.addElement(new Event  ( EventType.IntermediateMessageCatchEvent, "Declare received" , "Declare received" , 3));
		QName evt4 = lane.addElementWithShift(new Event  ( EventType.IntermediateMessageCatchEvent, "Stop received" , "Stop received" , 4),0.4);
	    QName end3 = lane.addElement(new Event  ( EventType.End, "END" , "END" , 4));
		
		semantified_elements.add( new SemantifiedElement( strt , "INITIAL" , true)  ); // 0
		semantified_elements.add( new SemantifiedElement( act1 , "Request Decision")  ); //1 
		semantified_elements.add( new SemantifiedElement( gtw1 , "converging gateway", true)  ); //2
		semantified_elements.add( new SemantifiedElement( act2 , "Request")  ); //3
		semantified_elements.add( new SemantifiedElement( gtw2 , "Wait for Request result", true)  ); //4
		if (view.getTKStepValue("Promise").compareTo("") != 0) semantified_elements.add( new SemantifiedElement( evt1 , "Promise received" , true)  ); //5
		else semantified_elements.add( new SemantifiedElement( evt1 , "Promise received")  ); //5
		semantified_elements.add( new SemantifiedElement( gtw3 , "converging gateway" , true)  );//6
		semantified_elements.add( new SemantifiedElement( act3 , "Decision Accept")  ); //7
		semantified_elements.add( new SemantifiedElement( gtw4 , "Is product ok?" , true)  ); //8
		semantified_elements.add( new SemantifiedElement( act4 , "Accept")  );//9
		semantified_elements.add( new SemantifiedElement( end1 , "END" , true)  ); //10
		semantified_elements.add( new SemantifiedElement( act5 , "Reject")  );//11
		semantified_elements.add( new SemantifiedElement( end2 , "END" , true)  );//12
		semantified_elements.add( new SemantifiedElement( gtw5 , "Make new request?" , true)  ); //13
		semantified_elements.add( new SemantifiedElement( act6 , "After Decline Decision")  ); //14
		if (view.getTKStepValue("Decline").compareTo("") != 0) semantified_elements.add( new SemantifiedElement( evt2 , "Decline received" , true)  ); //15
		else semantified_elements.add( new SemantifiedElement( evt2 , "Decline received")  ); //15
		semantified_elements.add( new SemantifiedElement( gtw6 , "Wait for Reject result" , true)  ); //16
		if (view.getTKStepValue("Declare").compareTo("") != 0) semantified_elements.add( new SemantifiedElement( evt3 , "Declare received" , true)  ); //17
		else semantified_elements.add( new SemantifiedElement( evt3 , "Declare received" )  ); //17
		if (view.getTKStepValue("Stop").compareTo("") != 0) semantified_elements.add( new SemantifiedElement( evt4 , "Stop received", true)  );// 18
		else semantified_elements.add( new SemantifiedElement( evt4 , "Stop received")  );// 18
		semantified_elements.add( new SemantifiedElement( end3 , "END" , true)  );//19
		
		     // add all the flows to semantified elements
		semantified_elements.get(0).AddReferenced_semantified_element(1);
		semantified_elements.get(1).AddReferenced_semantified_element(2);
		semantified_elements.get(2).AddReferenced_semantified_element(3);
		semantified_elements.get(3).AddReferenced_semantified_element(4);
		semantified_elements.get(4).AddReferenced_semantified_element(5);
		semantified_elements.get(5).AddReferenced_semantified_element(6);
		semantified_elements.get(6).AddReferenced_semantified_element(16);
		semantified_elements.get(16).AddReferenced_semantified_element(17);
		semantified_elements.get(16).AddReferenced_semantified_element(18);
		semantified_elements.get(18).AddReferenced_semantified_element(19);
		semantified_elements.get(17).AddReferenced_semantified_element(7);
		semantified_elements.get(7).AddReferenced_semantified_element(8);
		semantified_elements.get(8).AddReferenced_semantified_element(9);
		semantified_elements.get(8).AddReferenced_semantified_element(11);
		semantified_elements.get(9).AddReferenced_semantified_element(10);
		semantified_elements.get(11).AddReferenced_semantified_element(6);
		semantified_elements.get(4).AddReferenced_semantified_element(15);	    
		semantified_elements.get(15).AddReferenced_semantified_element(14);
		semantified_elements.get(14).AddReferenced_semantified_element(13);
		semantified_elements.get(13).AddReferenced_semantified_element(2);
		semantified_elements.get(13).AddReferenced_semantified_element(12);
		
		
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
				
		// remove the useless gateways from lane

		
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
					
					while ( targetIdx < semantified_elements.size())
					{						
						SemantifiedElement semElemTarget = semantified_elements.get(targetIdx); 
						if ( semElemTarget.isToConsider()  )
						{
							lane.addSequenceFlow(new BPMNSequenceFlow( semElemSource.getSemantified_element() , 
																	   semElemTarget.getSemantified_element() )	);							
							targetIdx = semantified_elements.size();
							System.out.println("Flow added from: " + semElemSource.toString() + " to: " + semElemTarget.toString());
						}					
						else targetIdx = semElemTarget.GetReferenced_semantified_element(0);	// only consider one path - enough because gateways exist....					
					}					
				}				
			}			
		}

		
	    if ( CheckMessageFlow(MessageFlows , tk) == false ) //no message flow exists
	    {	    	
	    	if ( view.getTKStepValue("Request").compareTo("") != 0) MessageFlows.add(new BPMNMessageFlow(tk, semantified_elements.get(3).getSemantified_element(), "request (C-act)", true) );
	    	if ( view.getTKStepValue("Accept").compareTo("") != 0) MessageFlows.add(new BPMNMessageFlow(tk, semantified_elements.get(9).getSemantified_element(), "accept (C-act)",  true) );
	    	if ( view.getTKStepValue("Reject").compareTo("") != 0) MessageFlows.add(new BPMNMessageFlow(tk,semantified_elements.get(11).getSemantified_element(), "reject (C-act)",  true) );
	    	if ( view.getTKStepValue("Promise").compareTo("") != 0) MessageFlows.add(new BPMNMessageFlow(tk, semantified_elements.get(5).getSemantified_element() ,"promise (C-act)", false) );
	    	if ( view.getTKStepValue("Decline").compareTo("") != 0) MessageFlows.add(new BPMNMessageFlow(tk, semantified_elements.get(15).getSemantified_element() ,"decline (C-act)", false) );
	    	if ( view.getTKStepValue("Declare").compareTo("") != 0) MessageFlows.add(new BPMNMessageFlow(tk, semantified_elements.get(17).getSemantified_element() ,"declare (C-act)", false) );
	    	if ( view.getTKStepValue("Stop").compareTo("") != 0) MessageFlows.add(new BPMNMessageFlow(tk, semantified_elements.get(18).getSemantified_element() ,"stop (C-act)", false) );
	    }
	    else
	    {
	    	if ( view.getTKStepValue("Request").compareTo("") != 0) updateMessageFlow(MessageFlows, tk, semantified_elements.get(3).getSemantified_element(), "request (C-act)" );
	    	if ( view.getTKStepValue("Accept").compareTo("") != 0) updateMessageFlow(MessageFlows, tk, semantified_elements.get(9).getSemantified_element(), "accept (C-act)" );
	    	if ( view.getTKStepValue("Reject").compareTo("") != 0) updateMessageFlow(MessageFlows, tk, semantified_elements.get(11).getSemantified_element(), "reject (C-act)" );			
	    	if ( view.getTKStepValue("Promise").compareTo("") != 0) updateMessageFlow(MessageFlows,tk, semantified_elements.get(5).getSemantified_element() ,"promise (C-act)");
	    	if ( view.getTKStepValue("Decline").compareTo("") != 0) updateMessageFlow(MessageFlows,tk, semantified_elements.get(15).getSemantified_element() ,"decline (C-act)");
	    	if ( view.getTKStepValue("Declare").compareTo("") != 0) updateMessageFlow(MessageFlows,tk, semantified_elements.get(17).getSemantified_element() ,"declare (C-act)" );
	    	if ( view.getTKStepValue("Stop").compareTo("") != 0) updateMessageFlow(MessageFlows,tk, semantified_elements.get(18).getSemantified_element() ,"stop (C-act)" );
	    }	    
	    
		lane = SpecifyIncoming_Outgoing(lane);
		
		return(lane);
 		  
   	}


}
