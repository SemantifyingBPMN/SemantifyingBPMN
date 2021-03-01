package SemantifyingBPMN;

import java.util.ArrayList;

import javax.xml.namespace.QName;

public abstract class DEMOPattern {

	  
	  public abstract Lane CreateElements_and_Sequence(Lane lane , TransactionKind tk, ArrayList<BPMNMessageFlow> MessageFlows , ArrayList<String> deps , PatternView view);
	  
			
		
	  
	  protected Lane SpecifyIncoming_Outgoing(Lane lane_received)
	  {
		  Lane lane = lane_received;
		  
		   for(BPMNSequenceFlow seq:lane.getBPMNSequenceFlows())
		   {
			   QName id;
			   // check source to add as outgoing of BPMNElement
			   id = seq.getSourceQName();
			   for (BPMNElement elem:lane.getBPMNElements())
				   	if (elem.getQname_BPMNElement() == id) elem.add_Flow_Outgoing(seq.getQName_Flowid());				   		

			   // check target to add as incoming of BPMNElement
			   id = seq.getTargetQName();
			   for (BPMNElement elem:lane.getBPMNElements())
				   	if (elem.getQname_BPMNElement() == id) elem.add_Flow_Incoming(seq.getQName_Flowid());				   		
		   }
		 
		   
		  return(lane);
	  }
	  
	  
	  protected Lane SpecifyIncoming_Outgoing_BetweenLanes(Lane lane_received , Lane other_lane)
	  {
		  Lane lane = lane_received;
		  
		   for(BPMNSequenceFlow seq:lane.getBPMNSequenceFlows())
		   {
			   QName id;
			   // check source to add as outgoing of BPMNElement in my lane
			   id = seq.getSourceQName();
			   for (BPMNElement elem:lane.getBPMNElements())
				   	if (elem.getQname_BPMNElement() == id) elem.add_Flow_Outgoing(seq.getQName_Flowid());
			   
					   // check target to add as incoming of BPMNElement in my lane
			   id = seq.getTargetQName();
			   for (BPMNElement elem:lane.getBPMNElements())
				   	if (elem.getQname_BPMNElement() == id) elem.add_Flow_Incoming(seq.getQName_Flowid());				   		
		   }
		 

		   for(BPMNSequenceFlow seq:lane.getBPMNSequenceFlows())
		   {
			   QName id;
			   // check source to add as outgoing of BPMNElement in other lane
			   id = seq.getSourceQName();
			   for (BPMNElement elem:other_lane.getBPMNElements())
				   	if (elem.getQname_BPMNElement() == id) elem.add_Flow_Outgoing(seq.getQName_Flowid());
			   
					   // check target to add as incoming of BPMNElement in other lane
			   id = seq.getTargetQName();
			   for (BPMNElement elem:other_lane.getBPMNElements())
				   	if (elem.getQname_BPMNElement() == id) elem.add_Flow_Incoming(seq.getQName_Flowid());				   		
		   }
		   
		  return(lane);
	  }

	 public void updateMessageFlow(ArrayList<BPMNMessageFlow> MessageFlows, TransactionKind tk , QName destination  , String ActKey_v)
	 {
			// search transaction
			for (BPMNMessageFlow mf:MessageFlows)
			{
				if ( mf.getTransaction_origin() == tk && mf.getActKey().compareTo(ActKey_v) == 0 )
				{
					// communication act paired
					mf.setTargetQName(destination);
					mf.setTransaction_destination(tk);
				}
			}			
	 }
	 

	protected boolean CheckMessageFlow(ArrayList<BPMNMessageFlow> MessageFlows , TransactionKind tk) 
	{
		boolean return_value = false;
		
		// search transaction
		for (BPMNMessageFlow mf:MessageFlows)
		{
			if ( mf.getTransaction_origin() == tk) return (true);
		}
		return return_value;
	};

	 
	 
	 public boolean CheckPrevious(ArrayList<String> previous)
	 {
		 boolean result = false;
		 
		 for (String str:previous)
			 if (str.compareTo("RaP") == 0 || str.compareTo("RaE")==0 || str.compareTo("RaD") == 0) result = true;
			 
		 return (result);
	 }
	 
	 public boolean CheckPreviousFixed(ArrayList<String> previous , String toCheck)
	 {
		 boolean result = false;
		 
		 for (String str:previous)
				 if (str.compareTo(toCheck) == 0 ) result = true;
			 
		 return (result);
	 }	 
	  

	 
	 
	 protected void AddFlow2SemantifiedElements(ArrayList<SemantifiedElement> semantified_elements, QName source, QName destination) 
	 {
		 int sourceIdx = FindQNameInSemantifiedElements(source , semantified_elements);
		 int destinationIdx = FindQNameInSemantifiedElements(destination , semantified_elements);	
			
		 semantified_elements.get(sourceIdx).AddReferenced_semantified_element(destinationIdx);
	 }




	protected int FindQNameInSemantifiedElements(QName QName_elem, ArrayList<SemantifiedElement> elem_list) 
	{		
		for (int idx = 0 ; idx < elem_list.size() ; idx++)
		{
			SemantifiedElement elem = elem_list.get(idx);
			if (elem.getSemantified_element() == QName_elem) return(idx); 
		}
		
		return -1;
	}

}





