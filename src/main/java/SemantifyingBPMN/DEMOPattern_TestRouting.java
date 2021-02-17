package SemantifyingBPMN;

import java.util.ArrayList;

import javax.xml.namespace.QName;

public class DEMOPattern_TestRouting 
extends DEMOPattern{

		
		public Lane CreateElements_and_Sequence(Lane lane , TransactionKind tk, ArrayList<BPMNMessageFlow> MessageFlows , ArrayList<String> deps , PatternView view ) {
			
			
			QName t1 = lane.addElement(new Activity( ActivityType.ManualTask , "T1" , "T1"  , 1));
			QName t2 = lane.addElement(new Activity( ActivityType.ManualTask , "T2" , "T2"  , 1));
			QName t3 = lane.addElement(new Activity( ActivityType.ManualTask , "T3" , "T3"  , 1));
			QName t4 = lane.addElement(new Activity( ActivityType.ManualTask , "T4" , "T4"  , 2));
			QName t5 = lane.addElement(new Activity( ActivityType.ManualTask , "T5" , "T5"  , 2));
			QName t6 = lane.addElement(new Activity( ActivityType.ManualTask , "T6" , "T6"  , 2));
			QName t7 = lane.addElement(new Activity( ActivityType.ManualTask , "T7" , "T7"  , 3));
			QName t8 = lane.addElement(new Activity( ActivityType.ManualTask , "T8" , "T8"  , 3));
			QName t9 = lane.addElement(new Activity( ActivityType.ManualTask , "T9" , "T9"  , 3));

			lane.addSequenceFlow(new BPMNSequenceFlow(t5 , t1));
			lane.addSequenceFlow(new BPMNSequenceFlow(t5 , t2));
			lane.addSequenceFlow(new BPMNSequenceFlow(t5 , t3));
			lane.addSequenceFlow(new BPMNSequenceFlow(t5 , t4));
			lane.addSequenceFlow(new BPMNSequenceFlow(t5 , t6));
			lane.addSequenceFlow(new BPMNSequenceFlow(t5 , t7));
			lane.addSequenceFlow(new BPMNSequenceFlow(t5 , t8));
			lane.addSequenceFlow(new BPMNSequenceFlow(t5 , t9));

			
			
			
			lane = SpecifyIncoming_Outgoing(lane);
			  
			return(lane);
	 		  
	   	};
}
