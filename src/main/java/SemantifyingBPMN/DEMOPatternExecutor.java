package SemantifyingBPMN;

import java.util.ArrayList;

import javax.xml.namespace.QName;

public class DEMOPatternExecutor 
extends DEMOPattern{

 	  public Lane CreateElements_and_Sequence(Lane lane , TransactionKind tk, ArrayList<BPMNMessageFlow> MessageFlows , ArrayList<String> deps , PatternView view)
 	  {

 		  boolean RaP = CheckPreviousFixed(deps, new String("RaP"));  
 		  boolean RaE = CheckPreviousFixed(deps, new String("RaE"));  
 		  boolean RaD = CheckPreviousFixed(deps, new String("RaD"));
 		  
 		  
 		  QName DIVERGE_RaP = null , CONVERGE_RaP = null,
 				DIVERGE_RaE = null , CONVERGE_RaE = null,
 				DIVERGE_RaD = null , CONVERGE_RaD = null;
 		  
 		
 		 // level 1
		  QName gtw1_1 = lane.addElementWithShift(new Gateway( GatewayType.Eventbased, "" , "receive all triggers" , 1) , 0.1); 	
		  QName evt1_1 = lane.addElementWithShift(new Event  ( EventType.IntermediateMessageCatchEvent, "initial" , "initial" , 1), 0.2);
		  QName act1_1 = lane.addElement(new Activity( ActivityType.ManualTask , "Verify if can produce (e.g. stock)" , "Verify if can produce (e.g. stock)"  , 1));
		  QName act1_2 = lane.addElementWithShift(new Activity( ActivityType.SendTask , "Agree with arguments for rejection" , "Agree with arguments for rejection"  , 1) , 0.1);
		  QName end1_1 = lane.addElement(new Event  ( EventType.TerminateAll, "" , "" , 1));
		  lane.addSequenceFlow(new BPMNSequenceFlow(gtw1_1 , evt1_1));
		  lane.addSequenceFlow(new BPMNSequenceFlow(evt1_1 , act1_1));
		  lane.addSequenceFlow(new BPMNSequenceFlow(act1_2 , end1_1));
		  
  		 // level 2
		  QName evt2_1 = lane.addElement(new Event  ( EventType.IntermediateMessageCatchEvent, "revoke accept trigger" , "revoke accept trigger" , 2));
		  QName evt2_2 = lane.addElementWithShift(new Event  ( EventType.IntermediateMessageCatchEvent, "revoke request trigger" , "revoke request trigger" , 2) , 0.05);
		  QName evt2_3 = lane.addElementWithShift(new Event  ( EventType.IntermediateMessageCatchEvent, "Refuse received" , "Refuse received" , 2) , 0.075);
		  QName evt2_4 = lane.addElement(new Event  ( EventType.End, "end" , "end" , 2));
		  QName gtw2_1 = lane.addElementWithShift(new Gateway( GatewayType.Exclusive , "converging" , "converging" , 2), 0.085);
		  QName act2_1 = lane.addElement(new Activity( ActivityType.SendTask , "Decline Product" , "Decline Product"  , 2));
		  QName evt2_5 = lane.addElement(new Event  ( EventType.TerminateAll, "terminate all" , "terminate all" , 2));
		  QName gtw2_2 = lane.addElementWithShift(new Gateway( GatewayType.Exclusive , "Agree with arguments?" , "Agree with arguments?" , 2),0.03);
		  QName act2_2 = lane.addElement(new Activity( ActivityType.ManualTask , "Evaluate arguments for rejection" , "Evaluate arguments for rejection"  , 2));
		  QName evt2_6 = lane.addElementWithShift(new Event  ( EventType.IntermediateMessageCatchEvent, "reject product received" , "reject product received" , 2),0.05);
	  
		  lane.addSequenceFlow(new BPMNSequenceFlow(gtw1_1 , evt2_1));
		  lane.addSequenceFlow(new BPMNSequenceFlow(gtw1_1 , evt2_2));
		  lane.addSequenceFlow(new BPMNSequenceFlow(evt2_3 , evt2_4));
		  lane.addSequenceFlow(new BPMNSequenceFlow(gtw2_1 , act2_1));
		  lane.addSequenceFlow(new BPMNSequenceFlow(act2_1 , evt2_5));
		  lane.addSequenceFlow(new BPMNSequenceFlow(evt2_6 , act2_2));
		  lane.addSequenceFlow(new BPMNSequenceFlow(act2_2 , gtw2_2));
		  lane.addSequenceFlow(new BPMNSequenceFlow(gtw2_2 , act1_2));
		  
		  		  
  		 // level 3
		  QName act3_1 = lane.addElement(new Activity( ActivityType.ManualTask , "Decide about revoke accept" , "Decide about revoke accept"  , 3));
		  QName act3_2 = lane.addElement(new Activity( ActivityType.ManualTask , "Decide about revoke request" , "Decide about revoke request"  , 3));
		  QName evt3_1 = lane.addElement(new Event  ( EventType.IntermediateMessageCatchEvent, "revoke promise" , "revoke promise" , 3));
		  QName act3_3 = lane.addElement(new Activity( ActivityType.SendTask , "Revoke promise" , "Revoke promise"  , 3));
		  QName gtw3_1 = lane.addElement(new Gateway( GatewayType.Exclusive , "converging" , "converging" , 3));		  
		  QName gtw3_2 = lane.addElement(new Gateway( GatewayType.Eventbased, "wait for revoke result" , "wait for revoke result" , 3));
		  QName evt3_2 = lane.addElement(new Event  ( EventType.IntermediateMessageCatchEvent, "allow revoke promise received" , "allow revoke promise received" , 3));
		  QName evt3_3 = lane.addElement(new Event  ( EventType.IntermediateThrowCompensationEvent, "rollback declare" , "rollback declare" , 3));
		  QName evt3_4 = lane.addElement(new Event  ( EventType.IntermediateThrowCompensationEvent, "rollback execute" , "rollback execute" , 3));
		  QName evt3_5 = lane.addElement(new Event  ( EventType.IntermediateThrowCompensationEvent, "rollback promise" , "rollback promise" , 3));
		  QName gtw3_3 = lane.addElement(new Gateway( GatewayType.Exclusive , "ok to produce?" , "ok to produce?" , 3));
		  QName gtw3_4 = lane.addElement(new Gateway( GatewayType.Exclusive , "converging" , "converging" , 3));
		  BoundaryEvent bndevt3_1 = new BoundaryEvent("rollback promise trigger","rollback promise trigger");  
		  QName act3_4 = lane.addElement(new Activity( ActivityType.SendTaskWithBoundaryRollback , "Promise product" , "Promise product"  , 3 , bndevt3_1));
		  if (RaP)
 		  {
 			 DIVERGE_RaP = lane.addElement(new Gateway( GatewayType.Parallel , "DIVERGE_RaP" , "DIVERGE_RaP"   , 3));
 			 CONVERGE_RaP = lane.addElement(new Gateway( GatewayType.Parallel , "CONVERGE_RaP" , "CONVERGE_RaP"   , 3));
 		  }
		  BoundaryEvent bndevt3_2 = new BoundaryEvent("rollback execute trigger","rollback execute trigger");  
		  QName act3_5 = lane.addElement(new Activity( ActivityType.ManualTaskWithBoundaryRollback , "Execute product (Production)" , "Execute product (Production)"  , 3 , bndevt3_2));
		  if (RaE)
 		  {
  			 DIVERGE_RaE = lane.addElement(new Gateway( GatewayType.Parallel , "DIVERGE_RaE" , "DIVERGE_RaE"   , 3));
  			 CONVERGE_RaE = lane.addElement(new Gateway( GatewayType.Parallel , "CONVERGE_RaE" , "CONVERGE_RaE"   , 3)); 
 		  }
		  QName gtw3_7 = lane.addElement(new Gateway( GatewayType.Exclusive , "converging" , "converging" , 3));
		  BoundaryEvent bndevt3_3 = new BoundaryEvent("rollback declare trigger","rollback declare trigger");  
		  QName act3_6 = lane.addElement(new Activity( ActivityType.SendTaskWithBoundaryRollback , "Declare product" , "Declare product"  , 3 , bndevt3_3));
		  if (RaD)
 		  {
  			 DIVERGE_RaD = lane.addElement(new Gateway( GatewayType.Parallel , "DIVERGE_RaD" , "DIVERGE_RaD"   , 3));
 		  }
		  QName gtw3_6 = lane.addElement(new Gateway( GatewayType.Eventbased, "wait for declare result" , "wait for declare result" , 3));
		  QName evt3_6 = lane.addElement(new Event  ( EventType.IntermediateMessageCatchEvent, "accept product received" , "accept product received" , 3));
		  if (RaD) 
		  {
	  	      CONVERGE_RaD = lane.addElement(new Gateway( GatewayType.Parallel , "CONVERGE_RaD" , "CONVERGE_RaD"   , 3)); 			  
		  }		  
		  QName end1 = lane.addElement(new Event  ( EventType.TerminateAll, "end" , "end" , 3));

		  lane.addSequenceFlow(new BPMNSequenceFlow(gtw2_2 , gtw3_4));
		  
		  lane.addSequenceFlow(new BPMNSequenceFlow(act1_1 , gtw3_3));
		  lane.addSequenceFlow(new BPMNSequenceFlow(evt2_1 , act3_1 ));
		  lane.addSequenceFlow(new BPMNSequenceFlow(evt2_2 , act3_2));
		  lane.addSequenceFlow(new BPMNSequenceFlow(gtw1_1 , evt3_1 ));
		  lane.addSequenceFlow(new BPMNSequenceFlow(evt3_1 ,  act3_3));
		  lane.addSequenceFlow(new BPMNSequenceFlow(act3_3 ,  gtw3_1));
		  lane.addSequenceFlow(new BPMNSequenceFlow(gtw3_1 ,  gtw3_2));
		  lane.addSequenceFlow(new BPMNSequenceFlow(gtw3_2 ,  evt2_3));
		  lane.addSequenceFlow(new BPMNSequenceFlow(gtw3_2 , evt3_2));
		  lane.addSequenceFlow(new BPMNSequenceFlow(evt3_2 ,  evt3_3));
		  lane.addSequenceFlow(new BPMNSequenceFlow(evt3_3 , evt3_4 ));
		  lane.addSequenceFlow(new BPMNSequenceFlow(evt3_4 ,  evt3_5));
		  lane.addSequenceFlow(new BPMNSequenceFlow(evt3_5 , gtw2_1 ));
		  lane.addSequenceFlow(new BPMNSequenceFlow(gtw3_3 ,  gtw2_1));
		  lane.addSequenceFlow(new BPMNSequenceFlow(gtw3_3 , gtw3_4 ));
		  lane.addSequenceFlow(new BPMNSequenceFlow(gtw3_4 , act3_4 ));
		  
		  // ADD all the sequences in the model
		  if (RaP)
 		  {
			  lane.addSequenceFlow(new BPMNSequenceFlow(act3_4 , DIVERGE_RaP));
			  lane.addSequenceFlow(new BPMNSequenceFlow(DIVERGE_RaP , CONVERGE_RaP));
			  lane.addSequenceFlow(new BPMNSequenceFlow(CONVERGE_RaP , act3_5));
 		  }
		  else 	lane.addSequenceFlow(new BPMNSequenceFlow(act3_4 , act3_5 ));

 		  if (RaE)
 		  {
			  lane.addSequenceFlow(new BPMNSequenceFlow(act3_5 , DIVERGE_RaE));
			  lane.addSequenceFlow(new BPMNSequenceFlow(DIVERGE_RaE , CONVERGE_RaE));
			  lane.addSequenceFlow(new BPMNSequenceFlow(CONVERGE_RaE , gtw3_7));
  		  } else lane.addSequenceFlow(new BPMNSequenceFlow(act3_5 , gtw3_7 ));
 		  lane.addSequenceFlow(new BPMNSequenceFlow( gtw2_2 , gtw3_7 ));
 		  lane.addSequenceFlow(new BPMNSequenceFlow( gtw3_7 ,  act3_6));		  		 
 		  if (RaD)
 		  {
			  lane.addSequenceFlow(new BPMNSequenceFlow(act3_6 , DIVERGE_RaD));
			  lane.addSequenceFlow(new BPMNSequenceFlow(DIVERGE_RaD , gtw3_6));
			  lane.addSequenceFlow(new BPMNSequenceFlow(gtw3_6 , evt3_6));
			  lane.addSequenceFlow(new BPMNSequenceFlow(evt3_6 ,  CONVERGE_RaD));			  
			  lane.addSequenceFlow(new BPMNSequenceFlow(CONVERGE_RaD,end1));			  
 		  }
 		  else
 		  {
			  lane.addSequenceFlow(new BPMNSequenceFlow(act3_6 , gtw3_6));
			  lane.addSequenceFlow(new BPMNSequenceFlow(gtw3_6 , evt3_6));
			  lane.addSequenceFlow(new BPMNSequenceFlow(evt3_6 , end1));			  
 		  }
		  
 		  lane.addSequenceFlow(new BPMNSequenceFlow( gtw3_6 , evt2_6 ));
		    
		  
		  // level 4
 		  QName gtw4_1 = lane.addElement(new Gateway( GatewayType.Exclusive , "decision?" , "decision?" , 4));		  
 		  QName gtw4_2 = lane.addElement(new Gateway( GatewayType.Exclusive , "decision?" , "decision?" , 4));
 		  QName evt4_1 = lane.addElement(new Event  ( EventType.IntermediateMessageCatchEvent, "revoke declare" , "revoke declare" , 4));
 		  QName act4_1 = lane.addElementWithShift(new Activity( ActivityType.SendTask , "Revoke declare" , "Revoke declare"  , 4), 0.02);
		  QName evt4_2 = lane.addElementWithShift(new Event  ( EventType.IntermediateMessageCatchEvent, "allow revoke declare received" , "allow revoke declare received" , 4),0.02);
		  QName evt4_3 = lane.addElement(new Event  ( EventType.IntermediateThrowCompensationEvent, "rollback declare" , "rollback declare" , 4));
		  QName evt4_4 = lane.addElement(new Event  ( EventType.IntermediateThrowCompensationEvent, "rollback execute" , "rollback execute" , 4));
		  QName act4_2 = lane.addElementWithShift(new Activity( ActivityType.Compensation , "Rollback Promise product" , "Rollback Promise product"  , 4) , 0.2);
		  QName act4_3 = lane.addElement(new Activity( ActivityType.Compensation , "Rollback Execute product" , "Rollback Execute product"  , 4));
		  QName act4_4 = lane.addElement(new Activity( ActivityType.Compensation , "Rollback Declare product" , "Rollback Declare product"  , 4));
		  
		  lane.addSequenceFlow(new BPMNSequenceFlow(act3_1 , gtw4_1 ));
		  lane.addSequenceFlow(new BPMNSequenceFlow(act3_2 , gtw4_2 ));
		  lane.addSequenceFlow(new BPMNSequenceFlow(gtw1_1 ,  evt4_1));
		  lane.addSequenceFlow(new BPMNSequenceFlow(evt4_1 , act4_1 ));
		  lane.addSequenceFlow(new BPMNSequenceFlow(act4_1 , gtw3_1 ));
		  lane.addSequenceFlow(new BPMNSequenceFlow(gtw3_2 , evt4_2 ));
		  lane.addSequenceFlow(new BPMNSequenceFlow(evt4_2 ,  evt4_3));
		  lane.addSequenceFlow(new BPMNSequenceFlow(evt4_3 ,  evt4_4));
		  lane.addSequenceFlow(new BPMNSequenceFlow(evt4_4 , gtw3_4));
		  
 		  lane.addAssociation(new BPMNAssociation( bndevt3_1.getQname_BPMNElement() , act4_2));
 		  lane.addAssociation(new BPMNAssociation( bndevt3_2.getQname_BPMNElement() , act4_3));
 		  lane.addAssociation(new BPMNAssociation( bndevt3_3.getQname_BPMNElement() , act4_4));

		  
  		 // level 5
		  QName gtw5_1 = lane.addElement(new Gateway( GatewayType.Exclusive , "converging" , "converging" , 5));
		  QName act5_1 = lane.addElement(new Activity( ActivityType.SendTask , "Refuse" , "Allow revoke accept"  , 5));
		  QName evt5_1 = lane.addElement(new Event  ( EventType.End, "end" , "end" , 5));

		  lane.addSequenceFlow(new BPMNSequenceFlow( gtw4_1, gtw5_1 ));
		  lane.addSequenceFlow(new BPMNSequenceFlow( gtw4_2 , act5_1)); 
		  lane.addSequenceFlow(new BPMNSequenceFlow( gtw4_1, gtw5_1 ));
		  lane.addSequenceFlow(new BPMNSequenceFlow( gtw5_1 , act5_1));
		  lane.addSequenceFlow(new BPMNSequenceFlow( act5_1, evt5_1 ));
		  
  		 // level 6
		  QName act6_1 = lane.addElement(new Activity( ActivityType.SendTask , "Allow revoke accept" , "Allow revoke accept"  , 6));
		  lane.addSequenceFlow(new BPMNSequenceFlow(gtw4_1 , act6_1 ));
		  lane.addSequenceFlow(new BPMNSequenceFlow(act6_1 , gtw3_6 ));
 		  
		  //level 7
		  QName act7_1 = lane.addElement(new Activity( ActivityType.SendTask , "Allow revoke request" , "Allow revoke request"  , 7));
		  QName evt7_1 = lane.addElement(new Event  ( EventType.IntermediateThrowCompensationEvent, "rollback declare" , "rollback declare" , 7));
		  QName evt7_2 = lane.addElement(new Event  ( EventType.IntermediateThrowCompensationEvent, "rollback execute" , "rollback execute" , 7));
		  QName evt7_3 = lane.addElement(new Event  ( EventType.IntermediateThrowCompensationEvent, "rollback promise" , "rollback promise" , 7));
		  QName evt7_4 = lane.addElement(new Event  ( EventType.TerminateAll, "end" , "end" , 7));
		  lane.addSequenceFlow(new BPMNSequenceFlow( gtw4_2, act7_1 ));
		  lane.addSequenceFlow(new BPMNSequenceFlow( act7_1, evt7_1 ));
		  lane.addSequenceFlow(new BPMNSequenceFlow( evt7_1, evt7_2));
		  lane.addSequenceFlow(new BPMNSequenceFlow( evt7_2, evt7_3 ));
		  lane.addSequenceFlow(new BPMNSequenceFlow( evt7_3, evt7_4  ));
		  	
		  
		  
		  
		  // ADD all the message flows 
		  if ( CheckMessageFlow(MessageFlows , tk) == true ) // message flow exists
		  {
 		    updateMessageFlow(MessageFlows, tk, evt1_1 , "request (C-act)" );
 		    updateMessageFlow(MessageFlows, tk, evt3_6 , "accept (C-act)");
 			updateMessageFlow(MessageFlows, tk, evt2_1 ,"revoke accept (C-act)" );
 			updateMessageFlow(MessageFlows, tk, evt2_2 ,"revoke request (C-act)" );
 			updateMessageFlow(MessageFlows, tk, evt2_3 ,"refuse received (C-act)" );
 			updateMessageFlow(MessageFlows, tk, evt4_2 ,"allow revoke declare (C-act)" );
 			updateMessageFlow(MessageFlows, tk, evt3_2 ,"allow revoke promise (C-act)" );
 			updateMessageFlow(MessageFlows, tk, evt2_6 ,"reject (C-act)" );
 			
 			updateMessageFlow(MessageFlows, tk, act3_4,"promise (C-act)" );
 			updateMessageFlow(MessageFlows, tk, act3_6,"declare (C-act)" );
 			updateMessageFlow(MessageFlows, tk, act7_1,"allow revoke request (C-act)" );
 			updateMessageFlow(MessageFlows, tk, act6_1,"allow revoke accept (C-act)" );
 			updateMessageFlow(MessageFlows, tk, act5_1 ,"refuse (C-act)" );
 			updateMessageFlow(MessageFlows, tk, act4_1 ,"revoke declare (C-act)" );
 			updateMessageFlow(MessageFlows, tk, act3_3 ,"revoke promise (C-act)" );
 			updateMessageFlow(MessageFlows, tk, act2_1 ,"decline (C-act)" );
 			updateMessageFlow(MessageFlows, tk, act1_2 ,"stop (C-act)" );
		  }
		  else
		  {
 			MessageFlows.add(new BPMNMessageFlow(tk, evt1_1 , "request (C-act)", false) );
 			MessageFlows.add(new BPMNMessageFlow(tk, evt3_6 , "accept (C-act)",  false) );
 			MessageFlows.add(new BPMNMessageFlow(tk, evt2_1 ,"revoke accept (C-act)", false) );
 			MessageFlows.add(new BPMNMessageFlow(tk, evt2_2 ,"revoke request (C-act)", false) );
 			MessageFlows.add(new BPMNMessageFlow(tk, evt2_3 ,"refuse received (C-act)", false) );
 			MessageFlows.add(new BPMNMessageFlow(tk, evt4_2 ,"allow revoke declare (C-act)", false) );
 			MessageFlows.add(new BPMNMessageFlow(tk, evt3_2,"allow revoke promise (C-act)", false) );
 			MessageFlows.add(new BPMNMessageFlow(tk, evt2_6 ,"reject (C-act)", false) );
 			
 			MessageFlows.add(new BPMNMessageFlow(tk, act3_4,"promise (C-act)", true) );
 			MessageFlows.add(new BPMNMessageFlow(tk, act3_6,"declare (C-act)", true) );
 			MessageFlows.add(new BPMNMessageFlow(tk, act7_1 ,"allow revoke request (C-act)", true) );
 			MessageFlows.add(new BPMNMessageFlow(tk, act6_1 ,"allow revoke accept (C-act)", true) );
 			MessageFlows.add(new BPMNMessageFlow(tk, act5_1,"refuse (C-act)", true) );
 			MessageFlows.add(new BPMNMessageFlow(tk, act4_1 ,"revoke declare (C-act)", true) );
 			MessageFlows.add(new BPMNMessageFlow(tk, act3_3 ,"revoke promise (C-act)", true) );
 			MessageFlows.add(new BPMNMessageFlow(tk, act2_1 ,"decline (C-act)", true) );
 			MessageFlows.add(new BPMNMessageFlow(tk, act1_2 ,"stop (C-act)", true) );
			  
			  
			  
		  }
 			
 			

		  
		  lane = SpecifyIncoming_Outgoing(lane);

		  
 		  return(lane);
 	  };
 	  

}
