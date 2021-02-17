package SemantifyingBPMN;

import java.util.ArrayList;

import javax.xml.namespace.QName;

public class DEMOPatternInitiator 
extends DEMOPattern{

   	  public Lane CreateElements_and_Sequence(Lane lane , TransactionKind tk, ArrayList<BPMNMessageFlow> MessageFlows , ArrayList<String> deps , PatternView view )
   	  {
   		  // level 1
   		  QName evt1_1 = lane.addElement(new Event  ( EventType.IntermediateMessageCatchEvent, "revoke accept triggered" , "revoke accept triggered" , 1));
   		  QName act1_1 = lane.addElementWithShift(new Activity( ActivityType.SendTask , "Revoke accept" , "Revoke accept"  , 1) , 0.08);
   		  QName evt1_2 = lane.addElementWithShift(new Event  ( EventType.IntermediateMessageCatchEvent, "revoke accept received" , "revoke accept received" , 1) , 0.05);
   		  QName evt1_3 = lane.addElementWithShift(new Event  ( EventType.IntermediateThrowCompensationEvent, "rollback accept" , "rollback accept" , 1) , 0.05);   		  
		  lane.addSequenceFlow(new BPMNSequenceFlow(evt1_1 , act1_1));
		  lane.addSequenceFlow(new BPMNSequenceFlow(evt1_2 , evt1_3));
   		  
   		  // level 2
   		  QName evt2_1 = lane.addElementWithShift(new Event  ( EventType.IntermediateMessageCatchEvent, "revoke request triggered" , "revoke request triggered" , 2) , 0.05);
   		  QName act2_1 = lane.addElement(new Activity( ActivityType.SendTask , "Revoke request" , "Revoke request"  , 2));
   		  QName gtw2_1 = lane.addElement(new Gateway( GatewayType.Exclusive , "converging" , "converging" , 2));
   		  QName gtw2_2 = lane.addElement(new Gateway( GatewayType.Eventbased, "wait for allow revokes" , "wait for allow revokes" , 2));
   		  QName evt2_2 = lane.addElement(new Event  ( EventType.IntermediateMessageCatchEvent, "revoke request received" , "revoke request received" , 2) );
   		  QName evt2_3 = lane.addElement(new Event  ( EventType.IntermediateThrowCompensationEvent, "rollback accept" , "rollback accept" , 2 ));
   		  QName end2_1 = lane.addElement(new Event  ( EventType.TerminateAll, "end" , "end" , 2));
		  lane.addSequenceFlow(new BPMNSequenceFlow(act1_1 , gtw2_1));
		  lane.addSequenceFlow(new BPMNSequenceFlow(evt2_1 , act2_1));
		  lane.addSequenceFlow(new BPMNSequenceFlow(act2_1 , gtw2_1));
		  lane.addSequenceFlow(new BPMNSequenceFlow(gtw2_1 , gtw2_2));
		  lane.addSequenceFlow(new BPMNSequenceFlow(gtw2_2 , evt1_2));
		  lane.addSequenceFlow(new BPMNSequenceFlow(gtw2_2 , evt2_2));
		  lane.addSequenceFlow(new BPMNSequenceFlow(evt2_2 , evt2_3));
		  lane.addSequenceFlow(new BPMNSequenceFlow(evt2_3 , end2_1));
   		  
   		  //level 3
		  QName evt3_1 = lane.addElementWithShift(new Event  ( EventType.IntermediateMessageCatchEvent, "refuse received" , "refuse received" , 3) , 0.27 );
		  QName end3_1 = lane.addElement(new Event  ( EventType.End, "end" , "end" , 3));
		  lane.addSequenceFlow(new BPMNSequenceFlow(gtw2_2 , evt3_1));
		  lane.addSequenceFlow(new BPMNSequenceFlow(evt3_1 , end3_1));
		  
		  //level 4
		  QName evt4_1 = lane.addElementWithShift(new Event  ( EventType.IntermediateMessageCatchEvent, "revoke declare received" , "revoke declare received" , 4) , 0.1);
   		  QName act4_1 = lane.addElementWithShift(new Activity( ActivityType.ManualTask , "decide about revoke declare" , "decide about revoke declare"  , 4) , 0.05);
   		  QName gtw4_1 = lane.addElement(new Gateway( GatewayType.Exclusive , "converging" , "converging" , 4));
   		  QName evt4_2 = lane.addElementWithShift(new Event  ( EventType.IntermediateThrowCompensationEvent, "rollback accept" , "rollback accept" , 4 ) , 0.05);
   		  QName act4_2 = lane.addElement(new Activity( ActivityType.SendTask , "Allow revoke declare" , "Allow revoke declare"  , 4));
   		  lane.addSequenceFlow(new BPMNSequenceFlow(evt4_1 , act4_1));
   		  lane.addSequenceFlow(new BPMNSequenceFlow(act4_1 , gtw4_1));
   		  lane.addSequenceFlow(new BPMNSequenceFlow(gtw4_1 , evt4_2));
   		  lane.addSequenceFlow(new BPMNSequenceFlow(evt4_2 , act4_2));
   		  
   		  //level 5
   		  QName gtw5_1 = lane.addElement(new Gateway( GatewayType.Eventbased, "wait for revokes" , "wait for revokes" , 5));
		  QName end5_1 = lane.addElementWithShift(new Event  ( EventType.End, "end" , "end" , 5) , 0.05);
   		  QName act5_1 = lane.addElement(new Activity( ActivityType.SendTask , "Refuse" , "Refuse"  , 5));
   		  QName gtw5_2 = lane.addElement(new Gateway( GatewayType.Exclusive , "converging" , "converging" , 5));
   		  QName gtw5_3 = lane.addElementWithShift(new Gateway( GatewayType.Parallel , "diverging" , "diverging" , 5) , 0.2);   		  
   		  lane.addSequenceFlow(new BPMNSequenceFlow(gtw5_1 , evt1_1));
   		  lane.addSequenceFlow(new BPMNSequenceFlow(gtw5_1 , evt2_1));
   		  lane.addSequenceFlow(new BPMNSequenceFlow(gtw5_1 , evt4_1));
   		  lane.addSequenceFlow(new BPMNSequenceFlow(gtw4_1 , gtw5_2));
   		  lane.addSequenceFlow(new BPMNSequenceFlow(gtw5_2 , act5_1));
     	  lane.addSequenceFlow(new BPMNSequenceFlow(act5_1 , end5_1));
     	  lane.addSequenceFlow(new BPMNSequenceFlow(act4_2 , gtw5_3));
     	  lane.addSequenceFlow(new BPMNSequenceFlow(gtw5_3 , gtw5_1));
   		  
   		  //level 6
   		  QName evt6_1 = lane.addElementWithShift(new Event  ( EventType.IntermediateMessageCatchEvent, "revoke promise received" , "revoke promise received" , 6) , 0.1);
   		  QName act6_1 = lane.addElement(new Activity( ActivityType.ManualTask , "Decide about revoke promise" , "Decide about revoke promise"  , 6));
   		  QName gtw6_1 = lane.addElement(new Gateway( GatewayType.Exclusive , "diverging" , "diverging" , 6));
   		  QName evt6_2 = lane.addElement(new Event  ( EventType.IntermediateThrowCompensationEvent, "rollback accept" , "rollback accept" , 6 ));
   		  QName act6_2 = lane.addElement(new Activity( ActivityType.SendTask , "Allow revoke promise" , "Allow revoke promise"  , 6));
   		  QName gtw6_2 = lane.addElementWithShift(new Gateway( GatewayType.Exclusive , "converging" , "converging" , 6) , 0.03);
   		  QName act6_3 = lane.addElementWithShift(new Activity( ActivityType.SendTask , "Reject Product" , "Reject Product"  , 6), 0.1);
   		  QName gtw6_3 = lane.addElement(new Gateway( GatewayType.Exclusive , "converging" , "converging" , 6));
   		  
   		  lane.addSequenceFlow(new BPMNSequenceFlow(gtw5_1 , evt6_1));
   		  lane.addSequenceFlow(new BPMNSequenceFlow(evt6_1 , act6_1));
   		  lane.addSequenceFlow(new BPMNSequenceFlow(act6_1 , gtw6_1));
   		  lane.addSequenceFlow(new BPMNSequenceFlow(gtw6_1 , evt6_2));
   		  lane.addSequenceFlow(new BPMNSequenceFlow(gtw6_1 , gtw5_2));
   		  lane.addSequenceFlow(new BPMNSequenceFlow(evt6_2 , act6_2));
   		  lane.addSequenceFlow(new BPMNSequenceFlow(act6_2 , gtw6_2));
   		  lane.addSequenceFlow(new BPMNSequenceFlow(evt1_3 , gtw6_3));
   		  lane.addSequenceFlow(new BPMNSequenceFlow(gtw6_3 , act6_3));
     	  lane.addSequenceFlow(new BPMNSequenceFlow(gtw5_3 , gtw6_2));

   		  //level 7
   		  QName strt = lane.addElement(new Event  ( EventType.Start , "INITIAL" , "INITIAL" , 7 ));
   		  QName gtw7_1 = lane.addElement(new Gateway( GatewayType.Parallel , "Diverging" , "Diverging" , 7  ));
   		  QName act7_1 = lane.addElement(new Activity( ActivityType.ManualTask , "Decide the type of product to order" , "Decide the type of product to order"  , 7));
   		  QName gtw7_2 = lane.addElement(new Gateway( GatewayType.Exclusive , "converging" , "converging" , 7) );
   		  BoundaryEvent bndevt7_1 = new BoundaryEvent("rollback request trigger","rollback request trigger");  
   		  QName act7_2 = lane.addElement(new Activity( ActivityType.SendTaskWithBoundaryRollback , "Request Product" , "Request Product"  , 7 , bndevt7_1));
   		  QName gtw7_3 = lane.addElement(new Gateway( GatewayType.Eventbased, "wait for request response" , "wait for request response" , 7));
		  QName evt7_1 = lane.addElement(new Event  ( EventType.IntermediateMessageCatchEvent, "Promise received" , "Promise received" , 7) );
		  QName gtw7_4 = lane.addElement(new Gateway( GatewayType.Exclusive , "converging" , "converging" , 7));
   		  lane.addSequenceFlow(new BPMNSequenceFlow(strt , gtw7_1));
   		  lane.addSequenceFlow(new BPMNSequenceFlow(gtw7_1 , gtw5_1));
   		  lane.addSequenceFlow(new BPMNSequenceFlow(gtw7_1 , act7_1));
   		  lane.addSequenceFlow(new BPMNSequenceFlow(act7_1 , gtw7_2));
   		  lane.addSequenceFlow(new BPMNSequenceFlow(gtw7_2 , act7_2));
   		  lane.addSequenceFlow(new BPMNSequenceFlow(act7_2 , gtw7_3));
   		  lane.addSequenceFlow(new BPMNSequenceFlow(gtw7_3 , evt7_1));
   		  lane.addSequenceFlow(new BPMNSequenceFlow(evt7_1 , gtw7_4));
   		  lane.addSequenceFlow(new BPMNSequenceFlow(act6_3 , gtw7_4));
   		  lane.addSequenceFlow(new BPMNSequenceFlow(gtw6_2 , gtw7_3));

   		  
   		  // level 8
   		  QName end8_1 = lane.addElementWithShift(new Event  ( EventType.TerminateAll, "end" , "end" , 8) , 0.1);
   		  QName gtw8_1 = lane.addElement(new Gateway( GatewayType.Exclusive , "Make new request?" , "Make new request?" , 8) );
 		  QName roll6_1 = lane.addElement(new Activity( ActivityType.Compensation , "Rollback request Product" , "Rollback request Product" , 8));
   		  QName gtw8_2 = lane.addElementWithShift(new Gateway( GatewayType.Eventbased, "wait for reject response" , "wait for reject response" , 8) , 0.2);
		  QName evt8_1 = lane.addElement(new Event  ( EventType.IntermediateMessageCatchEvent, "Declare received" , "Declare received" , 8) );
   		  QName act8_1 = lane.addElement(new Activity( ActivityType.ManualTask , "Check Product" , "Check Product"  , 8));
   		  QName gtw8_3 = lane.addElement(new Gateway( GatewayType.Exclusive , "Is product ok?" , "Is product ok?" , 8) );
   		  BoundaryEvent bndevt8_1 = new BoundaryEvent("rollback accept trigger","rollback accept trigger");  
   		  QName act8_2 = lane.addElement(new Activity( ActivityType.SendTaskWithBoundaryRollback , "Accept Product" , "Accept Product"  , 8 , bndevt8_1));
   		  QName end8_2 = lane.addElement(new Event  ( EventType.TerminateAll, "end" , "end" , 8));
   		  lane.addSequenceFlow(new BPMNSequenceFlow(gtw8_1 , end8_1));
   		  lane.addSequenceFlow(new BPMNSequenceFlow(gtw8_1 , gtw7_2));
   		  lane.addSequenceFlow(new BPMNSequenceFlow(gtw8_2 , evt8_1));
   		  lane.addSequenceFlow(new BPMNSequenceFlow(gtw7_4 , gtw8_2));
   		  lane.addSequenceFlow(new BPMNSequenceFlow(evt8_1 , act8_1));
   		  lane.addSequenceFlow(new BPMNSequenceFlow(act8_1 , gtw8_3));
   		  lane.addSequenceFlow(new BPMNSequenceFlow(gtw8_3 , act8_2));
   		  lane.addSequenceFlow(new BPMNSequenceFlow(act8_2 , end8_2));
  		  lane.addSequenceFlow(new BPMNSequenceFlow(gtw8_3 , gtw6_3));
		  lane.addAssociation(new BPMNAssociation( bndevt7_1.getQname_BPMNElement() , roll6_1));

 		  
 		  // level 9
   		  QName act9_1 = lane.addElementWithShift(new Activity( ActivityType.ManualTask , "Decide what to do next" , "Decide what to do next"  , 9) , 0.2);
		  QName evt9_1 = lane.addElementWithShift(new Event  ( EventType.IntermediateMessageCatchEvent, "Decline received" , "Decline received" , 9) ,0.1);
		  QName evt9_2 = lane.addElementWithShift(new Event  ( EventType.IntermediateMessageCatchEvent, "Stop received" , "Stop received" , 9) , 0.1);
   		  QName end9_1 = lane.addElement(new Event  ( EventType.TerminateAll, "end" , "end" , 9));
 		  QName roll7_1 = lane.addElementWithShift(new Activity( ActivityType.Compensation , "Rollback Accept Product" , "Rollback Accept Product" , 9) , 0.1);
   		  lane.addSequenceFlow(new BPMNSequenceFlow(gtw7_3 , evt9_1));
   		  lane.addSequenceFlow(new BPMNSequenceFlow(evt9_1 , act9_1));
   		  lane.addSequenceFlow(new BPMNSequenceFlow(act9_1 , gtw8_1));
   		  lane.addSequenceFlow(new BPMNSequenceFlow(gtw8_2 , evt9_2));
   		  lane.addSequenceFlow(new BPMNSequenceFlow(evt9_2 , end9_1));
 		  lane.addAssociation(new BPMNAssociation( bndevt8_1.getQname_BPMNElement() , roll7_1));

		  
		  // ADD all the message flows 
 		    if ( CheckMessageFlow(MessageFlows , tk) == false ) //no message flow exists
 		    {

	 			MessageFlows.add(new BPMNMessageFlow(tk, act7_2 , "request (C-act)", true) );
	 			MessageFlows.add(new BPMNMessageFlow(tk, act8_2 , "accept (C-act)",  true) );
	 			MessageFlows.add(new BPMNMessageFlow(tk, act1_1 ,"revoke accept (C-act)", true) );
	 			MessageFlows.add(new BPMNMessageFlow(tk, act2_1 ,"revoke request (C-act)", true) );
	 			MessageFlows.add(new BPMNMessageFlow(tk, act5_1 ,"refuse received (C-act)", true) );
	 			MessageFlows.add(new BPMNMessageFlow(tk, act4_2 ,"allow revoke declare (C-act)", true) );
	 			MessageFlows.add(new BPMNMessageFlow(tk, act6_2,"allow revoke promise (C-act)", true) );
	 			MessageFlows.add(new BPMNMessageFlow(tk, act6_3 ,"reject (C-act)", true) );
	 			
	 			MessageFlows.add(new BPMNMessageFlow(tk, evt7_1,"promise (C-act)", false) );
	 			MessageFlows.add(new BPMNMessageFlow(tk, evt8_1,"declare (C-act)", false) );
	 			MessageFlows.add(new BPMNMessageFlow(tk, evt2_2 ,"allow revoke request (C-act)", false) );
	 			MessageFlows.add(new BPMNMessageFlow(tk, evt1_2 ,"allow revoke accept (C-act)", false) );
	 			MessageFlows.add(new BPMNMessageFlow(tk, evt3_1,"refuse (C-act)", false) );
	 			MessageFlows.add(new BPMNMessageFlow(tk, evt4_1 ,"revoke declare (C-act)", false) );
	 			MessageFlows.add(new BPMNMessageFlow(tk, evt6_1 ,"revoke promise (C-act)", false) );
	 			MessageFlows.add(new BPMNMessageFlow(tk, evt9_1 ,"decline (C-act)", false) );
	 			MessageFlows.add(new BPMNMessageFlow(tk, evt9_2 ,"stop (C-act)", false) );
 		    }
 		    else
 		    {
 	 		    updateMessageFlow(MessageFlows, tk, act7_2 , "request (C-act)" );
 	 		    updateMessageFlow(MessageFlows, tk, act8_2 , "accept (C-act)");
 	 			updateMessageFlow(MessageFlows, tk, act1_1 ,"revoke accept (C-act)" );
 	 			updateMessageFlow(MessageFlows, tk, act2_1 ,"revoke request (C-act)" );
 	 			updateMessageFlow(MessageFlows, tk, act5_1 ,"refuse received (C-act)" );
 	 			updateMessageFlow(MessageFlows, tk, act4_2 ,"allow revoke declare (C-act)" );
 	 			updateMessageFlow(MessageFlows, tk, act6_2 ,"allow revoke promise (C-act)" );
 	 			updateMessageFlow(MessageFlows, tk, act6_3 ,"reject (C-act)" );
 	 			
 	 			updateMessageFlow(MessageFlows, tk, evt7_1,"promise (C-act)" );
 	 			updateMessageFlow(MessageFlows, tk, evt8_1,"declare (C-act)" );
 	 			updateMessageFlow(MessageFlows, tk, evt2_2,"allow revoke request (C-act)" );
 	 			updateMessageFlow(MessageFlows, tk, evt1_2,"allow revoke accept (C-act)" );
 	 			updateMessageFlow(MessageFlows, tk, evt3_1 ,"refuse (C-act)" );
 	 			updateMessageFlow(MessageFlows, tk, evt4_1 ,"revoke declare (C-act)" );
 	 			updateMessageFlow(MessageFlows, tk, evt6_1 ,"revoke promise (C-act)" );
 	 			updateMessageFlow(MessageFlows, tk, evt9_1 ,"decline (C-act)" );
 	 			updateMessageFlow(MessageFlows, tk, evt9_2 ,"stop (C-act)" );
 		    }
		  
		  lane = SpecifyIncoming_Outgoing(lane);
		  
 		  return(lane);
 		  
   	  };
   	  
	
}





















