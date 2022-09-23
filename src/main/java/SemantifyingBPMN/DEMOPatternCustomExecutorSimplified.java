package SemantifyingBPMN;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

import javax.xml.namespace.QName;

public class DEMOPatternCustomExecutorSimplified 
extends DEMOPattern{

	
	public Lane CreateElements_and_Sequence(Lane lane , TransactionKind tk, ArrayList<BPMNMessageFlow> MessageFlows , ArrayList<String> deps , PatternView view, boolean isFirst) {
		
		 boolean RaP = CheckPreviousFixed(deps, new String("RaP"));  
		 boolean RaE = CheckPreviousFixed(deps, new String("RaE"));  
		 boolean RaD = CheckPreviousFixed(deps, new String("RaD"));
		  
		  
		 QName DIVERGE_RaP = null , CONVERGE_RaP = null,
			   DIVERGE_RaE = null , CONVERGE_RaE = null,
			   DIVERGE_RaD = null , CONVERGE_RaD = null;
		
		 ArrayList<SemantifiedElement> semantified_elements = new ArrayList<SemantifiedElement>();

			// Provision the full pattern with declinations and rejections in semantified_elements - BPMN elements and all flow
		 QName act1 = lane.addElementWithShift(new Activity ( ActivityType.UserTask, "Promise Decision" , "Promise Decision" , 2) , 0.2);
		 QName gtw1 = lane.addElement(new Gateway  ( GatewayType.Exclusive , "OK to produce?" , "OK to produce?"  , 2));
		 QName act2 = lane.addElement(new Activity( ActivityType.UserTask , "Promise" , "Promise"  , 2));
		 if (RaP)
		 {
	 		 DIVERGE_RaP = lane.addElement(new Gateway( GatewayType.Inclusive , "DIVERGE_RaP" , "DIVERGE_RaP"   , 2));
 			 CONVERGE_RaP = lane.addElement(new Gateway( GatewayType.Inclusive , "CONVERGE_RaP" , "CONVERGE_RaP"   , 2));
		 } 	
	     QName act3 = lane.addElement(new Activity  ( ActivityType.UserTask, "Execute" , "Execute" , 2));
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
			 act4 = lane.addElement(new Activity( ActivityType.UserTask , "Declare" , "Declare"  , 2));
			 DIVERGE_RaD = lane.addElement(new Gateway( GatewayType.Inclusive , "DIVERGE_RaD" , "DIVERGE_RaD"   , 2));
			 CONVERGE_RaD = lane.addElement(new Gateway( GatewayType.Inclusive , "CONVERGE_RaD" , "CONVERGE_RaD"   , 2));
		 }
		 QName gtw4 = lane.addElement(new Gateway( GatewayType.Exclusive , "Make declare adapted?" , "Make declare adapted?"  , 2));
		 QName act7 = lane.addElementWithShift(new Activity( ActivityType.UserTask , "Evaluate Rejection" , "Evaluate Rejection"  , 2),0.05); 		 
		 
		 QName act6 = lane.addElementWithShift(new Activity( ActivityType.UserTask , "Decline" , "Decline"  , 1) , 0.275);
		 
		 if (!RaD) 
		 {
			 if (view.getTKStepValue("Decline").compareTo("")  != 0)
				 act4 = lane.addElementWithShift(new Activity( ActivityType.UserTask , "Declare" , "Declare"  , 1) , 0.25);
			 else 
				 act4 = lane.addElementWithShift(new Activity( ActivityType.UserTask , "Declare" , "Declare"  , 1) , 0.55);
			 
			 act5 = lane.addElementWithShift(new Activity( ActivityType.UserTask , "Stop" , "Stop"  , 1),0.05);
		 }
		 else act5 = lane.addElementWithShift(new Activity( ActivityType.UserTask , "Stop" , "Stop"  , 1),0.35);		 
		  	    
	     QName end2 = lane.addElement(new Event  ( EventType.End, "END" , "END" , 1));

	     
	     
	     

	     semantified_elements.add( new SemantifiedElement(act1 , "Promise Decision"));
	     
	     /*if  ( (view.getTKStepValue("Decline").compareTo("") != 0) )
	    	   semantified_elements.add( new SemantifiedElement(gtw1 , "ok to produce?" , true));
	     else  */
	     
	     semantified_elements.add( new SemantifiedElement(gtw1 , "ok to produce?" ,true));
		 
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
	    
	     
	     if ( view.getTKStepValue("Reject").compareTo("")  == 0 && 
			 view.getTKStepValue("Evaluate Rejection").compareTo("")  == 0)
			{	    	 
	    	 	semantified_elements.add( new SemantifiedElement(gtw2 , "converging"));
	    	    semantified_elements.add( new SemantifiedElement(act7 , "Evaluate Rejection"));
			    semantified_elements.add( new SemantifiedElement(act5 , "Stop" ));
		    	semantified_elements.add( new SemantifiedElement(end2 , "END"));
		    	semantified_elements.add( new SemantifiedElement(gtw4 , "Make declare adapted?"));
			}
			else
			{
				semantified_elements.add( new SemantifiedElement(gtw2 , "converging" , true));
	    	    semantified_elements.add( new SemantifiedElement(act7 , "Evaluate Rejection"));
			    semantified_elements.add( new SemantifiedElement(act5 , "Stop" ));
		    	semantified_elements.add( new SemantifiedElement(end2 , "END", true));
		    	semantified_elements.add( new SemantifiedElement(gtw4 , "Make declare adapted?",true));

			}			
	     
		 semantified_elements.add( new SemantifiedElement(act4 , "Declare" ));
		 
		 if (RaD) 
		 {
			 semantified_elements.add( new SemantifiedElement(DIVERGE_RaD , "DIVERGE_RaD" , true));
			 semantified_elements.add( new SemantifiedElement(CONVERGE_RaD , "CONVERGE_RaD" , true));
		 } 
	    
	    
	    semantified_elements.add( new SemantifiedElement(act6 , "Decline" ));
	    


		/////////////////////////////////////////////////////////

		
	    // add all the flows to semantified elements
		
	    AddFlow2SemantifiedElements(semantified_elements , act1 , gtw1);
	    AddFlow2SemantifiedElements(semantified_elements , gtw1 , act2);	    
	    AddFlow2SemantifiedElements(semantified_elements , gtw1 , act6);	   
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
	    if (RaD)
		{
	    	AddFlow2SemantifiedElements(semantified_elements , act4 , DIVERGE_RaD);
	    	AddFlow2SemantifiedElements(semantified_elements , DIVERGE_RaD , CONVERGE_RaD);			
		}	    
	    AddFlow2SemantifiedElements(semantified_elements , gtw2 , act4);
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
								else if ( semElemTarget.CheckReferenced_semantified_element(0) )
									targetIdx = semElemTarget.GetReferenced_semantified_element(0);	// trying first path
								else break; // give-up													
							}
						}
					}					
				}				
			}			
		}

		  
		return(lane);
   	}

	
	public void connectwithInitiator(Lane Initiator, Lane Executor) {

		
		
   		ArrayList<String> PossibilidadesInitiatorCluster1 = new ArrayList<String>();
   		ArrayList<String> PossibilidadesInitiatorCluster2 = new ArrayList<String>();
   		
   		PossibilidadesInitiatorCluster1.add("INITIAL");
   		PossibilidadesInitiatorCluster1.add("Request Decision");
   		PossibilidadesInitiatorCluster1.add("converging gateway");
   		PossibilidadesInitiatorCluster1.add("Request");
		
   		PossibilidadesInitiatorCluster2.add("Decision Accept");
   		PossibilidadesInitiatorCluster2.add("Is product OK?");
   		PossibilidadesInitiatorCluster2.add("Accept");   		
   		PossibilidadesInitiatorCluster2.add("END");
   		
   		
   		ArrayList<String> PossibilidadesExecutorCluster = new ArrayList<String>();
   		PossibilidadesExecutorCluster.add("Promise Decision");
   		PossibilidadesExecutorCluster.add("OK to produce?");
   		PossibilidadesExecutorCluster.add("Promise");
   		PossibilidadesExecutorCluster.add("DIVERGE_RaP");
   		PossibilidadesExecutorCluster.add("CONVERGE_RaP");
   		PossibilidadesExecutorCluster.add("Execute");   		
   		PossibilidadesExecutorCluster.add("DIVERGE_RaE");
   		PossibilidadesExecutorCluster.add("CONVERGE_RaE");
   		PossibilidadesExecutorCluster.add("converging");
   		PossibilidadesExecutorCluster.add("Declare");
   		PossibilidadesExecutorCluster.add("DIVERGE_RaD");
   		PossibilidadesExecutorCluster.add("CONVERGE_RaD");  
   		
   		
   		// From initiator to Executor
   		int Elem_I = -1;
   		for (int Elem_I_1 = 0 ; Elem_I_1 < PossibilidadesInitiatorCluster1.size() ; Elem_I_1++)
   			if (Initiator.SearchQNameByString( PossibilidadesInitiatorCluster1.get(Elem_I_1)) != null) Elem_I = Elem_I_1;   			
   
   		int Elem_E = - 1;
   		for (int Elem_E_1 = 0 ; Elem_E_1 < PossibilidadesExecutorCluster.size() ; Elem_E_1++)
   			if (Executor.SearchQNameByString( PossibilidadesExecutorCluster.get(Elem_E_1)) != null )
   			{
   					Elem_E = Elem_E_1;
   					Elem_E_1 = PossibilidadesExecutorCluster.size();
   			}
    	
   		if (Elem_I != -1 && Elem_E != -1)
   			Initiator.addSequenceFlow( new BPMNSequenceFlow
					( Initiator.SearchQNameByString(PossibilidadesInitiatorCluster1.get(Elem_I)),
					  Executor.SearchQNameByString( PossibilidadesExecutorCluster.get(Elem_E) ) ) );
   		else System.out.println("Warning: There is no way to connect by flowElement: " + Initiator.getName() + " -> " + Executor.getName());
   		
   		
   		
   		
   		
   		
   		// From Executor to initiator
   		Elem_I = -1;
   		for (int Elem_I_2 = 0 ; Elem_I_2 < PossibilidadesInitiatorCluster2.size() ; Elem_I_2++)
   			if (Initiator.SearchQNameByString( PossibilidadesInitiatorCluster2.get(Elem_I_2)) != null) 
   			{
   				Elem_I = Elem_I_2;
   				Elem_I_2 = PossibilidadesInitiatorCluster2.size();
   			}
   
   		Elem_E = - 1;
   		for (int Elem_E_2 = 0 ; Elem_E_2 < PossibilidadesExecutorCluster.size() ; Elem_E_2++)
   			if (Executor.SearchQNameByString( PossibilidadesExecutorCluster.get(Elem_E_2)) != null )
   			{
   					Elem_E = Elem_E_2;
   			}
    	
   		if (Elem_I != -1 && Elem_E != -1)
   			Executor.addSequenceFlow( new BPMNSequenceFlow
   					( Executor.SearchQNameByString(PossibilidadesExecutorCluster.get(Elem_E)),
   					  Initiator.SearchQNameByString(PossibilidadesInitiatorCluster2.get(Elem_I) ) ) );
   		else System.out.println("Warning: There is no way to connect by flowElement: " + Executor.getName() + " -> " + Initiator.getName()); 

   		
		
		
		
		
   		
   		
		
   		
   		if (Initiator.SearchQNameByString("Reject") != null)
   		{
   			if ( Executor.SearchQNameByString("Evaluate Rejection") != null)
		   		Initiator.addSequenceFlow( new BPMNSequenceFlow
		   									( Initiator.SearchQNameByString("Reject"),
		   									  Executor.SearchQNameByString("Evaluate Rejection") ) );
   			else 
   				Initiator.addSequenceFlow( new BPMNSequenceFlow
							( Initiator.SearchQNameByString("Reject"),
							  Executor.SearchQNameByString("Make declare adapted?") ) );
   		}

   		
   		
   		if (Executor.SearchQNameByString("Decline") != null)
   		{
   			if (Initiator.SearchQNameByString("After Decline Decision") != null)
	   			Executor.addSequenceFlow( new BPMNSequenceFlow
						( Executor.SearchQNameByString("Decline"),
						  Initiator.SearchQNameByString("After Decline Decision") ) );
   			else
	   			Executor.addSequenceFlow( new BPMNSequenceFlow
						( Executor.SearchQNameByString("Decline"),
						  Initiator.SearchQNameByString("Make adapted request?") ) );
   		}
   		   		
   		
   		
 

		  Initiator = SpecifyIncoming_Outgoing_BetweenLanes(Initiator , Executor);
		  Executor  = SpecifyIncoming_Outgoing_BetweenLanes(Executor , Initiator);
		
	}
}
