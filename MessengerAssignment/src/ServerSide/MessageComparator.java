package ServerSide;

import java.util.Comparator;

import common.Message;

public class MessageComparator implements Comparator<Message>{

	public MessageComparator() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public int compare(Message m1, Message m2) {
		return m1.getTimestamp().compareTo(m2.getTimestamp());
	}

}
